package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.Kinematic_Cmpnt;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class Kinematic_Properties extends Properties<Kinematic_Properties> {
	private Kinematic_Cmpnt.KinematicPattern pattern;

	public Kinematic_SubClass_Properties subClass;
	//
	private static final Pool<Kinematic_Properties> kinematicPool = new Pool<>(
			new Pool.PoolObjectFactory<Kinematic_Properties>() {
				@Override
				public Kinematic_Properties createObject() {
					return new Kinematic_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.kinematic_Properties_Pool_size)
	);
	private Kinematic_Properties(){
		super(kinematicPool);
	}

	public static Kinematic_Properties _new() {
		return kinematicPool.newObject();
	}

	public void setPattern(Kinematic_Cmpnt.KinematicPattern pattern){
		this.pattern=pattern;
		switch (pattern){
			case BoxBounce:
				subClass = new BoxBounce_Properties();
				break;
			case Polygon:
				subClass = new Polygon_Properties();
				break;
			case Circular:
				subClass = new Circular_Properties();
				break;
		}
	}

	public Kinematic_Cmpnt.KinematicPattern getPattern() {
		return pattern;
	}

	public abstract static class Kinematic_SubClass_Properties extends Properties<Kinematic_SubClass_Properties> {
		public Kinematic_SubClass_Properties() {
			super(null);
		}
	}
	public static class BoxBounce_Properties extends Kinematic_SubClass_Properties {
		public Box area;

		@Override public void reset() {
			this.area=null;
		}

		@Override public Kinematic_SubClass_Properties clone() {
			BoxBounce_Properties newInstance = new BoxBounce_Properties();
			newInstance.area=new Box(area);
			return newInstance;
		}

		@Override public boolean isReady() {
			return area!=null;
		}

		@Override public PropertyException getErrors() {
			if(!isReady()){
				String msg="BoxBounce_Properties not ready:";
				if(area==null)        msg+="pattern BoxBounce but area is null";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class Circular_Properties extends Kinematic_SubClass_Properties {
		public float center[], radius, startAngle;
		public boolean clockwise;

		@Override public void reset() {
			if(center==null)
				center = new float[2];
			radius=0;
			startAngle =0;
			clockwise=false;
		}

		@Override public Kinematic_SubClass_Properties clone() {
			Circular_Properties newInstance = new Circular_Properties();
			newInstance.center = new float[]{center[X], center[Y]};
			newInstance.radius=radius;
			newInstance.startAngle = startAngle;
			newInstance.clockwise=clockwise;
			return newInstance;
		}

		@Override public boolean isReady() {
			return (center!=null && radius>=0f);
		}

		@Override public PropertyException getErrors() {
			if(!isReady()){
				String msg="Circular_Properties not ready:";
				if(center==null)       msg+="pattern Circular but center is null";
				if(radius<0)          msg+="pattern Circular but radius<0";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class Polygon_Properties extends Kinematic_SubClass_Properties {
		public float points[][];
		public boolean clockwise;
		public String choreography;
		public Float expectedCompleteTime;
		public int startingPoint;

		@Override public void reset() {
			points=null;
			clockwise =false;
			choreography =null;
			startingPoint=0;
			expectedCompleteTime=null;
		}

		@Override public Kinematic_SubClass_Properties clone() {
			Polygon_Properties newInstance = new Polygon_Properties();
			newInstance.clockwise=clockwise;
			newInstance.choreography = choreography;
			newInstance.expectedCompleteTime=expectedCompleteTime;
			if(points!=null){
				newInstance.points=new float[points.length][2];
				for(int i=0; i<points.length; i++){
					newInstance.points[i][X] = points[i][X];
					newInstance.points[i][Y] = points[i][Y];
				}
			}
			newInstance.startingPoint = startingPoint;
			return newInstance;
		}

		@Override public boolean isReady() {
			return points!=null &&
					points.length>1 &&
					(expectedCompleteTime==null || expectedCompleteTime>0)
					;
		}

		@Override public PropertyException getErrors() {
			if(!isReady()){
				String msg="Polygon_Properties not ready:";
				if(points!=null)                    msg+="\npattern Polygon but points is null";
				if(points.length>1)                 msg+="\npattern Polygon but not enough points";
				if(expectedCompleteTime==null)      msg+="\nexpectedCompleteTime is null";
				else if(expectedCompleteTime<=0)    msg+="\nexpectedCompleteTime <= 0 ("+expectedCompleteTime+")";
				return new PropertyException(msg);
			}
			return null;
		}
	}

	@Override public void reset() {
		subClass.free();
	}

	@Override public Kinematic_Properties clone() {
		Kinematic_Properties newInstance= _new();
		newInstance.subClass = subClass.clone();
		newInstance.pattern=pattern;
		return newInstance;
	}

	@Override public boolean isReady() {
		return pattern!=null &&
				(subClass!=null && subClass.isReady())
				;
	}

	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg="Kinematic_Properties not ready:";
			if(pattern==null)               msg+="\n\tpattern is null";
			if(subClass==null)              msg+="\n\tsubClass is null";
			else if(!subClass.isReady())    msg+="\n\t"+subClass.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}
}
