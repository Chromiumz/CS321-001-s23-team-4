package cs321.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cs321.btree.BTree;
import cs321.btree.BTree.Tuple;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.SequenceUtils;

public class GeneBankSearchBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchBTreeArguments cmdArgs = parseArgumentsAndHandleExceptions(args);

        int degree = cmdArgs.getDegree();
        int sequenceLength = cmdArgs.getSubsequenceLength();
        File btreeFile = cmdArgs.getbtreeFile();
        boolean useCache = cmdArgs.isUseCache();
        File queryFile = cmdArgs.getqueryFile();
        int cacheSize = cmdArgs.getCacheSize();
        int debugLevel = cmdArgs.getDebugLevel();

        BTree bTree = new BTree(btreeFile, degree, useCache, cacheSize);

        BufferedReader reader = new BufferedReader(new FileReader(queryFile));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
        	int total = 0;
        	long sequence = SequenceUtils.dnaStringToLong(line);
        	
        	Tuple n1 = bTree.search(bTree.getRoot(), sequence);
        	Tuple n2 = bTree.search(bTree.getRoot(), SequenceUtils.getComplement(sequence, sequenceLength));
        	
        	if(n1 != null)
        		total += n1.getNode().getKey(n1.getIndex()).getFrequency();
        	if(n2 != null)
        		total += n2.getNode().getKey(n2.getIndex()).getFrequency();
        	
        	sb.append(String.format("%s %d%n", line, total));
        }
        
        System.out.println(sb.toString());
        
        reader.close();

    }

    private static GeneBankSearchBTreeArguments parseArgumentsAndHandleExceptions(String[] args)
    {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = null;
        try
        {
            geneBankSearchBTreeArguments = parseArguments(args);
            System.out.println("Success");
        }
        catch (ParseArgumentException e)
        {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchBTreeArguments;
    }

    private static void printUsageAndExit(String errorMessage)
    {
        System.err.println("java -jar build/libs/GeneBankSearchBTree.jar --cache=<0/1> --degree=<btree-degree>"
        + " --btreefile=<b-tree-file> --length=<sequence-length> --queryfile=<query-file>" 
        + " [--cachesize=<n>] [--debug=0|1]");
        System.err.println(errorMessage);
        System.exit(1);
    }

    public static GeneBankSearchBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        if (args.length < 5) {
            throw new ParseArgumentException("Not enough arguments");
        }
            
        boolean useCache = false;
        int degree = 0;
        File btreeFile = null;
        int subsequenceLength = 0;
        File queryFile = null;
        int cacheSize = 0;
        int debugLevel = 0;
        boolean cacheSizeSpecified = false; // track if cacheSize was specified

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--cache=")) {
                useCache = arg.substring(8).equals("1");
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--btreefile=")) {
                btreeFile = new File(arg.substring(12));
            } else if (arg.startsWith("--length=")) {
                subsequenceLength = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--queryfile=")) {
                queryFile = new File(arg.substring(12));
            } else if (arg.startsWith("--cachesize=")) {
                cacheSize = Integer.parseInt(arg.substring(12));
                if (useCache && (cacheSize < 100 || cacheSize > 10000)) {
                    throw new ParseArgumentException("CacheSize must be between 100 - 10000");
                }
                cacheSizeSpecified = true;
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring(8));
            } else {
                throw new ParseArgumentException("Invalid argument: " + arg);
            }
        }
    
        if (useCache == true && !cacheSizeSpecified) {
            throw new ParseArgumentException("Cache size is required when cache is enabled");
        }
    
        if (degree < 0) {
            throw new ParseArgumentException("Degree must be >= 0");
        }
    
        if (degree == 0) {
            degree = 128;
        }
    
        if (subsequenceLength < 1 || subsequenceLength > 31) {
            throw new ParseArgumentException("Sequence length must be between 1 and 31");
        }
    
        // Set cacheSize to 0 if useCache is false
        if (!useCache) {
            cacheSize = 0;
        }
    
        return new GeneBankSearchBTreeArguments(useCache, degree, btreeFile, subsequenceLength, queryFile, cacheSize, debugLevel);
    }
}

