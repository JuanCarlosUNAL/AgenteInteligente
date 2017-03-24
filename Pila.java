package unalcol.agents.examples.labyrinth.multeseo.eater.isi2017I.turianos;

/*
 * Ordena los puntos de tal manera que cuando haga una peticion de un punto 'a' retorne el punto 'b' mas cercano a 'a' 
 */

public final class Pila {
	
	private static int INIT_SIZE = 30;
	private coordenada[] arr;
	private coordenada actual; 
	private int size ;
	
	public Pila(){
		this.arr = new coordenada[INIT_SIZE];
		this.size = 0;
	}	
	private static int left(int i ){
		return 2*i;
	}
	private static int right(int i){
		return 2*i+1;
	}
	private static int parent(int i){
		return (int)Math.floor(i/2);
	}
	
	public coordenada verSiguiente() {
		if (this.Vacia()) return null;
		else return arr[0];
	}
	
	public coordenada eliminarSiguiente() {
		coordenada aux =  arr[0];
		arr[0] = arr[this.size-1];
		this.size --;
		this.max_heapify (0);
		return aux;
	}
	
	private void max_heapify(int i) {
		int l = Pila.right(i);
		int r = Pila.left(i);
		int minim = 0;
		if ( distancia(arr[l]) < distancia(arr[i]) ) minim = l;
		else minim = i;
		if ( distancia (arr[r]) < distancia(arr[minim]) ) minim = r;
		if ( minim != i ) intercambio (i, minim);  
		return;
	}

	private void intercambio(int a, int b) {
		coordenada aux = arr[a];
		arr[a] = arr[b];
		arr[b] = aux;
	}

	private double distancia(coordenada coordenada) {
		return this.actual.distance(coordenada);
	}

	public void add(coordenada a) {
		if (this.size-2 == this.arr.length )
			aumentar_array();
		this.size++;
		arr[this.size] = a;
		build_heap();
	}
	
	private void aumentar_array() {
		coordenada[] aux = this.arr;
		this.arr = new coordenada[ aux.length*2 ];
		for(int i = 0; i < aux.length ; i ++) this.arr[i] = aux[i];
		return;
	}
	
	private void build_heap() {
		for (int i =  Math.floorDiv(this.size,2); i >= 0 ; i--){
			this.max_heapify(i);
		}
	}
	
	public boolean Vacia(){
		return this.size == 0;
	}
	
	public void setCoordenadaActual(coordenada c){
		this.actual = c; 
	}
	

}
