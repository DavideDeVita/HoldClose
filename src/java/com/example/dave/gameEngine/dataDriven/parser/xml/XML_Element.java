package com.example.dave.gameEngine.dataDriven.parser.xml;

import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.Color;
import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.*;
import com.example.dave.gameEngine.dataDriven.parser.*;
import com.example.dave.gameEngine.dataDriven.component.*;
import com.example.dave.gameEngine.dataDriven.component.AnimationUpdate_Properties.*;
import com.example.dave.gameEngine.dataDriven.ai.*;
import com.example.dave.gameEngine.dataDriven.ai.field_p.*;
import com.example.dave.gameEngine.dataDriven.ai.action_p.*;
import com.example.dave.gameEngine.dataDriven.ai.node_p.*;
import com.example.dave.gameEngine.entity_component.*;
import com.example.dave.gameEngine.myMultimedia.DrawableRes;
import com.example.dave.gameEngine.myMultimedia.MyBackgrounds;
import com.example.dave.gameEngine.myMultimedia.Spritesheets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.ArrayMap;
import android.util.Pair;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.google.fpl.liquidfun.Vec2;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public class XML_Element {
	private final Element element;

	private XML_Element(Element element){
		this.element=element;
	}

	boolean hasTag(String tag){
		return getElementsByTag(tag).size()>0;
	}

	String getTagName() {
		return element.getTagName();
	}

	XML_Element getElementByTag(String tag){
		return getElementsByTag(tag).get(0);
	}

	static XML_Element getElementByTag(Document doc, String tag){
		return new XML_Element((Element)doc.getElementsByTagName(tag).item(0));
	}

	/* TO DO: Use array instead*/

	List<XML_Element> getElementsByTag(String tag){
		List<XML_Element> retList = new LinkedList<>();
		for(org.w3c.dom.Node node = element.getFirstChild(); node!=null; node=node.getNextSibling()){
			if(node instanceof Element) {
				Element el = (Element)node;
				if (el.getTagName().equals(tag))
					retList.add(new XML_Element(el));
			}
		}
		return retList;
	}
	static List<XML_Element> getElementsByTag(Document doc, String tag){
		NodeList nodeList = doc.getElementsByTagName(tag);
		List<XML_Element> retList = new ArrayList<>(nodeList.getLength());
		for(int i=0; i<nodeList.getLength(); i++)
			retList.add(new XML_Element((Element)nodeList.item(i)));
		return retList;
	}

	List<XML_Element> getChildNodes() {
		NodeList nodeList = element.getChildNodes();
		List<XML_Element> retList = new ArrayList<>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element)
				retList.add(new XML_Element((Element)node ));
		}
		return retList;
	}
	static List<XML_Element> getChildNodes(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		List<XML_Element> retList = new ArrayList<>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element)
				retList.add(new XML_Element((Element)node ));
			//else _Log.e("Parser", "non element node is "+node.getClass().getSimpleName()+":\n"+node.getTextContent());
		}
		return retList;
	}

	XML_Element getFirstChild() throws MyParseException {
		Node node = element.getFirstChild();
		return _getFirstElement(node);
	}
	static XML_Element getFirstChild(Document doc) throws MyParseException {
		Node node = doc.getFirstChild();
		return _getFirstElement(node);
	}

	static XML_Element _getFirstElement(Node node) throws MyParseException {
		while ( !(node instanceof Element) ){
			node = node.getNextSibling();
			if(node == null)
				throw new MyParseException("There are no Elements node sibilings of "+node.getParentNode().getFirstChild().getNodeName());
		}
		return new XML_Element( (Element)node );
	}

	String getTextContent() {
		return element.getTextContent();
	}

	String getAttribute(String attribute) {
		return element.getAttribute(attribute);
	}

	/*Reader of simple types*/
	private static boolean isNumber(char c){
		return c>='0' && c<='9';
	}

	private static int toInt(char c){
		return c-'0';
	}

	String readString(){
		return this.getTextContent();
	}

	float readFloat() throws MyParseException {
		char[] text = this.getTextContent().toCharArray();
		float number = 0.0f;
		int sign =1;
		int i=0;
		boolean hasDecimals=false;
		if(text[0]=='-'){
			sign=-1;
			i++;
		}
		for(; i<text.length; i++){
			char c=text[i];
			if(isNumber(c)){
				number*=10;
				number+= toInt(c);
			}
			else if(c=='.'){
				hasDecimals=true;
				i++;
				break;
			}
			else throw new MyParseException("Float", c, this.getTextContent());
		}

		if(hasDecimals){
			float decimal=0.0f;
			int digitsFraction = 1;
			for(; i<text.length; i++){
				char c=text[i];
				if(isNumber(c)){
					decimal*=10;
					decimal+= toInt(c);
					digitsFraction*=10;
				}
				else throw new MyParseException("Float", c,getTextContent());
			}
			number+=(decimal/digitsFraction);
		}

		return sign*number;
	}

	int readInt() throws MyParseException {
		char[] text = this.getTextContent().toCharArray();
		int number = 0;
		int sign =1;
		int i=0;
		if(text[0]=='-'){
			sign=-1;
			i++;
		}
		for(; i<text.length; i++){
			char c=text[i];
			if(isNumber(c)){
				number*=10;
				number+= toInt(c);
			}
			else throw new MyParseException("Integer", c,getTextContent());
		}

		return sign*number;
	}

	boolean readBoolean() throws MyParseException{
		String text = readString().toLowerCase();
		if(text.equals("false")) return false;
		if(text.equals("true")) return true;
		else throw new MyParseException("Boolean", text);
	}

	FloatValue readFloatValue() throws MyParseException{
		Float val1=null, val2=null;
		//if it has no tag <val1> it will be read as a normal float
		if(hasTag("val1"))
			val1 = getElementByTag("val1").readFloat();
		else{
			//Look in root for single float value
			val1=readFloat();
			return new FloatValue(val1);
		}
		if(hasTag("val2"))
			val2 = getElementByTag("val2").readFloat();
		return new FloatValue(val1, val2);
	}

	<E extends Enum<E>>E readEnum(Class<E> enumClass){
		return E.valueOf(enumClass, readString());
	}

	static <E extends Enum<E>>E readEnum(Class<E> enumClass, String text){
		return E.valueOf(enumClass, text);
	}

	Object readValue() throws MyParseException {
		switch (getAttribute("type").toLowerCase()){
			case "bool":
				return readBoolean();
			case "boolean":
				return readBoolean();
			case "string":
				return readString();
			case "int":
				return readInt();
			case "integer":
				return readInt();
			case "float":
				return readFloat();
		}
		return null;
	}

	Color readColor() throws MyParseException {
		String type = element.getAttribute("type");
		if(type==null) throw new MyParseException("Missing not nullable 'type' attribute in color");
		switch (type){
			case "String":
				return Color.byName(readString());
			case "rgb":
				int r = getElementByTag("r").readInt();
				int g = getElementByTag("g").readInt();
				int b = getElementByTag("b").readInt();
				if(hasTag("a")){
					int a = getElementByTag("a").readInt();
					return new Color(a,r,g,b);
				}
				else return new Color(r,g,b);
			default: return null;
		}
	}

	Box readBox() throws MyParseException {
		float xmin, xmax, ymin, ymax;
		xmin=getElementByTag("xMin").readFloat();
		xmax=getElementByTag("xMax").readFloat();
		ymin=getElementByTag("yMin").readFloat();
		ymax=getElementByTag("yMax").readFloat();
		return new Box(xmin, ymin, xmax, ymax);
	}
	@Override
	public String toString() {
		return getTextContent();
	}

	//Reading complex nodes
	//* * * * * * * * * * * * * *  Game Object  * * * * * * * * * * * * *
	/* Returns the static instance of GameObj_Properties
	 * @param finalObject if true will require a true on isReady().
	 *         Otherwise, it will be saved as a metaProperty to be completed somewhere else*/
	GameObj_Properties readGOProperty(boolean finalObject) throws MyParseException {
		GameObj_Properties gObj = GameObj_Properties._new();
		return readGOProperty(gObj, true, finalObject);
	}
	/* Returns the same instance given.. modified*/
	GameObj_Properties readGOProperty(GameObj_Properties gObj, boolean readName, boolean finalObject) throws MyParseException {
		if(gObj==null) return readGOProperty(finalObject);
		if (readName && hasTag("name")){
			gObj.name = getElementByTag("name").readString();
			if(gObj.existsDataDrivenStandard(gObj.name)){
				gObj = gObj.getDataDrivenStandard(gObj.name);}
			else if(_Log.LOG_ACTIVE){
				_Log.w("Couldn't find "+gObj.name+" in dataDrivenStandard");}
		}
		if(_Log.LOG_ACTIVE){
			_Log.i("Parser", "Reading GO "+gObj.name);}
		if (hasTag("onHitBehaviour")){
			gObj.oHB = getElementByTag("onHitBehaviour").readEnum(GameObject.OnHitBehaviour.class);}
		if (hasTag("onHitByElement")){
			if(gObj.oHbEB==null)
				gObj.oHbEB=new ArrayMap<>();
			for(XML_Element behaviour : getElementByTag("onHitByElement").getChildNodes())
				gObj.oHbEB.put(
						readEnum(GameElement.class, behaviour.getTagName()),
						behaviour.readAction(null)
						);
		}
		if(hasTag("isAesthetic")){
			gObj.isAesthetic = getElementByTag("isAesthetic").readBoolean();}
		if (hasTag("explicitID")){
			gObj.explicitID = getElementByTag("explicitID").readInt();}
		if (hasTag("shape")){
			gObj.GOShape = getElementByTag("shape").readEnum(GameObject.GO_Shape.class);}
		if(hasTag("physical")){
			XML_Element physical = getElementByTag("physical");
			if(gObj.phy_p!=null && physical.hasTag("disable")) {
				gObj.phy_p.free();
				gObj.phy_p=null;
			}
			else {
				if (gObj.phy_p == null) gObj.phy_p = Physical_Properties._new();
				if (physical.hasTag("position")) {
					XML_Element position = physical.getElementByTag("position");
					if (position.hasTag("x")) {
						gObj.phy_p.x = position.getElementByTag("x").readFloat();
					}
					if (position.hasTag("y")) {
						gObj.phy_p.y = position.getElementByTag("y").readFloat();
					}
					if (position.hasTag("angle")) {
						gObj.phy_p.angle = position.getElementByTag("angle").readFloat();
					}
				}
				if (physical.hasTag("type")) {
					gObj.phy_p.type = physical.getElementByTag("type").readEnum(GameObject.Type.class);
				}
				if (physical.hasTag("dimensionX")) {
					gObj.phy_p.dimensionX = physical.getElementByTag("dimensionX").readFloat();
				}
				if (physical.hasTag("dimensionY")) {
					gObj.phy_p.dimensionY = physical.getElementByTag("dimensionY").readFloat();
				}
				if (physical.hasTag("friction")) {
					gObj.phy_p.friction = physical.getElementByTag("friction").readFloat();
				}
				if (physical.hasTag("density")) {
					gObj.phy_p.density = physical.getElementByTag("density").readFloat();
				}
				if (physical.hasTag("restitution")) {
					gObj.phy_p.restitution = physical.getElementByTag("restitution").readFloat();
				}
				if (physical.hasTag("velocity")) {
					gObj.phy_p.velocity = physical.getElementByTag("velocity").readFloatValue();
				}
				if (physical.hasTag("direction")) {
					XML_Element direction = physical.getElementByTag("direction");
					try {
						FloatValue x = direction.getElementByTag("x").readFloatValue();
						FloatValue y = direction.getElementByTag("y").readFloatValue();
						gObj.phy_p.direction[X] = x;
						gObj.phy_p.direction[Y] = y;
					} catch (MyParseException myE) {
						if(_Log.LOG_ACTIVE){
							_Log.e(myE);}
					}
				}
				if (physical.hasTag("angularVelocity")) {
					gObj.phy_p.angularVel = physical.getElementByTag("angularVelocity").readFloat();
				}
				if (physical.hasTag("lockRotation")) {
					gObj.phy_p.lockRotation = physical.getElementByTag("lockRotation").readBoolean();
				}
			}
		}
		if(hasTag("kinematic") && gObj.phy_p!=null && gObj.phy_p.type==GameObject.Type.Kinematic ) {
			XML_Element kinematic = getElementByTag("kinematic");
			Kinematic_Properties kine_p=null;
			if(gObj.phy_p.kine_p!=null)
				kine_p = gObj.phy_p.kine_p;

			if(kine_p!=null && kinematic.hasTag("disable")) {
				kine_p.free();
				kine_p=null;
			}
			else {
				//Changes Everything anyway
				if (kinematic.hasTag("pattern")) {
					final Kinematic_Cmpnt.KinematicPattern pattern = kinematic.getElementByTag("pattern").readEnum(Kinematic_Cmpnt.KinematicPattern.class);
					//it changed
					if (kine_p == null || pattern != kine_p.getPattern()) {
						kine_p = Kinematic_Properties._new();
						kine_p.setPattern(pattern);
					}
				}
				if (kine_p == null || kine_p.getPattern() == null)
					throw new MyParseException("Error in Kinematic Parsing:\n(kine_p=null || kine_p.pattern=null) = " + (kine_p == null) + " || " + (kine_p != null && kine_p.getPattern() == null));
				switch (kine_p.getPattern()) {
					case BoxBounce:
						Kinematic_Properties.BoxBounce_Properties box = (Kinematic_Properties.BoxBounce_Properties) kine_p.subClass;
						if (kinematic.hasTag("area")) {
							box.area = kinematic.getElementByTag("area").readBox();
						}
						//If Outside of the box
						if (!box.area.contains(gObj.phy_p.x, gObj.phy_p.y)) {
							gObj.phy_p.x = (box.area.xmin + box.area.xmax) / 2;
							gObj.phy_p.y = (box.area.ymin + box.area.ymax) / 2;
						}
						break;
					case Polygon:
						Kinematic_Properties.Polygon_Properties polygon = (Kinematic_Properties.Polygon_Properties) kine_p.subClass;
						if (kinematic.hasTag("clockwise")) {
							polygon.clockwise = kinematic.getElementByTag("clockwise").readBoolean();
						}
						if (kinematic.hasTag("choreography")) {
							polygon.choreography = kinematic.getElementByTag("choreography").readString();
						}
						if (kinematic.hasTag("points")) {
							List<XML_Element> points = kinematic.getElementByTag("points").getElementsByTag("point");
							polygon.points = new float[points.size()][2];
							int i = 0;
							for (XML_Element point : points) {
								polygon.points[i][X] = point.getElementByTag("x").readFloat();
								polygon.points[i][Y] = point.getElementByTag("y").readFloat();
								i++;
							}
						}
						if (kinematic.hasTag("startingPoint")) {
							polygon.startingPoint = kinematic.getElementByTag("startingPoint").readInt() % polygon.points.length;
						}

						break;
					case Circular:
						Kinematic_Properties.Circular_Properties circle = (Kinematic_Properties.Circular_Properties) kine_p.subClass;
						if (kinematic.hasTag("center")) {
							circle.center = new float[2];
							circle.center[X] = kinematic.getElementByTag("center").getElementByTag("x").readFloat();
							circle.center[Y] = kinematic.getElementByTag("center").getElementByTag("y").readFloat();
						}
						if (kinematic.hasTag("radius")) {
							circle.radius = kinematic.getElementByTag("radius").readFloat();
						}
						if (kinematic.hasTag("angle")) {
							circle.startAngle = kinematic.getElementByTag("angle").readFloat();
						}
						if (kinematic.hasTag("clockwise")) {
							circle.clockwise = kinematic.getElementByTag("clockwise").readBoolean();
						}
						break;
				}
			}
			gObj.phy_p.kine_p = kine_p;
		}
		if(hasTag("drawable")){
			XML_Element drawable = getElementByTag("drawable");
			Drawable_Properties draw_p=null;
			if(gObj.draw_p!=null)
				draw_p = gObj.draw_p;

			if(draw_p!=null && drawable.hasTag("disable")){
				draw_p.free();
				draw_p=null;
			}
			else {
				//Changes Everything anyway
				if (drawable.hasTag("motive")) {
					final Drawable_Cmpnt.Motive motive = drawable.getElementByTag("motive").readEnum(Drawable_Cmpnt.Motive.class);
					//it changed
					if (draw_p == null || motive != draw_p.getMotive()) {
						draw_p = Drawable_Properties._new();
						draw_p.setMotive(motive);
					}
				}
				if (draw_p == null || draw_p.getMotive() == null)
					throw new MyParseException("Error in Drawable Parsing:\n(draw_p=null || draw_p.motive=null) = " + (draw_p == null) + " || " + (draw_p != null && draw_p.getMotive() == null));

				if (drawable.hasTag("epsilon")) {
					draw_p.eps = drawable.getElementByTag("epsilon").readFloat();
				}
				if (drawable.hasTag("lockSpriteAngle")) {
					draw_p.lockSpriteAngle = drawable.getElementByTag("lockSpriteAngle").readBoolean();
				}
				switch (draw_p.getMotive()) {
					case Monochrome:
						Drawable_Properties.MonochromeMotive_Properties monochrome = (Drawable_Properties.MonochromeMotive_Properties) draw_p.motive_p;
						if (drawable.hasTag("color")) {
							monochrome.color = drawable.getElementByTag("color").readColor();
						}
						if (drawable.hasTag("variance")) {
							monochrome.variance = drawable.getElementByTag("variance").readInt();
						}
						break;
					case Bitmap:
						Drawable_Properties.BitmapMotive_Properties bitmap = (Drawable_Properties.BitmapMotive_Properties) draw_p.motive_p;
						if (drawable.hasTag("image")) {
							List<XML_Element> images = drawable.getElementsByTag("image");
							DrawableRes[] options = new DrawableRes[images.size()];
							int i = 0;
							for (XML_Element image_me : images)
								options[i++] = image_me.readEnum(DrawableRes.class);
							bitmap.drawables = new RandomExtractor<>(options);
						}
						if (drawable.hasTag("bias_xMin")) {
							bitmap.bias_xMin = drawable.getElementByTag("bias_xMin").readFloat();
						}
						if (drawable.hasTag("bias_xMax")) {
							bitmap.bias_xMax = drawable.getElementByTag("bias_xMax").readFloat();
						}
						if (drawable.hasTag("bias_yMin")) {
							bitmap.bias_yMin = drawable.getElementByTag("bias_yMin").readFloat();
						}
						if (drawable.hasTag("bias_yMax")) {
							bitmap.bias_yMax = drawable.getElementByTag("bias_yMax").readFloat();
						}
						break;
					case Animated:
					/*AnimatedMotive_Properties animated = (AnimatedMotive_Properties)draw_p.motive_p;
					if (drawable.hasTag("image")){
						List<MyElement> images = drawable.getElementsByTag("image");
						DrawableRes[] options = new DrawableRes[images.size()];
						int i=0;
						for(MyElement image_me : images)
							options[i++] = image_me.readEnum(DrawableRes.class);
						bitmap.drawables = new RandomExtractor<>(options);
					}
					if (drawable.hasTag("offsetDestX")){
						bitmap.offsetDestX = drawable.getElementByTag("offsetDestX").readInt();}
					if (drawable.hasTag("offsetDestY")){
						bitmap.offsetDestY = drawable.getElementByTag("offsetDestY").readInt();}*/
						break;
				}
			}
			gObj.draw_p = draw_p;
		}
		if(hasTag("animation") && gObj.draw_p!=null && gObj.draw_p.getMotive()==Drawable_Cmpnt.Motive.Animated ){
			XML_Element animation = getElementByTag("animation");
			Animation_Properties animation_p=null;
			if(((Drawable_Properties.AnimatedMotive_Properties) gObj.draw_p.motive_p).animation_p!=null)
				animation_p = ((Drawable_Properties.AnimatedMotive_Properties) gObj.draw_p.motive_p).animation_p;

			if(animation_p!=null && animation.hasTag("disable")){
				animation_p.free();
				animation_p=null;
			}
			else {
				if(animation_p==null)
					animation_p = Animation_Properties._new();
				//Changes Everything anyway
				if (animation.hasTag("updateOn")) {
					final AnimationUpdateCondition.UpdateCondition updateOn = animation.getElementByTag("updateOn").readEnum(AnimationUpdateCondition.UpdateCondition.class);
					//it changed
					if (animation_p == null || updateOn!=animation_p.getUpdateCondition()) {
						animation_p.setUpdateCondition(updateOn);
					}
				}
				if (animation.hasTag("sheetType")) {
					final Animation_Cmpnt.SheetType sheetType = animation.getElementByTag("sheetType").readEnum(Animation_Cmpnt.SheetType.class);
					//it changed
					if (animation_p == null || sheetType!=animation_p.getSheetType()) {
						animation_p.setSheetType(sheetType);
					}
				}
				if (animation.hasTag("delay")) {
					animation_p.delayMillis = animation.getElementByTag("delay").readInt();
				}
				switch (animation_p.getUpdateCondition()) {
					case Time:
						break;
					case Direction:{
						AnimationUpdate_Properties.UpdateOnDirection_Properties UoD_p = (AnimationUpdate_Properties.UpdateOnDirection_Properties) animation_p.updateOn_p;
						if (animation.hasTag("toleranceX")) {
							UoD_p.toleranceX = animation.getElementByTag("toleranceX").readFloat();
						}
						if (animation.hasTag("toleranceY")) {
							UoD_p.toleranceY = animation.getElementByTag("toleranceY").readFloat();
						}
						if (animation.hasTag("N")) {
							UoD_p.N = animation.getElementByTag("N").readInt();
						}
						if (animation.hasTag("NW")) {
							UoD_p.NW = animation.getElementByTag("NW").readInt();
						}
						if (animation.hasTag("W")) {
							UoD_p.W = animation.getElementByTag("W").readInt();
						}
						if (animation.hasTag("SW")) {
							UoD_p.SW = animation.getElementByTag("SW").readInt();
						}
						if (animation.hasTag("S")) {
							UoD_p.S = animation.getElementByTag("S").readInt();
						}
						if (animation.hasTag("SE")) {
							UoD_p.SE = animation.getElementByTag("SE").readInt();
						}
						if (animation.hasTag("E")) {
							UoD_p.E = animation.getElementByTag("E").readInt();
						}
						if (animation.hasTag("NE")) {
							UoD_p.NE = animation.getElementByTag("NE").readInt();
						}
						if (animation.hasTag("Idle_Dx")) {
							UoD_p.Idle_DX = animation.getElementByTag("Idle_Dx").readInt();
						}
						if (animation.hasTag("Idle_Sx")) {
							UoD_p.Idle_SX = animation.getElementByTag("Idle_Sx").readInt();
						}

						animation_p.updateOn_p = UoD_p;
						break;}
					case Random:{
						UpdateRandomly_Properties UR_p = (UpdateRandomly_Properties) animation_p.updateOn_p;
						if (animation.hasTag("lingerChance")) {
							UR_p.lingerChance = animation.getElementByTag("lingerChance").readFloat();
						}
						if (animation.hasTag("nAnimations")) {
							UR_p.nAnimations = animation.getElementByTag("nAnimations").readInt();
						}
						break;}
					default:
						break;
				}
				switch (animation_p.getSheetType()) {
					case Single:
						Animation_Properties.SingleSheet_Properties single_p = (Animation_Properties.SingleSheet_Properties) animation_p.sheetUpdate_p;
						if (animation.hasTag("spritesheet")) {
							single_p.sheets = animation.getElementByTag("spritesheet").readEnum(Spritesheets.class);
						}
						animation_p.sheetUpdate_p = single_p;
						break;
					case Multiple:
						Animation_Properties.MultipleSheet_Properties multiple_p = (Animation_Properties.MultipleSheet_Properties) animation_p.sheetUpdate_p;
						if (animation.hasTag("spritesheet")) {
							List<XML_Element> possibleSheets = animation.getElementsByTag("spritesheet");
							multiple_p.sheets = new Spritesheets[possibleSheets.size()];
							int i = 0;
							for (XML_Element sheet : possibleSheets) {
								multiple_p.sheets[i++] = sheet.readEnum(Spritesheets.class);
							}
						}
						if (animation.hasTag("condition")) {
							List<XML_Element> conditions = animation.getElementsByTag("condition");
							for (XML_Element condition : conditions) {
								String name = condition.getElementByTag("name").readString();
								Object value = condition.getElementByTag("value").readValue();
								int sheet = condition.getElementByTag("sheet").readInt();
								multiple_p.addCondition(new Pair<>(name, value), sheet);
							}
						}
						animation_p.sheetUpdate_p = multiple_p;
						break;
					default:
						break;
				}
			}
			((Drawable_Properties.AnimatedMotive_Properties) gObj.draw_p.motive_p).animation_p = animation_p;
		}
		if(hasTag("resizeable")){
			XML_Element resizeable = getElementByTag("resizeable");
			if(gObj.resize_p!=null && resizeable.hasTag("disable")){
				gObj.resize_p.free();
				gObj.resize_p=null;
			}
			else {
				if (gObj.resize_p == null) gObj.resize_p = Resizeable_Properties._new();
				if (resizeable.hasTag("minDimX")) {
					gObj.resize_p.minDimX = resizeable.getElementByTag("minDimX").readFloat();
				}
				if (resizeable.hasTag("minDimY")) {
					gObj.resize_p.minDimY = resizeable.getElementByTag("minDimY").readFloat();
				}
				if (resizeable.hasTag("maxDimX")) {
					gObj.resize_p.maxDimX = resizeable.getElementByTag("maxDimX").readFloat();
				}
				if (resizeable.hasTag("maxDimY")) {
					gObj.resize_p.maxDimY = resizeable.getElementByTag("maxDimY").readFloat();
				}
			}
		}
		if(hasTag("health")){
			XML_Element health = getElementByTag("health");
			if(gObj.health_p!=null && health.hasTag("disable")){
				gObj.health_p.free();
				gObj.health_p=null;
			}
			else {
				if (gObj.health_p == null) gObj.health_p = Health_Properties._new();
				if (health.hasTag("healPriority")) {
					gObj.health_p.healPriority = health.getElementByTag("healPriority").readInt();
				}
				if (health.hasTag("maxHealth")) {
					gObj.health_p.maxHealth = health.getElementByTag("maxHealth").readFloat();
				}
				if (health.hasTag("startHealth")) {
					gObj.health_p.startHealth = health.getElementByTag("startHealth").readFloatValue();
				}
				if (health.hasTag("proportionalHealthDmg")) {
					gObj.health_p.proportionalHealthDmg = health.getElementByTag("proportionalHealthDmg").readFloatValue();
				}
				if (health.hasTag("constDmg")) {
					gObj.health_p.constDmg = health.getElementByTag("constDmg").readFloatValue();
				}
				if (health.hasTag("damageable")) {
					gObj.health_p.damageable = health.getElementByTag("damageable").readBoolean();
				}
				if (health.hasTag("element")) {
					gObj.health_p.element = health.getElementByTag("element").readEnum(GameElement.class);
				}
				if (health.hasTag("onDie")) {
					gObj.health_p.dyingBehaviour = health.getElementByTag("onDie").readEnum(Health_Cmpnt.DyingBehaviour.class);
				}
			}
		}
		if(hasTag("control")){
			XML_Element control = getElementByTag("control");
			if( (gObj.control_p!=null) && (control.hasTag("disable")) ) {
				gObj.control_p.free();
				gObj.control_p = null;
			}
			else {
				gObj.control_p = Control_Properties._new();
				if(control.hasTag("cooldown"))
					gObj.control_p.cooldown = control.getElementByTag("cooldown").readInt();
				if(control.hasTag("holdTimer"))
					gObj.control_p.holdTimer = control.getElementByTag("holdTimer").readInt();
				if(control.hasTag("penalty"))
					gObj.control_p.penalty = control.getElementByTag("penalty").readFloatValue();
			}
		}
		if(hasTag("flags")){
			XML_Element flags = getElementByTag("flags");
			if( (gObj.flag_p!=null) && (flags.hasTag("disable")) ) {
				gObj.flag_p.free();
				gObj.flag_p = null;
			}
			else {
				if (gObj.flag_p == null) gObj.flag_p = Flag_Properties._new();
				for (XML_Element flag : flags.getElementByTag("flags").getChildNodes()) {
					gObj.flag_p.putFlag(flag.getTagName(), flag.readValue());
				}
				if(flags.hasTag("toNotify"))
				for (XML_Element observers : flags.getElementByTag("toNotify").getChildNodes()) {
					gObj.flag_p.putObserver(observers.getTagName(), observers.readEnum(ComponentType.class));
				}
			}
		}
		if(hasTag("ai")){
			XML_Element ai = getElementByTag("ai");
			if(gObj.ai_p!=null && ai.hasTag("disable")){
				gObj.ai_p.free();
				gObj.ai_p=null;
			}
			else {
				String AI_name = ai.readString();
				if (AI_Properties.standardAlreadyExists(AI_name)) {
					if(_Log.LOG_ACTIVE){
						_Log.i("Parser AI", gObj.name + " will have ai " + AI_name);}
					gObj.ai_p = AI_Properties.getStandardAI(AI_name);
				}
				else if(_Log.LOG_ACTIVE){
					_Log.w("Parser", "Couldn't find " + AI_name + " in AI Standards");}
			}
		}
		if(hasTag("summoner")){
			XML_Element summon_tag = getElementByTag("summoner");
			if(gObj.summoner_p!=null && summon_tag.hasTag("disable")){
				gObj.summoner_p.free();
				gObj.summoner_p=null;
			}
			else {
				if (gObj.summoner_p == null) {
					gObj.summoner_p = Summoner_Properties._new();
				}
				if (summon_tag.hasTag("options")) {
					List<XML_Element> ammo_elements = summon_tag.getElementByTag("options").getChildNodes();
					RandomExtractor_Properties<GameObj_Properties> extractor = new RandomExtractor_Properties<>();
					extractor.options = new GameObj_Properties[ammo_elements.size()];
					GameObj_Properties ammo_p;
					int i = 0;
					for (XML_Element ammo_option : ammo_elements) {
						ammo_p = ammo_option.readGOProperty(true);
						extractor.options[i++] = ammo_p;
					}
					gObj.summoner_p.extractor = extractor/*.clone()*/;
				}
				if (hasTag("excludeLast") && gObj.summoner_p.extractor != null)
					gObj.summoner_p.extractor.excludeLast = getElementByTag("excludeLast").readInt();
			}
		}

		if(finalObject) {
			gObj.fixIntraPropertiesConstraint();
			if (!gObj.isReady()) {
				if(_Log.LOG_ACTIVE){
					_Log.e("Parser", "Getting a not ready here:\n"+getTagName()+"\n" + getTextContent());}
				throw gObj.getErrors();
			}
		}
		return gObj;
	}
	/* Returns a List:
	 * for each simple GObj.. makes a newInstance copy
	 * for each commonPropertyList.. a list of new Instances*/
	List<GameObj_Properties> readGOList() throws MyParseException {
		List<XML_Element> commonPLists = getElementsByTag("commonPropertyList");
		List<XML_Element> simpleGObjList = getElementsByTag("gObject");

		List<GameObj_Properties> gObjects = new LinkedList<>();
		for(XML_Element simple : simpleGObjList){
			gObjects.add(simple.readGOProperty(true)/*.clone()*/);
		}
		for(XML_Element commonPList : commonPLists){
			gObjects.addAll(commonPList.readCommonPropGOList());
		}
		return gObjects;
	}
	/*  Returns a List:
	 * for each simple GObj.. passes a new instance based on root
	 *for each commonPropertyList.. passes a new instance based on root*/
	List<GameObj_Properties> readGOList(GameObj_Properties rootProperty) throws MyParseException {
		List<XML_Element> commonPLists = getElementsByTag("commonPropertyList");
		List<XML_Element> simpleGObjList = getElementsByTag("gObject");

		List<GameObj_Properties> gObjects = new LinkedList<>();
		for(XML_Element simple : simpleGObjList){
			gObjects.add(simple.readGOProperty(rootProperty.clone(), false, true));
		}
		for(XML_Element commonPList : commonPLists){
			gObjects.addAll(commonPList.readCommonPropGOList(rootProperty.clone()));
		}
		return gObjects;
	}
	/* Returns readGOPropertyList(commonPList, cP)
	 * where cP is a new Instance built with properties found in <commonProperty>
	 *
	 *Lettura dei nodi <commonPropertyList>*/
	List<GameObj_Properties> readCommonPropGOList() throws MyParseException {
		if(!hasTag("commonProperty"))
			throw new MyParseException("Expected non Nullable tag <commonProperty> in GameObj_Builder common property list");
		final GameObj_Properties commonProperty = getElementByTag("commonProperty").readGOProperty(false)/*.clone()*/;
		return readGOList(commonProperty);
	}
	/* Returns readGOPropertyList(commonPList, cP)
	 * where cP is the input instance modified with properties found in <commonProperty>
	 *
	 *Lettura dei nodi <commonPropertyList>*/
	List<GameObj_Properties> readCommonPropGOList(GameObj_Properties previousCommonProperty) throws MyParseException {
		if(!hasTag("commonProperty"))
			throw new MyParseException("Expected non Nullable tag <commonProperty> in GameObj_Builder common property list");
		GameObj_Properties commonProperty = getElementByTag("commonProperty").readGOProperty(previousCommonProperty, false, false);
		return readGOList(commonProperty);
	}

	//* * * * * * * * * * * * * *  Level  * * * * * * * * * * * * *
	StoryNarration_Properties readStoryProperties() throws MyParseException {
		StoryNarration_Properties story = StoryNarration_Properties._new();
		if(hasTag("timePerScene"))
			story.requiredTime = getElementByTag("timePerScene").readFloat();
		if(hasTag("topDown"))
			story.topDown = getElementByTag("topDown").readBoolean();

		List<XML_Element> scenes = getElementsByTag("scene");
		MyBackgrounds[] images = new MyBackgrounds[scenes.size()];
		int i=0;
		for(XML_Element scene : scenes) {
			images[i++] = scene.readEnum(MyBackgrounds.class);
		}
		story.images=images;
		return story;
	}
	/* Read the <sections> list
	 * Will create a nonStaticCopy of SectionProperty for each call if there's
	 * a <common property>*/
	Section_Properties[] readSections(String name) throws MyParseException {
		List<XML_Element> sections = getElementsByTag("section");
		Section_Properties[] retSections = new Section_Properties[sections.size()];
		if(hasTag("commonProperty")){
			Section_Properties commonProp = getElementByTag("commonProperty").readSectionProperty(name, false);
			int i=0;
			for(XML_Element sec : sections){
				if(_Log.LOG_ACTIVE)
					_Log.i("Parser", "Reading section "+i);
				retSections[i] = sec.readSectionProperty(name, commonProp.clone(), true);
				i++;
			}
		}
		else{
			int i=0;
			for(XML_Element sec : sections){
				if(_Log.LOG_ACTIVE)
					_Log.i("Parser", "Reading section "+i);
				retSections[i] = sec.readSectionProperty(name, true);
				i++;
			}
		}
		return retSections;
	}

	/* Reads Section Properties, without any prior(root) information*/
	Section_Properties readSectionProperty(String name, boolean finalSection) throws MyParseException {
		Section_Properties section = Section_Properties._new();
		return readSectionProperty(name, section, finalSection);
	}

	/* Reads Section Properties, overwriting the given root properties
	 * N.B.: readSectionProperty(String name) will call this method
	 * with a new instance as root.
	 * Every other call should pass a copy of the root*/
	Section_Properties readSectionProperty(String name, Section_Properties root, boolean finalSection) throws MyParseException {
		if (hasTag("background")){
			root.background = getElementByTag("background").readEnum(MyBackgrounds.class);}
		if (hasTag("physicalBox")){
			root.physical = getElementByTag("physicalBox").readBox();}
		if (hasTag("damageFreeZone")){
			root.damageFreeZone = getElementByTag("damageFreeZone").readBox();}
		if (hasTag("touchZone")){
			final List<XML_Element> touchBoxes = getElementByTag("touchZone").getElementsByTag("touchBox");
			root.touchBoxes = new Box[touchBoxes.size()];
			int i=0;
			for(XML_Element touchBox : touchBoxes)
				root.touchBoxes[i++]=touchBox.readBox();
		}
		if (hasTag("forwardUpdate")){
			XML_Element forwardUpdate=getElementByTag("forwardUpdate");
			if(forwardUpdate.hasTag("x"))
				root.forwardUpdate_x = forwardUpdate.getElementByTag("x").readFloat();
			if(forwardUpdate.hasTag("y"))
				root.forwardUpdate_y = forwardUpdate.getElementByTag("y").readFloat();
		}
		if (hasTag("backwardUpdate")){
			XML_Element backwardUpdate=getElementByTag("backwardUpdate");
			if(backwardUpdate.hasTag("x"))
				root.backwardUpdate_x = backwardUpdate.getElementByTag("x").readFloat();
			if(backwardUpdate.hasTag("y"))
				root.backwardUpdate_y = backwardUpdate.getElementByTag("y").readFloat();
		}
		if (hasTag("view")){
			XML_Element view=getElementByTag("view");
			if(view.hasTag("width"))
				root.viewWidth = view.getElementByTag("width").readFloat();
			if(view.hasTag("height"))
				root.viewHeight = view.getElementByTag("height").readFloat();
		}
		if (hasTag("gravity")){
			XML_Element velocity = getElementByTag("gravity");
			try {
				float x = velocity.getElementByTag("x").readFloat();
				float y = velocity.getElementByTag("y").readFloat();
				root.gravity=new Vec2(x,y);
			}catch(MyParseException myE) {
				if (_Log.LOG_ACTIVE) {
					_Log.e(myE);}
			}
		}
		if (hasTag("enclosure")){
			XML_Element enclosure = getElementByTag("enclosure");
			if(root.enclosement == null)
				root.enclosement = Section_Properties.Enclosement._new();
			if(enclosure.hasTag("constant0_percentage1")){
				root.enclosement.constant0_percentage1=enclosure.getElementByTag("constant0_percentage1").readBoolean();}
			if(enclosure.hasTag("engorgement")){
				root.enclosement.engorgement=enclosure.getElementByTag("engorgement").readFloat();}
			if(enclosure.hasTag("onHitBehaviour")){
				root.enclosement.oHB = enclosure.getElementByTag("onHitBehaviour").readEnum(GameObject.OnHitBehaviour.class);}
		}
		if (hasTag("floor")){
			XML_Element floor = getElementByTag("floor");
			if(root.floor == null)
				root.floor = Section_Properties.Floor._new();
			if(floor.hasTag("std"))
				root.floor.std = floor.getElementByTag("std").readGOProperty(root.floor.std, true, true)/*.clone()*/;
			if(floor.hasTag("touchable"))
				root.floor.touchable = floor.getElementByTag("touchable").readGOProperty(root.floor.touchable, true, true)/*.clone()*/;
			if(floor.hasTag("end"))
				root.floor.end = floor.getElementByTag("end").readGOProperty(root.floor.end, true, true)/*.clone()*/;
			if (floor.hasTag("endBox")){
				root.floor.endBox = floor.getElementByTag("endBox").readBox();}
			if(floor.hasTag("damageSkip"))
				root.floor.damageSkip = floor.getElementByTag("damageSkip").readInt();
		}
		if (hasTag("rampages")){ //Always overwrote if has tag
			List<XML_Element> rampages = getElementByTag("rampages").getElementsByTag("rampage");
			//if(root.rampages == null)
			root.rampages = new Section_Properties.Rampage[rampages.size()];
			int i=0;
			for(XML_Element ramp : rampages){
				Section_Properties.Rampage rampage = Section_Properties.Rampage._new();
				//GOProperty will be copied anyway below
				rampage.obj=ramp.getElementByTag("gObject").readGOProperty(true)/*.nonStaticCopy()*/;
				if( ramp.hasTag("baseY")){
					rampage.baseY =ramp.getElementByTag("baseY").readFloat();}
				else throw new MyParseException("Expected non Nullable tag <baseY> in Rampage "+name);
				if( ramp.hasTag("fromX")){
					rampage.fromX =ramp.getElementByTag("fromX").readFloat();}
				else throw new MyParseException("Expected non Nullable tag <fromX> in Rampage "+name);
				if( ramp.hasTag("toX")){
					rampage.toX =ramp.getElementByTag("toX").readFloat();}
				else throw new MyParseException("Expected non Nullable tag <toX> in Rampage "+name);
				if( ramp.hasTag("angle")){
					rampage.angle=ramp.getElementByTag("angle").readFloat();}
				else throw new MyParseException("Expected non Nullable tag <angle> in Rampage "+name);
				if( ramp.hasTag("verticalSustain")){
					rampage.verticalSustain=ramp.getElementByTag("verticalSustain").readBoolean();}
				if( ramp.hasTag("horizontalSustain")){
					rampage.horizontalSustain=ramp.getElementByTag("horizontalSustain").readBoolean();}

				root.rampages[i++] = rampage/*.clone()*/;
				//rampage.reset();
			}
			//rampage.free();//did not exist
		}
		if (hasTag("spawns")){ //Always overwrote if has tag
			List<XML_Element> spawns_me = getElementByTag("spawns").getElementsByTag("spawn");
			root.spawns_p = new PeriodicSpawnGameObj_Properties[spawns_me.size()];
			int i=0;
			for(XML_Element spawn_me : spawns_me){
				PeriodicSpawnGameObj_Properties psgo_p = PeriodicSpawnGameObj_Properties._new();
				if( spawn_me.hasTag("spawnZone")){
					psgo_p.spawnZone = spawn_me.getElementByTag("spawnZone").readBox();}
				else throw new MyParseException("Expected non Nullable tag <spawnZone> in PeriodicSpawnGameObj_Properties "+name);
				if( spawn_me.hasTag("cooldownMillisec")){
					psgo_p.cooldownMillisec = spawn_me.getElementByTag("cooldownMillisec").readInt();}
				else throw new MyParseException("Expected non Nullable tag <cooldownMillisec> in PeriodicSpawnGameObj_Properties "+name);
				if( spawn_me.hasTag("maximumDelay")){
					psgo_p.maximumDelay = spawn_me.getElementByTag("maximumDelay").readInt();}
				if( spawn_me.hasTag("excludeLast")){
					psgo_p.excludeLast = spawn_me.getElementByTag("excludeLast").readInt();}
				if( spawn_me.hasTag("options")){
					List<XML_Element> options_list = spawn_me.getElementByTag("options").getChildNodes();
					psgo_p.gObjOptions = new GameObj_Properties[options_list.size()];
					int o=0;
					for(XML_Element option : options_list){
						psgo_p.gObjOptions[o] = option.readGOProperty(true)/*.clone()*/;
						o++;
					}
				}

				root.spawns_p[i++] = psgo_p/*.clone()*/;
				//psgo_p.reset();
			}
			//psgo_p.free();//was not
		}
		if (hasTag("MainObj")){
			/* In both cases the returned value will be a copy*/
			if(root.mainObj!=null)
				root.mainObj = getElementByTag("MainObj").readGOProperty(root.mainObj, false, true);
			else
				root.mainObj = getElementByTag("MainObj").readGOProperty(true)/*.clone()*/;
		}
		if (hasTag("gameObjects")){
			XML_Element gameObjects = getElementByTag("gameObjects");
			root.gObjects = gameObjects.readGOList();
			if(gameObjects.hasTag("fillZone")){
				List<XML_Element> fillZones = gameObjects.getElementsByTag("fillZone");
				int i=0;
				root.zoneFills = new Section_Properties.ZoneFill_Properties[fillZones.size()];
				for(XML_Element fz : fillZones){
					Section_Properties.ZoneFill_Properties zf_p=Section_Properties.ZoneFill_Properties._new();
					if(fz.hasTag("isAesthetic"))
						zf_p.isAesthetic = fz.getElementByTag("isAesthetic").readBoolean();
					if(fz.hasTag("zone"))
						zf_p.zone = fz.getElementByTag("zone").readBox();
					if(fz.hasTag("howMany"))
						zf_p.howMany = fz.getElementByTag("howMany").readInt();
					if(fz.hasTag("howManyAesthetic"))
						zf_p.howManyAesthetic = fz.getElementByTag("howManyAesthetic").readInt();
					zf_p.options = new RandomExtractor_Properties<>();
					if(fz.hasTag("excludeLast"))
						zf_p.options.excludeLast= fz.getElementByTag("excludeLast").readInt();
					if(fz.hasTag("options")) {
						XML_Element options = fz.getElementByTag("options");
						List<XML_Element> options_me = options.getChildNodes();
						int j=0;
						zf_p.options.options = new GameObj_Properties[options_me.size()];
						for(XML_Element go_me : options_me){
							GameObj_Properties go_option;
							go_option = go_me.readGOProperty(true);
							zf_p.options.options[j] = go_option/*.clone()*/;
							j++;
							//go_option.free();//was reset()
						}
					}
					if(zf_p.isReady())
						root.zoneFills[i] = zf_p/*.clone()*/;
					else if(_Log.LOG_ACTIVE){
						_Log.e("Parser", "Getting a not ready here (Zone Fill):\n"+fz.getTextContent()+
								"\n"+zf_p.getErrors().getMessage());}
					i++;
					//zf_p.reset();//was reset()
				}
				//zf_p.free();//was not here
			}
		}

		if(finalSection && !root.isReady()) {
			if(_Log.LOG_ACTIVE){
				_Log.e("Parser", "Getting a not ready here:\n"+getTextContent());}
			throw root.getErrors();
		}
		return root;
	}

	Credits_Properties readCreditsProperty() throws MyParseException {
		Credits_Properties credits_p = Credits_Properties._new();

		if(hasTag("anteStory")){
			credits_p.storyAnte = getElementByTag("anteStory").readStoryProperties();
		}
		if(hasTag("postStory")){
			credits_p.storyPost = getElementByTag("postStory").readStoryProperties();
		}
		if(hasTag("credits")){
			credits_p.credits = getElementByTag("credits").readStoryProperties();
		}
		if(hasTag("easterEggCredits")){
			credits_p.easterEggCredits = getElementByTag("easterEggCredits").readStoryProperties();
		}
		if(hasTag("onEnd")){
			List<XML_Element> onEnds = getElementByTag("onEnd").getElementsByTag("persistence");
			credits_p.onEndKeys= new String[onEnds.size()];
			credits_p.onEndValues= new boolean[onEnds.size()];
			int i=0;
			for(XML_Element onEnd : onEnds){
				credits_p.onEndKeys[i] = onEnd.getElementByTag("key").readString();
				credits_p.onEndValues[i] = onEnd.getElementByTag("value").readBoolean();
				i++;
			}
		}
		if(hasTag("triggerEasterEgg")){
			List<XML_Element> keys = getElementByTag("triggerEasterEgg").getElementsByTag("key");
			credits_p.triggerEE = new String[keys.size()];
			int i=0;
			for(XML_Element key : keys){
				credits_p.triggerEE[i] = key.readString();
				i++;
			}
		}

		if(!credits_p.isReady()) {
			if(_Log.LOG_ACTIVE)
				_Log.e("Parser", "Getting a not ready here:<"+getTagName()+">:\n"+getTextContent());
			throw credits_p.getErrors();
		}
		return credits_p;
	}

	public Level_onDemand_Properties readLevel_onDemand_Property(String lvlName) throws MyParseException {
		Level_onDemand_Properties lvl = Level_onDemand_Properties._new();
		if(_Log.LOG_ACTIVE)
			_Log.i("Parser", "Reading Lvl "+lvlName);
		lvl.name=lvlName;
		if(hasTag("anteStory")){
			lvl.storyAnte = getElementByTag("anteStory").readStoryProperties();
		}
		if(hasTag("postStory")){
			lvl.storyPost = getElementByTag("postStory").readStoryProperties();
		}
		if(hasTag("sections")) {
			XML_Element sections = getElementByTag("sections");
			lvl.sec_p = sections.getElementsByTag("section").toArray(Level_onDemand_Properties._0);
			if (sections.hasTag("commonProperty")) {
				lvl.common_p = sections.getElementByTag("commonProperty").readSectionProperty(lvlName, false);
			}
		}
		if(hasTag("onWin")){
			List<XML_Element> onWins = getElementByTag("onWin").getElementsByTag("persistence");
			lvl.onWinKeys= new String[onWins.size()];
			lvl.onWinValues= new boolean[onWins.size()];
			int i=0;
			for(XML_Element onWin : onWins){
				lvl.onWinKeys[i] = onWin.getElementByTag("key").readString();
				lvl.onWinValues[i] = onWin.getElementByTag("value").readBoolean();
				i++;
			}
		}
		if(hasTag("onLose")){
			List<XML_Element> onLoses = getElementByTag("onLose").getElementsByTag("persistence");
			lvl.onLoseKeys= new String[onLoses.size()];
			lvl.onLoseValues= new boolean[onLoses.size()];
			int i=0;
			for(XML_Element onLose : onLoses){
				lvl.onLoseKeys[i] = onLose.getElementByTag("key").readString();
				lvl.onLoseValues[i] = onLose.getElementByTag("value").readBoolean();
				i++;
			}
		}

		if(!lvl.isReady())
			throw lvl.getErrors();
		return lvl;
	}

	Level_FullLoaded_Properties readLevel_FullLoaded_Property(String lvlName) throws MyParseException {
		Level_FullLoaded_Properties lvl = Level_FullLoaded_Properties._new();
		if(_Log.LOG_ACTIVE)
			_Log.i("Parser", "Reading Lvl "+lvlName);

		if(hasTag("anteStory")){
			lvl.storyAnte = getElementByTag("anteStory").readStoryProperties();
		}
		if(hasTag("postStory")){
			lvl.storyPost = getElementByTag("postStory").readStoryProperties();
		}
		if(hasTag("sections")){
			lvl.sec_p = getElementByTag("sections").readSections(lvlName);}
		if(hasTag("onWin")){
			List<XML_Element> onWins = getElementByTag("onWin").getElementsByTag("persistence");
			lvl.onWinKeys= new String[onWins.size()];
			lvl.onWinValues= new boolean[onWins.size()];
			int i=0;
			for(XML_Element onWin : onWins){
				lvl.onWinKeys[i] = onWin.getElementByTag("key").readString();
				lvl.onWinValues[i] = onWin.getElementByTag("value").readBoolean();
				i++;
			}
		}
		if(hasTag("onLose")){
			List<XML_Element> onLoses = getElementByTag("onLose").getElementsByTag("persistence");
			lvl.onLoseKeys= new String[onLoses.size()];
			lvl.onLoseValues= new boolean[onLoses.size()];
			int i=0;
			for(XML_Element onLose : onLoses){
				lvl.onLoseKeys[i] = onLose.getElementByTag("key").readString();
				lvl.onLoseValues[i] = onLose.getElementByTag("value").readBoolean();
				i++;
			}
		}

		if(!lvl.isReady())
			throw lvl.getErrors();
		return lvl;
	}
	//* * * * * * * * * * * * * *  Decision Tree  * * * * * * * * * * * * *

	/**Reads a Decision Tree Properties, should read a <Actions> and a <root>*/
	DecisionTree_Properties readDecisionTreeProperties() throws MyParseException{
		DecisionTree_Properties dt_p = DecisionTree_Properties._new();
		dt_p.name = getElementByTag("name").readString();
		dt_p.actions = getElementByTag("Actions").readActions();
		dt_p.root = getElementByTag("root").getFirstChild().readDTNode();
		return dt_p;
	}

	/**Reads the <Actions> node, returning the ArrayMap of the root Decision Tree*/
	ArrayMap<String, Action_Properties> readActions() throws MyParseException {
		//starts in <Actions>
		final List<XML_Element> actionNodes = getChildNodes();
		final ArrayMap<String, Action_Properties> actions = new ArrayMap<>(actionNodes.size());
		String tagName;
		for (XML_Element node : actionNodes){
			tagName=node.getTagName();
			actions.put(tagName, node.getFirstChild().readAction(tagName));
		}
		return actions;
	}

	/**Reads a single Action_P, starting from the node named as the type*/
	Action_Properties readAction(String actionName) throws MyParseException {
		//Starts in <"type"> € {Cooldown. Chance, Multiple, FlagSetter, Apply..Impulse}
		//_Log.w("Parser", "Reading Action "+actionName+". type "+getTagName());
		Action_Properties action=null;
		switch (getTagName()){
			case "NoAction":{
				action = new A_NoAction_Properties();
				break;}
			case "Cooldown":{
				action = new A_Cooldown_Properties();
				A_Cooldown_Properties cd_action = (A_Cooldown_Properties)action;

				if(hasTag("millisec")){
					cd_action.millisec = getElementByTag("millisec").readInt();}
				if(hasTag("action")){
					cd_action.action = getElementByTag("action").getFirstChild().readAction(actionName);
				}
				break;}
			case "Chance":{
				action = new A_Chance_Properties();
				A_Chance_Properties chance_action = (A_Chance_Properties)action;

				if(hasTag("chance")){
					chance_action.chance = getElementByTag("chance").readFloatValue();}
				if(hasTag("action")){
					chance_action.action = getElementByTag("action").getFirstChild().readAction(actionName);
				}
				break;}
			case "FlagSetter":{
				action = new A_FlagSetter_Properties();
				A_FlagSetter_Properties fs_action = (A_FlagSetter_Properties)action;

				if(hasTag("flagName")){
					fs_action.flagName = getElementByTag("flagName").readString();}
				if(hasTag("value")){
					fs_action.value = getElementByTag("value").readValue();}
				break;}
			case "Multiple":{
				action = new A_Multiple_Properties();
				A_Multiple_Properties multi_action = (A_Multiple_Properties)action;

				final List<XML_Element> childActions = getChildNodes();
				multi_action.actions = new Action_Properties[childActions.size()];
				int i=0;
				for(XML_Element childA : childActions)
					multi_action.actions[i++] = childA.readAction(actionName);
				break;}
			case "ApplyDirectedImpulse":{
				action = new A_ApplyDirectedImpulse_Properties();
				A_ApplyDirectedImpulse_Properties adi_action = (A_ApplyDirectedImpulse_Properties)action;

				if( hasTag("intensity") ){
					adi_action.intensity = getElementByTag("intensity").readFloatValue();}
				if( hasTag("biasX") ){
					adi_action.biasX = getElementByTag("biasX").readFloatValue();}
				if( hasTag("biasY") ){
					adi_action.biasY = getElementByTag("biasY").readFloatValue();}
				if( hasTag("x") ){
					adi_action.x = getElementByTag("x").readBoolean();}
				if( hasTag("y") ){
					adi_action.y = getElementByTag("y").readBoolean();}
				if( hasTag("weight") ){
					adi_action.weight = getElementByTag("weight").readBoolean();}
				if( hasTag("field") ){
					adi_action.field = getElementByTag("field").readField();}
				if( hasTag("saveAs") ){
					adi_action.saveDirectionAs = getElementByTag("saveAs").readString();}
				break;}
			case "ApplyTargetedImpulse":{
				action = new A_ApplyTargetedImpulse_Properties();
				A_ApplyTargetedImpulse_Properties adi_action = (A_ApplyTargetedImpulse_Properties)action;

				if( hasTag("intensity") ){
					adi_action.intensity = getElementByTag("intensity").readFloatValue();}
				if( hasTag("biasX") ){
					adi_action.biasX = getElementByTag("biasX").readFloatValue();}
				if( hasTag("biasY") ){
					adi_action.biasY = getElementByTag("biasY").readFloatValue();}
				if( hasTag("x") ){
					adi_action.x = getElementByTag("x").readBoolean();}
				if( hasTag("y") ){
					adi_action.y = getElementByTag("y").readBoolean();}
				if( hasTag("weight") ){
					adi_action.weight = getElementByTag("weight").readBoolean();}
				if( hasTag("towards") ){
					adi_action.towards = getElementByTag("towards").readBoolean();}
				if( hasTag("field") ){
					adi_action.field = getElementByTag("field").readField();}
				if( hasTag("saveAs") ){
					adi_action.saveDirectionAs = getElementByTag("saveAs").readString();}
				break;}
			case "ApplyRandomImpulse":{
				action = new A_ApplyRandomImpulse_Properties();
				A_ApplyRandomImpulse_Properties ari_action = (A_ApplyRandomImpulse_Properties)action;

				if( hasTag("intensity") ){
					ari_action.intensity = getElementByTag("intensity").readFloatValue();}
				if( hasTag("dirX") ){
					ari_action.dirX = getElementByTag("dirX").readFloatValue();}
				if( hasTag("dirY") ){
					ari_action.dirY = getElementByTag("dirY").readFloatValue();}
				if( hasTag("x") ){
					ari_action.x = getElementByTag("x").readBoolean();}
				if( hasTag("y") ){
					ari_action.y = getElementByTag("y").readBoolean();}
				if( hasTag("weight") ){
					ari_action.weight = getElementByTag("weight").readBoolean();}
				if( hasTag("saveAs") ){
					ari_action.saveDirectionAs = getElementByTag("saveAs").readString();}
				break;}
			case "Shoot":{
				action = new A_Shoot_Properties();
				A_Shoot_Properties ari_action = (A_Shoot_Properties)action;
				if( hasTag("aim") ){
					Action_Properties aim = getElementByTag("aim").readAction("Aim");
					if(aim instanceof A_ApplyTargetedImpulse_Properties)
						ari_action.adi_p = (A_ApplyTargetedImpulse_Properties) aim;
					else
						throw new IllegalArgumentException("in Action_Shoot_Properties\n" +
								"under tag <aim> the action must be: A_ApplyDirectedImpulse_Properties\n" +
								"found "+aim.getClass().getSimpleName());
				}
				if( hasTag("x") ){
					ari_action.x = getElementByTag("x").readFloatValue();}
				if( hasTag("y") ){
					ari_action.y = getElementByTag("y").readFloatValue();}
				break;}
			default:
				//_Log.w("Parser", "Unrecognized tag <"+getTagName()+"> while reading action "+actionName+"\n\t..will try to search in FirstChild..");
				return getFirstChild().readAction(actionName);
		}
		action.name=actionName;
		//It is okay if it produces NullPointerException
		if(!action.isReady()){
			if(_Log.LOG_ACTIVE){
				_Log.e("Parser", "Getting a notReady here: <"+getTagName()+">\n"+getTextContent());}
			throw action.getErrors();
		}
		//_Log.w("Parser", "Read Action "+getTagName()+" is "+action.getClass().getSimpleName());
		return action;
	}

	/**Reads a Field_P, starting from the node named as the type*/
	<F> Field_Properties<F> readField() throws MyParseException {
		//Starts in <"type">
		//_Log.w("Parser", "Reading Field type "+getTagName());
		Field_Properties field=null;
		switch (getTagName()){
			case "Flag":{
				field = allocateParametricField();
				F_Flag_Properties flag_field = (F_Flag_Properties)field;
				if(hasTag("flagName"))
					flag_field.flagName = getElementByTag("flagName").readString();
				break;}
			case "Health":{
				field = new F_Health_Properties();
				F_Health_Properties health_field = (F_Health_Properties)field;

				if(hasTag("saveAs"))
					health_field.saveAs = getElementByTag("saveAs").readString();
				break;}
			case "Distance":{
				//Should F not be Float is going to crash
				field = new F_Distance_Properties();
				F_Distance_Properties dist_field = (F_Distance_Properties)field;

				if(hasTag("saveAs"))
					dist_field.saveAs = getElementByTag("saveAs").readString();
				/*if(hasTag("objID"))
					dist_field.gameObj_ID = getElementByTag("objID").readInt();*/
				if( hasTag("field") ){
					dist_field.field = getElementByTag("field").readField();}
				if(hasTag("x"))
					dist_field.x = getElementByTag("x").readBoolean();
				if(hasTag("y"))
					dist_field.y = getElementByTag("y").readBoolean();
				break;}
			case "DistanceFromElement":{
				//Should F not be Float is going to crash
				field = new F_DistanceFromElement_Properties();
				F_DistanceFromElement_Properties dfe_field = (F_DistanceFromElement_Properties)field;

				if(hasTag("saveAs"))
					dfe_field.saveAs = getElementByTag("saveAs").readString();
				if(hasTag("saveArgmin"))
					dfe_field.saveArgmin = getElementByTag("saveArgmin").readString();
				if(hasTag("element"))
					dfe_field.element = getElementByTag("element").readEnum(GameElement.class);
				break;}
			case "NearestElement":{
				field = new F_NearestElement_Properties();
				F_NearestElement_Properties ne_field = (F_NearestElement_Properties)field;

				if(hasTag("saveAs"))
					ne_field.saveAs = getElementByTag("saveAs").readString();
				if(hasTag("saveArgmin"))
					ne_field.saveArgmin = getElementByTag("saveArgmin").readString();
				if(hasTag("element"))
					ne_field.element = getElementByTag("element").readEnum(GameElement.class);
				if(_Log.LOG_ACTIVE){
					_Log.w("Leaf", "Read in Parser: @Deprecated NearestElement "+ne_field.element);}
				break;}
			default:
				//_Log.w("Parser", "Unrecognized tag <"+getTagName()+"> while reading field\nWill try to search in FirstChild..");
				return getFirstChild().readField();
		}
		if(!field.isReady()){
			if(_Log.LOG_ACTIVE){
				_Log.e("Parser", "Getting a notReady here <"+getTagName()+">\n"+getTextContent());}
			throw field.getErrors();
		}
		//_Log.i("Parser", "Read Field "+getTagName()+" is "+field.getClass().getSimpleName());
		return field;
	}

	Field_Properties allocateParametricField() {
		switch (getTagName()){
			case "Flag":{
				switch (getAttribute("type").toLowerCase()){
					case "boolean":
						return new F_Flag_Properties<Boolean>();
					case "string":
						return new F_Flag_Properties<String>();
					case "int":
						return new F_Flag_Properties<Integer>();
					case "integer":
						return new F_Flag_Properties<Integer>();
					case "float":
						return new F_Flag_Properties<Float>();
					case "float[]":
						return new F_Flag_Properties<float[]>();
					case "gameobject":
						return new F_Flag_Properties<GameObject>();
				}
				break;
			}
		}
		if(_Log.LOG_ACTIVE){
			_Log.e("Parser", "Couldn't allocate Parametric Field: <"+getTagName()+" type=\""+getAttribute("type")+"\">");}
		return null;
	}

	//Reading nodes
	DTNode_Properties readDTNode() throws MyParseException {
		//Starts in type name
		DTNode_Properties node = null;
		switch (getTagName()){
			case "Cooldown":{
				node = new N_Cooldown_Properties();
				N_Cooldown_Properties cd_node = (N_Cooldown_Properties)node;

				if(hasTag("millisec")){
					cd_node.millisec = getElementByTag("millisec").readInt();}
				if(hasTag("onTrue")){
					cd_node.onTrue = getElementByTag("onTrue").readDTNode();}
				if(hasTag("onFalse")){
					cd_node.onFalse = getElementByTag("onFalse").readDTNode();}
				break;}
			case "Chance":{
				node = new N_Chance_Properties();
				N_Chance_Properties chance_node = (N_Chance_Properties)node;

				if(hasTag("chance")){
					chance_node.chance = getElementByTag("chance").readFloatValue();}
				if(hasTag("onTrue")){
					chance_node.onTrue = getElementByTag("onTrue").readDTNode();}
				if(hasTag("onFalse")){
					chance_node.onFalse = getElementByTag("onFalse").readDTNode();}
				break;}
			case "IntervalNode":{
				node = new N_Interval_Properties();
				N_Interval_Properties interval_node = (N_Interval_Properties)node;

				if(hasTag("min"))
					interval_node.min = getElementByTag("min").readFloat();
				if(hasTag("max"))
					interval_node.max = getElementByTag("max").readFloat();
				if(hasTag("field"))
					interval_node.field = getElementByTag("field").readField();
				if(hasTag("onTrue"))
					interval_node.onTrue = getElementByTag("onTrue").readDTNode();
				if(hasTag("onFalse"))
					interval_node.onFalse = getElementByTag("onFalse").readDTNode();
				break;}
			case "BoolFieldNode":{
				node = new N_BoolField_Properties();
				N_BoolField_Properties bf_node = (N_BoolField_Properties)node;

				if(hasTag("field"))
					bf_node.field = getElementByTag("field").getFirstChild().readField();
				if(hasTag("onTrue"))
					bf_node.onTrue = getElementByTag("onTrue").readDTNode();
				if(hasTag("onFalse"))
					bf_node.onFalse = getElementByTag("onFalse").readDTNode();
				break;}
			case "Leaf":{
				node = new Leaf_Properties();
				Leaf_Properties leaf = (Leaf_Properties)node;

				if(hasTag("Log"))
					leaf.priority = getElementByTag("Log").readEnum(_Log.Priority.class);
				if(hasTag("action"))
					leaf.actionName = getElementByTag("action").getFirstChild().getTagName();
				break;}
			case "FirstCallLeaf":{
				node = new L_FirstCall_Properties();
				L_FirstCall_Properties fcl_node = (L_FirstCall_Properties)node;

				if(hasTag("Log"))
					fcl_node.priority = getElementByTag("Log").readEnum(_Log.Priority.class);
				if(hasTag("Standard"))
					fcl_node.actionName = getElementByTag("Standard").getFirstChild().getTagName();
				if(hasTag("FirstTime"))
					fcl_node.firstCallActionName = getElementByTag("FirstTime").getFirstChild().getTagName();
				break;}
			default:
				return getFirstChild().readDTNode();
		}
		if (node==null && _Log.LOG_ACTIVE){
			_Log.e("Parser", "node is null in <"+getTagName()+"> below <"+element.getParentNode().getNodeName()+">");}
		if(!node.isReady()){
			if(_Log.LOG_ACTIVE){
				_Log.e("Parser", "Getting a notReady here <"+getTagName()+">\n"+getTextContent());}
			throw node.getErrors();
		}
		return node;
	}
}