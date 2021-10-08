package com.example.dave.gameEngine.dataDriven.ai.action_p;

import com.example.dave.gameEngine.dataDriven.PropertyException;

abstract class A_Recursive extends Action_Properties {
	public Action_Properties action;
	@Override public void reset() {
		super.reset();
		action=null;
	}
	@Override public boolean isReady() {
		return super.isReady() &&
				action!=null && action.isReady();
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_Chance_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(action==null)               msg+="\n\taction is null";
			else if(!action.isReady())       msg+="\n\t"+action.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	//@Override
	void copyInto(A_Recursive into) {
		super.copyInto(into);
		into.action = action;
	}
}