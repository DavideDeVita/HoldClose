package com.example.dave.gameEngine.dataStructures;

/**Just like they do in Dorset*/
public interface PooleOriented<X> {
	X getFromPoole();

	void sendToPoole();

	void free();
}
