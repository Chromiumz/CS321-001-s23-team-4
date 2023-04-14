package cs321.btree;

import java.util.Objects;

/**
 * Represents a TreeObject capable of storing information about duplicates and a generic object.
 * 
 * @author Ernest
 *
 * @param <E>
 */
public class TreeObject<E>
{
	protected E object;
	private int frequency;
	
	/**
	 * Constructor for a TreeObject
	 * 
	 * @param object The object to store inside this TreeObject
	 */
	public TreeObject(E object) {
		this.object = object;
		this.frequency = 1;
	}
	
	/**
	 * Increments the frequency of this TreeObject by one
	 */
	public void incrementFrequency() {
		frequency++;
	}
	
	/**
	 * Gets the frequency of this TreeObject
	 * 
	 * @return The frequency of this tree object (Amount of duplicates)
	 */
	public int getFrequency() {
		return frequency;
	}
	
	
	/**
	 * Gets the object within this TreeObject
	 * 
	 * @return The object
	 */
	public E getObject() {
		return object;
	}
	
	/**
	 * Checks if this TreeObject is equal to another Object.
	 * 
	 * @param o The object to test for equality
	 * 
	 * @return True if the internal objects of both TreeObjects are equal, false otherwise.
	 */
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass()) {
	        return false;
	    }
	    
	    TreeObject<?> that = (TreeObject<?>) o;
	    return Objects.equals(object, that.object);
	}
}
