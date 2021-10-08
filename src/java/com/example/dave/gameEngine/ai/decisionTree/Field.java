package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;

public interface Field<T> {
	T fetch(Entity entity, GameSection gs);

	T fetch(Entity entity, GameSection gs, Loggable loggable);
}
