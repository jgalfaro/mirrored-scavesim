package src;
import java.util.Enumeration;
import java.util.Hashtable;

public class Token{
    public String node = null;
    public String port = null;
    public String owner = null;
    public String type = null;
    public int cardinality = 0;
    public boolean altered = false;

    public Token(String pOwner, String pNode, String pPort, String pType, boolean pAltered, int pCardi){
        this.node=pNode;
        this.port=pPort;
        this.owner=pOwner;
        this.type=pType;
        this.altered=pAltered;
        this.cardinality=pCardi;
    }

}//end-class-token
