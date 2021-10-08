package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class A_Multiple implements Action {
	private final Action actions[];
	private int i;

	public A_Multiple(Action[] actions) {
		this.actions = actions.clone();
	}

	@Override
	public boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		if(loggable!=null)
			loggable.appendLog("\n\tMultiple Action:\t"+actions.length+" actions to be performed");
		for(i=0; i<actions.length; i++)
			actions[i].act(entity, gs, loggable);
		return true;
	}
}
