package cs321.btree;

import java.io.IOException;

import cs321.btree.BTree.BTreeNode;
import cs321.btree.BTree.Tuple;

public interface BTreeInterface {

    /**
     * Searches for a key in the B-tree.
     * @param x the node to start the search from
     * @param k the key to search for
     * @return the node that contains the key, or null if the key is not found
     * @throws IOException 
     */
    public Tuple search(BTreeNode x, long k) throws IOException;

    /**
     * Creates an empty B-tree.
     * @throws IOException 
     */
    public void create() throws IOException;

    /**
     * Inserts a key into the B-tree.
     * @param k the key to insert
     */
    public void insert(long k) throws IOException;

    /**
     * Splits the root of the B-tree.
     * @return the new root of the B-tree
     */
    public BTreeNode splitRoot() throws IOException;

    /**
     * Splits a child of a B-tree node.
     * @param x the parent node of the child to split
     * @param i the index of the child to split
     */
    public void splitChild(BTreeNode x, int i) throws IOException;

    /**
     * Inserts a key into a non-full B-tree node.
     * @param x the node to insert the key into
     * @param k the key to insert
     */
    public void insertNonfull(BTreeNode x, long k) throws IOException;
}
