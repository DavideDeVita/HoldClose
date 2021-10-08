package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.Control_Properties;
import com.example.dave.gameEngine.gameLoop;

public class Control_Cmpnt extends Component {
	private final long cooldown, holdTimer;
	private final FloatValue penalty; //The game is not called Hold for long
	/*Removable*/private boolean isControlled=false;
	private final static long mTOn=1_000_000L; //milli to nano
	private Phase setUpFor = Phase.Start;

	private enum Phase{Start, Keep};

	public Control_Cmpnt(Entity owner, Control_Properties controlP) {
		super(owner);
		this.cooldown = controlP.cooldown*mTOn;
		this.holdTimer = controlP.holdTimer*mTOn;
		this.penalty = controlP.penalty;
		if(_Log.LOG_ACTIVE){
			_Log.i("Control", "cd:"+(cooldown/(mTOn*1000))+"\nhold:"+(holdTimer/(mTOn*1000))+"\npen:"+(penalty));}
	}

	private long time, next=0;
	@gameLoop public boolean tryStartControl(){
		if(setUpFor==Phase.Start) {
			time = System.nanoTime();
			if (!isControlled && time > next) {
				isControlled = true;
				setUpFor = Phase.Keep;
				next = time + holdTimer; //Release this before next
				return true;
			}
		}
		/*anyway else*/ return false;
	}

	@gameLoop public boolean tryKeepControl(){
		if(isControlled && setUpFor==Phase.Keep) {
			time = System.nanoTime();
			if (isControlled && time <= next)
				return true;
			else { //works as end control
				setUpFor = Phase.Start;
				next = time + (long) (cooldown * penalty.get());
				isControlled = false;
				return false;
			}
		}
		else return false;
	}

	@gameLoop public void endControl(){
		if(isControlled && setUpFor==Phase.Keep) {
			time = System.nanoTime();
			next = time + cooldown; //No penalty ending
			setUpFor=Phase.Start;
			isControlled = false;
		}
	}

	/**Should I signal the player that he can't control*/
	public boolean canControl(){
		if(isControlled) return true;
		//else
		time = System.nanoTime();
		return (time > next);
	}

	@Override
	public ComponentType type() {
		return ComponentType.Control;
	}
}
