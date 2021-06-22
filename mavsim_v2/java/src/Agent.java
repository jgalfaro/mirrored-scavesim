package src;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;

import src.behavior.AgentBehavior;
import src.behavior.NodeBehavior;

public class Agent implements IAgent{

	private int steps;
	private Node currentNode;
	private NodeInfo info;
	private double p; 
	private Node nextNode;
	private int id;
	private double rand;

	//COST
	double maxSpeed;
	private Vector<Double> consumedEnergy; 
	
	//Controller
	protected Record dataCollector;
	protected Hashtable<Node, Integer> knownNodes;
	protected Hashtable<Edge,Integer> knownEdges;
	protected Hashtable<Node, Integer> rumouredNodes;
	protected Hashtable<Edge, Integer> rumouredEdges;
	
	//STATIC
	protected static Hashtable<Agent, Node> MapAgentToNextNode = new Hashtable<Agent, Node>();
	
	private Transform3D translateAgent;
	
	private AgentBehavior behavior;
	private Node nodeReturned;
	
	public Agent(int id, double p, Record dataCollector, double maxSpeed) {
		
		this.knownNodes = new Hashtable<Node, Integer>();
		this.knownEdges = new Hashtable<Edge, Integer>();
		this.rumouredNodes = new Hashtable<Node, Integer>();
		this.rumouredEdges = new Hashtable<Edge, Integer>();
		this.consumedEnergy = new Vector<Double>();	
		this.nextNode = null;
		this.currentNode = null;
		this.steps = 0;
		
		this.id = id;
		this.p = p;
		this.dataCollector = dataCollector;
		this.maxSpeed = maxSpeed;
		
	}
	
	
	public void setRecord(Record paramRecord){
		this.dataCollector = paramRecord;
	}

	public void run(){
		while (true){
			step();
			Thread.yield();
		}
	}

	public void step(){
		
//		this.knownNodes.put(this.getCurrentNode(), new Integer(this.getSteps()));
//		updateKnownEdgesFromCurrentNode();
//		updateRumoursFromAgents();
		this.setNextNode(this.computeGetNextNode(this.getCurrentNode()));
	    MapAgentToNextNode.put(this, this.getNextNode());
	    
	    if(Simulator.getDataCollector().stepByStep  && this.getCurrentNode().isLandmark()) {
	    	System.out.println("[::] Advice Given To Agent[" + this.getId() + "]: [" + this.getNextNode().getId()  + "] "
				+ " AgentProb.: [" + this.getRand() + "]  Node's Advice Given: [" + this.getNodeReturned().getId()  +"] NodeProb.: [" + this.getCurrentNode().getLandmark().getRand() + "]");
	    }
	    
	}
	
	protected static Node getTheMajority(Vector<Node> nodeList) {
		
		Enumeration localEnumeration = MapAgentToNextNode.keys();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		while (localEnumeration.hasMoreElements()){
			Agent localObject = (Agent) localEnumeration.nextElement();
			int id = MapAgentToNextNode.get(localObject).getId();
			Integer val = map.get(id);
			map.put(id, val == null ? 1 : val + 1);
		}
			
		Entry<Integer, Integer>  majority = null;

	    for (Entry<Integer, Integer> e : map.entrySet()) {
	        if (majority == null || e.getValue().compareTo(majority.getValue()) > 0)
	        	majority = e;
	    }
	    
	    return Node.getNodeById(majority.getKey());

	}
	
	protected static void setTheMajority(Node node, Vector<Agent> agentList) {
		
		for(Agent agent : agentList) {
			Node currenteNode = agent.getCurrentNode();
			if ((node != agent.getCurrentNode()) && (agent.getCurrentNode().moveAgentTo(agent, node) == false)) {
				System.err.println("Move for " + agent + " to " + node + " failed!");
			}
			
			else{
				//System.out.println("The energy consumed by: " + agent.getId() + " is: " t);
				agent.consumedEnergy.add(agent.computeEnergyToFly(currenteNode, node));
				agent.setSteps( agent.getSteps() + 1 );
			}
		}
		
	}
	
	protected void updateKnownEdgesFromCurrentNode(){
		Enumeration<Node> localEnumeration = this.getCurrentNode().getReachableNodes().elements();
		while (localEnumeration.hasMoreElements()){
			Node localNode = localEnumeration.nextElement();
			updateKnownEdge(this.knownEdges, this.getCurrentNode(), localNode);
		}
	}

	protected void updateRumoursFromAgents(){
		Enumeration<Agent> localEnumeration = this.getCurrentNode().getAgents().elements();
		while (localEnumeration.hasMoreElements()){
			Agent localAgent = localEnumeration.nextElement();
			if (localAgent != this){
				this.dataCollector.reportData("le " + this + " " + localAgent + "\n");
				mergeTableFrom(this.rumouredNodes, localAgent.knownNodes, localAgent.getSteps());
				mergeTableFrom(this.rumouredNodes, localAgent.rumouredNodes, localAgent.getSteps());
				mergeTableFrom(this.rumouredEdges, localAgent.knownEdges, localAgent.getSteps());
				mergeTableFrom(this.rumouredEdges, localAgent.rumouredEdges, localAgent.getSteps());
			}
		}
	}

	protected void mergeTableFrom(Hashtable paramHashtable1, Hashtable paramHashtable2, int paramInt){
		Enumeration localEnumeration = paramHashtable2.keys();
		while (localEnumeration.hasMoreElements()){
			Object localObject = localEnumeration.nextElement();
			int i = paramInt - ((Integer)paramHashtable2.get(localObject)).intValue();
			if (!(paramHashtable1.containsKey(localObject))){
				paramHashtable1.put(localObject, new Integer(this.getSteps() - i));
			}
			else{
				int j = this.getSteps() - ((Integer)paramHashtable1.get(localObject)).intValue();
				if (j > i)
					paramHashtable1.put(localObject, new Integer(this.getSteps() - i));
			}
		}
	}

	protected void dumpState(){
		System.out.print(this + " steps = " + this.getSteps() + " ");
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

	public void setCurrentNode(Node paramNode){
		this.currentNode = paramNode;
	}

	public Node getCurrentNode(){
		return this.currentNode;
	}
	
	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
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

	protected void updateKnownEdge(Hashtable paramHashtable, Node paramNode1, Node paramNode2){
		paramHashtable.put(new Edge(paramNode1, paramNode2), new Integer(this.getSteps()));
		paramHashtable.put(new Edge(paramNode2, paramNode1), new Integer(this.getSteps()));
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

	public AgentBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(AgentBehavior behavior) {
		this.behavior = behavior;
	}
	
	public void changeColorNode(Color3f color){
		if(behavior!=null){
			behavior.setColor(color);
			behavior.postId(NodeBehavior.ID);
		}
	}
	
	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}
		
	public Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
	}

	public Node computeGetNextNode(Node currentNode) {		
		
		if (this.getCurrentNode().isLandmark()) {
			
			Node n =  currentNode.getLandmark().computeGetNextNode(currentNode); 
			Double rand = new Random().nextDouble();  
			
			this.setNodeReturned(n);
			this.setRand(rand);;
			
			if(rand <= p ){
				
				return n;
			}else {
				
				ArrayList<Integer> ls = new ArrayList<Integer>();
				Enumeration localEnumeration = this.getCurrentNode().getReachableNodes().elements();
				
		        while (localEnumeration.hasMoreElements()){
		        	
		        	Node localNode = (Node)localEnumeration.nextElement();
		        	
		            if(localNode.getId() != n.getId()) {
		            	ls.add(localNode.getId());
		            }
		            
		        } 
		        
				if (ls.size() == 0) { // Only one adjacent vertex 
					return currentNode.getNextNode();
				}else {
					int x = new Random().nextInt(ls.size());
					return Node.getNodeById(ls.get(x));				
				}
			}
		
		}else {
			return currentNode.getNextNode();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public double getRand() {
		return rand;
	}

	public void setRand(double rand) {
		this.rand = rand;
	}
	
	public Node getNodeReturned() {
		return nodeReturned;
	}

	public void setNodeReturned(Node nodeReturned) {
		this.nodeReturned = nodeReturned;
	}
	
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public double getMaxSpeed() {
		return this.maxSpeed;
	}
	
	public double computeEnergyToFly(Node from, Node to) {
		double distance = from.getWeight( to.getId() );
		return ( distance * ((0.95 * (this.getMaxSpeed() * this.getMaxSpeed())) - (20.4 * this.getMaxSpeed()) +130) );
	}

	public void setConsumedEnergy(Vector<Double> consumedEnergy) {
		this.consumedEnergy = consumedEnergy;
	}
	
	public Vector<Double> getConsumedEnergy() {
		return this.consumedEnergy;
	}
}
