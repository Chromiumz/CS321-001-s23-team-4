package cs321.create;
import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneBankCreateBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankCreateBTreeArguments cmdArgs = parseArgumentsAndHandleExceptions(args);

        int degree = cmdArgs.getDegree();
        int sequenceLength = cmdArgs.getSubsequenceLength();
        String gbkFileName = cmdArgs.getGbkFileName();
        boolean useCache = cmdArgs.isUseCache();
        int cacheSize = cmdArgs.getCacheSize();
        int debugLevel = cmdArgs.getDebugLevel();

        // Create BTree file, if the file exists delete it to make it easier to re-run test
        File bTreeFile = new File("btreeData/"+gbkFileName + ".btree.data." + sequenceLength + "." + degree);
        if (bTreeFile.exists()) {
            bTreeFile.delete();
        }

        // Initialize BTree
        BTree bTree = new BTree(new File("btreeData/"+gbkFileName + ".btree.data." + sequenceLength + "." + degree), degree, useCache, cacheSize);
        bTree.create();
        
        
        // Setup debug levels
        if (debugLevel == 0){
            // print diagnostic messages, help and status messages on standard error stream
            System.err.println("Starting program...");
            System.err.println("Degree: " + degree);
            System.err.println("Subsequence length: " + sequenceLength);
            System.err.println("Use cache: " + useCache);
            System.err.println("Cache size: " + cacheSize);
            System.err.println("Debug level: " + debugLevel);
        }else if (debugLevel == 1){

        }

        StringBuilder sequenceBuilder = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(new FileReader(gbkFileName))) {

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sequenceBuilder.append(currentLine).append("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputString = sequenceBuilder.toString();

        Pattern pattern = Pattern.compile("ORIGIN([\\s\\S]*?)\\/\\/");
		Matcher matcher = pattern.matcher(inputString);
	
		while (matcher.find()) {
		    String sequence = matcher.group(1);
		    String[] x = sequence.split("\\s+");
		    
		    //System.out.println(sequence);
		    
		    List<String> filteredList = Arrays.stream(x)
                    .filter(str -> !str.isEmpty() && !str.matches(".*\\d.*"))
                    .collect(Collectors.toList());
		    
		    StringBuilder compile = new StringBuilder();

		    for(String s : filteredList) {
		    	compile.append(s);
		    }
		    
            Pattern subSeqPattern = Pattern.compile("(?=([atcg]{"+sequenceLength+"}))");
            Matcher subSeq = subSeqPattern.matcher(compile.toString());
            
            while (subSeq.find()) {
            		String match = subSeq.group(1);
                	long key = SequenceUtils.dnaStringToLong(match);
                	bTree.insert(key);
            	}
			}
			
            bTree.inOrderTraversal(bTree.getRoot(), sequenceLength, false);
		}

    private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args)
    {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
        try
        {
            geneBankCreateBTreeArguments = parseArguments(args);
        }
        catch (ParseArgumentException e)
        {
            printUsageAndExit(e.getMessage());
        }
        return geneBankCreateBTreeArguments;
    }

    private static void printUsageAndExit(String errorMessage)
    {
        System.err.println("java -jar build/libs/GeneBankCreateBTree.jar --cache=<0|1>  --degree=<btree-degree> --gbkfile=<gbk-file> --length=<sequence-length> [--cachesize=<n>] [--debug=0|1]");
        System.exit(1);
    }

    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        if (args.length < 4) {
            throw new ParseArgumentException("Not enough arguments");
        }
            
        boolean useCache = false;
        int degree = 0;
        String gbkFileName = null;
        int subsequenceLength = 0;
        int cacheSize = 0;
        int debugLevel = 0;
        boolean cacheSizeSpecified = false; // track if cacheSize was specified

        for (String arg : args) {
            if (arg.startsWith("--cache=")) {
                useCache = arg.substring(8).equals("1");
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--gbkfile=")) {
                gbkFileName = arg.substring(10);
            } else if (arg.startsWith("--length=")) {
                subsequenceLength = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--cachesize=")) {
                cacheSize = Integer.parseInt(arg.substring(12));
                if (cacheSize < 100 || cacheSize > 10000){
                    throw new ParseArgumentException("CacheSize must be between 100 - 10000");
                }
                cacheSizeSpecified = true;
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring(8));
            } else {
                throw new ParseArgumentException("Invalid argument: " + arg);
            }
        }

        if (useCache && !cacheSizeSpecified) {
            throw new ParseArgumentException("Cache size is required when cache is enabled");
        }

        if (!gbkFileName.endsWith(".gbk")) {
            throw new ParseArgumentException("Input file must have .gbk extension");
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
        

        return new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
    }

}
