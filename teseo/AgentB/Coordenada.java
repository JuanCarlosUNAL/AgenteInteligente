package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

import java.awt.Point;

class Coordenada extends Point implements Comparable<Coordenada>{
	
	private static final long serialVersionUID = -474084216159588422L;
	private static int counter = 0;
	
	int comida;
	int consecutivo; //orden en el que es creado una coordenada
	
	public Coordenada (double d, double e){
		super();
		this.setLocation(d,e);
		this.consecutivo = Coordenada.counter;
		Coordenada.counter++;
		this.comida = -1;
	}
	
	public Coordenada (int x, int y, int comida){
		super.setLocation(x,y);
		this.comida = comida;
		this.consecutivo = Coordenada.counter;
		Coordenada.counter++;
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
	public int compareTo(Coordenada o) {
		int dif_x = this.x - o.x;
		int dif_y = this.y - o.y;
		return (dif_x == 0 ? dif_y : dif_x);
	}
	
	/*
	 * devuelve la distancia de un punbto a otro
	 */
	public double distance(Coordenada c) {
		double t1 = Math.pow(c.x - this.x,2);
		double t2 = Math.pow(c.y - this.y , 2);
		return Math.sqrt( t1 + t2 );
	}
}
