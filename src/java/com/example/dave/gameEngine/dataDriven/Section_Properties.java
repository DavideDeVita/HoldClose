package com.example.dave.gameEngine.dataDriven;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.myMultimedia.MyBackgrounds;
import com.google.fpl.liquidfun.Vec2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Section_Properties extends Properties<Section_Properties> {
	public Box physical, damageFreeZone;
	public Box[] touchBoxes;
	public Vec2 gravity=null;
	public float forwardUpdate_x =0.75f, forwardUpdate_y =0.75f;
	public Float backwardUpdate_x =null, backwardUpdate_y =null;
	public Float viewWidth =null, viewHeight =null;
	public GameObj_Properties mainObj=null;
	public List<GameObj_Properties> gObjects = null;
	public Enclosement enclosement=null;
	public Floor floor=null;
	public Rampage[] rampages =null;
	public PeriodicSpawnGameObj_Properties[] spawns_p =null;
	public ZoneFill_Properties[] zoneFills = null;
	public MyBackgrounds background=null;
	//
	private static final Pool<Section_Properties> sectionsPool = new Pool<>(
			new Pool.PoolObjectFactory<Section_Properties>() {
				@Override
				public Section_Properties createObject() {
					return new Section_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.Section_Properties_Pool_size)
	);

	private Section_Properties(){
		super(sectionsPool);
	}

	private static <X extends Properties<X>>List<X> cloneList(List<X> list){
		if(list==null) return null;
		List<X> ret = new ArrayList<>(list.size());
		for (X x : list)
			ret.add(x.clone());
		return ret;
	}

	private static <X extends Properties<X>>X[] cloneArray(X[] list){
		if(list==null) return null;
		X[] ret = list.clone();
		int i=0;
		for (X x : list)
			ret[i++]=(x.clone());
		return ret;
	}

	public static Section_Properties _new() {
		return sectionsPool.newObject();
	}

	@Override
	public void free() {
		enclosement.free();
		int i;
		if(rampages!=null) {
			for (i = 0; i < rampages.length; i++)
				rampages[i].free();
		}
		/*if(spawns_p!=null) {
			for (i = 0; i < spawns_p.length; i++)
				spawns_p[i].free();
		}*/ // Must not free here.. used in Game Section
		if(zoneFills!=null) {
			for (i = 0; i < zoneFills.length; i++)
				zoneFills[i].free();
		}
		if(mainObj!=null)
			mainObj.free();
		if(gObjects!=null) {
			final Iterator<GameObj_Properties> iterator = gObjects.iterator();
			for (i = 0; i < gObjects.size(); i++)
				iterator.next().free();
		}
		super.free();
	}

	@Override
	public void reset() {
		this.background=null;
		this.physical=null; this.damageFreeZone=null; this.touchBoxes =null;
		this.gravity=null;
		this.forwardUpdate_x =0.75f;
		this.backwardUpdate_x =null;
		this.forwardUpdate_y =0.75f;
		this.backwardUpdate_y =null;
		this.viewWidth=null;
		this.viewHeight=null;
		this.mainObj=null;
		this.enclosement = null;
		if(this.gObjects!=null) this.gObjects.clear();
		this.gObjects = null;
		this.floor=null;
		this.rampages =null; //is array
		this.spawns_p =null;
		this.zoneFills = null;
	}

	@Override
	public Section_Properties clone() {
		Section_Properties newInstance = _new();
		newInstance.background=background;
		newInstance.physical=physical;
			newInstance.damageFreeZone=damageFreeZone;
			if(touchBoxes!=null)
			newInstance.touchBoxes = touchBoxes.clone();
		newInstance.gravity=gravity;
		newInstance.forwardUpdate_x = forwardUpdate_x;
		newInstance.backwardUpdate_x = backwardUpdate_x;
		newInstance.forwardUpdate_y = forwardUpdate_y;
		newInstance.backwardUpdate_y = backwardUpdate_y;
		newInstance.viewWidth=viewWidth;
		newInstance.viewHeight=viewHeight;
		if(mainObj!=null)
			newInstance.mainObj=mainObj.clone();
		if(gObjects!=null)
			newInstance.gObjects=cloneList(gObjects);
		if(enclosement!=null)
			newInstance.enclosement=enclosement.clone();
		if(floor!=null)
			newInstance.floor=floor.clone();
		if(rampages!=null)
			newInstance.rampages =cloneArray(rampages);
		if(spawns_p!=null)
			newInstance.spawns_p = cloneArray(spawns_p);
		if(zoneFills!=null)
			newInstance.zoneFills = cloneArray(zoneFills);
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return background!=null &&
				physical!=null &&
				forwardUpdate_x >0 && forwardUpdate_x <1 &&
				(backwardUpdate_x ==null || (backwardUpdate_x >0 && backwardUpdate_x < forwardUpdate_x) ) &&
				forwardUpdate_y >0 && forwardUpdate_y <1 &&
				(backwardUpdate_y ==null || (backwardUpdate_y >0 && backwardUpdate_y < forwardUpdate_y) ) &&
				(viewWidth!=null || viewHeight!=null) &&
				(enclosement==null || enclosement.isReady()) &&
				(floor==null || floor.isReady())
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Section_properties not ready:";
			if(background==null) msg+="\n\tbackground is null";
			if(physical==null) msg+="\n\tphysical is null";
			if(forwardUpdate_x <=0 || forwardUpdate_x >=1 ) msg+="\n\tforwardUpdate_x is outside ]0; 1[";
			if(backwardUpdate_x !=null && (backwardUpdate_x <=0 || backwardUpdate_x >= forwardUpdate_x) ) msg+="\n\tbackwardUpdate_x is outside ]0; forwardUpdate[";
			if(forwardUpdate_y <=0 || forwardUpdate_y >=1 ) msg+="\n\tforwardUpdate_y is outside ]0; 1[";
			if(backwardUpdate_y !=null && (backwardUpdate_y <=0 || backwardUpdate_y >= forwardUpdate_y) ) msg+="\n\tbackwardUpdate_y is outside ]0; forwardUpdate[";
			if(viewWidth==null && viewHeight==null) msg+="\nBoth fixed Width and Height for view are null";
			if(enclosement!=null && !enclosement.isReady()) msg+="\n\t"+enclosement.getErrors().getMessage();
			if(floor!=null && !floor.isReady()) msg+="\n\t"+floor.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}

	//Inner classes
	public static class Enclosement extends Properties<Enclosement> {
		public boolean constant0_percentage1;
		public float engorgement;
		public GameObject.OnHitBehaviour oHB;
		//
		private static final Pool<Enclosement> enclosementPool = new Pool<>(
				new Pool.PoolObjectFactory<Enclosement>() {
					@Override
					public Enclosement createObject() {
						return new Enclosement();
					}
				},
				MainActivity.readIntOnDemand(R.integer.Enclosement_Pool_size)
		);

		private Enclosement() {
			super(enclosementPool);
		}

		public static Enclosement _new() {
			return enclosementPool.newObject();
		}

		@Override
		public void reset() {
			oHB = null;
			constant0_percentage1=false;
			engorgement=0.f;
		}

		@Override
		public Enclosement clone() {
			Enclosement newInstance = _new();
			newInstance.oHB = oHB;
			newInstance.constant0_percentage1=constant0_percentage1;
			newInstance.engorgement=engorgement;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public PropertyException getErrors() {
			return null;
		}
	}
	public static class Rampage extends Properties<Rampage> {
		public GameObj_Properties obj=null;
		public float baseY, fromX, toX, angle;
		public boolean verticalSustain=true, horizontalSustain=false;
		//
		private static final Pool<Rampage> rampagesPool = new Pool<>(
				new Pool.PoolObjectFactory<Rampage>() {
					@Override
					public Rampage createObject() {
						return new Rampage();
					}
				},
				MainActivity.readIntOnDemand(R.integer.Rampage_Properties_Pool_size)
		);

		private Rampage(){
			super(rampagesPool);
		}

		public static Rampage _new() {
			return rampagesPool.newObject();
		}

		@Override
		public void free() {
			if(obj!=null)
				obj.free();
			super.free();
		}

		@Override
		public void reset() {
			obj=null;
			baseY = fromX = toX =angle=0f;
			verticalSustain=true;
			horizontalSustain=false;
		}

		@Override
		public Rampage clone() {
			Rampage newInstance = _new();
			newInstance.obj=obj.clone();
			newInstance.baseY = baseY;
			newInstance.fromX = fromX;
			newInstance.toX = toX;
			newInstance.angle=angle;
			newInstance.verticalSustain=verticalSustain;
			newInstance.horizontalSustain=horizontalSustain;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return obj!=null && obj.phy_p!=null;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="Rampage not ready:";
				if(obj==null) msg+="\n\tobj is null";
				else if(obj.phy_p==null) msg+="\n\tobj.phy_p is null";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class Floor extends Properties<Floor> {
		public GameObj_Properties std=null, touchable=null, end=null;
		public Box endBox=null;
		public int damageSkip = 0;
		//
		private static final Pool<Floor> floorsPool = new Pool<>(
				new Pool.PoolObjectFactory<Floor>() {
					@Override
					public Floor createObject() {
						return new Floor();
					}
				},
				MainActivity.readIntOnDemand(R.integer.Floor_Properties_Pool_size)
		);

		private Floor(){
			super(floorsPool);
		}

		public static Floor _new() {
			return floorsPool.newObject();
		}

		@Override
		public void free() {
			std.free();
			touchable.free();
			end.free();
			super.free();
		}

		@Override
		public void reset() {
			std = null;
			touchable = null;
			end = null;
			endBox = null;
			damageSkip =0;
		}

		@Override
		public Floor clone() {
			Floor floor = _new();
			floor.std = std.clone();
			floor.touchable = touchable.clone();
			floor.end = end.clone();
			floor.endBox = endBox;
			floor.damageSkip = damageSkip;
			return floor;
		}

		@Override
		public boolean isReady() {
			return damageSkip >=0 &&
					(std!=null && std.isReady()) &&
					(touchable!=null && touchable.isReady()) &&
					(end!=null && end.isReady())
					;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="Floor not ready:";
				if(damageSkip <0) msg+="\n\talternating damage floor is negative";
				if(std==null) msg+="\n\tstd game Object properties is null";
				else if(!std.isReady()) msg+="\n\t"+std.getErrors().getMessage();
				if(touchable==null) msg+="\n\ttouchable game Object properties is null";
				else if(!touchable.isReady()) msg+="\n\t"+touchable.getErrors().getMessage();
				if(end==null) msg+="\n\tend game Object properties is null";
				else if(!end.isReady()) msg+="\n\t"+end.getErrors().getMessage();
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class ZoneFill_Properties extends Properties<ZoneFill_Properties> {
		public Box zone=null;
		public int howMany=0, howManyAesthetic=0;
		public boolean isAesthetic;
		public RandomExtractor_Properties<GameObj_Properties> options=null;//
		private static final Pool<ZoneFill_Properties> zoneFillPool = new Pool<>(
				new Pool.PoolObjectFactory<ZoneFill_Properties>() {
					@Override
					public ZoneFill_Properties createObject() {
						return new ZoneFill_Properties();
					}
				},
				MainActivity.readIntOnDemand(R.integer.ZoneFill_Properties_Pool_size)
		);

		private ZoneFill_Properties(){
			super(zoneFillPool);
		}

		public static ZoneFill_Properties _new() {
			return zoneFillPool.newObject();
		}

		@Override
		public void reset() {
			zone=null;
			howMany = howManyAesthetic = 0;
			options=null;
			isAesthetic=false;
		}

		@Override
		public ZoneFill_Properties clone() {
			ZoneFill_Properties newInstance = _new();
			newInstance.howMany=howMany;
			newInstance.howManyAesthetic=howManyAesthetic;
			newInstance.zone=zone;
			newInstance.isAesthetic=isAesthetic;
			if(options!=null)
				newInstance.options=options.clone();
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return (zone.width>0 && zone.height>0) &&
					howMany>0 && howManyAesthetic>=0 &&
					(options!=null && options.isReady());
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="ZoneFill not ready:";
				if(zone.width<=0) msg+="\n\tzone not ready: width is zero or less";
				if(zone.height<=0) msg+="\n\tzone not ready: height is zero or less";
				if(howMany<=0) msg+="\n\tzero or less ("+howMany+") object should spawn";
				if(howManyAesthetic<0) msg+="\n\tnegative value for howManyAesthetic ("+howManyAesthetic+")";
				if(options==null) msg+="\n\toptions is null";
				else if(!options.isReady()) msg+="\n\t"+options.getErrors().getMessage();
				return new PropertyException(msg);
			}
			return null;
		}
	}
}