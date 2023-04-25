package cs321.create;
import cs321.btree.BTree;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.Arrays;
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
        File bTreeFile = new File("myBTree");
        if (bTreeFile.exists()) {
            bTreeFile.delete();
        }

        // Initialize BTree
        BTree bTree = new BTree(new File("myBTree"), degree);
        
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
		    List<String> filteredList = Arrays.stream(x)
                    .filter(str -> !str.isEmpty() && !str.matches(".*\\d.*"))
                    .collect(Collectors.toList());
                    for (String subsequence : filteredList) {
                        // Check if subsequence contains any 'N's
                        if (subsequence.indexOf('N') == -1) {
                            // Generate all subsequences of length k
                            for (int i = 0; i <= subsequence.length() - sequenceLength; i++) {
                                String dnaSubsequence = subsequence.substring(i, i + sequenceLength);
                                System.out.println(dnaSubsequence);
                                // Convert DNA subsequence to long key
                                //long key = SequenceUtils.dnaStringToLong(dnaSubsequence);
                                // Insert key into BTree
                                //System.out.println(key);
                            }
                        }
                    }
		    System.out.println(Arrays.toString(filteredList.toArray()));
		    System.out.println("S");
		}

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
            
        boolean useCache = args[0].equals("1");
        int degree = Integer.parseInt(args[1]);
        String gbkFileName = args[2];
        int subsequenceLength = Integer.parseInt(args[3]);
        int cacheSize = useCache && args.length > 4 ? Integer.parseInt(args[4]) : 0;
        int debugLevel = args.length > 5 ? Integer.parseInt(args[5]) : 0;

        if (args.length < 4) {
            throw new ParseArgumentException("Not enough arguments");
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
    
        if (debugLevel < 0 || debugLevel > 1) {
            throw new ParseArgumentException("Debug level must be between 0 and 1");
        }
    
        return new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
    }

}
