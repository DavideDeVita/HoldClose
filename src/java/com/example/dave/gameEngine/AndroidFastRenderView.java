package com.example.dave.gameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private Bitmap framebuffer;
    private Thread renderThread = null;
    private SurfaceHolder holder;
    private GameContent gameContent;
    private volatile boolean running = false;
    public final Rect dstRect;

    public AndroidFastRenderView(Context context, GameContent game) {
        super(context);
        this.gameContent = game;
        this.framebuffer = GraphicSubSys.buffer;
        this.holder = getHolder();
        dstRect = new Rect();
        dstRect.set(0,0,MainActivity.screenWidth, MainActivity.screenHeight);
    }

    /** Starts the game loop in a separate thread.
     */
    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();         
    }

    /** Stops the game loop and waits for it to finish
     */
    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // just retry
            }
        }
    }

    @Override /**This is the Game Loop.. everything called in here should have @gameLoop*/
    public void run() {
        long startTime = System.nanoTime(), fpsTime = startTime;
        int frameCounter = 0, secondsCounter=0;
        float avgFps=0;

        /*** The Game Main Loop ***/
        while (running && gameContent.isOn()) {
            if(!holder.getSurface().isValid()) {
                // too soon (busy waiting), this only happens on startup and resume
                continue;
            }

            long currentTime = System.nanoTime();
            // deltaTime is in seconds
            float deltaTime = (currentTime-startTime) / 1_000_000_000f,//seconds
                  fpsDeltaTime = (currentTime-fpsTime) / 1_000_000_000f;
            startTime = currentTime;


            if(!gameContent.update(deltaTime))
            	break; //Game Over.. sadly
            gameContent.render();

            // Draw framebuffer on screen
            Canvas canvas = holder.lockCanvas();
            //canvas.getClipBounds(dstRect);

            // Scales to actual screen resolution
            canvas.drawBitmap(framebuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);

            // Measure FPS
            frameCounter++;
            if (fpsDeltaTime > 1) { // every second
            	avgFps=((avgFps*secondsCounter)+frameCounter)/(secondsCounter+1);
	            secondsCounter++;
	            //if(_Log.LOG_ACTIVE){
	                _Log.i("FastRenderView", "Current FPS = " + frameCounter+"\n\t\tavgFps: "+avgFps);//}
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
        if(!gameContent.isOn() ) {
	        //if(_Log.LOG_ACTIVE){
		        _Log.i("FastRenderView", "AVG FPS: "+avgFps);//}
            MainActivity.thisActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(_Log.LOG_ACTIVE){
                        _Log.d("Menu", Thread.currentThread() + " android Render View calling on ui thread");}
                    MainMenu.instance.changeContent();
                }
            });
        }
    }
}