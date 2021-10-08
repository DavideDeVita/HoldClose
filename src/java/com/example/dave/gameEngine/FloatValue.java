package com.example.dave.gameEngine;

import androidx.annotation.NonNull;

public class FloatValue {
	private final Float val1, val2;

	public FloatValue(@NonNull float val1, @NonNull Float val2){
		this.val1 = val1;
		this.val2 = val2;

	}

	public FloatValue(@NonNull float value){
		this(value, null);
	}

	public float get(){
		if(val2 ==null)
			return val1;
		else
			return MyMath.randomFloat(val1, val2);
	}

	@NonNull @Override
	public String toString() {
		return (val2==null) ? val1+"" : "["+val1+", "+val2+"]";
	}

	//simple relations
	public boolean gte(float val){ return val1>=val; }
	public boolean greater(float val){ return val1>val; }
	public boolean leq(float val){ return (val2==null) ? val1<=val : val2<=val; }
	public boolean less(float val){ return (val2==null) ? val1<val : val2<val; }
	public boolean between(float min, float max){
		if(val2==null)
			return MyMath.between(val1, min, max);
		else return val1>=min && val2<=max;
	}
	public boolean between_excl(float min, float max, boolean exclMin, boolean exclMax){
		if(val2==null)
			return MyMath.between(val1, min, max);
		else
			return (min<val1 || (!exclMin && min==val1)) && (val2<=max || (!exclMax && max==val2));
	}

	public final static FloatValue _0f = new FloatValue(0f),
									_1f = new FloatValue(1f);
}
