package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.Health_Cmpnt;

import static com.example.dave.gameEngine.FloatValue._0f;

public class Health_Properties extends Properties<Health_Properties> {
	public int healPriority=-1;
	public float maxHealth=0;
	public FloatValue startHealth=null;
	public FloatValue proportionalHealthDmg =_0f, constDmg =_0f;
	public boolean damageable=false;
	public GameElement element;
	public Health_Cmpnt.DyingBehaviour dyingBehaviour = Health_Cmpnt.DyingBehaviour.Die;
	//
	private static final Pool<Health_Properties> healthPool = new Pool<>(
			new Pool.PoolObjectFactory<Health_Properties>() {
				@Override
				public Health_Properties createObject() {
					return new Health_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.health_Properties_Pool_size)
	);

	private Health_Properties(){
		super(healthPool);
	}

	public static Health_Properties _new() {
		return healthPool.newObject();
	}

	@Override
	public void reset() {
		this.element = null;
		healPriority=-1;
		maxHealth=0;
		startHealth=null;
		proportionalHealthDmg =_0f; constDmg =_0f;
		damageable=false;
		dyingBehaviour=Health_Cmpnt.DyingBehaviour.Die;
	}

	@Override
	public Health_Properties clone() {
		Health_Properties newInstance = _new();
		newInstance.healPriority=healPriority;
		newInstance.maxHealth=maxHealth;
		newInstance.startHealth=startHealth;
		newInstance.proportionalHealthDmg = proportionalHealthDmg;
		newInstance.constDmg = constDmg;
		newInstance.damageable=damageable;
		newInstance.element = element;
		newInstance.dyingBehaviour = dyingBehaviour;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return maxHealth>0 &&
				element!=null;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Health_Properties not ready:";
			if(maxHealth<=0) msg+="\n\tmaxHealth<=0";
			return new PropertyException(msg);
		}
		return null;
	}
}
