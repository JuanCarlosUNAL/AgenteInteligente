package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

/**
 * Interfase para implementar una heuristica, recibe un agente con todos los datos del agente 
 * y la coordenada nueva que aun no se ha explorado, y que sera ubicada o reubicada en la pila.
 * 
 * Dependiendo de  la heuristica el nodo se puede reacomodar mientras explora el mapa.
 * 
 * @author Turianos
 *
 */
interface Heuristic {
	double evaluate(AgentB agent, Coordenada coord);
}
