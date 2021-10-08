package com.example.dave.gameEngine.myMultimedia;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.example.dave.gameEngine._Log;

public class Spritesheet {
	public final Bitmap image;
	private final int frameWidth, frameHeight;
	private final int length[];
	private final float xMinBias[][], yMinBias[][], xMaxBias[][], yMaxBias[][];

	public Spritesheet(DrawableRes drawRes, int frameWidth, int frameHeight, int[] length, float[][] xMinBias, float[][] yMinBias, float[][] xMaxBias, float[][] yMaxBias) {
		this.image = drawRes.getBitmap();
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.length = length;
		this.xMinBias = xMinBias;
		this.yMinBias = yMinBias;
		this.xMaxBias = xMaxBias;
		this.yMaxBias = yMaxBias;
	}

	public Spritesheet(int drawableID, int frameWidth, int frameHeight, int[] length, float[][] xMinBias, float[][] yMinBias, float[][] xMaxBias, float[][] yMaxBias) {
		this.image = DrawableRes.loadDrawable(drawableID);
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.length = length;
		this.xMinBias = xMinBias;
		this.yMinBias = yMinBias;
		this.xMaxBias = xMaxBias;
		this.yMaxBias = yMaxBias;
	}

	public void getFrame(Rect source, int animation, int step){
		if(animation>=length.length || animation<0)
			throw new IllegalStateException("Spritesheet\tRequired animation "+animation+", animations from 0 to "+length.length);
		if(step>=length[animation] || step<0)
			throw new IllegalStateException("Spritesheet\tRequired step "+step+" of animation "+animation+", steps from 0 to "+length[animation]);
		source.set(frameWidth*step, frameHeight*animation, frameWidth*(step+1), frameHeight*(animation+1));
		if(_Log.LOG_ACTIVE){
			_Log.d("Animation", "animation: "+animation+"\tstep: "+step+"\nsrc is: "+
				(frameWidth*step)+" "+(frameHeight*animation)+" "+(frameWidth*(step+1))+" "+(frameHeight*(animation+1)));}
	}

	public int getAnimationLength(int animation){
		return length[animation];
	}

	public int getNumberOfAnimations(){
		return length.length;
	}

	public float getFrame_xMin_bias(int animation, int step) {
		if(xMinBias==null) return 0F;
		return xMinBias[animation][step];
	}
	public float getFrame_xMax_bias(int animation, int step) {
		if(xMaxBias==null) return 0F;
		return xMaxBias[animation][step];
	}
	public float getFrame_yMin_bias(int animation, int step) {
		if(yMinBias==null) return 0F;
		return yMinBias[animation][step];
	}
	public float getFrame_yMax_bias(int animation, int step) {
		if(yMaxBias==null) return 0F;
		return yMaxBias[animation][step];
	}
}