package com.example.dave.gameEngine;

import android.util.ArrayMap;

import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Color {
    public int a, r, g, b;

    static final int alphaDefault = 255, STD_VARIATION=36;
    private static final Map<String, Color> names = new ArrayMap<>();
    public static boolean avoidBlack=true, avoidWhite=true;

    public static final Color RED, GREEN, BLUE, ORANGE, YELLOW, CYAN, PURPLE, BLACK, WHITE, GREY, BROWN, AQUA, SILVER, GOLD;
    //Special
    public static final Color GRASS, AUTUMN;

    static{
        RED = new Color(255, 0, 0);
            names.put("RED", RED);
        GREEN = new Color(0, 255, 0);
            names.put("GREEN", GREEN);
        BLUE = new Color(0, 0, 255);
            names.put("BLUE", BLUE);
        ORANGE = new Color(255, 127, 0);
            names.put("ORANGE", ORANGE);
        YELLOW = new Color(255, 255, 0);
            names.put("YELLOW", YELLOW);
        CYAN = new Color(0, 255, 255);
            names.put("CYAN", CYAN);
        PURPLE = new Color(127, 0, 127);
            names.put("PURPLE", PURPLE);
        BLACK = new Color(0, 0, 0);
            names.put("BLACK", BLACK);
        WHITE = new Color(255, 255, 255);
            names.put("WHITE", WHITE);
        GREY = new Color(127, 127, 127);
            names.put("GREY", GREY);
        BROWN = new Color(139,69,19);
            names.put("BROWN", BROWN);
        AQUA = new Color(0, 255, 127);
            names.put("AQUA", AQUA);
        GOLD = new Color(212,175,55);
            names.put("GOLD", GOLD);
        SILVER = new Color(196, 202, 206);
            names.put("SILVER", SILVER);

        GRASS = new Color(0, 117, 94);
            names.put("GRASS", GRASS);
        AUTUMN = new Color(124, 40, 34);//r:12 original
            names.put("AUTUMN", AUTUMN);
    }

    public Color(int a, int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b) {
        this.a=alphaDefault;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color byName(String name){
        return names.get(name.toUpperCase());
    }

    public static Color randomStandard(){
        int number = names.size();
        if(avoidBlack)  number--;
        if(avoidWhite)  number--;
        int rand=MyMath.randomInt(0, number);
        switch (rand) {
            case 0:
                return RED;
            case 1:
                return GREEN;
            case 2:
                return BLUE;
            case 3:
                return ORANGE;
            case 4:
                return YELLOW;
            case 5:
                return CYAN;
            case 6:
                return PURPLE;
            case 7:
                return GREY;
            case 8:
                return BROWN;
            case 9:
                return AQUA;
            case 10:
                return GOLD;
            case 11: return SILVER;
            case 12:
                return AUTUMN;
            case 13: return GRASS;
            case 14: return WHITE;
            case 15: return BLACK;
            default: return null;
        }
    }

    public static Color random(int minR, int maxR, int minG, int maxG, int minB, int maxB){
        return new Color(
                MyMath.randomInt(minR, maxR),
                MyMath.randomInt(minG, maxG),
                MyMath.randomInt(minB, maxB)
                );        
    }

    public static Color random(Color aroundColor){
        return random(aroundColor, STD_VARIATION);
    }

    public static Color random(Color aroundColor, Integer var){
        if(var!=null){
            if(var==0) return aroundColor;
            else if(var<0) var*=-1;
            return random(max(0, aroundColor.r-var), min(255, aroundColor.r+var),
                max(0, aroundColor.g-var), min(255, aroundColor.g+var),
                max(0, aroundColor.b-var), min(255, aroundColor.b+var));
        }
        else return random (aroundColor);
    }

    public static Color random(){ return random(0, 255, 0, 255, 0, 255); }

    @Override
    public String toString(){
        return "["+r+", "+g+", "+b+"]";
    }
}
