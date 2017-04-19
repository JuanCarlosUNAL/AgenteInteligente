package unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

import unalcol.agents.Agent;
import unalcol.agents.examples.isi2017I.turianos.fourinrow.FourinrowProgramTuirianos;

public final class MiniMaxTree extends Thread {
	
	public static boolean generateTree = true;
	
	private Node root;
	private int color;
	private int rivalColor;
	public int partialDeep;
	
	private FourinrowProgramTuirianos agent;
	private boolean restart = false;
	
	/**
	 * crate a Minimax tree with a root node
	 * @param init	root 
	 * @param color a integer with the color of the maximizer player
	 */
	public MiniMaxTree(Node init, int color,FourinrowProgramTuirianos g){
		this.setName("Minimax Tree");
		this.root = init;
		this.color = color;
		this.agent = g;
		this.rivalColor = (color == FourinrowProgramTuirianos.BLACK?FourinrowProgramTuirianos.WHITE:FourinrowProgramTuirianos.BLACK);
	}
	
	@Override
	public void run() {
		//make bread first search with to construct the minimax tree
		LinkedList<Node> struct = new LinkedList<Node>();
		TreeSet<Node> marked = new TreeSet<Node>();
		struct.addLast(this.root);//add the root with the initial board with at less one move
		marked.add(this.root);
		
		while(! struct.isEmpty() && this.partialDeep <= FourinrowProgramTuirianos.maxDeep){
			
			//wainting the move
			if(this.agent.Playing){
				synchronized (this.agent) {
					try {this.agent.wait();
					} catch (InterruptedException e) {}
				}
			}
			
			//re-creating a search
			if(this.restart){
				struct.clear();
				marked.clear();
				struct.addLast(this.root);//add the root with the initial board with at less one move
				marked.add(this.root);
				this.partialDeep = 0;
				this.restart = false;
			}
			
			
			Node curr = struct.removeFirst(); //get the next element of struct
			
			if (curr.children == null){
				ArrayList<Node> children = this.getChilds(curr,curr.max?color:rivalColor);
				curr.addPossibleNextMove(children);// add the children to children of the node
			}
			
			if(Math.random() > 0.90){
				this.partialDeep = curr.getDeep() - this.root.getDeep();
				synchronized (this) {
					this.notify();					
				}
				Thread.yield();
			}
			
			for (Node n : curr.children){
				
				//Add some basic characteristics
				
				//set the deep of the node
				n.setDeep(curr.getDeep() + 1);
				
				
				// set a score of the node
				if (curr.getDeep() >= 4) { //if the board has more than five moves
					n.setScore( this.getWinner(n, curr.max?color:rivalColor) ); // if curr is maximizing then next move minimizing
				}else{
					n.setScore(0);
				}
				
				if(marked.contains(n)) {
					continue;
				}
								
				//if node n is has not score then it has no winner 
				if (n.getScore() == 0){
					struct.addLast(n);
					n.max = !curr.max;  // if its parent is maximizer the the child is minimizer
				}else if (n.getScore() < 0 && !n.max) { //if n get -1 and is minimizer node, Prune tree
					break;
				}else if (n.getScore() > 0 && n.max) { //if n get 1 and is maximizer node, Prune tree
					break;
				}
				marked.add(n); //mark it like visited
			}
		}
		
	}

	private int getWinner(Node n, int turn) {
		int[][] board = n.getBoard();  
		boolean[] directions = new boolean[4]; //directions
		
		for (int i = board.length -1; i >= 0; i--){
			for (int j = board.length - 1; j >= 0; j--) {
				int marker = board[i][j]; // the current piece in the i,j board indices
				if (marker !=  turn ) continue; // if this piece is not of this turn 
				
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
					return turn == color?3:-5;
			}
		}
		return 0;
	}

	private ArrayList<Node> getChilds(Node curr,int turn) {
		int [][] board = curr.getBoard();
		int dimension =  board.length;
		
		//detect holes
		int[] falls = new int[dimension];
		for (int j = 0; j < dimension; j++){ //iterate over columns
			for (int i = 0; i < dimension ; i++) {
				if (board[i][j] != FourinrowProgramTuirianos.EMPTY){
					falls[j] = i-1;
					break;
				}else{
					falls[j] = i;
				}
			}
		}
		
		ArrayList<Node> ans = new ArrayList<Node>(); 
		for(int i = 0; i < dimension; i++){
			if(falls[i] < 0) continue;
			int[][] aux = MiniMaxTree.cloneBoard(board);
			aux[falls[i]][i] = turn;
			ans.add(new Node(aux));			
		}
		
		//Randomize arrayList
		Collections.shuffle(ans, new SecureRandom());
		
		return ans;
	}
	
	@Override
	public String toString() {
		return this.root.toString();
	}

	public void calcScores() {
		this.calcSocre(this.root);
	}
	
	private synchronized int calcSocre(Node node) {
		if(node.getScore() != 0){
			return node.getScore();
		}else if (node.children == null){
			return 0;
		}
		
		int sum = 0;
		for( Node n: node.children ){
			//sum = this.calcSocre(n);
			if(node.max){
				if(n.children == null && this.calcSocre(n) < 0){
					sum = this.calcSocre(n)+1;
					break;
				}else{
					sum = (n.getScore()-1) < sum?n.getScore()-1:sum; ;
				}
			}else{
				if(n.children == null && this.calcSocre(n) > 1){
					sum = this.calcSocre(n)-1;
					break;
				}else{
					sum = (n.getScore()+1) < sum?n.getScore()+1:sum; ;
				}
			}
		}
		node.setScore(sum);
		return sum;
	}

	private static int[][] cloneBoard(int[][] board){
		int[][] ans = new int[board.length][board.length];
		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans.length; j++) {
				ans[i][j] = board[i][j];
			}
		}
		return ans;
	}

	public synchronized Node getNextBestMove() {
		
		if(this.root.children == null) this.root.children = this.getChilds(this.root, color); // machetazo
		
		Node Best = this.root.children.get(0);
		
		for (int i = 1; i < this.root.children.size(); i++) {
			//if(this.root.children.get(i).getScore() > 0 && this.root.children.get(i).getScore() < Best.getScore()){
			if(this.root.children.get(i).getScore() > Best.getScore()){
				Best = this.root.children.get(i);
			}
		}
		if(Best.getScore() < 0){
			System.out.println("Escogi uno muy malo");
		}
		return Best;
	}

	public void reasignRoot(Node next) {
		this.root = next;
		this.restart  = true;
	}

	public void reasignRoot(int i, int j) {
		for(Node n : this.root.children){
			if (n.getBoard()[i][j] != FourinrowProgramTuirianos.EMPTY){
				this.reasignRoot(n);
				break;
			}
		}
	}
	
	public synchronized void waitForAgent(Agent g){
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
