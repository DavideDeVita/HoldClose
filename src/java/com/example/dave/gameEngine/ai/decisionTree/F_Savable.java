package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.entity_component.Entity;

abstract class F_Savable<T> implements Field<T> {
	private final String name;

	protected F_Savable(){
		this.name = null;
	}

	protected F_Savable(String name){
		this.name = name;
	}

	protected final void saveValue(Entity entity, T value) {
		if(name!=null)
			entity.getFlags().setFlag(name, value);
	}

	/**To save other values as well*/
	protected final void saveValue(Entity entity, String name, Object value){
		if(name!=null)
			entity.getFlags().setFlag(name, value);
	}
}
