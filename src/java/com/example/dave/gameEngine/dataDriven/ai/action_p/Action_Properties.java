package com.example.dave.gameEngine.dataDriven.ai.action_p;

import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;

public abstract class Action_Properties extends Properties<Action_Properties> implements Buildable<Action> {
	public String name;

	public Action_Properties() {
		super(null);
	}

	@Override
	public void reset(){
		name=null;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="";
			//if(name==null)       msg+="name is null";
			return new PropertyException(msg);
		}
		return null;
	}

	void copyInto(Action_Properties into) {
		into.name=name;
	}
}
