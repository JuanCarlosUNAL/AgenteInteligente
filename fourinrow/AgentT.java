/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.isi2017I.turianos.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

/**
 *
 * @author Jonatan
 */
public class AgentT implements AgentProgram {
    
	protected String color;
    protected int INF = 100000;
    public int tablero[][];
    public int heights[];
    public int turns;
    
    public AgentT( String color ){
    	tablero = null;
    	heights = null;
    	turns = 0;
        this.color = color;
        System.out.println("Agente David creado");
    }
    
    public int heuristic(int tab[][],int size, int height[]){
	// Count number of threads
	int x, y;
	int threads_pos = 0;
	int threads_neg = 0;
	for(int i = 0; i < size; i++){
		if( height[i] == size )
			continue;
		x = size - height[i] - 1;	
	    y = i;
	    // x, y -> posicion de vacio valido
	    // Derecha
	    threads_pos += (y + 3 < size && (tab[x][y+1] == 1 && tab[x][y+2] == 1 && tab[x][y+3] == 1)) ? 1 : 0;
	    // Izquierda
	    threads_pos += (y - 3 >= 0 && (tab[x][y-1] == 1 && tab[x][y-2] == 1 && tab[x][y-3] == 1)) ? 1 : 0;
	    // Diag + der
	    threads_pos += (x + 3 < size && y + 3 < size && (tab[x+1][y+1] == 1 && tab[x+2][y+2] == 1 && tab[x+3][y+3] == 1)) ? 1 : 0;
	    // Diag + izq

	    // Diag - der
	    threads_pos += (x + 3 < size && y - 3 >= 0 && (tab[x+1][y-1] == 1 && tab[x+2][y-2] == 1 && tab[x+3][y-3] == 1)) ? 1 : 0;
	    // Diag - izq


	    // THREADS_NEG
	    threads_neg += (y + 3 < size && (tab[x][y+1] == 2 && tab[x][y+2] == 2 && tab[x][y+3] == 2)) ? 2: 0;
	    // Izquierda
	    threads_neg += (y - 3 >= 0 && (tab[x][y-1] == 2 && tab[x][y-2] == 2 && tab[x][y-3] == 2)) ? 2: 0;
	    // Diag + der
	    threads_neg += (x + 3 < size && y + 3 < size && (tab[x+1][y+1] == 2 && tab[x+2][y+2] == 2 && tab[x+3][y+3] == 2)) ? 2: 0;
	    // Diag + izq

	    // Diag - der
	    threads_neg += (x + 3 < size && y - 3 >= 0 && (tab[x+1][y-1] == 2 && tab[x+2][y-2] == 2 && tab[x+3][y-3] == 2)) ? 2 : 0;
	    // Diag - izq	
	}
	return threads_pos - threads_neg;
    }

    public int terminal(int tab[][], int size, int height[],  int b_move){
	// Return true if someone win or draw
	int x, y;
	x = size - height[b_move];
	y = b_move;
	int color = tab[x][y];
	// Revisar fila
	int count = 0;
	for(int j = 0; j < size; j++)
	    if( tab[x][j] == color ){
		count++;
		if( count == 4 && color == 1) return 1;
		if( count == 4 && color == 2) return 2;
	    }else
		count = 0;
	// Revisar columna
	count = 0;
	for(int i = 0; i < size; i++){
	    if( tab[i][y] == color ){
		count++;
		if( count == 4 && color == 1) return 1;
		if( count == 4 && color == 2) return 2;
	    }else
		count = 0;
	}
	
	// Revisar diagonal +
	count = 0;
	int dis = 0;
	while( x - dis > 0 && y - dis > 0 )
	    dis++;
	int x1 = x - dis;
	int y1 = y - dis;
	for(int i = 0; x1 + i < size && y1 + i < size; i++)
	    if( tab[x1+i][y1+i] == color ){
		count++;
		if( count == 4 && color == 1) return 1;
		if( count == 4 && color == 2) return 2;
	    }else
		count = 0;


	//Revisar diagonal -
	count = 0;
	dis = 0;
	while( x - dis > 0 && y + dis < size - 1 )
	    dis++;
	x1 = x - dis;
	y1 = y + dis;
	//System.out.println(y1);
	for(int i = 0; x1 + i < size && y1 - i >= 0 ; i++)
	    if( tab[x1+i][y1-i] == color ){
		count++;
		if( count == 4 && color == 1) return 1;
		if( count == 4 && color == 2) return 2;
	    }else
		count = 0;

	boolean flag = true;
	for(int i = 1; i < size; i++)
	    if( height[i-1] != height[i] ){
		flag = false;
		break;
	    }
	if( flag )
	    return 3;
	return 0;
    }
	
    public int[][] move(int tab[][], int size, int height[],  int move_i, boolean me){
    	int[][] tab2 = new int[size][size];
		for(int i = 0; i < size; i++)
		    for(int j = 0; j < size; j++)
			tab2[i][j] = tab[i][j];
		int h = height[move_i];
		int x = size - h; // h = size
		if( me ) tab2[x][move_i] = 1;
		else tab2[x][move_i] = 2;
		return tab2;
    }
    
    public int alphabeta(int tab[][], int depth, int alpha, int beta, boolean maximizing, int size, int height[], int b_move){
	int v, value, t;
	if( height[b_move] == 0 )
	    t = 0;
	else{					
		t = terminal(tab, size,height, b_move);
	}
	if( depth == 0 || t > 0 )
	    if( depth != 0 ){
		if( t == 1 ) return INF - 1;
		if( t == 2 ) return -INF + 1;
		if( t == 3 ) return 0;
	    }else
	    	return heuristic(tab,size,height);
	if( maximizing ){
	    v = -INF;
	    for(int i = 0; i < size; i++)
	    	if(++height[i] <= size){			
	    		value = alphabeta(move(tab,size, height, i, true), depth - 1, alpha, beta, false, size, height, i );
	    		height[i]--;
	    		v = v > value ? v : value;
	    		alpha = alpha > v ? alpha : v;
	    		if( beta <= alpha )
	    			break;		    
	    	}else height[i]--;	    
	    return v;
	}else{
	    v = INF;
	    for(int i = 0; i < size; i++)
		if(++height[i] <= size){			
		    value = alphabeta(move(tab,size, height, i, false), depth - 1, alpha, beta, true,size, height,  i);
		    height[i]--;
		    v = v < value ? v : value;
		    beta = beta < v ? beta : v;
		    if( beta <= alpha )
			break;
		}else --height[i];
	    return v;
	}
    }
    
    @Override
    public Action compute(Percept p) {
    	int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
    	if( tablero == null ){
    		tablero = new int[n][n];
    		for(int i = 0; i < n; i++)
    			for(int j = 0; j < n; j++)
    				tablero[i][j] = 0;
    	}
    	
        if( p.getAttribute(FourInRow.TURN).equals(color) ){        	
            
        	if( this.heights == null ){
        		this.heights = new int[n];
        		for(int i = 0; i < n; i++)
        			heights[i] = 0;        		
        	}        	
        	
        	
        	if(this.turns++ < 4){
        		if(this.heights[n/2] == 0){
        			tablero[n-1][n/2] = 1;
        			heights[n/2]++;
        			return new Action((n-1) + ":" + n/2 + ":"  + color );
        		}
        		if(heights[n/2 - 1] == 0){
        			tablero[n-1][n/2] = 1;
        			heights[n/2]++;
        			return new Action((n-1) + ":" + (n/2 - 1) + ":"  + color );
        		}
        	}
        	
        	int v;
    		int max = -INF; 
    		int max_i = -1;
        	
    		int depth = 6;


        	for(int i = 0; i < n; i++)
    	    	if(++heights[i] <= n){			
    	    		v = alphabeta(move(tablero,n, heights, i, true), depth, -INF, +INF, false, n, heights, i );
    	    		heights[i]--;
    	    		if( v >= max && heights[i] < 3 ){
    	    			max = v;
    	    			max_i = i;    	    			
    	    		}else if( v >  max){
    	    			max = v;
    	    			max_i = i;
    	    			
    	    		}
    	    	}else --heights[i];
        	
        	if( max_i < 0 )
        		return new Action(FourInRow.PASS);
        	tablero[n - heights[max_i] - 1][max_i] = 1;
        	
        	return new Action((n - heights[max_i]++ - 1) + ":" + max_i + ":"  + color );
        }
        return new Action(FourInRow.PASS);
    }

    //@Override
    public void init() {
    	
    }
    
}