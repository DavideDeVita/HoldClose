package com.example.dave.gameEngine.dataDriven.component;

import android.util.ArrayMap;
import android.util.Pair;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import static com.example.dave.gameEngine.entity_component.AnimationUpdateCondition.*;
import static com.example.dave.gameEngine.entity_component.AnimationUpdateCondition.UpdateCondition.Time;
import static com.example.dave.gameEngine.entity_component.Animation_Cmpnt.*;
import com.example.dave.gameEngine.myMultimedia.Spritesheets;

import java.util.Map;

public class Animation_Properties extends Properties<Animation_Properties> {
	public int delayMillis;
	public AnimationUpdate_Properties updateOn_p;
	private UpdateCondition updateOn=Time;
	public SheetUpdate_Properties sheetUpdate_p;
	private SheetType sheetType;

	//
	private static final Pool<Animation_Properties> animationPool = new Pool<>(
			new Pool.PoolObjectFactory<Animation_Properties>() {
				@Override
				public Animation_Properties createObject() {
					return new Animation_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.animation_Properties_Pool_size)
	);
	private Animation_Properties(){
		super(animationPool);
	}

	public static Animation_Properties _new() {
		return animationPool.newObject();
	}

	public UpdateCondition getUpdateCondition() {
		return updateOn;
	}

	public void setUpdateCondition(UpdateCondition updateCondition) {
		this.updateOn=updateCondition;
		switch(updateOn){
			case Time:
				updateOn_p = null;
				break;
			case Direction:
				updateOn_p = new AnimationUpdate_Properties.UpdateOnDirection_Properties();
				break;
			case Random:
				updateOn_p = new AnimationUpdate_Properties.UpdateRandomly_Properties();
				break;
			default:
				updateOn_p=null;
		}
	}

	public SheetType getSheetType() {
		return sheetType;
	}

	public void setSheetType(SheetType sheetType) {
		this.sheetType = sheetType;
		switch(sheetType){
			case Single:
				sheetUpdate_p = new SingleSheet_Properties();
				break;
			case Multiple:
				sheetUpdate_p = new MultipleSheet_Properties();
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + sheetType);
		}
	}

	@Override
	public void reset() {
		delayMillis =0;
		if(updateOn_p!=null)
			updateOn_p.free();
		updateOn_p=null;
		updateOn= Time;
		if(sheetUpdate_p!=null)
			sheetUpdate_p.free();
		sheetUpdate_p=null;
		sheetType=null;
	}

	@Override
	public Animation_Properties clone() {
		Animation_Properties newInstance = _new();
		if(updateOn_p!=null)
			newInstance.updateOn_p = updateOn_p.clone();
		else
			newInstance.updateOn_p=null;
		newInstance.updateOn=updateOn;
		//if(sheets!=null)
		//	newInstance.sheets = sheets.clone();
		newInstance.delayMillis = delayMillis;
		newInstance.sheetType=sheetType;
		if(sheetUpdate_p!=null)
			newInstance.sheetUpdate_p = sheetUpdate_p.clone();
		else
			newInstance.sheetUpdate_p=null;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return updateOn!=null &&
				(updateOn_p!=null || updateOn==Time) &&
				( updateOn_p!=null && updateOn_p.isReady())  &&
				( sheetType!=null && sheetUpdate_p!=null && sheetUpdate_p.isReady())  &&
				delayMillis >0;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Animation_Properties not ready:";
			if(updateOn==null)                              msg+="\nupdateOn is null";
			if( updateOn_p==null && updateOn!=Time)         msg+="\nupdateOn_p is null while updateOn is not Time";
			if( updateOn_p!=null && !updateOn_p.isReady())         msg+="\n"+updateOn_p.getErrors().getMessage();
			if( sheetType==null )         msg+="\nsheet type is null";
			else if( sheetUpdate_p==null )         msg+="\nsheet update is null";
			else if( !sheetUpdate_p.isReady() )         msg+="\n"+sheetUpdate_p.getErrors().getMessage();
			if(delayMillis <=0)                             msg+="\ndelay <=0 :"+ delayMillis;
			return new PropertyException(msg);
		}
		return null;
	}

	public static abstract class SheetUpdate_Properties extends Properties<SheetUpdate_Properties>{
		protected SheetUpdate_Properties() {
			super(null);
		}
	}
	public static class SingleSheet_Properties extends SheetUpdate_Properties {
		//public RandomExtractor<Spritesheets> sheets;
		public Spritesheets sheets;

		@Override
		public void reset() {
			sheets = null;
		}

		@Override
		public SingleSheet_Properties clone() {
			SingleSheet_Properties newInstance = new SingleSheet_Properties();
			newInstance.sheets = sheets;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return sheets != null;
		}

		@Override
		public PropertyException getErrors() {
			if (!isReady()) {
				String msg = "SingleSheet_Properties not ready:";
				if (sheets == null) msg += "\nsheet is null";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class MultipleSheet_Properties extends SheetUpdate_Properties {
		public Spritesheets sheets[];
		//Technically.. should be Map<String, Map<Object, Integer>>.. but let's make it easier
		private Map<Pair<String, ?>, Integer> conditions;

		public void addCondition(Pair<String, ?> value, int setSheet){
			if(conditions==null)
				conditions=new ArrayMap<>();
			conditions.put(value, setSheet);
		}

		@Override
		public void reset() {
			sheets = null;
		}

		@Override
		public MultipleSheet_Properties clone() {
			MultipleSheet_Properties newInstance = new MultipleSheet_Properties();
			newInstance.sheets = sheets.clone();
			newInstance.conditions=new ArrayMap<>();
			copyConditionsInto(newInstance.conditions);
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return sheets != null && sheets.length>1
					;
		}

		@Override
		public PropertyException getErrors() {
			if (!isReady()) {
				String msg = "MultipleSheet_Properties not ready:";
				if (sheets == null) msg += "\nsheet is null";
				else if(sheets.length<2)    msg+="\nless that 2 sheets";
				return new PropertyException(msg);
			}
			return null;
		}

		public void copyConditionsInto(Map<Pair<String, ?>, Integer> copyInto) {
			for (Map.Entry<Pair<String, ?>, Integer> entry : conditions.entrySet())
				copyInto.put(entry.getKey(), entry.getValue());
		}
	}
}
