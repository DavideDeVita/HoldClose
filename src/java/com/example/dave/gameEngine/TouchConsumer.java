package com.example.dave.gameEngine;

import com.badlogic.androidgames.framework.Input;
import com.example.dave.gameEngine.entity_component.ComponentType;
import com.example.dave.gameEngine.entity_component.Control_Cmpnt;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.MouseJointDef;
import com.google.fpl.liquidfun.QueryCallback;

import static com.example.dave.gameEngine.MainActivity.screenSignalPixel;

/**
 * Takes care of user interaction: pulls objects using a Mouse Joint.
 */
public class TouchConsumer {

    // keep track of what we are dragging
    private MouseJoint mouseJoint;
    private int activePointerID;
    private Fixture touchedFixture;
    private GameObject touchedGO, queryCallbackGO;
    private Control_Cmpnt ctrlable;

    private final GameSection gs;
    private final Box touchPixelZone;
    private QueryCallback touchQueryCallback = new TouchQueryCallback();

    // physical units, semi-side of a square around the touch point
    private final static float POINTER_SIZE = 0.66f;
	//private int qi=0;

	private class TouchQueryCallback extends QueryCallback{
        @Override
        public boolean reportFixture(Fixture fixture) {
	        queryCallbackGO = (GameObject)fixture.getBody().getUserData();
	        if(_Log.LOG_ACTIVE){
		        _Log.i("TouchQuery", "reported fixture of "+queryCallbackGO);}
	        //qi++;
            if(queryCallbackGO.hasComponent(ComponentType.Control)){
                //only first.. hopefully, the closest
	            touchedGO=queryCallbackGO;
                if(touchedFixture==null) {
                    ctrlable = (Control_Cmpnt) touchedGO.getComponent(ComponentType.Control);
                    if(ctrlable.tryStartControl()) {
                        touchedFixture = fixture;
                        return true;
                    }
                    else{
                        ctrlable=null;
                        touchedGO=null;
                    }
                }
            }
            /*anyway else*/ return true; //Otherwise stops the query
        }
    }

    /**
        scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    */
    TouchConsumer(GameSection gs) {
        this.gs = gs;
        this.touchPixelZone = new Box(screenSignalPixel, screenSignalPixel,
		        MainActivity.screenWidth- screenSignalPixel, MainActivity.screenWidth- screenSignalPixel);
    }

    private boolean checkEventCoordinatesAndFix(Input.TouchEvent event){
	    if(_Log.LOG_ACTIVE){
		    _Log.i("TouchConsumer", "Touched pixel "+event.x+" "+event.y);}
	    if(!touchPixelZone.contains(event.x, event.y)){
		    if(_Log.LOG_ACTIVE){
			    _Log.w("TouchConsumer", "touching a pixel outside of the level touch zone: "+event.x+" "+event.y);}
		    //return false; REDACTED IN ORDER TO MAKE TOUCH MORE FLUID
		    {   if (event.x < touchPixelZone.xmin)
				    event.x = (int) touchPixelZone.xmin;
			    else if (event.x > touchPixelZone.xmax)
				    event.x = (int) touchPixelZone.xmax;

			    if (event.y < touchPixelZone.ymin)
				    event.y = (int) touchPixelZone.ymin;
			    else if (event.y > touchPixelZone.ymax)
				    event.y = (int) touchPixelZone.ymax;
		    }
		    return true;
	    }
	    event.x-=screenSignalPixel;
	    event.y-=screenSignalPixel;
	    return true;
    }

    /**Checks whether the ctrlable is held still*/
	public void checkHold() {
		if(ctrlable!=null && !ctrlable.tryKeepControl())
			interruptTouch();
	}

    void consumeTouchEvent(Input.TouchEvent event){
        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                consumeTouchDown(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                consumeTouchUp(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                consumeTouchMove(event);
                break;
        }
    }

    private void consumeTouchDown(Input.TouchEvent event) {
    	if(!checkEventCoordinatesAndFix(event))
    		return;

        int pointerId = event.pointer;
        // if we are already dragging with another finger, discard this event
        if (mouseJoint != null) return;

        float x = gs.toMetersX(event.x), y = gs.toMetersY(event.y);

        touchedFixture = null;
        touchedGO=null;
	    if(gs.touchZone.contains(x,y)){
            gs.world.queryAABB(touchQueryCallback, x - POINTER_SIZE, y - POINTER_SIZE, x + POINTER_SIZE, y + POINTER_SIZE);
            if (touchedFixture != null) {
	            //_Log.i("Control", "Touch down obj "+touchedGO);
                // From fixture to GO
                Body touchedBody = touchedFixture.getBody();
                activePointerID = pointerId;
                setupMouseJoint(x, y, touchedBody);
            }
        }
    }

	private void consumeTouchUp(Input.TouchEvent event) {
		if (mouseJoint != null && event.pointer == activePointerID) {
			gs.world.destroyJoint(mouseJoint);
			mouseJoint = null;
			activePointerID = 0;
			if(ctrlable!=null){
				ctrlable.endControl();
				ctrlable=null;
				touchedGO=null;
			}
		}
	}

	private void interruptTouch() {
		if (mouseJoint != null ) {
			gs.world.destroyJoint(mouseJoint);
			mouseJoint = null;
			activePointerID = 0;
			if(ctrlable!=null){
				ctrlable.endControl();
				ctrlable=null;
				touchedGO=null;
			}
		}
	}

    private void consumeTouchMove(Input.TouchEvent event) {
    	if(!checkEventCoordinatesAndFix(event))
	        return;

        if (mouseJoint!=null && event.pointer == activePointerID) {
	        float x = gs.toMetersX(event.x), y = gs.toMetersY(event.y);
            if(gs.touchZone.contains(x,y)) {
                if(ctrlable.tryKeepControl()) {
	                mouseJoint.setTarget(x, y);
                }
                else
                    interruptTouch();
            }
            else {
	            interruptTouch();
            }
        }
    }

	// Set up a mouse joint between the touched GameObject and the touch coordinates (x,y)
	private void setupMouseJoint(float x, float y, Body touchedBody) {
		MouseJointDef mouseJointDef = new MouseJointDef();
		mouseJointDef.setBodyA(touchedBody); // irrelevant but necessary
		mouseJointDef.setBodyB(touchedBody);
		mouseJointDef.setMaxForce(500 * touchedBody.getMass());
		mouseJointDef.setTarget(x, y);
		mouseJoint = gs.world.createMouseJoint(mouseJointDef);
	}

    public boolean safeRemove(GameObject go){
	    if(touchedGO == go) {
	        interruptTouch();
		    if(_Log.LOG_ACTIVE){
			    _Log.w("TouchConsumer", "Removing Touched and Controlled GO "+go);}
		    return true;
	    }
	    return false;
    }
}
