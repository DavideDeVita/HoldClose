package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

public interface Action {
	/**returns true if the action was completed
	 * see @A_Cooldown or @A_Chance*/
	boolean act(Entity entity, GameSection gs);

	/**returns true if the action was completed
	 * see @A_Cooldown or @A_Chance*/
	boolean act(Entity entity, GameSection gs, Loggable loggable);
}
