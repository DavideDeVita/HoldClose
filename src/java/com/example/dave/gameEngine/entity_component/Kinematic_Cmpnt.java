package com.example.dave.gameEngine.entity_component;

import java.util.Map;
import android.util.ArrayMap;

import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.SubBox;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.Kinematic_Properties;
import com.example.dave.gameEngine.dataDriven.component.Kinematic_Properties.*;


import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public abstract class Kinematic_Cmpnt extends Component{
	public float velocity;
	public float[] direction;
	protected final Physical_Cmpnt thisPhys;

	protected Kinematic_Cmpnt(Entity owner, FloatValue[] dir, FloatValue vel) {
		super(owner);
		thisPhys = owner.getPhysical();
		if(dir==null)
			direction=new float[2];
		else
			direction = new float[]{dir[X].get(), dir[Y].get()};
		this.velocity = vel.get();
	}

	@Override
	public final ComponentType type() {
		return ComponentType.Kinematic;
	}

	public abstract void update();

	/**Physical Link*/
	public void updateVelocityVector(){
		thisPhys.setVelocityVector(direction, velocity);
	}

	public enum KinematicPattern{BoxBounce, Polygon, Circular}
}

class Kinematic_BoxBouncePattern_Cmpnt extends Kinematic_Cmpnt {
	public final Box area;

	protected Kinematic_BoxBouncePattern_Cmpnt(Entity owner, FloatValue[] dir, FloatValue vel, Kinematic_Properties kineP) {
		super( owner, dir, vel );
		BoxBounce_Properties boxBounce = (BoxBounce_Properties)kineP.subClass;
		this.area=new SubBox(boxBounce.area, owner.gs.physicalBox);
		updateVelocityVector();
	}

	@Override
	public void update(){
		float x = thisPhys.getX(), y= thisPhys.getY();
		if(_Log.LOG_ACTIVE){
			_Log.i("Kinematic", "BoxBounce. Direction pre update "+direction[X]+" "+direction[Y]+"\n" +
				"Position is "+x+" "+y+"\n" +
				"area is [x]("+area.xmin+" - "+area.xmax+") [y]("+area.ymin+" - "+area.ymax+") ");}
		if (!area.contains(x, y)){
			if(x<=area.xmin || x>=area.xmax)
				direction[X] *= -1;
			if(y<=area.ymin || y>=area.ymax)
				direction[Y] *= -1;
			//You could correct this bug by setting the direction positive if before the box and viceversa
			if(_Log.LOG_ACTIVE){
				_Log.i("Kinematic", "BoxBounce. Direction post update "+direction[X]+" "+direction[Y]);}
			updateVelocityVector();
		}
	}
}

class Kinematic_PolygonPattern_Cmpnt extends Kinematic_Cmpnt {
	public float points[][];
	private int goingForward; //iteration
	private final int step; //{1, -1}
	private final float originalVelocity;
	private final float expectedTravelTimes[];
	//to remove
	private final int choreographyID;
	private final ChoreographyDetails chDetails;
	private final static Map<String, ChoreographyDetails> choreographies = new ArrayMap<>();

	protected Kinematic_PolygonPattern_Cmpnt(Entity owner, FloatValue vel, Kinematic_Properties kineP) {
		super( owner, null, vel );
		Polygon_Properties polygon = (Polygon_Properties)kineP.subClass;
		this.points=polygon.points; //n X 2
		step = (polygon.clockwise) ? -1 : 1;
		goingForward = polygon.startingPoint;
		originalVelocity = this.velocity;
		if(polygon.choreography!=null){
			if(choreographies.containsKey(polygon.choreography)){   //Choreography already exists
				chDetails = choreographies.get(polygon.choreography);
				expectedTravelTimes = chDetails.expectedTravelTimes;
				choreographyID=(chDetails.nextID++);
				chDetails.members++;
			}
			else{   //New choreography
				expectedTravelTimes = computeExpectedTime(polygon.expectedCompleteTime);
				chDetails = new ChoreographyDetails(polygon.choreography, expectedTravelTimes);
				choreographyID=1;
				choreographies.put(polygon.choreography, chDetails);
			}
		}
		else{
			expectedTravelTimes = computeExpectedTime(polygon.expectedCompleteTime);
			chDetails=null;
			choreographyID=0;
		}

		direction[X] = points[next()][X]-points[goingForward][X];
		direction[Y] = points[next()][Y]-points[goingForward][Y];
		updateVelocityVector();
		goingForward=next();
		//update();
	}

	private float[] computeExpectedTime(Float expectedCompleteTime){
		float[] ret = new float[points.length];
		if(expectedCompleteTime!=null) {
			float wholeDist=0f;
			for (int i = 0; i < points.length; i++) {
				wholeDist += MyMath.dist(points[i][X], points[i][Y], points[next(i)][X], points[next(i)][Y]);
			}
			for (int i = 0; i < points.length; i++) {
				ret[i] = MyMath.dist(points[i][X], points[i][Y], points[next(i)][X], points[next(i)][Y])
						*expectedCompleteTime/wholeDist;
			}
		}
		else {
			for(int i=0; i<points.length; i++){
				ret[i]= MyMath.dist(points[i][X], points[i][Y], points[next(i)][X], points[next(i)][Y]) / originalVelocity;
			}
		}
		return ret;
	}

	public void update(){
		float x = thisPhys.getX(), y= thisPhys.getY();
		if( checkOnNext(x, X) || checkOnNext(y, Y) ) {
			direction[X] = points[next()][X]-x;//points[goingForward][X];
			direction[Y] = points[next()][Y]-y;//points[goingForward][Y];
			//Sperpetuo assurdo per evitare che nelle coreografie un oggetto vada più veloce o più lento perchè sfora di più o di meno rispetto al limite
			if(MainActivity.fixKinematicPolygonChoreography){
				velocity = MyMath.dist(x, y, points[next()][X], points[next()][Y]) / expectedTravelTimes[goingForward];
				if(chDetails!=null) {
					if (chDetails.members % 2 == 1) {
						velocity *= 1.f + (chDetails.updates - (chDetails.members / 2)) * ChoreographyDetails.chDelayCorrector;
					} else {
						if (chDetails.updates < (chDetails.members / 2)) {
							velocity *= 1.f + (chDetails.updates - (chDetails.members / 2)) * ChoreographyDetails.chDelayCorrector;
						} else {
							velocity *= 1.f + (chDetails.updates - (chDetails.members / 2) + 1) * ChoreographyDetails.chDelayCorrector;
						}
					}
					chDetails.update();
				}
			}
			updateVelocityVector();
			goingForward = next();
		}
	}

	private boolean checkOnNext(float value, int axis){
		if(direction[axis]>0 && value>=points[goingForward][axis]) return true;
		else if (direction[axis]<0 && value<=points[goingForward][axis]) return true;
		else return false;
	}

	private int next(){
		return (goingForward+step+points.length)%points.length;
	}

	private int next(int i){
		return (i+step)%points.length;
	}

	@Override
	public void clear(){
		if(chDetails!=null)
			chDetails.remove();
	}

	private class ChoreographyDetails {
		private final String name;
		private int members=1;
		private int nextID=0;
		private final float[] expectedTravelTimes;
		protected int updates = 0;
		protected final static float chDelayCorrector =.005f;

		ChoreographyDetails(String name, float[] expectedTravelTimes){
			this.name=name;
			this.expectedTravelTimes = expectedTravelTimes;
		}

		public void update(){
			updates = (updates +1)%members;
		}

		public void remove() {
			members--;
			if(members==0)
				choreographies.remove(name);
		}
	}
}

class Kinematic_CircularPattern_Cmpnt extends Kinematic_Cmpnt {
	public float center[];
	public float angle;
	private float next[];
	private final float angleStep, radius;

	protected Kinematic_CircularPattern_Cmpnt(Entity owner, FloatValue vel, Kinematic_Properties kineP) {
		super( owner, null, vel );
		Circular_Properties circular = (Circular_Properties)kineP.subClass;
		this.center= circular.center; //n X 2
		//this.angle= circular.startAngle;
		this.radius= circular.radius;
		angleStep = circular.clockwise ? -45 : 45;
		next = new float[2];
		update();
	}

	public void update(){
		float x = thisPhys.getX(), y= thisPhys.getY();

		angle = getAngle(x, y);
		float nextRadAngle = MyMath.toRadians(angle+angleStep);
		next[X]= MyMath.cos(nextRadAngle) * radius + center[X];
		next[Y]= MyMath.sin(nextRadAngle) * radius + center[Y];
		direction[X] = next[X] - x;
		direction[Y] = next[Y] - y;

		updateVelocityVector();
	}

	private float getAngle(float x, float y){
		return MyMath.toDegrees(MyMath.atan2( y-center[Y], x-center[X] ));
	}
}