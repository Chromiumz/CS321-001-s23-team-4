package cs321.search;

import cs321.btree.BTree;
import cs321.btree.BTree.Tuple;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.SequenceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GeneBankSearchDatabase
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchDatabaseArguments cmdArgs = parseArgumentsAndHandleExceptions(args);

        File databaseFile = cmdArgs.getdatabaseFile();
        File queryFile = cmdArgs.getqueryFile();

        Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFile.getPath()));
        Statement statement = connection.createStatement();
        
        BufferedReader reader = new BufferedReader(new FileReader(queryFile));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
        	int total = 0;
        	line = line.replaceAll("\n", "").toLowerCase();
        	long sequence = SequenceUtils.dnaStringToLong(line);
        	
        	ResultSet rs = statement.executeQuery(String.format("SELECT * FROM btree WHERE sequence = '%s' OR sequence = '%s'", 
        			line,
        			SequenceUtils.longToDnaString(SequenceUtils.getComplement(sequence, line.length()), line.length())));
        	
        	while(rs.next())
        		total += rs.getInt("frequency");
        	
        	sb.append(String.format("%s %d%n", line, total));
        }
        
        System.out.println(sb.toString());
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
                databaseFile = new File(arg.substring(11));
            } else if (arg.startsWith("--queryfile=")) {
                queryFile = new File(arg.substring(12));
            } else {
                throw new ParseArgumentException("Invalid argument: " + arg);
            }
        }

        return new GeneBankSearchDatabaseArguments(databaseFile, queryFile);
    }

}
