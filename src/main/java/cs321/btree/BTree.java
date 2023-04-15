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


    class BTreeNode {
        private long address;
        private TreeObject[] key;
        private long[] child;
        private boolean leaf;
        private int n;

        public BTreeNode(int t, boolean leaf, boolean onDisk) {
            this.key = new TreeObject[(2 * t) - 1];
            this.child = new long[(2 * t)];
            this.n = 0;

            if (onDisk) {
                address = nextDiskAddress;
                nextDiskAddress += nodeSize;
            }
        }

        public BTreeNode(int t, boolean leaf) {
            this(t, leaf, false);
        }

        public BTreeNode(int t) {
            this(t, true);
        }

        public int getDiskSize() {
            return
            TreeObject.getDiskSize() * key.length +
                Long.BYTES * child.length +
                Integer.BYTES +
                1;
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

}