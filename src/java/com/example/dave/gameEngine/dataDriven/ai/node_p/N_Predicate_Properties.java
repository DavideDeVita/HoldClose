package com.example.dave.gameEngine.dataDriven.ai.node_p;
import com.example.dave.gameEngine.dataDriven.PropertyException;

public abstract class N_Predicate_Properties extends DTNode_Properties {
	public DTNode_Properties onTrue=null, onFalse=null;

	@Override
	public void reset() {
		onTrue = null;
		onFalse = null;
	}

	protected void copyInto(N_Predicate_Properties into){
		into.onTrue = onTrue.clone();
		into.onFalse = onFalse.clone();
	}

	@Override
	public boolean isReady() {
		return (onTrue!=null && onTrue.isReady()) &&
				(onFalse!=null && onFalse.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="N_Predicate_Properties not ready:";
			if(onTrue==null)              msg+="\n\tonTrue is null";
			else if(!onTrue.isReady())    msg+="\n\t"+onTrue.getErrors().getMessage();
			if(onFalse==null)             msg+="\n\tonFalse is null";
			else if(!onFalse.isReady())   msg+="\n\t"+onFalse.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}
}
