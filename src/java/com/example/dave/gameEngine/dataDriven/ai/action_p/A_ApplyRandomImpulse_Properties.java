package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.ai.A_ApplyRandomImpulse;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class A_ApplyRandomImpulse_Properties extends A_ApplyImpulse_Properties {
	public FloatValue dirX, dirY;

	@Override public void reset() {
		super.reset();
		dirX=dirY=null;
	}

	@Override public A_ApplyRandomImpulse_Properties clone() {
		A_ApplyRandomImpulse_Properties newInstance = new A_ApplyRandomImpulse_Properties();
		copyInto(newInstance);
		newInstance.dirX=dirX;
		newInstance.dirY=dirY;
		return newInstance;
	}

	@Override public boolean isReady() {
		return super.isReady() &&
				(!x || dirX!=null) &&
				(!y || dirY!=null)
				;
	}

	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_ApplyImpulse_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(x && dirX==null)       msg+="\n\tdirection over X is asked but not defined";
			if(y && dirY==null)       msg+="\n\tdirection over Y is asked but not defined";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_ApplyRandomImpulse build(@Nullable Object caller) {
		return new A_ApplyRandomImpulse(intensity, x, y, weight, saveDirectionAs, dirX, dirY);
	}
}