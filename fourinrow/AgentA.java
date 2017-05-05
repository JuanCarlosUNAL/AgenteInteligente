package unalcol.agents.examples.isi2017I.turianos.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;
import unalcol.agents.examples.isi2017I.turianos.fourinrow.MinimaxA.*;

public final class AgentA implements AgentProgram {
	
	public static int EMPTY =  0;
	public static int BLACK = 1;
	public static int WHITE = 2;
	
	public static int Deep = 5; //deep of the minimax tree
	public static int dimension;
	
	private boolean init;
	private Percept p;
	private TreeMinimax tree;
	private String color;
	
	public AgentA ( String color ){
        this.color = color;
        this.init = false;
	}
	
	@Override
	public Action compute(Percept p) {
		this.p = p;
		
		//initialization
		if(!this.init){
			AgentA.dimension = Integer.parseInt((String) this.p.getAttribute(FourInRow.SIZE));
		}
		
		//read the board
		int[] falls = new int[AgentA.dimension];
		int[][] board = new int[AgentA.dimension][AgentA.dimension];
		for(int i = 0; i <AgentA.dimension; i++) {//columns
			for(int j = AgentA.dimension - 1; j >= 0; j--){
				int hole = this.getPosition(i, j); 
				if( hole == AgentA.EMPTY ){
					falls[i] = j;
					break;
				}
				board[j][i] = hole;
			}
		}
		
		//construct tree
		this.tree = new TreeMinimax(board, true,falls, this.color == FourInRow.WHITE ? AgentA.WHITE : AgentA.BLACK);
		this.tree.generateTree();
		int j = this.tree.getBestMove();
		int i =  falls[j];
		
		return this.toAction(i, j);
	}
	
	private Action toAction(int i, int j) {
		return new Action( i+":"+j+":"+ this.color );
	}
	
	private int getPosition(int i, int j) {
		String aux = (String) this.p.getAttribute(i+":"+j);
		if( aux.equals(FourInRow.SPACE) )
			return AgentA.EMPTY;
		else if(aux.equals(FourInRow.BLACK))
			return AgentA.BLACK;
		else{
			return AgentA.WHITE;
		}
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
