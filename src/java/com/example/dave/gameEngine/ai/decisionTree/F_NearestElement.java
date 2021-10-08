package com.example.dave.gameEngine.ai.decisionTree;

import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.ai.Loggable;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.GameObject;

import java.util.Iterator;
import java.util.Set;

@Deprecated
public class F_NearestElement extends F_Savable<Entity>{
	private final GameElement element;
	private final String saveArgmin;

	private float minDist, dist; //allocated once
	private	GameObject argMin=null;

	public F_NearestElement(GameElement element){
		super();
		this.element=element;
		this.saveArgmin=null;
	}

	public F_NearestElement(String saveAs, GameElement element){
		this(saveAs, null, element);
	}

	public F_NearestElement(String saveAs, String saveArgMin, GameElement element){
		super(saveAs);
		this.element=element;
		this.saveArgmin=saveArgMin;
	}

	@Override
	public Entity fetch(Entity This, GameSection gs){
		return fetch(This, gs, null);
	}

	@Override
	public Entity fetch(Entity This, GameSection gs, Loggable loggable) {
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

		//minDist = (minDist==-1) ? -1 : minDist;
		saveValue(This, argMin);
		saveValue(This, saveArgmin, minDist);
		if(loggable!=null)
			loggable.appendLog("\n\t\tNearest Element Field:\t Nearest object of element "+element+" to "+This+" is:\t"+argMin);
		return argMin;
	}
}
