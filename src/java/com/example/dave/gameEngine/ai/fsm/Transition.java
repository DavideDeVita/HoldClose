package com.example.dave.gameEngine.ai.fsm;

import com.example.dave.gameEngine.ai.Action;

import java.util.List;

public class Transition {
	final Condition condition;
	final List<Action> onTransition;
	final State destState;

	public Transition(Condition condition, List<Action> onTransition, State destState) {
		this.condition = condition;
		this.onTransition = onTransition;
		this.destState = destState;
	}
}
