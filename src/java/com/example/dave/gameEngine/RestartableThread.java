package com.example.dave.gameEngine;

@Deprecated
public class RestartableThread {
	private final Runnable runMe;
	private Thread t;

	public RestartableThread(Runnable runMe) {
		this.runMe = runMe;
	}

	public void start(){
		if(t!=null && t.isAlive())
			t.interrupt();

		t = new Thread(runMe);
		t.start();
	}

	public void joinAndRestart() throws InterruptedException{
		t.join();
		t = new Thread(runMe);
		t.start();
	}

	public void interrupt(){
		if(t!=null)
			t.interrupt();
		t=null;
	}

	public boolean isInterrupted(){
		return t.isInterrupted();
	}
}
