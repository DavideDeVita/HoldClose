package com.example.dave.gameEngine.myMultimedia;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine._Log;

public enum MyBackgrounds {
	//Tutorial Background
	Tutorial_1(R.drawable.tutorial1), Tutorial_2(R.drawable.tutorial2),	Tutorial_3(R.drawable.tutorial3),
	Tutorial_4(R.drawable.tutorial4), Tutorial_5(R.drawable.tutorial5), Tutorial_6(R.drawable.tutorial6),
	Tutorial_7(R.drawable.tutorial7), Tutorial_8(R.drawable.tutorial8),

	//Level 1 background
	Level1_bg(R.drawable.lvl1_button),

	//Level 2 Background
	Level2_bg(R.drawable.lvl2_button), Level2_Cave_bg(R.drawable.lvl2_cave),

	//Level 1 Story
	Level1_Ante1(R.drawable.lvl1ante1), Level1_Ante2(R.drawable.lvl1ante2),
	Level1_Ante3(R.drawable.lvl1ante3), Level1_Ante4(R.drawable.lvl1ante4),

	Level1_Post1(R.drawable.lvl1post1), Level1_Post2(R.drawable.lvl1post2),
	Level1_Post3(R.drawable.lvl1post3), Level1_Post4(R.drawable.lvl1post4),

	//Level 2 Story
	Level2_Ante1(R.drawable.lvl2ante1), Level2_Ante2(R.drawable.lvl2ante2),
	Level2_Ante3(R.drawable.lvl2ante3), Level2_Ante4(R.drawable.lvl2ante4), Level2_Ante5(R.drawable.lvl2ante5),

	Level2_Post1(R.drawable.lvl2post1), Level2_Post2(R.drawable.lvl2post2),

	//Level 3 Story
	Level3_Ante1(R.drawable.lvl3ante1),

	Level3_Post1(R.drawable.lvl3post1), Level3_Post2(R.drawable.lvl3post2),
	Level3_Post3(R.drawable.lvl3post3), Level3_Post4(R.drawable.lvl3post4), Level3_Post5(R.drawable.lvl3post5),

	//Credits
	Credits1(R.drawable.credits1), Credits2(R.drawable.credits2),
	Credits3(R.drawable.credits3), Credits(R.drawable.credits),
	CreditsEasterEgg(R.drawable.missable_happy_ending)
	;

	private final int id;
	private Bitmap bitmap=null;
	private static final BitmapFactory.Options bf_o = new BitmapFactory.Options();
		static{
			bf_o.inScaled=true;
		}

	MyBackgrounds(int id){
		this.id=id;
	}

	public Bitmap getBitmap(){
		if(bitmap==null) {
			_Log.i("Bitmap", "MyBackground is allocating for a new bitmap");
			bitmap = BitmapFactory.decodeResource(MainActivity.thisActivity.getResources(), id, bf_o);
		}
		return bitmap;
	}
}
