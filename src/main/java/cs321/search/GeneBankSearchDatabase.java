package cs321.search;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import java.io.File;

public class GeneBankSearchDatabase
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchDatabaseArguments cmdArgs = parseArgumentsAndHandleExceptions(args);

        File databaseFile = cmdArgs.getdatabaseFile();
        File queryFile = cmdArgs.getqueryFile();


    }

    private static GeneBankSearchDatabaseArguments parseArgumentsAndHandleExceptions(String[] args)
    {
        GeneBankSearchDatabaseArguments geneBankSearchDatabaseArguments = null;
        try
        {
            geneBankSearchDatabaseArguments = parseArguments(args);
            System.out.println("Success");
        }
        catch (ParseArgumentException e)
        {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchDatabaseArguments;
    }

    private static void printUsageAndExit(String errorMessage)
    {
        System.err.println("java -jar build/libs/GeneBankSearchDatabase.jar --database=<SQLite-database-path>"
        + " --queryfile=<query-file>");
        System.exit(1);
    }

    public static GeneBankSearchDatabaseArguments parseArguments(String[] args) throws ParseArgumentException
    {
        if (args.length < 2) {
            throw new ParseArgumentException("Not enough arguments");
        }

        File databaseFile = null;
        File queryFile = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--database=")) {
                databaseFile = new File(arg.substring(12));
            } else if (arg.startsWith("--queryfile=")) {
                queryFile = new File(arg.substring(12));
            } else {
                throw new ParseArgumentException("Invalid argument: " + arg);
            }
        }

        return new GeneBankSearchDatabaseArguments(databaseFile, queryFile);
    }

}
