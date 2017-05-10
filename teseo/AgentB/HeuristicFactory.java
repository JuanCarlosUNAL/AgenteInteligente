package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

class HeuristicFactory {
	
	/**
	 * Organiza los nodos como una pila
	 */
	static Heuristic<?> getDFS(){
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
	}
	
	/**
	 * Organiza los nodos en forma de cola
	 */
	static Heuristic<?> getBFS() {
		return new Heuristic<Integer>() {
			@Override
			public Integer evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
				return m.getPath(agent.posicion, coord).size() + 1;
			}

			@Override
			public boolean comparator(Integer retador, Integer retado) {
				return retador < retado;
			}
		};

	}
	
	/**
	 * la distancia euclidiana a un punto Agregando mas importancia a los nodos conexos
	 */
	static Heuristic<?> getDistanciEuclidiana () {
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
	
	/**
	 * La distancia eclidiana pero medida con respecto al origen (0,0)
	 * y con rioridad a la mas cercana al agente
	 * @return
	 */
	static Heuristic<?> getDistanciaOrigen () {
		return new Heuristic<Double>() {
			
			private Coordenada origen = new Coordenada(0, 0);
			
			@Override
			public Double evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
				
				double distanciaOrigen = coord.distance(origen);
				double distanciaAgente = agent.posicion.distance(coord);
				double prior = (m.isConected(agent.posicion, coord ))? 0:1;
				return distanciaOrigen + distanciaAgente + prior;
			}

			@Override
			public boolean comparator(Double retador, Double retado) {
				return retador < retado;
			}
		};
		
	}
	
	/**
	 * La distancia eclidiana pero medida con respecot al origen (0,0)
	 * y con rioridad a la mas cercana al agente. Ademas cambia cada vez que el mapa tiene mas nodos
	 * Cuando el mapa obtiene cierta cantidad de nodos el origen se cambia a la pocision donde se encuentre el agente
	 * @return
	 */
	static Heuristic<?> getDistanciaOrigenConCambio () {
		return new Heuristic<Double>() {
			
			private Coordenada origen = new Coordenada(0, 0);
			private boolean origen_reasignado = false;
			
			@Override
			public Double evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
				if(agent.visitados.size() % 20 == 0 && !this.origen_reasignado) {
					this.origen = agent.posicion;
					this.origen_reasignado = true;
					System.out.println("Origen reasignado a " + this.origen.toString());
				}
				
				if(this.origen_reasignado && agent.visitados.size() % 25 == 1 )
					this.origen_reasignado = false;
				
				double distanciaOrigen = coord.distance(origen);
				double distanciaAgente = agent.posicion.distance(coord);
				double prior = (m.isConected(agent.posicion, coord ))? 0:1;
				return distanciaOrigen + distanciaAgente + prior;
			}

			@Override
			public boolean comparator(Double retador, Double retado) {
				return retador < retado;
			}
		};
		
	}
	
	/**
	 * La distancia eclidiana pero medida con respecot al origen (0,0)
	 * y con rioridad a la mas cercana al agente
	 * SImilar a la Distancia con cambio original pero esta vez la cambia si la distancia eclidiana 
	 * es mayor a un multiplo de un numero establecito
	 * 
	 * @return
	 */
	static Heuristic<?> getDistanciaOrigenConCambio2 () {
		return new Heuristic<Double>() {
			
			private Coordenada origen = new Coordenada(0, 0);
			private boolean origen_reasignado = false;
			private int distanciaMinima = 5;
			
			@Override
			public Double evaluate(Mapa m, TeseoAgent agent, Coordenada coord) {
				if(  (int)agent.posicion.distance(origen) % this.distanciaMinima == 0 ) {
					this.origen = agent.posicion;
					this.origen_reasignado = true;
					System.out.println("Origen reasignado a " + this.origen.toString());
				}
				
				if(this.origen_reasignado && (int)agent.posicion.distance(origen) % this.distanciaMinima > 2 )
					this.origen_reasignado = false;
				
				double distanciaOrigen = coord.distance(origen);
				double distanciaAgente = agent.posicion.distance(coord);
				double prior = (m.isConected(agent.posicion, coord ))? 0:1;
				return distanciaOrigen + distanciaAgente + prior;
			}

			@Override
			public boolean comparator(Double retador, Double retado) {
				return retador < retado;
			}
		};
		
	}
	
}
