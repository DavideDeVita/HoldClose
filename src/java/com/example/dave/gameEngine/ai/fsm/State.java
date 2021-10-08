package com.example.dave.gameEngine.ai.fsm;

import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Action;

import android.util.ArrayMap;
import java.util.List;
import java.util.Map;

public class State {
	String name;
	private final int logPrior;
	List<Action> onEntry;
	List<Action> onPersist;
	List<Action> onExit;
	List<Transition> transitions;

	final static Map<String, State> allStates = new ArrayMap<>();

	State(String name, int logPrior){
		this.logPrior=logPrior;
		if (!allStates.containsKey(name))
			this.name = name;
		else
			throw new RuntimeException(name+" already exists as a state");
	}

	void setOnEntry(final List<Action> onEntry){
		this.onEntry = onEntry;
	}

	void setOnPersist(final List<Action> onPersist){
		this.onPersist = onPersist;
	}

	void setOnExit(final List<Action> onExit){
		this.onExit = onExit;
	}

	public static State getByName(String name){
		return allStates.get(name);
	}

	public void log() {
		_Log.log(logPrior, "AI_FSM", name);
	}
}
