package com.example.dave.gameEngine.dataDriven.ai.node_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.decisionTree.N_Cooldown;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class N_Cooldown_Properties extends N_Predicate_Properties {
	public long millisec;
	@Override public void reset() {
		super.reset();
		millisec =0L;
	}
	@Override public N_Cooldown_Properties clone() {
		N_Cooldown_Properties newInstance = new N_Cooldown_Properties();
		copyInto(newInstance);
		newInstance.millisec = millisec;
		return newInstance;
	}
	@Override public boolean isReady() {
		return super.isReady() &&
				millisec >0L;
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=" N_Chance_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(millisec <=0L)              msg+="\n\tCooldown is less than 0 ";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public N_Cooldown build(@Nullable Object caller) {
		return new N_Cooldown(onTrue.build(caller), onFalse.build(caller), millisec);
	}
}
