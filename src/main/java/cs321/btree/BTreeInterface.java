package cs321.btree;

public interface BTreeInterface<E> {

    /**
     * Searches for a key in the B-tree.
     * @param x the node to start the search from
     * @param k the key to search for
     * @return a 2-tuple (y,i) consisting of a node y and an index i such that y.keyi = k,
     *         where we are searching for key k. If not found, the search returns null.
     */
    public Tuple<BTreeNode<E>, Integer> search(BTreeNode<E> x, E k);

    /**
     * Creates an empty B-tree.
     * @return the newly created B-tree
     */
    public BTree<E> create();

    /**
     * Inserts a key into the B-tree.
     * @param k the key to insert
     */
    public void insert(E k);

    /**
     * Splits the root of the B-tree.
     * @return the new root of the B-tree
     */
    public BTreeNode<E> splitRoot();

    /**
     * Splits a child of a B-tree node.
     * @param x the parent node of the child to split
     * @param i the index of the child to split
     */
    public void splitChild(BTreeNode<E> x, int i);

    /**
     * Inserts a key into a non-full B-tree node.
     * @param x the node to insert the key into
     * @param k the key to insert
     */
    public void insertNonfull(BTreeNode<E> x, E k);

}
