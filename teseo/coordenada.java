package unalcol.agents.examples.isi2017I.turianos.teseo;

import java.awt.Point;

public final class coordenada extends Point implements Comparable<coordenada>{
	private static final long serialVersionUID = 1L;
	public int comida;
	
	public coordenada (double d, double e){
		super();
		this.setLocation(d,e);
	}
	public coordenada (int x, int y, int comida){
		super.setLocation(x,y);
		this.comida = comida;
		return;
	}
	
	/**
	 * Rota hacia la derecha la cordenada
	 */
	public void rotar(){
		int x = this.x;
		int y =  this.y;
		this.setLocation(y,-x);
		return;
	}
	
	@Override
	public String toString() {
		return "("+this.x + "," + this.y+")";
	}
	/**
	 * Compara dos coordenadas convieriendolas a string, 
	 * Si se modifica asegurar que la funcion pueda devolver numeros positivos y nï¿½meros negativos 
	 * para el manejo de diccionarios (arboles binarios).
	 */
	@Override
	public int compareTo(coordenada o) {
		int dif_x = this.x - o.x;
		int dif_y = this.y - o.y;
		return (dif_x == 0 ? dif_y : dif_x);
	}
	
	/*
	 * devuelve la distancia de un punbto a otro
	 */
	public double distance(coordenada c) {
		double t1 = Math.pow(c.x - this.x,2);
		double t2 = Math.pow(c.y - this.y , 2);
		return Math.sqrt( t1 + t2 );
	}
}
