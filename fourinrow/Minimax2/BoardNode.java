package unalcol.agents.examples.isi2017I.turianos.fourinrow.Minimax2;

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
}
