package src;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Record{
	Vector<Node> nodeList;
	Vector<Agent> agentList;
	Hashtable<Edge,Boolean> edgeList;
//	Hashtable<Node,NodeInfo> nodeInfoTable;
	boolean printOutData;
	public double correctNodesAverage;
	public double missingNodesAverage;
	public double correctEdgesAverage;
	public double missingEdgesAverage;
	public double wrongNodesAverage;
	public double wrongEdgesAverage;
	protected static int knownByCutoff = 50;

	public Record(boolean paramBoolean){
		this.printOutData = paramBoolean;
	}

	public void setInputs(Vector<Node> nodeList, Hashtable<Edge, Boolean> edgeList, Vector<Agent> agentList){
		this.nodeList = nodeList;
//		this.nodeInfoTable = nodeInfoTable;
		this.edgeList = edgeList;
		this.agentList = agentList;
	}

	public void setEdgeList(Hashtable<Edge, Boolean> edgeList){
		this.edgeList = edgeList;
	}

	public void step(){
		int i4;
		int i5;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int i2 = 0;
		Enumeration<Agent> localEnumeration1 = this.agentList.elements();
		while (localEnumeration1.hasMoreElements()){
			Edge localEdge;
			Agent localObject1 = localEnumeration1.nextElement();
			int i3 = 0;
			i4 = 0;
			i5 = 0;
			int i6 = 0;
			int i7 = 0;
			int i8 = 0;

			Enumeration<Node> localEnumeration2 = localObject1.getKnownNodes().keys();

			while (localEnumeration2.hasMoreElements()){
				Node localObject2 = localEnumeration2.nextElement();
				if (localObject1.believesNode(localObject2)){
					if (this.nodeList.contains(localObject2)){
						i6++;
					}
					else{
						i8++;
					}
				}
			}//while

				localEnumeration2 = localObject1.getRumouredNodes().keys();
			while (localEnumeration2.hasMoreElements()){
				Node localObject2 = (Node)localEnumeration2.nextElement();
				if ((!(localObject1.getKnownNodes().containsKey(localObject2))) && (localObject1.believesNode(localObject2))){
					if (this.nodeList.contains(localObject2)){
						i6++;
					}
					else{
						i8++;
					}
				}
			}

			i7 = this.nodeList.size() - i6;
			Enumeration<Edge> localObject2 = ((Agent)localObject1).getKnownEdges().keys();

			while (localObject2.hasMoreElements()){
				localEdge = localObject2.nextElement();
				if (localObject1.believesEdge(localEdge)){
					if (this.edgeList.containsKey(localEdge)){
						i3++;
					}
					else{
						i5++;
					}
				}
			}//while

				localObject2 = localObject1.getRumouredEdges().keys();
				while (localObject2.hasMoreElements()){
					localEdge = localObject2.nextElement();
					if ((!(localObject1.getKnownEdges().containsKey(localEdge))) && (localObject1.believesEdge(localEdge))){
						if (this.edgeList.containsKey(localEdge)){
							i3++;
						}
						else{
							i5++;
						}
					}
				}

				i4 = this.edgeList.size() - i3;
				reportData(i7 + "," + i4 + " ");
				l += i6;
				i1 += i7;
				i2 += i8;
				i += i3;
				j += i4;
				k += i5;

		}

		Enumeration<Node> localObject1 = this.nodeList.elements();
		while (localObject1.hasMoreElements()){
			Node localNode = localObject1.nextElement();
			i4 = localNode.howManyKnowMe(this.agentList);
			localNode.getInfo().knownBy=i4;
//			this.nodeInfoTable.get(localNode).knownBy = i4;
			i5 = localNode.getAgents().size();
			reportData(i4 + ":" + i5 + " ");
		}

		double d = this.agentList.size();
		this.correctNodesAverage = (l / d);
		this.missingNodesAverage = (i1 / d);
		this.wrongNodesAverage = (i2 / d);
		this.correctEdgesAverage = (i / d);
		this.missingEdgesAverage = (j / d);
		this.wrongEdgesAverage = (k / d);
	}

	public void reportData(String strData){
		if (this.printOutData){
			System.err.print(strData);
		}
	}
}
