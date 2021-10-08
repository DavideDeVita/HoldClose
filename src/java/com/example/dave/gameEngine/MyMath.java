package com.example.dave.gameEngine;

import java.util.Random;

/**Ironically, I always thought that Math was not an opinion*/
public class MyMath {
	public static final int X=0, Y=1;
	public static final float PI = 3.1415926535897932384f;
	public static final float TODEGREES = 180F / PI;
	public static final float TORADIANS = PI / 180F;
	public static final float[] cosine;
	public static final float[] sine;

	static {
		cosine = new float[360];
		for (int i = 0; i<360 ;i++)
			cosine[i] = (float) Math.cos(i * TORADIANS);
		sine = new float[360];
		for (int i = 0; i<360 ;i++)
			sine[i] = (float) Math.sin(i * TORADIANS);
	}


	private final static Random rand = new Random();

	public static final float sqrt(float arg){return (float)Math.sqrt(arg);}
	
	public static final float pow(float base, float exp){return (float)Math.pow(base, exp);}

	public static final float toRadians(float degrees){return degrees*TORADIANS;}

	public static final float toDegrees(float radians){return radians*TODEGREES;}

	public static final float sin(float radAngle){	return sin((int)(radAngle*TODEGREES) );}

	public static final float sin(int degAngle){return sine[ (degAngle+360)%360 ];}

	public static final  float cos(float radAngle){	return cos((int)(radAngle*TODEGREES) );}

	public static final float cos(int degAngle){return cosine[ (degAngle+360)%360 ];}

	public static final float atan2(float y, float x){return (float)Math.atan2(y, x);}

	private static float norm2=0;
	private static int i=0;
	public static final float norm(float vec[]){
		norm2=0.f;
		for(i=0; i<vec.length; i++)
			norm2+=pow(vec[i], 2);
		return sqrt(norm2);
	}

	private static float ret;
	public static final float randomFloat(float min, float max) {
		if (min==max) return min;
		ret = min;
		min = min(ret, max);
		max = max(ret, max);
		ret = rand.nextFloat()*(0.0001f+max-min)+min;
		return (ret>max) ? max : ret;
	}

	public static final int randomInt(int min, int max) {
		if(min>max) throw new IllegalArgumentException("min must be less or equal than max ("+min+"<="+max+")");
		if(min==max)
			return min;
		int n = 1+max-min;
		return rand.nextInt(n)+min;
	}

	public static final boolean randomChance(){
		return rand.nextBoolean();
	}

	public static final boolean randomChance(float percentage) {
		return randomFloat(0f, 1f)<percentage;
	}

	public static final float dist(float x1, float y1, float x2, float y2) {
		return sqrt(pow(x1-x2,2) + pow(y1-y2,2));
	}

	public static final float dist(float[] v1, float[] v2) {
		return dist(v1[X], v1[Y], v2[X], v2[Y]);
	}

	public static final boolean sameVec(float[] v1, float[] v2) {
		return (v1.length==v2.length) &&
				v1[X]==v2[X] &&
				v1[Y]==v2[Y];
	}

	public static final float min(float... args) {
		float min = args[0];
		for (int i=1; i<args.length; i++)
			min = Math.min(min, args[i]);
		return min;
	}

	public static final float max(float... args) {
		float max = args[0];
		for (int i=1; i<args.length; i++)
			max = Math.max(max, args[i]);
		return max;
	}

	public static final int min(int... args) {
		int min = args[0];
		for (int i=1; i<args.length; i++)
			min = Math.min(min, args[i]);
		return min;
	}

	public static final int max(int... args) {
		int max = args[0];
		for (int i=1; i<args.length; i++)
			max = Math.max(max, args[i]);
		return max;
	}

	public static final float abs(float v) {
		return Math.abs(v);
	}

	public static final float sign(float v) {
		return v>0 ? 1 : (v<0 ? -1 : 0);
	}

	public static final float diff(float d, float v) {
		return abs(d-v);
	}

	public static final boolean between(int n, int min, int max) {
		return min<=n && n<=max;
	}

	public static final boolean between_excl(int n, float min, float max, boolean exclMin, boolean exclMax) {
		return (min<n || (!exclMin && min==n)) && (n<=max || (!exclMax && max==n));
	}

	public static final boolean between(float n, float min, float max) {
		return min<=n && n<=max;
	}

	public static final boolean between_excl(float n, float min, float max, boolean exclMin, boolean exclMax) {
		return (min<n || (!exclMin && min==n)) && (n<=max || (!exclMax && max==n));
	}

	public static final float readFloats_xml(int base, int digits){
		return (base*1F)/(pow(10F, digits));
	}
}
