package unalcol.agents.examples.isi2017I.turianos.fourinrow.MinimaxA;

import java.util.ArrayList;

import unalcol.agents.examples.isi2017I.turianos.fourinrow.TurianosFIR;

final class BoardNode {
	
	int[][] board;
	int[] falls;
	int deep;
	boolean max;
	boolean sheet;
	int score;
	boolean isFourInRow = false;
	
	ArrayList<BoardNode> children;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TurianosFIR.dimension; i++) {
			for (int j = 0; j < TurianosFIR.dimension; j++) {
				sb.append(this.board[i][j]);
			}
			sb.append('\n');
		}
		sb.append(this.score);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().getName() != this.getClass().getName()) return false;
		
		BoardNode o =  (BoardNode) obj;
		if(o.max != this.max) return false;
		
		for(int i = 0; i < TurianosFIR.dimension; i++){
			if(o.falls[i] != this.falls[i]) return false;
		}
		return true;
		
	}
}
