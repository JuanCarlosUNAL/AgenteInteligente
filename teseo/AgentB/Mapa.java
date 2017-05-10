package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

class Mapa {
	
	private TreeMap< Coordenada, TreeSet< Coordenada > > grafo;
	
	public Mapa () {
		 this.grafo = new TreeMap<Coordenada,TreeSet<Coordenada>>();
	}
	
	public void makeLink (Coordenada a, Coordenada b){
		if (!grafo.containsKey(a)){
			grafo.put(a, new TreeSet<Coordenada>());
		}
		if (!grafo.containsKey(b)){
			grafo.put(b, new TreeSet<Coordenada>());
		}
		grafo.get(a).add(b);
		grafo.get(b).add(a);
		return;
	}
	public boolean hascoordenada(Coordenada a){
		return grafo.containsKey(a);
	}
	public boolean isConected (Coordenada a, Coordenada b){
		return grafo.get(a).contains(b);
	}
	/**
	 * Devuelve el camino mas corto entre dos puntos del grafo, recorrido realizado con BFS
	 * 
	 * @param a	Punto inicial
	 * @param b	Punto destino
	 * @return LinkedList con los puntos en el orden en que se deben recorrer
	 */
	public LinkedList<Coordenada> getPath (Coordenada a, Coordenada b){
		
		if(a.equals(b)){
			LinkedList<Coordenada> aux = new LinkedList<Coordenada>();
			aux.add(b);
			return aux;
		}
		
		TreeMap<Coordenada,Coordenada> padre = new TreeMap<Coordenada,Coordenada>();
		Vector<Coordenada> color = new Vector<Coordenada>();  
		LinkedList<Coordenada> cola =new LinkedList<Coordenada>();
		cola.add( a );
		color.add(a);
		Coordenada curr=null;
		while ( cola.size() > 0 && !color.contains(b) ){
			curr = cola.remove(0);
			for (Coordenada c : this.grafo.get(curr)) {
				if(!color.contains(c)){
					color.add(c);
					cola.add(c);
					padre.put(c,curr);
				}
			}
		}
		
		LinkedList<Coordenada> path= new LinkedList<Coordenada>();
		curr = b;
		path.add( curr );
		while (curr != a){
			Coordenada p = padre.get(curr);
			curr = p;
			path.add( curr );
		}
		path.removeLast();
		return path;
	}
	@Override
	public String toString(){
		String ans = "{";
		for (Map.Entry<Coordenada, TreeSet<Coordenada>> entry : grafo.entrySet()) {
			ans += entry.getKey().toString() + "[";
			for ( Coordenada c : entry.getValue() ){
				ans += c.toString() + ", ";
			}
			ans += "]\n";
		}
		return ans;
	}
}
