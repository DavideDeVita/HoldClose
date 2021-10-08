package com.example.dave.gameEngine.dataDriven.ai.field_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.ai.decisionTree.F_DistanceFromElement;
import com.example.dave.gameEngine.ai.decisionTree.F_NearestElement;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.Entity;
@Deprecated
public class F_NearestElement_Properties extends F_Savable_Properties<Entity> {
	public GameElement element;
	public String saveArgmin=null;

	@Override
	public void reset() {
		super.reset();
		element=null;
		saveArgmin=null;
	}

	@Override
	public Field_Properties<Entity> clone() {
		F_NearestElement_Properties newInstance = new F_NearestElement_Properties();
		copyInto(newInstance);
		newInstance.element = element;
		newInstance.saveArgmin = saveArgmin;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return element!=null;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="F_DistanceFromElement_Properties not ready:";
			if(element==null)               msg+="\nelement is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Field<Entity> build(@Nullable Object caller) {
		//Unnecessary, but more readable
		if(saveAs==null)
			return new F_NearestElement(element);
		else{
			if(saveArgmin==null)
				return new F_NearestElement(saveAs, element);
			else
				return new F_NearestElement(saveAs, saveArgmin, element);
		}
	}
}