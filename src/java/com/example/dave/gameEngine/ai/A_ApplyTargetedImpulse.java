package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.entity_component.Entity;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class A_ApplyTargetedImpulse extends A_ApplyImpulse {
	private final Field<Entity> field;
	private final boolean towards;
	private final FloatValue biasX, biasY;

	public A_ApplyTargetedImpulse(FloatValue intensity, boolean x, boolean y, boolean weight, String saveDirectionAs, Field<Entity> target, boolean towards) {
		super(intensity, x, y, weight, saveDirectionAs);
		this.field = target;
		this.towards=towards;
		this.biasX=this.biasY=FloatValue._0f;
	}

	public A_ApplyTargetedImpulse(FloatValue intensity, FloatValue biasX, FloatValue biasY, boolean x, boolean y, boolean weight, String saveDirectionAs, Field<Entity> target, boolean towards) {
		super(intensity, x, y, weight, saveDirectionAs);
		this.field = target;
		this.towards=towards;
		this.biasX=biasX;
		this.biasY=biasY;
	}

	@Override
	protected float[] direction(Entity entity, GameSection gs, Loggable loggable) {
		if(x && y){
			dir = entity.direct(dir, field.fetch(entity, gs), towards);
			dir[X] += biasX.get();
			dir[Y] += biasY.get();
		}
		else if (x){
			dir = entity.directX(dir, field.fetch(entity, gs), towards);
			dir[X]+=biasX.get();
			//dir[Y]+=biasY.get();
		}
		else { //y
			dir = entity.directY(dir,  field.fetch(entity, gs), towards);
			//dir[X] += biasX.get();
			dir[Y] += biasY.get();
		}
		if(loggable!=null)
			loggable.appendLog("\n\t\t..Targeted Impulse\tdirection:  ["+dir[X]+",  "+dir[Y]+"]");
		return dir;
	}
}
