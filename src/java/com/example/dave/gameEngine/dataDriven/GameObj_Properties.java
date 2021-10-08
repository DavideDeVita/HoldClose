package com.example.dave.gameEngine.dataDriven;

import android.util.ArrayMap;

import java.util.Map;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.ai.AI_Properties;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.dataDriven.ai.action_p.Action_Properties;
import com.example.dave.gameEngine.dataDriven.component.Control_Properties;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Flag_Properties;
import com.example.dave.gameEngine.dataDriven.component.Health_Properties;
import com.example.dave.gameEngine.dataDriven.component.Physical_Properties;
import com.example.dave.gameEngine.dataDriven.component.Resizeable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Summoner_Properties;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.entity_component.Resizeable_Cmpnt;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;
import static com.example.dave.gameEngine.entity_component.Kinematic_Cmpnt.KinematicPattern.Circular;
import static com.example.dave.gameEngine.entity_component.Kinematic_Cmpnt.KinematicPattern.Polygon;
import static com.example.dave.gameEngine.dataDriven.component.Kinematic_Properties.*;

public class GameObj_Properties extends PreLoadedProperties<GameObj_Properties> {
	public GameObject.OnHitBehaviour oHB;
	public Map<GameElement, Action_Properties> oHbEB;
	public GameObject.GO_Shape GOShape;
	public Physical_Properties phy_p;
	public Drawable_Properties draw_p;
	public Health_Properties health_p;
	public Resizeable_Properties resize_p;
	public Control_Properties control_p;
	public AI_Properties ai_p;
	public Flag_Properties flag_p;
	public Summoner_Properties summoner_p;
	public Integer explicitID;
	public boolean isAesthetic=false;
	//...
	private static final Map<String, GameObj_Properties> stdGObjects = new ArrayMap<>();
	private static final Pool<GameObj_Properties> gObjPool = new Pool<>(
			new Pool.PoolObjectFactory<GameObj_Properties>() {
				@Override
				public GameObj_Properties createObject() {
					return new GameObj_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.GO_Properties_Pool_size)
	);

	private GameObj_Properties(){
		super(stdGObjects, gObjPool);
	}
	
	public static GameObj_Properties _new(){
		return gObjPool.newObject();
	}

	@Override
	public void free() {
		if(phy_p!=null)      phy_p.free();
		if(draw_p!=null)     draw_p.free();
		if(resize_p!=null)   resize_p.free();
		if(health_p!=null)   health_p.free();
		if(control_p!=null)  control_p.free();
		if(ai_p!=null)       ai_p.free();
		if(flag_p!=null)     flag_p.free();
		if(summoner_p!=null) summoner_p.free();
		super.free();
	}

	@Override
	public void reset() {
		super.reset();
		this.oHB = null;
		this.oHbEB=null;
		this.explicitID=null;
		this.isAesthetic=false;
		this.GOShape = null;

		this.phy_p=null;
		this.draw_p=null;
		this.health_p=null;
		this.resize_p=null;
		this.control_p=null;
		this.ai_p=null;
		this.flag_p=null;
		this.summoner_p=null;
	}

	@Override
	public GameObj_Properties clone() {
		GameObj_Properties newInstance = _new();
		newInstance.name=name;
		newInstance.oHB = oHB;
		newInstance.oHbEB = oHbEB;
		newInstance.name = name;
		newInstance.isAesthetic=false;
		newInstance.explicitID = explicitID;
		newInstance.GOShape = GOShape;
		if(phy_p!=null)     newInstance.phy_p = phy_p.clone();
		else                newInstance.phy_p =null;

		if(draw_p!=null)     newInstance.draw_p = draw_p.clone();
		else                newInstance.draw_p =null;

		if(resize_p!=null)     newInstance.resize_p = resize_p.clone();
		else                newInstance.resize_p =null;

		if(health_p!=null)     newInstance.health_p = health_p.clone();
		else                newInstance.health_p =null;

		if(control_p!=null)     newInstance.control_p = control_p.clone();
		else                newInstance.control_p =null;

		if(ai_p!=null)     newInstance.ai_p = ai_p.clone();
		else                newInstance.ai_p =null;

		if(flag_p!=null)     newInstance.flag_p = flag_p.clone();
		else                newInstance.flag_p =null;

		if(summoner_p!=null)     newInstance.summoner_p = summoner_p.clone();
		else                newInstance.summoner_p =null;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return ( GOShape!=null &&
				(phy_p==null || phy_p.isReady() ) &&
				(draw_p==null || draw_p.isReady() ) &&
				(resize_p==null || resize_p.isReady() ) &&
				(health_p==null || health_p.isReady() ) &&
				(control_p==null || control_p.isReady() ) &&
				(ai_p==null || ai_p.isReady() ) &&
				(flag_p==null || flag_p.isReady() ) &&
				(summoner_p==null || summoner_p.isReady())
				//(  phy_p==null || ((phy_p.dimensionX>0 && phy_p.dimensionY>0) || resize_p!=null)  )
			);
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" GameObj_Properties not ready:";
			if(GOShape==null) msg+="\n\tGOShape is null";
			if(phy_p!=null && !phy_p.isReady()) msg+="\n\t"+phy_p.getErrors().getMessage();
			if(draw_p!=null && !draw_p.isReady()) msg+="\n\t"+draw_p.getErrors().getMessage();
			if(resize_p!=null && !resize_p.isReady()) msg+="\n\t"+resize_p.getErrors().getMessage();
			if(health_p!=null && !health_p.isReady()) msg+="\n\t"+health_p.getErrors().getMessage();
			if(control_p!=null && !control_p.isReady()) msg+="\n\t"+control_p.getErrors().getMessage();
			if(ai_p!=null && !ai_p.isReady()) msg+="\n\t"+ai_p.getErrors().getMessage();
			if(flag_p!=null && !flag_p.isReady()) msg+="\n\t"+flag_p.getErrors().getMessage();
			if(summoner_p!=null && !summoner_p.isReady()) msg+="\n\t"+summoner_p.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public String toString() {
		return name+"{" +
				"shape=" + GOShape +
				((explicitID!=null) ? " "+explicitID : "") +
				'}';
	}

	@Override
	public void fixIntraPropertiesConstraint() {
		if (phy_p != null){
			if(GOShape== GameObject.GO_Shape.Circle){
				if(phy_p.dimensionY<=0)
					phy_p.dimensionY=phy_p.dimensionX;
				else if (phy_p.dimensionX<=0)
					phy_p.dimensionX=phy_p.dimensionY;
			}

		/**Kinematic Pattern Start position*/
			if (phy_p.kine_p != null) {
				if (phy_p.kine_p.getPattern() == Polygon) {
					Polygon_Properties kine = (Polygon_Properties) phy_p.kine_p.subClass;
					this.phy_p.x = kine.points[kine.startingPoint][X];
					this.phy_p.y = kine.points[kine.startingPoint][Y];
				} else if (phy_p.kine_p.getPattern() == Circular) {
					Circular_Properties kine = (Circular_Properties) phy_p.kine_p.subClass;
					float radAngle = MyMath.toRadians(kine.startAngle);
					this.phy_p.x = kine.center[X] + MyMath.cos(radAngle) * kine.radius;
					this.phy_p.y = kine.center[Y] + MyMath.sin(radAngle) * kine.radius;
				}
			}
			/**Resizeable according to Health starting size*/
			if (health_p != null && resize_p != null) {
				phy_p.dimensionX = Resizeable_Cmpnt.getProportionalSize(resize_p.minDimX, resize_p.maxDimX, 1);
				phy_p.dimensionY = Resizeable_Cmpnt.getProportionalSize(resize_p.minDimY, resize_p.maxDimY, 1);
			} //Temporary dismantled beacuse it should be automatic in Health Constructor thanks to onLifeAltered()
			/**Only Dynamic Objects are controllable*/
			if (control_p != null) {
				if (phy_p.type != GameObject.Type.Dynamic)
					control_p = null;
			}
		}
	}
}