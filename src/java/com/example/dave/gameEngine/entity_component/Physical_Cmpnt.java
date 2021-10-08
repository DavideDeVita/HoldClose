package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.dataStructures.FixtureInfo;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.dataDriven.component.Physical_Properties;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Shape;
import com.google.fpl.liquidfun.Vec2;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;
import static com.example.dave.gameEngine.MyMath.pow;

public abstract class Physical_Cmpnt extends Component {
    public final Body body;
	final FixtureInfo fInfo;

	Physical_Cmpnt(Entity owner, Body body, FixtureInfo fInfo) {
		super(owner);
		this.body = body;
		this.fInfo=fInfo;
	}

	Physical_Cmpnt(Entity owner, Body body) {
		this(owner, body, null);
	}

	@Deprecated
	public static float angleCorrector(float angle) {
		return angle;
	}

	@Override
	public ComponentType type() { return ComponentType.Physics; }

    public float getX(){ return body.getPositionX(); }
	public float getY(){ return body.getPositionY(); }
    public float getAngle(){ return body.getAngle(); }

	public float[] getDirection(){
		return getDirection(null, false);
	}

	public float[] getDirection(float[] direction, boolean onlySign) {
		if(direction==null) direction = new float[2];
		direction[X] = body.getLinearVelocity().getX();
		direction[Y] = body.getLinearVelocity().getY();
		if(onlySign){
			direction[X] = MyMath.sign(direction[X]);
			direction[Y] = MyMath.sign(direction[Y]);
		}
		return direction;
	}
	public abstract float getArea();

	public abstract void updateSize(float dimX, float dimY);
	private static float norm; //this way is not allocated every time

	private static final Vec2 vVec = new Vec2();
	private void setVec(float[] direction, float intensity){
		norm= MyMath.norm(direction);
		if(norm!=0){
			vVec.setX(intensity*direction[X]/norm);
			vVec.setY(intensity*direction[Y]/norm);
		}
		else{
			vVec.setX(0);
			vVec.setY(0);
		}
	}

	public void setVelocityVector(float[] direction, float intensity) {
		setVec(direction, intensity);
		body.setLinearVelocity(vVec);
	}

	public void setVelocityVector(FloatValue[] direction, FloatValue intensity) {
		float d[]=new float[]{direction[X].get(), direction[Y].get()}, i=intensity.get();
		setVec(d, i);
		body.setLinearVelocity(vVec);
	}

	public void applyImpulse(float[] direction, float intensity) {
		setVec(direction, intensity);
		body.applyLinearImpulse(vVec, body.getWorldCenter(), true);
	}
}

class PhysicalBox_Cmpnt extends Physical_Cmpnt{
    private float width, height;

	protected PhysicalBox_Cmpnt(Entity owner, Body body, float width, float height) {
		super(owner, body);
		this.width=width;
		this.height=height;
	}

	protected PhysicalBox_Cmpnt(Entity owner, Body body, FixtureInfo fi, Physical_Properties physP) {
		super(owner, body, fi);
		this.width=physP.dimensionX;
		this.height=physP.dimensionY;
	}

	@Override
	public float getArea() {
		return (width*height);
	}

	@Override
	public void updateSize(float dimX, float dimY) {
		if(fInfo!=null){
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(dimX/2, dimY/2);
			fInfo.updateFixture(shape);
			this.width=dimX;
			this.height=dimY;
		}
	}
}

class PhysicalCircle_Cmpnt extends Physical_Cmpnt{
    private float radius;

	protected PhysicalCircle_Cmpnt(Entity owner, Body body, FixtureInfo fi, Physical_Properties physP) {
		super(owner, body, fi);
		this.radius= (physP.dimensionX+physP.dimensionY)/4;
	}

	@Override
	public float getArea() {
		return pow(radius, 2)*MyMath.PI;
	}

	@Override
	public void updateSize(float dimX, float dimY) {
		if(fInfo!=null){
			Shape shape = new CircleShape();
			shape.setRadius(dimX/2);
			//shape.setRadius((dimX+dimY)/4);
			fInfo.updateFixture(shape);
			this.radius=dimX/2;
		}
	}
}