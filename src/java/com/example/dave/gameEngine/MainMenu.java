package com.example.dave.gameEngine;

import android.view.View;
import android.widget.Button;

import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.example.dave.gameEngine.dataStructures.Indexed;
import com.example.dave.gameEngine.dataStructures.MySparseArray;
import com.example.dave.gameEngine.myMultimedia.MyMusic;
import com.example.dave.gameEngine.myMultimedia.Playable;

public class MainMenu {
	private final MainActivity main;
	static MainMenu instance=null;
	 Content current=null;

	 static int count=1; //0 is reserved for menu
	private static final MySparseArray<Content> contents = new MySparseArray<>(5);
	private static final int DEFAULT_CONTENT = 0;

	private MainMenu(MainActivity ma){
		this.main=ma;
		instance=this;
	}

	static MainMenu getInstance(MainActivity ma){
		if(instance==null)
			instance = new MainMenu(ma);
		return instance;
	}

	void createMenuContent(Playable playable, int idLayout, MenuEntry[] behavior){
		Content c = new MenuContent(0, "menu", playable, idLayout, behavior);
		contents.put(c);
	}

	int createLvlContent(Playable playable, String lvlName){
		Content c = new LevelContent(count++, playable, lvlName);

		contents.put(c);
		return c.getIndex();
	}

	int createCreditsContent(Playable playable, String lvlName){
		Content c = new CreditsContent(count++, playable, lvlName);

		contents.put(c);
		return c.getIndex();
	}

	void changeContent(){
		if(current!=null)
			current.onEnd();
		current = contents.get(DEFAULT_CONTENT);
		if(_Log.LOG_ACTIVE){
			_Log.i("changing to "+current);}
		current.onStart();
	}

	void changeContent(int id){
		if(current!=null)
			current.onEnd();
		current = contents.get(id);
		if(_Log.LOG_ACTIVE){
			_Log.i("changing to "+current);}
		current.onStart();
	}

	void resume(){
		if(current!=null){
			current.onResume();
		}
	}

	void pause(){
		if(current!=null)
			current.onPause();
	}

	void stop(){
		count=1;
		contents.clear();
	}

	/**Content*/
	abstract class Content implements Indexed {
		private final Playable playable;
		private final int id;
		protected final String name;

		protected Content(int id, String name, Playable playable){
			this.id=id;
			this.name=name;
			this.playable=playable;
		}

		public void onStart(){
			onResume();
		}

		public void onResume(){
			playable.play(0.75f);
		}

		public void onPause(){
			playable.pause();
		}

		public void onEnd(){
			//playable.stop();
		}

		@Override public int getIndex(){return id;}

		@Override public String toString(){return name;}

		public void stopMusic(){
			playable.pause();
		}

		protected Playable getPlayable() {
			return playable;
		}
	}

	class LevelContent extends Content{
		private GameLevel gl;
		private AndroidFastRenderView renderView;
		private MultiTouchHandler touch;

		protected LevelContent(int id, Playable playable, String levelName){
			super(id, levelName, playable);
		}

		@Override
		public void onStart() {
			gl = Level_Builder.loadLevel(this.name);
			renderView = new AndroidFastRenderView(main, gl);
			touch = new MultiTouchHandler(renderView, 1, 1);
			// Setter needed due to cyclic dependency
			gl.setTouchHandler(touch);
			main.setContentView(renderView);
			gl.start();
			super.onStart();
		}

		@Override
		public void onResume(){
			super.onResume();
			renderView.resume(); // starts game loop in a separate thread
		}

		@Override
		public void onPause(){
			super.onPause();
			renderView.pause(); // stops the main loop
		}
	}

	class CreditsContent extends Content{
		private Credits credits;
		private AndroidFastRenderView renderView;

		protected CreditsContent(int id, Playable playable, String levelName){
			super(id, levelName, playable);
		}

		@Override
		public void onStart() {
			credits = Level_Builder.loadCredits(this.name);
			renderView = new AndroidFastRenderView(main, credits);
			// Setter needed due to cyclic dependency
			main.setContentView(renderView);
			credits.start();
			super.onStart();
		}

		@Override
		public void onResume(){
			super.onResume();
			renderView.resume(); // starts game loop in a separate thread
		}

		@Override
		public void onPause(){
			super.onPause();
			renderView.pause(); // stops the main loop
		}
	}

	class MenuContent extends Content{
		private final int layoutID;
		private final MenuEntry behavior[];

		protected MenuContent(int id, String name, Playable playable, int layoutID, MenuEntry behavior[] ){
			super(id, name, playable);
			this.layoutID=layoutID;
			this.behavior=behavior;
		}

		@Override
		public void onStart() {
			main.setContentView(layoutID);
			boolean lvlUnlocked;
			for(final MenuEntry entry : behavior) {
				Button button = main.findViewById( entry.buttonID );
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(_Log.LOG_ACTIVE){
							_Log.i("Main Menu", "clicked "+entry.content_Index );}
						changeContent(entry.content_Index );
					}
				});

				if( entry.enableIf == null )
					lvlUnlocked=true;
				else
					lvlUnlocked=main.preferences.getBoolean(entry.enableIf, false);
				if(_Log.LOG_ACTIVE){
					_Log.i("EnableButtons", ""+entry.enableIf+" is "+lvlUnlocked);}
				button.setEnabled(lvlUnlocked);
				button.setAlpha(0.33f +(lvlUnlocked ? 0.67f : 0) );
			}
			super.onStart();
		}

		@Override
		public void onResume(){
			super.onResume();
			if(_Log.LOG_ACTIVE){
				_Log.i("onWin", "Menu Resume");}
		}

		@Override
		public void onPause(){
			super.onPause();
		}
	}

	static class MenuEntry{
		public int buttonID;
		public int content_Index;
		public String enableIf; //Null means true
	}
}
