package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.ai.A_ChangeElement;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class A_ChangeElement_Properties  extends Action_Properties{
	public GameElement element=GameElement.NULL;

	@Override public void reset() {
		super.reset();
		element=GameElement.NULL;
	}
	@Override public A_ChangeElement_Properties clone() {
		A_ChangeElement_Properties newInstance = new A_ChangeElement_Properties();
		copyInto(newInstance);
		newInstance.element = element;
		return newInstance;
	}
	@Override public boolean isReady() {
		return super.isReady() &&
				(element!=null);
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg= " A_ChangeElement_Properties not ready:";
			if(!super.isReady())        msg+="\n\t"+super.getErrors().getMessage();
			if(element==null)       msg+="\n\telement is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_ChangeElement build(@Nullable Object caller) {
		return new A_ChangeElement(element);
	}
}
