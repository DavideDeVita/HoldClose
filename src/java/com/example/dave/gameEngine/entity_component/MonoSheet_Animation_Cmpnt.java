package com.example.dave.gameEngine.entity_component;

import android.graphics.Rect;

import com.example.dave.gameEngine.myMultimedia.Spritesheet;
import com.example.dave.gameEngine.dataDriven.component.Animation_Properties;
import com.example.dave.gameEngine.dataDriven.component.Animation_Properties.SingleSheet_Properties;

public class MonoSheet_Animation_Cmpnt extends Animation_Cmpnt {
	private final Spritesheet spritesheet;

	public MonoSheet_Animation_Cmpnt(Entity owner, Animation_Properties animation_p) {
		super(owner, animation_p);
		SingleSheet_Properties singleSheet_p = (SingleSheet_Properties)animation_p.sheetUpdate_p;
		this.spritesheet=singleSheet_p.sheets.spritesheet;
	}

	void setSheetRect(Rect src){
		/*if(true){
			src.set(0, 0, 275, 275);
			return;
		}*/
		curr=System.nanoTime();
		if(curr>lastTime+delay) {
			lastTime=curr;
			selectAnimation();
			if(currentAnimation!=lastAnimation || currentStep!=lastStep) {
				lastAnimation=currentAnimation;
				lastStep=currentStep;
				spritesheet.getFrame(src, currentAnimation, currentStep);
			}
		}
	}

	@Override
	protected Spritesheet currSheet() {
		return spritesheet;
	}
}