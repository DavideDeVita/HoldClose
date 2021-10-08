package com.example.dave.gameEngine.myMultimedia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;

public enum DrawableRes {
	//Leaf(R.drawable.leaf),
	Leaf_SE(R.drawable.leaf_se),                           Leaf_SW(R.drawable.leaf_sw),
	Leaves_SW(R.drawable.leaves_sw),                       Leaves_SE(R.drawable.leaves_se),

	WetLeaf_SW(R.drawable.leaf_wet_sw),                    WetLeaf_SE(R.drawable.leaf_wet_se),
	WetLeaves_SW(R.drawable.leaves_wet_sw),                WetLeaves_SE(R.drawable.leaves_wet_se),

	YellowLeaf_SW(R.drawable.leaf_yellow_sw),              YellowLeaf_SE(R.drawable.leaf_yellow_se),
	OrangeLeaf_SW(R.drawable.leaf_orange_sw),              OrangeLeaf_SE(R.drawable.leaf_orange_se),

	//Wood
	Log(R.drawable.wood_log),                              Log_Mirror(R.drawable.wood_log_mirror),

	BurningLog(R.drawable.wood_burning_log),               BurningLog_Mirror(R.drawable.wood_burning_log_mirror),
	BurningLogStack(R.drawable.wood_burning_log_stacked),  BurningLogStack_Mirror(R.drawable.wood_burning_log_stacked_mirror),
	BurningLogSmoke(R.drawable.wood_burning_log_smoke), //mirrored?

	//Stone
	Stone1(R.drawable.stone1),Stone2(R.drawable.stone2),Stone3(R.drawable.stone3),
	Stone4(R.drawable.stone4),Stone5(R.drawable.stone5),Stone6(R.drawable.stone6),
	Stone7(R.drawable.stone7),Stone8(R.drawable.stone8),Stone9(R.drawable.stone9),

	SecretStone(R.drawable.secret_stone),

	//Ground
	GroundGrass1(R.drawable.ground_grass_1),                GroundGrass1_mirrored(R.drawable.ground_grass_1_mirrored),
	GroundGrass2(R.drawable.ground_grass_2),                GroundGrass2_mirrored(R.drawable.ground_grass_2_mirrored),
	GroundGrass3(R.drawable.ground_grass_3),                GroundGrass3_mirrored(R.drawable.ground_grass_3_mirrored),
	GroundGrass4(R.drawable.ground_grass_4),                GroundGrass4_mirrored(R.drawable.ground_grass_4_mirrored),

	GroundBurnt1(R.drawable.ground_burnt_1),                GroundBurnt1_mirrored(R.drawable.ground_burnt_1_mirrored),
	GroundBurnt2(R.drawable.ground_burnt_2),                GroundBurnt2_mirrored(R.drawable.ground_burnt_2_mirrored),
	GroundBurnt3(R.drawable.ground_burnt_3),                GroundBurnt3_mirrored(R.drawable.ground_burnt_3_mirrored),
	GroundBurnt4(R.drawable.ground_burnt_4),                GroundBurnt4_mirrored(R.drawable.ground_burnt_4_mirrored),
	;

	private final int id;
	private Bitmap bitmap=null;
	private static final BitmapFactory.Options bf_o = new BitmapFactory.Options();
	static{
		bf_o.inScaled=false;
	}

	DrawableRes(int id){
		this.id=id;
	}

	public Bitmap getBitmap(){
		//bf_o.inScaled=true;
		if(bitmap==null)
			bitmap = BitmapFactory.decodeResource(MainActivity.thisActivity.getResources(), id, bf_o);
		return bitmap;
	}

	public static Bitmap loadDrawable(int id){
		//bf_o.inScaled=false;
		return BitmapFactory.decodeResource(MainActivity.thisActivity.getResources(), id, bf_o);
	}
}
