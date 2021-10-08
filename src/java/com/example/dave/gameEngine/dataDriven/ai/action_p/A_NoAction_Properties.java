package com.example.dave.gameEngine.dataDriven.ai.action_p;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.ai.A_NoAction;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public final class A_NoAction_Properties extends Action_Properties {
	@Override public void reset() {
		super.reset();
	}
	@Override public A_ChangeElement_Properties clone() {
		A_ChangeElement_Properties newInstance = new A_ChangeElement_Properties();
		copyInto(newInstance);
		return newInstance;
	}
	@Override public boolean isReady() {
		return true;
	}
	@Override public PropertyException getErrors() {
		if(!isReady()){
			String msg= " A_NoAction not ready (How on earth?):";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public A_NoAction build(@Nullable Object caller) {
		return new A_NoAction();
	}
}
