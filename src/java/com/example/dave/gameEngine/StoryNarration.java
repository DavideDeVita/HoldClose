package com.example.dave.gameEngine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.dave.gameEngine.myMultimedia.MyBackgrounds;

public class StoryNarration {
	private final MyBackgrounds[] images;
	private final int screenWidth, screenHeight;
	private final float requiredTime; //in seconds. (for each image)
	private final boolean topDown;
	private int bgH;
	private Bitmap background;
	private Rect bgSrc = new Rect();

	private final static int hOffset=250;//both start & end

	public StoryNarration(int screenWidth, int screenHeight, MyBackgrounds[] images, float requiredTime, boolean topDown) {
		this.screenWidth=screenWidth;
		this.screenHeight=screenHeight;
		this.requiredTime=requiredTime;
		this.images = images;
		this.topDown=topDown;
		GraphicSubSys.clear();
		setBackground(0);
		update(0);
	}

	private int i=0, h=0;
	private float time=0F;
	/**Returns false when the story fragment is over.
	 *Game Level should end in this case*/
	/*public boolean _update(float elapsedTime){
		if(time>=0){
			time+=elapsedTime;
			h = (int)((time/requiredTime) * bgH);
			if(h>bgH){
				i++;
				if(i>=images.length)
					return false;
				_Log.w("Narration", "next image");
				setBackground(i);
				h=0;
				time=-1F;
			}
			setView(h);
		}
		else{
			time=0F;
			h=0;
			setView(h);
		}
		return true;
	}*/
	public boolean update(float elapsedTime){
		time+=elapsedTime;
		h = (int)((time/requiredTime) * (bgH-2*hOffset)) + hOffset;
		if(h>bgH -hOffset){
			i++;
			if(i>=images.length)
				return false;
			//_Log.w("Narration", "next image");
			setBackground(i);
			h=0;
			time=0F;
		}
		setView(h);
		return true;
	}

	public void render(){
		GraphicSubSys.drawScreenBackground(background, bgSrc);
	}

	private void setBackground(int ith){
		background = images[ith].getBitmap();
		bgH=background.getHeight();
	}

	/*top <= bottom*/
	//private int from, to;
	private void setView(int h) {
		if(!this.topDown)
			h=bgH-h;
		if(h-(screenHeight/2)<0){
			bgSrc.set( 0,
					0,
					background.getWidth(),
					MyMath.min(0+screenHeight, bgH) //Should the image be less than screenSize
			);
			//from=0;
			//to=MyMath.min(0+screenHeight, bgH);
		}
		else if(h+(screenHeight/2)>bgH) {
			bgSrc.set(0,
					MyMath.max(0, bgH-screenHeight ), //should always be the 2nd
					background.getWidth(),
					bgH
			);
			//from=MyMath.max(0, bgH-screenHeight);
			//to=bgH;
		}
		else{
			bgSrc.set( 0,
					h-(screenHeight/2),
					background.getWidth(),
					h+(screenHeight/2) //Should the image be less than screenSize
			);
			//from=h-(screenHeight/2);
			//to=h+(screenHeight/2);
		}
	}
}