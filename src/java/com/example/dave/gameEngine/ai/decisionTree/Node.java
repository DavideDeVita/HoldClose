package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;

public interface Node{
	Action evaluate(final Entity entity, final GameSection gs, DecisionTree tree, String pathLog);
}