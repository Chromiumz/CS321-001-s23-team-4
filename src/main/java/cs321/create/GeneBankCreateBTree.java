package cs321.create;
import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        File bTreeFile = new File(gbkFileName + ".btree.data." + sequenceLength + "." + degree);
        if (bTreeFile.exists()) {
            bTreeFile.delete();
        }

        // Initialize BTree
        BTree bTree = new BTree(new File(gbkFileName + ".btree.data." + sequenceLength + "." + degree), degree, useCache, cacheSize);
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
		if(debugLevel == 1)
			bTree.writeToFile(bTree.getRoot(), sequenceLength, new PrintWriter(new File("results/ourDump/"+gbkFileName+".dump."+sequenceLength)));
		
		Connection connection = null;
		
		
		System.out.println(bTree.diskForceRead(bTree.getRoot().getAddress()).toJSONData());
		
		try {
			String basePath = "results/ourDatabase";
			String dbName = gbkFileName + "." + sequenceLength + ".db";
			connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/%s", basePath, dbName));
			
			Statement statement = connection.createStatement();
			
			statement.executeUpdate("drop table if exists btree");
	        statement.executeUpdate("create table btree (sequence string, frequency integer)");
			
	        bTree.writeToDatabase(bTree.getRoot(), sequenceLength, statement);
	        
	        /*ResultSet rs = statement.executeQuery("select * from btree");
			while(rs.next())
			{
			// read the result set
				System.out.println(rs.getString("sequence") + " " + rs.getInt("frequency"));
			}*/
	        
			connection.close();
			//System.out.println("Closed");
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		System.out.println(bTree.diskForceRead(9424).toJSONData());
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

        if (useCache  == true && !cacheSizeSpecified) {
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
            // Set cacheSize to 0 if useCache is false
        if (!useCache) {
            cacheSize = 0;
        }
        

        return new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
    }

}
