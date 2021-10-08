package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.ComponentType;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.GameObject;

public class F_Health extends F_Savable<Float> {
	private float health; //Allocated once

	public F_Health(String saveAs){
		super(saveAs);
	}

	public F_Health(){
		super();
	}

	@Override
	public Float fetch(Entity This, GameSection gs) {
		return fetch(This, gs, null);
	}

	@Override
	public Float fetch(Entity This, GameSection gs, Loggable loggable) {
		if(!This.hasComponent(ComponentType.Health)) {
			if(_Log.LOG_ACTIVE){
				_Log.w("AI", "No health component in "+This+" while fetching health");}
			return null;
		}
		//else
		health=This.getHealth().health;
		saveValue(This, health);
		if(loggable!=null) {
			loggable.appendLog("\n\t\t Health Field:\t "+This+" health is:\t"+health);
		}
		return health;
	}
}
