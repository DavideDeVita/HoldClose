package com.example.dave.gameEngine.dataDriven.ai.node_p;

import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.ai.decisionTree.L_FirstCall;
import com.example.dave.gameEngine.ai.decisionTree.Leaf;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.DecisionTree_Properties;

public class L_FirstCall_Properties extends Leaf_Properties {
	public String firstCallActionName =null;

	@Override
	public void reset() {
		super.reset();
		firstCallActionName =null;
	}

	@Override
	public DTNode_Properties clone() {
		L_FirstCall_Properties newInstance = new L_FirstCall_Properties();
		copyInto(newInstance);
		newInstance.firstCallActionName = firstCallActionName;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (super.isReady()) &&
				(firstCallActionName !=null)
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Leaf_Properties not ready:";
			if(!super.isReady())                    msg+="\n\t"+super.getErrors().getMessage();
			if(firstCallActionName ==null)               msg+="\n\tfirstCallAction is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Leaf build(Object caller) {
		DecisionTree_Properties dt_p = (DecisionTree_Properties) caller;
		Action action = dt_p.actions.get(actionName).build(dt_p);
		Action firstCallAction = dt_p.actions.get(firstCallActionName).build(dt_p);
		if(priority==null)
			return new L_FirstCall(actionName, action, firstCallAction);
		else
			return new L_FirstCall(actionName, priority, action, firstCallAction);
	}
}
