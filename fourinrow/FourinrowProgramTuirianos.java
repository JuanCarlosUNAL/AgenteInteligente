package unalcol.agents.examples.isi2017I.turianos.fourinrow;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;
import unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax1.MiniMaxTree;
import unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax1.Node;

public class FourinrowProgramTuirianos implements AgentProgram {
	
	public static int EMPTY =  0;
	public static int BLACK = 1;
	public static int WHITE = 2;
	
	public static int maxDeep = 8; //deep of the minimax tree
	public static int minDeep = 5; //deep of the minimax tree
	
	public boolean Playing = false;
	
	private boolean init;
	private Percept p;
	private MiniMaxTree tree;
	private boolean blackFlag;
	
	protected int[] falls; 
	protected int dimension;
	protected String color;
	
    public FourinrowProgramTuirianos ( String color ){
        this.color = color;
        this.init = false;
    }

	@Override
	public Action compute(Percept p) {
		this.p = p;
		
		if( this.p.getAttribute(FourInRow.TURN).equals(color)){ //if it is my turn
			if(!this.init) commonInit(); //initializing some common variables
			
			if(this.color.equals(FourInRow.WHITE)){ //if I am white
				if(!this.init) {
					int pos = (int) (Math.random() * (this.dimension));
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
		
		if (this.blackFlag){
			this.UpdateFallsAndTree();
		}else{
			this.blackFlag = true;
		}
		
		this.commonMove();
		
		Node next = this.tree.getNextBestMove();
		//get coordinates of the next node
		int j;
		for( j = 0; j < this.dimension; j++){
			if(this.falls[j] < 0) continue;
			if( next.getBoard()[this.falls[j]][j] != FourinrowProgramTuirianos.EMPTY )
				break;
		}
		int i = this.falls[j];
		this.falls[j]--;
		
		this.tree.reasignRoot(next);
		
		return this.toAction(i, j);
	}

	private Action getMoveWhite() {
		synchronized (this.tree) {
			if(this.tree.partialDeep < FourinrowProgramTuirianos.maxDeep){
				try { this.tree.wait();
				} catch (InterruptedException e) {}
			}
		}
		this.UpdateFallsAndTree();
		this.commonMove();
		
		Node next = this.tree.getNextBestMove();
		//get coordinates of the next node
		int j;
		for( j = 0; j < this.dimension; j++){
			if(this.falls[j] < 0) continue;
			if( next.getBoard()[this.falls[j]][j] != FourinrowProgramTuirianos.EMPTY )
				break;
		}
		int i = this.falls[j];
		this.falls[j]--;
		
		this.tree.reasignRoot(next);
		
		return toAction(i, j);
	}

	private void commonMove() {
		synchronized (this.tree) {
			while(this.tree.partialDeep < FourinrowProgramTuirianos.minDeep){
				try {this.tree.wait();
				} catch (InterruptedException e) {}
			}
			this.Playing = true;
			this.tree.calcScores();
		}
	}

	private void commonInit() {
		this.dimension = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
		this.falls = new int[this.dimension];
		for (int i = 0; i < this.dimension; i++) this.falls[i] = this.dimension-1;
	}
	
	private void initBlack() {

		this.blackFlag = false;
		
		//search for white move
		int i ;
		for( i= 0; i < this.dimension; i ++){
			if(this.falls[i] < 0) continue;
			if ( this.getPosition(this.falls[i],i) == FourinrowProgramTuirianos.WHITE)
				break;
			else{
				System.out.println(this.falls[i] + "- " + i);
			}
		}
		this.falls[i]--;
		int[] aux = new int[(int) Math.pow(this.dimension, 2)];
		aux[aux.length -this.dimension + i] = FourinrowProgramTuirianos.WHITE;
		Node n = new Node(aux,false,0);
		this.tree = new MiniMaxTree(n, FourinrowProgramTuirianos.WHITE,this);
		
		// construct some part of the tree
		this.tree.start();		
	}

	private void initWhite(int pos) {
		commonInit();
		this.falls[pos]--;
		
		//initializing root node of tree with the first move
		int[] aux = new int[(int)Math.pow(this.dimension, 2)];
		for (int i = 0; i < aux.length; i++)  aux[i] = FourinrowProgramTuirianos.EMPTY;
		int cas = aux.length - this.dimension + pos;
		aux[cas] = FourinrowProgramTuirianos.WHITE;
		Node n = new Node(aux,true,0);
		this.tree = new MiniMaxTree(n, FourinrowProgramTuirianos.WHITE,this);
		this.tree.start();
	}
	
	private void UpdateFallsAndTree() {
		int j ;
		for(j = 0; j < this.dimension; j++){
			if(this.falls[j] < 0) continue;
			if(getPosition(this.falls[j], j) != FourinrowProgramTuirianos.EMPTY)
				break;
		}
		
		if (j >= this.dimension) return;
		
		int i = this.falls[j];
		this.falls[j]--;
		this.tree.reasignRoot(i,j);
		
	}
	
	private Action toAction(int i, int j) {
		this.Playing = false;
		synchronized (this) {
			this.notify();			
		}
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


	}
	

}
