package cs321.create;
import cs321.btree.BTree;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.List;

public class GeneBankCreateBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArguments(args);


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
