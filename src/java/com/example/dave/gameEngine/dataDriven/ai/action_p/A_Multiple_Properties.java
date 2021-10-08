package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.A_Multiple;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class A_Multiple_Properties extends Action_Properties{
	public Action_Properties actions[];

	@Override public void reset() {
		super.reset();
		actions=null;
	}

	@Override public A_Multiple_Properties clone() {
		A_Multiple_Properties newInstance = new A_Multiple_Properties();
		copyInto(newInstance);
		newInstance.actions = new Action_Properties[actions.length];
		for(int i=0; i<actions.length; i++)
			newInstance.actions[i] = actions[i].clone();
		return newInstance;
	}

	@Override public boolean isReady() {
		return super.isReady() &&
				actions!=null && actions.length>1;
	}

	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_Multiple_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(actions==null)               msg+="\n\tactions is null";
			else if(actions.length<2)       msg+="\n\tless than 2 actions";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_Multiple build(@Nullable Object caller) {
		Action[] ret = new Action[actions.length];
		int i=0;
		for(Action_Properties a_p : actions)
			ret[i++] = a_p.build(caller);
		return new A_Multiple(ret);
	}
}