package com.example.dave.gameEngine.dataDriven.ai;

import android.util.ArrayMap;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.ai.AI_engine;
import com.example.dave.gameEngine.dataDriven.PreLoadedProperties;

import java.util.Map;

public abstract class AI_Properties extends PreLoadedProperties<AI_Properties> implements Buildable<AI_engine> {
	private final static Map<String, AI_Properties> stdAI = new ArrayMap<>();

	public AI_Properties(Pool<AI_Properties> aiPool) {
		super(stdAI, aiPool);
	}

	public static boolean standardAlreadyExists(String ai_name) {
		return stdAI.containsKey(ai_name.toLowerCase());
	}

	public static AI_Properties getStandardAI(String ai_name) {
		return stdAI.get(ai_name.toLowerCase()).clone();
	}
}
