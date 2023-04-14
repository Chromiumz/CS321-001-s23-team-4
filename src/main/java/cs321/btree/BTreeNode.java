package cs321.btree;

public class BTreeNode<E>
{
	TreeObject[] keys;
	
	public BTreeNode(int m) {
		this.keys = new TreeObject[m-1];
	}
}