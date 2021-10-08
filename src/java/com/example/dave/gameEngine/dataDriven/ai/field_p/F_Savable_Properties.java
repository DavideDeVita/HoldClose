package com.example.dave.gameEngine.dataDriven.ai.field_p;

public abstract class F_Savable_Properties<F> extends Field_Properties<F> {
	public String saveAs=null;

	@Override
	public void reset() {
		saveAs=null;
	}

	public void copyInto(F_Savable_Properties<F> into){
		into.saveAs = saveAs;
	}
}
