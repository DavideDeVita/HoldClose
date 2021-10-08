package com.example.dave.gameEngine.entity_component;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.dave.gameEngine.Color;
import com.example.dave.gameEngine.GraphicSubSys;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties.*;

public abstract class DrawableCircle_Cmpnt extends DrawablePhysicalObj_Cmpnt {
	protected float screen_radius;

	protected DrawableCircle_Cmpnt(Entity owner, Drawable_Properties draw_p, final float screen_radius) {
		super(owner, draw_p);
		this.screen_radius=screen_radius;
	}

	@Override
	public void updateScreenSize(float physDimX, float physDimY) {
		this.screen_radius=owner.gs.toPixelsXLength(physDimX )/2;
	}
}

class DrawableCircleMonochrome_Cmpnt extends DrawableCircle_Cmpnt {
	final Color color;

	protected DrawableCircleMonochrome_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_radius) {
		super(owner, draw_p, screen_radius);
		MonochromeMotive_Properties monochrome = (MonochromeMotive_Properties)draw_p.motive_p;
		if (monochrome.color==null) color=Color.randomStandard();
		else color=Color.random(monochrome.color, monochrome.variance);
	}

	@Override
	public void draw(float x, float y, float angle) {
		GraphicSubSys.drawCircle( x, y, screen_radius, color);
	}
}

abstract class DrawableCircleSprite_Cmpnt extends DrawableCircle_Cmpnt{
	protected final Rect src = new Rect();
	protected final RectF dest = new RectF();
	private final boolean lockRotation;

	protected DrawableCircleSprite_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_radius) {
		super(owner, draw_p, screen_radius);
		this.lockRotation = draw_p.lockSpriteAngle;
	}

	protected abstract void setSrc();

	protected final void setRect(float x, float y) {
		dest.left = (x-screen_radius) - (get_XMin_Bias()*screen_radius);
		dest.top = (y-screen_radius) - (getYMax_Bias()*screen_radius);
		dest.right = (x+screen_radius) + (get_XMax_Bias()*screen_radius);
		dest.bottom = (y+screen_radius) + (get_YMin_Bias()*screen_radius);
	}

	protected abstract float get_XMin_Bias();
	protected abstract float get_XMax_Bias();
	protected abstract float get_YMin_Bias();
	protected abstract float getYMax_Bias();

	@Override
	public final void draw(float x, float y, float angle) {
		setSrc();
		setRect(x, y);
		GraphicSubSys.drawCircle( x, y, dest, lockRotation ? 0 : angle, getBitmap(), src);
	}

	protected abstract Bitmap getBitmap();
}

class DrawableCircleStaticSprite_Cmpnt extends DrawableCircleSprite_Cmpnt {
	private final Bitmap bitmap;
	private final float bias_xMin, bias_xMax, bias_yMin, bias_yMax;

	protected DrawableCircleStaticSprite_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_radius) {
		super(owner,draw_p , screen_radius);
		BitmapMotive_Properties bitmap_p = (BitmapMotive_Properties)draw_p.motive_p;
		this.bitmap = bitmap_p.drawables.extract().getBitmap();
		src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		bias_xMin = bitmap_p.bias_xMin;
		bias_xMax = bitmap_p.bias_xMax;
		bias_yMin = bitmap_p.bias_yMin;
		bias_yMax = bitmap_p.bias_yMax;
	}

	@Override
	protected void setSrc() {
		//No Action, already set in constructor
	}

	@Override
	protected float get_XMin_Bias() {
		return bias_xMin;
	}
	@Override
	protected float get_XMax_Bias() {
		return bias_xMax;
	}
	@Override
	protected float get_YMin_Bias() {
		return bias_yMin;
	}
	@Override
	protected float getYMax_Bias() {
		return bias_yMax;
	}

	@Override
	protected Bitmap getBitmap() {
		return bitmap;
	}
}

class DrawableCircleAnimated_Cmpnt extends DrawableCircleSprite_Cmpnt {
	protected DrawableCircleAnimated_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_radius) {
		super(owner, draw_p, screen_radius);
	}

	@Override
	protected Bitmap getBitmap() {
		return owner.getAnimation().currSheet().image;
	}

	@Override
	protected void setSrc() {
		owner.getAnimation().setSheetRect(src);
	}

	@Override
	protected float get_XMin_Bias() {
		return owner.getAnimation().get_xMin_Bias();
	}
	@Override
	protected float get_XMax_Bias() {
		return owner.getAnimation().get_xMax_Bias();
	}
	@Override
	protected float get_YMin_Bias() {
		return owner.getAnimation().get_yMin_Bias();
	}
	@Override
	protected float getYMax_Bias() {
		return owner.getAnimation().get_yMax_Bias();
	}
}