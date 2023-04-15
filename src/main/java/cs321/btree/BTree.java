package cs321.btree;

import java.util.Vector;

public class BTree<E> implements BTreeInterface
{

	private BTreeNode<E> root;
	private int t = 0;
	
	public BTree(int t) {
		this.t = t;
		this.root = new BTreeNode<E>(t);
	}
	
	static class Tuple<K, V> {
	    public final K node;
	    public final V index;

	    public Tuple(K node, V index) {
	        this.node = node;
	        this.index = index;
	    }
	}
	
	static class BTreeNode<E>
	{
		private TreeObject<E>[] keys;
		private BTreeNode<E>[] child;
		private int n;
		private boolean leaf;
		
		@SuppressWarnings("unchecked")
		public BTreeNode(int t, boolean leaf) {
			this.keys = (TreeObject<E>[]) new Object[(2*t)-1];
			this.child = (BTreeNode<E>[]) new Object[(2*t)];
		}
		
		public BTreeNode(int t) {
			this(t, true);
		}
	}

	@Override
	public BTreeNode search(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BTreeNode splitRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void splitChild(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertNonfull(BTreeNode x, Object object) {
		// TODO Auto-generated method stub
		
	}
}
