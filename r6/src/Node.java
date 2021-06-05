package src;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import src.Token.Direction;
import src.Token.TypeToken;
import src.behavior.NodeBehavior;

public class Node {

	protected Vector<Agent> agents = new Vector<Agent>();
	protected Vector<Token> tokenList = new Vector<Token>();
	protected boolean currentlyUp = true;
	protected boolean BH = false;
	protected boolean token = false;
	private NodeInfo info;
	protected String name;

	//=============================
	//TODO: generalize this code
	private int Ps,Pn,Pw,Pe; //ports of a torus, including orientation
	protected int ring; //ring identifier
	protected String ringList; //list of nodes in the same ring
	//=============================

	private NodeBehavior behavior;


	protected Vector<Node> reachableNodes = new Vector<Node>();


	public Node(String pName) {
		this.tokenList = new Vector<Token>();
		this.name = pName;
		this.Pn=-1;
		this.Ps=-1;
		this.Pw=-1;
		this.Pe=-1;
	}

	protected void acceptAgent(Agent pAgent) {
		if (this.agents.contains(pAgent)) {
			System.err.println("ERROR! " + pAgent + " already in place.");
			return;
		}
		this.agents.addElement(pAgent);
		pAgent.setCurrentNode(this);
	}

	public void addToken(Token pToken){
		this.tokenList.addElement(pToken);
	}

	public void removeToken(Token pToken){
		if (this.tokenList.contains(pToken) == false) {
			System.err.println("ERROR! " + pToken + " is missing.");
			return;
		}
		this.tokenList.removeElement(pToken);
	}
	
	public void removeMoveTokens(String owner ){
		if(tokenList.size()>0){
			ArrayList<Token> tokenToDelete=new ArrayList<Token>();
			for(Token token: tokenList){
				if(token.owner.equals(owner)&&(token.port==Direction.East||token.port==Direction.West||token.port==Direction.North||token.port==Direction.South))
					tokenToDelete.add(token);
			}
			for(Token token: tokenToDelete){
				removeToken(token);
			}
		}
	}

	public void dumpTokenList(){
		Token nextToken =null;
		int i=0;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			i++;
			nextToken = (Token) lEnum.nextElement();
			System.out.print(" Token_"+i+"[owner="+nextToken.owner+",node="+nextToken.node+",port="+nextToken.port+",type="+nextToken.type+",altered="+nextToken.altered+",cardinality="+nextToken.cardinality+"]; ");
		}//endWhile
		if(i==0){
			System.out.print("; 0 tokens");
		}
		System.out.println(".");
	}//dumpTokenList


	public boolean getUETsignal(){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.UET){
				found=true;
			}//if
		}//while
		return found;
	}

	public boolean removeMyUET(String pName){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.UET){
				if(nextToken.owner.equals(pName)){
					this.removeToken(nextToken);
					found=true;
				}else{
					System.out.print("[Weird, I find an UET, but it's not mine :-?],");
				}
			}//if
		}//while
		return found;
	}

	public boolean getCWWTPairedPort(Direction pPort){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if(nextToken.cardinality==1){
					if(nextToken.port==pPort){
						found=true;
					}//if
				}//if
			}//if
		}//while

		return found;
	}


	public boolean AlteredCWWTOnPort(String pName){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if(!nextToken.owner.equals(pName)){
					if(nextToken.cardinality==2){
						if(nextToken.altered==true){//to avoid stealing the CWWT of our dead pair
							return true;
						}//if
					}
				}//if
			}//if
		}//while

		return found;

	}//AlteredCWWTOnPort

	public boolean OneCWWTOnPort(String pName){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if(!nextToken.owner.equals(pName)){
					if(!nextToken.port.equals("middle")){
						if(nextToken.cardinality==1){
							return true;
						}
					}
				}//if
			}//if
		}//while

		return found;

	}//OneCWWTOnPort

	public boolean StealMyAlteredCWWT(String pName){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if(nextToken.cardinality==2){
					if(nextToken.owner.equals(pName)){
						if(nextToken.altered){//to avoid stealing the BASE signal
							found=true;
							this.removeToken(nextToken);
						}//if
					}//if
				}//if
			}//if
		}//while

		return found;
	}

	public boolean StealIFTwoUnalteredTokensOnPort(){
		boolean found=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if(nextToken.cardinality==2){
					if((nextToken.port==Direction.East)||(nextToken.port==Direction.North)){//to avoid stealing the BASE signal
						if(nextToken.altered==false){//to avoid stealing the CWWT of our dead pair
							found=true;
							this.removeToken(nextToken);
						}//if
					}
				}//if
			}//if
		}//while

		return found;
	}

	public boolean getBASEsignal(){
		boolean signal=false;
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				if((nextToken.port.equals("middle"))){
					signal=true;
					this.removeToken(nextToken);
				}
			}//if
		}//while
		return signal;
	}



	public int containsOtherCWWT(String pOwner){
		//System.out.println("\nNODE["+this.name+"]::Verifying if there is the CWWT of an agent who is not agent["+pOwner+"];");
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				//System.out.println("\nNODE["+this.name+"]::I do have a CWWT");
				if(nextToken.owner.equals(pOwner)){
					//System.out.println("\nNODE["+this.name+"]::I have the CWWT of agent["+nextToken.owner+"]");
					return 0;
				}else{
					//System.out.println("\nNODE["+this.name+"]::I have the CWWT of agent["+nextToken.owner+"]");
					nextToken.altered=true;//since an agent called this method, and has found the CWWT of another token,
					//the token gets automatically altered
					if(nextToken.port==Direction.North){
						return 1;
					}else if(nextToken.port==Direction.East){
						return 2;
					}else{
						return 0;
					}
					//System.out.println("\nNODE["+this.name+"]:: else 1");
				}
			}else{
				//System.out.println("\nNODE["+this.name+"]:: else 2");
				return 0;
			}
		}//endWhile
		return 0;
	}


	//returns:
	//0 if it does not contain the CWWT;
	//1 it it contains the CWWT unaltered;
	//2 if it contains the CWWT, but altered.
	public int containsMyCWWT(String pOwner){
		//System.out.println("\nNODE["+this.name+"]::Verifying if have in my list of tokens, the CWWT of agent["+pOwner+"];");
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				//System.out.println("\nNODE["+this.name+"]::I do have a CWWT");
				if(nextToken.owner.equals(pOwner)){
					if(nextToken.altered){
						return 2;//altered
					}else{
						return 1;//unaltered
					}
				}else{
					return 0;
					//System.out.println("\nNODE["+this.name+"]:: else 1");
				}
			}else{
				//System.out.println("\nNODE["+this.name+"]:: else 2");
			}
		}//endWhile
		return 0;
	}

	public Token getMyPairedCWWT(String pOwner){
		//System.out.println("\nNODE["+this.name+"]::Verifying if have in my list of tokens, the CWWT of agent["+pOwner+"];");
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				//System.out.println("\nNODE["+this.name+"]::I do have a CWWT");
				if(nextToken.owner.equals(pOwner)){
					//System.out.println("\nNODE["+this.name+"]::I have the CWWT of agent["+nextToken.owner+"]");
					if(nextToken.cardinality==1){
						return nextToken;
					}else{
						System.out.println("\nNODE["+this.name+"]::I do see a CWWT at previous node, but cardinality is not 1");
					}
				}else{
					//System.out.println("\nNODE["+this.name+"]:: else 1");
				}
			}else{
				//System.out.println("\nNODE["+this.name+"]:: else 2");
			}
		}//endWhile
		return null;
	}


	public Token getMyCWWT(String pOwner){
		//System.out.println("\nNODE["+this.name+"]::Verifying if have in my list of tokens, the CWWT of agent["+pOwner+"];");
		Token nextToken =null;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()){
			nextToken = (Token) lEnum.nextElement();
			if(nextToken.type==TypeToken.CWWT){
				//System.out.println("\nNODE["+this.name+"]::I do have a CWWT");
				if(nextToken.owner.equals(pOwner)){
					//System.out.println("\nNODE["+this.name+"]::I have the CWWT of agent["+nextToken.owner+"]");
					return nextToken;
				}else{
					//System.out.println("\nNODE["+this.name+"]:: else 1");
				}
			}else{
				//System.out.println("\nNODE["+this.name+"]:: else 2");
			}
		}//endWhile
		return null;
	}

	public void clearConnections() {
		this.reachableNodes = new Vector<Node>();
	}

	public void connectTo(Node pNode) {
		if (this.reachableNodes.contains(pNode) == false)
			this.reachableNodes.addElement(pNode);
	}

	public void connectToRing(int ring) {
		this.ring=ring;
	}

	public void dumpStateNS3() {
		//		Enumeration<Node> lEnum = this.reachableNodes.elements();
		Point3d p=this.getPosition();
		//		positionAlloc->Add (Vector (250.01,250.01, 0.0));
		System.out.print("positionAlloc->Add (Vector ("+p.x+","+p.y+","+p.z+"))\n");

	}

	public void dumpStateNeato() {
		Enumeration<Node> lEnum = this.reachableNodes.elements();
		while (lEnum.hasMoreElements()) {
			System.out.print(this.name+" -- "+((Node) lEnum.nextElement()).getName()+"\n");
		}// while
	}

	public void dumpState() {
		//System.out.print("\\--node "+this.name+", " + ((this.currentlyUp) ? "running " : "stopped. ") + "at ring"+this.ring+" has " + this.agents.size() + " agents and " + this.reachableNodes.size() + " reachable nodes. ");
		//System.out.print("--N["+this.name+"] (ring "+this.ring+") has " + this.agents.size() + " agents. It is connected to:[");
		System.out.print("--N["+this.name+"] has " + this.agents.size() + " agents.");

		// Enumeration<Node> lEnum = this.reachableNodes.elements();
		//             while (lEnum.hasMoreElements()) {
		//                 System.out.print(((Node) lEnum.nextElement()).getName() + " ");
		//             }// while
		//             System.out.print("];");
		//System.out.print("];Ports(S,N,W,E)={"+this.Ps+","+this.Pn+","+this.Pw+","+this.Pe+"}");
		//System.out.println("Ports(N,S)=["+this.Pn+","+this.Ps+"]");
	}



	public Vector<Agent> getAgents() {
		return this.agents;
	}

	public Vector<Token> getTokenList() {
		return this.tokenList;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public String getName() {
		return this.name;
	}

	public Point3d getPosition() {
		return info.getPosition();
	}

	public Vector<Node> getReachableNodes() {
		return this.reachableNodes;
	}

	public int howManyKnowMe(Vector<Agent> pVector) {
		int i = 0;
		Enumeration<Agent> lEnum = pVector.elements();
		while (lEnum.hasMoreElements()) {
			Agent lAgent = lEnum.nextElement();
			if (lAgent.believesNode(this)){
				i++;
			}
		}
		return i;
	}

	public int howManyTokens() {
		int i = 0;
		Enumeration<Token> lEnum = this.tokenList.elements();
		while (lEnum.hasMoreElements()) {
			i++;
		}
		return i;
	}

	public boolean moveAgentTo(Agent pAgent, Node pNode) {
		if ((this.agents.contains(pAgent)) && (this.reachableNodes.contains(pNode))) {
			removeAgent(pAgent);
			pNode.acceptAgent(pAgent);
			return true;
		}
		return false;
	}

	public void removeAgent(Agent pAgent) {
		if (this.agents.contains(pAgent) == false) {
			System.err.println("ERROR! " + pAgent + " is missing.");
			return;
		}
		this.agents.removeElement(pAgent);
	}


	public void setInfo(NodeInfo info) {
		this.info = info;
	}

	public void setPosition(double x, double y, double z) {
		info.setPosition(new Point3d(x, y, z));
	}

	public void setNorthPort(int nodeID) {
		this.Pn=nodeID;
	}

	public void setSouthPort(int nodeID) {
		this.Ps=nodeID;
	}

	public void setWestPort(int nodeID) {
		this.Pw=nodeID;
	}

	public void setEastPort(int nodeID) {
		this.Pe=nodeID;
	}

	public int getNorthPort() {
		return(this.Pn);
	}

	public int getSouthPort() {
		return(this.Ps);
	}

	public int getWestPort() {
		return(this.Pw);
	}

	public int getEastPort() {
		return(this.Pe);
	}

	public void setPosition(Point3d position) {
		info.setPosition(position);
	}

	public void setRingList(String list) {
		this.ringList=list;
	}

	public String getRingList() {
		return this.ringList;
	}

	public String toString() {
		String ret="Node:" +
				"\n\tName: "+this.name+
				"\n\tNodeInfo: "+this.info+
				"\n\tToken: "+this.token+
				"\n\tBH: "+this.BH+
				"\n\tCurrenlyUP: "+this.currentlyUp+
				"\n\ttokenList: ";
		for (Token token : tokenList) {
			ret+=token.node+", ";
		}

		ret+="\n\tagents: ";
		for (Agent agent : agents) {
			ret+=agent.name+", ";
		}
		ret+="\n\tPs:"+Ps;
		ret+="\n\tPn:"+Pn;
		ret+="\n\tPw:"+Pw;
		ret+="\n\tPe:"+Pe;
		ret+="\n\tRing:"+ring;
		ret+="\n\treachableNodes:";
		for (Node node : reachableNodes) {
			ret+=node.name+", ";
		}


		return ret;
	}
	public NodeBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(NodeBehavior behavior) {
		this.behavior = behavior;
	}

	public void setBH() {
		this.BH = true;
	}

	public boolean getBH() {
		return this.BH;
	}

	public void setToken(boolean token){//3d
		this.token = token;
	}

	public boolean getToken(){//3d
		return this.token;
	}

	public void changeColorNode(Color3f color){
		if(behavior!=null){
			behavior.setColor(color);
			behavior.postId(NodeBehavior.ID);
		}
	}
	public void printArrayVisitedNodes(){
		System.out.println();
		int count=0;
		for (Node node : reachableNodes) {
			System.out.print(node.info.knownBy+"\t");
			if(count%5==0){
				System.out.println();
			}

		}
		System.out.println();
	}
	
	public Boolean isVisited(){
		for(Token token:getTokenList()){
			if(token.port==Direction.Done)
				return true;
		}
		return false;
		
	}
	
	public int getMiddleTokensNumber(){
		int ret=0;
		for(Token token:getTokenList()){
			if(token.port==Direction.Middle)
				ret++;
		}
		return ret;
		
	}
}
