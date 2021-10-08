package com.example.dave.gameEngine;

import android.content.SharedPreferences;

import com.example.dave.gameEngine.dataDriven.parser.Credits_Properties;

import static com.example.dave.gameEngine.GamePhase.*;

class Credits extends GameContent{
	private final Credits_Properties credits_p;
	private StoryNarration story;

	public Credits(Credits_Properties credits_p) {
		this.credits_p = credits_p;
	}

	@Override public void start(){
		phase = StoryAnte;
		if(credits_p.storyAnte !=null) {
			story = credits_p.storyAnte.build(this);
		}
		else
			nextPhase();
	}

	private void nextPhase(){
		switch (phase){
			case StoryAnte:
				phase= Credits;
				startCreditsPhase();
				break;
			case Credits:
				if(credits_p.storyPost !=null) {
					phase = StoryPost;
					story = credits_p.storyPost.build(this);
				}
				else
					phase=End;
				break;
			case StoryPost:
				final SharedPreferences.Editor editor = MainActivity.thisActivity.preferences.edit();
				_Log.i("onEnd", credits_p.onEndValues.length+"");
				for(int i = 0; i< credits_p.onEndValues.length; i++){
					if(_Log.LOG_ACTIVE){
						_Log.i("onEnd", credits_p.onEndKeys[i]+": "+ credits_p.onEndValues[i]);}
					editor.putBoolean(credits_p.onEndKeys[i], credits_p.onEndValues[i]);
				}
				editor.commit();
				phase = End;
				break;
			case End:
		}
	}

	private void startCreditsPhase() {
		if (credits_p.triggerEE==null)
			story = credits_p.credits.build(this);
		else{
			final SharedPreferences preferences = MainActivity.thisActivity.preferences;
			boolean ee=true, pref;
			for(int i = 0; i< credits_p.triggerEE.length; i++){
				pref = preferences.getBoolean(credits_p.triggerEE[i], false);
				_Log.w("Credits", credits_p.triggerEE[i]+": "+pref);
				if(!pref)
					ee=false;
					break;
			}

			if(ee)
				story = credits_p.easterEggCredits.build(this);
			else
				story = credits_p.credits.build(this);
		}
	}

	@gameLoop
	public synchronized boolean update(float elapsedTime){
		if(_Log.LOG_ACTIVE){
			_Log.d("Credits", "Update Phase is "+phase);}
		switch (phase){
			case StoryAnte:
				if (story==null || !story.update(elapsedTime))
					nextPhase();
				break;
			case Credits:
				if (story==null || !story.update(elapsedTime))
					nextPhase();
				break;
			case StoryPost:
				if (story==null || !story.update(elapsedTime))
					nextPhase();
				break;
			case End:
				break;
		}
		return isOn();
	}

	@Override public synchronized void render(){
		if(_Log.LOG_ACTIVE){
			_Log.d("GameLevel", "Render Phase is "+phase);}
		switch (phase){
			case StoryAnte:
				story.render();
				break;
			case Credits:
				story.render();
				break;
			case StoryPost:
				story.render();
				break;
			case End:
		}
	}

	@Override
	public void finalize(){
		credits_p.free();
	}
}
