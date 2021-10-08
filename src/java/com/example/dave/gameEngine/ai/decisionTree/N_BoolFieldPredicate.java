package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class N_BoolFieldPredicate extends N_Predicate {
	private Field<Boolean> field;
	Boolean fieldVal;

	public N_BoolFieldPredicate(Node onTrue, Node onFalse, Field<Boolean> field) {
		super(onTrue, onFalse);
		this.field=field;
	}

	@Override
	public final boolean evaluatePredicate(Entity entity, GameSection gs) {
		fieldVal = field.fetch(entity, gs, this);
		predicateResult = fieldVal==null ? false : fieldVal;
		appendLog("\n\t BoolFieldPredicate Node:  "+fieldVal+" ?\t "+predicateResult);
		return predicateResult;
	}
}
