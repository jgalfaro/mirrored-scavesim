package src;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.media.j3d.Transform3D;

import src.Simulator.Status;
import src.behavior.AgentBehavior;

public abstract class Agent extends Thread{
	public enum AgentStatus {
		single,done,/*COMMON*/
		pairedNS,pairedEW,crossringNS,crossringEW,dangerousringNS,dangerousringEW,/*<3*/
		forward, checking/*>3*/
	}
	protected AgentStatus AlgoStatus=null;
	protected String NetworkType=null;
	protected String name=null;
	public boolean executing=true;
	public boolean exit=false;
	public int xCount=0;
	public int yCount=0;
	protected String cwwtTokenAtNode = null;
	//protected String HomeBaseAtNode = null;
	protected int steps = 0;
	protected int visitedNSrings = 0;
	protected int visitedEWrings = 0;
	protected int numNodesNS=0;
	protected int numNodesEW=0;
	protected int phase=0;
	
	protected Node currentNode = null;
	protected Node previousNode = null;
	private NodeInfo info;

	protected Record dataCollector;
	protected Hashtable<Node, Integer> knownNodes = new Hashtable<Node, Integer>();
	protected Hashtable<Edge,Integer> knownEdges = new Hashtable<Edge, Integer>();
	protected Hashtable<Node, Integer> rumouredNodes = new Hashtable<Node, Integer>();
	protected Hashtable<Edge, Integer> rumouredEdges = new Hashtable<Edge, Integer>();

	private Transform3D translateAgent;

	private AgentBehavior behaviour;



	public void setcwwtTokenAtNode(String node){
		this.cwwtTokenAtNode = node;
	}

/*	public void setHomeBase(String node){
		this.HomeBaseAtNode = node;
	}

	public String getHomeBase(){
		return this.HomeBaseAtNode;
	}*/

	public String getcwwtTokenAtNode(){
		return(this.cwwtTokenAtNode);
	}

	public void setAlgoStatus(AgentStatus pStatus){
		this.AlgoStatus = pStatus;
	}

	public void setRecord(Record paramRecord){
		this.dataCollector = paramRecord;
	}

	public void run(){
		while (!exit){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(Simulator.status==Status.STEPPING)/*(Simulator.running && ( Simulator.continueP || Simulator.status!=Status.FINISHED))*/{
				if(this.executing)
					step();
			}
			Thread.yield();
		}
	}


	public int getPhase() {
		return(this.phase);
	}

	public void setPhase(int phase) {
		this.phase=phase;
	}

	public void step(){
		
		this.knownNodes.put(this.currentNode, new Integer(this.steps));
		updateKnownEdgesFromCurrentNode();
		updateRumoursFromAgents();
		Node localNode = calculateNextNode();

		try {
			long time=(long) (100l* Simulator.get().getWeight(this.currentNode,localNode));
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if ((localNode != this.currentNode) && (this.currentNode.moveAgentTo(this, localNode) == false)){
			System.err.println("Move for:\n " + this + "\nTo:\n " + localNode + " failed!");
		}
		if(localNode.getBH()){//the agent is at the BH
			System.out.println("\n \n ************** Agent " + this.name + " died at the BH["+localNode.name+"] from ["+previousNode.name+"]! **************************** \n \n \n");
			this.executing=false;
		}
		this.steps += 1;
	}

	protected void updateKnownEdgesFromCurrentNode(){
		Enumeration<Node> localEnumeration = this.currentNode.getReachableNodes().elements();
		while (localEnumeration.hasMoreElements()){
			Node localNode = localEnumeration.nextElement();
			updateKnownEdge(this.knownEdges, this.currentNode, localNode);
		}
	}

	protected void updateRumoursFromAgents(){
		Enumeration<Agent> localEnumeration = this.currentNode.getAgents().elements();
		while (localEnumeration.hasMoreElements()){
			Agent localAgent = localEnumeration.nextElement();
			if (localAgent != this){
				//				this.dataCollector.reportData("le " + this + " " + localAgent + "\n");
				mergeTableFrom(this.rumouredNodes, localAgent.knownNodes, localAgent.steps);
				mergeTableFrom(this.rumouredNodes, localAgent.rumouredNodes, localAgent.steps);
				mergeTableFrom(this.rumouredEdges, localAgent.knownEdges, localAgent.steps);
				mergeTableFrom(this.rumouredEdges, localAgent.rumouredEdges, localAgent.steps);
			}
		}
	}

	protected void mergeTableFrom(Hashtable paramHashtable1, Hashtable paramHashtable2, int paramInt){
		Enumeration localEnumeration = paramHashtable2.keys();
		while (localEnumeration.hasMoreElements()){
			Object localObject = localEnumeration.nextElement();
			int i = paramInt - ((Integer)paramHashtable2.get(localObject)).intValue();
			if (!(paramHashtable1.containsKey(localObject))){
				paramHashtable1.put(localObject, new Integer(this.steps - i));
			}
			else{
				int j = this.steps - ((Integer)paramHashtable1.get(localObject)).intValue();
				if (j > i)
					paramHashtable1.put(localObject, new Integer(this.steps - i));
			}
		}
	}

	protected void dumpState(){
		System.out.print("Agent ["+this.name+"] at previous steps ={" + this.steps + "}");
		Enumeration<Node> localEnumeration = this.knownNodes.keys();
		while (localEnumeration.hasMoreElements()){
			Node localNode = localEnumeration.nextElement();
			System.out.print("(" + localNode.getName() + " " + this.knownNodes.get(localNode) + ") ");
		}
		System.out.println();
	}

	protected boolean believesEdge(Edge paramEdge){
		return this.knownEdges.containsKey(paramEdge);
	}

	protected boolean believesNode(Node paramNode){
		return this.knownNodes.containsKey(paramNode);
	}

	protected abstract Node calculateNextNode();

	public void setCurrentNode(Node paramNode){
		this.currentNode = paramNode;
	}

	public Node getCurrentNode(){
		return this.currentNode;
	}

	public Hashtable<Node, Integer> getKnownNodes(){
		return this.knownNodes;
	}

	public Hashtable<Edge, Integer> getKnownEdges(){
		return this.knownEdges;
	}

	public Hashtable<Node, Integer> getRumouredNodes(){
		return this.rumouredNodes;
	}

	public Hashtable<Edge, Integer> getRumouredEdges(){
		return this.rumouredEdges;
	}

	protected void updateKnownEdge(Hashtable<Edge,Integer> paramHashtable, Node paramNode1, Node paramNode2){
		double weidth=Edge.randomWeight();        
		paramHashtable.put(new Edge(paramNode1, paramNode2,weidth), new Integer(this.steps));
		paramHashtable.put(new Edge(paramNode2, paramNode1,weidth), new Integer(this.steps));
	}

	public int getMoves() {
		return this.steps;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public void setInfo(NodeInfo info) {
		this.info = info;
	}

	public Transform3D getTranslateAgent() {
		return translateAgent;
	}

	public void setTranslateAgent(Transform3D translateAgent) {
		this.translateAgent = translateAgent;
	}

	public AgentBehavior getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(AgentBehavior behaviour) {
		this.behaviour = behaviour;
	}

	public String getNetworkType() {
		return name;
	}

	public void setNetworkType(String name) {
		this.NetworkType = name;
	}

	public String toString() {
		String aux="Agent:\n";
		aux+="\n\tname:\t"+name;
		aux+="\n\tAlgoStatus:\t"+AlgoStatus;
		aux+="\n\tNetworkType:\t"+NetworkType;
		aux+="\n\talive:\t"+executing;
		aux+="\n\txCount:\t"+xCount;
		aux+="\n\tyCount:\t"+yCount;
		aux+="\n\tcwwtTokenAtNode:\t"+cwwtTokenAtNode;
		//aux+="\n\tHomeBaseAtNode:\t"+HomeBaseAtNode;
		aux+="\n\tsteps:\t"+steps;
		aux+="\n\tvisitedNSrings:\t"+visitedNSrings;
		aux+="\n\tvisitedEWrings:\t"+visitedEWrings;
		aux+="\n\tnumNodesNS:\t"+numNodesNS;
		aux+="\n\tnumNodesEW:\t"+numNodesEW;
		aux+="\n\tphase:\t"+phase;

		aux+="\n\tcurrentNode\t:"+currentNode.name ;
		aux+="\n\tpreviousNode:\t";
		if(previousNode!= null)
			aux+=previousNode.name ;
		aux+="\n\tinfo:\t"+info;

		aux+="\n\tknownNodes:\t"+knownNodes.size();

		aux+="\n\tknownEdges:\t"+knownEdges.size();
		aux+="\n\trumouredNodes:\t"+rumouredNodes.size();
		aux+="\n\trumouredEdges:\t"+rumouredEdges.size();		

		return aux;
	}


	public void remove(){
		behaviour.emptyBehavior();//.setPosition(new Point3d(0d,0d,0d));
		//a.getBehaviour().postId(AgentBehavior.ID);
		executing=false;
		exit=true;
		Simulator.get().agentList.remove(this);
		
	}
	
	protected Node calculateNextNodeAtRandom() {
		int i = this.steps + 1;
		Node localObject = this.currentNode;
		Node nextNode;
		int j = 0;
		Vector<Node> localVector = null;
		Enumeration<Node> localEnumeration = this.currentNode.getReachableNodes().elements();

		if (this.steps == 0) {
			System.out.println("step[" + this.steps + "]::Agent[" + this.name + "] in Node[" + this.currentNode + "]");
		}

		while (localEnumeration.hasMoreElements()) {
			Node localNode = (Node) localEnumeration.nextElement();
			Integer localInteger = this.knownNodes.get(localNode);
			if (localInteger == null) {
				if (j == 0) {
					j = 1;
					i = -1;
					localVector = new Vector<Node>();
				}
				localVector.addElement(localNode);
			} else if (localInteger.intValue() < i) {
				localObject = localNode;
				i = localInteger.intValue();

			} else if (localInteger.intValue() == i) {
				System.out.println("WARNING! two nodes were last visited at steps=" + localInteger.intValue());
			}
		}
		if (j == 0) {
			System.out.println("step[" + (this.steps + 1) + "]::Agent[" + this.name + "] in Node[" + localObject.getName() + "] (already visited)");
			return localObject;
		}
		nextNode = (Node) localVector.elementAt((int) (Math.random() * localVector.size()));
		System.out.println("step[" + (this.steps + 1) + "]::Agent[" + this.name + "] in Node[" + nextNode.getName() + "]");
		return (nextNode);
	}

}
