package unalcol.agents.examples.isi2017I.turianos.teseo.Agent1;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

class Mapa {
	
	private TreeMap< coordenada, TreeSet< coordenada > > grafo;
	
	public Mapa () {
		 this.grafo = new TreeMap<coordenada,TreeSet<coordenada>>();
	}
	public void makeLink (coordenada a, coordenada b){
		if (!grafo.containsKey(a)){
			grafo.put(a, new TreeSet<coordenada>());
		}
		if (!grafo.containsKey(b)){
			grafo.put(b, new TreeSet<coordenada>());
		}
		grafo.get(a).add(b);
		grafo.get(b).add(a);
		return;
	}
	public boolean hascoordenada(coordenada a){
		return grafo.containsKey(a);
	}
	public boolean isConected (coordenada a, coordenada b){
		return grafo.get(a).contains(b);
	}
	/**
	 * Devuelve el camino mas corto entre dos puntos del grafo, recorrido realizado con BFS
	 * 
	 * @param a	Punto inicial
	 * @param b	Punto destino
	 * @return LinkedList con los puntos en el orden en que se deben recorrer
	 */
	public LinkedList<coordenada> getPath (coordenada a, coordenada b){
		
		TreeMap<coordenada,coordenada> padre = new TreeMap<coordenada,coordenada>();
		Vector<coordenada> color = new Vector<coordenada>();  
		LinkedList<coordenada> cola =new LinkedList<coordenada>();
		cola.add( a );
		color.add(a);
		coordenada curr=null;
		while ( cola.size() > 0 && !color.contains(b) ){
			curr = cola.remove(0);
			for (coordenada c : this.grafo.get(curr)) {
				if(!color.contains(c)){
					color.add(c);
					cola.add(c);
					padre.put(c,curr);
				}
			}
		}
		
		LinkedList<coordenada> path= new LinkedList<coordenada>();
		curr = b;
		path.add( curr );
		while (curr != a){
			coordenada p = padre.get(curr);
			curr = p;
			path.add( curr );
		}
		path.removeLast();
		return path;
	}
	@Override
	public String toString(){
		String ans = "{";
		for (Map.Entry<coordenada, TreeSet<coordenada>> entry : grafo.entrySet()) {
			ans += entry.getKey().toString() + "[";
			for ( coordenada c : entry.getValue() ){
				ans += c.toString() + ", ";
			}
			ans += "]\n";
		}
		return ans;
	}
}
