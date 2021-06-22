package src;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Record{

	boolean printOutData;
	public double missingNodes;
	public int numBH;
	public int numAgents=0;
	public int numNodes=0;
	public int numSlices=0;
	public int numNodesNS=0;
	public int numNodesEW=0;
	public byte phaseA1=0;
	public byte phaseA2=0;
	public String missingNodeName=null;
	public Vector<String> initialNodeName= new Vector<String>();
	public String lastInBaseA1=null;
	public String lastInBaseA2=null;


	public void setNumNodesNS(int pNum){
		this.numNodesNS=pNum;
	}

	public void setNumNodesEW(int pNum){
		this.numNodesEW=pNum;
	}

	public int getNumNodesNS(){
		return this.numNodesNS;
	}

	public int getNumNodesEW(){
		return this.numNodesEW;
	}


	public byte getPhaseA1(){
		return this.phaseA1;
	}

	public byte getPhaseA2(){
		return this.phaseA2;
	}

	public void setPhaseA1(byte pPhase){
		this.phaseA1=pPhase;
	}

	public void setPhaseA2(byte pPhase){
		this.phaseA2=pPhase;
	}

	public void setLastInBaseA1(String pName){
		this.lastInBaseA1=pName;
	}

	public void setLastInBaseA2(String pName){
		this.lastInBaseA2=pName;
	}

	public String getLastInBaseA1(){
		return this.lastInBaseA1;
	}

	public String getLastInBaseA2(){
		return this.lastInBaseA2;
	}


	public int getNumAgents(){
		return this.numAgents;
	}

	public void setNumAgents(int pNum){
		this.numAgents=pNum;
	}

	public int getNumNodes(){
		return this.numNodes;
	}

	public void setNumNodes(int pNum){
		this.numNodes=pNum;
	}


	public int getNumSlices(){
		return this.numSlices;
	}

	public void setNumSlices(double pNum){
		this.numSlices=(int) pNum;
	}



	public Record(boolean paramBoolean, int pnumBH){
		this.printOutData = paramBoolean;
		this.numBH=pnumBH;
	}

	public void setInputs(Vector<Node> nodeList, Hashtable<Edge, Boolean> edgeList, Vector<Agent> agentList){
		Simulator.get().edgeList = edgeList;
		Simulator.get().agentList = agentList;
	}

	public void setEdgeList(Hashtable<Edge, Boolean> edgeList){
		Simulator.get().edgeList = edgeList;
	}

	public void step(){
		Enumeration<Node> NodeList = Simulator.get().nodeList.elements();

		this.missingNodes = 0;

		int i=0;
		int j=0;
		
		while (NodeList.hasMoreElements()){
			Node localNode = NodeList.nextElement();
            i = localNode.howManyKnowMe(Simulator.get().agentList);
			localNode.getInfo().knownBy=i;

			j = localNode.getAgents().size();

			if((i==0)&&(j==0)&&(!localNode.getBH())){
				this.missingNodes++;
				this.missingNodeName=localNode.getName();
			}
			if((i==0)&&(localNode.getBH())){
				this.missingNodes++;
				this.missingNodeName=localNode.getName();
			}

		}

	}//step()

	public void reportData(String strData){
		if (this.printOutData){
			System.out.print(strData);
		}
	}

}//CLASS_RECORD
