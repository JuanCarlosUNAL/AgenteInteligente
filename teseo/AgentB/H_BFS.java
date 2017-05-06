package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

class H_BFS implements Heuristic {
	
	@Override
	public double evaluate(AgentB agent, Coordenada coord) {
		return coord.consecutivo;
	}

}
