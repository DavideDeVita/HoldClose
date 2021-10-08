package com.example.dave.gameEngine.entity_component;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.dave.gameEngine.Color;
import com.example.dave.gameEngine.GraphicSubSys;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties.*;

public abstract class DrawableBox_Cmpnt extends DrawablePhysicalObj_Cmpnt {
    protected float screen_semi_width, screen_semi_height;
    protected RectF rect;

    protected DrawableBox_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_semi_width, float screen_semi_height) {
        super(owner, draw_p);
        rect = new RectF();
        this.screen_semi_width = screen_semi_width;
        this.screen_semi_height = screen_semi_height;
    }

    protected void setRect(float x, float y){
        rect.left = x-screen_semi_width;
        rect.top = y-screen_semi_height;
        rect.right = x+screen_semi_width;
        rect.bottom = y+screen_semi_height;
    }

    @Override
    public void updateScreenSize(float physDimX, float physDimY) {
        this.screen_semi_width=owner.gs.toPixelsXLength(physDimX/2f );
        this.screen_semi_height=owner.gs.toPixelsYLength(physDimY/2f );
    }
}

class DrawableBoxMonochrome_Cmpnt extends DrawableBox_Cmpnt {
    final Color color;

    protected DrawableBoxMonochrome_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_semi_width, float screen_semi_height) {
        super(owner, draw_p, screen_semi_width, screen_semi_height);
        MonochromeMotive_Properties monochrome = (MonochromeMotive_Properties)draw_p.motive_p;
        if (monochrome.color==null) color=Color.randomStandard();
        else color=Color.random(monochrome.color, monochrome.variance);
    }

    @Override
    public void draw(float x, float y, float angle) {
        setRect(x, y);
        GraphicSubSys.drawRect( x, y, rect, angle, color);
    }
}

abstract class DrawableBoxSprite_Cmpnt extends DrawableBox_Cmpnt{
    protected final Rect src = new Rect();
    private final boolean lockRotation;

    protected DrawableBoxSprite_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_semi_width, float screen_semi_height) {
        super(owner, draw_p, screen_semi_width, screen_semi_height);
        this.lockRotation = draw_p.lockSpriteAngle;
    }

    @Override
    protected final void setRect(float x, float y) {
        super.setRect(x, y);
        rect.left -= (get_XMin_Bias()*screen_semi_width*2);
        rect.top -= (getYMax_Bias()*screen_semi_width*2);
        rect.right += (get_XMax_Bias()*screen_semi_width*2);
        rect.bottom += (get_YMin_Bias()*screen_semi_width*2);
    }

    protected abstract float get_XMin_Bias();
    protected abstract float get_XMax_Bias();
    protected abstract float get_YMin_Bias();
    protected abstract float getYMax_Bias();

    @Override
    public final void draw(float x, float y, float angle) {
        setSrc();
        setRect(x, y);
        GraphicSubSys.drawRect( x, y, rect, lockRotation? 0 : angle, getBitmap(), src);
    }

    protected abstract Bitmap getBitmap();

    protected abstract void setSrc();
}

class DrawableBoxStaticSprite_Cmpnt extends DrawableBoxSprite_Cmpnt {
    private final Bitmap bitmap;
    private final float bias_xMin, bias_xMax, bias_yMin, bias_yMax;

    protected DrawableBoxStaticSprite_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_semi_width, float screen_semi_height) {
        super(owner, draw_p, screen_semi_width, screen_semi_height);
        BitmapMotive_Properties bitmap_p = (BitmapMotive_Properties)draw_p.motive_p;
        bitmap=bitmap_p.drawables.extract().getBitmap();
        src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        bias_xMin = bitmap_p.bias_xMin;
        bias_xMax = bitmap_p.bias_xMax;
        bias_yMin = bitmap_p.bias_yMin;
        bias_yMax = bitmap_p.bias_yMax;
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

    @Override
    protected void setSrc() {
        //No Action.. already initialized in constructor
    }
}

class DrawableBoxAnimated_Cmpnt extends DrawableBoxSprite_Cmpnt {
    protected DrawableBoxAnimated_Cmpnt(Entity owner, Drawable_Properties draw_p, float screen_semi_width, float screen_semi_height) {
        super(owner, draw_p, screen_semi_width, screen_semi_height);
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

    @Override
    protected Bitmap getBitmap() {
        return owner.getAnimation().currSheet().image;
    }

    @Override
    protected void setSrc() {
        owner.getAnimation().setSheetRect(src);
    }
}