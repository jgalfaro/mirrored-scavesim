package src;

public class Token{
	
	public enum TypeToken{
		CWWT,UET
	}
	
	public enum Direction {
		North,East,South,West,Done,Middle
	}

    public String node = null;
    public Direction port = null;
    public String owner = null;
    public TypeToken type = null;
    public int cardinality = 0;
    public boolean altered = false;

    public Token(String pOwner, String pNode, Direction pPort, TypeToken pType, boolean pAltered, int pCardi){
        this.node=pNode;
        this.port=pPort;
        this.owner=pOwner;
        this.type=pType;
        this.altered=pAltered;
        this.cardinality=pCardi;
    }

}//end-class-token
