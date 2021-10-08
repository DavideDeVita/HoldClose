package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;

public abstract class N_Predicate implements Node, Loggable {
	private final Node onTrue, onFalse;
	protected String logPredicate="";
	protected boolean predicateResult;

	public N_Predicate(Node onTrue, Node onFalse){
		this.onTrue=onTrue;
		this.onFalse=onFalse;
	}

	@Override
	public final Action evaluate(Entity entity, GameSection gs, DecisionTree tree, String pathLog) {
		logPredicate="";
		return evaluatePredicate(entity, gs) ?
				onTrue.evaluate(entity, gs, tree, pathLog+logPredicate) :
				onFalse.evaluate(entity,gs, tree, pathLog+logPredicate);
	}

	@Override
	public final void appendLog(String log) {
		logPredicate += log;
	}

	/**Evaluates the actual abstract predicate
	 *  this must also set the log Predicate in a format like below:
	 *  "\n NodeName:  boolean expression ?\t     boolean result \n"
	 * */
	public abstract boolean evaluatePredicate(Entity entity, GameSection gs);
}
