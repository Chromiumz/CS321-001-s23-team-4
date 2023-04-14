package cs321.btree;

public class BTree<E>
{

	public class BTreeNode<E>
	{
		private TreeObject<E> self;
		private BTreeNode<E> left;
		private BTreeNode<E> right;
		
		public BTreeNode(TreeObject<E> treeObject) {
			this.self = treeObject;
		}
		
		public BTreeNode(E object) {
			this(new TreeObject<E>(object));
		}
		
		public BTreeNode<E> getRight() {
			return right;
		}
		
		public BTreeNode<E> getLeft() {
			return left;
		}
		
		public TreeObject<E> getSelf() {
			return self;
		}
	}
}
