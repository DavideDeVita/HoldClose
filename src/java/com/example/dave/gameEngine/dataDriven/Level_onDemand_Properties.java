package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.parser.xml.XML_Element;

public class
Level_onDemand_Properties extends Level_Properties {
	public XML_Element[] sec_p = null;
	public Section_Properties common_p;
	public String name;

	public final static XML_Element[] _0 = new XML_Element[0];
	//
	private static final Pool<Level_Properties> lvlPool = new Pool<>(
			new Pool.PoolObjectFactory<Level_Properties>() {
				@Override
				public Level_onDemand_Properties createObject() {
					return new Level_onDemand_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.Lvl_Properties_Pool_size)
	);

	private Level_onDemand_Properties(){
		super(lvlPool);
	}

	@Override
	public void free() {
		if(storyAnte!=null)     storyAnte.free();
		if(storyPost!=null)     storyPost.free();
		if(common_p!=null)      common_p.free();
		//Sections freed in Level Builder
		super.free();
	}

	public static Level_onDemand_Properties _new() {
		return (Level_onDemand_Properties)lvlPool.newObject();
	}

	@Override
	public void reset() {
		super.reset();
		name = null;
		if(common_p!=null)
			common_p.reset();
		common_p=null;
		sec_p = null;
	}

	@Override
	public Level_onDemand_Properties clone() {
		Level_onDemand_Properties lvl = _new();
		copyInto(lvl);
		lvl.sec_p = sec_p.clone();
		if(common_p!=null)
			lvl.common_p=common_p.clone();
		lvl.name=name;
		return lvl;
	}

	@Override
	public boolean isReady() {
		return (sec_p!=null && sec_p.length > 0) &&
				(super.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if (!isReady()) {
			String msg = "Lvl_properties not ready:";
			if(!super.isReady())    msg+="\n"+super.getErrors().getMessage();
			if (sec_p==null ) msg += "\nsections is null";
			else if (sec_p.length<=0 ) msg += "\nNo sections";
			return new PropertyException(msg);
		}
		return null;
	}

	@Override
	public boolean hasNthSection(int n) {
		return 0 <= n && n < sec_p.length;
	}
}
