package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.ai.decisionTree.Field;
import com.example.dave.gameEngine.entity_component.Entity;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class A_ApplyDirectedImpulse extends A_ApplyImpulse {
	private final Field<float[]> field;
	private final FloatValue biasX, biasY;

	private float[] fetch;

	public A_ApplyDirectedImpulse(FloatValue intensity, boolean x, boolean y, boolean weight, String saveDirectionAs, Field<float[]> direction) {
		super(intensity, x, y, weight, saveDirectionAs);
		this.field = direction;
		this.biasX=this.biasY=FloatValue._0f;
	}

	public A_ApplyDirectedImpulse(FloatValue intensity, FloatValue biasX, FloatValue biasY, boolean x, boolean y, boolean weight, String saveDirectionAs, Field<float[]> direction) {
		super(intensity, x, y, weight, saveDirectionAs);
		this.field = direction;
		this.biasX=biasX;
		this.biasY=biasY;
	}

	@Override
	protected float[] direction(Entity entity, GameSection gs, Loggable loggable) {
		fetch = field.fetch(entity, gs);
		if(x && y){
			dir[X] = fetch[X] + biasX.get();
			dir[Y] = fetch[Y] + biasY.get();
		}
		else if (x){
			dir[X] = fetch[X] + biasX.get();
			dir[Y] = 0;
		}
		else { //y
			dir[X] = 0;
			dir[Y] = fetch[Y] + biasY.get();
		}
		if(loggable!=null)
			loggable.appendLog("\n\t\t..Directed Impulse\tdirection:  ["+dir[X]+",  "+dir[Y]+"]");
		return dir;
	}
}
