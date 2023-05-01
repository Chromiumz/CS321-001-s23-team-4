package cs321.search;

import java.io.File;

public class GeneBankSearchBTreeArguments
{
    private boolean useCache;
    private int degree;
    private File btreeFile;
    private int subsequenceLength;
    private File queryFile;
    private int cacheSize;
    private int debugLevel;

    public GeneBankSearchBTreeArguments(boolean useCache, int degree, File btreeFile, int subsequenceLength, File queryFile, int cacheSize, int debugLevel)
    {
        this.useCache = useCache;
        this.degree = degree;
        this.btreeFile = btreeFile;
        this.subsequenceLength = subsequenceLength;
        this.queryFile = queryFile;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
    }

        // Getter Methods
    public boolean isUseCache() {
        return useCache;
    }

    public int getDegree() {
        return degree;
    }

    public File getbtreeFile() {
        return btreeFile;
    }

    public int getSubsequenceLength() {
        return subsequenceLength;
    }
    public File getqueryFile(){
        return queryFile;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    // Setter Methods
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void setbtreeFile(File btreeFile) {
        this.btreeFile = btreeFile;
    }

    public void setSubsequenceLength(int subsequenceLength) {
        this.subsequenceLength = subsequenceLength;
    }

    public void setqueryFile(File queryFile) {
        this.queryFile = queryFile;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
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
        GeneBankSearchBTreeArguments other = (GeneBankSearchBTreeArguments) obj;
        if (cacheSize != other.cacheSize)
        {
            return false;
        }
        if (debugLevel != other.debugLevel)
        {
            return false;
        }
        if (degree != other.degree)
        {
            return false;
        }
        if (btreeFile == null)
        {
            if (other.btreeFile != null)
            {
                return false;
            }
        }
        else
        {
            if (!btreeFile.equals(other.btreeFile))
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
        if (subsequenceLength != other.subsequenceLength)
        {
            return false;
        }
        if (useCache != other.useCache)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        //this method was generated using an IDE
        return "GeneBankCreateBTreeArguments{" +
                "useCache=" + useCache +
                ", degree=" + degree +
                ", btreeFile='" + btreeFile + '\'' +
                ", subsequenceLength=" + subsequenceLength +
                ", queryFile=" + queryFile +
                ",cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                '}';
    }


}
