package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.GameObject;

import java.util.List;

public interface AI_engine extends Loggable{
	List<Action> exec(Entity entity, GameSection gs);

	void log();
}
