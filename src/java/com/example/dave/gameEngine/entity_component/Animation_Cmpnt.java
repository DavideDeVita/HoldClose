package com.example.dave.gameEngine.entity_component;

import android.graphics.Rect;

import com.example.dave.gameEngine.dataDriven.component.Animation_Properties;
import com.example.dave.gameEngine.myMultimedia.Spritesheet;

import static com.example.dave.gameEngine.MyMath.between;

public abstract class Animation_Cmpnt extends Component{
	protected int currentAnimation=0, currentStep=0;
	protected int lastAnimation=0, lastStep=0;
	protected final long delay;
	protected long lastTime=0, curr;
	protected final AnimationUpdateCondition animationUpdate;

	protected Animation_Cmpnt(Entity owner, Animation_Properties animation_p) {
		super(owner);
		this.delay=animation_p.delayMillis * 1_000_000L;
		if(animation_p.updateOn_p!=null)
			this.animationUpdate = animation_p.updateOn_p.build(this);
		else
			this.animationUpdate = new AnimationUpdateCondition(this) {
				@Override
				protected void setAnimation() {
					stepForward();
				}
			};
	}

	/**Selects row and column of the sheet
	 * Default will leave the current animation and move to the next step*/
	protected final void selectAnimation(){
		animationUpdate.setAnimation();
	}

	protected void stepForward(){
		currentStep = (currentStep+1)%currSheet().getAnimationLength(currentAnimation);
	}

	public final float get_xMin_Bias(){
		return currSheet().getFrame_xMin_bias(currentAnimation, currentStep);
	}
	public final float get_yMin_Bias(){
		return currSheet().getFrame_yMin_bias(currentAnimation, currentStep);
	}
	public final float get_xMax_Bias(){
		return currSheet().getFrame_xMax_bias(currentAnimation, currentStep);
	}
	public final float get_yMax_Bias(){
		return currSheet().getFrame_yMax_bias(currentAnimation, currentStep);
	}

	abstract void setSheetRect(Rect src);

	protected abstract Spritesheet currSheet();

	@Override
	public final ComponentType type() {
		return ComponentType.Animation;
	}

	public enum SheetType {Single, Multiple}
}