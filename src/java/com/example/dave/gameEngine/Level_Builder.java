package com.example.dave.gameEngine;

import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataDriven.Level_FullLoaded_Properties;
import com.example.dave.gameEngine.dataDriven.Level_Properties;
import com.example.dave.gameEngine.dataDriven.Level_onDemand_Properties;
import com.example.dave.gameEngine.dataDriven.parser.Credits_Properties;
import com.example.dave.gameEngine.dataDriven.parser.MyParseException;
import com.example.dave.gameEngine.dataDriven.parser.xml.XML_Parser;
import com.example.dave.gameEngine.dataDriven.Section_Properties;
import com.example.dave.gameEngine.dataStructures.PeriodicSpawnGameObj;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine.entity_component.GameObject;

import java.io.InputStream;

class Level_Builder {
	static Credits loadCredits(String levelName) {
		InputStream inputStream = MainActivity.thisActivity.getResources().openRawResource(R.raw.lvl_properties_std);
		try {
			Credits_Properties credits_p = XML_Parser.getInstance().parseCreditsProperties(inputStream, levelName);
			return new Credits(credits_p);
		} catch (MyParseException e) {
			_Log.e(e);
		}
		return null;
	}

	static GameLevel loadLevel(String levelName) {
		InputStream inputStream = MainActivity.thisActivity.getResources().openRawResource(R.raw.lvl_properties_std);
		try {
			Level_Properties lvl = XML_Parser.getInstance().parseLevelProperties(inputStream, levelName);
			return new GameLevel(lvl);
		} catch (MyParseException e) {
			_Log.e(e);
		}
		return null;
	}

	/*Usual level features*/
	private static void createFloor(GameSection gs, Section_Properties.Floor floor_p){
		class FloorTypes{
			final GameObj_Properties std, touchable, end, damageFreeStd, damageFreeTouchable, damageFreeEnd;

			private FloorTypes(GameObj_Properties std, GameObj_Properties touch, GameObj_Properties end){
				this.std = std;
				this.touchable=touch;
				this.end=end;
					this.end.oHB = GameObject.OnHitBehaviour.Endgame;
					//Damage free should be Non Wet
				this.damageFreeStd = std.clone();
					damageFreeStd.health_p=null;
				this.damageFreeTouchable = touch.clone();
					damageFreeTouchable.health_p=null;
				this.damageFreeEnd = this.end.clone();
					damageFreeEnd.health_p=null;
			}
		}

		Box phys=gs.physicalBox, end=null;
		MultiZone touch=gs.touchZone;
		if(floor_p.endBox!=null){
			if(gs.damageFreeZone!=null)
				end = new SubBox(floor_p.endBox, gs.damageFreeZone);
			else
				end = new SubBox(floor_p.endBox, gs.physicalBox);
		}

		float halfStepX = floor_p.std.phy_p.dimensionX/2, halfStepY = floor_p.std.phy_p.dimensionY/2;
		float y = phys.ymin + halfStepY/5;

		FloorTypes floorTypes = new FloorTypes(floor_p.std, floor_p.touchable, floor_p.end);

		int i=0;
		for(float x = phys.xmin+halfStepX; x<phys.xmax/*-halfStepX*/; x+=floor_p.std.phy_p.dimensionX){
			if(i%(floor_p.damageSkip+1) ==0){
				if(end!=null && end.contains(x, y))
					gs.createGameObject(  floorTypes.end, x, y);
				else if(touch.contains(x, y))
					gs.createGameObject(  floorTypes.touchable, x, y);
				else
					gs.createGameObject(  floorTypes.std, x, y);
			}
			else{
				if(end!=null && end.contains(x, y))
					gs.createGameObject(  floorTypes.damageFreeEnd, x, y);
				else if(touch.contains(x, y))
					gs.createGameObject(  floorTypes.damageFreeTouchable, x, y);
				else
					gs.createGameObject(  floorTypes.damageFreeStd, x, y);
			}
			i++;
		}
	}

	private static void createRampage(GameSection gw, Section_Properties.Rampage rampage){
		if(rampage.fromX>rampage.toX)
			rampage.angle = 180-rampage.angle; //Other direction
		float radAngle = MyMath.toRadians(rampage.angle);
		float length = MyMath.abs((rampage.toX -rampage.fromX)/ MyMath.cos(radAngle));
		float toY = length * MyMath.sin(radAngle) + rampage.baseY;
		float cy=(rampage.baseY +toY)/2, cx=(rampage.toX +rampage.fromX)/2;
		rampage.obj.phy_p.angle=rampage.angle;
		rampage.obj.phy_p.dimensionX=length;
		//_Log.i("Level", "rampage dims: "+length+", "+rampage.obj.phy_p.dimensionY);
		//_Log.i("Level", "rampage fromX: "+rampage.fromX+" to "+rampage.toX);
		//_Log.i("Level", "rampage fromY: "+rampage.baseY+" to "+toY);
		//_Log.i("Level", "angle: "+rampage.angle+" cos: "+MyMath.cos(radAngle)+" sin "+MyMath.sin(radAngle));
		//_Log.i("Level", "cx: "+cx+" cy: "+cy);
		gw.addGameObject(GameObject.make(gw, rampage.obj, cx, cy));

		if(rampage.verticalSustain){
			GameObj_Properties verticalSustain = rampage.obj.clone();
			verticalSustain.phy_p.angle=0;
			verticalSustain.phy_p.dimensionX=verticalSustain.phy_p.dimensionY;
			verticalSustain.phy_p.dimensionY=(toY-rampage.baseY);
			gw.addGameObject(GameObject.make(gw, verticalSustain, rampage.toX, cy));
		}

		if(rampage.horizontalSustain){
			GameObj_Properties horizontalSustain = rampage.obj.clone();
			horizontalSustain.phy_p.angle=0;
			horizontalSustain.phy_p.dimensionX=MyMath.abs((rampage.toX - rampage.fromX));
			gw.addGameObject(GameObject.make(gw, horizontalSustain, cx, rampage.baseY));
		}
	}

	private static GameSection loadSection(GameLevel gl, Section_Properties gs_p) {
		GameSection gs = new GameSection(gl, gs_p);
		//Nullable box fixing.. not required anymore

		if (gs_p.enclosement != null)
			gs.addGameObject(GameObject.makeEnclosure(gs, gs_p.physical, gs_p.enclosement));

		if (gs_p.mainObj != null) {
			gs.createMainObject(gs_p.mainObj, null, null);
		}

		if (gs_p.gObjects != null){
			for (GameObj_Properties gObj : gs_p.gObjects){
				if(gObj.isAesthetic && MainActivity.avoidAesthetic) {
					if(_Log.LOG_ACTIVE)
						_Log.w("LevelBuild", "Ignored aesthetic Game Object "+gObj.name);
					continue;
				}
				gs.createGameObject( gObj, null, null );
			}
		}

		if (gs_p.zoneFills != null){
			int howMany;
			for (Section_Properties.ZoneFill_Properties zoneFill : gs_p.zoneFills){
				if(zoneFill.isAesthetic && MainActivity.avoidAesthetic){
					if(_Log.LOG_ACTIVE)
						_Log.w("LevelBuild", "Ignored aesthetic zone fill ");
					continue;
				}
				RandomExtractor<GameObj_Properties> options = zoneFill.options.build(null);
				Box zone = new SubBox(zoneFill.zone, gs.physicalBox);
				howMany = zoneFill.howMany + (MainActivity.avoidAesthetic ? 0 : zoneFill.howManyAesthetic);
				for(int i=0; i<howMany; i++)
					gs.createGameObject( options.extract(), zone.randomX(), zone.randomY() );
			}
		}

		if (gs_p.rampages != null){
			for (Section_Properties.Rampage rampage : gs_p.rampages){
				createRampage(gs, rampage);
			}
		}

		if (gs_p.spawns_p!=null){
			PeriodicSpawnGameObj spawnables[] = new PeriodicSpawnGameObj[gs_p.spawns_p.length];
			for ( int i=0; i<gs_p.spawns_p.length; i++ ){
				spawnables[i] = gs_p.spawns_p[i].build(null);
			}
			gs.setSpawnables(spawnables);
		}

		if (gs_p.floor != null){
			/*if(gs_p.floor.endBox==null) {
				gs_p.floor.endBox = new SubBox(gs_p.damageFreeZone);}
			else{
				gs_p.floor.endBox = new SubBox(gs_p.floor.endBox, gs_p.damageFreeZone);}
				*/
			createFloor(gs, gs_p.floor);
		}

		//if (loadAI) addAI(gs);
		//gs.fullTest();
		//gs.fullTest_iter();
		//gs.test(GameElement.Fire);
		//gs.test_iter(GameElement.Fire);
		gs_p.free();
		return gs;
	}

	private static GameSection loadSection(GameLevel gl, Section_Properties gs_p, float remainingHp){
		gs_p.mainObj.health_p.startHealth= new FloatValue(remainingHp);
		return loadSection(gl, gs_p);
	}

	public static GameSection loadSection(GameLevel gl, Level_Properties level_p, int n_th) {
		if(level_p instanceof Level_onDemand_Properties) {
			try {
				Section_Properties s_p = XML_Parser.getInstance().parseLevelSection((Level_onDemand_Properties) level_p, n_th);
				return loadSection(gl, s_p);
			} catch (MyParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		else if(level_p instanceof Level_FullLoaded_Properties){
			return loadSection(gl, ((Level_FullLoaded_Properties)level_p).sec_p[n_th]);
		}
		throw new IllegalArgumentException("level_p is neither FullLoaded nor onDemand.. what the hell happened? ("+level_p.getClass().getSimpleName()+")");
	}

	public static GameSection loadSection(GameLevel gl, Level_Properties level_p, int n_th, float remainingHp) {
		if(level_p instanceof Level_onDemand_Properties) {
			try {
				Section_Properties s_p = XML_Parser.getInstance().parseLevelSection((Level_onDemand_Properties) level_p, n_th);
				return loadSection(gl, s_p, remainingHp);
			} catch (MyParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		else if(level_p instanceof Level_FullLoaded_Properties){
			return loadSection(gl, ((Level_FullLoaded_Properties)level_p).sec_p[n_th], remainingHp);
		}
		throw new IllegalArgumentException("level_p is neither FullLoaded nor onDemand.. what the hell happened? ("+level_p.getClass().getSimpleName()+")");
	}

	/*private static void addAI(GameSection gs) {
		GameObj_Properties go_p = GameObj_Properties.getInstance();
		go_p.reset();
		go_p.GOShape = GameObject.GO_Shape.Block;
		{
			go_p.phy_p = new GameObj_Properties.Physical_Properties();
			go_p.phy_p.dimensionX = 2.3f;
			go_p.phy_p.dimensionY = 1.15f;
			go_p.phy_p.type = GameObject.Type.Dynamic;
		}
		{   go_p.draw_p = new GameObj_Properties.Drawable_Properties();
			go_p.draw_p.color = Color.GREY;
			go_p.draw_p.variance = 16;
		}
		{	go_p.health_p = new GameObj_Properties.Health_Properties();
			go_p.health_p.element = GameElement.Water;//
			go_p.health_p.damageable = true;
			go_p.health_p.maxHealth = 500;
			go_p.health_p.proportionalHealthDmg = new FloatValue(0.1f);
			go_p.health_p.constDmg = new FloatValue(31);
		}
		{   go_p.flag_p = new GameObj_Properties.Flag_Properties();
			go_p.flag_p.put("hasWater", false);
		}
		{   //AI
			Action wander = new A_Cooldown(new A_ApplyRandomImpulse(
						new FloatValue(1f, 2f), true, false,new FloatValue(-1f, 1f), null),
						1000),
					flee = new A_Cooldown(new A_ApplyDirectedImpulse(
						new FloatValue(1.1f, 2.2f), true, false, new F_Flag<Entity>("closestFire"), false),
						500),
					firstFlee = new A_ApplyDirectedImpulse(
							new FloatValue(2.5f, 9.36f), true, false, new F_Flag<Entity>("closestFire"), false),
					fetch = new A_Chance(new A_FlagSetter("hasWater", true), new FloatValue(0.5f)),
					attack = new A_Cooldown(
							new A_Multiple( new Action[]{
									new A_ApplyDirectedImpulse(
											new FloatValue(0.1f), true, false, new F_Flag<Entity>("closestFire"), true),
									new A_FlagSetter("hasWater", false)
							}),
							1000),
					chase = new A_Cooldown(new A_ApplyDirectedImpulse(
							new FloatValue(0.1f, 0.5f), true, false, new F_Flag<Entity>("closestFire"), true),
							1000),
					firstChase = new A_ApplyDirectedImpulse(
							new FloatValue(0.75f, 1.5f), true, false, new F_Flag<Entity>("closestFire"), true);
			Leaf wander_L = new Leaf("Wander", wander),
					flee_L = new L_FirstCall("Flee", _Log.Priority.WARN, flee, firstFlee),
					fetch_L = new Leaf("Fetch", fetch),
					attack_L = new Leaf("Attack!", _Log.Priority.ERROR, attack),
					chase_L = new L_FirstCall("Chase", _Log.Priority.WARN, chase, firstChase);
			Node fire_3_5 = new N_Interval(attack_L, chase_L, null, 5f, new F_Flag<Float>("distanceFromFire")),
					hasWater = new N_BoolFieldPredicate(fire_3_5, fetch_L, new F_Flag<Boolean>("hasWater")),
					lowOnHP = new N_Interval(flee_L, hasWater, null, 50f, new F_Health()),
					fire_gt10 = new N_Interval(wander_L, flee_L, 10f, null, new F_Flag<Float>("distanceFromFire")),
					fire_3_10 = new N_Interval(lowOnHP, fire_gt10, 3f, 10f, new F_DistanceFromElement("distanceFromFire", "closestFire", GameElement.Fire));
			DecisionTree dt = new DecisionTree(fire_3_10);
			go_p.ai_p = new AI_tmp_Properties(dt);
		}
		go_p.explicitID = 168;
		gs.createGameObject(go_p, 15f, 0f);
	}*/

	/*
	public static GameWorld loadLestrangeVault(){
		// boundaries of the physical simulation
		final float XMIN = -10, XMAX = 5.2f, YMIN = -30, YMAX = 30;
		Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX);
		GameWorld gw = new GameWorld(physicalSize, MainActivity.screenSize, MainActivity.thisActivity);
		gw.addGameObject(GameObject.makeEnclosure(gw, physicalSize, 0.5f, .0f));


		gw.addMainObject(GameObject.makeBlock(gw, -3, 10, true, 1.1f, 1.1f, Color.ORANGE));
		gw.addMainObject(GameObject.makeBlock(gw, -3, 8, true, 1.1f, 1.1f, Color.BROWN));
		gw.addMainObject(GameObject.makeBlock(gw, -4, 4, true, 1.1f, 1.1f, Color.GREEN));
		gw.addMainObject(GameObject.makeBlock(gw, 0, 15, true, 1.1f, 1.1f, Color.RED));

		for(float i=YMIN+0.5f; i<YMAX; i++)
			gw.addGameObject(GameObject.makeBlock(gw, XMIN+.5f, i, false, 1, 1, Color.random(Color.BROWN)));

		gw.addGameObject( GameObject.makeCircle(gw, 1, 20, true, 0.33f, Color.GOLD));
		gw.addGameObject( GameObject.makeCircle(gw, 1, 15, true, 0.25f, Color.SILVER));
		gw.addGameObject( GameObject.makeCircle(gw, 1, 17, true, 0.33f, Color.GOLD));
		gw.addGameObject( GameObject.makeCircle(gw, 1, -8, true, 0.25f, Color.SILVER));
		gw.addGameObject( GameObject.makeBlock(gw, 1, 15.2f, true, 0.3f, 0.8f, Color.SILVER));
		return gw;

		//Heart
		go_properties.draw_p.color = Color.RED;
		for(int x=0; x<3; x++)
			for(int y=0; y<1+2*x; y++)
				gw.addGameObject(GameObject.make(gw, go_properties, x, -3f-x+y));
		gw.addGameObject(GameObject.make(gw, go_properties, 3, -2));
		gw.addGameObject(GameObject.make(gw, go_properties, 3, -4));
	}
	*/
}
