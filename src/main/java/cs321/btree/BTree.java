package cs321.btree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class BTree {
    private int METADATA_SIZE = Long.BYTES + Integer.BYTES;
    private long nextDiskAddress = METADATA_SIZE;
    private FileChannel file;
    private ByteBuffer buffer;
    private int nodeSize;

    private long rootAddress = METADATA_SIZE; // offset to the root node

    private BTreeNode root;
    private int t;

    public BTree(File BTreeFile, int pageSize) {    	
    	//dynamic space
    	int x1 = TreeObject.getDiskSize() * 2;
    	int x2 = Long.BYTES * 2;
    	
    	//constant space
    	int x3 = Integer.BYTES + 1 + TreeObject.getDiskSize() * -1;
    	
    	pageSize -= x3;
    	
    	pageSize /= x1 + x2;
    	
    	this.t = pageSize;
        
        this.root = new BTreeNode(t, false, false);

        nodeSize = root.getDiskSize();
        buffer = ByteBuffer.allocateDirect(nodeSize);

        try {
            if (!BTreeFile.exists()) {
                BTreeFile.createNewFile();
                RandomAccessFile dataFile = new RandomAccessFile(BTreeFile, "rw");
                file = dataFile.getChannel();
                writeMetaData();
            } else {
                RandomAccessFile dataFile = new RandomAccessFile(BTreeFile, "rw");
                file = dataFile.getChannel();
                readMetaData();
                this.root = diskRead(rootAddress);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    /**
     * Read the metadata from the data file.
     * @throws IOException
     */
    public void readMetaData() throws IOException {
        file.position(0);

        ByteBuffer tmpbuffer = ByteBuffer.allocateDirect(METADATA_SIZE);

        tmpbuffer.clear();
        file.read(tmpbuffer);

        tmpbuffer.flip();

        rootAddress = tmpbuffer.getLong();
        t = tmpbuffer.getInt();
    }


    /**
     * Write the metadata to the data file.
     * @throws IOException
     */
    public void writeMetaData() throws IOException {
        file.position(0);

        ByteBuffer tmpbuffer = ByteBuffer.allocateDirect(METADATA_SIZE);

        tmpbuffer.clear();
        tmpbuffer.putLong(rootAddress);
        tmpbuffer.putInt(t);

        tmpbuffer.flip();
        file.write(tmpbuffer);
    }

    /**
     * Reads a node from the disk and returns a Node object built from it.
     * @param diskAddress the byte offset for the node in the data file
     * @return the Node object
     * @throws IOException
     */
    public BTreeNode diskRead(long diskAddress) throws IOException {
        if (diskAddress == 0) return null;

        file.position(diskAddress);
        buffer.clear();

        file.read(buffer);
        buffer.flip();
        
        int n = buffer.getInt();

        TreeObject[] keys = new TreeObject[2 * t - 1];

        for (int i = 0; i < (2 * t) - 1; i++) {
            long value = buffer.getLong();
            long frequency = buffer.getLong();

            TreeObject key = null;
            if (value != -1 && frequency != -1) {
                key = new TreeObject(value);
                key.setFrequency(frequency);
            }

            keys[i] = key;
        }



        byte flag = buffer.get(); // read a byte
        boolean leaf = false;
        if (flag == 1)
            leaf = true;

        long[] child = new long[2 * t];

        for (int i = 0; i < child.length; i++) {
            child[i] = buffer.getLong();
        }

        BTreeNode x = new BTreeNode(t, leaf, false);

        x.key = keys;
        x.child = child;
        x.n = n;
        x.address = diskAddress;

        return x;
    }

    /**
     * Writes a node to the disk at the specified disk offset *in the Node object).
     * @param x the Node to write
     * @throws IOException
     */
    public void diskWrite(BTreeNode x) throws IOException {
        file.position(x.address);
        buffer.clear();
        
        buffer.putInt(x.n);

        for (int i = 0; i < x.key.length; i++) {
            if (x.key[i] == null) {
                buffer.putLong(-1);
                buffer.putLong(-1);
            } else {
                buffer.putLong(x.key[i].getValue());
                buffer.putLong(x.key[i].getFrequency());
            }
        }

        if (x.leaf)
            buffer.put((byte) 1);
        else
            buffer.put((byte) 0);

        for (int i = 0; i < x.child.length; i++) {
            buffer.putLong(x.child[i]);
        }

        buffer.flip();
        file.write(buffer);
    }


    /**
     * Internal Class of BTree to represent a BTreeNode
     * 
     * @author Ernest
     *
     */
    class BTreeNode {
        private long address;
        private TreeObject[] key;
        private long[] child;
        private boolean leaf;
        private int n;

        /**
         * Constructor of a BTreeNode
         * 
         * @param t The degree of the BTree
         * @param leaf If the BTreeNode is a leaf
         * @param onDisk Should the node have an address that is non-null (Writable)
         */
        public BTreeNode(int t, boolean leaf, boolean onDisk) {
            this.key = new TreeObject[(2 * t) - 1];
            this.child = new long[(2 * t)];
            this.leaf = leaf;
            this.n = 0;

            if (onDisk) {
                address = nextDiskAddress;
                nextDiskAddress += nodeSize;
            }
        }

        /**
         * Constructor of a BTreeNode
         * 
         * @param t The degree of the BTree
         * @param leaf If the BTreeNode is a leaf
         */
        public BTreeNode(int t, boolean leaf) {
            this(t, leaf, false);
        }

        /**
         * Constructor of a BTreeNode
         * 
         * @param t The degree of the BTree
         */
        public BTreeNode(int t) {
            this(t, true);
        }

        /**
         * Gets the disk size of a BTreeNode so it can be stored in the BTree data file
         * This value will be different depending on the degree of the BTree!
         * 
         * @return The size in bytes.
         */
        public int getDiskSize() {
            return
            	TreeObject.getDiskSize() * key.length +
                Long.BYTES * child.length +
                Integer.BYTES +
                1;
        }
        
        /**
         * Get the address of the BTreeNode
         * 
         * @return The address to its position in the data file
         */
        public long getAddress() {
        	return address;
        }
        
        /**
         * Get all the keys held inside this node.
         * 
         * @return The keys.
         */
        public TreeObject[] getKeys() {
        	return key;
        }
        
        /**
         * Get a specific key at a certain index
         * 
         * @param x The index
         * @return The key at the given index
         */
        public TreeObject getKey(int x) {
        	if(x >= n)
        		return null;
        	return key[x];
        }
        
        /**
         * Get all the children of this BTreeNode
         * 
         * @return The children
         */
        public long[] getChildrenAddresses() {
        	return child;
        }
        
        /**
         * Get a specific child node at a given index
         * 
         * @param x The index
         * @return The child at the given index
         */
        public long getChildAddress(int x) {
        	if(x >= n)
        		return 0;
        	return child[x];
        }
        
        /**
         * Check if the BTreeNode is a leaf
         * 
         * @return Whether or not the BTreeNode is a leaf.
         */
        public boolean isLeaf() {
        	return leaf;
        }
        
        ///////////////////////////////////////////////
        //              HELPER METHODS               //
        // TRY TO AVOID USING THESE OUTSIDE OF TESTS //
        //  THEY CREATE MORE BTREENODES AT RUNTIME   //
        ///////////////////////////////////////////////
        
        /**
         * Returns a BTreeNode from a target index in the children.
         * 
         * @param x The target index
         * @return The target BTreeNode.
         * 
         * @throws IOException
         */
        public BTreeNode getChildBTreeNode(int x) throws IOException {
        	return diskRead(child[x]);
        }
        
        /**
         * Returns a BTreeNode array for the current BTreeNode.
         * 
         * !!! WARNING ALL BTREENODES ARE READ !!!
         * 
         * @return The array of children BTreeNodes
         * 
         * @throws IOException
         */
        public BTreeNode[] getAllChildBTreeNode() throws IOException {
        	int x = 0;
        	BTreeNode[] resolve = new BTreeNode[child.length];
        	while(x < n) {
        		resolve[x] = diskRead(child[x]);
        	}
        	return resolve;
        }
        
        /**
         * Creates a compact JSON representation of important data. Useful for quickly checking the information of a node.
         * 
         * @return The compiled String
         */
        public String toJSONData() {
        	String keyJSON = "";
        	for(int i = 0; i < key.length; i++) {
        		if(i >= n) {
        			continue;
        		}
        		if(key[i] == null) {
        			keyJSON += String.format("\t\t{\n\t\t\tvalue: null\n\t\t\tfrequency: null\n\t\t},\n");
        			continue;
        		}
        		keyJSON += String.format("\t\t{\n\t\t\tvalue: %d\n\t\t\tfrequency: %d\n\t\t},\n", key[i].getValue(), key[i].getFrequency());
        	}
        	
        	String childJSON = "";
        	for(int i = 0; i < child.length; i++) {
        		if(child[i] == 0) { 
        			continue;
        		}
        		childJSON += String.format("\t\t{\n\t\t\taddress: %d\n\t\t},\n", child[i]);
        	}
        	
        	String resolve = String.format("{\n"
        			+ "\taddress: %d,\n"
        			+ "\tdiskSize: %d,\n"
        			+ "\tdegree: %d,\n"
        			+ "\tisLeaf: %b,\n"
        			+ "\tn: %d,\n"
        			+ "\tkeys: [\n"
        			+ "%s"
        			+ "\t],\n"
        			+ "\tchildren: [\n"
        			+ "%s"
        			+ "\t]\n"
        			+ "}",
        			address,
        			getDiskSize(),
        			t,
        			leaf,
        			n,
        			keyJSON,
        			childJSON);
        	
        	return resolve;
        }
    }
    
    class Tuple {
    	private BTreeNode node;
    	private int index;
    	
    	public Tuple(BTreeNode node, int index) {
    		this.node = node;
    		this.index = index;
    	}
    	
    	public BTreeNode getNode() {
    		return node;
    	}
    	
    	public int getIndex() {
    		return this.index;
    	}
    }
    
    public void create() throws IOException {
        root = new BTreeNode(t, true, true);
        
		diskWrite(root);
    }
    
    public static void main(String[] args) throws IOException {
    	BTree tree = new BTree(new File("test15"), 200);
    	tree.create();
    	for(int i = 1; i < 100; i++) {
    		tree.insert(i);
    	}
    	
    	tree.inOrderTraversal(tree.root);
    }
    
    public void inOrderTraversal(BTreeNode node) throws IOException {
        if (node == null) {
            return;
        }
        int i;
        for (i = 0; i < node.n; i++) {
            if (!node.leaf) {
                inOrderTraversal(diskRead(node.child[i]));
            }
            System.out.print(node.key[i].getValue() + " ");
        }
        if (!node.leaf) {
            inOrderTraversal(diskRead(node.child[i]));
        }
    }
    
    public void printBTree(BTreeNode node) throws IOException {
        if (node != null) {
            // Convert the node to a JSON string
            String jsonData = node.toJSONData();
            
            // Print the JSON string
            System.out.println(jsonData);
            
            // Recursively print the child nodes
            if (!node.leaf) {
                for (int i = 0; i < node.n + 1; i++) {
                    BTreeNode child = diskRead(node.child[i]);
                    printBTree(child);
                }
            }
        }
    }
    
    /*
     * This code has slight modifications from the cited website in order to match out Binary File implementation.
     *  
     * Citation: https://www.programiz.com/dsa/b-tree#:~:text=B%2Dtree%20is%20a%20special,%2Dbalanced%20m%2Dway%20tree.
     * 
     * Splits a BTreeNode.
     * 
     * @param x The new parent.
     * @param pos The position of the split.
     * @param y The node to split on.
     * 
     * @throws If something goes wrong during a disk write/read operation
     */
    private void split(BTreeNode x, int pos, BTreeNode y) throws IOException {
      BTreeNode z = new BTreeNode(t, false, true);
      z.leaf = y.leaf;
      z.n = t - 1;
      for (int j = 0; j < t - 1; j++) {
        z.key[j] = y.key[j + t];
      }
      if (!y.leaf) {
        for (int j = 0; j < t; j++) {
          z.child[j] = y.child[j + t];
        }
      }
      y.n = t - 1;
      for (int j = x.n; j >= pos + 1; j--) {
        x.child[j + 1] = x.child[j];
      }
      x.child[pos + 1] = z.address;

      for (int j = x.n - 1; j >= pos; j--) {
        x.key[j + 1] = x.key[j];
      }
      x.key[pos] = y.key[t - 1];
      x.n = x.n + 1;
      
      diskWrite(z);
      diskWrite(y);
      diskWrite(x);
    }

    /*
     * This code has slight modifications from the cited website in order to match out Binary File implementation.
     *  
     * Citation: https://www.programiz.com/dsa/b-tree#:~:text=B%2Dtree%20is%20a%20special,%2Dbalanced%20m%2Dway%20tree.
     * 
     * Inserts a value into the BTree
     * 
     * @param key The value to insert.
     * 
     * @throws If something goes wrong during a disk write/read operation
     */
    public void insert(final long key) throws IOException {
      BTreeNode r = root;
      if (r.n == 2 * t - 1) {
        BTreeNode s = new BTreeNode(t, false, true);
        root = s;
        s.leaf = false;
        s.n = 0;
        s.child[0] = r.address;
        split(s, 0, r);
        insertValue(s, key);
      } else {
        insertValue(r, key);
      }
    }

    /*
     * This code has slight modifications from the cited website in order to match out Binary File implementation.
     *  
     * Citation: https://www.programiz.com/dsa/b-tree#:~:text=B%2Dtree%20is%20a%20special,%2Dbalanced%20m%2Dway%20tree.
     * 
     * Inserts a value at a specific BTreeNode
     * 
     * @param x The BTreeNode that will attempt to inherit the key
     * @param k The value to insert.
     * 
     * @throws If something goes wrong during a disk write/read operation
     */
    final private void insertValue(BTreeNode x, long k) throws IOException {

      if (x.leaf) {
        int i = 0;
        for (i = x.n - 1; i >= 0 && k < x.key[i].value; i--) {
          x.key[i + 1] = x.key[i];
        }
        x.key[i + 1] = new TreeObject(k);
        x.n = x.n + 1;
        diskWrite(x);
      } else {
        int i = 0;
        for (i = x.n - 1; i >= 0 && k < x.key[i].value; i--) {
        }
        ;
        i++;
        BTreeNode tmp = diskRead(x.child[i]);
        if (tmp.n == 2 * t - 1) {
          split(x, i, tmp);
          if (k > x.key[i].value) {
            i++;
          }
        }
        insertValue(diskRead(x.child[i]), k);
      }

    }
    
    
    public BTreeNode getRoot() {
    	return root;
    }
    
    public int getDegree() {
    	return t;
    }
    
    public int getNodeSize() {
    	return nodeSize;
    }
    
    public int getMetaDataSize() {
    	return METADATA_SIZE;
    }

}