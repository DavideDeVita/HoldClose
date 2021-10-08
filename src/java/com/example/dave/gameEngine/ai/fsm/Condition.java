package com.example.dave.gameEngine.ai.fsm;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public interface Condition {
	boolean evaluate(Entity This, GameSection gs);
}