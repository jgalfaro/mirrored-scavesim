package src.network;

import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Vector3d;

import src.Edge;
import src.Node;
import src.Simulator;
public class Utils {
    public static void connectNodes(int start, int end){
        Node localNode1 = Simulator.get().nodeList.get(start);
        Node localNode2 = Simulator.get().nodeList.get(end);

        connectNodes(localNode1, localNode2);
    }

    public static void connectNodes(Node start, Node end){
        start.connectTo(end);
        end.connectTo(start);
        double weidth=Edge.randomWeight();
        Simulator.get().edgeList.put(new Edge(start, end,weidth), Boolean.TRUE);
        Simulator.get().edgeList.put(new Edge(end, start,weidth), Boolean.TRUE);
    }

    public static void moveNodes(Vector<Node> nodeList,Vector3d size){

        Enumeration<Node> localEnumeration = nodeList.elements();
        while (localEnumeration.hasMoreElements()){
            Node node= localEnumeration.nextElement();
            node.getInfo().getPosition().x += node.getInfo().xMove;
            node.getInfo().getPosition().y += node.getInfo().yMove;
            node.getInfo().getPosition().z += node.getInfo().zMove;
            if ((node.getInfo().getPosition().x < 0.0D) ||
                (node.getInfo().getPosition().x > size.x)){
                node.getInfo().xMove *= -1.0D;
                node.getInfo().getPosition().x += node.getInfo().xMove;
                node.getInfo().getPosition().x += node.getInfo().xMove;
            }
            if ((node.getInfo().getPosition().y >= 0.0D) &&
                (node.getInfo().getPosition().y <= size.y)){
                continue;
            }
            node.getInfo().yMove *= -1.0D;
            node.getInfo().getPosition().y += node.getInfo().yMove;
            node.getInfo().getPosition().y += node.getInfo().yMove;

            node.getInfo().getPosition().z += node.getInfo().zMove;
        }

    }
}
