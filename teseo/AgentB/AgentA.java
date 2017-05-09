package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;

public class AgentA extends TeseoAgent implements AgentProgram {

	public AgentA(SimpleLanguage lang) {
		super(lang, HeuristicFactory.getDFS());
	}

}
