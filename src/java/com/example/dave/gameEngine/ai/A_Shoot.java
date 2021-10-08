package com.example.dave.gameEngine.ai;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.entity_component.Physical_Cmpnt;

public class A_Shoot implements Action {
	private final A_ApplyTargetedImpulse aim;
	private final FloatValue offX, offY;

	private GameObject gObj=null;
	private Physical_Cmpnt thisPhys;

	public A_Shoot(A_ApplyTargetedImpulse aim, FloatValue offX, FloatValue offY) {
		this.aim = aim;
		this.offX = offX;
		this.offY = offY;
	}

	@Override
	public final boolean act(Entity entity, GameSection gs) {
		return act(entity, gs, null);
	}

	@Override
	public boolean act(Entity entity, GameSection gs, Loggable loggable) {
		thisPhys = entity.getPhysical();
		float x=thisPhys.getX()+offX.get(), y=thisPhys.getY()+offY.get();
		gObj = entity.getSummoner().summon(x, y );
		gObj.addComponent(entity.getFlags().shareFlags(gObj)); //This will give the bullet the information about the target
		if(loggable!=null)
			loggable.appendLog("\n\tShoot Action:\t"+entity+" (in "+thisPhys.getX()+", "+thisPhys.getY()+") created "+gObj+" in ("+x+", "+y+") and fired..");
		aim.act(gObj, gs);
		return true;
	}
}