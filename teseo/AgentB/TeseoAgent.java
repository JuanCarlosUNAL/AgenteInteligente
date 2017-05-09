package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

abstract class TeseoAgent implements AgentProgram 
{

	//variables para avanzar
	Coordenada posicion, dir;
	
	//energy variables
	Comida comida;
	boolean probarComida, energiaMaximaAsignada, buscandoComida;
	int energia_max, energia_actual, recien_comio, energia_anterior;
	
	//variables para las paredes
	private boolean front, back, left, right;
	
	//variables para deteccion de agentes vecinos
	boolean aFront, aBack, aLeft, aRight; 
	int espera; // tiempo que lleva eseperando
	private Coordenada CoordenadaEnEspera; //reubicacion de celda ocupada
	
	//actions
	private Action rotate, advance, eat, nothing;
	
	//perceptions
	private String[] percepts;
	
	//planeacion de rutas
	private TreeSet<Coordenada> visitados;
	private Pila pila;
	private LinkedList<Coordenada> plan;
			Mapa mapa;
	
	/**
	 * Constructor del Agente. Guarda las acciones que puede realizar
	 * y nombres de las percepciones, luego inicializa las estructuras y variables.
	 */
	public TeseoAgent( SimpleLanguage lang, Heuristic<?> h ) {
		//Inicializa las acciones posibles
		this.eat = new Action ( lang.getAction(4) );
		this.rotate = new Action ( lang.getAction(3) );
		this.advance = new Action ( lang.getAction(2) );
		this.nothing = new Action ( lang.getAction(0) );
		
		//Guarda los string de percepciones
		
		int size_percepts = lang.getPerceptsNumber();
		String[] lang_percepts = new String[size_percepts];
		for(int i = 0;i < size_percepts; i++)
			lang_percepts[i] = lang.getPercept(i);
		this.percepts = lang_percepts;
	
		this.pila = new Pila(this, h);
		
		this.init();
		
	}
	
	/**
	 * Recible las percepciones del ambiente las procesa y devuelve una acciï¿½n.
	 * @param p Es un diccionario con claves de tipo string y valores variables dependiendo el tipo de percepcion 
	 */
	@Override
	public Action compute(Percept p) {		
		
		/*
		 ********************************************
		 *				PERCEPCIONES				*
		 ********************************************
		 */
		
		// si es el estado de acceptaci�n
		if ( (boolean)(p.getAttribute( this.percepts[4] )) ){	
			return this.nothing;
		}
		
		// percepcion de paredes
		this.front = (boolean)(p.getAttribute( this.percepts[0] ));
		this.right = (boolean)(p.getAttribute( this.percepts[1] ));
		this.back  = (boolean)(p.getAttribute( this.percepts[2] ));
		this.left  = (boolean)(p.getAttribute( this.percepts[3] ));
		
		//Percepcion de agentes
		this.aFront = false;
		try{
			this.aFront = (boolean)(p.getAttribute( this.percepts[6] )); 
		}catch( Exception e ){}
		
		this.aRight = false;
		try{
			this.aRight = (boolean)(p.getAttribute( this.percepts[7] )); 
		}catch( Exception e ){}
		
		this.aBack = false;
		try{
			this.aBack = (boolean)(p.getAttribute( this.percepts[8] )); 
		}catch( Exception e ){}
		
		this.aLeft = false;
		try{
			this.aLeft = (boolean)(p.getAttribute( this.percepts[9] )); 
		}catch( Exception e ){}
		
		
		//percepcion de recursos si la casilla no ha sido asignada a recursos
		if(posicion.comida == Comida.NO_ASIGNADO ){
			if (!(boolean)(p.getAttribute(this.percepts[10]))) this.posicion.comida = Comida.SIN_COMIDA ; // si la casilla actual no tiene ningun recurso
			else {
				boolean resource_color = (boolean)(p.getAttribute(this.percepts[11]));
				boolean resource_shape = (boolean)(p.getAttribute(this.percepts[12]));
				boolean resource_size =  (boolean)(p.getAttribute(this.percepts[13]));
				boolean resource_weight =(boolean)(p.getAttribute(this.percepts[14]));
				this.posicion.comida = (resource_color?1:0) + (resource_shape?2:0) + (resource_size?4:0) + (resource_weight?8:0);		
			}
		}
		
		//percepcion de la energia
		this.energia_anterior = this.energia_actual;
		this.energia_actual = (int) p.getAttribute(this.percepts[15]);
		
		/*
		 ********************************************
		 *			ALGUNAS INICIALIZACIONES 		*
		 ********************************************
		 */
		
		Action accion = null;
		Coordenada next = null;
		//Agrega nuevos vecinos si la casilla es nueva
		this.casilla_nueva();
		
		/*
		 ********************************************
		 *				ALMACEN DE COMIDA			*
		 ********************************************
		 */
		
		// si no conoce la clase de comida y la casilla tiene comida
		if(this.posicion.comida > Comida.SIN_COMIDA  && !this.comida.existTipo(this.posicion.comida)) {
			if(!this.probarComida) { //hasta ahora la va a provar; 
				this.probarComida = true;
				return this.eat;
			} else {						//Ya la probo, entonces agrega un nuevo tipo y agrega 
				this.comida.addNewTipo(this.posicion.comida, this.energia_actual - this.energia_anterior);
				this.probarComida = false;
			}
		}
		
		if(this.posicion.comida > Comida.SIN_COMIDA){ //si ya la conoce agrega la coordenada al mapa de comidas
			this.comida.addNewCoord(this.posicion);
		}
		
		//Asignar el maximo de comida si la comida actual es buena
		if(this.posicion.comida > Comida.SIN_COMIDA && !this.energiaMaximaAsignada && this.comida.esBuenaComida(this.posicion.comida)){
			if(this.energia_actual > this.energia_anterior)
				return this.eat;
			else{
				this.energia_max = this.energia_actual;
				this.energiaMaximaAsignada = true;
			}
		}
		
		//si la comida mas cercana esta muy lejos armar un plan hacia la comida
		//si el camino hacia la comida consume la misma energia que tenemos en el momento
		if(this.energiaMaximaAsignada && !this.buscandoComida){
			LinkedList<Coordenada> pathComida = this.mapa.getPath(this.comida.getComidaCercana(), this.posicion);
			if( pathComida.size() > this.energia_actual -2 ) {
				this.plan = pathComida;
				this.buscandoComida = true;
			}
		}
		
		//si hay comida, tengo algo de ambre y la comida actual es buena
		if(this.posicion.comida > Comida.SIN_COMIDA && 
				this.energia_actual < this.energia_max && 
				this.comida.esBuenaComida(this.posicion.comida) ){			
			this.buscandoComida = this.energia_max == this.energia_actual?false:true;
			return this.eat;
		}
			
		
		/*
		 ********************************************
		 *			ALGORITMO DE BUSQUEDA			*
		 ********************************************
		 */
		
		int estado = 0;
		
		//Obtiene la siguiente posicion a visitar
		next = this.siguiente_pos();
		
		
		while ( accion == null )
			switch (estado) {
			
			case 0:
				//esperar que el agente vecino se mueva
				if ( this.isAgentVecino(next) ){
					this.espera += 1;
					if(this.espera > 10){
						this.casilla_ocupada(next);
						System.out.println("he esperado mucho: " + this.getClass().getSimpleName() + " - " +this.plan.isEmpty() );
					}
					accion = this.nothing;
				}else{
					this.espera = 0;
					estado = 1;
				}
				break;
			case 1:
				Coordenada aux = new Coordenada( posicion.x + dir.x,posicion.y +dir.y );
				
				if (aux.compareTo( next ) == 0 ){
					accion = this.advance;
					if (!plan.isEmpty()){
						this.posicion = this.plan.removeLast();
					}else{
						this.posicion = this.pila.eliminarSiguiente();
						this.pila.reaorganizarPila();
					}
				}else{
					this.dir.rotar();
					accion = this.rotate;
				}
				estado = -1;
				break;
			default:
				throw new Error("Error en el ciclo del Agente ", null);
			}
		
		return accion;
	}
	
	/**
	 * Que hacer si la casilla esta ocupada, verificar si esta siguiendo una 
	 * ruta planeada o si esta siguiendo la pila
	 * 
	 * @param next	pocicion que se supone ocupada
	 */
	private void casilla_ocupada(Coordenada next) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * Dice si la coordenada next contiene un agente contrario
	 * 
	 * @return
	 */
	private boolean isAgentVecino(Coordenada next) {
		Coordenada aux =null;
		// esta funcion se hizo considerando que unicamente hay un agente en el mapa
		if(this.aLeft){
			aux = new Coordenada( this.posicion.getX() - this.dir.getY(), this.posicion.getY() + this.dir.getX() );
		}
		if(this.aBack){
			aux = (new Coordenada( this.posicion.getX() - this.dir.getX() , this.posicion.getY() - this.dir.getY()) );
		}
		if(this.aRight){
			aux = (new Coordenada( this.posicion.getX() + this.dir.getY() , this.posicion.getY() - this.dir.getX() ) );
		}
		if(this.aFront){
			aux = ( new Coordenada( this.posicion.getX() + this.dir.getX() , this.posicion.getY() + this.dir.getY() ) );
		}
		
		if (aux == null){
			return false;
		}else{
			return aux.compareTo(next) == 0;
		}
	}

	/**
	 * Devuelve la siguiente posicion que deberia ocupar el agente deacuerdo a las estructuras plan y pila
	 * 
	 * @return 		La coordenada a la que debe moverse
	 */
	private Coordenada siguiente_pos() {
		//Ejecuta cuando el plan esta vacio y es un vecino
		if ( plan.isEmpty() && this.isVecino(this.pila.verSiguiente())){ 
			return pila.verSiguiente();
		}else{
			// marca un camino hacia la siguiente pocision que rrecorrera para continuar la busqueda
			if(plan.isEmpty()){
				this.plan = this.mapa.getPath(this.posicion, this.pila.eliminarSiguiente()); 
				//this.plan.removeFirst(); // remueve la pocicion que tambien esta guardada en la pila
			}
			return plan.getLast();
		}
	}
	
	/**
	 * Retorna falso o verdadero, dependiendo si el parametro c es un vecino de la pocicion actual
	 * 
	 * @param c		Posible vecino
	 * @return		true or false 
	 */
	private boolean isVecino(Coordenada c) {
		return this.vecinos().contains(c);
	}
	
	/**
	 * 
	 * Funcion que debe ser llamada solo si la casilla en la que se encuentra es una casilla nueva.
	 * No contiene verificacion de errores.
	 * 
	 * Agrega los hijos (vecinos) de la pocicion actual para realizar una busqueda en profundidad
	 */
	private void casilla_nueva() {
		ArrayList<Coordenada> vecinos = this.vecinos();
		for (Coordenada c : vecinos) {
			this.mapa.makeLink(posicion, c);
			if (!this.visitados.contains(c)){
				this.visitados.add(c);
				pila.add(c);
			}
		}
		return;
	}
	
	/**
	 * Devuelve los vecinos de la pocicion actual basado en el atributo de 
	 * posiciï¿½n del agente y las percepciones actuales
	 * 
	 * @return		Retorna un ArrayList con las coordenadas de los vecinos
	 */
	private ArrayList<Coordenada> vecinos(){
		ArrayList<Coordenada> ans =  new ArrayList<Coordenada>();
		if(!this.front){
			ans.add( new Coordenada( posicion.getX() + dir.getX() , posicion.getY() + dir.getY() ) );
		}
		if(!this.left){
			ans.add(new Coordenada( posicion.getX() - dir.getY(), posicion.getY() + dir.getX() ) );
		}
		if(!this.back){
			ans.add(new Coordenada( posicion.getX() - dir.getX() , posicion.getY() - dir.getY()) );
		}
		if(!this.right){
			ans.add(new Coordenada( posicion.getX() + dir.getY() , posicion.getY() - dir.getX() ) );
		}
		return ans;
	}
	
	/**
	 * Inicializa las variables que se usan a lo largo de la vida del Agente.
	 * Puede que sea llamado desde el ambiente.
	 */
	@Override
	public void init() {
		//System.out.println("Reiniciar agente");
		
		//inicializa la pocicion actual y el mapa
		this.posicion = new Coordenada(0, 0);
		this.dir = new Coordenada(0, 1, 0); //
		
		//inicializa las estructuras para la busqueda
		this.visitados = new TreeSet<Coordenada>();
		this.visitados.add(this.posicion);
		
		// inicializa cola para rutas hacia sitios planeados
		this.plan = new LinkedList<Coordenada>();
		this.mapa = new Mapa();
		
		//Inicializa variables de energia
		this.comida = new Comida(this);
		this.probarComida = false;
		this.energia_max = 0;
		this.energiaMaximaAsignada = false;
	}
	
}
