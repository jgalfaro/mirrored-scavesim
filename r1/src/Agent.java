import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Agent implements Runnable{
  protected int steps = 0;
  protected Node currentNode = null;
  protected Record dataCollector;
  protected Hashtable<Node, Integer> knownNodes = new Hashtable<Node, Integer>();
  protected Hashtable<Edge,Integer> knownEdges = new Hashtable<Edge, Integer>();
  protected Hashtable<Node, Integer> rumouredNodes = new Hashtable<Node, Integer>();
  protected Hashtable<Edge, Integer> rumouredEdges = new Hashtable<Edge, Integer>();
  protected Hashtable metAgents = new Hashtable();

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
    this.knownNodes.put(this.currentNode, new Integer(this.steps));
    updateKnownEdgesFromCurrentNode();
    updateRumoursFromAgents();
    Node localNode = calculateNextNode();
    if ((localNode != this.currentNode) && (this.currentNode.moveAgentTo(this, localNode) == false))
      System.err.println("Move for " + this + " to " + localNode + " failed!");
    this.steps += 1;
  }

  protected void updateKnownEdgesFromCurrentNode(){
    Enumeration localEnumeration = this.currentNode.getReachableNodes().elements();
    while (localEnumeration.hasMoreElements()){
      Node localNode = (Node)localEnumeration.nextElement();
      updateKnownEdge(this.knownEdges, this.currentNode, localNode);
    }
  }

  protected void updateRumoursFromAgents(){
    Enumeration localEnumeration = this.currentNode.getAgents().elements();
    while (localEnumeration.hasMoreElements()){
      Agent localAgent = (Agent)localEnumeration.nextElement();
      if (localAgent != this){
        this.dataCollector.reportData("le " + this + " " + localAgent + "\n");
        mergeTableFrom(this.rumouredNodes, localAgent.knownNodes, localAgent.steps);
        mergeTableFrom(this.rumouredNodes, localAgent.rumouredNodes, localAgent.steps);
        mergeTableFrom(this.rumouredEdges, localAgent.knownEdges, localAgent.steps);
        mergeTableFrom(this.rumouredEdges, localAgent.rumouredEdges, localAgent.steps);
      }
    }
  }

  protected void mergeTableFrom(Hashtable paramHashtable1, Hashtable paramHashtable2, int paramInt){
    Enumeration localEnumeration = paramHashtable2.keys();
    while (localEnumeration.hasMoreElements()){
      Object localObject = localEnumeration.nextElement();
      int i = paramInt - ((Integer)paramHashtable2.get(localObject)).intValue();
      if (!(paramHashtable1.containsKey(localObject))){
        paramHashtable1.put(localObject, new Integer(this.steps - i));
      }
      else{
        int j = this.steps - ((Integer)paramHashtable1.get(localObject)).intValue();
        if (j > i)
          paramHashtable1.put(localObject, new Integer(this.steps - i));
      }
    }
  }

  protected void dumpState(){
    System.out.print(this + " steps = " + this.steps + " ");
    Enumeration localEnumeration = this.knownNodes.keys();
    while (localEnumeration.hasMoreElements()){
      Node localNode = (Node)localEnumeration.nextElement();
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

  protected abstract Node calculateNextNode();

  public void setCurrentNode(Node paramNode){
    this.currentNode = paramNode;
  }

  public Node getCurrentNode(){
    return this.currentNode;
  }

  public Hashtable getKnownNodes(){
    return this.knownNodes;
  }

  public Hashtable getKnownEdges(){
    return this.knownEdges;
  }

  public Hashtable getRumouredNodes(){
    return this.rumouredNodes;
  }

  public Hashtable getRumouredEdges(){
    return this.rumouredEdges;
  }

  protected void updateKnownEdge(Hashtable paramHashtable, Node paramNode1, Node paramNode2){
    paramHashtable.put(new Edge(paramNode1, paramNode2), new Integer(this.steps));
    paramHashtable.put(new Edge(paramNode2, paramNode1), new Integer(this.steps));
  }
}
