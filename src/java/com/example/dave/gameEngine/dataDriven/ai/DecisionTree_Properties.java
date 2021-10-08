package com.example.dave.gameEngine.dataDriven.ai;

import android.util.ArrayMap;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.ai.AI_engine;
import com.example.dave.gameEngine.ai.decisionTree.DecisionTree;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.action_p.Action_Properties;
import com.example.dave.gameEngine.dataDriven.ai.node_p.DTNode_Properties;

import java.util.Map;

public class DecisionTree_Properties extends AI_Properties {
	public ArrayMap<String, Action_Properties> actions = new ArrayMap<>();
	public DTNode_Properties root;
	//
	private static final Pool<AI_Properties> decisionTreesPool = new Pool<>(
			new Pool.PoolObjectFactory<AI_Properties>() {
				@Override
				public DecisionTree_Properties createObject() {
					return new DecisionTree_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.decisionTree_Properties_Pool_size)
	);

	private DecisionTree_Properties(){
		super(decisionTreesPool);
	}

	public static DecisionTree_Properties _new() {
		return (DecisionTree_Properties)decisionTreesPool.newObject();
	}

	@Override
	public void fixIntraPropertiesConstraint() {}

	@Override
	public void reset() {
		super.reset();
		actions.clear();
		root=null;
	}

	@Override
	public DecisionTree_Properties clone() {
		DecisionTree_Properties newInstance = (DecisionTree_Properties)_new();
		newInstance.actions= new ArrayMap<>(actions.size());
		for(Map.Entry<String, Action_Properties> entry : actions.entrySet())
			newInstance.actions.put(entry.getKey(), entry.getValue());
		newInstance.root = root.clone();
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (actions!=null && actions.size()>1) &&
				(root!=null && root.isReady());
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=name+" DecisionTree_Properties not ready:";
			if(actions==null)               msg+="\n\tactions is null";
			else if(actions.size()<2)       msg+="\n\tless than 2 actions!";
			if(root==null)               msg+="\n\troot is null";
			else if(!root.isReady())       msg+="\n\t"+root.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public AI_engine build(Object caller) {
		return new DecisionTree(root.build(this));
	}
}
