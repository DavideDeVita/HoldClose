package com.example.dave.gameEngine.dataDriven.parser;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.StoryNarration_Properties;

public class Credits_Properties extends Properties<Credits_Properties> {
	public StoryNarration_Properties storyAnte =null, storyPost = null;
	public StoryNarration_Properties credits=null, easterEggCredits =null;
	public String[] onEndKeys, triggerEE; //default: they have to be true
	public boolean[] onEndValues;

	//
	private static final Pool<Credits_Properties> credisPool = new Pool<>(
			new Pool.PoolObjectFactory<Credits_Properties>() {
				@Override
				public Credits_Properties createObject() {
					return new Credits_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.credits_Properties_Pool_size)
	);

	private Credits_Properties(){
		super(credisPool);
	}

	@Override
	public void free() {
		if(storyAnte!=null)     storyAnte.free();
		if(credits!=null)       credits.free();
		if(easterEggCredits !=null)       easterEggCredits.free();
		if(storyPost!=null)     storyPost.free();
		//Sections freed in Level Builder
		super.free();
	}

	public static Credits_Properties _new() {
		return credisPool.newObject();
	}

	@Override
	public void reset() {
		storyAnte = null;
		credits = null;
		easterEggCredits = null;
		storyPost = null;
		onEndKeys =null;
		onEndValues=null;
		triggerEE=null;
	}

	@Override
	public Credits_Properties clone() {
		Credits_Properties newInstance = _new();
		newInstance.storyAnte = storyAnte.clone();
		newInstance.credits = credits.clone();
		newInstance.easterEggCredits = easterEggCredits.clone();
		newInstance.storyPost = storyPost.clone();
		if(onEndKeys !=null)
			newInstance.onEndKeys = onEndKeys.clone();
		if(onEndValues!=null)
			newInstance.onEndValues = onEndValues.clone();
		if(triggerEE!=null)
			newInstance.triggerEE = triggerEE.clone();
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return  (storyAnte ==null || storyAnte.isReady()) &&
				(credits !=null && credits.isReady()) &&
				(	easterEggCredits ==null ||
						( easterEggCredits.isReady() && triggerEE!=null && triggerEE.length>0)    ) &&
				(storyPost ==null || storyPost.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if (!isReady()) {
			String msg = "credits_properties not ready:";
			if (storyAnte !=null && !storyAnte.isReady() ) msg += "\nPrologue: "+ storyAnte.getErrors().getMessage();
			if (credits ==null || !credits.isReady() ) msg += "\nCredits: "+ credits.getErrors().getMessage();
			if (easterEggCredits !=null && !easterEggCredits.isReady() ) msg += "\nEasterEggsCredits: "+ easterEggCredits.getErrors().getMessage();
			if (easterEggCredits !=null && triggerEE==null ) msg += "\nEasterEggsCredits: trigger condition is null";
			else if (easterEggCredits !=null && triggerEE.length==0 ) msg += "\nEasterEggsCredits: trigger condition is empty";
			if (storyPost !=null && !storyPost.isReady() ) msg += "\nEpilogue: "+ storyPost.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}
}