package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.AnimationUpdate_Properties;
import com.example.dave.gameEngine.dataDriven.component.AnimationUpdate_Properties.*;
import com.example.dave.gameEngine.dataStructures.RandomExtractor;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;
import static com.example.dave.gameEngine.MyMath.between;
import static com.example.dave.gameEngine.MyMath.max;

public abstract class AnimationUpdateCondition{
	protected final Animation_Cmpnt animation_c;

	protected AnimationUpdateCondition(Animation_Cmpnt animation_c) {
		this.animation_c = animation_c;
	}

	protected abstract void setAnimation();

	public enum UpdateCondition {Time, Direction, Random}

	public static class UpdateOnDirection_Condition extends AnimationUpdateCondition {
		private final int N, NW, W, SW, S, SE, E, NE, Idle_DX, Idle_SX; //-1 is undefined
		private final float toleranceX, toleranceY;
		private float[] currDir, trueDir, lastDir=new float[2];
		private Direction dir, lastHDirection=Direction.E;//for extreme emergency
		private int ret;

		public UpdateOnDirection_Condition(Animation_Cmpnt animation_c, AnimationUpdate_Properties update_p) {
			super(animation_c);
			currDir=new float[2];
			UpdateOnDirection_Properties updateOnDir_p = (UpdateOnDirection_Properties)update_p;
			this.toleranceX=updateOnDir_p.toleranceX;
			this.toleranceY=updateOnDir_p.toleranceY;
			this.N = updateOnDir_p.N;
			this.NW = updateOnDir_p.NW;
			this.W = updateOnDir_p.W;
			this.SW = updateOnDir_p.SW;
			this.S = updateOnDir_p.S;
			this.SE = updateOnDir_p.SE;
			this.E = updateOnDir_p.E;
			this.NE = updateOnDir_p.NE;
			this.Idle_DX = updateOnDir_p.Idle_DX;
			this.Idle_SX = updateOnDir_p.Idle_SX;
		}

		@Override
		protected void setAnimation() {
			trueDir=animation_c.owner.getPhysical().getDirection(trueDir, false);
			currDir[X]=trueDir[X];
			currDir[Y]=trueDir[Y];
			if(_Log.LOG_ACTIVE){
				_Log.d("Animation", "Velocity "+currDir[X]+" "+currDir[Y]);}
			{	if (between(currDir[X], -toleranceX, toleranceX))
				currDir[X] = 0f;
				if (between(currDir[Y], -toleranceY, toleranceY))
					currDir[Y] = 0f;
			}
			dir = Direction.getDirection(currDir[X], currDir[Y], lastDir[X], lastDir[Y]);
			ret = getByDirection(dir);

			if(ret!=animation_c.currentAnimation){
				if (ret != -1) {
					animation_c.currentAnimation = ret;
					animation_c.currentStep = 0;

					if(dir==Direction.NW || dir==Direction.W || dir==Direction.SW)
						lastHDirection=Direction.W;
					else if(dir==Direction.NE || dir==Direction.E || dir==Direction.SE)
						lastHDirection=Direction.E;
				}
				else {//dir does not have animation.
					ret = closestAvailableDirection();//updates lastHDirection too
				}
			}
			//could be the same due to closestAvailableDirection
			if(ret==animation_c.currentAnimation)
				animation_c.stepForward();
			lastDir[X] = currDir[X];
			lastDir[Y] = currDir[Y];
		}

		private int closestAvailableDirection(){
			switch (dir) {
				case N: {
					//if(NW==-1 && NE==-1) return Idle;
					if (trueDir[X] < 0) {
						lastHDirection=Direction.W;
						if (NW != -1) return NW;
						else return Idle_SX;
					}
					if (trueDir[X] > 0) {
						lastHDirection=Direction.E;
						if (NE != -1) return NE;
						else return Idle_DX;
					}
					else return idleAnimation();
				}
				case NW: {
					lastHDirection=Direction.W;
					if(trueDir[Y]>-trueDir[X]){
						if(N!=-1) return N;
						else if(W!=-1) return W;
						else return Idle_SX;
					}
					else{
						if(W!=-1) return W;
						else if(N!=-1) return N;
						else return Idle_SX;
					}
				}
				case W:{
					//if(NW==-1 && SW==-1) return Idle;
					lastHDirection=Direction.W;
					if (trueDir[Y] < 0) {
						if (SW != -1) return SW;
						else return Idle_SX;
					}
					if (trueDir[Y] > 0) {
						if (NW != -1) return NW;
						else return Idle_SX;
					}
					return Idle_SX;
				}
				case SW:{
					lastHDirection=Direction.W;
					if(-trueDir[Y]>-trueDir[X]){
						if(S!=-1) return S;
						else if(W!=-1) return W;
						else return Idle_SX;
					}
					else{
						if(W!=-1) return W;
						else if(S!=-1) return S;
						else return Idle_SX;
					}
				}
				case S:{
					//if(SW==-1 && SE==-1) return Idle;
					if(trueDir[X]<0){
						lastHDirection=Direction.W;
						if(SW!=-1) return SW;
						else return Idle_SX;
					}
					if(trueDir[X]>0){
						lastHDirection=Direction.E;
						if(SE!=-1) return SE;
						else return Idle_DX;
					}
					else return idleAnimation();
				}
				case SE:{
					lastHDirection=Direction.E;
					if(-trueDir[Y]>trueDir[X]){
						if(S!=-1) return S;
						else if(E!=-1) return E;
						else return Idle_DX;
					}
					else{
						if(E!=-1) return E;
						else if(S!=-1) return S;
						else return Idle_DX;
					}
				}
				case E:{
					//if(NE==-1 && SE==-1) return Idle;
					lastHDirection=Direction.E;
					if(trueDir[Y]<0){
						if(SE!=-1) return SE;
						else return Idle_DX;
					}
					if(trueDir[Y]>0){
						if(NE!=-1) return NE;
						else return Idle_DX;
					}
					return Idle_DX;
				}
				case NE:{
					//if(NW==-1 && NE==-1) return Idle;
					lastHDirection=Direction.E;
					if(trueDir[Y]>trueDir[X]){
						if(N!=-1) return N;
						else if(E!=-1) return E;
						else return Idle_DX;
					}
					else{
						if(E!=-1) return E;
						else if(N!=-1) return N;
						else return Idle_DX;
					}
				}
			}
			if(_Log.LOG_ACTIVE){
				_Log.e("Animation", "in closestAvailableDirection had to return default Idle_DX");}
			return Idle_DX;
		}

		private int idleAnimation() {
			if(lastHDirection==Direction.W)
				return Idle_SX;
			else
				return Idle_DX;
		}

		private int getByDirection(Direction d){
			switch (d){
				case N:
					return N;
				case NW:
					return NW;
				case W:
					return W;
				case SW:
					return SW;
				case S:
					return S;
				case SE:
					return SE;
				case E:
					return E;
				case NE:
					return NE;
				case Idle:
					if(trueDir[X]>=0)
						return Idle_DX;
					else
						return Idle_SX;
				default:
					return idleAnimation();
			}
		}

		private enum Direction{N, NW, W, SW, S, SE, E, NE, Idle;

			/**x, y are velocities*/
			private static Direction getDirection(float x, float y){
				if(x==0f && y==0f)
					return Idle;
				else if(x==0f && y>0f) //Nord
					return N;
				else if(x<0f && y>0f) //Nord West
					return NW;
				else if(x<0f && y==0f) //West
					return W;
				else if(x<0f && y<0f) //Sud West
					return SW;
				else if(x==0f && y<0f) //Sud
					return S;
				else if(x>0f && y<0f) //Sud East
					return SE;
				else if(x>0f && y==0f) //East
					return E;
				else if(x>0f && y>0f) //Nord East
					return NE;
				else return Idle;//impossible
			}

			private static Direction getDirection(float x, float y, float lastX, float lastY){
				if(x==0f && y==0f)
					return Idle;
				else if(x==0f && y>0f) //Nord
					return N;
				else if(x<0f && y>0f) //Nord West
					return NW;
				else if(x<0f && y==0f) { //West
					if(lastY>0)  return SW;
					return W;
				}
				else if(x<0f && y<0f) //Sud West
					return SW;
				else if(x==0f && y<0f) //Sud
					return S;
				else if(x>0f && y<0f) //Sud East
					return SE;
				else if(x>0f && y==0f){ //East
					if(lastY>0)  return SE;
					return E;
				}
				else if(x>0f && y>0f) //Nord East
					return NE;
				else return Idle;//impossible
			}
		}
	}
	public static class UpdateRandomly_Condition extends AnimationUpdateCondition {
		private final RandomExtractor<Integer> random;
		private final static int LINGER=-1;
		private int last, curr;

		public UpdateRandomly_Condition(Animation_Cmpnt animation_c, AnimationUpdate_Properties update_p) {
			super(animation_c);
			UpdateRandomly_Properties updateRandomly_p = (UpdateRandomly_Properties)update_p;
			last=-1;
			int n_anim=updateRandomly_p.nAnimations;
			int lingerEntries=computeLingerEntries(n_anim, updateRandomly_p.lingerChance);
			Integer options[] = new Integer[n_anim + lingerEntries];
			int i=0;
			for(; i<n_anim; i++)
				options[i] = i;
			for(; i<n_anim+lingerEntries; i++)
				options[i] = LINGER;
			random = new RandomExtractor<>(options);
		}

		/**find k t.c.
		 * (1 + k) / (n + k) = c
		 * k = (nc - 1) / (1 - c) */
		private int computeLingerEntries(int n, float c){
			return max(
					(int)( (n*c - 1F)/(1F - c) ),
					0);
		}

		@Override
		protected void setAnimation() {
			curr = random.extract();
			if(curr==LINGER){
				curr=last;
				if(_Log.LOG_ACTIVE){
					_Log.d("Animation", "Random Update: Linger");}
			}
			else if(curr!=last){
				animation_c.currentAnimation = curr;
				animation_c.currentStep = 0;
				last=curr;
				if(_Log.LOG_ACTIVE){
					_Log.d("Animation", "Random Update: Change to "+curr);}
			}
			animation_c.stepForward();
		}
	}
}