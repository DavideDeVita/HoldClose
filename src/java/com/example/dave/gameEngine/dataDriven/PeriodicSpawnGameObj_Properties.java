package com.example.dave.gameEngine.dataDriven;

import androidx.annotation.Nullable;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataStructures.NonConsecutiveRandomExtraction;
import com.example.dave.gameEngine.dataStructures.PeriodicSpawnGameObj;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;

public class PeriodicSpawnGameObj_Properties extends Properties<PeriodicSpawnGameObj_Properties> implements Buildable<PeriodicSpawnGameObj> {
	public int cooldownMillisec;
	public Integer maximumDelay;
	public GameObj_Properties[] gObjOptions;
	public int excludeLast=0;
	public Box spawnZone=null;
	//
	private static final Pool<PeriodicSpawnGameObj_Properties> spawnPool = new Pool<>(
			new Pool.PoolObjectFactory<PeriodicSpawnGameObj_Properties>() {
				@Override
				public PeriodicSpawnGameObj_Properties createObject() {
					return new PeriodicSpawnGameObj_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.Spawn_Properties_Pool_size)
	);

	private PeriodicSpawnGameObj_Properties(){
		super(spawnPool);
	}

	public static PeriodicSpawnGameObj_Properties _new() {
		return spawnPool.newObject();
	}

	@Override
	public void free() {
		if(gObjOptions!=null)
			for(GameObj_Properties gObj : gObjOptions)
				gObj.free();
		super.free();
	}

	@Override
	public void reset() {
		cooldownMillisec =0;
		maximumDelay =null;
		gObjOptions=null;
		excludeLast=0;
		spawnZone=null;
	}

	@Override
	public PeriodicSpawnGameObj_Properties clone() {
		PeriodicSpawnGameObj_Properties newInstance = _new();
		newInstance.cooldownMillisec = cooldownMillisec;
		newInstance.maximumDelay = maximumDelay;
		newInstance.gObjOptions=gObjOptions.clone();
		newInstance.excludeLast = excludeLast;
		newInstance.spawnZone=spawnZone;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (cooldownMillisec>0L) &&
				(maximumDelay ==null || maximumDelay >0) &&
				(gObjOptions!=null && gObjOptions.length>=2) &&
				excludeLast>=0 &&
				spawnZone!=null;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="PeriodicSpawnGameObj_Properties not ready:";
			if( cooldownMillisec<=0 ) msg+="\ncooldown ("+cooldownMillisec+") must be > 0";
			if( maximumDelay !=null && maximumDelay <=0 ) msg+="\nrandomMaxDelay is less than zero";
			if( gObjOptions==null ) msg+="\ngObjOptions is null";
			else if( gObjOptions.length<2 ) msg+="\nless than 2 gObjOptions";
			if( excludeLast <0 ) msg+="\nexcluding last \"n\" called options, but \"n\" is < 0: "+excludeLast;
			if( spawnZone==null ) msg+="\nthe spawnZone is null";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public PeriodicSpawnGameObj build(@Nullable Object caller) {
		RandomExtractor<GameObj_Properties> extractor;
		if(excludeLast==0)
			extractor = new RandomExtractor<>(gObjOptions);
		else
			extractor = new NonConsecutiveRandomExtraction<>(gObjOptions, excludeLast);
		final PeriodicSpawnGameObj ret = PeriodicSpawnGameObj._new();
		ret.set(cooldownMillisec, maximumDelay, extractor, spawnZone);
		return ret;
	}
}
