package com.example.dave.gameEngine.dataDriven.ai.field_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.decisionTree.F_Flag;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class F_Flag_Properties<F> extends Field_Properties<F> {
	public String flagName=null;

	@Override
	public void reset() {
		flagName=null;
	}

	@Override
	public Field_Properties<F> clone() {
		F_Flag_Properties newInstance = new F_Flag_Properties<>();
		newInstance.flagName = flagName;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return flagName!=null;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="F_Flag_Properties not ready:";
			if(flagName==null)               msg+="\nflagName is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Field<F> build(@Nullable Object caller) {
		return new F_Flag<>(flagName);
	}
}
