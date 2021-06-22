package src;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import src.network.Hypercube;
import src.network.Torus;
import src.network.Utils;


public class Simulator implements Runnable{


	public enum NetworkType { RANDOM, TORUS, HYPERCUBE };

	public static Vector<Node> nodeList;
	public static Hashtable<Edge,Boolean> edgeList;
	//Hashtable<Node,NodeInfo> nodeInfoTable;
	Vector<Agent> agentList;
	Vector<Agent> agentExecutionList;
	Network3D network;
	Record dataCollector;
	protected int initialNumAgents;
	protected int initialNumNodes;
	protected Vector3d size;
	public static final boolean drawGraphics = true;
	protected boolean moveableNodes = false;
	Random NetRNG = new Random(518L);//random seed
	int runLength;	

	static NetworkType type = NetworkType.TORUS;

	@SuppressWarnings("unchecked")
	public Simulator(Record pRecord, int pNumNodes, int pNumAgents,	double x, double y, double z, Class<? extends MobAgent> pAgentClass,	float pMoveableNodes, int pRunLength){

		this.dataCollector = pRecord;
		this.initialNumNodes = pNumNodes;
		this.initialNumAgents = pNumAgents;

		this.size=new Vector3d(x, y, z);
		this.runLength = pRunLength;
		this.moveableNodes = (pMoveableNodes > 0.0F);
		nodeList = new Vector<Node>();
		this.agentList = new Vector<Agent>();
		edgeList = new Hashtable<Edge,Boolean>();

		pRecord.setInputs(nodeList, edgeList, this.agentList);
		int j = 100;

		for (int i = 0; i < this.initialNumNodes; i++){
			int counter = j;
			j = (char)(counter + 1);
			Node localNode = new Node("n" + Integer.toString(counter) + "n");
			NodeInfo localObject = new NodeInfo(this.NetRNG.nextDouble() * 2 - 1 ,this.NetRNG.nextDouble() * 2 - 1, this.NetRNG.nextDouble() * 2 - 1);

			if ((this.moveableNodes) && (Math.random() < pRunLength)){
				((NodeInfo)localObject).xMove = (Math.random());
				((NodeInfo)localObject).yMove = (Math.random());
				((NodeInfo)localObject).zMove = (Math.random());
			}

			localNode.setInfo(localObject);
			nodeList.addElement(localNode);

			//this.nodeInfoTable.put(localNode, localObject);

		}

		calculateEdges();
		for (int i = 0; i < this.initialNumAgents; i++){
			int k = (int)(Math.random() * nodeList.size());
			Node localObject = (Node)nodeList.elementAt(k);
			Agent localAgent = null;
			try{
				localAgent = pAgentClass.newInstance();
				localAgent.setRecord(this.dataCollector);
			}
			catch (Exception localException){
				System.err.println("Error creating agents from class " + pAgentClass);
				System.exit(0);
			}
			this.agentList.addElement(localAgent);
			((Node)localObject).acceptAgent(localAgent);
		}
		this.agentExecutionList = (Vector<Agent>) this.agentList.clone();
		this.network = new Network3D(nodeList,this.agentList, (int)this.size.x, (int)this.size.y);
	}//end_constructor

	public void run(){
		System.err.println("\n Starting simulator with " + nodeList.size() +
				" nodes, " + edgeList.size() + " edges, and " +
				this.agentList.size() + " agents\n");

		this.dataCollector.reportData("Simulator " + this + " started! nodes: " +
				nodeList.size() + " edges " + edgeList.size() +
				" agents: " + this.agentList.size() + "\n");
		int i = 0;
		int j = 0;
		
		while(i == 0){
			this.network.repaint();
			this.moveableNodes=false;
			if(this.moveableNodes){
				Utils.moveNodes(nodeList, size);
				calculateEdges();
			}
			if (this.agentExecutionList.size() != this.agentList.size()){
				System.err.println("Warning - agent list changed and now execution is broken.");
			}
			shuffleList(this.agentExecutionList);
			Enumeration<Agent> localEnumeration = this.agentExecutionList.elements();
			while (localEnumeration.hasMoreElements()){
				localEnumeration.nextElement().step();
			}
			this.dataCollector.step();
			this.dataCollector.reportData(" T " + j + " - " +
					this.dataCollector.missingNodesAverage + " " +
					this.dataCollector.missingEdgesAverage + "\n");
			j++;
			if (this.runLength > 0){
				i = (j < this.runLength) ? 0 : 1;
			}else{
				i = ((this.moveableNodes) || (this.dataCollector.missingEdgesAverage != 0.0D)) ? 0 : 1;
			}
			Thread.yield();
		}
	}//end_run()

	public Network3D getNetwork(){
		return this.network;
	}

	protected void calculateEdges(){
		edgeList = new Hashtable<Edge,Boolean>();
		if (this.dataCollector != null){
			this.dataCollector.setEdgeList(edgeList);
		}

		/* Type of network
		 * 0 Random
		 * 1 Torus
		 * 2 Hypercube
		 * */

		switch (type) {
		case TORUS: //Torus
			Torus.calculateEdges(nodeList);
			break;

		case HYPERCUBE: //Hypercube
			Hypercube.calculateEdges(nodeList);
			break;

		case RANDOM:
			//Default
			src.network.Random.calculateEdges(nodeList);

			break;
		}
	}

	public void dumpState(){
		Enumeration<Node> localEnumeration = nodeList.elements();
		while (localEnumeration.hasMoreElements()){
			Node localNode = (Node)localEnumeration.nextElement();
			//			NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(localNode);

			localNode.dumpState();
		}
	}

	public void shuffleList(Vector pVector){
		int i = pVector.size();
		while (i > 1){
			int j = (int)(Math.random() * i);
			i--;
			Object lObject = pVector.elementAt(j);
			pVector.setElementAt(pVector.elementAt(i), j);
			pVector.setElementAt(lObject, i);
		}
	}
	
	

}
