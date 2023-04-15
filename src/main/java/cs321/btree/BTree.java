package cs321.btree;

public class BTree implements BTreeInterface
{    
	private BTreeNode root;
	private int t = 0;
	
	public BTree(int t) {
		this.t = t;
		this.root = new BTreeNode(t);
	}
	
	static class BTreeNode
	{
		private long address;
		private TreeObject[] key;
		private long[] child;
		private boolean leaf;

		public BTreeNode(int t, boolean leaf) {
			this.key = (TreeObject[]) new Object[(2*t)-1];
			this.child = new long[(2*t)];
		}
		
		public BTreeNode(int t) {
			this(t, true);
		}
		
		public int getDiskSize() {
			return 
					TreeObject.getDiskSize() * key.length 
					+ Long.BYTES * child.length 
					+ 1;
		}
	}

	@Override
	public BTreeNode search(BTreeNode x, long k) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BTree create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(long k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BTreeNode splitRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void splitChild(BTreeNode x, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertNonfull(BTreeNode x, long k) {
		// TODO Auto-generated method stub
		
	}
	
}
