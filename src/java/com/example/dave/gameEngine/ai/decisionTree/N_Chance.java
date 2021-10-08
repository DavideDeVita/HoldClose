package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.entity_component.Entity;

public class N_Chance extends N_Predicate {
	private final FloatValue chance;

	public N_Chance(Node onTrue, Node onFalse, FloatValue chance) {
		super(onTrue, onFalse);
		this.chance=chance;
	}

	@Override
	public boolean evaluatePredicate(Entity entity, GameSection gs) {
		predicateResult = MyMath.randomChance(chance.get());
		appendLog("\n\tChance Node:\t"+ predicateResult);
		return predicateResult;
	}
}
