import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;


public class Simulator implements Runnable{


	public enum NetworkType { RANDOM, TORUS, HYPERCUBE };

	Vector<Node> nodeList;
	Hashtable<Edge,Boolean> edgeList;
	Hashtable<Node,NodeInfo> nodeInfoTable;
	Vector<Agent> agentList;
	Vector<Agent> agentExecutionList;
	Network network;
	Record dataCollector;
	protected int initialNumAgents;
	protected int initialNumNodes;
	protected double NetXSize;
	protected double NetYSize;
	public static final boolean drawGraphics = true;
	protected boolean moveableNodes = false;
	Random NetRNG = new Random(518L);//random seed
	int runLength;

	NetworkType type = NetworkType.HYPERCUBE;

	public Simulator(Record pRecord, int pNumNodes, int pNumAgents,
			double pNetXSize, double pNetYSize, Class pAgentClass,
			float pMoveableNodes, int pRunLength){

		this.dataCollector = pRecord;
		this.initialNumNodes = pNumNodes;
		this.initialNumAgents = pNumAgents;
		this.NetXSize = pNetXSize;
		this.NetYSize = pNetYSize;
		this.runLength = pRunLength;
		this.moveableNodes = (pMoveableNodes > 0.0F);
		this.nodeList = new Vector<Node>();
		this.nodeInfoTable = new Hashtable<Node, NodeInfo>();
		this.agentList = new Vector();
		this.edgeList = new Hashtable<Edge,Boolean>();

		pRecord.setInputs(this.nodeList, this.nodeInfoTable, this.edgeList, this.agentList);
		int j = 100;

		for (int i = 0; i < this.initialNumNodes; i++){
			int counter = j;
			j = (char)(counter + 1);
			Node localNode = new Node("n" + Integer.toString(counter) + "n");
			NodeInfo localObject = new NodeInfo(this.NetRNG.nextDouble() * this.NetXSize,
					this.NetRNG.nextDouble() * this.NetYSize);

			if ((this.moveableNodes) && (Math.random() < pRunLength)){
				((NodeInfo)localObject).xMove = (Math.random() * 11.0D - 5.0D);
				((NodeInfo)localObject).yMove = (Math.random() * 11.0D - 5.0D);
			}
			this.nodeList.addElement(localNode);
			this.nodeInfoTable.put(localNode, localObject);

		}

		calculateEdges();
		for (int i = 0; i < this.initialNumAgents; i++){
			int k = (int)(Math.random() * this.nodeList.size());
			Node localObject = (Node)this.nodeList.elementAt(k);
			Agent localAgent = null;
			try{
				localAgent = (Agent)pAgentClass.newInstance();
				localAgent.setRecord(this.dataCollector);
			}
			catch (Exception localException){
				System.err.println("Error creating agents from class " + pAgentClass);
				System.exit(0);
			}
			this.agentList.addElement(localAgent);
			((Node)localObject).acceptAgent(localAgent);
		}
		this.agentExecutionList = ((Vector)this.agentList.clone());
		this.network = new Network(this.nodeList, this.nodeInfoTable,
				this.agentList, (int)this.NetXSize, (int)this.NetYSize);
	}//end_constructor

	public void run(){
		System.err.println("\n Starting simulator with " + this.nodeList.size() +
				" nodes, " + this.edgeList.size() + " edges, and " +
				this.agentList.size() + " agents\n");

		this.dataCollector.reportData("Simulator " + this + " started! nodes: " +
				this.nodeList.size() + " edges " + this.edgeList.size() +
				" agents: " + this.agentList.size() + "\n");
		int i = 0;
		int j = 0;
		while(i == 0){
			this.network.repaint();
			this.moveableNodes=false;
			if(this.moveableNodes){
				Enumeration localEnumeration = this.nodeList.elements();
				while (localEnumeration.hasMoreElements()){
					NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(localEnumeration.nextElement());
					localNodeInfo.x += localNodeInfo.xMove;
					localNodeInfo.y += localNodeInfo.yMove;
					if ((localNodeInfo.x < 0.0D) || (localNodeInfo.x > this.NetXSize)){
						localNodeInfo.xMove *= -1.0D;
						localNodeInfo.x += localNodeInfo.xMove;
						localNodeInfo.x += localNodeInfo.xMove;
					}
					if ((localNodeInfo.y >= 0.0D) && (localNodeInfo.y <= this.NetYSize)){
						continue;
					}
					localNodeInfo.yMove *= -1.0D;
					localNodeInfo.y += localNodeInfo.yMove;
					localNodeInfo.y += localNodeInfo.yMove;
				}
				calculateEdges();
				this.network.invalidateGraphPicture();
			}
			if (this.agentExecutionList.size() != this.agentList.size()){
				System.err.println("Warning - agent list changed and now execution is broken.");
			}
			shuffleList(this.agentExecutionList);
			Enumeration localEnumeration = this.agentExecutionList.elements();
			while (localEnumeration.hasMoreElements()){
				((Agent)localEnumeration.nextElement()).step();
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

	public Network getNetwork(){
		return this.network;
	}

	private void connectNodes(int start, int end){
		Node localNode1 = this.nodeList.get(start);
		Node localNode2 = this.nodeList.get(end);

		localNode1.connectTo(localNode2);
		this.edgeList.put(new Edge(localNode1, localNode2), Boolean.TRUE);
		System.out.println(start+"<->"+end);

		/*	localNode2.connectTo(localNode1);
		this.edgeList.put(new Edge(localNode2, localNode1), Boolean.TRUE);*/

	}

	private void linkGroupNodesHypercube(ArrayList<Integer> listA,ArrayList<Integer> listB){
		for(int i=0;i<listA.size();i++){
			if(listB.size()>i)
				connectNodes(listA.get(i),listB.get(i));
		}
	}

	private void moveNode(Node node,int x, int y){
		NodeInfo nodeInfo = this.nodeInfoTable.get(node);
		nodeInfo.x=x;
		nodeInfo.y=y;
		this.nodeInfoTable.put(node, nodeInfo);
	}
	protected void calculateTorusEdges(){
		int itemsInSlice= 20;
		int lastNodeLinked = 0;

		int x=40;
		int y=40;

		for(int i=0;i<this.nodeList.size();i++){
			Node localNode1 = this.nodeList.get(i);
			localNode1.clearConnections();


			if((i+1)%itemsInSlice!=0){
				if(i+1<this.nodeList.size()){
					connectNodes(i,i+1);
				}
				y+=40;
			}else {
				// if Slice complete & is not first slice
				connectNodes(i,i+1-itemsInSlice);

				if(i>itemsInSlice){// if Exist other comple slice
					//Link actual slice with previous
					System.out.println("Fin anillo");
					for(int j=0;j<itemsInSlice;j++){
						connectNodes(i-j,i-j-itemsInSlice);
					}
				}
				lastNodeLinked=i;
				x+=40;
				y=40;
			}



			moveNode(localNode1, x, y);

		}
		//Link the fist slice with the last
		int inicio=0;
		for(int i=lastNodeLinked;i<this.nodeList.size();i++){
			connectNodes(i,i-itemsInSlice);
			connectNodes(inicio,i);
			inicio++;
		}
	}

	protected void calculateHyperCubeEdges(){
		int numNodes=this.nodeList.size();
		//Calc dimensions of hypercube log2(numNodes)
		double dimensions= Math.log(numNodes) / Math.log(2);

		System.out.println("numNodes="+numNodes+", dimensiones="+dimensions);

		//To group nodes in 2^x groups of nodes

		//Nodes position data
		int x0=5;
		int y0=5;
		int inc= 60;
		int intraIncr=25;

		for(int sizeGroup=1;sizeGroup<=numNodes/2;sizeGroup*=2){
			ArrayList<Integer> listA=new ArrayList<Integer>();
			ArrayList<Integer> listB=new ArrayList<Integer>();

			System.out.println("Uniendo en grupos de "+sizeGroup);

			boolean isFirstGroup=true;

			int beginLastComplete=0;
			for(int i=0;i<numNodes;i++){
				if(isFirstGroup)
					listA.add(i);
				else
					listB.add(i);


				System.out.println("ListA:"+listA);
				System.out.println("ListB:"+listB);
				if((i+1)%sizeGroup==0){
					//Link both groups
					if(!isFirstGroup){
						linkGroupNodesHypercube(listA,listB);

						if(sizeGroup==4){
							Node nodeA0=nodeList.get(listA.get(0));
							Node nodeA1=nodeList.get(listA.get(1));
							Node nodeA2=nodeList.get(listA.get(2));
							Node nodeA3=nodeList.get(listA.get(3));

							this.moveNode(nodeA0, x0, y0);
							this.moveNode(nodeA1, x0, y0+inc);
							this.moveNode(nodeA2, x0+inc, y0);
							this.moveNode(nodeA3, x0+inc, y0+inc);


							Node nodeB0=nodeList.get(listB.get(0));
							Node nodeB1=nodeList.get(listB.get(1));
							Node nodeB2=nodeList.get(listB.get(2));
							Node nodeB3=nodeList.get(listB.get(3));

							this.moveNode(nodeB0, x0+intraIncr, y0+intraIncr);
							this.moveNode(nodeB1, x0+intraIncr, y0+inc+intraIncr);
							this.moveNode(nodeB2, x0+inc+intraIncr, y0+intraIncr);
							this.moveNode(nodeB3, x0+inc+intraIncr, y0+inc+intraIncr);


							x0=(int) (x0+inc*2.5);
							y0+=10;
							if(x0>=640){
								x0=5;
								y0=y0+inc*2;
							}
						}
						listA.clear();
						listB.clear();
						beginLastComplete=i-sizeGroup;
					}
					//Change group
					isFirstGroup=!isFirstGroup;

				}
				//Last node
				if(numNodes-1==i){
					if(!listB.isEmpty()){
						linkGroupNodesHypercube(listA,listB);
					}else{

						int indexLastGroup=beginLastComplete;

						ArrayList<Integer> listPreviousGroup=new ArrayList<Integer>();

						while(indexLastGroup<beginLastComplete+sizeGroup){
							listPreviousGroup.add(indexLastGroup);
							indexLastGroup++;
						}
						linkGroupNodesHypercube(listPreviousGroup,listA);
					}
				}//End Last node
			}
		}

	}

	protected void calculateRandomEdges(){

		Enumeration<Node> localEnumeration1 = this.nodeList.elements();

		while (localEnumeration1.hasMoreElements()){
			Node localNode1 = (Node)localEnumeration1.nextElement();
			NodeInfo localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
			localNode1.clearConnections();
			Enumeration<Node> localEnumeration2 = this.nodeList.elements();
			while (localEnumeration2.hasMoreElements()){
				Node localNode2 = (Node)localEnumeration2.nextElement();
				NodeInfo localNodeInfo2 = (NodeInfo)this.nodeInfoTable.get(localNode2);
				if (localNode1 == localNode2){
					continue;
				}
				double d = (localNodeInfo1.x - localNodeInfo2.x) * (localNodeInfo1.x - localNodeInfo2.x) +
				(localNodeInfo1.y - localNodeInfo2.y) * (localNodeInfo1.y - localNodeInfo2.y);

				if ((d >= 7225.0D) || (d >= 7225.0D)){
					continue;
				}
				localNode1.connectTo(localNode2);
				this.edgeList.put(new Edge(localNode1, localNode2), Boolean.TRUE);
			}
		}
	}
	protected void calculateEdges(){
		this.edgeList = new Hashtable<Edge,Boolean>();
		if (this.dataCollector != null){
			this.dataCollector.setEdgeList(this.edgeList);
		}

		/* Type of network
		 * 0 Random
		 * 1 Torus
		 * 2 Hypercube
		 * */

		switch (type) {
		case TORUS: //Torus
			calculateTorusEdges();


			break;
		case HYPERCUBE: //Hypercube

			calculateHyperCubeEdges();
			break;

		case RANDOM:
			//Default
			calculateRandomEdges();

			break;
		}
	}

	public void dumpState(){
		Enumeration localEnumeration = this.nodeList.elements();
		while (localEnumeration.hasMoreElements()){
			Node localNode = (Node)localEnumeration.nextElement();
			NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(localNode);
			System.out.print("Node " + localNode.getName() + " at (" + (int)localNodeInfo.x +
					"," + (int)localNodeInfo.y + ")");
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
