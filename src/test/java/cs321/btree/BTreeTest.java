package cs321.btree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
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
    	newTree =	new BTree(newFile, 4);
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
    public void BTreeInsert() {
    //TO-DO
    }
    
    @Test
    public void BTreeMetadata() {
    //TO-DO	
    }
    	
}
