package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.Resizeable_Properties;

public class Resizeable_Cmpnt extends Component {
	public final float minDimX, minDimY;
	public final float maxDimX, maxDimY;

	protected Resizeable_Cmpnt(Entity owner, Resizeable_Properties resizeP) {
		super(owner);
		this.minDimX=resizeP.minDimX;
		this.minDimY=resizeP.minDimY;
		this.maxDimX=resizeP.maxDimX;
		this.maxDimY=resizeP.maxDimY;
	}

	@Override
	public final ComponentType type() { return ComponentType.Resizeable; }

	public void resize(float sizeRatio) {
		if(owner.hasComponent(ComponentType.Physics)){
			Physical_Cmpnt phys = owner.getPhysical();
			phys.updateSize(computeXsize(sizeRatio), computeYsize(sizeRatio) );
		}
		Component draw_c = owner.getDrawable();
		if(draw_c instanceof DrawablePhysicalObj_Cmpnt){
			DrawablePhysicalObj_Cmpnt draw = (DrawablePhysicalObj_Cmpnt)draw_c;
			if(_Log.LOG_ACTIVE){
				_Log.i("Resize", owner+" resized to "+computeXsize(sizeRatio)+" "+computeYsize(sizeRatio));}
			draw.updateScreenSize(computeXsize(sizeRatio), computeYsize(sizeRatio));
		}
	}

	private float computeXsize(float ratio){
		return minDimX+ratio*(maxDimX-minDimX);
	}

	private float computeYsize(float ratio){
		return minDimY+ratio*(maxDimY-minDimY);
	}

	public static float getProportionalSize(float min, float max, float ratio){
		return min+ratio*(max-min);
	}

}
