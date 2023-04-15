package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class BTree implements BTreeInterface {
    private int METADATA_SIZE = Long.BYTES + Integer.BYTES;
    private long nextDiskAddress = METADATA_SIZE;
    private FileChannel file;
    private ByteBuffer buffer;
    private int nodeSize;

    private long rootAddress = METADATA_SIZE; // offset to the root node

    private BTreeNode root;
    private int t = -1;

    public BTree(File BTreeFile, int t) {
        this.t = t;
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
            if (value != 0 && frequency != 0) {
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

        for (int i = 0; i < (2 * t) - 1; i++) {
            child[i] = buffer.getLong();
        }

        BTreeNode x = new BTreeNode(t, leaf, false);

        x.key = keys;
        x.child = child;
        x.n = n;

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
                buffer.putLong(0);
                buffer.putLong(0);
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
        	if(x >= n)
        		return null;
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
        		keyJSON += String.format("\t\t{\n\t\t\tvalue: %d\n\t\t\tfrequency: %d\n\t\t},\n", key[i].getValue(), key[i].getFrequency());
        	}
        	
        	String childJSON = "";
        	for(int i = 0; i < child.length; i++) {
        		if(i >= n) {
        			continue;
        		}
        		childJSON += String.format("\t\t{\n\t\t\taddress: %d\n\t\t},\n", child[i]);
        	}
        	
        	String resolve = String.format("{\n"
        			+ "\taddress: %d,\n"
        			+ "\tdiskSize: %d,\n"
        			+ "\tdegree: %d,\n"
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

    @Override
    public Tuple search(BTreeNode x, long k) throws IOException {
        int i = 0;
        while(i < x.n && k > x.key[i].value) {
        	i = i + 1;
        }
        if(i < x.n && k == x.key[i].value) {
        	return new Tuple(x, i);
        } else if (x.leaf) {
        	return null;
        } else {
        	return search(diskRead(x.child[i]),i);
        }
    }

    @Override
    public void create() throws IOException {
        root = new BTreeNode(t, true, true);
        
		diskWrite(root);
    }

    @Override
    public void insert(long k) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public BTreeNode splitRoot() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void splitChild(BTreeNode x, int i) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertNonfull(BTreeNode x, long k) throws IOException {
        // TODO Auto-generated method stub

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