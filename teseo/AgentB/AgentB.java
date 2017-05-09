package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;

public class AgentB extends TeseoAgent implements AgentProgram {

	public AgentB(SimpleLanguage lang) {
		super(lang, HeuristicFactory.getDistanciEuclidiana());
	}

}
