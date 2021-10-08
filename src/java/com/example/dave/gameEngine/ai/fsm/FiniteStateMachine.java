package com.example.dave.gameEngine.ai.fsm;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.AI_engine;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.entity_component.Entity;

import java.util.LinkedList;
import java.util.List;

public class FiniteStateMachine implements AI_engine {
	State curr;
	private String log="";
	private List<Action> toDo = new LinkedList<>();

	public FiniteStateMachine(State startState){
		curr=startState;
	}

	public List<Action> step(Entity This, GameSection gs){
		toDo.clear();
		for (Transition t : curr.transitions){
			if(t.condition.evaluate(This, gs)){
				toDo.addAll(curr.onExit);
				toDo.addAll(t.onTransition);
				toDo.addAll(t.destState.onEntry);
				curr = t.destState;
			}
			//implicit else
			toDo.addAll(curr.onPersist);
		}
		return toDo;
	}

	@Override
	public List<Action> exec(Entity This, GameSection gs) {
		return step(This, gs);
	}

	@Override
	public void log() {
		curr.log();
	}

	@Override
	public void appendLog(String log) {
		this.log+=log;
	}
}
