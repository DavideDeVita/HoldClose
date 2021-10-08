package com.example.dave.gameEngine.dataDriven.ai;

import androidx.annotation.Nullable;

public interface Buildable<T> {
	T build(@Nullable Object caller);
}
