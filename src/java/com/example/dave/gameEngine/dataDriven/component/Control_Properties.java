package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;

import static com.example.dave.gameEngine.FloatValue._1f;

public class Control_Properties extends Properties<Control_Properties> {
	public int cooldown, holdTimer;
	public FloatValue penalty= _1f;
	//
	private static final Pool<Control_Properties> controlPool = new Pool<>(
			new Pool.PoolObjectFactory<Control_Properties>() {
				@Override
				public Control_Properties createObject() {
					return new Control_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.control_Properties_Pool_size)
	);

	private Control_Properties(){
		super(controlPool);
	}

	public static Control_Properties _new() {
		return controlPool.newObject();
	}

	@Override
	public void reset() {
		cooldown=0;
		holdTimer=2_000_000_000;
		penalty = _1f ;
	}

	@Override
	public Control_Properties clone() {
		Control_Properties newInstance = _new();
		newInstance.cooldown=cooldown;
		newInstance.holdTimer=holdTimer;
		newInstance.penalty=penalty;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return cooldown>0 &&
				holdTimer>0 &&
				penalty.greater(0f);
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Control_Properties not ready:";
			if(cooldown<=0) msg+="cooldown<=0 :"+cooldown;
			if(holdTimer<=0) msg+="holdTimer<=0 :"+holdTimer;
			if(penalty.leq(0f) ) msg+="penalty<=0 :"+penalty;
			return new PropertyException(msg);
		}
		return null;
	}
}
