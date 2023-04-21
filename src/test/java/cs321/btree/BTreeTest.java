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
    // HINT:
    //  instead of checking all intermediate states of constructing a tree
    //  you can check the final state of the tree and
    //  assert that the constructed tree has the expected number of nodes and
    //  assert that some (or all) of the nodes have the expected values
    @Test
    public void btreeDegree4Test()
    {
//        //TODO instantiate and populate a bTree object
//        int expectedNumberOfNodes = TBD;
//
//        // it is expected that these nodes values will appear in the tree when
//        // using a level traversal (i.e., root, then level 1 from left to right, then
//        // level 2 from left to right, etc.)
//        String[] expectedNodesContent = new String[]{
//                "TBD, TBD",      //root content
//                "TBD",           //first child of root content
//                "TBD, TBD, TBD", //second child of root content
//        };
//
//        assertEquals(expectedNumberOfNodes, bTree.getNumberOfNodes());
//        for (int indexNode = 0; indexNode < expectedNumberOfNodes; indexNode++)
//        {
//            // root has indexNode=0,
//            // first child of root has indexNode=1,
//            // second child of root has indexNode=2, and so on.
//            assertEquals(expectedNodesContent[indexNode], bTree.getArrayOfNodeContentsForNodeIndex(indexNode).toString());
//        }
    }
    Random rand = new Random();
    
    @Test
    public void BTreeConstructor() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	try {
    	BTree newTree = new BTree(newFile, 4);
    	if(newTree.getRoot() != null) {
			 fail("BTree is not null after constructed.");
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
    	newTree = new BTree(newFile, 4);
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
        	newTree = new BTree(newFile, 4);
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
        	newTree = new BTree(newFile, 4);
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
        	newTree = new BTree(newFile, 4);
        	newTree.create();
        	newTree.insert(rand.nextLong());
        	x = newTree.getRoot();
        	
        	assertNotNull(newTree.search(x, rand.nextLong()));
        	
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
        	newTree = new BTree(newFile, 4);
        	newTree.create();
        	newTree.insertNonfull(x, rand.nextLong());
        	x = newTree.getRoot();
        	
        	assertNotNull(newTree.search(x, rand.nextLong()));
        	
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
    public void BTreeSplitRoot() {
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	
    	try {
        	newTree = new BTree(newFile, 4);
        	newTree.create();
        	
        	for (int i = 0; i < 6; i ++) {
        		newTree.insert(rand.nextLong());
        	}
        	
        	int size = newTree.getNodeSize();
        	
        	assertNotNull(newTree.splitRoot());
        	
        	//make sure the resulting children have the right number of keys
        	BTreeNode[] newArr = newTree.getRoot().getAllChildBTreeNode();
        	for (int i = 0; i < newArr.length; i ++) {
        		assertNotNull(newArr[i]);
        		if ((newArr[i].getKeys().length != (size-1)/2 || newArr[i].getKeys().length != (size-1)-(size-1)/2) && newArr[i].getKeys().length < size) {
        			fail("Amount of keys in the new child nodes is incorrect");
        		}
        	}
    
    	}
    	catch (Exception e) {
    		fail(e.getMessage());
    	}
    }
    
    @Test
    public void BTreeSplitChild() {
    	
    	File newFile = new File("ConstructorTest");
    	if(newFile.exists()) {
    		newFile.delete();
    	}
    	
    	BTree newTree = null;
    	
    	try {
        	newTree = new BTree(newFile, 4);
        	newTree.create();
        	
        	for (int i = 0; i < 6; i ++) {
        		newTree.insert(rand.nextLong());
        	}
        	
        	int size = newTree.getNodeSize();
        	
        	BTreeNode child = newTree.getRoot().getChildBTreeNode(1);
        	
        	newTree.splitChild(newTree.getRoot(), 1);
        	assertNotNull(child);
        	
        	//make sure the resulting children have the right number of keys
        	BTreeNode[] newArr = child.getAllChildBTreeNode();
        	for (int i = 0; i < newArr.length; i ++) {
        		assertNotNull(newArr[i]);
        		if ((newArr[i].getKeys().length != (size-1)/2 || newArr[i].getKeys().length != (size-1)-(size-1)/2) && newArr[i].getKeys().length < size) {
        			fail("Amount of keys in the new child nodes is incorrect");
        		}
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
