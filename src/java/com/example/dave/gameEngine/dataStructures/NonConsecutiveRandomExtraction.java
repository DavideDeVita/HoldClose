package com.example.dave.gameEngine.dataStructures;

import com.example.dave.gameEngine.MyMath;

import java.util.LinkedList;
import java.util.Queue;

public class NonConsecutiveRandomExtraction<X> extends RandomExtractor<X> {
	private final int excludedLast;
	private int actuallyExcluded=0, i;
	private final boolean[] excluded;
	private final Queue<Integer> queue;

	public NonConsecutiveRandomExtraction(X[] options, int excludedLast) {
		super(options);
		this.excludedLast=excludedLast;
		queue = new LinkedList<>();
		excluded = new boolean[options.length];
	}

	@Override
	public RandomExtractor<X> clone() {
		return new NonConsecutiveRandomExtraction<>(options.clone(), excludedLast);
	}

	@Override
	public X extract() {
		lastIndexExtracted = MyMath.randomInt(0, options.length-1-actuallyExcluded);
		for(i=0; i<= lastIndexExtracted; i++){
			if(excluded[i])
				lastIndexExtracted++;
		}
		if(actuallyExcluded==excludedLast){
			excluded[queue.poll()]=false;
			queue.add(lastIndexExtracted);
			excluded[lastIndexExtracted]=true;
		}
		else{
			queue.add(lastIndexExtracted);
			actuallyExcluded++;
			excluded[lastIndexExtracted]=true;
		}
		return options[lastIndexExtracted];
	}
}