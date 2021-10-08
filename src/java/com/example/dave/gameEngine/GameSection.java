package com.example.dave.gameEngine;

import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataStructures.CustomTaggedSet;
import com.example.dave.gameEngine.dataStructures.Fetcher;
import com.example.dave.gameEngine.dataStructures.MapBuilder;
import com.example.dave.gameEngine.dataStructures.PeriodicSpawnGameObj;
import com.example.dave.gameEngine.dataStructures.SetBuilder;
import com.example.dave.gameEngine.myMultimedia.MyBackgrounds;
import com.google.fpl.liquidfun.World;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.util.ArraySet;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.androidgames.framework.Input;
import com.example.dave.gameEngine.dataDriven.Section_Properties;
import com.example.dave.gameEngine.entity_component.AI_cmpnt;
import com.example.dave.gameEngine.entity_component.Entity;
import com.example.dave.gameEngine.entity_component.Kinematic_Cmpnt;
import com.example.dave.gameEngine.entity_component.ComponentType;
import com.example.dave.gameEngine.entity_component.Drawable_Cmpnt;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.entity_component.Physical_Cmpnt;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;
import static com.example.dave.gameEngine.MyMath.diff;
import static com.example.dave.gameEngine.MyMath.max;
import static com.example.dave.gameEngine.MyMath.norm;
import static com.example.dave.gameEngine.MyMath.sign;
import static com.example.dave.gameEngine.SectionPhase.*;

/**
 * Old Game World.
 * Is now the Section of a Game Level
 */
public class GameSection {
	private final GameLevel gLevel;
	private SectionPhase phase;
	
	// Simulation
	public World world;
	private float gravity[];

	//public final Set<GameObject> gObjects;
	//public final TaggedSet<GameElement, GameObject> gObjectsTS;
	//public final ElementTaggedSet gObjectsETS;
	//public final CustomTaggedSet<GameElement, GameObject> gObjectsCTS;
	public final CustomTaggedSet<GameElement, GameObject> gObjectsAS;
	private final Set<Drawable_Cmpnt> drawables;
	private final Set<Kinematic_Cmpnt> kinematics;
	private final Set<AI_cmpnt> intelligent;
	//Let's reduce that FPS shall we?
	private Iterator<Drawable_Cmpnt> drawables_it;
	private Iterator<Kinematic_Cmpnt> kinematics_it;
	private Iterator<AI_cmpnt> intelligent_it;
	private int it;
	//private final Queue<GameObject> deadQueue = new LinkedList<>();
	public GameObject mainObject;
	private PeriodicSpawnGameObj[] spawnables = null;

	public final Box physicalBox;
	final MultiZone touchZone;
	final Box damageFreeZone;
	public final ViewWindow currentView;
	
	TouchConsumer touchConsumer;

	// Parameters for world simulation
	private static final float TIME_STEP = 1 / 50f; // 50 fps
	private static final int VELOCITY_ITERATIONS = 8;
	private static final int POSITION_ITERATIONS = 3;
	private static final int PARTICLE_ITERATIONS = 3;

	private static abstract class Untangler{
		float currMainX, currMainY, untangleVec[]=new float[2];
		Box closestBox;
		long lastMainMovement, currTime;
		int consecutive=1;
		float direction[]=new float[2], lastDirection[]=new float[2], velocity;

		protected abstract void untangleMain(GameSection This);

		protected float[] untangleDirection(GameSection This) {
			closestBox = This.touchZone.getClosest(currMainX, currMainY);
			if(currMainX<closestBox.xmin)            untangleVec[X] = closestBox.xmin-currMainX; // >0
			else if(currMainX>closestBox.xmax)       untangleVec[X] = closestBox.xmax-currMainX; // <0;
			else                                    untangleVec[X]=0;

			if(currMainY<closestBox.ymin)            untangleVec[Y] = closestBox.ymin-currMainY; // >0
			else if(currMainY>closestBox.ymax)       untangleVec[Y] = closestBox.ymax-currMainY; // >0
			else                                    untangleVec[Y]=0;

			untangleVec[X] -= This.gravity[X];
			untangleVec[Y] -= This.gravity[Y];

			/*AVG Improvement to stabilize jump*/
			untangleVec[X] = sign(untangleVec[X]);
			untangleVec[Y] = sign(untangleVec[Y]);

			return untangleVec;
		}

		private void init(GameObject main){
			consecutive=1;
			direction[X] = direction[Y]=0f;
			lastDirection[X] = lastDirection[Y]=0f;
			this.lastMainMovement = System.nanoTime();
		}
	}
	private final static Untangler untangler;

	static {
		untangler = new Untangler() {
			@Override
			protected void untangleMain(GameSection This){
				direction = This.mainObject.getPhysical().getDirection(direction, false);
				currMainX=This.mainObject.getPhysical().getX();
				currMainY=This.mainObject.getPhysical().getY();
				if(!This.touchZone.contains(currMainX, currMainY)){
					velocity = norm(direction);
					/*{if ( ( velocity< MainActivity.velocityThreshold ) &&
							(diff(direction[X], lastDirection[X])<=MainActivity.directionChangeThreshold &&
									diff(direction[Y], lastDirection[Y])<=MainActivity.directionChangeThreshold) )
						_Log.w("Untangle", "velocity:"+velocity+"\t\tDirection: ["+direction[X]+", "+direction[Y]+"].\tLastDirection: ["+lastDirection[X]+", "+lastDirection[Y]+"]");
					else
						_Log.i("Untangle", "velocity:"+velocity+"\t\tDirection: ["+direction[X]+", "+direction[Y]+"].\tLastDirection: ["+lastDirection[X]+", "+lastDirection[Y]+"]");
					}*/
					currTime = System.nanoTime();
					if ( ( velocity< MainActivity.velocityThreshold ) &&
							(diff(direction[X], lastDirection[X])<=MainActivity.directionChangeThreshold &&
									diff(direction[Y], lastDirection[Y])<=MainActivity.directionChangeThreshold) ){
						if(currTime-lastMainMovement>=MainActivity.stuckTimeout){
							This.mainObject.getPhysical().applyImpulse(untangleDirection(This),
									MyMath.randomFloat(1.7f, 2.0f)*consecutive*This.mainObject.getPhysical().getArea()); //from 2017 to 2020
							lastMainMovement=currTime;
							consecutive++;
						}
						//}//If velocity is low do not record new direction in order to avoid( i hope) impulses
					}
					else{
						lastMainMovement=currTime;
						lastDirection[X] = direction[X];
						lastDirection[Y] = direction[Y];
						consecutive = max(consecutive, 1); //See comment below.. still more convenient
					}
				}
				else /*if(consecutive>1)*/{
					//_Log.w("Untangle", "reset consecutive");
					lastMainMovement=currTime;
					lastDirection[X] = direction[X];
					lastDirection[Y] = direction[Y];
					consecutive=0;
					/**consecutive is 0 and not 1 here because:
					 * Should the mainObj move from a border zone between TouchZone and noTouchZone after a brief period,
					 * it will be seen as an untangle condition by the second if (too close from last position)
					 * Setting to consecutive to zero, in this occasion the first untangle will be useless
					 * without having to check whether the last position was in the TouchZone.
					 * */
				}
			}
		};
	}

	public GameSection(GameLevel level, Section_Properties gs_p) {
		this.gLevel=level;
		Entity.resetIdCounter();
		this.phase= Play;
		this.physicalBox = gs_p.physical;

		if(gs_p.damageFreeZone!=null) {
			this.damageFreeZone = new SubBox(gs_p.damageFreeZone, gs_p.physical);}
		else
			this.damageFreeZone=null;

		if(gs_p.touchBoxes ==null) {
			this.touchZone = new MultiZone(gs_p.physical);}
		else{
			this.touchZone = new MultiZone(gs_p.physical,gs_p.touchBoxes);}

		if(gs_p.gravity==null){
			this.world = new World(0, 0);  // gravity vector
			this.gravity = new float[]{0, 0};
			MainActivity.accelerometerListener.setGW(this);
		}
		else{
			this.gravity = new float[]{gs_p.gravity.getX(), gs_p.gravity.getY()};
			this.world = new World( gravity[X], gravity[Y] );
		}

		{
			final ViewWindow.ViewBox_Details vb_d = new ViewWindow.ViewBox_Details();
			if(gs_p.backwardUpdate_x !=null){
				vb_d.allowBack_x=true;
				vb_d.backwardRatio_x= gs_p.backwardUpdate_x;
			}
			if(gs_p.backwardUpdate_y !=null){
				vb_d.allowBack_y=true;
				vb_d.backwardRatio_y= gs_p.backwardUpdate_y;
			}
			vb_d.forwardRatio_x= gs_p.forwardUpdate_x;
			vb_d.forwardRatio_y= gs_p.forwardUpdate_y;
			//_Log.w("View", gs_p.viewWidth+" ---- "+gs_p.viewHeight);
			if(gs_p.viewWidth!=null)
				vb_d.fixedWidth=gs_p.viewWidth;
			else {
				vb_d.fixedWidth = gs_p.viewHeight * gLevel.PIXEL_RATIO;
				if(vb_d.fixedWidth>physicalBox.width){ //Avoid deformity
					if(_Log.LOG_ACTIVE){
						_Log.w("ViewSetting", "Fixed Height ("+gs_p.viewHeight+") would produce width grater than physical ("+vb_d.fixedWidth+">"+physicalBox.width+")..\n" +
							"\tWidth set to max and height adjusted");}
					vb_d.fixedWidth=physicalBox.width;
					gs_p.viewHeight = vb_d.fixedWidth / gLevel.PIXEL_RATIO; //gs_p so that next if will set
					if(_Log.LOG_ACTIVE){
						_Log.i("ViewSetting", "W: "+vb_d.fixedWidth+" H: "+gs_p.viewHeight);}
				}
			}
			if(gs_p.viewHeight!=null)
				vb_d.fixedHeight=gs_p.viewHeight;
			else {
				vb_d.fixedHeight = gs_p.viewWidth / gLevel.PIXEL_RATIO;
				if(vb_d.fixedHeight>physicalBox.height){ //Avoid deformity
					if(_Log.LOG_ACTIVE){
						_Log.w("ViewSetting", "Fixed Width ("+gs_p.viewWidth+") would produce height grater than physical ("+vb_d.fixedHeight+">"+physicalBox.height+")..\n" +
							"\tHeight set to max and width adjusted");}
					vb_d.fixedHeight=physicalBox.height;
					gs_p.viewWidth = vb_d.fixedHeight * gLevel.PIXEL_RATIO;
					if(_Log.LOG_ACTIVE){
						_Log.i("ViewSetting", "W: "+gs_p.viewWidth+" H: "+vb_d.fixedHeight);}
				}
			}
			this.currentView = physicalBox.getViewWindow(vb_d);
			//_Log.i("View", currentView.width+" ---- "+currentView.height);
		}
		// stored to prevent GC
		world.setContactListener(gLevel.contactListener);

		touchConsumer = new TouchConsumer(this);

		this.drawables = new HashSet<>();
		this.kinematics = new HashSet<>();
		this.intelligent = new HashSet<>();
		//this.gObjects = new HashSet<>();
		/*this.gObjectsETS = new ElementTaggedSet(new TagFetcher<GameObject, GameElement>() {
			@Nullable @Override
			public GameElement fetchKey(@NonNull GameObject go) {
				if(go==null) throw new NullPointerException("null Game Object in fetchKey");
				else{
					if(!go.hasComponent(ComponentType.Health)) return GameElement.NULL;
					else return go.getHealth().element;
				}
			}
		});*/
		/*this.gObjectsTS = new TaggedSet<>(new TagFetcher<GameObject, GameElement>() {
			@Nullable @Override
			public GameElement fetchKey(@NonNull GameObject go) {
				if(go==null) throw new NullPointerException("null Game Object in fetchKey");
				else{
					if(!go.hasComponent(ComponentType.Health)) return GameElement.NULL;
					else return go.getHealth().element;
				}
			}
		});*/
		/*this.gObjectsCTS = new CustomTaggedSet<>(
				new Fetcher<GameObject, GameElement>() {
					@Nullable @Override
					public GameElement fetch(@NonNull GameObject go) {
						if(go==null) throw new NullPointerException("null Game Object in fetchKey");
						else{
							if(!go.hasComponent(ComponentType.Health)) return GameElement.NULL;
							else return go.getHealth().element;
						}
					}
				},
				new SetBuilder<GameObject>() {
					@Override
					public Set<GameObject> buildSet() {
						return new HashSet<GameObject>();
					}
				},
				new MapBuilder<GameElement, Set<GameObject>>() {
					@Override
					public Map<GameElement, Set<GameObject>> buildMap() {
						return new EnumMap<GameElement, Set<GameObject>>(GameElement.class);
					}
				});*/
		this.gObjectsAS = new CustomTaggedSet<>(
				new Fetcher<GameObject, GameElement>() {
					@Nullable @Override
					public GameElement fetch(@NonNull GameObject go) {
						if(go==null) throw new NullPointerException("null Game Object in fetchKey");
						else{
							if(!go.hasComponent(ComponentType.Health)) return GameElement.NULL;
							else return go.getHealth().element;
						}
					}
				},
				new SetBuilder<GameObject>() {
					@Override
					public Set<GameObject> buildSet() {
						return new ArraySet<GameObject>();
					}
				},
				new MapBuilder<GameElement, Set<GameObject>>() {
					@Override
					public Map<GameElement, Set<GameObject>> buildMap() {
						return new EnumMap<GameElement, Set<GameObject>>(GameElement.class);
					}
				});

		setBackground(gs_p.background);
	}

	public final void win() { this.phase=Win; }

	public final void lose() {
		this.phase=Lose;
		gLevel.lose();
	}

	public final synchronized GameObject createGameObject(GameObj_Properties go_p, Float x, Float y){
		return addGameObject( GameObject.make(this, go_p, x, y) );
	}

	final synchronized GameObject createMainObject(GameObj_Properties go_p, Float x, Float y){
		return addMainObject( GameObject.make(this, go_p, x, y) );
	}

	final private synchronized GameObject addMainObject(GameObject go){
		this.mainObject=go;
		untangler.init(mainObject);

		addGameObject(go);
		if(_Log.LOG_ACTIVE)
			_Log.i("NewSection", "Main Object "+mainObject+" starts with "+mainObject.getHealth().health+" hp");
		return mainObject;
	}

	final synchronized GameObject addGameObject(GameObject go){
		if(go.hasComponent(ComponentType.Drawable)){
			drawables.add(go.getDrawable());
		}

		if(go.hasComponent(ComponentType.Kinematic)){
			kinematics.add(go.getKinematic());
		}

		if(go.hasComponent(ComponentType.AI)){
			 intelligent.add(go.getAI());
		}

		//gObjects.add(go);
		//gObjectsTS.add(go);
		//gObjectsETS.add(go);
		//gObjectsCTS.add(go);
		gObjectsAS.add(go);
		return go;
	}

	/**Destroys the gameObject and removes it from the gObj list*/
	public synchronized void removeGameObject(GameObject go){
		//gObjects.remove(go);
		//gObjectsTS.remove(go);
		//gObjectsETS.remove(go);
		//gObjectsCTS.remove(go);
		gObjectsAS.remove(go);
		destroyGameObject(go);

		if(mainObject==go)
			this.lose();
	}

	/**Destroys the gameObject while keeping it in the gObj list
	 * called in finalize*/
	public synchronized void destroyGameObject(GameObject go){
		if(go.hasComponent(ComponentType.Control)){
			touchConsumer.safeRemove(go);
		}
		if(go.hasComponent(ComponentType.Drawable)){
			drawables.remove(go.getDrawable());
		}
		if(go.hasComponent(ComponentType.Physics)){
			Physical_Cmpnt phys = go.getPhysical();
			world.destroyBody(phys.body);
		}
		if(go.hasComponent(ComponentType.Kinematic)){
			kinematics.remove( go.getKinematic());
		}
		if(go.hasComponent(ComponentType.AI)){
			intelligent.remove( go.getAI());
		}
	}

	public void setSpawnables(PeriodicSpawnGameObj[] spawnables){
		this.spawnables=spawnables;
	}

	/**Returns true if the game is won*/
	@gameLoop
	synchronized boolean update(float elapsedTime){
		// advance the physics simulation
		world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

		//Check Main is stuck
		if(MainActivity.untangleMain)
			untangler.untangleMain(this);

		// Handle collisions
		handleCollisions(gLevel.contactListener.getCollisions());
		/**On Lose it will be already ended here*/
		//_Log.i("phase is "+phase);

		//End Game
		if(phase==Win)
			return true;
		else if(phase==Lose)
			return false;

		//Update kinematics
		kinematics_it = kinematics.iterator();
		for (it=0; it<kinematics.size(); it++) {
			kinematics_it.next().update();
		}

		//Update AI
		intelligent_it = intelligent.iterator();
		for (it=0; it<intelligent.size(); it++) {
			//_Log.w("AI", "Health "+ai.getHp());
			intelligent_it.next().act(this);
		}

		// Handle touch events
		for (Input.TouchEvent event: gLevel.touch.getTouchEvents())
			touchConsumer.consumeTouchEvent(event);
		if(MainActivity.checkHoldInTouch)
			touchConsumer.checkHold();
		checkSpawns();

		return false;
	}

	synchronized void render(){
		//GraphicSubSys.clear(background); // clear the screen (with black)
		updateCurrentView();
		if(MainActivity.allowLightSignal)
			updateSignalColor();
		if(MainActivity.scrollBackground)
			computeLevelBackgroundView();
		GraphicSubSys.drawLevelBackGround(background, bgSrc);
		drawables_it = drawables.iterator();
		for (it=0; it<drawables.size(); it++) {
			drawables_it.next().draw();
		}
	}

	private void updateCurrentView() {
		if(mainObject!=null){
			Physical_Cmpnt physical = mainObject.getPhysical();
			currentView.resetView(physical.getX(), physical.getY());
		}
	}

	@gameLoop
	private void handleCollisions(Collection<Collision> collisions) {
		for (Collision event: collisions) {
			//Done before in order to allow correct heal and damage of rain
			event.manageHits(damageFreeZone);

			if(phase!=Play) return;

			/*Sound of collision*/
			if(currentView.contains(event.x, event.y))
				event.manageSound();
		}
	}

	private void checkSpawns() {
		if( spawnables!=null )
			for ( int i=0; i<spawnables.length; i++ )
				spawnables[i].check(this);
	}

	// Conversions between screen coordinates and physical coordinates
	public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/gLevel.lvlScreenWidth); }
	public float toMetersY(float y) { return currentView.ymax - y * (currentView.height/gLevel.lvlScreenHeight);}

	public float toPixelsX(float x) { return (x-currentView.xmin)/currentView.width*gLevel.lvlScreenWidth; }
	public float toPixelsY(float y) { return (currentView.ymax-y)/currentView.height*gLevel.lvlScreenHeight; }

	public float toPixelsXLength(float x){	return x/currentView.width*gLevel.lvlScreenWidth;	}
	public float toPixelsYLength(float y){	return y/currentView.height*gLevel.lvlScreenHeight;}

	public float toScreenAngle(float angle) { return -angle; }

	synchronized void setGravity(float x, float y){
		world.setGravity(x, y);
		gravity[X]=x; gravity[Y]=y;
	}

	private Bitmap background;
	private Rect bgSrc;
	private void setBackground(MyBackgrounds myBackground){
		bgSrc = new Rect();
		background = myBackground.getBitmap();

		if(MainActivity.scrollBackground)
			computeLevelBackgroundView();
		else
			bgSrc.set(0, 0 , background.getWidth(), background.getHeight());
	}

	/*top <= bottom*/
	private void computeLevelBackgroundView() {
		bgSrc.set( (int)(background.getWidth()*((currentView.xmin- physicalBox.xmin) / physicalBox.width)),
				(int)(background.getHeight()*((physicalBox.ymax-currentView.ymax) / physicalBox.height)),
				(int)(background.getWidth()*((currentView.xmax- physicalBox.xmin) / physicalBox.width)),
				(int)(background.getHeight()*((physicalBox.ymax-currentView.ymin) / physicalBox.height))
		);
		/*bgSrc.set( (int)(background.getWidth()*((currentView.xmin- physicalBox.xmin) / physicalBox.width)),
				0,
				(int)(background.getWidth()*((currentView.xmax- physicalBox.xmin) / physicalBox.width)),
				background.getHeight()
		);*/
	}

	/**Signals:
	 * Blue = non touchable*/
	private void updateSignalColor() {
		if(!mainObject.isInside(touchZone) || !mainObject.getControllable().canControl())
			GraphicSubSys.drawScreenBackground(Color.RED);
		else
			GraphicSubSys.drawScreenBackground(Color.BLACK);
	}

	/*No stuck variables*/

	@Override
	public void finalize(){
		for (GameObject go : gObjectsAS) {
			go.destroy();
		}
		//gObjects.clear();
		//gObjectsTS.clear();
		//gObjectsETS.clear();
		//gObjectsCTS.clear();
		gObjectsAS.clear();
		if(spawnables!=null){
			for( PeriodicSpawnGameObj spawnable : spawnables){
				spawnable.free();
			}
		}
		world.delete();
	}

	/**Data Structure Testing*/
	/*
	public void test(GameElement element) {

		long startTimeTS=System.nanoTime();
		Float retTS = testTS(element);
		long endTimeTS = System.nanoTime();

		long startTimeETS=System.nanoTime();
		Float retETS = testETS(element);
		long endTimeETS = System.nanoTime();

		long startTimeHS=System.nanoTime();
		Float retHS = testHS(element);
		long endTimeHS = System.nanoTime();

		long startTimeCTS=System.nanoTime();
		Float retCTS = testCTS(element);
		long endTimeCTS = System.nanoTime();

		long startTimeAS=System.nanoTime();
		Float retAS = testAS(element);
		long endTimeAS = System.nanoTime();

		class TestEntry implements Comparable<TestEntry>{
			private final String name;
			private final long micros;

			TestEntry(String name, long nanos){
				this.name=name;
				this.micros = nanos/1000L;
			}

			@Override
			public int compareTo(TestEntry o) {
				if(micros==o.micros)
					return name.compareTo(o.name);
				else
					return (int)(micros-o.micros);
			}

			@Override public String toString(){ return name+"("+micros+")";}
		}
		SortedSet<TestEntry> testResult = new TreeSet<>();
		//testResult.add(new TestEntry("HashSet", endTimeHS-startTimeHS));
		//testResult.add(new TestEntry("TaggedSet", endTimeTS-startTimeTS));
		//testResult.add(new TestEntry("ElemTagSet", endTimeETS-startTimeETS));
		testResult.add(new TestEntry("CustomTagSet", endTimeCTS-startTimeCTS));
		testResult.add(new TestEntry("ArraySet", endTimeAS-startTimeAS));

		_Log.e("TaggedSetTest", "On element "+element+"\n" +
		//		"TaggedSet: returned "+retTS+" in "+((endTimeTS-startTimeTS)/1_000L)+" micros\n" +
		//		"HashSet: returned "+retHS+" in "+((endTimeHS-startTimeHS)/1_000L)+" micros\n" +
		//		"ElemTaggedSet: returned "+retETS+" in "+((endTimeETS-startTimeETS)/1_000L)+" micros\n" +
				"Custom ArraySet: returned "+retAS+" in "+((endTimeAS-startTimeAS)/1_000L)+" micros\n" +
				"Custom TaggedSet: returned "+retCTS+" in "+((endTimeCTS-startTimeCTS)/1_000L)+" micros\n" );
		_Log.e("TaggedSetTest", "classifica "+element+": "+testResult);
	}

	public void test_iter(GameElement element) {

		long startTimeTS=System.nanoTime();
		Float retTS = testTS_iter(element);
		long endTimeTS = System.nanoTime();

		long startTimeETS=System.nanoTime();
		Float retETS = testETS_iter(element);
		long endTimeETS = System.nanoTime();

		long startTimeHS=System.nanoTime();
		Float retHS = testHS_iter(element);
		long endTimeHS = System.nanoTime();

		long startTimeCTS=System.nanoTime();
		Float retCTS = testCTS_iter(element);
		long endTimeCTS = System.nanoTime();

		long startTimeAS=System.nanoTime();
		Float retAS = testAS_iter(element);
		long endTimeAS = System.nanoTime();

		class TestEntry implements Comparable<TestEntry>{
			private final String name;
			private final long micros;

			TestEntry(String name, long nanos){
				this.name=name;
				this.micros = nanos/1000L;
			}

			@Override
			public int compareTo(TestEntry o) {
				if(micros==o.micros)
					return name.compareTo(o.name);
				else
					return (int)(micros-o.micros);
			}

			@Override public String toString(){ return name+"("+micros+")";}
		}
		SortedSet<TestEntry> testResult = new TreeSet<>();
		//testResult.add(new TestEntry("HashSet", endTimeHS-startTimeHS));
		//testResult.add(new TestEntry("TaggedSet", endTimeTS-startTimeTS));
		//testResult.add(new TestEntry("ElemTagSet", endTimeETS-startTimeETS));
		testResult.add(new TestEntry("CustomTagSet", endTimeCTS-startTimeCTS));
		testResult.add(new TestEntry("ArraySet", endTimeAS-startTimeAS));

		_Log.e("TaggedSetTest", "On element "+element+"\n" +
		//		"TaggedSet: returned "+retTS+" in "+((endTimeTS-startTimeTS)/1_000L)+" micros\n" +
		//		"HashSet: returned "+retHS+" in "+((endTimeHS-startTimeHS)/1_000L)+" micros\n" +
		//		"ElemTaggedSet: returned "+retETS+" in "+((endTimeETS-startTimeETS)/1_000L)+" micros\n" +
				"Custom ArraySet: returned "+retAS+" in "+((endTimeAS-startTimeAS)/1_000L)+" micros\n" +
				"Custom TaggedSet: returned "+retCTS+" in "+((endTimeCTS-startTimeCTS)/1_000L)+" micros\n" );
		_Log.e("TaggedSetTest", "classifica "+element+": "+testResult);
	}

	public void fullTest() {
		for(GameElement element : GameElement.values())
			test(element);
	}

	public void fullTest_iter() {
		for(GameElement element : GameElement.values())
			test_iter(element);
	}

	public Float testTS(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Iterable<GameObject> byTag = gObjectsTS.getByTag(element);
		if(byTag!=null) {
			for (GameObject go : byTag) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testETS(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Iterable<GameObject> byTag = gObjectsETS.getByTag(element);
		if(byTag!=null) {
			for (GameObject go : byTag) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testHS(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		for(GameObject go : gObjects){
			if(go.hasComponent(ComponentType.Health) && go.getHealth().element==element) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}
*
	public Float testCTS(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Iterable<GameObject> byTag = gObjectsCTS.getByTag(element);
		if(byTag!=null) {
			for (GameObject go : byTag) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testAS(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Iterable<GameObject> byTag = gObjectsAS.getByTag(element);
		if(byTag!=null) {
			for (GameObject go : byTag) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testTS_iter(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Set<GameObject> byTag = gObjectsTS.getByTag(element);
		if(byTag!=null) {
			Iterator<GameObject> iterator = byTag.iterator();
			GameObject go;
			for(int i=byTag.size(); i>0; i--){
				go=iterator.next();
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testETS_iter(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		final Set<GameObject> byTag = gObjectsETS.getByTag(element);
		if(byTag!=null) {
			Iterator<GameObject> iterator = byTag.iterator();
			GameObject go;
			for(int i=byTag.size(); i>0; i--){
				go=iterator.next();
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testHS_iter(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		final Set<GameObject> byTag = gObjects;
		Iterator<GameObject> iterator = byTag.iterator();
		GameObject go;
		for(int i=byTag.size(); i>0; i--){
			go=iterator.next();
			if(go.hasComponent(ComponentType.Health) && go.getHealth().element==element) {
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}
*
	public Float testCTS_iter(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		Set<GameObject> byTag = gObjectsCTS.getByTag(element);
		if(byTag!=null) {
			Iterator<GameObject> iterator = byTag.iterator();
			GameObject go;
			for(int i=byTag.size(); i>0; i--){
				go=iterator.next();
				dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
				if (minDist == -1 || dist < minDist)
					minDist = dist;
			}
		}
		return minDist==-1 ? null : minDist;
	}

	public Float testAS_iter(GameElement element){
		float minDist=-1;
		float mainX=mainObject.getPhysical().getX(), mainY=mainObject.getPhysical().getY();
		float dist;
		final Set<GameObject> byTag = gObjectsAS.getByTag(element);
		Iterator<GameObject> iterator = byTag.iterator();
		GameObject go;
		for(int i=byTag.size(); i>0; i--){
			go=iterator.next();
			dist = MyMath.dist(mainX, mainY, go.getPhysical().getX(), go.getPhysical().getY());
			if (minDist == -1 || dist < minDist)
				minDist = dist;
		}
		return minDist==-1 ? null : minDist;
	}
	 */

}