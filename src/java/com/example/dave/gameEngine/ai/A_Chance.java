package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.entity_component.Entity;
@Deprecated
public class A_Chance extends A_Recursive {
	private final FloatValue chance;

	public A_Chance(Action action, FloatValue chance){
		super(action);
		this.chance=chance;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs){
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		if(MyMath.randomChance(chance.get())) {
			if(loggable!=null)
				loggable.appendLog("\n\tAction by Chance:\t Passed");
			return action.act(entity, gs);
		}
		if(loggable!=null)
			loggable.appendLog("\n\tAction by Chance:\t Failed");
		return false;
	}
}
