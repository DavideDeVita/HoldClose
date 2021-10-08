package com.example.dave.gameEngine.dataDriven.ai.node_p;

import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.ai.decisionTree.Leaf;
import com.example.dave.gameEngine.ai.decisionTree.Node;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.DecisionTree_Properties;
import com.example.dave.gameEngine.dataDriven.ai.action_p.Action_Properties;

public class Leaf_Properties extends DTNode_Properties {
	public String actionName;
	public _Log.Priority priority;

	@Override
	public void reset() {
		actionName =null;
		priority=null;
	}

	@Override
	public DTNode_Properties clone() {
		Leaf_Properties newInstance = new Leaf_Properties();
		newInstance.actionName = actionName;
		newInstance.priority = priority;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (actionName !=null)
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Leaf_Properties not ready:";
			if(actionName ==null)               msg+="\naction is null";
			return new PropertyException(msg);
		}
		return null;
	}

	protected void copyInto(Leaf_Properties into){
		into.actionName = actionName;
		into.priority = priority;
	}

	@Override
	public Leaf build(Object caller) {
		DecisionTree_Properties dt_p = (DecisionTree_Properties) caller;
		Action action = dt_p.actions.get(actionName).build(dt_p);
		if(priority==null)
			return new Leaf(actionName, action);
		else
			return new Leaf(actionName, priority, action);
	}
}
