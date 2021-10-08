package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.ComponentType;
import com.example.dave.gameEngine.entity_component.Entity;

public abstract class A_ApplyImpulse implements Action {
	private final FloatValue intensity;
	protected final boolean x, y, weighted;
	private final String saveDirectionAs;

	private float _intensity;

	protected A_ApplyImpulse(FloatValue intensity, boolean x, boolean y, boolean weighted, String saveDirectionAs) {
		this.intensity = intensity;
		this.x = x;
		this.y = y;
		this.weighted = weighted;
		this.saveDirectionAs=saveDirectionAs;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		_intensity = intensity.get() * (weighted ? entity.getPhysical().getArea() : 1f);
		if(loggable!=null)
			loggable.appendLog("\n\tApply Impulse Action:\ton object "+entity+". {over X:"+x+", over Y:"+y+", intensity: "+_intensity+
					(weighted?"\tweighted" : "") + "}");
		entity.getPhysical().applyImpulse(
				direction(entity, gs, loggable),
				_intensity //if intensity has to be weighted
		);
		if(saveDirectionAs!=null && entity.hasComponent(ComponentType.Flags))
			entity.getFlags().setFlag(saveDirectionAs, dir);
		return true;
	}

	protected float[] dir = new float[2];
	protected abstract float[] direction(Entity entity, GameSection gs, Loggable loggable);
}
