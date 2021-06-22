import java.util.Enumeration;
import java.util.Vector;

public class MobAgent extends Agent{

    protected Node calculateNextNode(){
        int i = this.steps + 1;
        Node localObject = this.currentNode;
        int j = 0;
        Vector<Node> localVector = null;
        Enumeration localEnumeration = this.currentNode.getReachableNodes().elements();

        while (localEnumeration.hasMoreElements()){
            Node localNode = (Node)localEnumeration.nextElement();
            Integer localInteger = (Integer)this.knownNodes.get(localNode);
            if (localInteger == null){
                if (j == 0){
                    j = 1;
                    i = -1;
                    localVector = new Vector<Node>();
                }
                localVector.addElement(localNode);
            }
            else if(localInteger.intValue() < i){
                localObject = localNode;
                i = localInteger.intValue();
                //System.err.println("MobAgent next step " + localInteger.intValue());
            }
            else if(localInteger.intValue() == i){
                System.err.println("WARNING! two nodes were last visited at steps=" + localInteger.intValue());
            }
        }
        if (j == 0){
            return localObject;
        }
        return ((Node)localVector.elementAt((int)(Math.random() * localVector.size())));
    }
}

