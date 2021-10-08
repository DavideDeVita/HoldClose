package com.example.dave.gameEngine.myMultimedia;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Sound;

public enum MySound implements Playable{
	burningLeaves, dumb, evaporatingWater, fireGrow,
		metallic, waterDrop, tickTickTick;

	private Sound sound;
	private long timeOfLastPlay = 0, currentTime;
	private long PLAY_COOLDOWN = 2_000_000_000L;

	private void setSound(Sound sound){
		this.sound=sound;
	}

	private void setSound(Sound sound, long cooldown){
		this.sound=sound;
		this.PLAY_COOLDOWN = cooldown;
	}

	@Override
	public void play(float volume){
		currentTime = System.nanoTime();
		if( currentTime-timeOfLastPlay > PLAY_COOLDOWN) {
			timeOfLastPlay=currentTime;
			sound.play(volume);
		}
	}

	@Override public void pause(){}

	@Override
	public void stop() {}

	@Override
	public boolean isPlaying() {
		return false;//Unimplemented
	}

	public static void init(Audio audio){
		burningLeaves.setSound(audio.newSound("sounds/burning_leaves.wav"), 1_000_000_000L * 5L);
		dumb.setSound(audio.newSound("sounds/dumb.wav"));
		evaporatingWater.setSound(audio.newSound("sounds/evaporating_water.mp3"));
		fireGrow.setSound(audio.newSound("sounds/fire_grow.mp3"));
		metallic.setSound(audio.newSound("sounds/metallic.wav"));
		waterDrop.setSound(audio.newSound("sounds/waterDrop.wav"));

		tickTickTick.setSound(audio.newSound("sounds/tickTickTick.mp3"));
	}
}
