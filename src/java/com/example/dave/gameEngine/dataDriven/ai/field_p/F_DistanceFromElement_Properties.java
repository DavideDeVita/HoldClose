package com.example.dave.gameEngine.dataDriven.ai.field_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.decisionTree.F_DistanceFromElement;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.GameElement;

public class F_DistanceFromElement_Properties extends F_Savable_Properties<Float> {
	public GameElement element;
	public String saveArgmin=null;

	@Override
	public void reset() {
		super.reset();
		element=null;
		saveArgmin=null;
	}

	@Override
	public Field_Properties<Float> clone() {
		F_DistanceFromElement_Properties newInstance = new F_DistanceFromElement_Properties();
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
	public Field<Float> build(@Nullable Object caller) {
		//Unnecessary, but more readable
		if(saveAs==null)
			return new F_DistanceFromElement(element);
		else{
			if(saveArgmin==null)
				return new F_DistanceFromElement(saveAs, element);
			else
				return new F_DistanceFromElement(saveAs, saveArgmin, element);
		}
	}
}
