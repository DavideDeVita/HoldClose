package com.example.dave.gameEngine.myMultimedia;

import com.example.dave.gameEngine.dataStructures.RandomExtractor;

public class Playlist extends RandomExtractor<Playable> implements Playable {
	private Playable last=null;

	public Playlist(final Playable[] playlist){
		super(playlist.clone());
	}

	@Override
	public void play(float volume) {
		last = extract();
		last.play(volume);
	}

	@Override
	public void pause() {
		//Random should be the same
		if(last!=null)
			last.pause();
	}

	@Override
	public void stop() {
		if(last!=null){
			last.stop();
		}
	}

	@Override
	public boolean isPlaying() {
		return last!=null && last.isPlaying();
	}

	/**Standard Playlists*/
	public final static Playlist youWin = new Playlist(new Playable[] {MyMusic.youWin_LazyIntro, MyMusic.youWin_aReason, MyMusic.youWin_LazySigh});
	public final static Playlist youLose = new Playlist(new Playable[] {MyMusic.youLose_scrubs, MyMusic.youLose_madWorld, MyMusic.youLose_people});
	public final static Playlist menuMusic = new Playlist(new Playable[] { MyMusic.howCouldHaveBeen, MyMusic.believe,
			MyMusic.holdMeClose, MyMusic.holdMeClose_opening, MyMusic.vamoAllaFlamenco, MyMusic.aPlaceToCallHome,});
}
