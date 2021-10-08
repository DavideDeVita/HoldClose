package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class N_Cooldown extends N_Predicate {
	private long curr, last=0L;
	private final long cooldown;

	public N_Cooldown(Node onTrue, Node onFalse, long millis) {
		super(onTrue, onFalse);
		this.cooldown=millis * 1_000_000L;
	}

	@Override
	public boolean evaluatePredicate(Entity entity, GameSection gs) {
		curr = System.nanoTime();
		predicateResult = curr>=last+cooldown;
		appendLog("\n\tCooldown Node:  cooldown is: "+(cooldown/1_000_000_000F)+"\tlast was "+((curr-last)/1_000_000_000F)+" seconds ago. Is enough?" + predicateResult);
		if(predicateResult)
			last = curr;
		return predicateResult;
	}
}
