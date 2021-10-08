package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.ai.AI_engine;
import com.example.dave.gameEngine.ai.Action;
import com.example.dave.gameEngine.dataDriven.ai.DecisionTree_Properties;

import java.util.Iterator;
import java.util.List;

public class AI_cmpnt extends Component {
	private final AI_engine engine;

	protected String log="";
	private List<Action> toDo;
	private Iterator<Action> iter;
	private int i;

	public AI_cmpnt(Entity owner, DecisionTree_Properties dt_p) {
		super(owner);
		this.engine=dt_p.build(null);
	}

	/*public AI_cmpnt(Entity owner, FiniteStateMachine_Properties fsm_p) {
		super(owner);
		this.engine=engine;
	}*/

	public void act(GameSection gs){
		toDo = engine.exec(owner, gs);
		iter=toDo.iterator();
		for(i=toDo.size(); i>0; i--){
			//if( iter.next().act(owner, gs, engine) ) //Enable this to prevent log of failed cooldown and chances
			iter.next().act(owner, gs, engine);
			if(_Log.LOG_ACTIVE){
				engine.log();}
		}
	}

	@Override
	public ComponentType type() {
		return ComponentType.AI;
	}
}
