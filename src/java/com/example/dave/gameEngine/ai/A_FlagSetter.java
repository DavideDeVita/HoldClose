package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.entity_component.Entity;

public class A_FlagSetter implements Action {
	private final String flagName;
	private final Object value;

	public A_FlagSetter(String flagName, Object value) {
		this.flagName = flagName;
		this.value = value;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		if(loggable!=null)
			loggable.appendLog("\n\tFlag Setter Action:\tSetting "+flagName+" to "+value);
		entity.getFlags().setFlag(flagName, value);
		return true;
	}
}
