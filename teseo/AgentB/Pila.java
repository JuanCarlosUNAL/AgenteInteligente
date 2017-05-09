package unalcol.agents.examples.isi2017I.turianos.teseo.AgentB;

/*
 * Ordena los puntos de acuerdo con una heuristica especificada en el constructor
 * tiene prioridad el que menor valor obtenga
 * 
 */

class Pila {
	
	private static int INIT_SIZE = 30;
	
	private Coordenada[] arr;
	private TeseoAgent actual; 
	private int size ;
	
	private Heuristic<Object> heuristic;
	
	@SuppressWarnings("unchecked")
	public Pila(TeseoAgent a, Heuristic<?> heuristic){
		this.arr = new Coordenada[INIT_SIZE];
		this.size = 0;
		this.actual = a;
		this.heuristic = (Heuristic<Object>) heuristic; //heuristica para organizar las coordenadas
	}
	private static int left(int i ){
		return 2*i;
	}
	
	private static int right(int i){
		return 2*i+1;
	}
	
	public Coordenada verSiguiente() {
		if (this.Vacia()) return null;
		else return arr[0];
	}
	
	public Coordenada eliminarSiguiente() {
		Coordenada aux =  arr[0];
		arr[0] = arr[this.size-1];
		this.size --;
		this.max_heapify (0);
		return aux;
	}
	
	private void max_heapify(int i) {
		
		int l = Pila.right(i);
		int r = Pila.left(i);
		
		int minim = 0;
		if ( l < this.size && heuristic.comparator( funcionPeso(arr[l]), funcionPeso(arr[i])) ) minim = l;
		else minim = i;
		if ( r < this.size && heuristic.comparator( funcionPeso (arr[r]), funcionPeso(arr[minim])) ) minim = r;
		if ( minim != i ) {
			intercambio (i, minim);
			this.max_heapify(minim);
		}
		return;
	}

	private void intercambio(int a, int b) {
		Coordenada aux = arr[a];
		arr[a] = arr[b];
		arr[b] = aux;
	}

	private Object funcionPeso(Coordenada coordenada) {
		return this.heuristic.evaluate(this.actual.mapa, this.actual,coordenada);
	}

	public void add(Coordenada a) {
		if (this.size >= this.arr.length-2)
			aumentar_array();
		this.size++;
		arr[this.size-1] = a;
		build_heap();
		return;
	}
	
	private void aumentar_array() {
		Coordenada[] aux = this.arr;
		this.arr = new Coordenada[ aux.length*2 ];
		for(int i = 0; i < aux.length ; i ++) this.arr[i] = aux[i];
		return;
	}
	
	private void build_heap() {
		for (int i =  Math.floorDiv(this.size-1,2); i >= 0 ; i--)
			this.max_heapify(i);
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
		String str = "";
		for (int i = 0; i < this.size; i++ ) {
			str += arr[i].toString() + ", ";
		}
		return str;
	}

}
