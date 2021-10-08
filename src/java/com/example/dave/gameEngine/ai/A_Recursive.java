package com.example.dave.gameEngine.ai;

public abstract class A_Recursive implements Action {
	//recursive Actions can share their root's name
	protected final Action action;

	public A_Recursive(Action action){
		this.action=action;
	}
}
