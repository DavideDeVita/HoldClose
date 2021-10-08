package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataStructures.PooleOriented;

public abstract class Properties<T extends Properties<? super T>> implements PooleOriented<T>, Cloneable {
	private final Pool<T> sovereign;

	protected Properties(Pool<T> staticPool) {
		this.sovereign = staticPool;
	}

	public T getFromPoole(){
		return sovereign.newObject();
	}

	public void sendToPoole(){
		sovereign.free((T)this);
	}

	@Override
	public void free(){
		if(_Log.LOG_ACTIVE)
			_Log.d("PropertiesPoolEfficacy", getClass().getSimpleName()+" free() called");
		this.reset();
		if(sovereign!=null)
			sendToPoole();
	}

	public abstract void reset();

	public abstract T clone();

	public abstract boolean isReady();

	public abstract PropertyException getErrors();
}
