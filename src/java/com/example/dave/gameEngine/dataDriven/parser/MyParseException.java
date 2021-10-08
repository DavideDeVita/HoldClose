package com.example.dave.gameEngine.dataDriven.parser;

//Parsing micro functions
public class MyParseException extends Exception{
	public MyParseException(String s) {
		super(s);
	}
	public MyParseException(String readingType, char found, String reading) {
		super("[Reading "+readingType+"] Found '"+found+"' while reading "+reading);
	}
	public MyParseException(String readingType, String found) {
		super("[Reading "+readingType+"] Found unexpected "+found);
	}
}
