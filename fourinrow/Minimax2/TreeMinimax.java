package unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax2;

import java.util.ArrayList;
import java.util.Collections;

import unalcol.agents.examples.isi2017I.turianos.fourinrow.TurianosFIR;

public final class TreeMinimax {
	
	private BoardNode root;
	private int turn;
	private int enemy;
	
	public TreeMinimax (int[][] board, boolean max, int[] falls, int turn){
		
		this.enemy = turn == TurianosFIR.WHITE?TurianosFIR.BLACK:TurianosFIR.WHITE;
		this.turn = turn;
		
		this.root = new BoardNode();
		this.root.board = board;
		this.root.deep = 0;
		this.root.max = max;
		this.root.falls = falls;
		this.root.sheet = true;
		this.root.score =  0;
		this.root.children = null;
	}
	
	public void generateTree(){
		this.root.isFourInRow = false;
		this.generateTree(this.root,0);
	}
	
	private void generateTree(BoardNode n, int pruneVariable) {
		if(n.isFourInRow || isFourInRow(n.board, n.max?turn:enemy) ){
			n.score = !n.max?Integer.MAX_VALUE:Integer.MIN_VALUE;
			n.isFourInRow = true;
			n.sheet = true;
			return;
		}else if (n.deep - this.root.deep >= TurianosFIR.Deep ){
			n.score = calcScore(n);
			n.sheet = true;
			return;
		}else{
			n.score = 0;
		}
		
		n.children = new ArrayList<BoardNode>();
		for(int j = 0; j < TurianosFIR.dimension; j++){
			if(n.falls[j] < 0) continue;			
			int[][] aux = TreeMinimax.cloneBoard(n.board);
			aux[n.falls[j]] [j] = n.max?this.turn:this.enemy;
			
			BoardNode child = new BoardNode();
			child.board = aux;
			child.max = !n.max;
			child.deep = n.deep +1;
			child.falls = TreeMinimax.cloneFalls(n.falls);
			child.falls[j]--;
			generateTree(child,n.score);
			
			if(n.max) n.score = child.score >= n.score?child.score:n.score;
			else n.score = child.score <= n.score?child.score:n.score;
			
			n.children.add(child);
			
			//prune tree
			if(n.max && child.score > pruneVariable ||
					!n.max && child.score < pruneVariable) break;
			
		}
		
		Collections.shuffle(n.children);
		
	}

	private int calcScore(BoardNode n) {
		int[][] board = n.board;
		
		boolean[] directions = new boolean[4]; //directions
		int[] maxLine =  new int[4]; //max line in that direction
		
		int sum = 0; 
		for (int i = board.length -1; i >= 0; i--){
			for (int j = board.length - 1; j >= 0; j--) {
				int marker = board[i][j]; // the current piece in the i,j board indices
				if (marker == TurianosFIR.EMPTY ) continue; // if this piece is not of this turn 
				
				for(int k = 0; k < directions.length ; k++) directions[k] = true; //reset directions
								
				for (int k = 0; k < 4; k++) { // search for lines in clockwise
					
					if (directions[0]){ // left
						directions[0] = (j-k >= 0) && (board[i][j-k] == marker);
						maxLine[0]++;
					}
					if (directions[1]){ //up-left
						directions[1] = (j-k >= 0) && (i-k >= 0) &&  (board[i-k][j-k] == marker);
						maxLine[1]++;
					}
					if (directions[2]){ //up
						directions[2] = (i-k >= 0) &&  (board[i-k][j] == marker);
						maxLine[2]++;
					}
					if (directions[3]){ //up-right
						directions[3] = (j+k < board.length ) && (i-k >= 0) && (board[i-k][j+k] == marker);
						maxLine[3]++;
					}
				}
				
				for (int k = 1; k < maxLine.length; k++)
					maxLine[0] = Integer.max(maxLine[0], maxLine[k]);
				
				sum +=(int)( marker == this.turn?Math.pow (10, maxLine[0]):-Math.pow (10, maxLine[0]));
			}
		}
		return sum;
	}
	
	private static boolean hasFourInRow (BoardNode n,int turn){
		int[][] board = TreeMinimax.cloneBoard(n.board);
		for(int i = 0; i < TurianosFIR.dimension; i++){
			if(n.falls[i] < 0) continue;
			board[n.falls[i]][i] = turn;
			if(isFourInRow(board, turn)) return true;
			board[n.falls[i]][i] = TurianosFIR.EMPTY;
		}
		return false;
	}
	
	private static boolean isFourInRow(int[][] board, int turn) {  
		
		boolean[] directions = new boolean[4]; //directions
		
		for (int i = board.length -1; i >= 0; i--){
			for (int j = board.length - 1; j >= 0; j--) {
				int marker = board[i][j]; // the current piece in the i,j board indices
				if (marker != turn) continue; // if this piece is not of this turn 
				
				for(int k = 0; k < directions.length ; k++) directions[k] = true; //reset directions
				
				for (int k = 0; k < 4; k++) { // search for lines in clockwise
					
					if (directions[0]){ // left
						directions[0] = (j-k >= 0) && (board[i][j-k] == marker);
					}
					if (directions[1]){ //up-left
						directions[1] = (j-k >= 0) && (i-k >= 0) &&  (board[i-k][j-k] == marker);
					}
					if (directions[2]){ //up
						directions[2] = (i-k >= 0) &&  (board[i-k][j] == marker);
					}
					if (directions[3]){ //up-right
						directions[3] = (j+k < board.length ) && (i-k >= 0) && (board[i-k][j+k] == marker);
					}
				}
				// unify possible directions 
				for(int k = 1; k < directions.length; k++) {
					directions[0] = directions[0] || directions[k];
				}
				
				if (directions[0]) 
					return true;
			}
		}
		return false;
	}

	private static int[] cloneFalls(int[] falls) {
		int [] ans = new int[TurianosFIR.dimension];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = falls[i];
		}
		return ans;
	}

	private static int[][] cloneBoard(int[][] board) {
		int[][] ans = new int[board.length][board.length];
		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans.length; j++) {
				ans[i][j] = board[i][j];
			}
		}
		return ans;
	}

	public int getBestMove(){
		
		BoardNode best=null;
		
		//worst = this.root.children.get( (int)Math.random() * this.root.children.size() );
		this.generateTree();
		for (int i = 0; i < this.root.children.size(); i++ ){
			if (this.root.children.get(i).score != Integer.MAX_VALUE)
			if( TreeMinimax.hasFourInRow(this.root.children.get(i), this.enemy ) && this.root.children.size() > 1 ){
				this.root.children.remove(i);
				i--;
			}
		}
		
		best = this.root.children.get( 0 );
		
		for (int i = 1; i < this.root.children.size(); i++ ){
			if( this.root.children.get(i).score > best.score )
				best = this.root.children.get(i);
		}
		
		for(int j = 0; j < TurianosFIR.dimension; j++){
			if( this.root.falls[j] != best.falls[j] )
				return j;
		}
		return -1;
	}

	public void Update(int j, int turn) {
		if(this.root.children == null) this.generateTree();
		for(BoardNode n : this.root.children){
			if(n.children == null) this.generateTree();
			if (n.board[this.root.falls[j]][j] == turn){
				this.root = n;
				return;
			}
		}
		//throw new Error("El turno no se encontro en el arbol");
	}
	
	public String toString() {
		return this.root.toString();
	}
}
