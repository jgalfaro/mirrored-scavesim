package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import src.behavior.NodeBehavior;
import src.network.Scenario;

public class Node {

	protected Vector<Agent> agents = new Vector<Agent>();
	protected boolean currentlyUp = true;
	private NodeInfo info;
	protected String name;
	private int id;
	
	private NodeBehavior behavior;
	
	private Node nextNode;
	static Node endNode;
	static Node startNode;
	private Landmark landmark;

	protected Vector<Node> reachableNodes = new Vector<Node>();

	private Map<Integer, Double> weight;
	
	public Node(){}
	
	public Node(String pName, int id) {
		this.name = pName;
		this.id = id;
		this.landmark = null;
		this.weight = new HashMap<Integer, Double>();
	}
		
	protected void acceptAgent(Agent pAgent) {
		if (this.agents.contains(pAgent)) {
			System.err.println("ERROR! " + pAgent + " already in place.");
			return;
		}
		this.agents.addElement(pAgent);
		pAgent.setCurrentNode(this);
	}

	public void clearConnections() {
		this.reachableNodes = new Vector<Node>();
	}

	public void connectTo(Node pNode) {
		if (this.reachableNodes.contains(pNode) == false)
			this.reachableNodes.addElement(pNode);
	}

	public void dumpState() {
		System.out.print(" is " + ((this.currentlyUp) ? "running. " : "stopped. ") + "Has " + this.agents.size() + " agents and " + this.reachableNodes.size()
				+ " reachable nodes. ");
		System.out.print("Connected to: ");
		Enumeration<Node> lEnum = this.reachableNodes.elements();
		while (lEnum.hasMoreElements()) {
			System.out.print(((Node) lEnum.nextElement()).getName() + " ");
		}// while
		System.out.println("");
	}

	public Vector<Agent> getAgents() {
		return this.agents;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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
			if (lAgent.believesNode(this))
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

	public void setPosition(Point3d position) {
		info.setPosition(position);
	}

	public String toString() {
		return Integer.toString(this.id);
	}

	public NodeBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(NodeBehavior behavior) {
		this.behavior = behavior;
	}

	public void changeColorNode(Color3f color){
		if(behavior!=null){
			behavior.setColor(color);
			behavior.postId(NodeBehavior.ID);
		}
	}
	
	public static Node getNodeByName(String node) {
		Vector<Node> nodeList = Simulator.nodeList;
		for(Node n : nodeList) {
			if(n.name.equals(node) ) {
				return n;
			}
		}
		return null;
		
	}
		
	public Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
	}

	public static Node getNodeById(int id) {
		Vector<Node> nodeList = Simulator.nodeList;
		for (Node node : nodeList) {
			if ( node.getId() == id) {
				return node;
			}
		}
		return null;
	}
	
	public static boolean isAdjacent(int v1, int v2) {
		Node node1 = getNodeById(v1);

        Enumeration<Node> localEnumeration = node1.getReachableNodes().elements();
        while (localEnumeration.hasMoreElements()){
        	
        	Node localNode = (Node)localEnumeration.nextElement();            
        	if(v2 == localNode.getId() ) {
        		return true;
        	}
        }
		return false;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public static Node getStartNode() {
		return startNode;
	}

	public static void setStartNode(Node startPoint) {
		Node.startNode = startPoint;
	}

	public static Node getEndNode() {
		return endNode;
	}


	public static void setEndNode(Node endPoint) {
		Node.endNode = endPoint;
	}
	
	public void setLandmark(Landmark landmark) {
		this.landmark = landmark;
	}
	
	public Landmark getLandmark() {
		return this.landmark;
	}
	
	public boolean isLandmark() {
		return this.getLandmark() == null ? false : true;
	}
	
	public void setWeight(int node, double weight) {
		this.weight.put(node, weight);
	}
	
	public double getWeight(int node) {
		try {
			return this.weight.get(node);
		}catch(NullPointerException e){
			return Double.POSITIVE_INFINITY;
		}
	}
	
	public static double getWeight(int i, int j) {
		return Node.getNodeById(i).getWeight( j  );
	}
	
}







