package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;

public abstract class Level_Properties extends Properties<Level_Properties> {
	public StoryNarration_Properties storyAnte =null, storyPost = null;
	public String[] onWinKeys, onLoseKeys;
	public boolean[] onWinValues, onLoseValues;


	protected Level_Properties(Pool<Level_Properties> staticPool) {
		super(staticPool);
	}

	@Override
	public void free() {
		if(storyAnte!=null)     storyAnte.free();
		if(storyPost!=null)     storyPost.free();
		//Sections freed in Level Builder
		super.free();
	}

	@Override
	public void reset() {
		storyAnte = null;
		storyPost = null;
		onWinKeys=null;
		onWinValues=null;
		onLoseKeys=null;
		onLoseValues=null;
	}

	public Level_Properties copyInto(Level_Properties into) {
		into.storyAnte = storyAnte.clone();
		into.storyPost = storyPost.clone();
		if(onWinKeys!=null)
			into.onWinKeys = onWinKeys.clone();
		if(onWinValues!=null)
			into.onWinValues = onWinValues.clone();
		if(onLoseKeys!=null)
			into.onLoseKeys = onLoseKeys.clone();
		if(onLoseValues!=null)
			into.onLoseValues = onLoseValues.clone();
		return into;
	}

	@Override
	public boolean isReady() {
		return (storyAnte ==null || storyAnte.isReady()) &&
				(storyPost ==null || storyPost.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if (!isReady()) {
			String msg = "Lvl_properties not ready:";
			if (storyAnte !=null && !storyAnte.isReady() ) msg += "\nPrologue: "+ storyAnte.getErrors().getMessage();
			if (storyPost !=null && !storyPost.isReady() ) msg += "\nEpilogue: "+ storyPost.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	//
	public abstract boolean hasNthSection(int n);
}
