package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.dataStructures.Indexed;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.dataStructures.MySparseArray;
import com.example.dave.gameEngine.dataDriven.ai.action_p.Action_Properties;

import java.util.Map;

public class IdentifiedGameObject extends GameObject implements Indexed {
	private static MySparseArray<IdentifiedGameObject> identified = new MySparseArray<>();

	IdentifiedGameObject(GameSection gs, GO_Shape shape, OnHitBehaviour oHB, Map<GameElement, Action_Properties> oHbEB, int explicitId) {
		super(gs, shape, oHB, oHbEB, explicitId);
		if(identified.size() == MainActivity.identifiedGameObjectCacheSize)
			throw new IllegalStateException("identified cache exceeds max size: "+MainActivity.identifiedGameObjectCacheSize);
		identified.put(this);
	}

	public static GameObject getById(int id){
		return identified.get(id);
	}

	@Override
	public int getIndex(){
		return id;
	}

	public void resetIdentifiedCache(){
		identified.clear();
	}
}
