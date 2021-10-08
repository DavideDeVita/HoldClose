package com.example.dave.gameEngine;

import com.example.dave.gameEngine.myMultimedia.MySound;
import com.example.dave.gameEngine.myMultimedia.Playable;

public class CollisionSounds {
    private static Playable[][] map;

    static{
    	 int size = GameElement.values().length;
    	 int FIRE = GameElement.Fire.ordinal(),
		     WATER = GameElement.Water.ordinal(),
		     GREEN = GameElement.Green.ordinal(),
		     WET_GREEN = GameElement.WetGreen.ordinal(),
		     BURNING_GREEN = GameElement.BurningGreen.ordinal(),
		     WOOD = GameElement.Wood.ordinal(),
		     WET_WOOD = GameElement.WetWood.ordinal(),
		     BURNING_WOOD = GameElement.BurningWood.ordinal()
		     ;
    	 map = new MySound[size][size];

	    map[FIRE][FIRE] = MySound.fireGrow;
	    map[FIRE][WATER] = map[WATER][FIRE] = MySound.evaporatingWater;
	    map[FIRE][GREEN] = map[GREEN][FIRE] = MySound.burningLeaves;
	    //map[FIRE][WET_GREEN] = map[WET_GREEN][FIRE] = NoSound;
	    map[FIRE][BURNING_GREEN] = map[BURNING_GREEN][FIRE] = MySound.burningLeaves;
	    map[FIRE][WOOD] = map[WOOD][FIRE] = MySound.burningLeaves;
	    map[FIRE][BURNING_WOOD] = map[BURNING_WOOD][FIRE] = MySound.fireGrow;

	    map[WATER][WATER] = MySound.waterDrop;
	    //Water fire done already
	    //map[WATER][GREEN] = map[GREEN][WATER] = MySound.dropOfRain;
	    //map[WATER][BURNING_GREEN] = map[BURNING_GREEN][WATER] = MySound.evaporatingWater;
	    map[WATER][BURNING_WOOD] = map[BURNING_WOOD][WATER] = MySound.evaporatingWater;
	    //map[WATER][WOOD] = map[WOOD][WATER] = No Sound;

	    map[BURNING_WOOD][BURNING_GREEN] = map[BURNING_GREEN][BURNING_WOOD] = MySound.burningLeaves;
	    map[BURNING_WOOD][GREEN] = map[BURNING_GREEN][GREEN] = MySound.burningLeaves;
	    map[BURNING_WOOD][WOOD] =  map[BURNING_GREEN][WOOD] = MySound.burningLeaves;

	    map[WET_WOOD][WET_GREEN] = map[WET_GREEN][WET_WOOD] = MySound.waterDrop;
	    map[WET_WOOD][GREEN] = map[WET_GREEN][GREEN] = MySound.waterDrop;
	    map[WET_WOOD][WOOD] =  map[WET_GREEN][WOOD] = MySound.waterDrop;
	    //...
    }

    public static Playable getSound(GameElement a, GameElement b){
        if(a==null || b==null)
        	return null;
        return map[a.ordinal()][b.ordinal()];
    }
}
