package com.example.dave.gameEngine.dataStructures;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**A fetcher of a certain type T from a Source S*/
public interface Fetcher<S, T> {
	@Nullable
	T fetch(@NonNull S from);
}
