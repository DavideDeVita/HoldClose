package com.example.dave.gameEngine.dataDriven.ai.node_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.ai.decisionTree.N_Chance;
import com.example.dave.gameEngine.dataDriven.PropertyException;

import static com.example.dave.gameEngine.FloatValue._0f;

public class N_Chance_Properties extends N_Predicate_Properties {
	public FloatValue chance = _0f;
	@Override public void reset() {
		super.reset();
		chance = _0f;
	}
	@Override public N_Chance_Properties clone() {
		N_Chance_Properties newInstance = new N_Chance_Properties();
		copyInto(newInstance);
		newInstance.chance=chance;
		return newInstance;
	}
	@Override public boolean isReady() {
		return super.isReady() &&
				chance.between_excl(0f, 1f, true, true);
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=" N_Chance_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(chance.leq(0f))              msg+="\n\tChance is less than 0%";
			else if(chance.gte(1f))              msg+="\n\tChance is more than 1%";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public N_Chance build(@Nullable Object caller) {
		return new N_Chance(onTrue.build(caller), onFalse.build(caller), chance);
	}
}
