package com.example.dave.gameEngine.ai.decisionTree;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.entity_component.Entity;

public class Leaf implements Node {
	protected final Action action;
	protected final String name;
	final _Log.Priority priority;

	public Leaf(String name, _Log.Priority priority, Action action) {
		this.name = name;
		this.action = action;
		this.priority = priority;
	}

	public Leaf(String name, Action action) {
		this(name, _Log.Priority.Info, action);
	}

	@Override
	public Action evaluate(Entity This, final GameSection gs, DecisionTree tree, String pathLog) {
		pathLog += "\nLeaf: "+name;
		tree.notifyTreeOutput(this, pathLog);
		return this.action;
	}

	public final void log(){
		_Log.log(priority, "DecisionTreeAction", getMessage());
		_Log.log(priority, "Leaf", getMessage());
	}

	protected String getMessage(){
		return name;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj==null || !(obj instanceof Leaf)) return false;
		Leaf oth = (Leaf)obj;
		return name.equals(oth.name);
	}
}
