package com.example.dave.gameEngine.myMultimedia;

import com.example.dave.gameEngine.R;

public enum Spritesheets {
	Fuocherello(new Spritesheet(R.drawable.spritesheet_fuocherello,
			275, 275,
			new int[]{2,4,2,2, 2,4,2,2},
			new float[][] {
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			},
			new float[][] {
					{0.1F, 0.1F}, {0.1F, 0.1F, 0.1F, 0.1F}, {0.1F, 0.1F}, {0.1F, 0.1F},
					{0.1F, 0.1F}, {0.1F, 0.1F, 0.1F, 0.1F}, {0.1F, 0.1F}, {0.1F, 0.1F}
			},
			new float[][] {
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			},
			new float[][] {
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			}
	)),
	WatHer(new Spritesheet(R.drawable.spritesheet_wather,
			275, 275,
			new int[]{3,4,2,2, 3,4,2,2},
			new float[][] {
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			},
			new float[][] {
					{0.1F, 0.1F, 0.1F}, {0.1F, 0.1F, 0.1F, 0.1F}, {0.1F, 0.1F}, {0.1F, 0.1F},
					{0.1F, 0.1F, 0.1F}, {0.1F, 0.1F, 0.1F, 0.1F}, {0.1F, 0.1F}, {0.1F, 0.1F}
			},
			new float[][] {
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			},
			new float[][] {
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F},
					{0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, 0.5F, 0.5F}, {0.5F, 0.5F}, {0.5F, 0.5F}
			}
	)),
	Ant(new Spritesheet(R.drawable.spritesheet_formicuzza,
			275, 275,
			new int[]{3,3},
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			null,
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			new float[][] {
					{0.75F, 0.75F, 0.75F},   // Dx
					{0.75F, 0.75F, 0.75F},  // Sx
			}
	)),
	Ant_Armed(new Spritesheet(R.drawable.spritesheet_formicuzza_haswater,
			275, 275,
			new int[]{3,3},
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			null,
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			new float[][] {
					{0.33F, 0.33F, 0.33F},   // Dx
					{0.33F, 0.33F, 0.33F},  // Sx
			}
	)),
	Evil_Ant(new Spritesheet(R.drawable.spritesheet_formicuzza_cattiva,
			275, 275,
			new int[]{3,3},
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			null,
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			new float[][] {
					{0.75F, 0.75F, 0.75F},   // Dx
					{0.75F, 0.75F, 0.75F},  // Sx
			}
	)),
	Evil_Ant_Armed(new Spritesheet(R.drawable.spritesheet_formicuzza_cattiva_haswater,
			275, 275,
			new int[]{3,3},
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			null,
			new float[][] {
					{0.5F, 0.5F, 0.5F},   // Dx
					{0.5F, 0.5F, 0.5F},  // Sx
			},
			new float[][] {
					{0.33F, 0.33F, 0.33F},   // Dx
					{0.33F, 0.33F, 0.33F},  // Sx
			}
	)),
	Flame(new Spritesheet(R.drawable.spritesheet_flames,
			100, 100,
			new int[]{6, 6, 6},
			null,
			null,
			null,
			new float[][] {
					{0.33F, 0.33F, 0.33F, 0.33F, 0.33F, 0.33F},
					{0.33F, 0.33F, 0.33F, 0.33F, 0.33F, 0.33F},
					{0.33F, 0.33F, 0.33F, 0.33F, 0.33F, 0.33F}
			}
	)),
	Water_Drop(new Spritesheet(R.drawable.spritesheet_waterdrop,
			60, 60,
			new int[]{4, 4, 4},
			null,
			null,
			null,
			null // Sx
	)),
	;

	public final Spritesheet spritesheet;

	Spritesheets(Spritesheet spritesheet) {
		this.spritesheet=spritesheet;
	}
}
