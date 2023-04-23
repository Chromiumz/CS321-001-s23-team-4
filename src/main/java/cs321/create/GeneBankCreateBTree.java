package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeInterface;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.List;


public class GeneBankCreateBTree
{

    public static final int MAX_SEQUENCE_LENGTH = 31;
	public static final int MAX_DEBUG_LEVEL = 1;
    private static final int defaultDegree = 128;

    private static int degree;
	private static String gbkFileName;
	private static int subsequenceLength;
	private static boolean useCache;
	private static int cacheSize;
	private static int debugLevel;


    public static void main(String[] args) throws Exception
    {
        System.out.println("Hello world from cs321.create.GeneBankCreateBTree.main");
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);

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

        System.exit(1);
    }

    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        GeneBankCreateBTreeArguments a = new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
        if (args.length < 4) {
			badUsage();
		}

        useCache =  args[0].equals("1");
		degree   = Integer.parseInt(args[1]);
		gbkFileName  = args[2];
		subsequenceLength = Integer.parseInt(args[3]);
        debugLevel = Integer.parseInt(args[5]);
        
        if (useCache){
            if(args.length == 4){
                System.out.println("no cache size supplied");
                badUsage();
            }
            cacheSize = Integer.parseInt(args[4]);
            if(!(cacheSize >= 1)){
                System.out.println("cache size must be a positive number");
                badUsage();
            }
            if(args.length == 6){
                debugLevel = Integer.parseInt(args[5]);
                if(!(debugLevel == 0 || debugLevel == 1)){
                    System.out.println("debug level must be 0 or 1");
                    badUsage();
                }
            }
        }
        else{
			if(args.length == 5){
				debugLevel = Integer.parseInt(args[4]);
				if(!(debugLevel == 0 || debugLevel == 1)){
					System.out.println("debug level must be 0 or 1");
					badUsage();
				}
			}

		    badUsage();
        }

        File gbkFileTest = new File(gbkFileName);
			if(!gbkFileTest.exists()){
				System.out.println(gbkFileName+" does not exist");
				badUsage();
			} else {
				if(!gbkFileName.contains(".gbk")){
					System.out.println("input file does contain .gbk extension");
					badUsage();
				}
			}

			if(!(degree >= 0)){
				System.out.println("degree must be >= 0");
				badUsage();
			}

			if(degree==0){
				degree = defaultDegree;
				System.out.println("using degree "+defaultDegree+" assuming disk block of 4096 bytes");
			}

			if(!(subsequenceLength >= 1 && subsequenceLength <= 31)){
				System.out.println("sequence length must be between 1 and 31");
				badUsage();
			}
        
        

       return a;
    

    }

    private static void badUsage() {
		System.err.println("Usage: java GeneBankCreateBTree <cache> <degree> <gbk file> <sequence length> [<cache size>] [<debuglevel>]");
		System.err.println("<cache>: 1 to use cache, 0 for no cache");
		System.err.println("<degree>: degree of the BTree (0 for default)");
		System.err.println("<gbk file>: file with sequence data");
		System.err.println("<sequence length>: length of a sequence (1-31)");
		System.err.println("[<cache size>]: if cache enabled, size of cache");
		System.err.println("[<debug level>]: debugging level (0-1)");
		System.exit(1);
    }

}
