package src.network;

import java.util.Vector;

import src.Node;
import src.Record;

public class Torus {

    private static final double R= 0.7d;//major radius
    private static final double r= 0.3d;//minor radius
    private static final double ratio=0.1;//nodes per ring



    public static void calculateEdges(Vector<Node> nodeList, Record dataCollector){

        int i,j,k;
        int nodesPerNS=0;
        int nodesPerEW=0;
        Node u,v;


        int nodesInRing= (int) (nodeList.size()*ratio);
        int ringsInTorus=nodeList.size()/nodesInRing;

        int ringCounter=0;
        int nodeInRing=0;

        if(nodesInRing>nodeList.size()){
            nodesInRing=nodeList.size();
        }

        if(nodeList.size()%nodesInRing!=0){
            ringsInTorus++;
        }

        nodesPerNS=nodesInRing;
        nodesPerEW=ringsInTorus;

        System.out.println("------------------------------");
        System.out.println("Nodes in torus: "+nodeList.size());
        System.out.println("Number of NS-rings: "+ringsInTorus );
        System.out.println("Nodes per NS-ring: "+nodesPerNS);
        System.out.println("Nodes per EW-ring: "+nodesPerEW);
        System.out.println("------------------------------");


        int lastNodeLinked = 0;
        Node localNode1=null;

        for(i=0;i<nodeList.size();i++){
            localNode1 = nodeList.get(i);

            //localNode1.clearConnections();

            /* Torus equation
             *  x=(R + r*cos(alpha) )cos(beta)
             *  y=(R + r*cos(alpha) )sen(beta)
             *  z=r*sen(alpha)
             *
             */

            double alpha = ((2d*Math.PI)/(double)nodesInRing)*(double)nodeInRing;
            double beta = ((2d*Math.PI)/(double)ringsInTorus)*(double)ringCounter;


            double x=(R+r*Math.cos(alpha))*Math.cos(beta);
            double y=(R+r*Math.cos(alpha))*Math.sin(beta);
            double z=r*Math.sin(alpha);

            localNode1.setPosition(x, y, z);

            //all nodes of a ring, but the last
            if(!((i+1)%nodesInRing==0)){

                if((i+1)<nodeList.size()){
                    u = nodeList.get(i);
                    u.connectToRing(ringCounter);
                    Utils.connectNodes(i,i+1);

                    //North Port::
                    u.setNorthPort(i+1);

                    //South Port::
                    //special case, the first node
                    if(i%nodesInRing==0){
                        //since it must be preceded by the last node
                        u.setSouthPort(i+nodesInRing-1);
                    }else{
                        //otherwise, intermediary nodes follow rationale
                        u.setSouthPort(i-1);
                    }
                    //System.out.println("Linking node "+i+" to node "+i+1+" (south)!\n");
                }

            //last node of a ring
            }else{
                u = nodeList.get(i);//current node
                u.connectToRing(ringCounter);
                k=i+1-nodesInRing;//last node of the ring

                //connect last node to the first one
                Utils.connectNodes(i,k);

                //North Port::(first node)
                u.setNorthPort(k);

                //South Port:(precedent one)
                u.setSouthPort(i-1);

                //once we have closed the ring, we can now
                //link the EW-rings (or slices), assuming that already another
                //NS-ring is in place.


                //i.e., we connect the slices of NS-ring 1 to NS-ring 0;
                //from NS-ring 2 to NS-ring 1; from NS-ring 3 to NS-ring 2, ...

                //e.g., first time, i=19; second time, i=29 ...
                if(i>nodesInRing){//e.g., ring 1 to ring 0
                    k=0;
                    for(j=0;j<nodesInRing;j++){
                        k=i-j-nodesInRing;
                        Utils.connectNodes(i-j,k);//e.g., 19 & 9

                        u = nodeList.get(i-j);
                        u.setEastPort(k);
                        u.setWestPort(i-j+nodesInRing);//e.g., 19 & 29

                    }//enfFor
                }//endIf

            }

            nodeInRing++;//we added a new node in the ring

            //did we complete a NS-ring?
            //if so, let's increment counter and reset nodeInRing-counter
            if(nodeInRing==nodesInRing){
                ringCounter++;
                nodeInRing=0;
            }

        }//end-for



        //Connect the slices of ring 0 to ring n-1,
        //and set the pending ports (west and east)
        k=0;
        for(i=0;i<nodesInRing;i++){
            k=nodeList.size()-nodesInRing+i;
            Utils.connectNodes(i,k);
            u = nodeList.get(i);
            u.setEastPort(i+(nodesInRing*(ringsInTorus-1)));
            u.setWestPort(i+nodesInRing);
        }//endfor

        dataCollector.setNumNodesNS(nodesPerNS);
        dataCollector.setNumNodesEW(nodesPerEW);

    }

}//class_torus
