package com.example.dave.gameEngine.entity_component;


import com.example.dave.gameEngine.dataStructures.RandomExtractor;
import com.example.dave.gameEngine.dataDriven.GameObj_Properties;
import com.example.dave.gameEngine.dataDriven.component.Summoner_Properties;

public class Summoner_Cmpnt extends Component {
	private final RandomExtractor<GameObj_Properties> extractor;

	Summoner_Cmpnt(Entity owner, Summoner_Properties summon_p) {
		super(owner);
		this.extractor = summon_p.extractor.build(this);
	}

	public GameObject summon(Float x, Float y){
		return owner.gs.createGameObject(extractor.extract(), x, y);
	}

	@Override
	public ComponentType type() {
		return ComponentType.Summoner;
	}
}
