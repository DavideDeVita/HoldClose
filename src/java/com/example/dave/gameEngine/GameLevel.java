package com.example.dave.gameEngine;

import android.content.SharedPreferences;

import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.example.dave.gameEngine.dataDriven.Level_FullLoaded_Properties;
import com.example.dave.gameEngine.dataDriven.Level_Properties;
import com.example.dave.gameEngine.dataDriven.Level_onDemand_Properties;
import com.example.dave.gameEngine.myMultimedia.Playlist;

import static com.example.dave.gameEngine.MainActivity.screenSignalPixel;
import static com.example.dave.gameEngine.GamePhase.*;

/**
 * Old Game World.
 * Is now the Section of a Game Level
 */
public class GameLevel extends GameContent{
	// Rendering
	public final int lvlScreenWidth, lvlScreenHeight;    // pixels of the window dedicated to the game
	final float PIXEL_RATIO; //width/height

	// Simulation
	private GameSection section;
	private int curr=0;
	private final Level_Properties level_p;
	private StoryNarration story;

	// Sub Sys
	MyContactListener contactListener;
	TouchHandler touch;
	//private MainMenu.LevelContent lvlContent;
	//private Playable bgMusic;

	// Arguments are in physical simulation units.
	public GameLevel(Level_Properties lvl) {
		lvlScreenWidth = MainActivity.screenWidth-(2*screenSignalPixel);
		lvlScreenHeight = MainActivity.screenHeight-(2*screenSignalPixel);
		PIXEL_RATIO=1f* lvlScreenWidth / lvlScreenHeight;

		this.level_p = lvl;
		// stored to prevent GC
		contactListener = new MyContactListener();
	}

	public GameLevel(Level_onDemand_Properties lvl) {
		lvlScreenWidth = MainActivity.screenWidth-(2*screenSignalPixel);
		lvlScreenHeight = MainActivity.screenHeight-(2*screenSignalPixel);
		PIXEL_RATIO=1f* lvlScreenWidth / lvlScreenHeight;

		this.level_p = lvl;
		// stored to prevent GC
		contactListener = new MyContactListener();
	}

	@Override public final void start(){
		curr=0;
		if(level_p.storyAnte !=null) {
			phase = StoryAnte;
			story = level_p.storyAnte.build(this);
		}
		else
			phase=Play;
		section = Level_Builder.loadSection(this, level_p, curr);
	}

	private final void nextSection(){
		curr++;
		if(!level_p.hasNthSection(curr)) {
			win();
		}
		else {
			float mainHealth = section.mainObject.getHealth().health;
			if(_Log.LOG_ACTIVE)
				_Log.i("NewSection", "Section completed with  "+mainHealth+" hp");
			section.finalize();
			section = Level_Builder.loadSection(this, level_p, curr, mainHealth);
		}
	}

	private final void win() {
		if (_Log.LOG_ACTIVE) {
			_Log.e("YOU WIN");
		}
		section.finalize();
		Playlist.youWin.play(1);
		if(level_p.onWinValues!=null && level_p.onWinKeys!=null) {
			final SharedPreferences.Editor editor = MainActivity.thisActivity.preferences.edit();
			for (int i = 0; i < level_p.onWinValues.length; i++) {
				if (_Log.LOG_ACTIVE) {
					_Log.d("onWin", level_p.onWinKeys[i] + ": " + level_p.onWinValues[i]);
				}
				editor.putBoolean(level_p.onWinKeys[i], level_p.onWinValues[i]);
			}
			editor.commit();
		}
		if(level_p.storyPost !=null) {
			phase = StoryPost;
			story = level_p.storyPost.build(this);
		}
		else
			phase=End;

	}

	public final void lose() {
		if(_Log.LOG_ACTIVE){
			_Log.e("YOU LOSE");}
		Playlist.youLose.play(1);
		if(level_p.onLoseValues!=null && level_p.onLoseKeys!=null) {
			final SharedPreferences.Editor editor = MainActivity.thisActivity.preferences.edit();
			for (int i = 0; i < level_p.onLoseValues.length; i++) {
				if (_Log.LOG_ACTIVE) {
					_Log.d("onLose", level_p.onLoseKeys[i] + ": " + level_p.onLoseValues[i]);
				}
				editor.putBoolean(level_p.onLoseKeys[i], level_p.onLoseValues[i]);
			}
		}
		finalize();
		phase=End;
	}

	@gameLoop @Override
	public final synchronized boolean update(float elapsedTime){
		if(_Log.LOG_ACTIVE){
			_Log.d("GameLevel", "Update Phase is "+phase);}
		switch (phase){
			case Play:
				if (section.update(elapsedTime)) //You win.. move on
					nextSection();
				break;
			case StoryAnte:
				if (story==null || !story.update(elapsedTime))
					phase = Play;
				break;
			case StoryPost:
				if (story==null || !story.update(elapsedTime))
					phase = End;
				break;
			case End:
		}
		return isOn();
	}

	@Override public synchronized final void render(){
		if(_Log.LOG_ACTIVE){
			_Log.d("GameLevel", "Render Phase is "+phase);}
		switch (phase){
			case Play:
				section.render();
				break;
			case StoryAnte:
				story.render();
				break;
			case StoryPost:
				story.render();
				break;
			case End:
		}
	}

	public final synchronized void setTouchHandler(TouchHandler touch){
		this.touch=touch;
	}

	@Override
	public final void finalize(){
		section.finalize();
		level_p.free();
	}
}
