package com.example.dave.gameEngine.dataDriven.parser;

import com.example.dave.gameEngine.dataDriven.Level_Properties;
import com.example.dave.gameEngine.dataDriven.Level_onDemand_Properties;
import com.example.dave.gameEngine.dataDriven.Section_Properties;

import java.io.InputStream;

public interface Parser {

	/* Pre Loading Game Objects*/
	void parseStdGOProperties(InputStream iStream);

	/* On demand loading of a Level*/
	void parseStdAI_Engines(InputStream iStream);

	Level_Properties parseLevelProperties(InputStream iStream, String tag) throws MyParseException;

	/* On demand loading of a Level*/
	Section_Properties parseLevelSection(Level_onDemand_Properties lvl_p, int n_th) throws MyParseException;

	Credits_Properties parseCreditsProperties(InputStream iStream, String tag) throws MyParseException;
}
