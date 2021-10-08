package com.example.dave.gameEngine;

import java.security.InvalidParameterException;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by mfaella on 05/03/16.
 * edited by Davide F. De Dita on 18/03/2020
 */
public class Box {
    /**unmodifiable direct access*/
    public float xmin, ymin, xmax, ymax;
    public float width, height;
    /*private float _xmin, _ymin, _xmax, _ymax;
    private float _width, _height;*/

    protected Box(){}

    public Box(float xmin, float ymin, float xmax, float ymax){
        if(xmin>=xmax || ymin>=ymax) throw new InvalidParameterException("Either xmin>xmax or ymin>ymax.. you can't\n\txmin:"+xmin+"\txmax:"+xmax+"\tymin:"+ymin+"\tymax:"+ymax);
        /*this._xmin = */this.xmin = xmin;
        /*this._xmax = */this.xmax = xmax;
        /*this._ymin = */this.ymin = ymin;
        /*this._ymax = */this.ymax = ymax;
        /*this._width = */this.width = xmax-xmin;
        /*this._height = */this.height = ymax-ymin;
    }

    public Box( Box box){
        this(box./*_*/xmin, box./*_*/ymin, box./*_*/xmax, box./*_*/ymax);
    }

    public void reset(float xmin, float ymin, float xmax, float ymax){
        /*this._xmin = */this.xmin = xmin;
        /*this._xmax = */this.xmax = xmax;
        /*this._ymin = */this.ymin = ymin;
        /*this._ymax = */this.ymax = ymax;
        /*this._width = */this.width = xmax-xmin;
        /*this._height = */this.height = ymax-ymin;
    }

    @gameLoop
    public final boolean contains(float x, float y){
        return /*_*/xmin<=x && x<=/*_*/xmax && /*_*/ymin<=y && y<=/*_*/ymax;
    }

    public final boolean contains(float x, float y, float eps){
        return /*_*/xmin-eps<=x && x<=/*_*/xmax+eps && /*_*/ymin-eps<=y && y<=/*_*/ymax+eps;
    }

    public final boolean contains(Box box){
        return /*_*/xmin<=box./*_*/xmin && box./*_*/xmin<=/*_*/xmax && /*_*/ymin<=box./*_*/ymin && box./*_*/ymax<=/*_*/ymax;
    }

    public float distanceFrom(float x, float y){
        if(contains(x, y)) return 0f;
        else if(x>xmax){
            if(y>ymax) return MyMath.dist(x, y, xmax, ymax);
            else if(y<ymin) return MyMath.dist(x, y, xmax, ymin);
            else return x-xmax;
        }
        else if(x<xmin){
            if(y>ymax) return MyMath.dist(x, y, xmin, ymax);
            else if(y<ymin) return MyMath.dist(x, y, xmin, ymin);
            else return xmin-x;
        }
        else{
            if(y>ymax) return y-ymax;
            else /*if(y<ymin)*/ return ymin-y;
        }
    }

    /*Save changes made on direct access
    public void commit(){
        this._xmin = this.xmin;
        this._xmax = this.xmax;
        this._ymin = this.ymin;
        this._ymax = this.ymax;
        this._width = this.width = xmax-xmin;
        this._height = this.height = ymax-ymin;
    }*/

    /*Fixes direct access variables according to _ones
    public void fix(){
        this.xmin = this._xmin;
        this.xmax = this._xmax;
        this.ymin = this._ymin;
        this.ymax = this._ymax;
        this.width = this._width = _xmax-_xmin;
        this.height = this._height = _ymax-_ymin;
    }*/

    public ViewWindow getViewWindow(ViewWindow.ViewBox_Details viewDetails){
        return new ViewWindow(this,viewDetails);
    }

    @Override
    public String toString(){
        return "["+/*_*/xmin+":"+/*_*/ymin+"] ["+/*_*/xmax+":"+/*_*/ymax+"]     "+"width: "+/*_*/width+"     height: "+/*_*/height;
    }

    @Override
    public Box clone(){
        return new Box(this);
    }

    public float centerX() {
        /*fix();*/
        return (xmin+xmax)/2f;
    }

    public float centerY() {
        /*fix();*/
        return (ymin+ymax)/2f;
    }

    public float randomX() {
        return MyMath.randomFloat(/*_*/xmin, /*_*/xmax);
    }

    public float randomY() {
        return MyMath.randomFloat(/*_*/ymin, /*_*/ymax);
    }
}