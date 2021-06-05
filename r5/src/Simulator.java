package src;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import java.io.*;

import javax.vecmath.*;

import src.network.Torus;
import src.network.Utils;


public class Simulator implements Runnable{

    public static int showLabels=0;
    public static Vector<Node> nodeList;
    public static Hashtable<Edge,Boolean> edgeList;
    public static Vector<Agent> agentList;


    public enum NetworkType { TORUS};

    Vector<Agent> agentExecutionList;
    Network3D network;
    Record dataCollector;
    protected int initialNumAgents;
    protected int initialNumNodes;
    protected int initialNumTokens;
    protected int numRings=0;
    protected int numSlices=0;
    protected int testA2=0;
    protected Vector3d size;
    protected int BH = 0;
    protected int numBH = 0;
    protected int InitialNode1 = 0;
    protected int InitialNode2 = 0;
    protected int InitialNode3 = 0;


    public static final boolean drawGraphics = true;
    protected boolean moveableNodes = false;
    Random NetRNG = new Random(2001L);//random seed
    int runLength;

    /**CONTROL**/
    public static boolean finished = false;
    public static boolean running = false;
    public static boolean one_step = false;


    static NetworkType type = NetworkType.TORUS;

    @SuppressWarnings("unchecked")
	public Simulator(Record pRecord, int pNumNodes, int pNumAgents,
                         double x, double y, double z, Class<? extends MobAgent> pAgentClass,
                         float pMoveableNodes, int pRunLength, int pNumTokens, int pNumBH, int pShowLabels, int testA2){

        Node localNode=null;
        this.showLabels=pShowLabels;
        this.dataCollector = pRecord;
        this.initialNumNodes = pNumNodes;
        this.initialNumAgents = pNumAgents;
        this.initialNumTokens = pNumTokens;
        this.testA2=testA2;
        this.numBH = pNumBH;

        this.size=new Vector3d(x, y, z);
        this.runLength = pRunLength;
        this.moveableNodes = (pMoveableNodes > 0.0F);
        nodeList = new Vector<Node>();
        this.agentList = new Vector<Agent>();
        edgeList = new Hashtable<Edge,Boolean>();

        pRecord.setInputs(nodeList, edgeList, this.agentList);
        int j = 0;

        //test:: ant run -Dtype=1 -Dtokens=7 -Dnodes=20 -Dagents=2 -DBH=1 ...
        this.BH = (int)(Math.random() *  this.initialNumNodes); //test:: BH at node 40

        do{
            this.InitialNode1 = (int)(Math.random() *  this.initialNumNodes); //testA2:: InitialNode at node 17
        }while(this.BH == this.InitialNode1);

        do{
            this.InitialNode2 = (int)(Math.random() *  this.initialNumNodes); //testA2:: InitialNode at node 23
        }while((this.BH == this.InitialNode2) || (this.InitialNode1 == this.InitialNode2));

        do{
            this.InitialNode3 = (int)(Math.random() *  this.initialNumNodes); //testA2:: InitialNode at node 48
        }while((this.BH == this.InitialNode3) || (this.InitialNode1 == this.InitialNode3) || (this.InitialNode2 == this.InitialNode3));


        for (int i = 0; i < this.initialNumNodes; i++){
            int counter = j;
            j = (char)(counter + 1);
            localNode = new Node(Integer.toString(counter));

            NodeInfo localObject = new NodeInfo(this.NetRNG.nextDouble() * 2 - 1 ,this.NetRNG.nextDouble() * 2 - 1, this.NetRNG.nextDouble() * 2 - 1);

            if ((this.moveableNodes) && (Math.random() < pRunLength)){
                ((NodeInfo)localObject).xMove = (Math.random());
                ((NodeInfo)localObject).yMove = (Math.random());
                ((NodeInfo)localObject).zMove = (Math.random());
            }

            localNode.setInfo(localObject);
            nodeList.addElement(localNode);

        }

        if(this.numBH>0){
            localNode = (Node)nodeList.elementAt(this.BH);
            localNode.setBH();
        }

        calculateEdges();

        Node localObject = (Node)nodeList.elementAt(this.InitialNode1);
        String initialNodeName1 = localObject.getName();
        this.dataCollector.setInitialNode1Name(initialNodeName1);
        localObject = (Node)nodeList.elementAt(this.InitialNode2);
        String initialNodeName2 = localObject.getName();
        this.dataCollector.setInitialNode2Name(initialNodeName2);

        localObject = (Node)nodeList.elementAt(this.InitialNode3);
        String initialNodeName3 = localObject.getName();
        this.dataCollector.setInitialNode3Name(initialNodeName3);

        this.dataCollector.setNumAgents(this.initialNumAgents);
        this.dataCollector.setNumSlices(this.initialNumNodes*0.1);//TODO: parametrize this!

        for (int i = 0; i < this.initialNumAgents; i++){

            if(i==0){
                localObject = (Node)nodeList.elementAt(this.InitialNode1);
            }else if(i==1){
                localObject = (Node)nodeList.elementAt(this.InitialNode2);
            }else{
                localObject = (Node)nodeList.elementAt(this.InitialNode3);
            }


            Agent localAgent = null;
            try{
                localAgent = pAgentClass.newInstance();
                localAgent.setRecord(this.dataCollector);
                localAgent.alive=true;
                localAgent.AlgoStatus="single";
                localAgent.setPhase(0);
                if(this.testA2==1){
                    localAgent.setName("A"+Integer.toString(i+1+1));
                }else{
                    localAgent.setName("A"+Integer.toString(i+1));
                }
                switch (Simulator.type) {
                case TORUS:
                    localAgent.setNetworkType("TORUS");
                    break;
                default:
                    localAgent.setNetworkType("TORUS");
                }//switch
            }
            catch (Exception localException){
                System.err.println("Error creating agents from class " + pAgentClass);
                System.exit(0);
            }
            this.agentList.addElement(localAgent);
            ((Node)localObject).acceptAgent(localAgent);
        }
        this.agentExecutionList = (Vector<Agent>) this.agentList.clone();
        this.network = new Network3D(nodeList,this.agentList, (int)this.size.x, (int)this.size.y);
        this.network.setRecord(this.dataCollector);

    }//end_constructor

    public void run(){
        if(this.agentExecutionList.size()>0){//we stop the simulation
            int i = 0;
            int j = 0;
            int k = 0;
            int TotalMoves=0;
            int SurvivalList=0;

            Agent localAgent=null;

            try {
                Thread.sleep(100);
                //increase == delay speed
                //decrease == faster
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            this.dataCollector.reportData("Starting simulator with " + nodeList.size() +
                                          " nodes, " + this.dataCollector.getNumSlices() + " slices, and " +
                                          this.agentList.size() + " agents\n");

            if(this.numBH>0){
                this.dataCollector.reportData("BH at node " + this.BH + "\n");
            }

            this.dataCollector.reportData("Agent A1 at node "+this.InitialNode1+",");
            this.dataCollector.reportData("Agent A2 at node "+this.InitialNode2+",");
            this.dataCollector.reportData("Agent A3 at node "+this.InitialNode3+"\n");


            //         this.dataCollector.reportData("Simulator " + this + " started! nodes: " +
            //                                       nodeList.size() + ";edges: " + edgeList.size() +
            //                                       ";agents: " + this.agentList.size() + "\n");

            this.dataCollector.step();

            //this.dataCollector.reportData("\nNodes to visit=" + (int) this.dataCollector.missingNodes + "\n");

            // ; Pending Links[" +
            //                                           this.dataCollector.missingEdgesAverage + "]\n");

            //         this.dataCollector.reportData("Step[" + j + "], Nodes[" +
            //                                       this.dataCollector.missingNodesAverage + "], Edges[" +
            //                                       this.dataCollector.missingEdgesAverage + "]\n");

            while(i == 0 || finished ){
                if(running){
                    try {
                        Thread.sleep(100);
                        //increase == slower
                        //decrease == faster
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.network.repaint();
                    this.moveableNodes=false;
                    if(this.moveableNodes){
                        Utils.moveNodes(nodeList, size);
                        calculateEdges();
                    }

//                     if (this.agentExecutionList.size() != this.agentList.size()){
//                         System.err.println("Warning - agent list changed and now execution is broken.");
//                     }

                    shuffleList(this.agentExecutionList);
                    Enumeration<Agent> localEnumeration = this.agentExecutionList.elements();
                    while (localEnumeration.hasMoreElements()){
                        localAgent=localEnumeration.nextElement();
                        if(localAgent.alive){
                            localAgent.step();
                        }else{
                            TotalMoves+=localAgent.getMoves();
                            this.agentExecutionList.removeElement(localAgent);
                            //this.agentList.removeElement(localAgent);
                            this.network.updateAgentList(this.agentExecutionList);
                        }
                    }

                    j++;//executed steps
                    if(this.agentExecutionList.size()<1){//we stop the simulation
                        finished=false;
                        i=1;
                    }else{

                        this.dataCollector.step();
                        this.dataCollector.reportData("\nNodes to visit=" + (int) this.dataCollector.missingNodes + "\n");
                        //Pending Links[" + this.dataCollector.missingEdgesAverage + "]\n");

                        if (this.runLength > 0){
                            i = (j < this.runLength) ? 0 : 1;//more steps that maxNumberOfSteps (simulator parameter, by default -1)
                        }else{
                            i = ((this.moveableNodes) || (this.dataCollector.missingNodes != 1)) ? 0 : 1;
                        }

                    }
                    if(one_step){
                        running=false;
                        one_step=false;
                    }

                    //this.dumpState();
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread.yield();
            }

            if(this.dataCollector.missingNodes == 1){
                System.out.print("The BH should be node "+this.dataCollector.missingNodeName+"\n");
            }


            SurvivalList=0;
            Enumeration<Agent> localEnumeration = this.agentExecutionList.elements();
            while (localEnumeration.hasMoreElements()){
                localAgent=localEnumeration.nextElement();
                if(localAgent.getAlive()){
                    SurvivalList++;
                }
                System.out.print("Agent "+localAgent.getName()+" moved "+localAgent.getMoves()+" times \n");
                TotalMoves+=localAgent.getMoves();
            }//while

            System.out.print("\n\n*************************************\n");
            System.out.print("Summary: Total moves in this simulation = "+TotalMoves+" moves \n");
            System.out.print("Summary: Total agents alive after the simulation = "+SurvivalList+" agent(s) \n");

            if(this.numBH>0){
                System.out.print("Summary: BH was at node " + this.BH + "\n");
            }

            System.out.print("Summary: Agents were initially at nodes [A1,"+this.InitialNode1+"], [A2,"+this.InitialNode2+"], [A3,"+this.InitialNode3+"],\n");
            System.out.print("*************************************\n");
        }

    }//end_run()

    public Network3D getNetwork(){
        return this.network;
    }

    protected void calculateEdges(){
        edgeList = new Hashtable<Edge,Boolean>();
        if (this.dataCollector != null){
            this.dataCollector.setEdgeList(edgeList);
        }

        /* Type of network
         * 1 Torus
         * */

        switch (type) {
        case TORUS: //Torus
            Torus.calculateEdges(nodeList, this.dataCollector);
            break;

        default:
            Torus.calculateEdges(nodeList, this.dataCollector);

        }
    }

    public void dumpState(){
        System.out.println("Simulator started with " + this.nodeList.size() +
                           " nodes, " + this.initialNumTokens + " tokens, and " +
                           this.agentList.size() + " agent(s)\n");
        if(this.numBH>0){
            System.out.println("BH is at node " + this.BH + "\n");
        }else{
            System.out.println("Network does not contain any BH \n");
        }

        System.out.print("Agents initially at nodes [A1,"+this.InitialNode1+"], [A2,"+this.InitialNode2+"], [A3,"+this.InitialNode3+"],\n");

        System.out.println("System state::\n");
        Enumeration<Node> localEnumeration = nodeList.elements();
        while (localEnumeration.hasMoreElements()){
            Node localNode = (Node)localEnumeration.nextElement();
            //NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(localNode);
            localNode.dumpState();
            localNode.dumpTokenList();
            //localNode.dumpStateNS3();
            //localNode.dumpStateNeato();
        }
    }

    public void shuffleList(Vector pVector){
        int i = pVector.size();
        while (i > 1){
            int j = (int)(Math.random() * i);
            i--;
            Object lObject = pVector.elementAt(j);
            pVector.setElementAt(pVector.elementAt(i), j);
            pVector.setElementAt(lObject, i);
        }
    }

    public static void clean(){
        nodeList.clear();
        edgeList.clear();
        agentList.clear();

    }

}
