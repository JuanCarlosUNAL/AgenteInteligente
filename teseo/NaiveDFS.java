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
	/* NaiveDFS:
	 * Dummy version of DFS that remembers his movements
	 * and go back when the agent cannot expand more cells,
	 * 
	 * UPDATES: Use of bitmask for all perceptions, rotate perceptions so all
	 * past choices have a common reference point, use global variables to decide
	 * future actions if choices are obvious "torotate" and "toadvance".
	 * */
    private Action rotate; 
    private Action advance;
    private Action nothing;
    private Action eat;

    private String[] percepts;


    private coordenada posicion;
    private int rotations; //Where the agent is looking? front: 0, right: 1, back: 2, left: 3
    private int torotate;
    private int toadvance;
    
    
    private Stack<Integer> pila; // Used to see past movements
    private TreeSet<coordenada> visitados;
    
    
    
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

    	// Arrived to goal?
		if( (boolean)(p.getAttribute( this.percepts[4] )) )
		    return this.nothing;
	
		// Still have to turn right from previous search (calls to compute)
		if(torotate > 0)
			return turningright();
		
		// Have to advance from previous search (calls to compute) Note: Only happens if finished rotations
		if(toadvance == 1 )
		    return frontstep();
	
		// The walls are seen like a bits 1110 -> [left][back][right][front] -> 14
		int walls = 0;
		for(int i = 0; i < 4; i++)
		    walls += (boolean)(p.getAttribute( this.percepts[i] )) ? 1<<i : 0;
	
		/* The walls could be rearrange to be seen like the agent
		 * is looking in the direction from the start 
		 */
		
		for(int i = 0; i < ((4 - rotations) % 4); i++)
			walls = (walls>>1) + ((walls & 1)<<3);
		
		// Examples with the other perceptions
		int otheragent = 0;
		for(int i = 0; i < 4; i++)
		    otheragent = (boolean)(p.getAttribute( this.percepts[i + 6] )) ? 1<<i : 0;
	
	
		boolean resource = (boolean)(p.getAttribute(this.percepts[10])) ;
		int resource_class = 0;
		if( resource )
		    for(int i = 0; i < 4; i++)
			resource_class += (boolean)(p.getAttribute(this.percepts[i+11])) ? 1<<i : 0;
		
		
		// Mark current place as visited
		coordenada current = new coordenada( posicion.getX(), posicion.getY() ); 
		if( !visitados.contains(current) ){
		    visitados.add(current);
		}
		
		/*
		 * Calculate next movement ( how many rotations are necessary?  )
		 */
		add_pila( current, walls );
		
		
		// The last method determined that there are only adjacent visited cells
		boolean neighbors_visited = toadvance == 0 && torotate == 0;
		if( neighbors_visited ){
		    if( !pila.isEmpty() ){
		    	// Return the last move ever made, without going back
				int lastmove = pila.pop();
		    
				/* Transform the past move to the current rotation,
				 * and determine how many rotations are necessary to go back
				 */
				torotate = ((4 + (((lastmove + 2) % 4) - rotations)) % 4);
				
				// Advance 1 when finish rotations
				toadvance = 1;
		    }
		}
		
		// If is necessary rotate
		if( torotate > 0 )
			return turningright();
		// If is not necessary to rotate but agent could advance right away
		if( toadvance == 1 )
			return frontstep();
		
		// After calculations the was not possible way to go back or advance
	    return this.nothing;
    }

    public void add_pila(coordenada current, int walls){
    	
		int possible_rotations;
		int minrot = 5; // Minimum number of rotations necessary
		int min_i = 0;
		coordenada pos = null;
		coordenada dir = new coordenada(0, 1);
		for(int i = 0; i < 4; i++){
		    if( ( walls & (1<<i) ) == 0 ){
		    	pos = new coordenada(posicion.getX() + dir.getX(), posicion.getY() + dir.getY());
				if( !visitados.contains(pos) ){
				    possible_rotations = ( 4 + i - rotations ) % 4;
				    if(possible_rotations < minrot){
				    	minrot = possible_rotations;
				    	min_i = i;				    	
				    }				    					    		   
				}
		    }
		    dir.rotar();
		}
			
		// All adjacent cells are visited or have walls
		if( minrot == 5 ){
			return;
		}
		
		// Use the minimum number of rotations and advance one
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
    	/* Update the current positions
    	 * using the number of current rotations
    	 * to determine the next cell right in front
    	 */
    	coordenada dir = new coordenada(0, 1);
    	for(int i = 0; i < rotations; i++)
    		dir.rotar();
    	posicion = new coordenada(posicion.getX() + dir.getX(), posicion.getY() + dir.getY());
    	return this.advance;
    }

    @Override
	public void init() {
		this.posicion = new coordenada(0, 0); 
		this.rotations = 0;
		this.visitados = new TreeSet<coordenada>();
		this.visitados.add(this.posicion);
		this.pila = new Stack<Integer>();
		this.torotate = 0;
		this.toadvance = 0;
	}
}