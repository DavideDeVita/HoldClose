package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;

import java.util.Map;

public abstract class PreLoadedProperties<T extends PreLoadedProperties<T>> extends Properties<T> {
	public String name=null;
	private final Map<String, T> dataDriven;

	protected PreLoadedProperties(Map<String, T> preloadedMap, Pool<T> commonPool) {
		//All class will have one static and will pass to constructors.
		super(commonPool);
		dataDriven = preloadedMap;
	}

	public final T getDataDrivenStandard(String name){
		T std = dataDriven.get(name.toLowerCase());
		if(std!=null)
			return std.clone();
		else
			return null;
	}
	
	public final boolean existsDataDrivenStandard(String name){ return dataDriven.containsKey(name.toLowerCase()); }

	public final void addDataDrivenStandard(){
		final T addMe = this.clone();
		if(!existsDataDrivenStandard(name.toLowerCase()))
			dataDriven.put(name.toLowerCase(), addMe);
	}

	public final int count(){
		return dataDriven.size();
	}

	public void reset(){
		name=null;
	}

	/*Not necessary*/
	public boolean isReady(){
		return name!=null;
	}

	public abstract void fixIntraPropertiesConstraint();

	/*Every subclass should have the following static:
		protected final Map<String, T> dataDriven;


	* */
}