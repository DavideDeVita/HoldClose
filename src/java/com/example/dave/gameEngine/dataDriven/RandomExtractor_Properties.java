package com.example.dave.gameEngine.dataDriven;

import androidx.annotation.Nullable;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataStructures.NonConsecutiveRandomExtraction;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;

public class RandomExtractor_Properties<X> extends Properties<RandomExtractor_Properties<X>> implements Buildable<RandomExtractor<X>> {
	public X[] options;
	public int excludeLast = 0;

	public RandomExtractor_Properties(){
		super(null);
	}

	@Override
	public void reset() {
		options = null;
		excludeLast = 0;
	}

	@Override
	public RandomExtractor_Properties<X> clone() {
		RandomExtractor_Properties newInstance = new RandomExtractor_Properties<>();
		newInstance.options = options.clone();
		newInstance.excludeLast = excludeLast;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return (options != null && options.length >= 1) &&
				excludeLast >= 0
				;
	}

	@Override
	public PropertyException getErrors() {
		if (!isReady()) {
			String msg = "RandomExtractor_Properties not ready:";
			if (options == null) msg += "\ngObjOptions is null";
			else if (options.length < 1) msg += "\nless than 1 gObjOption";
			if (excludeLast < 0)
				msg += "\nexcluding last \"n\" called options, but \"n\" is < 0: " + excludeLast;
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public RandomExtractor<X> build(@Nullable Object caller) {
		RandomExtractor<X> extractor;
		if (excludeLast == 0)
			extractor = new RandomExtractor<>(options);
		else
			extractor = new NonConsecutiveRandomExtraction<>(options, excludeLast);
		return extractor;
	}
}