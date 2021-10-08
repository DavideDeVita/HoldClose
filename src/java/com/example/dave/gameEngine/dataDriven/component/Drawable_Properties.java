package com.example.dave.gameEngine.dataDriven.component;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.Color;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.entity_component.Drawable_Cmpnt.Motive;
import com.example.dave.gameEngine.myMultimedia.DrawableRes;

public class Drawable_Properties extends Properties<Drawable_Properties> {
	protected Motive motive;
	public Motive_Properties motive_p;
	public float eps=0.5f;
	public boolean lockSpriteAngle =false;
	//
	private static final Pool<Drawable_Properties> drawablePool = new Pool<>(
			new Pool.PoolObjectFactory<Drawable_Properties>() {
				@Override
				public Drawable_Properties createObject() {
					return new Drawable_Properties();
				}
			},
			MainActivity.readIntOnDemand(R.integer.drawable_Properties_Pool_size)
	);
	private Drawable_Properties(){
		super(drawablePool);
	}

	public static Drawable_Properties _new() {
		return drawablePool.newObject();
	}

	public void setMotive(Motive motive) {
		this.motive = motive;
		switch (motive){
			case Bitmap:
				motive_p = new BitmapMotive_Properties();
				break;
			case Monochrome:
				motive_p = new MonochromeMotive_Properties();
				break;
			case Animated:
				motive_p = new AnimatedMotive_Properties();
				break;
		}
	}

	public Motive getMotive() {
		return motive;
	}

	public static abstract class Motive_Properties extends Properties<Motive_Properties> {
		public Motive_Properties() {
			super(null);
		}
	}
	public static class MonochromeMotive_Properties extends Motive_Properties{
		public Color color =null;
		public Integer variance=null;

		@Override
		public void reset() {
			this.color = null;
			this.variance = null;
		}

		@Override
		public MonochromeMotive_Properties clone() {
			MonochromeMotive_Properties newInstance= new MonochromeMotive_Properties();
			newInstance.color = color;
			newInstance.variance=variance;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="DrawableSimple_Properties not ready:";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class BitmapMotive_Properties extends Motive_Properties{
		public RandomExtractor<DrawableRes> drawables = null;
		public float bias_xMin, bias_xMax, bias_yMin, bias_yMax;

		@Override
		public void reset() {
			this.drawables = null;
			bias_xMin = bias_yMin = bias_xMax = bias_yMax = 0;
		}

		@Override
		public BitmapMotive_Properties clone() {
			BitmapMotive_Properties newInstance= new BitmapMotive_Properties();
			//newInstance.drawables = drawables;
			newInstance.drawables = drawables.clone();
			newInstance.bias_xMin = bias_xMin;
			newInstance.bias_xMax = bias_xMax;
			newInstance.bias_yMin = bias_yMin;
			newInstance.bias_yMax = bias_yMax;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return drawables !=null;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="DrawableSimple_Properties not ready:";
				if(drawables ==null)        msg+="\ndrawable is null";
				return new PropertyException(msg);
			}
			return null;
		}
	}
	public static class AnimatedMotive_Properties extends Motive_Properties{
		public Animation_Properties animation_p;

		@Override
		public void reset() {
			this.animation_p = null;
		}

		@Override
		public AnimatedMotive_Properties clone() {
			AnimatedMotive_Properties newInstance= new AnimatedMotive_Properties();
			if(animation_p!=null)
				newInstance.animation_p = animation_p.clone();
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return animation_p !=null;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="AnimatedMotive_Properties not ready:";
				if(animation_p ==null)        msg+="\nanimation_p is null";
				return new PropertyException(msg);
			}
			return null;
		}
	}

	@Override
	public void reset() {
		eps=0.5f;
		lockSpriteAngle = false;
		motive_p=null;
		motive=null;
	}

	@Override
	public Drawable_Properties clone() {
		Drawable_Properties newInstance = _new();
		newInstance.setMotive(motive);
		newInstance.motive_p = motive_p.clone();
		newInstance.eps=eps;
		newInstance.lockSpriteAngle=lockSpriteAngle;
		return newInstance;
	}

	@Override
	public boolean isReady() {
		return motive!=null &&
				motive_p!=null && motive_p.isReady()
				;
	}

	@Override
	public PropertyException getErrors() {
		if(!isReady()){
			String msg="Drawable_Properties not ready:";
			if(motive==null)        msg+="\nmotive is null";
			if(motive_p==null)        msg+="\nmotive_p is null";
			else if(!motive_p.isReady())        msg+="\n"+motive_p.getErrors().getMessage();
			return new PropertyException(msg);
		}
		return null;
	}


}
