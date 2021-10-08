package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.AI_engine;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.entity_component.Entity;

import java.util.ArrayList;
import java.util.List;

/**A single-action Decision Tree */
public class DecisionTree implements AI_engine {
	private final Node root;
	private final ArrayList<Action> ret = new ArrayList<>(1);

	private Leaf last=null;
	private String log=null;

	public DecisionTree(Node root) {
		this.root = root;
		ret.add(null); //ret must be size 1
	}

	@Override
	public List<Action> exec(Entity entity, GameSection gs){
		if(_Log.LOG_ACTIVE){
			_Log.d("DecisionTree", "Starting tree");}
		ret.set(0, root.evaluate(entity, gs, this, "\n\nDecisionTree: Start!"));
		return ret;
	}

	@Override
	public void log() {
		_Log.log(last.priority, "DecisionTree", log);
	}

	void notifyTreeOutput(Leaf leaf, String pathLog){
		last=leaf;
		log=pathLog;
	}

	boolean isLast(Leaf leaf){
		return leaf.equals(last);
	}

	@Override
	public void appendLog(String log) {
		 this.log+=log;
	}
}
