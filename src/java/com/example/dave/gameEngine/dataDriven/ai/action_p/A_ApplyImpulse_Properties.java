package com.example.dave.gameEngine.dataDriven.ai.action_p;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.dataDriven.PropertyException;

abstract class A_ApplyImpulse_Properties extends Action_Properties{
	public FloatValue intensity;
	public boolean x, y, weight;
	public String saveDirectionAs;

	@Override public void reset() {
		super.reset();
		intensity =null;
		x=y=weight=false;
		saveDirectionAs=null;
	}

	@Override public boolean isReady() {
		return super.isReady() &&
				intensity !=null &&
				(x||y);
	}

	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_ApplyImpulse_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(intensity ==null)               msg+="\n\tvalue is null";
			if(!x && !y)       msg+="\n\tnot x and not y";
			return new PropertyException(msg);
		}
		return null;
	}

	//@Override
	public void copyInto(A_ApplyImpulse_Properties into) {
		super.copyInto(into);
		into.intensity = intensity;
		into.x=x;
		into.y=y;
		into.weight=weight;
		into.saveDirectionAs=saveDirectionAs;
	}
}