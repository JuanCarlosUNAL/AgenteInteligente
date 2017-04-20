/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.games.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

/**
 *
 * @author Jonatan
 */
public class DummyFourInRowAgentProgram implements AgentProgram {
    protected String color;
    protected int INF = 100000;
    public int tablero[][];
    public int heights[];
    public int turns;
    public DummyFourInRowAgentProgram( String color ){
        this.color = color;        
    }
    
	
    // Maximize-threads
    // MISSING-CASES
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

	
    /* Da valores muy bajos a tableros donde se pierde
    y valores muy altos a tableros donde se sale victorioso*/
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
	
    
    // Realiza el movimiento y devuelve el tablero con el moviento realizado
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
    
	
    // Se realiza el minimax
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
	    	if(++height[i] < size){			
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
		if(++height[i] < size){			
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
    

    //@Override
    public Action compute(Percept p) {
    	int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
    	if( tablero == null ){
    		tablero = new int[n][n];
    		for(int i = 0; i < n; i++)
    			for(int j = 0; j < n; j++)
    				tablero[i][j] = 0;
    	}
    	
    		
        /*long time = (long)(2000 * Math.random());
        try{
           Thread.sleep(time);
        }catch(Exception e){}
        */
        if( p.getAttribute(FourInRow.TURN).equals(color) ){        	
            /*int i = (int)(n*Math.random());
            int j = (int)(n*Math.random());
            boolean flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
            while( !flag ){
                i = (int)(n*Math.random());
                j = (int)(n*Math.random());
                flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
            }
            return new Action( i+":"+j+":"+color );
            */
        	if( heights == null ){
        		heights = new int[n];
        		for(int i = 0; i < n; i++)
        			heights[i] = 0;
        		//height[n/2] += 1;
        		//return new Action((n-1) + ":" + n/2 + ":" + color);
        		
        	}
        	int last = n/2;
        	for(int i = 0; i < n; i++){
        			while( heights[i] < n && !p.getAttribute( (n - heights[i] - 1) + ":" + i ).equals((String)FourInRow.SPACE) ){
        				tablero[n-heights[i] - 1][i] = 2;
        				heights[i]++;
        				last = i;
        			}        		
        	}
        	
        	// Se prefiere la posicion media
		/*
		Heuristica conocida en el juego		
		*/
        	if(turns++ < 4){
        		if(heights[n/2] == 0){
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
        	/*for(int i = 0; i < n; i++){
        		v = alphabeta(tablero, 4, INF, -INF, false, n, heights, -1);
        		//System.out.println(v);
        		if( v > max ){
        			max = v;
        			max_i = i;        			
        		}        		
        	}*/
		
		/* La profundida se adapte a partir del tamaño, con un tamaño muy alto solo se revisa
		que el movimiento no lo haga perder, o lo haga ganar de inmediato*/
    		int depth = 6;
		if(n >= 11)
			depth = 5;
    		if(n >= 13)
    			depth = 3;
    		if(n >= 15)
    			depth = 0;
		
		// Llamar el minimax
        	for(int i = 0; i < n; i++)
    	    	if(++heights[i] < n){			
    	    		v = alphabeta(move(tablero,n, heights, i, true), depth, -INF, +INF, false, n, heights, i );
    	    		heights[i]--;
			// Se prefieren lugares bajos y a la derecha, la derecha es indeseable, es mejor centrado
			// LOWER-BETTER
    	    		if( v >= max && heights[i] < 3 ){
    	    			max = v;
    	    			max_i = i;    	    			
    	    		}else if( v >  max){
    	    			max = v;
    	    			max_i = i;
    	    			
    	    		}
    	    	}else --heights[i];
        	
		// En caso de que todo movimiento lo haga perder se pasa.
		// LOSS-GUARANTEED
        	if( max_i < 0 )
        		return new Action(FourInRow.PASS);
        	tablero[n - heights[max_i] - 1][max_i] = 1;
        	/*System.out.println(color);
        	for(int i = 0; i < n; i++){
        		for(int j = 0; j < n; j++)
        			System.out.print(tablero[i][j] + " ");
        		System.out.println();
        		
        	}*/
        	return new Action((n - heights[max_i]++ - 1) + ":" + max_i + ":"  + color );
        }
        return new Action(FourInRow.PASS);
    }

    //@Override
    public void init() {
    	tablero = null;
    	heights = null;
    	turns = 0;
    }
    
}
