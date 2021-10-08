package com.example.dave.gameEngine;

public enum GameElement {
	Ant, Fire, Water, Stone,
	Green, WetGreen, BurningGreen,
	Wood, WetWood, BurningWood,
	NULL;

	//This should be (and was) in Health_Cmpnt.. but is more legible
	public final static float[][] elementEffect = new float[values().length][values().length];

	static{ //eE[A][B] = multiplier for A when hits B. A is the receiver of the dmg
		elementEffect[Ant.ordinal()][Ant.ordinal()] = 0.01f;
		elementEffect[Ant.ordinal()][Fire.ordinal()] = 1.5f;
		elementEffect[Ant.ordinal()][Water.ordinal()] = 0.33f;
		elementEffect[Ant.ordinal()][Green.ordinal()] = 0f;
		elementEffect[Ant.ordinal()][WetGreen.ordinal()] = -0.1f;
		elementEffect[Ant.ordinal()][BurningGreen.ordinal()] = 1.1f;
		elementEffect[Ant.ordinal()][Wood.ordinal()] = 0.5f;
		elementEffect[Ant.ordinal()][WetWood.ordinal()] = -0.01f;
		elementEffect[Ant.ordinal()][BurningWood.ordinal()] = 1.25f;
		elementEffect[Ant.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[Fire.ordinal()][Ant.ordinal()] = 1f;
		elementEffect[Fire.ordinal()][Fire.ordinal()] = -1f;
		elementEffect[Fire.ordinal()][Water.ordinal()] = 2f;
		elementEffect[Fire.ordinal()][Green.ordinal()] = 0.5f;
		elementEffect[Fire.ordinal()][WetGreen.ordinal()] = 1f;
		elementEffect[Fire.ordinal()][BurningGreen.ordinal()] = -0.25f;
		elementEffect[Fire.ordinal()][Wood.ordinal()] = 0.75f;
		elementEffect[Fire.ordinal()][WetWood.ordinal()] = 1.25f;
		elementEffect[Fire.ordinal()][BurningWood.ordinal()] = -0.75f;
		elementEffect[Fire.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[Water.ordinal()][Ant.ordinal()] = 1.5f;
		elementEffect[Water.ordinal()][Fire.ordinal()] = 0.75f;
		elementEffect[Water.ordinal()][Water.ordinal()] = -1f;
		elementEffect[Water.ordinal()][Green.ordinal()] = 0.15f;
		elementEffect[Water.ordinal()][WetGreen.ordinal()] = -0.5f;
		elementEffect[Water.ordinal()][BurningGreen.ordinal()] = 0.5f;
		elementEffect[Water.ordinal()][Wood.ordinal()] = 1f;
		elementEffect[Water.ordinal()][WetWood.ordinal()] = -0.05f;
		elementEffect[Water.ordinal()][BurningWood.ordinal()] = 1.15f;
		elementEffect[Water.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[Stone.ordinal()][Ant.ordinal()] = 0f;
		elementEffect[Stone.ordinal()][Fire.ordinal()] = 0.125f;
		elementEffect[Stone.ordinal()][Water.ordinal()] = 0.1f;
		elementEffect[Stone.ordinal()][Green.ordinal()] = 0f;
		elementEffect[Stone.ordinal()][WetGreen.ordinal()] = 0.05f;
		elementEffect[Stone.ordinal()][BurningGreen.ordinal()] = 0.05f;
		elementEffect[Stone.ordinal()][Wood.ordinal()] = 0.66f;
		elementEffect[Stone.ordinal()][WetWood.ordinal()] = 0.5f;
		elementEffect[Stone.ordinal()][BurningWood.ordinal()] = 0.75f;
		elementEffect[Stone.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[Green.ordinal()][Ant.ordinal()] = 0.05f;
		elementEffect[Green.ordinal()][Fire.ordinal()] = 2.5f;
		elementEffect[Green.ordinal()][Water.ordinal()] = -0.15f;
		elementEffect[Green.ordinal()][Green.ordinal()] = 0f;
		elementEffect[Green.ordinal()][WetGreen.ordinal()] = -0.05f;
		elementEffect[Green.ordinal()][BurningGreen.ordinal()] = 1.05f;
		elementEffect[Green.ordinal()][Wood.ordinal()] = 0.85f;
		elementEffect[Green.ordinal()][WetWood.ordinal()] = 0.5f;
		elementEffect[Green.ordinal()][BurningWood.ordinal()] = 1.75f;
		elementEffect[Green.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[WetGreen.ordinal()][Ant.ordinal()] = 0.15f;
		elementEffect[WetGreen.ordinal()][Fire.ordinal()] = 0.88f;
		elementEffect[WetGreen.ordinal()][Water.ordinal()] = 0f;
		elementEffect[WetGreen.ordinal()][Green.ordinal()] = 0.01f;
		elementEffect[WetGreen.ordinal()][WetGreen.ordinal()] = 0f;
		elementEffect[WetGreen.ordinal()][BurningGreen.ordinal()] = 0.95f;
		elementEffect[WetGreen.ordinal()][Wood.ordinal()] = 0.75f;
		elementEffect[WetGreen.ordinal()][WetWood.ordinal()] = 0.1f;
		elementEffect[WetGreen.ordinal()][BurningWood.ordinal()] = 1.05f;
		elementEffect[WetGreen.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[BurningGreen.ordinal()][Ant.ordinal()] = 0.05f;
		elementEffect[BurningGreen.ordinal()][Fire.ordinal()] = 2f;
		elementEffect[BurningGreen.ordinal()][Water.ordinal()] = 1.5f;
		elementEffect[BurningGreen.ordinal()][Green.ordinal()] = 0f;
		elementEffect[BurningGreen.ordinal()][WetGreen.ordinal()] = 1.05f;
		elementEffect[BurningGreen.ordinal()][BurningGreen.ordinal()] = -0.33f; //See L2:S2
		elementEffect[BurningGreen.ordinal()][Wood.ordinal()] = 0.75f;
		elementEffect[BurningGreen.ordinal()][WetWood.ordinal()] = 1f;
		elementEffect[BurningGreen.ordinal()][BurningWood.ordinal()] = 1.75f;
		elementEffect[BurningGreen.ordinal()][Stone.ordinal()] = 1f;

		elementEffect[Wood.ordinal()][Ant.ordinal()] = 0.01f;
		elementEffect[Wood.ordinal()][Fire.ordinal()] = 2f;
		elementEffect[Wood.ordinal()][Water.ordinal()] = 0.7f;
		elementEffect[Wood.ordinal()][Green.ordinal()] = 0f;
		elementEffect[Wood.ordinal()][WetGreen.ordinal()] = -0.05f;
		elementEffect[Wood.ordinal()][BurningGreen.ordinal()] = 0.66f;
		elementEffect[Wood.ordinal()][Wood.ordinal()] = 1f;
		elementEffect[Wood.ordinal()][WetWood.ordinal()] = 0.25f;
		elementEffect[Wood.ordinal()][BurningWood.ordinal()] = 1.5f;
		elementEffect[Wood.ordinal()][Stone.ordinal()] = 1.25f;

		elementEffect[WetWood.ordinal()][Ant.ordinal()] = 0.15f;
		elementEffect[WetWood.ordinal()][Fire.ordinal()] = 1.33f;
		elementEffect[WetWood.ordinal()][Water.ordinal()] = 0.5f;
		elementEffect[WetWood.ordinal()][Green.ordinal()] = 0.01f;
		elementEffect[WetWood.ordinal()][WetGreen.ordinal()] = 0f;
		elementEffect[WetWood.ordinal()][BurningGreen.ordinal()] = 0.33f;
		elementEffect[WetWood.ordinal()][Wood.ordinal()] = 0.66f;
		elementEffect[WetWood.ordinal()][WetWood.ordinal()] = 0.15f;
		elementEffect[WetWood.ordinal()][BurningWood.ordinal()] = 1f;
		elementEffect[WetWood.ordinal()][Stone.ordinal()] = 1.33f;

		/*Both fire and water will hurt an already burning item*/
		elementEffect[BurningWood.ordinal()][Ant.ordinal()] = 0.05f;
		elementEffect[BurningWood.ordinal()][Fire.ordinal()] = 2f;
		elementEffect[BurningWood.ordinal()][Water.ordinal()] = 1.33f;
		elementEffect[BurningWood.ordinal()][Green.ordinal()] = 0f;
		elementEffect[BurningWood.ordinal()][WetGreen.ordinal()] = 0.57f;
		elementEffect[BurningWood.ordinal()][BurningGreen.ordinal()] = 1.33f;
		elementEffect[BurningWood.ordinal()][Wood.ordinal()] = 1.15f;
		elementEffect[BurningWood.ordinal()][WetWood.ordinal()] = 1.25f;
		elementEffect[BurningWood.ordinal()][BurningWood.ordinal()] = 1.5f;
		elementEffect[BurningWood.ordinal()][Stone.ordinal()] = 1.5f;
	}
}