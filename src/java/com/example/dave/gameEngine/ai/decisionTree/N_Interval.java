package com.example.dave.gameEngine.ai.decisionTree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public class N_Interval extends N_Predicate {
	private final Float min, max;
	private final Field<Float> field;
	private float fieldVal;

	public N_Interval(@NonNull Node onTrue, @NonNull Node onFalse, @Nullable Float min, @Nullable Float max, @NonNull Field<Float> field) {
		super(onTrue, onFalse);
		this.min=min;
		this.max=max;
		this.field = field;
	}

	@Override
	public boolean evaluatePredicate(Entity entity, GameSection gs) {
		fieldVal = field.fetch(entity, gs, this);
		predicateResult = ( min==null || min<=fieldVal ) && ( max==null || fieldVal<=max );
		appendLog("\n\tInterval Node:  "+min+" <= "+fieldVal+" <= "+max+" ?\t\t" + predicateResult);
		return predicateResult;
	}
}