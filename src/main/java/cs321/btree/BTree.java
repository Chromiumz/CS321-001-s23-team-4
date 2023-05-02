package cs321.btree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import cs321.create.SequenceUtils;

public class BTree {
    private int METADATA_SIZE = Long.BYTES + Integer.BYTES;
    private long nextDiskAddress = METADATA_SIZE;
    private FileChannel file;
    private ByteBuffer buffer;
    private int nodeSize;

    private long rootAddress = METADATA_SIZE; // offset to the root node

    private BTreeNode root;
    private int t;
    
    private Cache cache;
    private boolean cacheEnabled;

    public BTree(File BTreeFile, int degree, boolean cacheEnabled, int cacheSize) {
    	this.cacheEnabled = cacheEnabled;
    	this.cache = new Cache(cacheSize);
    	
    	this.t = degree;
    	
    	/*
    	//dynamic space
    	int x1 = TreeObject.getDiskSize() * 2;
    	int x2 = Long.BYTES * 2;
    	
    	//constant space
    	int x3 = Integer.BYTES + 1 + TreeObject.getDiskSize() * -1;
    	
    	pageSize -= x3;
    	
    	pageSize /= x1 + x2;
    	
    	this.t = pageSize;*/
        
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
    
    public BTree(File BTreeFile, int pageSize) {
    	this(BTreeFile, pageSize, false, 0);
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
        
        if(cacheEnabled)
	        return cache.getObject(diskAddress);
        
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
        
        if(cacheEnabled)
        	cache.append(x);

        return x;
    }
    
    public BTreeNode diskForceRead(long diskAddress) throws IOException {
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
        
        if(cacheEnabled)
        	cache.append(x);
    }
    
    /**
     * Writes a node to the disk at the specified disk offset *in the Node object).
     * @param x the Node to write
     * @throws IOException
     */
    public void diskForceWrite(BTreeNode x) throws IOException {
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
    public class BTreeNode {
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
        
        @Override
        public boolean equals(Object o) {
        	if(o.getClass().equals(this.getClass())) {
        		BTreeNode node = (BTreeNode) o;
        		
        		if(node.n != this.n)
        			return false;
        		
        		for(int i = 0; i < this.n; i++) {
        			if(node.key[i].value != this.key[i].value) {
        				break;
        			}
        		}
        		
        		return true;
        	}
        	
        	return false;
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
    
    public class Tuple {
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

    public void inOrderTraversal(BTreeNode node, int Seq, boolean toConsole) throws IOException {
      if (node == null) {
          return;
      }
      int i;
      for (i = 0; i < node.n; i++) {
          if (!node.leaf) {
              inOrderTraversal(diskRead(node.child[i]), Seq, toConsole);
          }
          if(toConsole) {
        	  System.out.print(SequenceUtils.longToDnaString(node.key[i].getValue(), Seq) + ":" + node.key[i].getFrequency()+ " ");
          }
      }
      if (!node.leaf) {
          inOrderTraversal(diskRead(node.child[i]), Seq, toConsole);
      }
  }
    
    public void writeToFile(BTreeNode node, int Seq, PrintWriter writer) throws IOException {
        if (node == null) {
            return;
        }
        int i;
        for (i = 0; i < node.n; i++) {
            if (!node.leaf) {
            	writeToFile(diskRead(node.child[i]), Seq, writer);
            }
            writer.println(SequenceUtils.longToDnaString(node.key[i].getValue(), Seq) + " " + node.key[i].getFrequency());
        }
        if (!node.leaf) {
        	writeToFile(diskRead(node.child[i]), Seq, writer);
        }
        
        writer.flush();
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
    public void split(BTreeNode x, int pos, BTreeNode y) throws IOException {
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
    final public void insertValue(BTreeNode x, long k) throws IOException {
      BTreeNode dup = duplicate(x,k);
      
      if(dup != null) {
    	  diskWrite(dup);
    	  return;
      }
      
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
    
    /*
     * This code has slight modifications from the cited website in order to match out Binary File implementation.
     *  
     * Citation: https://www.programiz.com/dsa/b-tree#:~:text=B%2Dtree%20is%20a%20special,%2Dbalanced%20m%2Dway%20tree.
     * 
     * Locate a specific BTreeNode in the BTree and return it as a tuple
     * 
     * @param x The BTreeNode to start the search from.
     * @param k The value to find.
     * 
     * @throws If something goes wrong during a disk write/read operation
     * 
     * @return A Tuple containing the BTreeNode and its index.
     */
    public Tuple search(BTreeNode x, long key) throws IOException {
        int i = 0;
        if (x == null)
          return null;
        for (i = 0; i < x.n; i++) {
          if (key < x.key[i].value) {
            break;
          }
          if (key == x.key[i].value) {
            return new Tuple(x, i);
          }
        }
        if (x.leaf) {
          return null;
        } else {
          return search(diskRead(x.child[i]), key);
        }
      }
    
    /*
     * This code has slight modifications from the cited website in order to match out Binary File implementation.
     *  
     * Citation: https://www.programiz.com/dsa/b-tree#:~:text=B%2Dtree%20is%20a%20special,%2Dbalanced%20m%2Dway%20tree.
     * 
     * Locate any potential duplicates in a BTree starting from node x increments frequency if it finds something.
     * 
     * @param x The BTreeNode to start the search from.
     * @param k The value to find.
     * 
     * @throws If something goes wrong during a disk write/read operation
     * 
     * @return The BTreeNode where the duplicate was found, null otherwise.
     */
    private BTreeNode duplicate(BTreeNode x, long key) throws IOException {
    	int i = 0;
        if (x == null) {
        	return null;
        }
        for (i = 0; i < x.n; i++) {
          if (key < x.key[i].value) {
            break;
          }
          if (key == x.key[i].value) {
              x.key[i].incrementFrequency();
        	  return x;
          }
        }
        if (x.leaf) {
          return null;
        } else {
          return duplicate(diskRead(x.child[i]), key);
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


    /*
    * An implementation of a Cache that uses a generic type working with a Java LinkedList.
    *
    * @author Ernest Coy
    */
    public class Cache extends LinkedHashMap<Long, BTreeNode> {

        private static final long serialVersionUID = 1L;
        
        /*
        * Total number of times the cache is hit when getting an object.
        */
        private int cacheHits;
        
        /*
        * Total number of times the cache is referenced.
        */
        private int cacheRef;
        
        /*
        * The maximum size of the cache. This can change the effectiveness of the cache quite a bit. Higher values may lead to poor performance.
        */
        private int maximumSize;
        
        /*
        * Constructs a fresh cache with a given maximum size.
        *
        * @param size   The maximum size of the cache.
        */
        public Cache(int size) {
        	super(size, 0.99f, true);
            this.cacheHits = 0;
            this.cacheRef = 0;
            this.maximumSize = size;
        }

        /*
        * Gets an object in the cache and pushes it to the front, makes room for the object by deleting the tail of the cache if needed (Depending on size).
        *
        * @param eq   The object to get.
        *
        * @return The object in the cache. 
        */
        public BTreeNode getObject(long address) throws IOException {
        	BTreeNode o = cache.get(address);
        	
            if (o == null) {
            	o = diskForceRead(address);
                cache.put(address, o);
            } else {
            	cache.remove(address);
                cache.put(address, o);
            }
            
            return o;
        }
        
        public void append(BTreeNode x) {
        	cache.remove(x.address);
        	cache.put(x.address, x);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, BTreeNode> eldest) {
            boolean isFull = size() > maximumSize;
            if (isFull) {
                Long key = eldest.getKey();
                BTreeNode value = eldest.getValue();
                remove(key);
            }
            return isFull;
        }

        /*
        * Converts the Cache to a helpful string of data.
        *
        * @return The resulting string.
        */
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n")
                .append(String.format("LinkedList Cache with %d entries has been created\n", maximumSize))
                .append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

            sb.append(String.format("Total number of references: %9d\n", cacheRef))
                .append(String.format("Total number of cache hits: %7d\n", cacheHits))
                .append(String.format("Cache hit ratio: %21.2f", ((double) cacheHits / (double) cacheRef) * 100) + "%")
                .append("\n");

            return sb.toString();
        }
    }

}