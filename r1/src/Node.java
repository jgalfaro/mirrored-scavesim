import java.util.Enumeration;
import java.util.Vector;

public class Node{
  protected boolean currentlyUp = true;
  protected Vector<Agent> agents = new Vector<Agent>();
  protected Vector<Node> reachableNodes = new Vector<Node>();
  protected String name;

  public Node(String pName){
    this.name = pName;
  }

  public boolean moveAgentTo(Agent pAgent, Node pNode){
    if ((this.agents.contains(pAgent)) && (this.reachableNodes.contains(pNode))){
      removeAgent(pAgent);
      pNode.acceptAgent(pAgent);
      return true;
    }
    return false;
  }

  public void clearConnections(){
    this.reachableNodes = new Vector<Node>();
  }

  public void connectTo(Node pNode){
    if (this.reachableNodes.contains(pNode) == false)
      this.reachableNodes.addElement(pNode);
  }

  public void removeAgent(Agent pAgent){
    if (this.agents.contains(pAgent) == false){
      System.err.println("ERROR! " + pAgent + " is missing.");
      return;
    }
    this.agents.removeElement(pAgent);
  }

  protected void acceptAgent(Agent pAgent){
    if (this.agents.contains(pAgent)){
      System.err.println("ERROR! " + pAgent + " already in place.");
      return;
    }
    this.agents.addElement(pAgent);
    pAgent.setCurrentNode(this);
  }

  public Vector<Agent> getAgents(){
    return this.agents;
  }

  public Vector<Node> getReachableNodes(){
    return this.reachableNodes;
  }

  public String getName(){
    return this.name;
  }

  public int howManyKnowMe(Vector pVector){
    int i = 0;
    Enumeration lEnum = pVector.elements();
    while (lEnum.hasMoreElements()){
      Agent lAgent = (Agent)lEnum.nextElement();
      if (lAgent.believesNode(this))
        i++;
    }
    return i;
  }

  public void dumpState(){
    System.out.print(" is " + ((this.currentlyUp) ? "running. " : "stopped. ") + "Has " + this.agents.size() + " agents and " + this.reachableNodes.size() + " reachable nodes. ");
    System.out.print("Connected to: ");
    Enumeration<Node> lEnum = this.reachableNodes.elements();
    while (lEnum.hasMoreElements()){
      System.out.print(((Node)lEnum.nextElement()).getName() + " ");
    }//while
    System.out.println("");
  }

  public String toString(){
    return this.name;
  }
}
