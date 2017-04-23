package unalcol.agents.examples.isi2017I.turianos.teseo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;


public class NaiveDFS implements AgentProgram {

    private Action rotate;
    private Action advance;
    private Action eat;
    private Action nothing;

    private String[] percepts;


    // Grafo del laberinto y posicion
    private Mapa map;
    private coordenada posicion;
    private int rotations;
    private int torotate;
    private int toadvance;
    private boolean backing;
    
    // Stack para el DFS
    private Stack<Integer> pila;
    private TreeSet<coordenada> visitados;
    private Stack<coordenada> previsitados;
    
    // Queue?
    private LinkedList<coordenada> plan;
    
    
    public NaiveDFS( SimpleLanguage lang ){
		this.eat = new Action( lang.getAction(4) );
		this.rotate = new Action( lang.getAction(3) );
		this.advance = new Action( lang.getAction(2) );
		this.nothing = new Action( lang.getAction(0) );
	
	
		int size_percepts = lang.getPerceptsNumber();
		String[] lang_percepts = new String[size_percepts];
		for(int i = 0; i < size_percepts; i++)
		    lang_percepts[i] = lang.getPercept(i);
		this.percepts = lang_percepts;
	
		this.init();
    }


    @Override
    public Action compute(Percept p){

		if( (boolean)(p.getAttribute( this.percepts[4] )) )
		    return this.nothing;
	
		if(torotate > 0)
			return turningright();
		
	
		if(toadvance == 1 )
		    return frontstep();
		
		    
		
		
		// Walls alrededor
		int walls = 0;
		for(int i = 0; i < 4; i++)
		    walls += (boolean)(p.getAttribute( this.percepts[i] )) ? 1<<i : 0;
	
		//System.out.println(walls);
		
		for(int i = 0; i < ((4 - rotations) % 4); i++)
			walls = (walls>>1) + ((walls & 1)<<3);
		
		// Other Agent
		int otheragent = 0;
		for(int i = 0; i < 4; i++)
		    otheragent = (boolean)(p.getAttribute( this.percepts[i + 6] )) ? 1<<i : 0;
	
	
		boolean resource = (boolean)(p.getAttribute(this.percepts[10])) ;
		int resource_class = 0;
		if( resource )
		    for(int i = 0; i < 4; i++)
			resource_class += (boolean)(p.getAttribute(this.percepts[i+11])) ? 1<<i : 0;
	
		Action action = null;
		coordenada next = null;
		
		//System.out.println(walls);
		
		coordenada current = new coordenada( posicion.getX(), posicion.getY() ); // Copia de posicion
		if( !visitados.contains(current) ){
		    visitados.add(current);
		}
		add_pila( current, walls );
		
		
		// Todos los posibles caminos fueron visitados
		// Devolverse
		
		
		if( toadvance == 0 && torotate == 0 ){
		    if( !pila.isEmpty() ){

				int lastmove = pila.pop();
				torotate = ((4 + (((lastmove + 2) % 4) - rotations)) % 4);		
				toadvance = 1;
		    }
		}
		if( torotate > 0 )
			return turningright();
		if( toadvance == 1 )
			return frontstep();
	     return this.nothing;
    }

    public void add_pila(coordenada current, int walls){
		int posrot;
		int minrot = 5;
	    int x = 0;
		int y = 1;
		int c;
		int min_i = 0;
		coordenada pos = null;
		coordenada next = null;
		coordenada dir = new coordenada(0, 1);
		// Han sido completamente visitados(todas las posibles expansiones han sido exploradas)
		// Han sido previsitados (el camino de donde viene el agente)
		for(int i = 0; i < 4; i++){
		    if( ( walls & (1<<i) ) == 0 ){
		    	pos = new coordenada(posicion.getX() + dir.getX(), posicion.getY() + dir.getY());
				if( !visitados.contains(pos) ){
				    posrot = ( 4 + i - rotations ) % 4;
				    if(posrot < minrot){
				    	minrot = posrot;
				    	min_i = i;				    	
				    }				    					    		   
				}
		    }
		    dir.rotar();
		}
			
		//System.out.println(visitados);

		if( minrot == 5 ){
			return;
		}
		
		torotate = minrot;		    
		pila.add(min_i);
		toadvance = 1;
    }
    
    
    public Action turningright(){
    	rotations = (rotations + 1) % 4; 
    	torotate--;
    	return this.rotate;
    }
    
    public Action frontstep(){
    	toadvance = 0;
    	coordenada dir = new coordenada(0, 1);
    	for(int i = 0; i < rotations; i++)
    		dir.rotar();
    	posicion = new coordenada(posicion.getX() + dir.getX(), posicion.getY() + dir.getY());
    	return this.advance;
    }

    @Override
	public void init() {
		//System.out.println("Reiniciar agente");
		//inicializa la pocicion actual y el mapa
		this.posicion = new coordenada(0, 0);
		//this.dir = new coordenada(0, 1, 0); // 
		this.map = new Mapa();
		this.rotations = 0;
		this.backing = false;
		//inicializa las estructuras para el DFS
		this.visitados = new TreeSet<coordenada>();
		this.visitados.add(this.posicion);
		this.pila = new Stack<Integer>();
		this.torotate = 0;
		this.toadvance = 0;
		
		// inicializa cola para rutas hacia sitios planeados
		this.plan = new LinkedList<coordenada>();
		
		//Inicializa variables de energia
		//this.comida = new TreeMap<Integer, Boolean>();
		//this.energia_actual = 0;
		//this.energia_max= 1;
	}
}