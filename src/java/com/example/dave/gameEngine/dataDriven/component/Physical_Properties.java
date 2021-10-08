package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.GameObject;

import static com.example.dave.gameEngine.FloatValue._0f;
import static com.example.dave.gameEngine.FloatValue._1f;
import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class Physical_Properties extends Properties<Physical_Properties> {
	public float x=0f,y=0f;
	public float angle=0;
	public float dimensionX=0f, dimensionY=0f;
	public GameObject.Type type;
	public FloatValue velocity= _1f;
	public FloatValue[] direction = new FloatValue[] { _0f, _0f };
	public float angularVel=0;
	public float density = 0.5f, friction = 0.5f, restitution=0.25f;
	public boolean lockRotation;
	public Kinematic_Properties kine_p=null;
	//
	private static final Pool<Physical_Properties> physicalPool = new Pool<>(
			new Pool.PoolObjectFactory<Physical_Properties>() {
				@Override
				public Physical_Properties createObject() {
					return new Physical_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.physical_Properties_Pool_size)
	);

	private Physical_Properties(){
		super(physicalPool);
	}

	public static Physical_Properties _new(){
		return physicalPool.newObject();
	}

	@Override
	public void free() {
		if(kine_p!=null)
			kine_p.free();
		super.free();
	}

	@Override
	public void reset() {
		this.x = this.y = 0f; this.angle=0;
		this.dimensionX = this.dimensionY = 1f;
		this.type = null;
		this.density = 0.5f; this.friction = 0.2f; this.restitution=0.25f;
		this.velocity= _1f;
		if(direction==null)
			direction = new FloatValue[]{_0f, _0f};
		this.angularVel=0;
		this.lockRotation=false;
		this.kine_p=null;
	}

	@Override
	public Physical_Properties clone() {
		Physical_Properties newInstance= _new();
		newInstance.x = x;	newInstance.y = y;
		newInstance.angle=angle;
		newInstance.dimensionX = dimensionX; newInstance.dimensionY = dimensionY;
		newInstance.type = type;
		newInstance.velocity=velocity;
		newInstance.direction=new FloatValue[]{direction[X], direction[Y]};
		newInstance.angularVel=angularVel;
		newInstance.density=density; newInstance.friction=friction; newInstance.restitution=restitution;
		newInstance.lockRotation=lockRotation;
		if(kine_p!=null)     newInstance.kine_p = kine_p.clone();
		else                newInstance.kine_p =null;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return type!=null &&
				dimensionX>0f && dimensionY>0f &&
				direction!=null && velocity!=null &&
				(type != GameObject.Type.Kinematic || (kine_p!=null && kine_p.isReady()) );
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Physical_Properties not ready:";
			if(type==null) msg+="\n\ttype is null";
			if(dimensionX<=0f) msg+="\n\tdimensionX<=0";
			if(dimensionY<=0f) msg+="\n\tdimensionY<=0";
			if(direction==null) msg+="\n\tdirection is null";
			if(velocity==null) msg+="\n\tvelocity is null";
			if(type==GameObject.Type.Kinematic && kine_p==null)         msg+="\n\tType is Kinematic but kine_p is null";
			else if(type==GameObject.Type.Kinematic && !kine_p.isReady())     msg+="\n\tType is Kinematic but "+kine_p.getErrors();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public String toString() {
		return "Physical_Properties{" +
				"x=" + x +
				", y=" + y +
				", dimensionX=" + dimensionX +
				", dimensionY=" + dimensionY +
				", type=" + type +
				", velocity=" + velocity +
				", density=" + density +
				", friction=" + friction +
				", restitution=" + restitution +
				'}';
	}
}
