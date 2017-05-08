package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import java.util.TreeMap;
import java.util.TreeSet;

class Comida {
	//par que indica el tipo de comida y el grado de energia que aporta,
	//energia negativa indica comida mala.
	private TreeMap<Integer,Integer> tipos;
	private TreeSet<Coordenada> coordenadas;
	private Pila posiblesComidas;
	
	@SuppressWarnings("unchecked")
	public Comida (TeseoAgent a) {
		this.tipos = new TreeMap<Integer,Integer>();
		this.coordenadas = new TreeSet<Coordenada>();
		this.posiblesComidas = new Pila(a, HeuristicFactory.getHeuristic("BFS"));
	}
	
	/**
	 * Agrega un nuevo tipo de comida.
	 * 
	 * @param tipo	tipo de comida
	 * @param aporte	aporte energetico de la comida
	 */
	public void addNewClass(int tipo, int aporte) {			
		this.tipos.put(tipo, aporte);
	}
	
	/**
	 * Agrega una nueva coordenada con comida, solo la agrega a la 
	 * pila si aporta energia al agente
	 * 
	 * @param c Coordenada nueva
	 */
	public void addNewCoord( Coordenada c ) {
		if( c.comida > 0 && !this.coordenadas.contains(c))
			this.posiblesComidas.add(c);
	}
	
	/**
	 * Retorna la comida con el camino mas cercano
	 * @return
	 */
	public Coordenada getComidaCercana (){
		this.posiblesComidas.reaorganizarPila();
		return this.posiblesComidas.verSiguiente();
	}
}
