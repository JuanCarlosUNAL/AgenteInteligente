package unalcol.agents.examples.labyrinth.multeseo.eater.isi2017I.turianos;

/*
 * Ordena los puntos de tal manera que cuando haga una peticion de un punto 'a' retorne el punto 'b' mas cercano a 'a' 
 */

public final class Pila {
	
	private static int INIT_SIZE = 30;
	private coordenada[] arr;
	private Agent2 actual; 
	private int size ;
	
	public Pila(Agent2 a){
		this.arr = new coordenada[INIT_SIZE];
		this.size = 0;
		this.actual = a;
	}	
	private static int left(int i ){
		return 2*i;
	}
	
	private static int right(int i){
		return 2*i+1;
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
		if ( l < this.size && distancia(arr[l]) < distancia(arr[i]) ) minim = l;
		else minim = i;
		if ( r < this.size && distancia (arr[r]) < distancia(arr[minim]) ) minim = r;
		if ( minim != i ) {
			intercambio (i, minim);
			this.max_heapify(minim);
		}
		return;
	}

	private void intercambio(int a, int b) {
		coordenada aux = arr[a];
		arr[a] = arr[b];
		arr[b] = aux;
	}

	private double distancia(coordenada coordenada) {
		return this.actual.posicion.distance(coordenada);
	}

	public void add(coordenada a) {
		if (this.size-2 == this.arr.length)
			aumentar_array();
		this.size++;
		arr[this.size-1] = a;
		build_heap();
		return;
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
	
	public void reaorganizarPila(){
		this.build_heap();
		return;
	}
	
	public boolean Vacia(){
		return this.size == 0;
	}
	
	@Override
	public String toString() {
		return this.arr.toString();
	}

}
