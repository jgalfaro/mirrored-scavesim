package src.network;

import java.util.Vector;

import src.Node;


public class Torus {

	private static final double R= 0.7d;
	private static final double r= 0.3d;	
	private static final double ratio=0.05;//Nodes for slice
	
	
public static void calculateEdges(Vector<Node> nodeList){
		

				
		int nodesInSlice= (int) (nodeList.size()*ratio);
		int slicesInTorus=nodeList.size()/nodesInSlice;
		
		if(nodesInSlice>nodeList.size())
			nodesInSlice=nodeList.size();

		if(nodeList.size()%nodesInSlice!=0)
			slicesInTorus++;
		
		int sliceInTorus=0;
		int nodeInSlice=0;

		System.out.println("------------------------------");
		System.out.println("Nodos a imprimir: "+nodeList.size());
		System.out.println("Nodos por Slice: "+nodesInSlice);
		System.out.println("Numero de Slice: "+slicesInTorus );
		System.out.println("------------------------------");

		int lastNodeLinked = 0;
		
		for(int i=0;i<nodeList.size();i++){
			Node localNode1 = nodeList.get(i);
			localNode1.clearConnections();

			//If do not close de slice AND is not the last node
			if((i+1)%nodesInSlice!=0||i==0){
				if((i+1)<nodeList.size())
					Utils.connectNodes(i,i+1);//Normal connection	
	
			}else {
				// if Slice complete & is not first slice
				//Connect in a ring all the slice
				Utils.connectNodes(i+1-nodesInSlice,i);

				if(i>nodesInSlice){// if Exist other comple slice
					//Link actual slice with previous					
					for(int j=0;j<nodesInSlice;j++){
						Utils.connectNodes(i-j,i-j-nodesInSlice);
					}
				}
				lastNodeLinked=i;
			}


			/*Torus equation
			 *  x=(R + r*cos(alpha) )cos(beta)
			 *  y=(R + r*cos(alpha) )sen(beta)
			 *  z=r*sen(alpha)
			 * */			
			
			double alpha = ((2d*Math.PI)/(double)nodesInSlice)*(double)nodeInSlice;
			double beta = ((2d*Math.PI)/(double)slicesInTorus)*(double)sliceInTorus;
			

			double x=(R+r*Math.cos(alpha))*Math.cos(beta);
			double y=(R+r*Math.cos(alpha))*Math.sin(beta);
			double z=r*Math.sin(alpha);

			localNode1.setPosition(x, y, z);
			
			nodeInSlice++;
			if(nodeInSlice==nodesInSlice){
				sliceInTorus++;
				nodeInSlice=0;
			}

		}
		
		//Link the last slice
		for(int i=lastNodeLinked;i<nodeList.size();i++){
			Utils.connectNodes(i,i-nodesInSlice);
		}
		
		//Link begin and end
		for(int i=0;i<nodesInSlice;i++){
			Utils.connectNodes(i,nodeList.size()-nodesInSlice+i);
		}
	}

}
