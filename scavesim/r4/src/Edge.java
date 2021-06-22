package src;
public class Edge{
    Node edgeFrom;
    Node edgeTo;

    public Edge(Node node1, Node node2){
        this.edgeFrom = node1;
        this.edgeTo = node2;
    }

    public boolean equals(Object pObject){
        if (this == pObject){
            return true;
        }

        if ((pObject == null) || (super.getClass() != pObject.getClass())){
            return false;
        }

        Edge lEdge = (Edge)pObject;

        return (
                (this.edgeFrom == lEdge.edgeFrom) &&
                (this.edgeTo == lEdge.edgeTo)
               );
    }

    public int hashCode(){
        return (65537 * this.edgeFrom.hashCode() + this.edgeTo.hashCode());
    }

    public String toString(){
        return this.edgeFrom.toString() + ">" + this.edgeTo.toString();
    }
}
