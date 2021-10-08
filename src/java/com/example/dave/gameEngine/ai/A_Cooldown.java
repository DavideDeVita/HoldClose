package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;
@Deprecated
public class A_Cooldown implements Action {
	private long curr, nextAvaiable=0L;
	private final long cooldown;
	private final Action action;

	public A_Cooldown(Action action, long millis){
		this.cooldown=millis * 1_000_000L;
		this.action=action;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		curr = System.nanoTime();
		if(curr>=nextAvaiable){
			nextAvaiable = curr+cooldown;
			if(loggable!=null)
				loggable.appendLog("\n\tAction by Cooldown:\t Success! Next available is in "+((nextAvaiable*1f)/1_000_000_000L)+" seconds");
			return action.act(entity, gs);
		}
		if(loggable!=null)
			loggable.appendLog("\n\tAction by Cooldown:\t Failed! Need to wait "+(((nextAvaiable-curr)*1f)/1_000_000_000L)+" more seconds");
		return false;
	}
}
