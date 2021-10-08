package com.example.dave.gameEngine.dataDriven.ai.field_p;

import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;

public abstract class Field_Properties<F> extends Properties<Field_Properties<F>> implements Buildable<Field<F>> {
	public Field_Properties() {
		super(null);
	}
}
