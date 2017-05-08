package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

/**
 * Interfase para implementar una heuristica, recibe un agente con todos los datos del agente 
 * y la coordenada nueva que aun no se ha explorado, y que sera ubicada o reubicada en la pila.
 * 
 * Dependiendo de  la heuristica el nodo se puede reacomodar mientras explora el mapa.
 * 
 * La Heuristica se agrega como argumento al constructor de la pila
 * 
 * @author Turianos
 *
 */
interface Heuristic <E>{
	/**
	 * Retorna el peso para la cola de prioridad. El peso puede basarse el las propiedades de el agente, 
	 * las propiedades de la coordenada y el mapa que ve el agente.
	 * Ej.
	 * para DFS o BFS, el peso es igual al consecutivo de la coordenada
	 * 
	 * 
	 * @param agent
	 * @param coord
	 * @return
	 */ 
	public E evaluate(Mapa m, TeseoAgent agent, Coordenada coord);
	
	/**
	 * Compara los pesos para determinar quien debe ir primero en la lista. Para esto hay un peso retador y otro retado
	 * si el retador cumple mejor los requerimientos retornamos verdadero, si no retornamos falso.
	 * Ej. 
	 * para BFS el consecutivo mas pequeño debe ir primero en la lista, esto comportamiento de cola
	 * para DFS el consecutivo mas grande, osea el la ultima coordenada, debe ir primero en la lista
	 * 
	 * @param retador
	 * @param retado
	 * @return
	 */
	public boolean comparator(E retador, E retado);
}
