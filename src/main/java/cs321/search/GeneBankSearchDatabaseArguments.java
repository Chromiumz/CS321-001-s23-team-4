package cs321.search;
import java.io.File;

public class GeneBankSearchDatabaseArguments
{
    private File databaseFile;
    private File queryFile;

    public GeneBankSearchDatabaseArguments(File databaseFile, File queryFile)
    {
        this.databaseFile = databaseFile;
        this.queryFile = queryFile;
    }

        // Getter Methods
    public File getdatabaseFile() {
        return databaseFile;
    }

    public File getqueryFile(){
        return queryFile;
    }

    // Setter Methods
    public void setdatabaseFile(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    public void setqueryFile(File queryFile) {
        this.queryFile = queryFile;
    }

    @Override
    public boolean equals(Object obj)
    {
        //this method was generated using an IDE
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        GeneBankSearchDatabaseArguments other = (GeneBankSearchDatabaseArguments) obj;
        if (databaseFile == null)
        {
            if (other.databaseFile != null)
            {
                return false;
            }
        }
        else
        {
            if (!databaseFile.equals(other.databaseFile))
            {
                return false;
            }
        }
        if (queryFile == null) {
            if (other.queryFile != null) {
                return false;
            }
        } else {
            if (!queryFile.equals(other.queryFile)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "GeneBankSearchDatabaseArguments{" +
                "databaseFile=" + databaseFile +
                ", queryFile=" + queryFile +
                '}';
    }
}
