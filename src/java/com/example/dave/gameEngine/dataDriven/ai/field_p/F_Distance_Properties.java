package com.example.dave.gameEngine.dataDriven.ai.field_p;

import com.example.dave.gameEngine.ai.decisionTree.F_Distance;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.Entity;

public class F_Distance_Properties extends F_Savable_Properties<Float>{
	public Field_Properties<Entity> field;
	public boolean x=true, y=true;

	@Override
	public void reset() {
		super.reset();
		if(field!=null)
			field.reset();
		field=null;
		x=y=true;
	}

	@Override
	public Field_Properties<Float> clone() {
		F_Distance_Properties newInstance = new F_Distance_Properties();
		copyInto(newInstance);
		newInstance.field = field.clone();
		newInstance.x=x;
		newInstance.y=y;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (x || y)  &&
				(field!=null && field.isReady());
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="F_Distance_Properties not ready:";
			if(!(x || y))   msg+="\nnor x nor y";
			if(field==null)       msg+="\n\tField is null";
			else if(!field.isReady())       msg+="\n\t"+field.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Field<Float> build(Object caller) {
		//Unnecessary, but more readable
		if(saveAs==null)
			return new F_Distance(field.build(caller), x, y);
		else{
				return new F_Distance(saveAs, field.build(caller), x, y);
		}
	}
}
