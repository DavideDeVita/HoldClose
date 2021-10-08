package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;

public class Level_FullLoaded_Properties extends Level_Properties {
	public Section_Properties[] sec_p = null;
	public StoryNarration_Properties storyAnte =null, storyPost = null;
	public String[] onWinKeys;
	public boolean[] onWinValues;
	//
	private static final Pool<Level_Properties> lvlPool = new Pool<>(
			new Pool.PoolObjectFactory<Level_Properties>() {
				@Override
				public Level_FullLoaded_Properties createObject() {
					return new Level_FullLoaded_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.Lvl_Properties_Pool_size)
	);

	private Level_FullLoaded_Properties(){
		super(lvlPool);
	}

	@Override
	public void free() {
		if(storyAnte!=null)     storyAnte.free();
		if(storyPost!=null)     storyPost.free();
		//Sections freed in Level Builder
		super.free();
	}

	public static Level_FullLoaded_Properties _new() {
		return (Level_FullLoaded_Properties)lvlPool.newObject();
	}

	@Override
	public void reset() {
		super.reset();
		sec_p = null;
	}

	@Override
	public Level_FullLoaded_Properties clone() {
		Level_FullLoaded_Properties lvl = _new();
		copyInto(lvl);
		lvl.sec_p = sec_p.clone();
		return lvl;
	}

	@Override
	public boolean isReady() {
		return (sec_p!=null && sec_p.length > 0) &&
				(super.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if (!isReady()) {
			String msg = "Lvl_properties not ready:";
			if (sec_p==null ) msg += "\nsections is null";
			else if (sec_p.length<=0 ) msg += "\nNo sections";
			if(!super.isReady())    msg+="\n"+super.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public boolean hasNthSection(int n) {
		return 0 <= n && n < sec_p.length;
	}
}
