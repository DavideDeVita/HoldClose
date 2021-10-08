package com.example.dave.gameEngine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.params.BlackLevelPattern;

import static com.example.dave.gameEngine.MainActivity.screenSignalPixel;


public class GraphicSubSys {
    static Bitmap buffer;
    private static Canvas canvas;
    private final static Paint paint = new Paint();
    private final static Paint.Style stdPaintStyle = Paint.Style.FILL_AND_STROKE;

    private static final RectF lvlDest=new RectF(), fullScreenDest=new RectF();
    public static int screenWidth, screenHeight;

    static void setGraphicSubSys(){
        buffer = Bitmap.createBitmap(MainActivity.screenWidth, MainActivity.screenHeight, Bitmap.Config.ARGB_8888);
        screenWidth=MainActivity.screenWidth;
        screenHeight=MainActivity.screenHeight;
        canvas = new Canvas(buffer);
        //Ricorda di aggiornare Android Fast Render View se dovessi aggiornare buffer

        lvlDest.left = screenSignalPixel;   lvlDest.top = screenSignalPixel;
        lvlDest.bottom = MainActivity.screenHeight-screenSignalPixel;
        lvlDest.right = MainActivity.screenWidth-screenSignalPixel;

        fullScreenDest.left = fullScreenDest.top = 0;
        fullScreenDest.bottom = MainActivity.screenHeight;
        fullScreenDest.right = MainActivity.screenWidth;
    }

    public static void clear(int a, int r, int g, int b){
        canvas.drawARGB(a, r, g, b);
    }
    public static void clear(){
        canvas.drawARGB(Color.alphaDefault, 0, 0, 0);
    }
    public static void clear(int r, int g, int b){
        canvas.drawARGB(Color.alphaDefault, r, g, b);
    }
    public static void clear(Color c){
        canvas.drawARGB(c.a, c.r, c.g, c.b);
    }
    public static void clear(Drawable d){
        //canvas.drawARGB(Color.alphaDefault, 0, 0, 0);
        d.draw(canvas);
    }

    /*Rectangle*/
    public static void drawRect(float cx, float cy, RectF rect, float angle, Color c){
        canvas.save();
        canvas.rotate(MyMath.toDegrees(angle), cx, cy);
        paint.setARGB(c.a, c.r, c.g, c.b);
        canvas.drawRect(rect, paint);
        canvas.restore();
    }

    public static void drawRect(RectF rect, float angle, Color color){
        drawRect(rect.centerX(), rect.centerY(), rect, angle, color);
    }

    public static void drawRect(float cx, float cy, RectF rect, float angle, Bitmap bitmap, Rect src){
        canvas.save();
        canvas.rotate(MyMath.toDegrees(angle), cx, cy);
        //canvas.drawRect(rect, paint);
        canvas.drawBitmap(bitmap, src, rect, null);
        canvas.restore();
    }

    public static void drawRect(RectF dest, float angle, Color color, Paint.Style paintStyle){
        paint.setStyle(paintStyle);
        drawRect(dest, angle, color);
        paint.setStyle(stdPaintStyle);
    }

    public static void drawRect(float cx, float cy, RectF dest, float angle, Color color, Paint.Style paintStyle){
        paint.setStyle(paintStyle);
        drawRect(cx, cy, dest, angle, color);
        paint.setStyle(stdPaintStyle);
    }

    /*Circle*/
    public static void drawCircle(float cx, float cy, float radius, Color color){
        canvas.save();
        paint.setARGB(color.a, color.r, color.g, color.b);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.restore();
    }

    public static void drawCircle(float cx, float cy, RectF dest, float angle, Bitmap image, Rect src){
        canvas.save();
        canvas.rotate(MyMath.toDegrees(angle), cx, cy);
        canvas.drawBitmap(image, src, dest, null);
        canvas.restore();
    }

    public static void drawCircle(float cx, float cy, float radius, Color color, Paint.Style paintStyle){
        paint.setStyle(paintStyle);
        drawCircle(cx, cy, radius, color);
        paint.setStyle(stdPaintStyle);
    }

    public static void drawLevelBackGround(Bitmap bg, Rect src){
        canvas.save();
        canvas.drawBitmap(bg, src, lvlDest, null);
        canvas.restore();
    }

    public static void drawScreenBackground(Color color){
        clear(color);
    }

    public static void drawScreenBackground(Bitmap bg, Rect src){
        canvas.save();
        canvas.drawBitmap(bg, src, fullScreenDest, null);
        canvas.restore();
    }
}
