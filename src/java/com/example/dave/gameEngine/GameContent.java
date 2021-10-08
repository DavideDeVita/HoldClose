package com.example.dave.gameEngine;

public abstract class GameContent {
	protected GamePhase phase;

	public abstract void start();

	public final boolean isOn(){
		return phase.isOn();
	}

	public abstract boolean update(float deltaTime);

	public abstract void render();
}
