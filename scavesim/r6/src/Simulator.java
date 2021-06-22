package src;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import src.Agent.AgentStatus;
import src.network.Torus;
import src.network.Utils;

public class Simulator implements Runnable {

	private static Simulator ref;

        private double speed=0.01;
	public  int showLabels = 0;
	public  Vector<Node> nodeList;
	public  Hashtable<Edge, Boolean> edgeList;
	public  Vector<Agent> agentList;
	private  Vector<Integer> BH = new Vector<Integer>();
	private  Vector<Integer> initialNodes;

	public enum NetworkType {
		TORUS, RANDOM, HYPERCUBE
	};

	public enum Status {
		INIT, STEPPING, PAUSED, FINISHED
	};

	Network3D network;
	Record dataCollector;
	protected  int numAgents;
	protected  int numNodes;
	private int numBH = 0;
	protected int initialNumTokens;
	protected int numRings = 0;
	protected int numSlices = 0;
	protected int testA2 = 0;
	protected Vector3d size;
	private static int TotalMoves = 0;

	public  final boolean drawGraphics = true;
	protected boolean moveableNodes = false;
	Random NetRNG = new Random(2001L);// random seed
	int runLength;

	/** CONTROL **/
	public boolean rerunning = false;
	public boolean continueP = true;
	public boolean one_step = false;

	static NetworkType type = NetworkType.TORUS;
	static Status status = Status.INIT;


    public static Simulator get(){
      if (ref == null)
          // it's ok, we can call this constructor
          ref = new Simulator();
      return ref;
    }
	public void Init(Record pRecord, int pNumNodes, int pNumAgents, double x, double y, double z, float pMoveableNodes, int pRunLength, int pNumTokens, int pNumBH,
			int pShowLabels, int testA2) {

		showLabels = pShowLabels;
		this.dataCollector = pRecord;
		numNodes = pNumNodes;
		numAgents = pNumAgents;
		this.initialNumTokens = pNumTokens;
		this.testA2 = testA2;
		numBH = pNumBH;

		this.size = new Vector3d(x, y, z);
		this.runLength = pRunLength;
		this.moveableNodes = (pMoveableNodes > 0.0F);
		nodeList = new Vector<Node>();
		agentList = new Vector<Agent>();
		edgeList = new Hashtable<Edge, Boolean>();

		pRecord.setInputs(nodeList, edgeList, agentList);

		// Init BlackHoles
		initBHPosition(numBH, numNodes);

		// Init agents
		initialNodes = new Vector<Integer>();
		initAgents(numAgents, numNodes);
		putInitialNodes(runLength);

		// Init BlackHoles, needs putInitialNodes
		putBHPosition();

		calculateEdges();

		for (int i = 0; i < initialNodes.size(); i++) {
			Node localObject = (Node) nodeList.elementAt(initialNodes.get(i));
			String initNodeName = localObject.getName();
			this.dataCollector.initialNodeName.add(i, initNodeName);
		}

		this.dataCollector.setNumAgents(numAgents);
		this.dataCollector.setNumSlices(numNodes * 0.1);// TODO: parametrize
		// this!

		for (int i = 0; i < numAgents; i++) {
			addAgent(i);
		}
		// agentExecutionList = (Vector<Agent>) agentList.clone();
		this.network = new Network3D(nodeList, agentList, (int) this.size.x, (int) this.size.y);
		this.network.setRecord(this.dataCollector);

		status = Status.INIT;

	}// end_constructor

	public void addAgent(int index) {
		Node localObject = (Node) nodeList.elementAt(initialNodes.get(index));

		Agent localAgent = null;
		try {
			/** TODO: DAVID poner la condicion segun el numero de nodos */
			//if (numAgents > 3)
				localAgent = new MobAgentSingleForward();
			/*else
				localAgent = new MobAgent();*/

			localAgent.setRecord(this.dataCollector);
			localAgent.executing = true;
			localAgent.AlgoStatus = AgentStatus.single;
			localAgent.setPhase(0);
			if (this.testA2 == 1) {
				localAgent.name = "A" + Integer.toString(index + 1 + 1);
			} else {
				localAgent.name = "A" + Integer.toString(index + 1);
			}
			switch (type) {
			case TORUS:
				localAgent.setNetworkType("TORUS");
				break;
			default:
				localAgent.setNetworkType("TORUS");
			}// switch
		} catch (Exception localException) {
			System.err.println("Error creating agents from class ");
			System.exit(0);
		}
		agentList.addElement(localAgent);
		((Node) localObject).acceptAgent(localAgent);

	}

	private void initAgents(int initialNumAgents, int initialNumNodes) {
		if (initialNodes.size() > initialNumAgents) {
			for (int i = initialNodes.size() - 1; i >= initialNumAgents; i--) {
				initialNodes.remove(i);
			}
		} else if (initialNodes.size() < initialNumAgents) {
			for (int i = initialNodes.size(); i < initialNumAgents; i++) {
				initialNodes.add(-1);
			}
		}

		for (int index = 0; index < initialNumAgents; index++) {
			int initialPosition;
			boolean unique;
			do {
				unique = true;
				initialPosition = (int) (Math.random() * initialNumNodes);
				if (BH.contains(initialPosition) || initialNodes.contains(initialPosition)) {
					unique = false;
				}
			} while (!unique);
			initialNodes.setElementAt(initialPosition, index);
                        System.out.println("A" + (index+1)+" wakes up at node "+ initialPosition);
		}
	}

	private void putInitialNodes(int pRunLength) {
		Node localNode = null;
		int j = 0;
		for (int i = 0; i < numNodes; i++) {

			int counter = j;
			j = (char) (counter + 1);
			localNode = new Node(Integer.toString(counter));

			NodeInfo localObject = new NodeInfo(NetRNG.nextDouble() * 2 - 1, NetRNG.nextDouble() * 2 - 1, NetRNG.nextDouble() * 2 - 1);

			if ((moveableNodes) && (Math.random() < pRunLength)) {
				((NodeInfo) localObject).xMove = (Math.random());
				((NodeInfo) localObject).yMove = (Math.random());
				((NodeInfo) localObject).zMove = (Math.random());
			}

			localNode.setInfo(localObject);
			nodeList.addElement(localNode);

		}
	}

	private void initBHPosition(int numBH, int numNodes) {

		BH = null;
		BH = new Vector<Integer>();
		// test:: ant run -Dtype=1 -Dtokens=7 -Dnodes=20 -Dagents=2 -DBH=1 ...
		for (int i = 0; i < numBH; i++) {
			int tmpBH;
			boolean unique;
			do {
				unique = true;
				tmpBH = (int) (Math.random() * numNodes);
				if (BH.contains(tmpBH)) {
					unique = false;
				}
			} while (!unique);
			BH.add(tmpBH);
		}
	}

	// Needs setBHPosotion has been exec previously
	private void putBHPosition() {
		Node localNode;
		for (Integer index : BH) {
			localNode = (Node) nodeList.elementAt(index);
			System.out.println("BH at node:" + localNode.name);
			localNode.setBH();
		}
	}

	public void run() {
		while (true) {

			// System.out.println("agentExecutionList:"+agentExecutionList.size());
			System.out.println("agentList:" + agentList.size());
			if (hasAgentsExecuting()) {// we stop the simulation
				System.out.println("Move");
				int j = 0;
				TotalMoves = 0;
				int SurvivalList = 0;

				Agent localAgent = null;

				this.dataCollector.reportData("Starting simulator with " + nodeList.size() + " nodes, " + this.dataCollector.getNumSlices() + " slices, and "
						+ agentList.size() + " agents\n");

				if (numBH > 0) {
					this.dataCollector.reportData("BH at node " + BH + "\n");
				}

				if (!rerunning) {
					Render r = new Render(this);
					r.start();
				}
				/* TODO: Comprobar que hace falta */
				shuffleList(agentList);
				Enumeration<Agent> agents = agentList.elements();

				while (agents.hasMoreElements()) {
					localAgent = agents.nextElement();
					if (localAgent.executing) {
						if (!rerunning)
							localAgent.start();
					} else {
						// TotalMoves+=localAgent.getMoves();
						localAgent.executing = false;
						this.network.updateAgentList(agentList);
					}
				}

				while (continueP || status == Status.FINISHED) {
					if (status == Status.STEPPING) {

						this.moveableNodes = false;
						if (this.moveableNodes) {
							Utils.moveNodes(nodeList, size);
							calculateEdges();
						}

						// if (this.agentExecutionList.size() !=
						// agentList.size()){
						// System.err.println("Warning - agent list changed and now execution is broken.");
						// }

						shuffleList(agentList);
						Enumeration<Agent> localEnumeration = agentList.elements();
						while (localEnumeration.hasMoreElements()) {
							localAgent = localEnumeration.nextElement();
							if (!localAgent.executing) {
								// localAgent.step();

								// TotalMoves+=localAgent.getMoves();
								// localAgent.alive=false;
								// agentList.removeElement(localAgent);
								// this.agentList.removeElement(localAgent);
								this.network.updateAgentList(agentList);
							}
						}

						j++;// executed steps
						if (!hasAgentsExecuting()) {// we stop the simulation
							continueP = false;
						} else {
							this.dataCollector.step();
							// this.dataCollector.reportData("\nNodes to visit="
							// + (int) this.dataCollector.missingNodes + "\n");
							// Pending
							// Links[" + this.dataCollector.missingEdgesAverage + "]\n");
							if (this.runLength > 0) {
								if (j < this.runLength)// more steps that
									// maxNumberOfSteps
									// (simulator parameter,
									// by default -1)
									continueP = true;
								else
									continueP = false;
							} else {
								if ((this.moveableNodes) || (this.dataCollector.missingNodes != 1))// more
									// steps
									// that
									// maxNumberOfSteps
									// (simulator
									// parameter,
									// by
									// default
									// -1)
									continueP = true;
								else
									continueP = false;
							}
						}
						if (one_step) {
							status = Status.PAUSED;
							one_step = false;
						}
						// this.dumpState();
					}
					try {
						// System.out.println("WAIT");
						Thread.sleep(50);
						// System.out.println("CONTINUE");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Thread.yield();
				}

				if (this.dataCollector.missingNodes == 1) {
					System.out.print("The BH should be node " + this.dataCollector.missingNodeName + "\n");
				}

				SurvivalList = 0;
				Enumeration<Agent> localEnumeration = agentList.elements();
				while (localEnumeration.hasMoreElements()) {
					localAgent = localEnumeration.nextElement();
					if (localAgent.executing || localAgent.currentNode.BH) {
						SurvivalList++;
					}
					System.out.print("Agent " + localAgent.name + " moved " + localAgent.getMoves() + " times \n");
					TotalMoves += localAgent.getMoves();
				}// while

				System.out.print("\n\n*************************************\n");
				System.out.print("Summary: Total moves in this simulation = " + TotalMoves + " moves \n");
				//System.out.print("Summary: Total agents alive after the simulation = " + SurvivalList + " agent(s) \n");

				if (numBH > 0) {
					System.out.print("Summary: BH were at node " + BH + "\n");
				}

				// System.out.print("Summary: Agents were initially at nodes [A1,"+this.InitialNode1+"], [A2,"+this.InitialNode2+"], [A3,"+this.InitialNode3+"],\n");
				System.out.print("*************************************\n");

				status = Status.FINISHED;
			}

			while (status == Status.FINISHED) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}// end_run()

	protected void calculateEdges() {
		edgeList = new Hashtable<Edge, Boolean>();
		if (this.dataCollector != null) {
			this.dataCollector.setEdgeList(edgeList);
		}

		/*
		 * Type of network 1 Torus
		 */

		switch (type) {
		case TORUS: // Torus
			Torus.calculateEdges(nodeList, this.dataCollector);
			break;

		default:
			Torus.calculateEdges(nodeList, this.dataCollector);

		}
	}

	public void dumpState() {
		System.out.println("Simulator started with " + nodeList.size() + " nodes, " + this.initialNumTokens + " tokens, and " + agentList.size()
				+ " agent(s)\n");
		if (numBH > 0) {
			System.out.println("BHs are at node " + BH + "\n");
		} else {
			System.out.println("Network does not contain any BH \n");
		}

		// System.out.print("Agents initially at nodes [A1,"+this.InitialNode1+"], [A2,"+this.InitialNode2+"], [A3,"+this.InitialNode3+"],\n");

		System.out.println("System state::\n");
		Enumeration<Node> localEnumeration = nodeList.elements();
		while (localEnumeration.hasMoreElements()) {
			Node localNode = (Node) localEnumeration.nextElement();
			// NodeInfo localNodeInfo =
			// (NodeInfo)this.nodeInfoTable.get(localNode);
			localNode.dumpState();
			localNode.dumpTokenList();
			// localNode.dumpStateNS3();
			// localNode.dumpStateNeato();
		}
	}

	public void shuffleList(Vector<Agent> pVector) {
		int i = pVector.size();
		while (i >= 1) {
			int j = (int) (Math.random() * i);
			i--;
			Agent lObject = pVector.elementAt(j);
			pVector.setElementAt(pVector.elementAt(i), j);
			pVector.setElementAt(lObject, i);
		}
	}

	public void clean() {
		nodeList.clear();
		edgeList.clear();
		agentList.clear();

	}

	public double getWeight(Node begin, Node end) {
		for (Edge e : edgeList.keySet()) {
			if ((e.edgeFrom.equals(begin) && e.edgeTo.equals(end)) || (e.edgeFrom.equals(end) && e.edgeTo.equals(begin))) {
                            // Return actual value
                            //joaquin
                            return e.weight*speed;
			}
		}
		// Return default value
                //return 2d;
                return speed;
	}

	private boolean hasAgentsExecuting() {
		for (Agent agent : agentList) {
			if (agent.executing)
				return true;
		}
		return false;
	}

	public Network3D getNetwork() {
		return this.network;
	}

	public void restart() {

		/* STOP BEFORE CHANGE */
		continueP = false;
		status = Status.FINISHED;

		// Wait agents step end
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Reset nodes
		Enumeration<Node> localEnumeration = nodeList.elements();
		while (localEnumeration.hasMoreElements()) {
			Node localNode = localEnumeration.nextElement();
			localNode.token = false;
			localNode.currentlyUp = true;
			localNode.BH = false;
			localNode.token = false;
			localNode.getInfo().knownBy = 0;
			localNode.agents.clear();

			localNode.tokenList = new Vector<Token>();
		}

		// Change BH
		initBHPosition(numBH, numNodes);

		// Change initial points Agents
		initAgents(numAgents, numNodes);

		// ADD or remove agents
		if (agentList.size() > numAgents) {
			for (int i = agentList.size() - 1; i >= numAgents; i--) {
				agentList.get(i).remove();
			}
		} else if (agentList.size() < numAgents) {
			for (int i = agentList.size(); i < numAgents; i++) {
				addAgent(i);
				agentList.get(i).start();
				network.drawAgent(agentList.get(i));
			}
		}

		// Reset agents
		for (Agent agent : agentList) {
			// agent.exit=true;
			agent.getKnownEdges().clear();
			agent.getKnownNodes().clear();

			// agentExecutionList.remove(agent);

			agent.xCount = 0;
			agent.yCount = 0;
			agent.exit = false;
			agent.numNodesEW = 0;
			agent.numNodesNS = 0;
			agent.visitedNSrings = 0;
			agent.visitedEWrings = 0;
			agent.phase = 0;
			agent.steps = 0;
			agent.executing = true;

			agent.AlgoStatus = AgentStatus.single;

			agent.cwwtTokenAtNode = null;
			// agent.HomeBaseAtNode = null;
		}

		for (int i = 0; i < agentList.size(); i++) {
			Agent agent = agentList.get(i);
			agent.currentNode = nodeList.get(initialNodes.get(i));
			agent.currentNode.getInfo().knownBy = 1;
			agent.currentNode.agents.add(agent);
			agent.knownNodes.put(agent.currentNode, 0);
		}

		putBHPosition();

		/* START AFTER CHANGE */
		continueP = true;
		one_step = false;
		rerunning = true;
		TotalMoves = 0;

		status = Status.STEPPING;
	}

	public void recalc(int numNodes, int numAgents, int numBH) {
		System.out.println("Num nodes: " + numNodes + ", NumAgents:" + numAgents + ", NumBH:" + numBH);

		this.numNodes = numNodes;
		if (numAgents > numAgents) {
			System.err.println("###################################");
			System.err.println("###Can't increase agents number ###");
			System.err.println("###################################");
		} else
			this.numAgents = numAgents;
		this.numBH = numBH;

		restart();
	}

	public void printSimulator() {

		System.out.println("##########################################################\n");
		System.out.println("##########################################################");
		System.out.println("################      NODES       ########################");
		System.out.println("##########################################################");
		Enumeration<Node> localEnumeration = nodeList.elements();

		while (localEnumeration.hasMoreElements()) {
			Node localNode = localEnumeration.nextElement();
			System.out.println(localNode);
		}

		System.out.println("##########################################################");
		System.out.println("################      AGENTS       #######################");
		System.out.println("##########################################################\n");
		for (Agent agent : agentList) {
			System.out.println(agent);
		}

		System.out.println("##########################################################");
		System.out.println("################      EDGES       ########################");
		System.out.println("##########################################################\n");
		Enumeration<Edge> keys = edgeList.keys();
		// iterate through Hashtable keys Enumeration
		while (keys.hasMoreElements()) {
			Edge e = keys.nextElement();
			System.out.println(e.edgeFrom.name + "->" + e.edgeTo.name + ": " + edgeList.get(e));
		}

		System.out.println("##########################################################");
	}

	public void printStatusAgents() {
		String status = "";
		for (Agent agent : agentList) {
			status += agent.name + ": Executing=" + agent.executing + ", phase=" + agent.phase + ", algoStatus: " + agent.AlgoStatus + "\t";
		}
		System.out.println("Status Agents:" + status);
	}

}
