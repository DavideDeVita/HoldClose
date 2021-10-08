package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class A_ChangeElement implements Action {
	private final GameElement element;

	public A_ChangeElement(GameElement element) {
		this.element = element;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		if(loggable!=null)
			loggable.appendLog("\n\tChange Element Action:\t"+entity+" will go from "+entity.getHealth().element+" to "+element);
		entity.getHealth().element = element;
		return true;
	}
}