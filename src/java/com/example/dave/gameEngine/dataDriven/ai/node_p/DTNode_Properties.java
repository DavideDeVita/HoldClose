package com.example.dave.gameEngine.dataDriven.ai.node_p;

import com.example.dave.gameEngine.ai.decisionTree.Node;
import com.example.dave.gameEngine.dataDriven.Properties;
import com.example.dave.gameEngine.dataDriven.ai.Buildable;

public abstract class DTNode_Properties extends Properties<DTNode_Properties> implements Buildable<Node> {

	public DTNode_Properties() {
		super(null);
	}
}
