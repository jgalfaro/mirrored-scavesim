package src.network;

import java.util.Enumeration;
import java.util.Vector;

import src.Node;

public class Random {
	private static final double DISTANCE = 0.4d;
	public static void calculateEdges(Vector<Node> nodeList){

		Enumeration<Node> localEnumeration1 = nodeList.elements();

		while (localEnumeration1.hasMoreElements()){
			Node localNode1 = (Node)localEnumeration1.nextElement();
			//			NodeInfo localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
			localNode1.clearConnections();
			Enumeration<Node> localEnumeration2 = nodeList.elements();
			while (localEnumeration2.hasMoreElements()){
				Node localNode2 = (Node)localEnumeration2.nextElement();
				//				NodeInfo localNodeInfo2 = (NodeInfo)this.nodeInfoTable.get(localNode2);
				if (localNode1 == localNode2){
					continue;
				}
				double d = (localNode1.getInfo().getPosition().x - localNode2.getInfo().getPosition().x) * (localNode1.getInfo().getPosition().x - localNode2.getInfo().getPosition().x) +
						(localNode1.getInfo().getPosition().y - localNode2.getInfo().getPosition().y) * (localNode1.getInfo().getPosition().y - localNode2.getInfo().getPosition().y)+
						(localNode1.getInfo().getPosition().z - localNode2.getInfo().getPosition().z) * (localNode1.getInfo().getPosition().z - localNode2.getInfo().getPosition().z);

				d= Math.sqrt(d);
				if ((d >= DISTANCE) || (d >= DISTANCE)){
					continue;
				}
				localNode1.connectTo(localNode2);
				
				Utils.connectNodes(localNode1,localNode2);
			}
		}
	}

}
