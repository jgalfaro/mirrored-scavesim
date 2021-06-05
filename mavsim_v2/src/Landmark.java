package src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Color3f;

import src.Node;

public class Landmark extends Node{

	private double p;
	private double rand;
	
	public Landmark(double p) {
		super();
		this.p = p;
	}
	
	public double getP() {
		return p;
	}
	

	public void setP(double p) {
		this.p = p;
	}
	
	public double getRand() {
		return rand;
	}


	public void setRand(double rand) {
		this.rand = rand;
	}	
	
	public Node computeGetNextNode(Node currentNode) {
		
		Double rand = new Random().nextDouble();  // random value in range 0.0 - 1.0
		this.setRand(rand);	
				
		if(rand <= this.getP() ){
			return currentNode.getNextNode();
		}else { 
			ArrayList<Integer> ls = new ArrayList<Integer>();
			
			Enumeration localEnumeration = currentNode.getReachableNodes().elements();
	        while (localEnumeration.hasMoreElements()){
	        	Node localNode = (Node)localEnumeration.nextElement();
	            if( localNode.getId() != currentNode.getNextNode().getId() ) {
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
	}
	
	

}
