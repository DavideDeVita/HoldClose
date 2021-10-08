package com.example.dave.gameEngine.dataDriven.ai.field_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.decisionTree.F_Health;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class F_Health_Properties extends F_Savable_Properties<Float> {
	@Override
	public Field_Properties<Float> clone() {
		F_Health_Properties newInstance = new F_Health_Properties();
		copyInto(newInstance);
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public PropertyException getErrors() {
		return null;
	}

	@Override
	public Field<Float> build(@Nullable Object caller) {
		//Unnecessary.. but more readable
		if(saveAs==null)
			return new F_Health();
		else
			return new F_Health(saveAs);
	}
}
