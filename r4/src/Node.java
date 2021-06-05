package src;

import java.util.Enumeration;
import java.util.Vector;

import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.geometry.Sphere;

public class Node {

	protected Vector<Agent> agents = new Vector<Agent>();
	protected boolean currentlyUp = true;
	private TransformGroup graphicNode=null;
	private NodeInfo info;
	protected String name;

	protected Vector<Node> reachableNodes = new Vector<Node>();


	public Node(String pName) {
		this.name = pName;
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

	public TransformGroup getGraphicNode() {
		return graphicNode;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public String getName() {
		return this.name;
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

	public void setGraphicNode(TransformGroup graphicNode) {
		this.graphicNode = graphicNode;
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
		return this.name;
	}

	public void changeColorNode(Color3f color){
		/*TODO: Se debe cambiar el color de los nodos aqui*/
		if(graphicNode!=null){
			if(graphicNode.getChild(0) instanceof Sphere){
				//System.out.println("Change color to:" +color);
				/*Sphere sphere =(Sphere)graphicNode.getChild(0);
				Appearance a=sphere.getAppearance();
				a.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
				a.setColoringAttributes(new ColoringAttributes(color,ColoringAttributes.FASTEST));
				sphere.setAppearance(a);*/
			}

		}
	}
}
