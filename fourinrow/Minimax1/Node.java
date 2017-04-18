package unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax1;

import java.util.ArrayList;

public final class Node implements Comparable<Node>{
	
	private int[] id;
	private int[][] board;
	private int score;
	private int deep;
	boolean max;
	
	protected ArrayList<Node> children;
	
	/**
	 * Construct a node
	 * 
	 * @param board	matrix with the state of the board 
	 * @param max	if this is a minimizer o maximizer node
	 * @param deep	deep of this node in the minimax tree
	 */
	public Node(int[][] board, boolean max, int deep) {
		this.board = board;
		this.generateId();
		this.max = max;
		this.deep = deep;
		this.children = null;
	}
	
	/**
	 * Construct a node
	 * 
	 * @param id	Array representation of the board 
	 * @param max	if this is a minimizer o maximizer node
	 * @param deep	deep of this node in the minimax tree
	 */
	public Node(int[] id, boolean max, int deep) {
		this.id = id;
		this.generateBoard();
		this.max = max;
		this.deep = deep;
		this.children = null;
	}
	
	protected Node(int [][] board){
		this.board = board;
		this.generateId();
	}
	
	/**
	 * Build a Board with the id of this object 
	 */
 	private void generateBoard() {
		int dimension = (int) Math.sqrt( this.id.length ); 
		this.board = new int[dimension][dimension];
		for (int i = 0; i < this.id.length; i++){
			
			this.board[i/dimension][i%dimension] =this.id[i];
		}
	}
	
	/**
	 * Build a id array of this node with the matrix Board representation
	 */
	private void generateId() {
		this.id = new int[(int) Math.pow(this.board.length, 2)];
		for(int i = 0; i < this.board.length; i++){
			for(int j = 0; j < this.board.length; j++)
				this.id[i*this.board.length +j] = this.board[i][j] ;
		}
	}

	public int getScore(){
		return this.score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getDeep(){
		return this.deep;
	}
	
	public void setDeep(int deep){
		this.deep = deep;
	}
	
	/**
	 * Add a possible next move in this board 
	 * @param n Node with the next board
	 */
	public void addPossibleNextMove(Node n){
		this.children.add(n);
	}
	
	public void addPossibleNextMove(ArrayList<Node> moves){
		this.children = moves;
	}
	
	@Override
	public int compareTo(Node o) {
		for(int i = 0; i < this.id.length; i++){
			if (this.id[i] < o.id[i])
				return 1;
			if (this.id[i] > o.id[i])
				return -1;
		}
		return 0;
	}
	
	public int[][] getBoard (){
		return this.board.clone();
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				sb.append(this.board[i][j]);
			}
			sb.append('\n');
		}
		sb.append(this.score);
		return sb.toString();
	}

}
