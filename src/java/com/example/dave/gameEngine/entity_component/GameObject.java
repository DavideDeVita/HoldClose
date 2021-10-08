package com.example.dave.gameEngine.entity_component;

import android.content.SharedPreferences;

import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.dataStructures.FixtureInfo;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.MultiZone;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataDriven.Section_Properties;
import com.example.dave.gameEngine.dataDriven.ai.AI_Properties;
import com.example.dave.gameEngine.dataDriven.ai.DecisionTree_Properties;
import com.example.dave.gameEngine.dataDriven.ai.action_p.Action_Properties;
import com.example.dave.gameEngine.dataDriven.component.Animation_Properties;
import com.example.dave.gameEngine.dataDriven.component.Control_Properties;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Flag_Properties;
import com.example.dave.gameEngine.dataDriven.component.Health_Properties;
import com.example.dave.gameEngine.dataDriven.component.Kinematic_Properties;
import com.example.dave.gameEngine.dataDriven.component.Physical_Properties;
import com.example.dave.gameEngine.dataDriven.component.Resizeable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Summoner_Properties;
import com.example.dave.gameEngine.gameLoop;
import com.example.dave.gameEngine.myMultimedia.MyMusic;
import com.example.dave.gameEngine.myMultimedia.MySound;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Shape;

import java.util.EnumMap;
import java.util.Map;

import static com.example.dave.gameEngine.GraphicSubSys.clear;

public class GameObject extends Entity{
	private final GO_Shape shape;
	private String name;
	private final OnHitBehaviour oHB;
	private final EnumMap<GameElement, Action> onHitByElementBehaviour;

	GameObject(GameSection gs, GO_Shape shape, OnHitBehaviour oHB, Map<GameElement, Action_Properties> onHitByElementBehaviourMap) {
		super(gs);
		this.shape = shape;
		this.name = ""+shape+id;
		this.oHB = (oHB!=null) ? oHB : OnHitBehaviour.Std;
		if(onHitByElementBehaviourMap==null)
			onHitByElementBehaviour=null;
		else {
			onHitByElementBehaviour = new EnumMap<>(GameElement.class);
			for(GameElement element : onHitByElementBehaviourMap.keySet())
				onHitByElementBehaviour.put(element, onHitByElementBehaviourMap.get(element).build(this));
		}
	}

	protected GameObject(GameSection gs, GO_Shape shape, OnHitBehaviour oHB, Map<GameElement, Action_Properties> onHitByElementBehaviourMap, int explicitId){
		super(gs, explicitId);
		this.shape = shape;
		this.name=""+shape+id+"*";
		this.oHB = (oHB!=null) ? oHB : OnHitBehaviour.Std;
		if(onHitByElementBehaviourMap==null)
			onHitByElementBehaviour=null;
		else{
			onHitByElementBehaviour = new EnumMap<>(GameElement.class);
			for(GameElement element : onHitByElementBehaviourMap.keySet())
				onHitByElementBehaviour.put(element, onHitByElementBehaviourMap.get(element).build(this));
		}
	}

	public static GameObject makeEnclosure(GameSection gs, Box box, Section_Properties.Enclosement enclosement) {
		GameObject go = new GameObject(gs, null, enclosement.oHB, null);
		float thickness = 0.5f;
		float addUp = (box.height/ 2f);
		if(enclosement.constant0_percentage1)	addUp *= enclosement.engorgement;
		else	                                addUp += enclosement.engorgement;
		float xmin = box.xmin-addUp;
		float xmax = box.xmax+addUp;
		float ymin = box.ymin-addUp;
		float ymax = box.ymax+addUp;

		// a body definition: position and type
		BodyDef bdef = new BodyDef();
		// default position is (0,0) and default type is staticBody
		Body body = gs.world.createBody(bdef);
		FixtureDef fixturedef = new FixtureDef();
		fixturedef.setFriction(0.66f);
		fixturedef.setRestitution(0.1f);
		body.setUserData(go);

		PolygonShape boxShape = new PolygonShape();
		// top
		boxShape.setAsBox(xmax-xmin, thickness, xmin+(xmax-xmin)/2, ymin-thickness, 0); // last is rotation angle
		fixturedef.setShape(boxShape);
		body.createFixture(fixturedef);
		// bottom
		boxShape.setAsBox(xmax-xmin, thickness, xmin+(xmax-xmin)/2, ymax+thickness, 0);
		fixturedef.setShape(boxShape);
		body.createFixture(fixturedef);
		// left
		boxShape.setAsBox(thickness, ymax-ymin, xmin-thickness, ymin+(ymax-ymin)/2, 0);
		fixturedef.setShape(boxShape);
		body.createFixture(fixturedef);
		// right
		boxShape.setAsBox(thickness, ymax-ymin, xmax+thickness, ymin+(ymax - ymin) / 2, 0);
		fixturedef.setShape(boxShape);
		body.createFixture(fixturedef);

		// clean up native objects
		bdef.delete();
		boxShape.delete();

		go.addComponent(new PhysicalBox_Cmpnt(go, body, box.width, box.height));

		go.name="Enclosure"+go.id;
		return go;
	}

	public static GameObject make(GameSection gs, GameObj_Properties go_p, Float x, Float y){
		GameObject go;
		if(go_p.explicitID!=null)
			go = new IdentifiedGameObject(gs, go_p.GOShape, go_p.oHB, go_p.oHbEB, go_p.explicitID);
		else
			go = new GameObject(gs, go_p.GOShape, go_p.oHB, go_p.oHbEB);

		if(go_p.phy_p!=null) {
			if(x!=null && y!=null)
				go.buildPhysicalRelated_Cmpnt(go_p.phy_p, x, y);
			else
				go.buildPhysicalRelated_Cmpnt(go_p.phy_p);
		}

		if(go_p.draw_p!=null)
			go.buildDrawableRelated_Cmpnt(go_p.draw_p, go_p.phy_p.dimensionX, go_p.phy_p.dimensionY);

		if(go_p.resize_p!=null)
			go.buildResizeable_Cmpnt(go_p.resize_p);

		if(go_p.health_p!=null)
			go.buildHealth_Cmpnt(go_p.health_p);

		if(go_p.control_p!=null)
			go.buildControl_Cmpnt(go_p.control_p);

		if(go_p.ai_p!=null)
			go.buildAI_Cmpnt(go_p.ai_p);

		if(go_p.flag_p!=null)
			go.buildFlags_Cmpnt(go_p.flag_p);

		if(go_p.summoner_p!=null)
			go.buildSummoner_Cmpnt(go_p.summoner_p);

		if(go_p.name!=null)
			go.name = '['+go_p.name+']'+go.name;

		if(_Log.LOG_ACTIVE){
			_Log.i("GameObject", go_p.name+" built.");
			_Log.d("GameObject", go_p.name+" built in "+go.getPhysical().getX()+" "+go.getPhysical().getY());}
		return go;
	}

	private void buildPhysicalRelated_Cmpnt(Physical_Properties phy_p, float x, float y) {
		/*Physical Component building*/
		BodyDef bdef = new BodyDef();
		if(!gs.physicalBox.contains(x,y)){
			bdef.setPosition(gs.physicalBox.randomX(), gs.physicalBox.randomY());
			if(_Log.LOG_ACTIVE){
				_Log.w("LevelWarning", this+" outside of Physical Box.. brought in TouchBox: "+phy_p.type+"(x,y) was in "+x+", "+y);}
		}
		else
			bdef.setPosition(x, y);
		bdef.setAngle(MyMath.toRadians(phy_p.angle));
		// a body
		Body body = gs.world.createBody(bdef);
		body.setSleepingAllowed(false);
		body.setUserData(this);
		/*Shape*/
		Shape shape;
		switch (this.shape){
			case Block:
				shape = new PolygonShape();
				((PolygonShape)shape).setAsBox(phy_p.dimensionX / 2, phy_p.dimensionY / 2);
				break;
			case Circle:
				shape = new CircleShape();
				((CircleShape)shape).setRadius(phy_p.dimensionX/2);
				break;
			default: shape=null;
		}

		FixtureDef fixturedef = new FixtureDef();
		fixturedef.setShape(shape); //
		fixturedef.setFriction(phy_p.friction);       // default 0.2
		fixturedef.setRestitution(phy_p.restitution);    // default 0
		fixturedef.setDensity(phy_p.density);     // default 0
		body.setFixedRotation(phy_p.lockRotation);
		Fixture fixture = body.createFixture(fixturedef);
		FixtureInfo fInfo = new FixtureInfo(body, fixturedef);

		fixturedef.delete();
		fixture.delete();
		bdef.delete();
		shape.delete();

		/*Phys Cmpnt*/
		Physical_Cmpnt phy_cmpnt;
		switch (this.shape){
			case Block:
				phy_cmpnt=new PhysicalBox_Cmpnt(this, body, fInfo, phy_p);
				break;
			case Circle:
				phy_cmpnt=new PhysicalCircle_Cmpnt(this, body, fInfo, phy_p);
				break;

			default: phy_cmpnt=null;
		}
		addComponent(phy_cmpnt);

		/*Type*/
		phy_cmpnt.body.setType(phy_p.type.bodyType);
		this.name=phy_p.type+name;
		if(phy_p.type!=Type.Static){
			body.setAngularVelocity(MyMath.toRadians(phy_p.angularVel));
			phy_cmpnt.setVelocityVector(phy_p.direction, phy_p.velocity);
			if(phy_p.type==Type.Kinematic){
				Kinematic_Properties kine_p = phy_p.kine_p;
				switch(kine_p.getPattern()){
					case BoxBounce:
						addComponent(new Kinematic_BoxBouncePattern_Cmpnt(this, phy_p.direction, phy_p.velocity, kine_p));
						break;
					case Polygon:
						addComponent(new Kinematic_PolygonPattern_Cmpnt(this, phy_p.velocity, kine_p));
						break;
					case Circular:
						addComponent(new Kinematic_CircularPattern_Cmpnt(this, phy_p.velocity, kine_p));
						break;
				}
			}
		}
	}

	private void buildPhysicalRelated_Cmpnt(Physical_Properties phy_p) {
		buildPhysicalRelated_Cmpnt(phy_p,phy_p.x,phy_p.y);
	}

	private void buildDrawableRelated_Cmpnt(Drawable_Properties draw_p, float dx, float dy) {
		float screen_semi_width = gs.toPixelsXLength(dx) / 2;
		float screen_semi_height = gs.toPixelsYLength(dy) / 2;
		switch(draw_p.getMotive()){
			case Bitmap:
				Drawable_Properties.BitmapMotive_Properties bitmap_p = (Drawable_Properties.BitmapMotive_Properties)draw_p.motive_p;
				switch (this.shape){
					case Block:
						this.addComponent( new DrawableBoxStaticSprite_Cmpnt(this, draw_p, screen_semi_width, screen_semi_height));
						break;
					case Circle:
						this.addComponent( new DrawableCircleStaticSprite_Cmpnt(this, draw_p,screen_semi_width));
						break;
				}
				break;
			case Animated:
				Animation_Properties animation_p = ((Drawable_Properties.AnimatedMotive_Properties)draw_p.motive_p).animation_p;
				switch (this.shape){
					case Block:
						this.addComponent( new DrawableBoxAnimated_Cmpnt(this, draw_p, screen_semi_width, screen_semi_height));
						break;
					case Circle:
						this.addComponent( new DrawableCircleAnimated_Cmpnt(this, draw_p, screen_semi_width));
						break;
				}
				buildAnimation_Cmpnt(animation_p);
				break;
			case Monochrome:
				switch (this.shape){
					case Block:
						this.addComponent( new DrawableBoxMonochrome_Cmpnt(this, draw_p, screen_semi_width, screen_semi_height));
						break;
					case Circle:
						this.addComponent( new DrawableCircleMonochrome_Cmpnt(this, draw_p, screen_semi_width));
						break;
				}
				break;
		}
	}

	private void buildResizeable_Cmpnt(Resizeable_Properties resize_p) {
		this.addComponent( new Resizeable_Cmpnt(this,resize_p));
	}

	private void buildHealth_Cmpnt(Health_Properties health_p) {
		this.addComponent(new Health_Cmpnt(this, health_p));
		this.name = health_p.element +" "+ name;
	}

	private void buildControl_Cmpnt(Control_Properties control_p) {
		this.addComponent(new Control_Cmpnt(this, control_p));
	}

	private void buildAI_Cmpnt(AI_Properties ai_p) {
		if(_Log.LOG_ACTIVE){
			_Log.d("GameObject", "buildingAI_cmpnt.\nI have "+ai_p.getClass().getSimpleName());}
		if(ai_p instanceof DecisionTree_Properties) {
			this.addComponent(new AI_cmpnt(this, (DecisionTree_Properties) ai_p));
		}
		/*else if(ai_p instanceof FSM_Properties) {
			this.addComponent(new AI_cmpnt(this, (FSM_Properties) ai_p));
		}*/
	}

	private void buildFlags_Cmpnt(Flag_Properties flag_p) {
		this.addComponent(new Flags_Cmpnt(this, flag_p));
	}

	private void buildSummoner_Cmpnt(Summoner_Properties summon_p) {
		this.addComponent(new Summoner_Cmpnt(this, summon_p));
	}

	private void buildAnimation_Cmpnt(Animation_Properties animation_p) {
		switch (animation_p.getSheetType()) {
			case Single:
				this.addComponent( new MonoSheet_Animation_Cmpnt(this, animation_p) );
				break;
			case Multiple:
				this.addComponent( new MultiSheet_Animation_Cmpnt(this, animation_p) );
				break;
			default:
				break;
		}
	}

	/*Other Functions*/
	public boolean isInside(Box box) {
		if(box==null){
			if(_Log.LOG_ACTIVE){
				_Log.w("GameObject.isInside(Box) method called by "+this+" on null box. Check if you did actually set Collision.freeDamageZone. ");}
			return false;
		}
		/*endBox.fix();*/
		if(!hasComponent(ComponentType.Physics)) return false;
		Physical_Cmpnt phys = getPhysical();
		return box.contains(phys.getX(), phys.getY());
	}

	public boolean isInside(MultiZone zone) {
		if(!hasComponent(ComponentType.Physics)) return false;
		Physical_Cmpnt phys = getPhysical();
		return zone.contains(phys.getX(), phys.getY());
	}

	@Override
	public void die(){
		if(_Log.LOG_ACTIVE){
			_Log.d("GO", "call die on "+this);}
		remove();
	}

	@Override
	public void remove() {
		gs.removeGameObject(this);
		super.remove();
	}

	public void destroy() {
		gs.destroyGameObject(this);
		super.remove();
	}

	@gameLoop
	public void onHit(GameObject hitBy, Health_Cmpnt hitBy_HC) {
		oHB.onHit(this, hitBy);
		if (MainActivity.enableOnHitByElementBehaviour && onHitByElementBehaviour != null){//on hit by Elem
			if (hasComponent(ComponentType.Health) && hitBy_HC!=null) {
				Action onHitByElementAction = onHitByElementBehaviour.get(hitBy_HC.element);
				if(onHitByElementAction!=null) {
					if(_Log.LOG_ACTIVE){
						_Log.e("GameObject", name+" onHitByElement "+hitBy_HC.element);
						_Log.e("DecisionTreeOHBE", name+" onHitByElement "+hitBy_HC.element);}
					onHitByElementAction.act(this, this.gs);
				}
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public enum GO_Shape {Block, Circle}
	public enum Type {Static(BodyType.staticBody), Dynamic(BodyType.dynamicBody), Kinematic(BodyType.kinematicBody);
		public final BodyType bodyType;
		Type(BodyType bt){
			bodyType=bt;
		}
	}
	public enum OnHitBehaviour {
		Std{ @Override protected void onHit(GameObject This, GameObject hitBy) {}
		},
		EasterEgg{ @Override protected void onHit(GameObject This, GameObject hitBy) {
			if(hitBy.equals(This.gs.mainObject)) {
			//	_Log.w("EasterEgg", "Unimplemented Easter Egg on hit");
				final SharedPreferences.Editor editor = MainActivity.thisActivity.preferences.edit();
				editor.putBoolean("EasterEgg_Lvl2", true);
				editor.commit();
				MySound.tickTickTick.play(1F);
				if(_Log.LOG_ACTIVE){
					_Log.d("onHitBehavior", This+" Easter Egg");}
				This.die();
			}
		}
		},
		Erase{ @Override protected void onHit(GameObject This, GameObject hitBy) {
			if(_Log.LOG_ACTIVE){
				_Log.d("onHitBehavior", This+" die");}
			This.die();
		}
		},
		Kill{
			@Override
			protected void onHit(GameObject This, GameObject hitBy) {
				if(_Log.LOG_ACTIVE){
					_Log.d("onHitBehavior", This+" kill "+hitBy);}
				hitBy.die();
			}
		},
		Endgame{ @Override protected void onHit(GameObject This, GameObject hitBy) {
			if(hitBy.equals(This.gs.mainObject))
				This.gs.win();
		}
		};

		protected abstract void onHit(GameObject This, GameObject hitBy);
	}
}