package unalcol.agents.examples.isi2017I.turianos.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;
import unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax2.TreeMinimax;

public final class TurianosFIR implements AgentProgram{
	
	public static int EMPTY =  0;
	public static int BLACK = 1;
	public static int WHITE = 2;
	
	public static int Deep = 5; //deep of the minimax tree
	public static int dimension;
	
	private boolean init;
	private Percept p;
	private TreeMinimax tree;
	
	protected int[] falls; 
	protected String color;
	private boolean BlackFlag;
	
	 public TurianosFIR ( String color ){
	        this.color = color;
	        this.init = false;
	        this.BlackFlag = false;
	}
	
	@Override
	public Action compute(Percept p) {
		this.p = p;
		
		if( this.p.getAttribute(FourInRow.TURN).equals(color)){ //if it is my turn
			if(!this.init) commonInit(); //initializing some common variables;
			
			if(this.color.equals(FourInRow.WHITE)){ //if I am white
				if(!this.init) {
					int pos = (int) (Math.random() * (TurianosFIR.dimension));
					this.initWhite(pos);
					this.init = true;
					return this.toAction(dimension-1,pos); // first white's move
				}
				return this.getMoveWhite();
			}else{	//if I am black
				if(!this.init) {
					this.initBlack();
					this.init = true;
				}
				return this.getMoveBlack();
			}
		}else{
			return new Action(FourInRow.PASS);
		}
	}

	private Action getMoveBlack() {
		
		int j,i;
		if (this.BlackFlag){
			for( j = 0; j < TurianosFIR.dimension; j++){
				if(this.falls[j] < 0)continue;
				if(this.getPosition(this.falls[j], j) != TurianosFIR.EMPTY)
					break;
			}
			
			//Control error API
			if(j < TurianosFIR.dimension){
				this.falls[j]--;
				this.tree.Update(j,TurianosFIR.WHITE);
			}else{
				return TurianosFIR.PASS(this);
			}
		}
		
		this.BlackFlag = true;
		
		//calculate scores
		this.tree.generateTree();
		j = this.tree.getBestMove();
		
		//update tree
		i = this.falls[j];
		this.falls[j]--;
		this.tree.Update(j,TurianosFIR.BLACK);
		
		return toAction(i, j);
	}

	private void initBlack() {
		int i, j;
		int[][] board = new int[TurianosFIR.dimension][TurianosFIR.dimension];
		//initial board
		for(i = 0; i < TurianosFIR.dimension; i++){
			for(j = 0; j < TurianosFIR.dimension; j++){
				board[i][j] = TurianosFIR.EMPTY;
			}
		}
		
		//get the enemy move, create tree
		for(j = 0; j < TurianosFIR.dimension; j++){
			if (this.getPosition( this.falls[j], j) != TurianosFIR.EMPTY) break;
		}
		i = this.falls[j];
		this.falls[j]--;
		
		board[i][j] = TurianosFIR.WHITE;
		
		this.tree = new TreeMinimax(board, true, this.falls.clone(), TurianosFIR.BLACK);
	}

	private Action getMoveWhite() {
		int j,i=0;
		for( j = 0; j < TurianosFIR.dimension; j++){
			if(this.falls[j] < 0) continue;
			if(this.getPosition(this.falls[j], j) != TurianosFIR.EMPTY)
				break;
		}
		
		//Control error API
		if(j < TurianosFIR.dimension){
			this.falls[j]--;
			this.tree.Update(j,TurianosFIR.BLACK);
		}else{
			return TurianosFIR.PASS(this);
		}
		
		//calculate scores
		this.tree.generateTree();
		j = this.tree.getBestMove();
		
		//update tree
		i = this.falls[j];
		this.falls[j]--;
		this.tree.Update(j,TurianosFIR.WHITE);
		
		return toAction(i, j);
	}

	private void initWhite(int j) {
		int i;
		int[][] board = new int[TurianosFIR.dimension][TurianosFIR.dimension];
		
		//initial board
		for(i = 0; i < TurianosFIR.dimension; i++){
			for(int k = 0; k < TurianosFIR.dimension; k++){
				board[i][k] = TurianosFIR.EMPTY;
			}
		}
		
		i = this.falls[j];
		this.falls[j]--;
		
		board[i][j] = TurianosFIR.WHITE;
		
		this.tree = new TreeMinimax(board, false, this.falls.clone(), TurianosFIR.WHITE);
	}

	private void commonInit() {
		TurianosFIR.dimension = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
		this.falls = new int[TurianosFIR.dimension];
		for (int i = 0; i < TurianosFIR.dimension; i++) this.falls[i] = TurianosFIR.dimension-1;
	}

	private Action toAction(int i, int j) {
		return new Action( i+":"+j+":"+ this.color );
	}
	
	private int getPosition(int i, int j) {
		String aux = (String) this.p.getAttribute(i+":"+j);
		if( aux.equals(FourInRow.SPACE) )
			return FourinrowProgramTuirianos.EMPTY;
		else if(aux.equals(FourInRow.BLACK))
			return FourinrowProgramTuirianos.BLACK;
		else{
			return FourinrowProgramTuirianos.WHITE;
		}
	}

	@Override
	public void init() {
		return;
	}
	
	private static Action PASS(TurianosFIR a) {
		for (int i = 0; i < TurianosFIR.dimension; i++) {
			if (a.falls[i] != 7 )
				return a.toAction(a.falls[i]-1,i );
		}
		return null;
	}
}
