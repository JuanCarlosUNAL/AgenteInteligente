package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

class HeuristicFactory {
	@SuppressWarnings("rawtypes")
	public static Heuristic getHeuristic (String name){
		/**
		 * Organiza los nodos como una pila
		 */
		if( name.equals("DFS") ){
			return new Heuristic<Integer>() {
				@Override
				public Integer evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
					return coord.consecutivo;
				}

				@Override
				public boolean comparator(Integer retador, Integer retado) {
					return retador > retado;
				}
			};
		/**
		 * Organiza los nodos en forma de cola
		 */
		}else if (name.equals("BFS")){ 
			return new Heuristic<Integer>() {
				@Override
				public Integer evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
					return m.getPath(coord, agent.posicion).size();
				}

				@Override
				public boolean comparator(Integer retador, Integer retado) {
					return retador < retado;
				}
			};
		/**
		 * la distancia euclidiana a un punto Agregando mas importancia a los nodos conexos
		 */
		}else if (name.equals("DistanciaEuclidiana")){ 
			return new Heuristic<Double>() {

				@Override
				public Double evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
					double geometrica = agent.posicion.distance(coord);
					double prior = (m.isConected(agent.posicion, coord ))? 0: (geometrica + 0.5);
					return geometrica + prior;
				}

				@Override
				public boolean comparator(Double retador, Double retado) {
					return retador < retado;
				}
			};
		}
		else
			throw new Error("La heuristica " + name + " no existe.");
	}
}
