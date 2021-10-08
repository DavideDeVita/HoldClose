package com.example.dave.gameEngine.dataDriven.component;

import androidx.annotation.Nullable;

import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.PropertyException;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;
import com.example.dave.gameEngine.entity_component.AnimationUpdateCondition;
import com.example.dave.gameEngine.entity_component.AnimationUpdateCondition.*;
import com.example.dave.gameEngine.entity_component.Animation_Cmpnt;

public abstract class AnimationUpdate_Properties extends Properties<AnimationUpdate_Properties> implements Buildable<AnimationUpdateCondition> {
	public AnimationUpdate_Properties() {
		super(null);
	}

	public static class UpdateOnDirection_Properties extends AnimationUpdate_Properties{
		public float toleranceX, toleranceY;
		public int N, NW, W, SW, S, SE, E, NE, Idle_DX, Idle_SX; //-1 is undefined

		@Override
		public void reset() {
			toleranceX = toleranceY = 0f;
			N = NW = W = SW = S = SE = E = NE = Idle_DX = Idle_SX = -1;
		}

		@Override
		public UpdateOnDirection_Properties clone() {
			UpdateOnDirection_Properties newInstance = new UpdateOnDirection_Properties();
			newInstance.toleranceX=toleranceX;
			newInstance.toleranceY=toleranceY;
			newInstance.N=N;
			newInstance.NW=NW;
			newInstance.W=W;
			newInstance.SW=SW;
			newInstance.S=S;
			newInstance.SE=SE;
			newInstance.E=E;
			newInstance.NE=NE;
			newInstance.Idle_DX=Idle_DX;
			newInstance.Idle_SX=Idle_SX;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return toleranceX>0F && toleranceY>0F &&
					Idle_DX>=0 && Idle_SX>=0;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="AnimationUpdateDirection_Properties not ready:";
				if(toleranceX<=0F) msg+="\ntoleranceX<=0 :"+toleranceX;
				if(toleranceY<=0F) msg+="\ntoleranceY<=0 :"+toleranceY;
				if(Idle_DX<0F) msg+="\nIdle_DX<0 :"+Idle_DX;
				if(Idle_SX<0F) msg+="\nIdle_SX<0 :"+Idle_SX;
				return new PropertyException(msg);
			}
			return null;
		}

		@Override
		public UpdateOnDirection_Condition build(@Nullable Object caller) {
			if(!(caller instanceof Animation_Cmpnt))
				throw new IllegalArgumentException("caller is not Animation_Cmpnt");
			return new UpdateOnDirection_Condition((Animation_Cmpnt)caller, this);
		}
	}
	public static class UpdateRandomly_Properties extends AnimationUpdate_Properties{
		public float lingerChance;
		public int nAnimations; //required;

		@Override
		public void reset() {
			lingerChance=0;
			nAnimations=0;
		}

		@Override
		public UpdateRandomly_Properties clone() {
			UpdateRandomly_Properties newInstance = new UpdateRandomly_Properties();
			newInstance.lingerChance=lingerChance;
			newInstance.nAnimations=nAnimations;
			return newInstance;
		}

		@Override
		public boolean isReady() {
			return lingerChance>0 && nAnimations>0;
		}

		@Override
		public PropertyException getErrors() {
			if(!isReady()){
				String msg="UpdateRandomly_Properties not ready:";
				if(lingerChance<=0F) msg+="\nlingerChance<=0 :"+lingerChance;
				if(nAnimations<=0) msg+="\nnAnimations<=0 :"+nAnimations;
				return new PropertyException(msg);
			}
			return null;
		}

		@Override
		public UpdateRandomly_Condition build(@Nullable Object caller) {
			if(!(caller instanceof Animation_Cmpnt))
				throw new IllegalArgumentException("caller is not Animation_Cmpnt");
			return new UpdateRandomly_Condition((Animation_Cmpnt)caller, this);
		}
	}
}