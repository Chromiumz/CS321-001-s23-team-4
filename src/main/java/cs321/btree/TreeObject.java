package cs321.btree;

import java.util.Objects;

/**
 * Represents a TreeObject capable of storing information about duplicates and a long value.
 * 
 * @author Ernest
 */
public class TreeObject
{
	protected long value;
	private long frequency;

	/**
	 * Constructor for a TreeObject
	 * 
	 * @param object The object to store inside this TreeObject
	 */
	public TreeObject(long value) {
		this.value = value;
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
	public long getFrequency() {
		return frequency;
	}
	
	
	/**
	 * Gets the value within this TreeObject
	 * 
	 * @return The value
	 */
	public long getValue() {
		return value;
	}
	
	public static int getDiskSize() {
		return Long.BYTES*2;
	}
}
