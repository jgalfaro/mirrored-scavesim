package src;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Vector3d;

import java.io.File;

import src.network.Hypercube;
import src.network.Scenario;
import src.network.Torus;
import src.network.Utils;


public class Simulator implements Runnable{


	public enum NetworkType { RANDOM, TORUS, HYPERCUBE, SCENARIO };
	
	public static String output;
	public static Vector<Node> nodeList;
	public static Vector<Integer> listOfLandmark;
	public static Vector<Integer> listOfLandmarkToCross;
	public static Hashtable<Edge,Boolean> edgeList;
	public static Vector<Agent> agentList;
	Vector<Agent> agentExecutionList;
	Network3D network;
	static Record dataCollector;
	protected int initialNumAgents;
	protected int initialNumNodes;
	protected Vector3d size;
	public static final boolean drawGraphics = true;
	protected boolean moveableNodes = false;	
	int runLength;	

	/**CONTROL**/
	public static boolean finished = false;
	public static boolean running = true;
	public static boolean one_step = false;
	public static int contLandmark;
	public static int numbLandmarkToCross;
	public static int numbOfNodes;
	public static Node currentTargetNode;
	   
	public static int landmarkListsize;
	
	/**WRITE**/
	private static FileWriter fileWriter;

	static NetworkType type = NetworkType.TORUS;

	@SuppressWarnings("unchecked")
	public Simulator(Record pRecord, int pNumNodes, int pNumAgents,	double x, double y, double z, 
			float pMoveableNodes, int pRunLength, String output,
			float agentSize, float nodeSize, int numbLandmarkToCross
			) throws IOException{

		this.dataCollector = pRecord;
		this.initialNumNodes = pNumNodes;
		this.initialNumAgents = pNumAgents;
		this.output = output;

		this.size=new Vector3d(x, y, z);
		this.runLength = pRunLength;
		this.moveableNodes = (pMoveableNodes > 0.0F);
		this.nodeList = new Vector<Node>();
		this.agentList = new Vector<Agent>();
		this.edgeList = new Hashtable<Edge,Boolean>();
		
		this.contLandmark = 1;
		this.listOfLandmark = new Vector<Integer>();
		this.listOfLandmarkToCross = new Vector<Integer>();
		this.numbLandmarkToCross=numbLandmarkToCross;
		
		pRecord.setInputs(nodeList, edgeList, this.agentList);
		
		Scenario.setNodes(0.8);
	      
	    numbOfNodes = Simulator.nodeList.size();
	    landmarkListsize = Simulator.listOfLandmark.size();
	    
	    Scenario.floydWarshall();   
	      
	    
	    if(numbLandmarkToCross <= landmarkListsize && numbLandmarkToCross >= 2) {
		
			for (int i = 0; i < this.initialNumAgents; i++){

				Agent localAgent = new Agent(i,0.8, this.dataCollector, 5); // MAXSPEED //5ms, 10ms, 15ms e 20 ms
				(Node.getStartNode()).acceptAgent(localAgent);
				
				this.agentList.addElement(localAgent);
							
			}
			
			
			this.agentExecutionList = (Vector<Agent>) this.agentList.clone();
			this.network = new Network3D(nodeList,this.agentList, (int)this.size.x, (int)this.size.y, agentSize, nodeSize);
		
	    }else {
	    	System.err.println("Number of landmarks to cross must be less than total number of landmarks and greater than 2!");
	    	GUI.getMainFrame().dispatchEvent(new WindowEvent(GUI.getMainFrame(), WindowEvent.WINDOW_CLOSING));
	    }
	    
	}//end_constructor

	public void run(){
		
		System.err.println("\nStarting simulator with " + this.nodeList.size() +
				" nodes, " + edgeList.size() + " edges, and " +
				this.agentList.size() + " agents.");
		
		
		System.err.println("Number of Landmarks: " + this.listOfLandmark.size());
		System.err.println("Number of Landmarks to cross: " + this.numbLandmarkToCross);
		for(Integer land : this.listOfLandmarkToCross) {
			System.err.print(Node.getNodeById(land).getId()+" ");
		}System.err.println();

		
		System.err.println("Visual error ratio:  " +  (1.0 - this.agentList.get(0).getP()) + "\n" +
				"Advice error ratio: " + (1.0 - Node.getNodeById(this.listOfLandmark.get(0)).getLandmark().getP()) );

		System.err.println("Start node: " + Node.getStartNode() + "\n" +
				"Target node: " + Node.getEndNode() + "\n");

		this.dataCollector.reportData("Simulator " + this + " started! nodes: " +
				this.nodeList.size() + " edges " + edgeList.size() +
				" agents: " + this.agentList.size() + "\n");
		
		int i = 0;
		int j = 0;


		Node majorityNode = null;

		
		while(i == 0){
			
			Node actualGlobalNode = this.agentList.get(0).getCurrentNode();			
			Scenario.findPath( actualGlobalNode.getId(), this.listOfLandmarkToCross.get(contLandmark));
			if(running){
			

				try{
				    Thread.sleep(100);		
				} catch (InterruptedException e){
				    e.printStackTrace();
				}

				this.network.repaint();

				shuffleList(this.agentExecutionList);
				Enumeration<Agent> localEnumeration = this.agentExecutionList.elements();
												
				dataCollector.showStepByStep(j);
							
				
				while (localEnumeration.hasMoreElements()){
					localEnumeration.nextElement().step(); 
				}
				
				majorityNode = Agent.getTheMajority(nodeList);
					
				this.dataCollector.compute(majorityNode);

				Agent.setTheMajority(majorityNode, this.agentList);
					
					
				if(this.dataCollector.stepByStep) {
					System.out.println("[::] The majority is: " + majorityNode.getId() + "\n\n");
				}							
				
				this.dataCollector.step();
				this.dataCollector.reportData(" T " + j + " - " +
						this.dataCollector.missingNodesAverage + " " +
						this.dataCollector.missingEdgesAverage + "\n");
				j++;
				
				if (this.runLength > 0){ //runLenght = -1
					i = (j < this.runLength) ? 0 : 1;
				}else{
					i = ((this.moveableNodes) || (this.dataCollector.missingEdgesAverage != 0.0D)) ? 0 : 1;
				}
				if(one_step){
					running=false;
					one_step=false;
				}
				
				if(dataCollector.isFinish(majorityNode)) {this.network.repaint();break;} 
				
			}
			
			Thread.yield();
			
		}
		
		
		String path = new File(".").getAbsolutePath().toString() + "/results/" + this.output;
		String path_cspt = new File(".").getAbsolutePath().toString() + "/results/" + this.output.split("\\.")[0] + "-consumption.txt";
		
		try{
			
			FileWriter fileWriter = new FileWriter(path,true);
			fileWriter.write(Double.toString(dataCollector.getOutput()) + "\n");
			fileWriter.close();
			
			FileWriter fileWriter_cspt= new FileWriter(path_cspt,true);
			fileWriter_cspt.write(Double.toString(dataCollector.consumption_getOutput()) + "\n");
			fileWriter_cspt.close();
	
		  }
		catch (Exception e){
		    System.err.format("Exception occurred trying to write '%s' or '%s'.", path, path_cspt );
		    e.printStackTrace();
		}
				
		//GUI.getMainFrame().dispatchEvent(new WindowEvent(GUI.getMainFrame(), WindowEvent.WINDOW_CLOSING)); // Close the simulation. 
	}//end_run()

	public Network3D getNetwork(){
		return this.network;
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

	public static void clean(){
		nodeList.clear();
		edgeList.clear();
		agentList.clear();		
	}
	
	public static Record getDataCollector() {
		return dataCollector;
	}

	

}
