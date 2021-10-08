package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.ai.A_ApplyTargetedImpulse;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.field_p.Field_Properties;
import com.example.dave.gameEngine.entity_component.Entity;

import static com.example.dave.gameEngine.FloatValue._0f;

public class A_ApplyTargetedImpulse_Properties extends A_ApplyImpulse_Properties {
	public Field_Properties<Entity> field;
	public boolean towards;
	public FloatValue biasX=_0f, biasY=_0f;

	@Override public void reset() {
		super.reset();
		if(field!=null)
			field.reset();
		field=null;
		towards=false;
		this.biasX = this.biasY = _0f;
	}

	@Override
	public A_ApplyTargetedImpulse_Properties clone() {
		A_ApplyTargetedImpulse_Properties newInstance = new A_ApplyTargetedImpulse_Properties();
		copyInto(newInstance);
		newInstance.field = field.clone();
		newInstance.towards=towards;
		newInstance.biasX=biasX;
		newInstance.biasY=biasY;
		return newInstance;
	}

	@Override public boolean isReady() {
		return super.isReady() &&
				(field!=null && field.isReady());
	}

	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" A_ApplyTargetedImpulse_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(field==null)       msg+="\n\tField is null";
			else if(!field.isReady())       msg+="\n\t"+field.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_ApplyTargetedImpulse build(@Nullable Object caller) {
		return new A_ApplyTargetedImpulse(intensity, biasX, biasY, x, y, weight, saveDirectionAs, field.build(caller), towards);
	}
}
