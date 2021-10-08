package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class A_NoAction implements Action {
	@Override
	public boolean act(Entity entity, GameSection gs) {
		return false;
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		if(loggable!=null)
			loggable.appendLog("\n\tNo Action:\tEmpty Action statement. 'cause.. why not?");
		return false;
	}
}
