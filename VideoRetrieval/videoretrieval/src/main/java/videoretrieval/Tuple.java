package videoretrieval;

public class Tuple<S, T> {
	
	private S item1;
	private T item2;
	
	public Tuple(S item1, T item2) {
		setItem1(item1);
		setItem2(item2);
	}
	
	public S getItem1() {
		return item1;
	}
	
	private void setItem1(S item1) {
		this.item1 = item1;
	}
	
	public T getItem2() {
		return item2;
	}
	
	private void setItem2(T item2) {
		this.item2 = item2;
	}
	
}
