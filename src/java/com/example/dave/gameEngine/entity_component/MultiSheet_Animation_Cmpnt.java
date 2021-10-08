package com.example.dave.gameEngine.entity_component;

import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.Pair;

import com.example.dave.gameEngine.myMultimedia.Spritesheet;
import com.example.dave.gameEngine.dataDriven.component.Animation_Properties;
import com.example.dave.gameEngine.dataDriven.component.Animation_Properties.MultipleSheet_Properties;
import com.example.dave.gameEngine.myMultimedia.Spritesheets;

import java.util.Collections;
import java.util.Map;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;
import static com.example.dave.gameEngine.MyMath.between;

public class MultiSheet_Animation_Cmpnt extends Animation_Cmpnt {
	private final Spritesheet sheets[];
	private int currSheet=0;
	private final Map<Pair<String, ?>, Integer> updateSheetCondition;

	public MultiSheet_Animation_Cmpnt(Entity owner, Animation_Properties animation_p) {
		super(owner, animation_p);
		MultipleSheet_Properties multiSheet_p = (MultipleSheet_Properties)animation_p.sheetUpdate_p;
		this.sheets=new Spritesheet[multiSheet_p.sheets.length];
		int i=0;
		for(Spritesheets s_s : multiSheet_p.sheets)
			sheets[i++] = s_s.spritesheet;
		this.updateSheetCondition= new ArrayMap<>();
		multiSheet_p.copyConditionsInto(updateSheetCondition);
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
				lastAnimation = currentAnimation;
				lastStep = currentStep;
				currSheet().getFrame(src, currentAnimation, currentStep);
			}
		}
	}

	@Override
	protected Spritesheet currSheet() {
		return sheets[currSheet];
	}

	@Override
	public void notify(String whatChanged, Object howIsNow) {
		Pair<String, ?> pair = new Pair<>(whatChanged, howIsNow);
		Integer newSheet = updateSheetCondition.get(pair);
		if(newSheet!=null && currSheet!=newSheet) {
			currSheet = newSheet;
			currentStep=0;
			currentAnimation=0;
		}
	}

	public String all(){
		String ret="";
		for (Map.Entry<Pair<String, ?>, Integer> entry : updateSheetCondition.entrySet())
			ret+=entry.getKey()+" : "+entry.getValue()+"\n";
		return ret;
	}
}