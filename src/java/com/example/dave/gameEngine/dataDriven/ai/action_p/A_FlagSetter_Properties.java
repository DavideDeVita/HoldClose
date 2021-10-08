package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.A_FlagSetter;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class A_FlagSetter_Properties extends Action_Properties{
	public String flagName;
	public Object value;
	@Override public void reset() {
		super.reset();
		flagName =null;
		value=null;
	}
	@Override public A_FlagSetter_Properties clone() {
		A_FlagSetter_Properties newInstance = new A_FlagSetter_Properties();
		copyInto(newInstance);
		newInstance.flagName = flagName;
		newInstance.value=value;
		return newInstance;
	}
	@Override public boolean isReady() {
		return super.isReady() &&
				(flagName!=null);
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg= flagName +" A_FlagSetter not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(flagName==null)       msg+="\n\tFlagName is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_FlagSetter build(@Nullable Object caller) {
		return new A_FlagSetter(flagName, value);
	}
}