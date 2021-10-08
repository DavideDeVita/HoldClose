package com.example.dave.gameEngine;

import java.security.InvalidParameterException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SubBox extends Box{
	private final Box outer;

	public SubBox(float xmin, float ymin, float xmax, float ymax, Box containedInto){
		if(xmin>=xmax || ymin>=ymax) throw new InvalidParameterException("Either xmin>xmax or ymin>ymax.. you can't");
		/*containedInto.fix();*/
		this.xmin = max(xmin, containedInto.xmin);
		this.xmax = min(xmax, containedInto.xmax);
		this.ymin = max(ymin, containedInto.ymin);
		this.ymax = min(ymax, containedInto.ymax);
		this.width = xmax-xmin;
		this.height = ymax-ymin;
		this.outer=containedInto;
		/*commit();*/
	}

	public SubBox(Box containedInto){
		/*containedInto.fix();*/
		this.xmin = containedInto.xmin;
		this.xmax = containedInto.xmax;
		this.ymin = containedInto.ymin;
		this.ymax =containedInto.ymax;
		this.width = xmax-xmin;
		this.height = ymax-ymin;
		this.outer=containedInto;
		/*commit();*/
	}

	public SubBox(Box box, Box containedInto){
		/*containedInto.fix();
		box.fix();*/
		this.xmin = max(box.xmin, containedInto.xmin);
		this.xmax = min(box.xmax, containedInto.xmax);
		this.ymin = max(box.ymin, containedInto.ymin);
		this.ymax = min(box.ymax, containedInto.ymax);
		this.width = xmax-xmin;
		this.height = ymax-ymin;
		this.outer=containedInto;
		/*commit();*/
	}

	@Override
	public void reset(float xmin, float ymin, float xmax, float ymax){
		/*outer.fix();*/
		this.xmin = max(xmin, outer.xmin);
		this.xmax = min(xmax, outer.xmax);
		this.ymin = max(ymin, outer.ymin);
		this.ymax = min(ymax, outer.ymax);
		this.width = xmax-xmin;
		this.height = ymax-ymin;
		/*commit();*/
	}
}