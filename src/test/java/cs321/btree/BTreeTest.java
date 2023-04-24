package cs321.btree;

import org.junit.Test;

import cs321.btree.BTree.BTreeNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Random;
/**
 * Class for BTree unit tests
 * @author ariap
 *
 */
public class BTreeTest
{
    Random rand = new Random();
    
    /*
     * This value is the required pageSize to reach a degree 4 BTree with our current data size.
     */
    final int magicPageSizeValue = 200;
    
    
    @Test
    public void BTreeConstructor() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	try {
    	BTree newTree = new BTree(newFile, magicPageSizeValue);
    	System.out.println(newTree.getRoot());
    	if(newTree.getRoot() == null) {
			 fail("BTree is null after constructed.");
			}
    	assertEquals(4, newTree.getDegree());
    	}
    	catch (Exception e) {
    		fail(e.getMessage());
    	}
    }
   
    @Test
    public void BTreeCreate() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	
    	try {
    	newTree = new BTree(newFile, magicPageSizeValue);
    	}
    	catch (Exception e) {
    		fail(e.getMessage());
    	}
    	
    	try {
			newTree.create();
			if(newTree.getRoot() == null) {
			 fail("The root is null after create is called.");
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
    }
    	
    @Test
    public void BTreeSearch() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	
    	//element not present
    	try {
        	newTree = new BTree(newFile, magicPageSizeValue);
        	newTree.create();
        	assertNull(newTree.search(newTree.getRoot(), -10));
        	assertNull(newTree.search(newTree.getRoot(), 10));
        	}
        	catch (Exception e) {
        		fail(e.getMessage());
        	}
    	
    	//element present
    	try {
    		long gen = new Random().nextLong();
        	newTree = new BTree(newFile, 200);
        	newTree.create();
        	newTree.insert(gen);
        	assertNotNull(newTree.search(newTree.getRoot(), gen));
        	}
        	catch (Exception e) {
        		fail(e.getMessage());
        	}
    }
    
    @Test
    public void BTreeInsert() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	BTreeNode x = null;
    	
    	try {
        	newTree = new BTree(newFile, magicPageSizeValue);
        	newTree.create();
        	
        	long save = rand.nextLong();
        	
        	newTree.insert(save);
        	x = newTree.getRoot();
        	
        	assertNotNull(newTree.search(x, save));
        	
        	TreeObject[] nodeSize = x.getKeys();
        	if (nodeSize.length > newTree.getDegree()*2-1) {
        		fail("Too many keys in the node");
    		}
        	
        	long[] childCount = x.getChildrenAddresses();
        	if (!x.isLeaf() && childCount.length != (nodeSize.length+1)) {
        		fail("The amount of children nodes is incorrect");
    		}
    		}
        	catch (Exception e) {
        		fail(e.getMessage());
        	}
    }
    
    @Test
    public void BTreeInsertNonFull() {
       	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	BTreeNode x = null;
    	
    	try {
        	newTree = new BTree(newFile, magicPageSizeValue);
        	newTree.create();
        	
        	long save = rand.nextLong();
        	
        	x = newTree.getRoot();
        	newTree.insertValue(x, save);
        	
        	assertNotNull(newTree.search(x, save));
        	
        	
        	
        	TreeObject[] nodeSize = x.getKeys();
        	if (nodeSize.length > newTree.getDegree()*2-1) {
        		fail("Too many keys in the node");
    		}
        	
        	long[] childCount = x.getChildrenAddresses();
        	if (!x.isLeaf() && childCount.length != (nodeSize.length+1)) {
        		fail("The amount of children nodes is incorrect");
    		}
    		}
        	catch (Exception e) {
        		fail(e.getMessage());
        	}
    }
    
    @Test
    public void BTreeMetadata() {
    //TO-DO	
    }
    	
}
