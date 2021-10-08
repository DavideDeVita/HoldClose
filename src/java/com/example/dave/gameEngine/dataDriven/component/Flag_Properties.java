package com.example.dave.gameEngine.dataDriven.component;

import android.util.ArrayMap;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataStructures.MapBuilder;
import com.example.dave.gameEngine.dataStructures.MapSet;
import com.example.dave.gameEngine.dataStructures.SetBuilder;
import com.example.dave.gameEngine.entity_component.ComponentType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Flag_Properties extends Properties<Flag_Properties> {
	public Map<String, Object> flags=new ArrayMap<>();
	public MapSet<String, ComponentType> observers = null;
	//
	private static final Pool<Flag_Properties> flagPool = new Pool<>(
			new Pool.PoolObjectFactory<Flag_Properties>() {
				@Override
				public Flag_Properties createObject() {
					return new Flag_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.flag_Properties_Pool_size)
	);

	private Flag_Properties(){
		super(flagPool);
	}

	public static Flag_Properties _new() {
		return flagPool.newObject();
	}

	//Add "or clear"
	public void buildObservers(){
		observers = new MapSet<>(
				new SetBuilder<ComponentType>() {
					@Override
					public Set<ComponentType> buildSet() {
						return EnumSet.noneOf(ComponentType.class);
					}
				},
				new MapBuilder<String, Set<ComponentType>>() {
					@Override
					public Map<String, Set<ComponentType>> buildMap() {
						return new ArrayMap<>();
					}
				}
		);
	}

	@Override
	public void reset() {
		flags = new ArrayMap<>();
		if (observers != null){
			observers.clear();
			observers = null;
		}
		observers=null;
	}

	@Override
	public Flag_Properties clone() {
		Flag_Properties newIstance=_new();
		newIstance.flags = new ArrayMap<>();
		copyMapInto(flags, newIstance.flags);
		if(observers!=null)
		newIstance.observers = observers.clone();
		return newIstance;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=" Flags_property not ready:";
			return new PropertyException(msg);
		}
		return null;
	}

	public void putFlag(String name, Object flag) {
		flags.put(name, flag);
	}

	public void putObserver(String name, ComponentType cmpnt) {
		if(observers==null)
			buildObservers();
		observers.put(name, cmpnt);
	}

	public <X>void copyMapInto(Map<String, X> toCopy, Map<String, X> copyInto) {
		for (Map.Entry<String, X> entry : toCopy.entrySet())
			copyInto.put(entry.getKey(), entry.getValue());
	}
}
