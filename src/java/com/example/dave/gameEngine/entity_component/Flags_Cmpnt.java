package com.example.dave.gameEngine.entity_component;

import android.util.ArrayMap;

import java.util.EnumSet;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import com.example.dave.gameEngine.dataDriven.component.Flag_Properties;
import com.example.dave.gameEngine.dataStructures.MapSet;


public class Flags_Cmpnt extends Component {
	private final Map<String, Object> flags = new ArrayMap<>();
	private final MapSet<String, ComponentType> toNotify;

	public Flags_Cmpnt(Entity owner, Flag_Properties flag_p) {
		super(owner);
		flag_p.copyMapInto(flag_p.flags, flags);
		if(flag_p.observers!=null){
			toNotify = flag_p.observers.clone();
			Set<ComponentType> interested;
			for (Map.Entry<String, Object> entry : this.flags.entrySet()) {
				interested = toNotify.get(entry.getKey());
				if (interested != null)
					for (ComponentType type : interested)
						if (owner.hasComponent(type))
							owner.getComponent(type).notify(entry.getKey(), entry.getValue());
			}
		}
		else toNotify=null;
	}

	private Flags_Cmpnt(Entity owner) {
		super(owner);
		toNotify=null;
	}

	public void setFlag(String name, Object value){
		flags.put(name, value);
		if(toNotify!=null){
			Set<ComponentType> interested = toNotify.get(name);
			if(interested!=null)
				for(ComponentType type : interested)
					if(owner.hasComponent(type))
					owner.getComponent(type).notify(name, value);
		}
	}

	public Object getFlag(String name){
		//ret = flags.get(name);
		return flags.get(name);
		//return (ret==null) ? MainActivity.defaultMissingFlag : ret;
	}

	@Override
	public ComponentType type() {
		return ComponentType.Flags;
	}

	@Override
	public void clear() {
		flags.clear();
	}

	public Flags_Cmpnt shareFlags(Entity with){
		Flags_Cmpnt ret = new Flags_Cmpnt(with);
		for (Map.Entry<String, Object> entry : this.flags.entrySet())
			ret.flags.put(entry.getKey(), entry.getValue());
		return ret;
	}
}
