package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.ai.A_ApplyTargetedImpulse;
import com.example.dave.gameEngine.ai.A_Shoot;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

import static com.example.dave.gameEngine.FloatValue._0f;

public class A_Shoot_Properties extends Action_Properties {
	public A_ApplyTargetedImpulse_Properties adi_p = null;
	public FloatValue x=_0f, y=_0f;

	@Override
	public void reset() {
		super.reset();
		adi_p = null;
		x = y = _0f;
	}

	@Override
	public A_Shoot_Properties clone() {
		A_Shoot_Properties newInstance = new A_Shoot_Properties();
		copyInto(newInstance);
		newInstance.adi_p = adi_p.clone();
		newInstance.x=x;
		newInstance.y=y;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return super.isReady() &&
				( adi_p!=null && adi_p.isReady() ) &&
				(x!=null && y!=null)
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_Shoot_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(adi_p==null)       msg+="\n\tAim is null";
			else if(!adi_p.isReady())       msg+="\n\t"+adi_p.getErrors().getMessage();
			if(x==null)       msg+="\n\tx is null";
			if(y==null)       msg+="\n\ty is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Action build(@Nullable Object caller) {
		A_ApplyTargetedImpulse aim = adi_p.build(this);
		return new A_Shoot(aim, x, y);
	}
}
