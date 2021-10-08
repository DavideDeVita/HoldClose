package com.example.dave.gameEngine.myMultimedia;

public interface Playable {
	void play(float volume);

	void pause();

	void stop();

	boolean isPlaying();
}
