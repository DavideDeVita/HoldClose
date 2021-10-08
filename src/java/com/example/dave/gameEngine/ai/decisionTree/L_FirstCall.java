package com.example.dave.gameEngine.ai.decisionTree;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.entity_component.Entity;

public class L_FirstCall extends Leaf {
	private final Action firstCallAction;
	private final String firstCallMsg;
	private boolean isLast;

	public L_FirstCall(String name, _Log.Priority priority, Action action, Action firstCallAction) {
		super(name, priority, action);
		this.firstCallAction=firstCallAction;
		this.firstCallMsg = "First"+name;
	}

	public L_FirstCall(String name, Action action, Action firstCallAction) {
		super(name, action);
		this.firstCallAction=firstCallAction;
		this.firstCallMsg = "First"+name;
	}

	@Override
	public Action evaluate(Entity This, GameSection gs, DecisionTree tree, String pathLog) {
		isLast=tree.isLast(this);
		if(isLast) {
			pathLog += "\nFirstCallLeaf (Consecutive call): "+name;
			tree.notifyTreeOutput(this, pathLog);
			return this.action;
		}
		else {
			pathLog += "\nFirstCallLeaf (First call): "+firstCallMsg;
			tree.notifyTreeOutput(this, pathLog);
			return firstCallAction;
		}
	}

	@Override
	protected String getMessage() {
		return isLast ? super.getMessage() : firstCallMsg;
	}
}
