package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;

public class F_Flag<X> implements Field<X> {
	private final String name;

	private X value;

	public F_Flag(String name){
		this.name=name;
	}

	@Override
	public X fetch(Entity entity, GameSection gs) {
		return fetch(entity, gs, null);
	}

	@Override
	public X fetch(Entity entity, GameSection gs, Loggable loggable) {
		value = (X)entity.getFlags().getFlag(name);
		if(loggable!=null)
			loggable.appendLog("\n\t\t Flag Field:\t flag \""+name+"\":\t"+value);
		return value;
	}
}
