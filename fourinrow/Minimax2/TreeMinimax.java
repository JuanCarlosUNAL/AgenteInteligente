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
		if(  n.isFourInRow || isFourInRow(n, n.max?this.enemy:this.turn) ){
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
		
		if ( n.children == null )
			n.children = new ArrayList<BoardNode>();
		
		for(int j = 0; j < TurianosFIR.dimension; j++){
			if(n.falls[j] < 0) continue;
			
			BoardNode child = new BoardNode();
			child.max = !n.max;
			child.falls = TreeMinimax.cloneFalls(n.falls);
			child.falls[j]--;
			
			int child_index = n.children.indexOf(child);
			if(child_index != -1){ 	// if child already exist
				generateTree(n.children.get(child_index),n.score);
				child = n.children.get(child_index);
			}else{
				child.board = TreeMinimax.cloneBoard(n.board);
				child.board[n.falls[j]] [j] = n.max?this.turn:this.enemy;
				child.deep = n.deep + 1;
				generateTree(child,n.score);
			}
			
			if(n.max) n.score = child.score >= n.score?child.score:n.score;
			else n.score = child.score <= n.score?child.score:n.score;
			
			if(child_index == -1)
				n.children.add(child);
			
			//prune tree
			if(n.max && child.score > pruneVariable ||
					!n.max && child.score < pruneVariable) break;
			
		}
		
		Collections.shuffle(n.children);
		
	}

	private int calcScore(BoardNode n) {
		int sum = -1;
		
		int[][] board = n.board;
		int marker =  n.max?this.enemy:this.turn;
		
		boolean[] directions = new boolean[4]; //directions
		int[] len_line = new int[4];	//length of lines   
		
		for(int k = 0; k < TurianosFIR.dimension; k++){
			if(n.falls[k] < 0) continue;
			int[][] aux_board = TreeMinimax.cloneBoard(board);
			
			int i = n.falls[k], j = k;
			aux_board[ i ][ j ] = marker;
			
			for(int m = 0; m < directions.length ; m++) directions[m] = true; //reset directions
			for(int m = 0; m < directions.length ; m++) len_line[m] = -1; //reset len line
			
			//walk on the board to find score
			for (int m = 0; m < 4; m++) {
				if (directions[0]){ // right
					directions[0] = (j+m < TurianosFIR.dimension ) && ( board[i][j+m] == marker);
					len_line[0]++;
				}
				if (directions[1]){ //down-left
					directions[1] = (j-m > 0) && (i+m < TurianosFIR.dimension ) && ( board[i+m][j-m] == marker);
					len_line[1]++;
				}
				if (directions[2]){ //dawn
					directions[2] = (i+m < TurianosFIR.dimension ) && (board[i+m][j] == marker);
					len_line[2]++;
				}
				if (directions[3]){ //down-right
					directions[3] = (j+m < TurianosFIR.dimension ) && (i+m < TurianosFIR.dimension) && (board[i+m][j+m] == marker);
					len_line[3]++;
				}
			}
			for(int m = 0; m < 4; m++){
				sum = ( sum < len_line[m] )? sum : len_line[m];  
			}
		}
		
		return (int) Math.pow(10, sum) * (n.max?-1:1) ;
	}
	
	private static boolean hasFourInRow (BoardNode n,int turn){
		BoardNode aux_n = new BoardNode();
		aux_n.board = TreeMinimax.cloneBoard(n.board);
		aux_n.falls = TreeMinimax.cloneFalls(n.falls);
		
		for(int i = 0; i < TurianosFIR.dimension; i++){
			if(n.falls[i] < 0) continue;
			aux_n.board[n.falls[i]][i] = turn;
			aux_n.falls[i]--;
			if(isFourInRow(aux_n, turn)) return true;
			aux_n.board[n.falls[i]][i] = TurianosFIR.EMPTY;
			aux_n.falls[i]++;
		}
		return false;
	}
	
	private static boolean isFourInRow(BoardNode n, int turn) {  
		
		int[][] board = n.board;
		boolean [] directions = new boolean[4];	
		
		for(int k = 0; k < TurianosFIR.dimension; k++){
			if(n.falls[k] + 1>= TurianosFIR.dimension) 
				continue;
			
			int i = n.falls[k] + 1, j = k;
			
			for(int m = 0; m < directions.length ; m++) directions[m] = true; //reset directions
			
			//walk on the board to find score
			for (int m = 0; m < 4; m++) {
				if (directions[0]){ // right
					directions[0] = (j+m < TurianosFIR.dimension ) && ( board[i][j+m] == turn);
				}
				if (directions[1]){ //down-left
					directions[1] = (j-m > 0) && (i+m < TurianosFIR.dimension ) && ( board[i+m][j-m] == turn);
				}
				if (directions[2]){ //dawn
					directions[2] = (i+m < TurianosFIR.dimension ) && (board[i+m][j] == turn);
				}
				if (directions[3]){ //down-right
					directions[3] = (j+m < TurianosFIR.dimension ) && (i+m < TurianosFIR.dimension) && (board[i+m][j+m] == turn);
				}
			}
			for (int m = 0; m < directions.length; m++) {
				if(directions[m] == true)
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
		this.generateTree(); //TODO: Eliminar esta linea
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
		if(this.root.children == null) 
			this.generateTree();
		for(BoardNode n : this.root.children){
			if (n.falls[j]+1 >= TurianosFIR.dimension) 
				continue;
			if (n.board[this.root.falls[j]][j] == turn){
				this.root = n;
				return;
			}
		}
		throw new Error("El turno no se encontro en el arbol");
	}
	
	public String toString() {
		return this.root.toString();
	}
}
