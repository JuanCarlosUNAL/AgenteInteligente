package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import java.util.TreeMap;
import java.util.TreeSet;

class Comida {
	public static final int NO_ASIGNADO = -1;
	public static final int SIN_COMIDA = 0;
	
	
	//par que indica el tipo de comida y el grado de energia que aporta,
	//energia negativa indica comida mala.
	private TreeMap<Integer,Integer> tipos;
	private TreeSet<Coordenada> coordenadas;
	private Pila posiblesComidas;
	
	public Comida (TeseoAgent a) {
		this.tipos = new TreeMap<Integer,Integer>();
		this.coordenadas = new TreeSet<Coordenada>();
		this.posiblesComidas = new Pila(a, HeuristicFactory.getBFS());
	}
	
	/**
	 * Devuelve si un tipo de comida es buena o no para el agente
	 * @param tipo
	 * @return
	 */
	public boolean esBuenaComida(int tipo) {
		return this.tipos.get(tipo) > 0;
	}
	
	/**
	 * Dice si el tipo de comida ya existe en la memoria 
	 * @param tipo
	 * @return
	 */
	public boolean existTipo(int tipo){
		return this.tipos.containsKey(tipo);
	}
	
	/**
	 * Agrega un nuevo tipo de comida.
	 * 
	 * @param tipo	tipo de comida
	 * @param aporte	aporte energetico de la comida
	 */
	public void addNewTipo(int tipo, int aporte) {			
		this.tipos.put(tipo, aporte);
	}
	
	/**
	 * Agrega una nueva coordenada con comida, solo la agrega a la 
	 * pila si aporta energia al agente y aun no contiene la coordenada
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
