package com.example.dave.gameEngine.myMultimedia;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;

import java.util.Deque;
import java.util.LinkedList;

public enum MyMusic implements Playable{
	holdMeClose, holdMeClose_opening, aPlaceToCallHome, vamoAllaFlamenco, howCouldHaveBeen,
			lightRain, believe, lazy,
			youWin_LazyIntro, youWin_aReason, youWin_LazySigh,
			youLose_scrubs, youLose_madWorld, youLose_people;
	private Music music;
	private boolean isLooping=true;
	private float volume=1F;

	private void setMusic(Music music, boolean loop){
		this.music=music;
		this.music.setLooping(loop);
		this.isLooping=loop;
	}

	private void setMusic(Music music){
		this.music=music;
		this.music.setLooping(true);
		this.isLooping=true;//default
	}

	public void _play(){
		music.setVolume(volume);
		music.play();
	}

	@Override
	public void pause(){
		music.pause();
	}

	@Override
	public void stop() {
		if(isPlaying()) {
			//music.stop();
			notifyMusicEnd(this.music);
		}
	}
	private void _stop() {
		if(isPlaying()) {
			music.stop();
		}
	}

	@Override
	public boolean isPlaying() {
		return music.isPlaying();
	}


	public static void init(Audio audio){
		holdMeClose.setMusic(audio.newMusic("musics/holdMeClose.mp3"));
		holdMeClose_opening.setMusic(audio.newMusic("musics/opening_HoldMeClose.mp3"));
		vamoAllaFlamenco.setMusic(audio.newMusic("musics/vamoAllaFlamenco_IX.mp3"));
		aPlaceToCallHome.setMusic(audio.newMusic("musics/aPlaceToCallHome_IX.mp3"));
		howCouldHaveBeen.setMusic(audio.newMusic("musics/howCouldHaveBeen.mp3"));
		lazy.setMusic(audio.newMusic("musics/LazyAfternoons.mp3"));
		believe.setMusic(audio.newMusic("musics/believe.mp3"));

		lightRain.setMusic(audio.newMusic("musics/light_rain.wav"));

		youWin_LazyIntro.setMusic(audio.newMusic("musics/youWin_lazy_intro.mp3"), false);
		youWin_aReason.setMusic(audio.newMusic("musics/youWin_aReason.mp3"), false);
		youWin_LazySigh.setMusic(audio.newMusic("musics/youWin_Lazy_sigh.mp3"), false);
		youLose_scrubs.setMusic(audio.newMusic("musics/youLose_scrubs.mp3"), false);
		youLose_people.setMusic(audio.newMusic("musics/ifIHadABrain.mp3"), false);
		youLose_madWorld.setMusic(audio.newMusic("musics/lookRightTroughMe.mp3"), false);
	}

	//Deque to handle music
	private static Deque<MyMusic> queue=new LinkedList<>();
	/**The idea is..
	 * Only one Looping element at a time in the queue and always at the end
	 * If the last element is Looping, on a new insert, remove it
	 * Only When last left play it.. interrupt it */

	@Override
	public void play(float volume){
		this.volume=volume;
		synchronized (queue) {
			if (queue.isEmpty())
				enqueueMusic(this);
			else if (queue.size() == 1) {
				//If head is looping
				if (queue.peekFirst().isLooping) {
					nextMusic(false);
					enqueueMusic(this);
				} else
					enqueueMusic(this);
			} else {
				if (queue.peekLast().isLooping) {
					queue.pollLast();
					enqueueMusic(this);
				} else
					enqueueMusic(this);
			}
		}
	}

	private static void nextMusic(boolean alreadyEnded) {
		if(alreadyEnded)
			queue.pollFirst();
		else
			queue.pollFirst()._stop();

		if(!queue.isEmpty())
			queue.peekFirst()._play();
	}

	private static void enqueueMusic(MyMusic music){
		queue.add(music);
		if(queue.size()==1)
			music._play();
	}

	public static void notifyMusicEnd(Music music){
		//Should check if First is argMusic..
		synchronized (queue) {
			nextMusic(true);
		}
	}

	public static void eventualPause() {
		MyMusic head = queue.peekFirst();
		if(head!=null && !head.isLooping){
			head.pause();
		}
	}

	public static void eventualResume() {
		MyMusic head = queue.peekFirst();
		if(head!=null && !head.isLooping){
			head._play();
		}
	}
}