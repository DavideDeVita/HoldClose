package com.example.dave.gameEngine.dataStructures;

import androidx.annotation.NonNull;

import com.example.dave.gameEngine.MyMath;

public class RandomExtractor<X> {
	protected final X[] options;

	protected int lastIndexExtracted =-1;//Allocated once

	public RandomExtractor(X[] options){
		this.options = options.clone();
	}
	public X extract(){
		lastIndexExtracted = MyMath.randomInt(0, options.length-1);
		return options[lastIndexExtracted];
	}

	protected int getLastIndexExtracted(){
		return lastIndexExtracted;
	}

	protected int getNumberOfOptions(){
		return options.length-1;
	}

	public RandomExtractor<X> clone(){
		return new RandomExtractor<>(options.clone());
	}

	public void free() {
		if(options[0] instanceof PooleOriented) {
			//_Log.i("RandomExtractor", "is of PoolOriented");
			for (int i = 0; i < options.length; i++)
				((PooleOriented) options[i]).free();
		}
	}

	@NonNull
	@Override
	public String toString() {
		String ret = "";
		for(X x : options)
			ret+= x+"  ";
		return ret;
	}
}