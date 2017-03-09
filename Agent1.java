package unalcol.agents.simulate.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

//TODO: verificar el sistema de rotacion y coordenadas, toma la derecha del plano como negativa. Verificar con debug
//TODO: crear un package nuevo para este archivo, para acceder correctamente a las percepciones

public class Agent1 implements AgentProgram {
	
	/**
	 * Coordenada para simplificar las operaciones de ubicaion y almacenamiento de los lugares visitados
	 * @author Juancho
	 *
	 */
	private class coordenada extends Point implements Comparable<coordenada>{
		public int comida;
		
		public coordenada (double d, double e){
			super();
			this.setLocation(d,e);
		}
		public coordenada (int x, int y, int comida){
			super.setLocation(x,y);
			this.comida = comida;
			return;
		}
		
		/**
		 * Rota hacia la derecha la cordenada
		 */
		public void rotar(){
			int x = this.x;
			int y =  this.y;
			this.setLocation(y,-x);
			return;
		}
		
		@Override
		public String toString() {
			return "("+this.x + "," + this.y+")";
		}
		/**
		 * Compara dos coordenadas convieriendolas a string, 
		 * Si se modifica asegurar que la funcion pueda devolver numeros positivos y números negativos 
		 * para el manejo de diccionarios (arboles binarios).
		 */
		@Override
		public int compareTo(coordenada o) {
			return this.toString().compareTo(o.toString());
		}
		
	}
	/**
	 * Mapa que realiza las funciones de ubicacion del agente.
	 * 
	 * @author Juan Carlos, -- , --
	 *
	 */
	private class Mapa {
		
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
	
	//Variables de ubicacion
	private coordenada posicion;
	private coordenada dir;
	private Mapa map;
	
	//Variables de energia
	private int energia_max;
	private boolean recien_comio;
	private int energia_actual;
	
	//Acciones
	private Action rotate;
	private Action advance;
	private Action eat;
	private Action nothing;
	
	//percepciones
	private String[] percepts;
	
	/**
	 * Variables para el manejo del DFS y rutas planeadas
	 */
	private TreeSet<coordenada> visitados;
	private TreeMap<Integer, Boolean> comida;
	private Stack<coordenada> pila;
	private LinkedList<coordenada> plan;
	
	/**
	 * Constructor del Agente. Guarda las acciones que puede realizar
	 * y nombres de las percepciones, luego inicializa las estructuras y variables.
	 */
	public Agent1( SimpleLanguage lang ) {
		//Inicializa las acciones posibles
		//this.eat = new Action ( lang.getAction(4) );
		this.rotate = new Action ( lang.getAction(3) );
		this.advance = new Action ( lang.getAction(2) );
		this.nothing = new Action ( lang.getAction(0) );
		
		//Guarda los string de percepciones
		this.percepts = lang.percepts;
		
		this.init();
		
	}
	
	/**
	 * Recible las percepciones del ambiente las procesa y devuelve una acción.
	 * @param p Es un diccionario con claves de tipo string y valores variables dependiendo el tipo de percepcion 
	 */
	@Override
	public Action compute(Percept p) {		
		
		// Qué estoy percibiendo ahora ?
		boolean front = (boolean)(p.getAttribute( this.percepts[0] ));
		boolean right = (boolean)(p.getAttribute( this.percepts[1] ));
		boolean back  = (boolean)(p.getAttribute( this.percepts[2] ));
		boolean left  = (boolean)(p.getAttribute( this.percepts[3] ));
		
		boolean exit = false;;
		try{
			exit  = (boolean)(p.getAttribute( this.percepts[4] ));
		}catch( Exception e ){
			
		}
		
		/*boolean resource = 		 (boolean)(p.getAttribute(this.percepts[5]));
		int resource_class = 0;
		if (resource){
			boolean resource_color = (boolean)(p.getAttribute(this.percepts[6]));
			boolean resource_shape = (boolean)(p.getAttribute(this.percepts[7]));
			boolean resource_size =  (boolean)(p.getAttribute(this.percepts[8]));
			boolean resource_weight =(boolean)(p.getAttribute(this.percepts[9]));
			//boolean resource_type =(boolean)(p.getAttribute(this.percepts[10]));
			resource_class = (resource_color?1:0) + (resource_shape?2:0) + (resource_size?4:0) + (resource_weight?8:0); 
		}*/
		
		//int energy_level = (int) p.getAttribute(this.percepts[11]);
		//
		int estado = 0; //TODO: el estado inicial debe depender de las percepciones
		if (exit){
			return this.nothing;
		}
		
		Action accion = null;
		coordenada next = null;
		while ( accion == null )
			//System.out.println(estado);
			switch (estado) {
			// Se ha movido a una nueva coordenada, agrega a los vecinos 
			// a la pila y al mapa y la marca como visitada
			case 0:
				this.casilla_nueva(front, right, back, left);
				estado = 1;
				break;
			case 1:
				next = this.siguiente_pos(front, right, back, left);
				coordenada aux = new coordenada( posicion.x + dir.x,posicion.y +dir.y );
				
				if (aux.compareTo( next ) == 0 ){
					accion = this.advance;
					if (!plan.isEmpty()){
						this.posicion = this.plan.removeLast();
					}else{
						this.posicion = this.pila.pop();
					}
				}else{
					this.dir.rotar();
					accion = this.rotate;
				}
				
				estado = -1;
				break;
			case 2:
				break;
			default:
				throw new Error("Error en el ciclo del Agente ", null);
			}
		
		//this.energia_actual = energy_level;
		energia_max = this.energia_actual > this.energia_actual? energia_actual:energia_max;
		
		return accion;
	}
	
	/**
	 * Devuelve la siguiente posicion que deberia ocupar el agente deacuerdo a las estructuras plan y pila
	 * 
	 * @param front Percepcion de muros frontal
	 * @param right Percepcion de muros  derecha
	 * @param back 	Percepcion de muros  tracera
	 * @param left 	Percepcion de muros  izquierda
	 * @return 		La coordenada a la que debe moverse
	 */
	private coordenada siguiente_pos(boolean front, boolean  right, boolean  back, boolean left) {
		//Ejecuta cuando el plan esta vacio y es un vecino
		if ( plan.isEmpty() && this.isVecino(this.pila.peek(),front, right, back, left)){ 
			return pila.peek();
		}else{
			// marca un camino hacia la siguiente pocision que rrecorrera para continuar la busqueda
			if(plan.isEmpty()){
				this.plan = map.getPath(this.posicion, this.pila.pop()); 
				//this.plan.removeFirst(); // remueve la pocicion que tambien esta guardada en la pila
			}
			return plan.getLast();
		}
	}
	
	/**
	 * Retorna falso o verdadero, dependiendo si el parametro c es un vecino de la pocicion actual
	 * 
	 * @param c
	 * @param front Percepcion de muros frontal
	 * @param right Percepcion de muros derecha
	 * @param back 	Percepcion de muros tracera
	 * @param left 	Percepcion de muros izquierda
	 * @return		true or false 
	 */
	private boolean isVecino(coordenada c, boolean front, boolean right, boolean back, boolean left) {
		return this.vecinos(front, right, back, left).contains( c);
	}
	
	/**
	 * 
	 * Funcion que debe ser llamada solo si la casilla en la que se encuentra es una casilla nueva.
	 * No contiene verificacion de errores.
	 * 
	 * Agrega los hijos (vecinos) de la pocicion actual para realizar una busqueda en profundidad
	 * 
	 * @param front Percepcion de muros frontal
	 * @param right Percepcion de muros derecha
	 * @param back 	Percepcion de muros tracera
	 * @param left 	Percepcion de muros izquierda
	 */
	private void casilla_nueva(boolean front, boolean right, boolean back, boolean left) {
		ArrayList<coordenada> vecinos = this.vecinos(front,right,back,left);
		for (coordenada c : vecinos) {
			if (!this.visitados.contains(c)){
				this.visitados.add(c);
				pila.push(c);
			}
			map.makeLink(posicion, c);
		}
		return;
	}
	
	/**
	 * Devuelve los vecinos de la pocicion actual basado en el atributo de 
	 * posición del agente y las percepciones actuales
	 * 
	 * @param front Percepcion de muros frontal
	 * @param right Percepcion de muros derecha
	 * @param back 	Percepcion de muros tracera
	 * @param left 	Percepcion de muros izquierda
	 * @return		Retorna un ArrayList con las coordenadas de los vecinos
	 */
	private ArrayList<coordenada> vecinos(boolean front, boolean right, boolean back, boolean left){
		ArrayList<coordenada> ans =  new ArrayList<coordenada>();
		if(!left){
			ans.add(new coordenada( posicion.getX() - dir.getY(), posicion.getY() + dir.getX() ) );
		}
		if(!back){
			ans.add(new coordenada( posicion.getX() - dir.getX() , posicion.getY() - dir.getY()) );
		}
		if(!right){
			ans.add(new coordenada( posicion.getX() + dir.getY() , posicion.getY() - dir.getX() ) );
		}
		if(!front){
			ans.add( new coordenada( posicion.getX() + dir.getX() , posicion.getY() + dir.getY() ) );
		}
		return ans;
	}
	
	/**
	 * Inicializa las variables que se usan a lo largo de la vida del Agente.
	 * Puede que sea llamado desde el ambiente.
	 */
	@Override
	public void init() {
		System.out.println("Reiniciar agente");
		//inicializa la pocicion actual y el mapa
		this.posicion = new coordenada(0, 0);
		this.dir = new coordenada(0, 1, 0); // 
		this.map = new Mapa();
		
		//inicializa las estructuras para el DFS
		this.visitados = new TreeSet<coordenada>();
		this.visitados.add(this.posicion);
		this.pila = new Stack<coordenada>();
		
		// inicializa cola para rutas hacia sitios planeados
		this.plan = new LinkedList<coordenada>();
		
		//Inicializa variables de energia
		this.comida = new TreeMap<Integer, Boolean>();
		this.energia_actual = 0;
		this.energia_max= 1;
	}

}
