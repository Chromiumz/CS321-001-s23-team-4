package cs321.btree;

import cs321.btree.BTree.BTreeNode;

public interface BTreeInterface<E> {

    /**
     * Searches for a key in the B-tree.
     * @param object the key to search for
     * @return the node that contains the key, or null if the key is not found
     */
    public BTreeNode<E> search(E object);

    /**
     * Inserts a key into the B-tree.
     * @param object the key to insert
     */
    public void insert(E object);

    /**
     * Splits the root of the B-tree.
     * @return the new root of the B-tree
     */
    public BTreeNode<E> splitRoot();

    /**
     * Splits a child of a B-tree node.
     * @param i the index of the child to split
     */
    public void splitChild(int i);

    /**
     * Inserts a key into a non-full B-tree node.
     * @param x the node to insert the key into
     * @param k the key to insert
     */
    public void insertNonfull(BTreeNode<E> x, E object);
}
