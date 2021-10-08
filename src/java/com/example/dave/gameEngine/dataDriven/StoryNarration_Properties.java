package com.example.dave.gameEngine.dataDriven;

import androidx.annotation.Nullable;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.GameContent;
import com.example.dave.gameEngine.GameLevel;
import com.example.dave.gameEngine.GraphicSubSys;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.StoryNarration;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;
import com.example.dave.gameEngine.myMultimedia.MyBackgrounds;

public class StoryNarration_Properties extends Properties<StoryNarration_Properties> implements Buildable<StoryNarration> {
	public MyBackgrounds[] images;
	public float requiredTime; //in seconds. (for each image)
	public boolean topDown=true;
	//
	private static final Pool<StoryNarration_Properties> storyNarrationPool = new Pool<>(
			new Pool.PoolObjectFactory<StoryNarration_Properties>() {
				@Override
				public StoryNarration_Properties createObject() {
					return new StoryNarration_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.StoryNarration_Properties_Pool_size)
	);

	private StoryNarration_Properties(){
		super(storyNarrationPool);
	}

	public static StoryNarration_Properties _new() {
		return storyNarrationPool.newObject();
	}

	@Override
	public void reset() {
		images=null;
		requiredTime=0F;
		topDown=true;
	}

	@Override
	public StoryNarration_Properties clone() {
		StoryNarration_Properties newInstance = _new();
		newInstance.images=images.clone();
		newInstance.requiredTime=requiredTime;
		newInstance.topDown=topDown;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (images!=null && images.length>0) &&
				requiredTime>=1F
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg=" StoryNarration_Properties not ready:";
			if(images==null)              msg+="\nimages is null";
			else if(images.length==0)     msg+="\nimages is empty";
			if(requiredTime<1F)           msg+="\n requiredTime is less than 1 second ("+requiredTime+")";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public StoryNarration build(@Nullable Object caller) {
		return new StoryNarration(GraphicSubSys.screenWidth, GraphicSubSys.screenHeight, images, requiredTime, topDown);
	}
}
