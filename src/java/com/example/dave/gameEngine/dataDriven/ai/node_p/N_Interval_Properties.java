package com.example.dave.gameEngine.dataDriven.ai.node_p;

import com.example.dave.gameEngine.ai.decisionTree.N_Interval;
import com.example.dave.gameEngine.ai.decisionTree.Node;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.field_p.Field_Properties;

public class N_Interval_Properties extends N_Predicate_Properties {
	public Float min=null, max=null;
	public Field_Properties<Float> field=null;

	@Override
	public void reset() {
		super.reset();
		min = max = null;
		field = null;
	}

	@Override
	public DTNode_Properties clone() {
		N_Interval_Properties newInstance = new N_Interval_Properties();
		super.copyInto(newInstance);
		newInstance.min = min;
		newInstance.max = max;
		newInstance.field = field.clone();
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return super.isReady() &&
				(field!=null && field.isReady()) &&
				(min!=null || max!=null)
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="N_Interval_Properties not ready:";
			if(!super.isReady())   msg+="\n\t"+super.getErrors().getMessage();
			if(min==null && max==null)    msg+="\n\tboth min and max are null";
			if(field==null)               msg+="\n\tfield is null";
			else if(!field.isReady())     msg+="\n\t"+field.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public Node build(Object caller) {
		return new N_Interval(onTrue.build(caller), onFalse.build(caller), min, max, field.build(caller));
	}
}
