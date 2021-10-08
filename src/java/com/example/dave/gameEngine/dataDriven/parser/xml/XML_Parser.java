package com.example.dave.gameEngine.dataDriven.parser.xml;

import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataDriven.Level_Properties;
import com.example.dave.gameEngine.dataDriven.Level_onDemand_Properties;
import com.example.dave.gameEngine.dataDriven.Section_Properties;
import com.example.dave.gameEngine.dataDriven.parser.Credits_Properties;
import com.example.dave.gameEngine.dataDriven.parser.MyParseException;
import com.example.dave.gameEngine.dataDriven.parser.Parser;
import com.example.dave.gameEngine.dataDriven.ai.*;
import com.example.dave.gameEngine._Log;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

public class XML_Parser implements Parser {
	private final static XML_Parser instance = new XML_Parser();

	public static Parser getInstance() {
		return instance;
	}

	/* Pre Loading Game Objects*/
	@Override public void parseStdGOProperties(InputStream iStream) {
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(iStream);
		}catch (Exception e) {
			if(_Log.LOG_ACTIVE){
				_Log.e(e);}
			return;
		}

		List<XML_Element> elements = XML_Element.getElementsByTag(doc, "GameObj_Property");
		for(XML_Element element : elements){
			GameObj_Properties gObj;
			try {
				 gObj = element.readGOProperty(false);
				 gObj.addDataDrivenStandard();
				 gObj.free();
			}
			catch(MyParseException mpe){
				if(_Log.LOG_ACTIVE){
					_Log.e("XML Parser", mpe);}
			}
		}
	}

	/* On demand loading of a Level*/
	@Override public Level_Properties parseLevelProperties(InputStream iStream, String tag) throws MyParseException {
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(iStream);
		}catch (Exception e) {
			if(_Log.LOG_ACTIVE){
				_Log.e(e);}
			return null;
		}

		XML_Element element = XML_Element.getElementByTag(doc, tag);
		if(element.hasTag("onDemand")){
			if(element.getElementByTag("onDemand").readBoolean()) //if onDemand
				return element.readLevel_onDemand_Property(tag);
			else
				return element.readLevel_FullLoaded_Property(tag);
		}
		//default is true (more efficient)
		return element.readLevel_onDemand_Property(tag);
	}

	/* On demand loading of a Level*/
	@Override public Section_Properties parseLevelSection(Level_onDemand_Properties lvl_p, int n_th) throws MyParseException {
		if(lvl_p.sec_p.length>n_th){
			if(lvl_p.common_p!=null)
				return lvl_p.sec_p[n_th].readSectionProperty(lvl_p.name, lvl_p.common_p.clone(), true);
			else
				return lvl_p.sec_p[n_th].readSectionProperty(lvl_p.name, true);
		}
		return null;
	}

	/* On demand loading of a Level*/
	@Override public Credits_Properties parseCreditsProperties(InputStream iStream, String tag) throws MyParseException {
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(iStream);
		}catch (Exception e) {
			if(_Log.LOG_ACTIVE){
				_Log.e(e);}
			return null;
		}

		XML_Element element = XML_Element.getElementByTag(doc, tag);
		return element.readCreditsProperty();
	}
	
	@Override public void parseStdAI_Engines(InputStream iStream) {
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(iStream);
		}catch (Exception e) {
			if(_Log.LOG_ACTIVE){
				_Log.e(e);}
			return;
		}

		try {
			List<XML_Element> elements = XML_Element.getFirstChild(doc).getChildNodes();
			for(XML_Element element : elements){
				try {
					if(element.getTagName().equals("DecisionTree")) {
						if(_Log.LOG_ACTIVE){
							_Log.i("Parser", "Reading a Decision Tree");}
						DecisionTree_Properties dt_p = element.readDecisionTreeProperties();
						dt_p.addDataDrivenStandard();
						dt_p.free();
					}
					else if(_Log.LOG_ACTIVE){
						_Log.w("Parser", "Unrecognized tag <"+element.getTagName()+"> in parseStdAI_Engines. Not parsed!");}
				} catch (MyParseException e) {
					e.printStackTrace();
				}
			}
		} catch (MyParseException e) {
			if(_Log.LOG_ACTIVE){
				_Log.e("XML Parser", e);}
		}
	}
}