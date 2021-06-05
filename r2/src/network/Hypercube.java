package src.network;

import java.util.ArrayList;
import java.util.Vector;

import src.Node;

public class Hypercube {

	private static void linkGroupNodesHypercube(ArrayList<Integer> listA, ArrayList<Integer> listB) {
		for (int i = 0; i < listA.size(); i++) {
			if (listB.size() > i)
				Utils.connectNodes(listA.get(i), listB.get(i));
		}
	}

	private static void situateNodes(Vector<Node> nodeList){


		// Nodes position data
		double x0 = -1d;
		double y0 = -1d;
		double z0 = -1d;
		double sizeBigCube= 0.4d;
		double sizeSmallCube= 0.2d;

		double incr = 0.25d;


		int hypercubeCount=0;
		//Make hypercubes of 16 nodes and connect with each hypercube
		for (Node node : nodeList) {

			//4-hypercube finished
			//Move the initial point
			if(hypercubeCount==16){
				x0=x0+sizeBigCube+incr;

				//Line finished
				if(x0+sizeBigCube>1){
					y0=y0+sizeBigCube+incr;
					x0=-1d;

					//Square complete
					if(y0+sizeBigCube>1){
						z0=z0+sizeBigCube+incr;
						y0=-1d;
					}
				}
				hypercubeCount=0;
			}

			double x=x0;
			double y=y0;
			double z=z0;
			double size=sizeBigCube;
			int cubeCount=hypercubeCount;

			//When is the interior cube
			if(hypercubeCount>7){
				x+=(sizeBigCube-sizeSmallCube)/2d;
				y+=(sizeBigCube-sizeSmallCube)/2d;
				z+=(sizeBigCube-sizeSmallCube)/2d;

				size=sizeSmallCube;

				cubeCount=hypercubeCount-8;
			}
			//left face
			if(cubeCount%2!=0&&cubeCount!=0)
				x+=size;

			//rear faces
			if(cubeCount>=4)
				z+=size;

			//top face
			if(cubeCount==2||cubeCount==3||cubeCount==6||cubeCount==7){
				y+=size;
			}

			node.setPosition(x, y, z);


			hypercubeCount++;
		}
	}
	public static void calculateEdges(Vector<Node> nodeList) {

		boolean trace = false;

		int numNodes = nodeList.size();
		// Calc dimensions of hypercube log2(numNodes)
		double dimensions = Math.log(numNodes) / Math.log(2);

		if(trace)
			System.out.println("numNodes=" + numNodes + ", dimensiones=" + dimensions);

		// To group nodes in 2^x groups of nodes

		for (int sizeGroup = 1; sizeGroup <= numNodes / 2; sizeGroup *= 2) {
			ArrayList<Integer> listA = new ArrayList<Integer>();
			ArrayList<Integer> listB = new ArrayList<Integer>();
			if(trace)
				System.out.println("Uniendo en grupos de " + sizeGroup);

			boolean isFirstGroup = true;

			int beginLastComplete = 0;
			for (int i = 0; i < numNodes; i++) {
				if (isFirstGroup)
					listA.add(i);
				else
					listB.add(i);
				if(trace){
					System.out.println("ListA:" + listA);
					System.out.println("ListB:" + listB);
				}
				if ((i + 1) % sizeGroup == 0) {
					// Link both groups
					if (!isFirstGroup) {
						linkGroupNodesHypercube(listA, listB);

						listA.clear();
						listB.clear();
						beginLastComplete = i - sizeGroup;
					}
					// Change group
					isFirstGroup = !isFirstGroup;

				}
				// Last node
				if (numNodes - 1 == i) {
					if (!listB.isEmpty()) {
						linkGroupNodesHypercube(listA, listB);
					} else {

						int indexLastGroup = beginLastComplete;

						ArrayList<Integer> listPreviousGroup = new ArrayList<Integer>();

						while (indexLastGroup < beginLastComplete + sizeGroup) {
							listPreviousGroup.add(indexLastGroup);
							indexLastGroup++;
						}
						linkGroupNodesHypercube(listPreviousGroup, listA);
					}
				}// End Last node
			}
		}
		//Move nodes
		situateNodes(nodeList);

	}
}
