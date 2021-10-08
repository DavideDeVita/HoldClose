package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.entity_component.GameObject;

import java.util.Iterator;
import java.util.Set;

public class F_DistanceFromElement extends F_Savable<Float>{
	private final GameElement element;
	private final String saveArgmin;
	private float minDist, dist; //allocated once
	private Entity argMin;

	public F_DistanceFromElement(GameElement element){
		super();
		this.element=element;
		this.saveArgmin=null;
	}

	public F_DistanceFromElement(String saveAs, GameElement element){
		this(saveAs, null, element);
	}

	public F_DistanceFromElement(String saveAs, String saveArgMin, GameElement element){
		super(saveAs);
		this.element=element;
		this.saveArgmin=saveArgMin;
	}

	@Override
	public Float fetch(Entity This, GameSection gs){
		return fetch(This, gs, null);
	}

	@Override
	public Float fetch(Entity This, GameSection gs, Loggable loggable) {
		minDist=-1;
		Set<GameObject> byTag = gs.gObjectsAS.getByTag(element);
		argMin=null;
		if(byTag!=null) {
			Iterator<GameObject> iterator = byTag.iterator();
			GameObject go;
			for (int i = byTag.size(); i > 0; i--) {
				go = iterator.next();
				dist = MyMath.dist(This.getPhysical().getX(), This.getPhysical().getY(), go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist) {
					minDist = dist;
					argMin = go;
				}
			}
		}

		//minDist = (minDist==-1) ? null : minDist;
		saveValue(This, minDist);
		saveValue(This, saveArgmin, argMin);
		if(loggable!=null)
			loggable.appendLog("\n\t\t DistanceFromElement Field:\t Distance of "+This+" from element "+element+" =\t"+minDist+" ("+argMin+")");
		return minDist;
	}
}
