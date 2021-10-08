package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class A_ApplyRandomImpulse extends A_ApplyImpulse {
	private final FloatValue dirX, dirY;

	public A_ApplyRandomImpulse(FloatValue intensity, boolean x, boolean y, boolean weight, String saveDirectionAs, FloatValue dirX, FloatValue dirY) {
		super(intensity, x, y, weight, saveDirectionAs);
		this.dirX = dirX;
		this.dirY = dirY;
	}

	@Override
	protected float[] direction(Entity entity, GameSection gs, Loggable loggable) {
		if(x && y){
			dir[X]=dirX.get();
			dir[Y]=dirY.get();
		}
		else if (x){
			dir[X]=dirX.get();
			dir[Y]=0;   //dirY.get();
		}
		else{//y
			dir[X]=0;   //dirX.get();
			dir[Y]=dirY.get();
		}
		if(loggable!=null)
			loggable.appendLog("\n\t.. Random Impulse:\t direction: {"+dir[X]+", "+dir[Y]+"}");
		return dir;
	}
}