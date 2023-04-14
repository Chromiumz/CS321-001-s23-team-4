package cs321.btree;

import java.util.Objects;

public class TreeObject<E>
{
	protected E object;
	private int frequency;
	
	public TreeObject(E object) {
		this.object = object;
		this.frequency = 1;
	}
	
	public void incrementFrequency() {
		frequency++;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	
	public E getObject() {
		return object;
	}
	
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass()) {
	        return false;
	    }
	    
	    TreeObject<?> that = (TreeObject<?>) o;
	    return Objects.equals(object, that.object);
	}
}
