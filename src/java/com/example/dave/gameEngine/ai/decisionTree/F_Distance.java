package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.entity_component.IdentifiedGameObject;

public class F_Distance extends F_Savable<Float> {
	private final Field<Entity> field;
	private GameObject from;
	private float dist; //allocated once
	private final boolean x, y;

	public F_Distance(String saveAs, Field<Entity> field, boolean x, boolean y){
		super(saveAs);
		//this.from =IdentifiedGameObject.getById(gObj_ID);
		this.field=field;
		this.x = x;
		this.y=y;
	}

	public F_Distance(Field<Entity> field, boolean x, boolean y){
		super();
		//this.from =IdentifiedGameObject.getById(gObj_ID);
		this.field=field;
		this.x = x;
		this.y=y;
	}

	@Override
	public Float fetch(Entity This, GameSection gs){
		return fetch(This, gs, null);
	}

	@Override
	public Float fetch(Entity This, GameSection gs, Loggable loggable) {
		from = (GameObject) field.fetch(This, gs, loggable);
		if(!gs.gObjectsAS.contains( (GameObject)This) || !gs.gObjectsAS.contains(from)) {
			if(_Log.LOG_ACTIVE){
				_Log.w("AI", "Distance of "+This+" from Game Object "+ from +" but one of them is no more a part of the Game World");}
			return null;
		}
		//else
		if(x && y) {
			dist = MyMath.dist(This.getPhysical().getX(), This.getPhysical().getY(),
					from.getPhysical().getX(), from.getPhysical().getY());
		}
		else if(x){
			dist = MyMath.diff(This.getPhysical().getX(), from.getPhysical().getX());
		}
		else{
			dist = MyMath.diff(This.getPhysical().getY(), from.getPhysical().getY());
		}
		saveValue(This, dist);
		if(loggable!=null)
			loggable.appendLog("\n\t\t Distance Field:\t Distance between "+This+" and "+ from +" =\t"+dist);
		return dist;
	}
}
