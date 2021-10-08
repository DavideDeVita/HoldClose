package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public class Resizeable_Properties extends Properties<Resizeable_Properties> {
	public float minDimX, minDimY;
	public float maxDimX, maxDimY;
	//
	private static final Pool<Resizeable_Properties> resizeablePool = new Pool<>(
			new Pool.PoolObjectFactory<Resizeable_Properties>() {
				@Override
				public Resizeable_Properties createObject() {
					return new Resizeable_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.resizeable_Properties_Pool_size)
	);

	private Resizeable_Properties(){
		super(resizeablePool);
	}

	public static Resizeable_Properties _new() {
		return resizeablePool.newObject();
	}

	@Override
	public void reset() {
		minDimX = minDimY= maxDimX = maxDimY= 0f;
	}

	@Override
	public Resizeable_Properties clone() {
		Resizeable_Properties newInstance = _new();
		newInstance.minDimX = minDimX;
		newInstance.minDimY = minDimY;
		newInstance.maxDimX = maxDimX;
		newInstance.maxDimY = maxDimY;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return minDimX>0 && minDimY>0 && maxDimX>minDimX && maxDimY>minDimY;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Resizeable_Properties not ready:";
			if(minDimX<0) msg+="\n\tminDimX<0";
			if(minDimY<0) msg+="\n\tminDimY<0";
			if(maxDimX<=maxDimX) msg+="\n\tminxDimX<=maxDimX";
			if(maxDimY<=minDimY) msg+="\n\tminDimY<=minDimY";
			return new PropertyException(msg);
		}
		return null;
	}
}
