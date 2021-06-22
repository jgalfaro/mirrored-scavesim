package src.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;

import src.Landmark;
import src.Network3D;
import src.Node;
import src.NodeInfo;
import src.Simulator;

public class Scenario {
	
	public static String f_nodes = null;
	public static String f_edges = null;	
    public static double[][] dist = null;
    public static int[][] path = null;
		
	    
	public static void linkNodes(Map<String, Collection<String>> edgeMap) {
		
		
		for (Node node : Simulator.nodeList) {
				
			if(edgeMap.get(node.getName()) != null) {
				for (String v : edgeMap.get(node.getName())) {
					Utils.connectNodes(node, Node.getNodeByName(v));					
				}
			}
		}

		
	}
	
	public static void setPositionNodes(Map<String, Double[]> map) {
	
		for (Node node : Simulator.nodeList) {
			node.setPosition(map.get(node.getName())[0]/2, map.get(node.getName())[1]/2, map.get(node.getName())[2]/5);
		}
		
		
	}
	
	public static void floydWarshall() throws IOException{
		
		boolean read = true; 
		String path_file = Paths.get("").toAbsolutePath().toString() + "/results/" + Simulator.shortest_path; 
			
		if (read) {
			
			String savedGameFile = path_file;
			path = new int[Simulator.numbOfNodes][Simulator.numbOfNodes];
			BufferedReader reader = new BufferedReader(new FileReader(savedGameFile));
			String line = "";
			int row = 0;
			while((line = reader.readLine()) != null)
			{
			   String[] cols = line.split(","); //note that if you have used space as separator you have to split on " "
			   int col = 0;
			   for(String  c : cols)
			   {
				   path[row][col] = Integer.parseInt(c);
			      col++;
			   }
			   row++;
			}
			reader.close();
			
			
		}else {
			
			int V = Simulator.numbOfNodes;
			Vector<Node> nodes = Simulator.nodeList;
			
	        dist = new double[V][V]; 
	        path = new int[V][V]; 
	        
	        int i, j, k; 
	  
	        for (Node v1 : nodes) {
	        	for (Node v2 : nodes) { 
	        		
	            	i = v1.getId();
	            	j = v2.getId();
	            	
	            	dist[i][j] = Node.getWeight(i, j);
	            	
	            	if(i == j) {	            		
	            		path[i][j] = 0;
	            	}
	            	else if(dist[i][j] != Double.POSITIVE_INFINITY) {
	            		path[i][j] = i;
	            	}
	            	else {
	            		path[i][j] = -1;
	            	}
	            }
	        }
	        
	        for (Node v1 : nodes) { //k
	        	for (Node v2 : nodes) { //v 
	        		for (Node v3 : nodes) { //u 
	                	
	                	k = v1.getId();
		            	i = v2.getId();
		            	j = v3.getId();
	                	
	                    if (dist[i][k] != Double.POSITIVE_INFINITY && dist[k][j] != Double.POSITIVE_INFINITY && dist[i][k] + dist[k][j] < dist[i][j]) {
	                    	
	                        dist[i][j] = dist[i][k] + dist[k][j];
	                    	path[i][j] = path[k][j];
	                    }
	                } 
	            } 
	        }        
	        
	        
	        StringBuilder builder = new StringBuilder();
	        for(int i1 = 0; i1 < path.length; i1++)//for each row
	        {
	           for(int j1 = 0; j1 < path.length; j1++)//for each column
	           {
	              builder.append(path[i1][j1]+"");//append to the output string
	              if(j1 < path.length - 1)//if this is not the last row element
	                 builder.append(",");//then add comma (if you don't like commas you can use spaces)
	           }
	           builder.append("\n");//append new line at the end of the row
	        }
	        BufferedWriter writer = new BufferedWriter(new FileWriter(path_file));
	        writer.write(builder.toString());//save the string representation of the board
	        writer.close();
		}
		
					
    } 
	
	public static void findPath(int v, int u){
			
		Node.getNodeById(v).setNextNode( Node.getNodeById( path[u][v] ) );
	
	}
	
	
		
	public static void BFS(int t) {
		
		Simulator.currentTargetNode = Node.getNodeById(t);
		
		// Mark all the vertices as not visited(By default set as false)
		int V = Simulator.nodeList.size();
		int n;
        boolean visited[] = new boolean[V]; 
        
        LinkedList<Integer> queue = new LinkedList<Integer>(); 
  
        visited[t]=true; 
        queue.add(t); 
        int s;
        while (queue.size() != 0) { 
            
        	s = queue.poll(); 
        	
            Node currentNode  = Node.getNodeById(s);
            Enumeration localEnumeration = currentNode.getReachableNodes().elements();
            
            while (localEnumeration.hasMoreElements()){
            	
            	Node localNode = (Node)localEnumeration.nextElement();
                n = localNode.getId(); 
                
                if (!visited[n]) 
                {
                	localNode.setNextNode(currentNode);
                    visited[n] = true; 
                    queue.add(n); 
                }
            } 
        } 

               
	}

   
	
	public static void setNodes(double p) {
		
		Map<String, Double[]> nodesMap = new HashMap<String, Double[]>();
		int id = 0;
		Random NetRNG = new Random(518L);//random seed
		try{
		
			BufferedReader reader = new BufferedReader(new FileReader(f_nodes));
		    String line, node;

		    while ((line = reader.readLine()) != null){
		    	
		    	String[] parts = line.split(" ");
		    	node = parts[0];
		    	
		    	Node localNode = new Node(node, id);
		    	NodeInfo localObject = new NodeInfo(NetRNG.nextDouble() * 2 - 1 ,NetRNG.nextDouble() * 2 - 1, NetRNG.nextDouble() * 2 - 1);
		    	localNode.setInfo(localObject);
		    	
		    	if( node.contains("l") ){
		    		node = node.replace("l","");
		    		Landmark nodeLandmark = new Landmark(p); 
		    		localNode.setLandmark(nodeLandmark);
		    		localNode.setName(node);
					
		    		Simulator.listOfLandmark.add(id);
		    	}
		    	
		    	id += 1;		 
		    	Simulator.nodeList.addElement(localNode);   			    	
		    	nodesMap.put(node, new Double[] {Double.parseDouble(parts[1]) , Double.parseDouble(parts[2]), Double.parseDouble(parts[3])});
		    	
		    }
		    reader.close();
		    
		  }
		catch (Exception e){
		    System.err.format("Exception occurred trying to read '%s'.", f_nodes);
		    e.printStackTrace();
		}
		
		
		//
		
		Map<String, Collection<String>> edgeMap = new HashMap<String, Collection<String>>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f_edges));
			String line;

			while ((line = reader.readLine()) != null){
				String[] parts = line.split(" ");
				
				//Store the weights
				Node.getNodeByName(parts[0]).setWeight( Node.getNodeByName(parts[1]).getId() , Float.parseFloat(parts[2]) );
				Node.getNodeByName(parts[1]).setWeight( Node.getNodeByName(parts[0]).getId() , Float.parseFloat(parts[2]) );
				
				if(edgeMap.containsKey(parts[0])) {
					edgeMap.get(parts[0]).add(parts[1]);
					continue;
				}
				ArrayList<String> values = new ArrayList<String>();
				values.add(parts[1]);
				edgeMap.put(parts[0], values);
					
			}
			reader.close();
		}
		catch (Exception e){
			System.err.format("Exception occurred trying to read '%s'.", f_edges);
			e.printStackTrace();
		}
		
			
		
		//TO GRID
		Vector<Node> nodeList = Simulator.nodeList;
		
		if(Simulator.listOfLandmark.size() == 0) {
		
			int size = nodeList.size();
			boolean[] bool = new boolean[size];
			Arrays.fill(bool, Boolean.FALSE);
			ArrayList<Integer> numbers = new ArrayList<Integer>();
			
			for(int i = 0; i < size; i++){
				numbers.add(i);				
			}
			
			Collections.shuffle(numbers);
			
			for(int i = 0; i < (size/4); i++){
				bool[(Integer) numbers.get(i)] = true;
			}
					
			for(Node node: nodeList) {
				int id1 = node.getId();
				if (bool[id1]) {
					Landmark nodeLandmark = new Landmark(p); 
					node.setLandmark(nodeLandmark);
					Simulator.listOfLandmark.add(id1);
				}
			}
			
			
			for(Integer idLandmark : Simulator.listOfLandmark) {
				
				Node landmark3D = Node.getNodeById(idLandmark);
				String nameLandmark3D = landmark3D.getName();
				
				Node node3D = new Node(landmark3D.getName() + "3D",id);
				
				NodeInfo localObject = new NodeInfo(nodesMap.get(nameLandmark3D)[0],  nodesMap.get(nameLandmark3D)[1],  1.0);
				node3D.setInfo(localObject);
				
				Simulator.nodeList.addElement(node3D);   			    	
				Simulator.nodeList3D.addElement(node3D);
				nodesMap.put(nameLandmark3D + "3D", new Double[] { nodesMap.get(nameLandmark3D)[0], nodesMap.get(nameLandmark3D)[1],  1.0 });
		    			
				id+=1;
				
			}
			
			
		}
		
		
		Collections.shuffle(Simulator.listOfLandmark);
		
		for(int i = 0; i < Simulator.numbLandmarkToCross; i ++) {
			Simulator.listOfLandmarkToCross.add(  Simulator.listOfLandmark.get(i) );			
		}
		
		Node start = Node.getNodeById( Simulator.listOfLandmarkToCross.get(0) );
		Simulator.actualGlobalNode = start;			
		Node.setStartNode(start);
		Node.setEndNode(Node.getNodeById( Simulator.listOfLandmarkToCross.get(Simulator.listOfLandmarkToCross.size()-1) ));

		
				
		setPositionNodes(nodesMap);
		linkNodes(edgeMap);
		//Different ways to build a path.
		// 1. BFS path
		// 2. HamiltonianPath
		// 3. Dijkstra
		//BFS(0,1); //BFS PATH
		//findHamiltonianPath(nodeList.size(), 0); 
		//dijkstra(0);
		
		
	}
	
}
