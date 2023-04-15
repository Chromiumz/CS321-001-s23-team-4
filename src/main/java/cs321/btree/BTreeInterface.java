package cs321.btree;

import cs321.btree.BTree.BTreeNode;

public interface BTreeInterface {

    /**
     * Searches for a key in the B-tree.
     * @param x the node to start the search from
     * @param k the key to search for
     * @return the node that contains the key, or null if the key is not found
     */
    public BTreeNode search(BTreeNode x, long k);

    /**
     * Creates an empty B-tree.
     * @return the newly created B-tree
     */
    public BTree create();

    /**
     * Inserts a key into the B-tree.
     * @param k the key to insert
     */
    public void insert(long k);

    /**
     * Splits the root of the B-tree.
     * @return the new root of the B-tree
     */
    public BTreeNode splitRoot();

    /**
     * Splits a child of a B-tree node.
     * @param x the parent node of the child to split
     * @param i the index of the child to split
     */
    public void splitChild(BTreeNode x, int i);

    /**
     * Inserts a key into a non-full B-tree node.
     * @param x the node to insert the key into
     * @param k the key to insert
     */
    public void insertNonfull(BTreeNode x, long k);
}
