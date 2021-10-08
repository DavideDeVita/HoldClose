package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.RandomExtractor_Properties;

public class Summoner_Properties extends Properties<Summoner_Properties> {
	public RandomExtractor_Properties<GameObj_Properties> extractor;
	//
	private static final Pool<Summoner_Properties> summonerPool = new Pool<>(
			new Pool.PoolObjectFactory<Summoner_Properties>() {
				@Override
				public Summoner_Properties createObject() {
					return new Summoner_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.summoner_Properties_Pool_size)
	);

	private Summoner_Properties(){
		super(summonerPool);
	}

	public static Summoner_Properties _new() {
		return summonerPool.newObject();
	}

	@Override
	public void reset() {
		extractor=null;
	}

	@Override
	public Summoner_Properties clone() {
		Summoner_Properties newIstance=_new();
		if(extractor!=null)
			newIstance.extractor = extractor.clone();
		return newIstance;
	}

	@Override
	public boolean isReady() {
		return extractor!=null && extractor.isReady();
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=" Summon_Properties not ready:";
			if(extractor==null)     msg+="\n\tExtractor is null";
			else if(!extractor.isReady())     msg+="\n\t"+extractor.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}
}
