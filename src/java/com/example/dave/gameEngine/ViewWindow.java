package com.example.dave.gameEngine;

import static java.lang.Math.max;
import static java.lang.Math.min;

//Scroll computed over X
public class ViewWindow extends Box{
    protected final Box completeBox;
    protected final ViewBox_Details vb;
    private float forwardThreshold_x,forwardThreshold_y, backwardThreshold_x, backwardThreshold_y;

    public ViewWindow(Box phyBox, ViewBox_Details viewBoxDetails) {
        this.completeBox = phyBox;
        this.vb=viewBoxDetails;

        if(vb.forwardRatio_x<=vb.backwardRatio_x || vb.forwardRatio_x>=1f || (vb.allowBack_x && vb.forwardRatio_x<=0f)){
            if(_Log.LOG_ACTIVE){
                _Log.e("errore forwardBackward in getViewWindow");}
            throw new RuntimeException("forward (F) e backward (B) devono essere: 0<B<F<1");
        }
        if(vb.forwardRatio_y<=vb.backwardRatio_y || vb.forwardRatio_y>=1f || (vb.allowBack_y && vb.backwardRatio_y<=0f)){
            if(_Log.LOG_ACTIVE){
                _Log.e("errore forwardBackward in getViewWindow");}
            throw new RuntimeException("forward (F) n backward (B) must be: 0<B<F<1");
        }
        if(completeBox.width<vb.fixedWidth) {
            if(_Log.LOG_ACTIVE){
                _Log.w("ViewBox", "View Width is greater than physical width.. adjusted");}
            vb.fixedWidth = completeBox.width;
        }
        if(completeBox.height<vb.fixedHeight) {
            if(_Log.LOG_ACTIVE){
                _Log.w("ViewBox", "View height is greater than physical height.. adjusted");}
            vb.fixedHeight = completeBox.height;
        }

        //Init
        reset(completeBox.xmin, completeBox.ymin, completeBox.xmin+vb.fixedWidth, completeBox.ymin+vb.fixedHeight);

        this.forwardThreshold_x = xmin+(width*vb.forwardRatio_x);
        if(vb.allowBack_x) this.backwardThreshold_x = xmin+(width*vb.backwardRatio_x);

        this.forwardThreshold_y = ymin+(height*vb.forwardRatio_y);
        if(vb.allowBack_y) this.backwardThreshold_y = ymin+(height*vb.backwardRatio_y);
    }

    public void resetView(float x, float y) { //according to x,y of an object
        if(x > forwardThreshold_x){
            this.xmax = min(xmax+x-forwardThreshold_x, completeBox.xmax);
            this.xmin = adjust_xmin();

            this.forwardThreshold_x = xmin+(width*vb.forwardRatio_x);
            if(vb.allowBack_x) this.backwardThreshold_x = xmin+(width*vb.backwardRatio_x);
        }
        else if(vb.allowBack_x && x<backwardThreshold_x){
            this.xmin = max(xmin+x-backwardThreshold_x, completeBox.xmin);
            this.xmax = adjust_xmax();

            this.forwardThreshold_x = xmin+(width*vb.forwardRatio_x);
            this.backwardThreshold_x = xmin+(width*vb.backwardRatio_x);
        }

        if(y > forwardThreshold_y){
            this.ymax = min(ymax+y-forwardThreshold_y, completeBox.ymax);
            this.ymin = adjust_ymin();

            this.forwardThreshold_y = ymin+(height*vb.forwardRatio_y);
            if(vb.allowBack_y) this.backwardThreshold_y = ymin+(height*vb.backwardRatio_y);
        }
        else if(vb.allowBack_y && y<backwardThreshold_y){
            this.ymin = max(ymin+y-backwardThreshold_y, completeBox.ymin);
            this.ymax = adjust_ymax();

            this.forwardThreshold_y = ymin+(height*vb.forwardRatio_y);
            this.backwardThreshold_y = ymin+(height*vb.backwardRatio_y);
        }
    }

    private float adjust_xmin(){
        return max(xmax-width, completeBox.xmin);
    }

    private float adjust_xmax(){
        return min(xmin+width, completeBox.xmax);
    }

    private float adjust_ymin(){
        return max(ymax-height, completeBox.ymin);
    }

    private float adjust_ymax(){
        return min(ymin+height, completeBox.ymax);
    }

    public static class ViewBox_Details{
        boolean allowBack_x=false, allowBack_y=false;
        float forwardRatio_x, forwardRatio_y;
        float backwardRatio_x, backwardRatio_y;
        float fixedWidth, fixedHeight;
    }
}