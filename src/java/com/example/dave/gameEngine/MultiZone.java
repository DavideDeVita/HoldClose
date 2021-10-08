package com.example.dave.gameEngine;

public class MultiZone {
	private final Box[] zones;
	private int i;

	public MultiZone(Box physicalWorld, Box[] zones) {
		this.zones = new Box[zones.length];
		for(i=0; i<zones.length; i++){
			this.zones[i] = new SubBox(zones[i], physicalWorld);
		}
	}

	public MultiZone(Box physicalWorld) {
		this.zones = new Box[1];
		this.zones[0] = physicalWorld;
	}

	/*Could be strongly optimized.. but i still don't know if I'm going to use it*/
	public boolean contains(float x, float y){
		for(i=0; i<zones.length; i++){
			if(zones[i].contains(x, y))
				return true;
		}
		return false;
	}

	public Box getClosest(float x, float y){
		Box argmin=zones[0];
		float minDist = argmin.distanceFrom(x, y), dist;
		for(i=1; i<zones.length; i++){
			dist = zones[i].distanceFrom(x, y);
			if( dist < minDist){
				minDist=dist;
				argmin=zones[i];
			}
		}
		return argmin;
	}
}
