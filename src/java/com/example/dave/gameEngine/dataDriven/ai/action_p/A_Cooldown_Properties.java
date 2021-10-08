package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.A_Cooldown;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;
@Deprecated
public class A_Cooldown_Properties extends A_Recursive {
	public long millisec;
	@Override public void reset() {
		super.reset();
		millisec =0L;
	}
	@Override public A_Cooldown_Properties clone() {
		A_Cooldown_Properties newInstance = new A_Cooldown_Properties();
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
			String msg=name+" A_Chance_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(millisec <=0L)              msg+="\n\tCooldown is less than 0 ";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_Cooldown build(@Nullable Object caller) {
		return new A_Cooldown(action.build(caller), millisec);
	}
}