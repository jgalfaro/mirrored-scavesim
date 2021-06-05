package src;
import java.util.Enumeration;
import java.util.Vector;

public class MobAgent extends Agent{

    protected Node calculateNextNode(){


        Node localObject=null;

        if(this.NetworkType=="TORUS"){
            if(this.AlgoStatus=="single"){
                localObject=calculateNextScatteredSingle();
            }else if(this.AlgoStatus=="paired-ns"){
                localObject=calculateNextScatteredPairedNS();
            }else if(this.AlgoStatus=="paired-ew"){
                localObject=calculateNextScatteredPairedEW();
            }else if(this.AlgoStatus=="crossring-ns"){
                localObject=calculateNextCrossRingNS();
            }else if(this.AlgoStatus=="crossring-ew"){
                localObject=calculateNextCrossRingEW();
            }else if(this.AlgoStatus=="dangerousring-ns"){
                 localObject=calculateNextDangerousRingNS();
            }else if(this.AlgoStatus=="dangerousring-ew"){
                localObject=calculateNextDangerousRingEW();
            }else if(this.AlgoStatus=="done"){
                System.out.println(this.name+"::status=DONE!");
                localObject=this.currentNode;
            }else{
                System.out.println(this.name+"::status=none, movement-at-random :-) ");
                localObject=calculateNextNodeAtRandom();
            }
        }else{
            localObject=calculateNextNodeAtRandom();
        }
        return localObject;
    }



    //BYPASS: east,south
    protected Node calculateNextDangerousRingNS(){

        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Integer Pw=this.currentNode.getWestPort();
        Integer Ps=this.currentNode.getSouthPort();

        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",DangRING-NS,xCount="+this.xCount+",yCount="+this.yCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){

            this.previousNode.removeToken(localToken);

        }else{

            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            direction="**DONE**";
            System.out.print("[*****My PairedCWWT is gone********],");

        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)

        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens (not altered) were found and stolen],");
        }


        //special case, if currentNode contains CWWT of our pair (i.e., 2 CWWT, altered)
        //me move directly to phase 3 (i.e., bypassing dangerous node)
        // Why? since we altered its CWWT, but it died without realizing
        if(this.currentNode.AlteredCWWTOnPort(this.name)){
            System.out.print("event=[I see altered CWWT token of my pair],");
            next=Pn.toString();
            direction="north";
            this.yCount++;
            this.setPhase(3);

        //special case, if currentNode contains CWWT of our pair (i.e., 1 CWWT, unaltered)
        //me move directly to phase 3 (i.e., bypassing dangerous node)
        // Why? since it altered our CWWT, but then it moved and died (didn't take back its pairCWWT)
        }else if(this.currentNode.OneCWWTOnPort(this.name)){
            System.out.print("event=[I see the CWWT token of my pair],");
            next=Pn.toString();
            direction="north";
            this.yCount++;
            this.setPhase(3);

        }else{

            if(this.phase==1){//searching the dangerous ring, i.e., the UET of its pair

                if(this.currentNode.getUETsignal()){
                    next=Pe.toString();
                    direction="east";
                    this.setPhase(2);
                    this.xCount=1;

                    System.out.print("[Event:: UET found; entering the dangerous ring],");

                    //... and place its UET signal
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                    this.currentNode.addToken(localToken);

                }else if(this.yCount==(this.numNodesNS+1)){//homebase
                    next=Pe.toString();
                    direction="east";
                    this.setPhase(2);
                    this.xCount=1;

                    System.out.print("[Event:: HomeBase found; entering the dangerous ring],");

                    //... and place its UET signal
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                    this.currentNode.addToken(localToken);

                }else{
                    next=Pn.toString();
                    direction="north";
                    this.yCount++;

                    //System.out.print("[Event:: UET not found; keep exploring base ring],");

                }

            }else if(this.phase==2){//exploring the dangerous ring

                //keep moving east till we see the CWWT of our pair

                //if the CWWT is there, bypass the dangerous node
                if(this.currentNode.getCWWTPairedPort("east")){
                    next=Pn.toString();
                    direction="north";
                    this.yCount++;
                    this.setPhase(3);
                    System.out.print("[Event:: CWWT found; bypassing the dangerous node],");

                }else{
                    next=Pe.toString();
                    direction="east";
                    this.xCount++;
                    //System.out.print("[Event:: CWWT not found; keep going east],");
                }


            }else if(this.phase==3){//bypassing dangerous node

                next=Pe.toString();
                direction="east";
                this.xCount++;
                this.setPhase(4);
                System.out.print("[Event:: bypass, step 1],");

            }else if(this.phase==4){//bypassing dangerous node

                next=Pe.toString();
                direction="east";
                this.xCount++;
                this.setPhase(5);
                System.out.print("[Event:: bypass, step 2],");

            }else if(this.phase==5){//bypassing dangerous node
                next=Ps.toString();
                direction="south";
                this.yCount++;
                this.setPhase(2);
                System.out.print("[Event:: bypass, step 3],");

            }

        }//exception, first node contains the unaltered CWWT

        //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
        localToken = null;
        localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
        this.currentNode.addToken(localToken);

        ReachableNodes = this.currentNode.getReachableNodes().elements();
        while(ReachableNodes.hasMoreElements()){
            localNode = (Node)ReachableNodes.nextElement();
            localName=localNode.getName();
            if(localName.equals(next)){
                nextNode=localNode;
            }//if
        }//while

        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//calculateNextNodeDangerousRing-NS

    //BYPASS == north,west
    protected Node calculateNextDangerousRingEW(){

        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Integer Pw=this.currentNode.getWestPort();
        Integer Ps=this.currentNode.getSouthPort();

        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",DangRING-EW,xCount="+this.xCount+",yCount="+this.yCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){

            this.previousNode.removeToken(localToken);

        }else{

            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            direction="**DONE**";
            System.out.print("[*****My PairedCWWT is gone********],");

        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)


        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens (not altered) were found and stolen],");
        }


        //special case, if currentNode contains CWWT of our pair (i.e., 2 CWWT, altered)
        //me move directly to phase 3 (i.e., bypassing dangerous node)
        // Why? since we altered its CWWT, but it died without realizing
        if(this.currentNode.AlteredCWWTOnPort(this.name)){
            System.out.print("event=[I see altered CWWT token of my pair],");
            next=Pe.toString();
            direction="east";
            this.xCount++;
            this.setPhase(3);

        //special case, if currentNode contains CWWT of our pair (i.e., 1 CWWT, unaltered)
        //me move directly to phase 3 (i.e., bypassing dangerous node)
        // Why? since it altered our CWWT, but then it moved and died (didn't take back its pairCWWT)
        }else if(this.currentNode.OneCWWTOnPort(this.name)){
            System.out.print("event=[I see the CWWT token of my pair],");
            next=Pe.toString();
            direction="east";
            this.xCount++;
            this.setPhase(3);
        }else{
            if(this.phase==1){//searching the dangerous ring, i.e., the UET of its pair

                if(this.currentNode.getUETsignal()){//UET
                    next=Pn.toString();
                    direction="north";
                    this.setPhase(2);
                    this.yCount=1;

                    System.out.print("[Event:: UET found; entering the dangerous ring],");

                    //... and place its UET signal
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                    this.currentNode.addToken(localToken);

                }else if(this.xCount==(this.numNodesEW+1)){//home base

                    next=Pn.toString();
                    direction="north";
                    this.setPhase(2);
                    this.yCount=1;

                    System.out.print("[Event:: HomeBase found; entering the dangerous ring],");

                    //... and place its UET signal
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                    this.currentNode.addToken(localToken);

                }else{
                    next=Pe.toString();
                    direction="east";
                    this.xCount++;

                    //System.out.print("[Event:: UET not found; keep exploring base ring],");

                }

            }else if(this.phase==2){//exploring the dangerous ring

                //keep moving north till we see the CWWT of our pair

                //if the CWWT is there, bypass the dangerous node
                if(this.currentNode.getCWWTPairedPort("north")){
                    next=Pe.toString();
                    direction="east";
                    this.xCount++;
                    this.setPhase(3);
                    System.out.print("[Event:: CWWT found; bypassing the dangerous node],");

                }else{
                    next=Pn.toString();
                    direction="north";
                    this.yCount++;
                    //System.out.print("[Event:: CWWT not found; keep going east],");
                }


            }else if(this.phase==3){//bypassing dangerous node

                next=Pn.toString();
                direction="north";
                this.yCount++;
                this.setPhase(4);
                System.out.print("[Event:: bypass, step 1],");

            }else if(this.phase==4){//bypassing dangerous node

                next=Pn.toString();
                direction="north";
                this.yCount++;
                this.setPhase(5);
                System.out.print("[Event:: bypass, step 2],");

            }else if(this.phase==5){//bypassing dangerous node
                next=Pw.toString();
                direction="west";
                this.xCount++;
                this.setPhase(2);
                System.out.print("[Event:: bypass, step 3],");

            }

        }//exception

        //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
        localToken = null;
        localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
        this.currentNode.addToken(localToken);

        ReachableNodes = this.currentNode.getReachableNodes().elements();
        while(ReachableNodes.hasMoreElements()){
            localNode = (Node)ReachableNodes.nextElement();
            localName=localNode.getName();
            if(localName.equals(next)){
                nextNode=localNode;
            }//if
        }//while

        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//calculateNextNodeDangerousRing-EW



    protected Node calculateNextCrossRingNS(){
        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",CROSS-NS,xCount="+this.xCount+",yCount="+this.yCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){

            this.previousNode.removeToken(localToken);

        }else{

            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            direction="**DONE**";
            System.out.print("[*****My PairedCWWT is gone********],");
        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)


        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens (not altered) were found and stolen],");
        }


        if(this.phase==1){//I moved north over the basering (NS)

            //first, verify if there is the UET of its pair on the east port of this node.
            //if so, keep moving north
            if(this.currentNode.getUETsignal()){
                this.yCount++;
                next=Pn.toString();
                direction="north";

                if(this.yCount==this.numNodesNS){
                    //after this movement, the agent would have visited all the nodes in the base ring
                    //therefore, moving to procedure last loop-ns
                    this.AlgoStatus="dangerousring-ns";
                    this.setPhase(1);
                    this.yCount=1;
                    System.out.print("event=[FIRST step as DangRING-NS],");
                }

            }else{
                this.setPhase(2);
                this.xCount=1;
                next=Pe.toString();
                direction="east";

                //... and place the UET signal
                localToken = null;
                localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                this.currentNode.addToken(localToken);

            }

            //the agent places the CWWT again (as a paired agent, the cardinality is just 1)
            localToken = null;
            localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
            this.currentNode.addToken(localToken);

            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while


        }else if(this.phase==2){//phase==2, the agent is visiting a dangerous ring

            //keep moving east until all the nodes explored
            if(this.xCount==this.numNodesEW){//it is back to the ringbase
                this.setPhase(1);
                this.xCount=0;
                this.yCount++;
                next=Pn.toString();
                direction="north";

                if(!this.currentNode.removeMyUET(this.name)){
                    System.out.print("[Weird, I don't see my UET :-?],");
                }

                if(this.yCount==this.numNodesNS){
                    //after this movement, the agent would have visited all the nodes in the base ring
                    //therefore, moving to procedure last loop-ns
                    this.AlgoStatus="dangerousring-ns";
                    this.setPhase(1);
                    this.yCount=1;
                    System.out.print("event=[FIRST step as DangRING-NS],");
                }


            }else{//it should keep moving east
                next=Pe.toString();
                direction="east";
                this.xCount++;
            }

            //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
            localToken = null;
            localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
            this.currentNode.addToken(localToken);


            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while

        }//end-phase-2


        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//endCalculateNextScatteredCrossRingNS()

    protected Node calculateNextCrossRingEW(){
        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",CROSS-EW,xCount="+this.xCount+",yCount="+this.yCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){

            this.previousNode.removeToken(localToken);

        }else{

            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            direction="**DONE**";
            System.out.print("[*****My PairedCWWT is gone********],");

        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)


        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens (not altered) were found and stolen],");
        }



        if(this.phase==1){//I moved east over the basering (EW)

            //first, verify if there is the UET of its pair on the north port of this node.
            //if so, I keep moving east
            if(this.currentNode.getUETsignal()){
                this.xCount++;
                next=Pe.toString();
                direction="east";

                if(this.xCount==this.numNodesEW){
                    //after this movement, the agent would have visited all the nodes in the base ring
                    //therefore, moving to procedure last loop-ns
                    this.AlgoStatus="dangerousring-ew";
                    this.setPhase(1);
                    this.xCount=1;
                    System.out.print("event=[FIRST step as DangRING-EW],");
                }


            }else{
                this.setPhase(2);
                this.yCount=1;
                next=Pn.toString();
                direction="north";


                //... and place the UET signal
                localToken = null;
                localToken = new Token(this.name,currentNodeName,direction,"UET",false,1);
                this.currentNode.addToken(localToken);

            }

            //the agent places the CWWT again (as a paired agent, the cardinality is just 1)
            localToken = null;
            localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
            this.currentNode.addToken(localToken);


            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while


        }else if(this.phase==2){//phase==2, the agent is visiting a dangerous ring

            //keep moving north, until all the nodes explored
            if(this.yCount==this.numNodesNS){//it is back to the ringbase
                this.setPhase(1);
                this.yCount=0;
                this.xCount++;
                next=Pe.toString();
                direction="east";

                if(!this.currentNode.removeMyUET(this.name)){
                    System.out.print("[Weird, I don't see my UET :-?],");
                }

                if(this.xCount==this.numNodesEW){
                    //after this movement, the agent would have visited all the nodes in the base ring
                    //therefore, moving to procedure last loop-ns
                    this.AlgoStatus="dangerousring-ew";
                    this.setPhase(1);
                    this.xCount=1;
                    System.out.print("event=[FIRST step as DangRING-EW],");
                }

            }else{//it should keep moving north
                next=Pn.toString();
                direction="north";
                this.yCount++;
            }

            //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
            localToken = null;
            localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
            this.currentNode.addToken(localToken);


            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while

        }

        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//endCalculateNextScatteredCrossRingEW()

    protected Node calculateNextScatteredPairedEW(){//paired-agent-finding-the-base-in-EW
        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",PAIR-EW,xCount="+this.xCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){
            this.previousNode.removeToken(localToken);
        }else{

            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            System.out.print("[*****My PairedCWWT is gone********],");
        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)


        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens were found and stolen],");
        }


        //Then, keep moving east until all the nodes are explored
        if(this.xCount==this.numNodesEW){//it is back to its homebase

            if(this.currentNode.getBASEsignal()){//if there is already the signal,
                //the pair of this agent found the base first. Therefore, the NS is the
                //base ring. The agent changes the status to "crossring-ns", in phase 0
                this.AlgoStatus="crossring-ns";
                this.setPhase(1);
                this.yCount=1;
                this.xCount=0;
                next=Pn.toString();
                direction="north";
                System.out.print("event=[FIRST step as CROSS-NS],");

            }else{//since there is no BASE signal,
                //the pair of this agent is still exploring the NS
                //ring. Therefore, the EW is the base ring. The agent
                //places the BASE signal in the middle, changes its status to
                //"crossring-ew"
                this.AlgoStatus="crossring-ew";
                this.setPhase(1);
                this.yCount=0;
                this.xCount=1;
                next=Pe.toString();
                direction="east";
                System.out.print("event=[FIRST step as CROSS-EW],");

                //the EW is the BASERING.
                //the agent places one single token in the middle, i.e., the BASE Ring Progress Sign

                localToken = null;
                localToken = new Token(this.name,currentNodeName,"middle","CWWT",false,2);
                this.currentNode.addToken(localToken);

            }


            //RECALL ....
            //The agent does not need to sign with its UET, since it will do it in function crossring-ew


        }else{//it should keep moving east
            next=Pe.toString();
            direction="east";
            this.xCount++;
        }

        //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
        localToken = null;
        localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
        this.currentNode.addToken(localToken);


        if(true){
            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while
        }


        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//calculateNextScatteredPairedEW


    protected Node calculateNextScatteredPairedNS(){//paired-agent-finding-the-base-in-NS
        int i = this.steps + 1;
        boolean steal=false;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",PAIR-NS,xCount="+this.xCount+",step="+this.steps+",phase="+this.getPhase()+",");

        //First of all, the agent takes its CWWT token from the previous node (as a paired agent, cardinality = 1)

        localToken = null;
        localToken = this.previousNode.getMyPairedCWWT(this.name);
        if(localToken!=null){
            this.previousNode.removeToken(localToken);
        }else{
            this.AlgoStatus="done";
            this.alive=false;
            nextNode = this.currentNode;
            direction="**DONE**";
            System.out.print("[*****My PairedCWWT is gone********],");
        }


        //Second of all, the agent verifies the tokens of this new node.

        //a) if the agent sees two tokens on either north or east port,
        //it steals them (i.e., the other node will be changed to DONE)

        if(this.currentNode.StealIFTwoUnalteredTokensOnPort()){
            System.out.print("event=[two tokens were found and stolen],");
        }


        //Then, keep moving north until all the nodes are explored
        if(this.xCount==this.numNodesNS){//it is back to its homebase

            if(this.currentNode.getBASEsignal()){//if there is already the signal,
                //the pair of this agent found the base first. Therefore, the EW is the
                //base ring. The agent changes the status to "crossring-ew", in phase 0
                this.AlgoStatus="crossring-ew";
                this.setPhase(1);
                this.xCount=1;
                this.yCount=0;
                next=Pe.toString();
                direction="east";
                System.out.print("event=[FIRST step as CROSS-EW],");

            }else{//since there is no BASE signal,
                //the pair of this agent is still exploring the EW
                //ring. Therefore, the NS is the base ring. The agent
                //places the BASE signal in the middle, changes its
                //status to "crossring-ns"
                this.AlgoStatus="crossring-ns";
                this.setPhase(1);
                this.xCount=0;
                this.yCount=1;
                next=Pn.toString();
                direction="north";
                System.out.print("event=[FIRST step as CROSS-NS],");

                //the NS is the BASERING.
                //the agent places one single token in the middle, i.e., the BASE Ring Progress Sign

                localToken = null;
                localToken = new Token(this.name,currentNodeName,"middle","CWWT",false,2);
                this.currentNode.addToken(localToken);

            }

            //RECALL ....
            //The agent does not need to sign with its UET, since it will do it in function crossring-ns

        }else{//it should keep moving north
            next=Pn.toString();
            direction="north";
            this.xCount++;

        }

        //the agents places the CWWT again (as a paired agent, the cardinality is just 1)
        localToken = null;
        localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
        this.currentNode.addToken(localToken);


        if(true){
            ReachableNodes = this.currentNode.getReachableNodes().elements();
            while(ReachableNodes.hasMoreElements()){
                localNode = (Node)ReachableNodes.nextElement();
                localName=localNode.getName();
                if(localName.equals(next)){
                    nextNode=localNode;
                }
            }//while
        }


        System.out.print("nextNode="+next+",by port["+direction+"];\n");

        this.previousNode=this.currentNode;
        return (nextNode);

    }//calculateNextScatteredPairedNS


    protected Node calculateNextScatteredSingle(){//Algorithm_5.2_agent_still_single
        int i = this.steps + 1;
        String currentNodeName = this.currentNode.getName();
        Node localObject = this.currentNode;
        Node nextNode = this.currentNode;
        String direction=null;
        String nextString=null;
        String next=null;
        Node localNode=null;
        String localName=null;
        Integer localInteger=0;
        Token localToken=null;

        Integer Pn=this.currentNode.getNorthPort();
        Integer Pe=this.currentNode.getEastPort();
        Enumeration ReachableNodes = null;

        int myCWWT=0;
        int AnotherCWWT=0;

        int j = 0;
        Vector<Node> localVector = null;

        System.out.print(this.name+"::node="+currentNodeName+",SINGLE,xCount="+this.xCount+",step="+this.steps+",phase="+this.getPhase()+",");

        if(this.steps==0){//node got up right now, for first time
            this.numNodesNS=this.dataCollector.getNumNodesNS();
            this.numNodesEW=this.dataCollector.getNumNodesEW();
            myCWWT=1;//assume like if CWWT was at previous node

        }else{

            localToken = null;
            localToken = this.previousNode.getMyCWWT(this.name);
            if(localToken!=null){
                if (localToken.altered){
                    myCWWT=2;//another agent altered token
                    //the agent doesn't remove the CWWT, since it is altered (meaning that 1 of the two token is in the middle)
                    //and must remain there as a flag pointing to the homebase
                }else{
                    myCWWT=1;//token was there, unaltered
                    //the agent can, therefore, remove it from the previous node
                    this.previousNode.removeToken(localToken);
                }
            }else{
                myCWWT=0;//token wasn't there
            }

        }

        if((this.currentNode.AlteredCWWTOnPort(this.name))||(this.currentNode.getUETsignal())){
            //a pair is already on ... Agent must change to done!
                this.AlgoStatus="done";
                this.alive=false;
                nextNode = this.currentNode;
                System.out.print("event=[I see the sign of an existing pair. I'm DONE],");
        }else{

            AnotherCWWT=this.currentNode.containsOtherCWWT(this.name);

            if(AnotherCWWT==2){//the agent sees the CWWT of another agent, on the east port, and automatically changes its config.

                this.AlgoStatus="paired-ns";
                next=Pn.toString();
                direction="north";
                this.xCount=1;
                System.out.print("event=[FIRST step as PAIRED-NS],");

                ReachableNodes = this.currentNode.getReachableNodes().elements();

                while(ReachableNodes.hasMoreElements()){
                    localNode = (Node)ReachableNodes.nextElement();
                    localName=localNode.getName();
                    if(localName.equals(next)){
                        nextNode=localNode;
                    }
                }

                //I place my CWWT again (as a paired agent, the cardinality is just 1)
                localToken = null;
                localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
                this.currentNode.addToken(localToken);

                System.out.print("nextNode="+next+",by port["+direction+"];\n");


            }else if(AnotherCWWT==1){//the agent sees the CWWT of another agent, on the north port, and automatically changes its config.

                this.AlgoStatus="paired-ew";
                next=Pe.toString();
                direction="east";
                this.xCount=1;
                System.out.print("event=[FIRST step as PAIRED-EW],");

                ReachableNodes = this.currentNode.getReachableNodes().elements();

                while(ReachableNodes.hasMoreElements()){
                    localNode = (Node)ReachableNodes.nextElement();
                    localName=localNode.getName();
                    if(localName.equals(next)){
                        nextNode=localNode;
                    }
                }

                //I place my CWWT again (as a paired agent, the cardinality is just 1)
                localToken = null;
                localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
                this.currentNode.addToken(localToken);

                System.out.print("nextNode="+next+",by port["+direction+"];\n");



            }else if(myCWWT==0){//the CWWT is no longer there

                this.AlgoStatus="done";
                this.alive=false;
                nextNode = this.currentNode;
                System.out.print("event=[My CWWT is gone. I'm DONE],");


            }else if(myCWWT==2){//the CWWT was still at previous node, but altered

                if(localToken.port=="east"){

                    next=Pe.toString();
                    direction="east";
                    this.AlgoStatus="paired-ew";
                    System.out.print("event=[FIRST step as PAIRED-EW],");

                }else if(localToken.port=="north"){

                    next=Pn.toString();
                    direction="north";
                    this.AlgoStatus="paired-ns";
                    System.out.print("event=[FIRST step as PAIRED-NS],");

                }

                //since the agent discovered that is paired on its way back, it has already
                //visited its first node (either PAIRED-EW or PAIRED-NS), and therefore, it
                //increments its xCount to 2.
                this.xCount=2;

                ReachableNodes = this.currentNode.getReachableNodes().elements();

                while(ReachableNodes.hasMoreElements()){
                    localNode = (Node)ReachableNodes.nextElement();
                    localName=localNode.getName();
                    if(localName.equals(next)){
                        nextNode=localNode;
                    }
                }

                //I place a new CWWT (as a paired agent, the cardinality is just 1)
                localToken = null;
                localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,1);
                this.currentNode.addToken(localToken);


                //the agent also removes its altered CWWT
                if(this.previousNode.StealMyAlteredCWWT(this.name)){
                    System.out.print("event=[I have removed my Altered CWWT],");
                }else{
                    System.out.print("event=[Weird, my Altered CWWT was gone],");
                }


                System.out.print("nextNode="+next+",by port["+direction+"];\n");




            }else if(myCWWT==1){//the CWWT was still at previous node, unaltered

                if(this.getPhase()==0){//single agent exploring the initial NS-ring

                    //verify if agent explored all the nodes in the NS-ring
                    if(this.xCount==this.numNodesNS){//it should move east
                        next=Pe.toString();
                        direction="east";
                        this.setPhase(1);
                        this.xCount=1;
                    }else{//it should keep moving north
                        next=Pn.toString();
                        direction="north";
                        this.xCount++;
                    }

                    ReachableNodes = this.currentNode.getReachableNodes().elements();

                    while(ReachableNodes.hasMoreElements()){
                        localNode = (Node)ReachableNodes.nextElement();
                        localName=localNode.getName();
                        if(localName.equals(next)){
                            nextNode=localNode;
                        }
                    }

                    //I place my CWWT again::
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,2);
                    this.currentNode.addToken(localToken);

                    System.out.print("nextNode="+next+",by port["+direction+"];\n");

                }else if(this.getPhase()==1){//single agent exploring EW-rings


                    //System.out.print(this.name+"::node="+currentNodeName+",SINGLE,xCount="+this.xCount+",step="+this.steps+",phase="+this.getPhase()+",");

                    //verify if agent explored all the nodes in the EW-ring
                    if(this.xCount==this.numNodesEW){//it should move one position north, then next EW ring
                        next=Pn.toString();
                        direction="north";
                        this.xCount=0;
                    }else{//I should keep moving east
                        next=Pe.toString();
                        direction="east";
                        this.xCount++;
                    }

                    ReachableNodes = this.currentNode.getReachableNodes().elements();

                    while (ReachableNodes.hasMoreElements()){
                        localNode = (Node)ReachableNodes.nextElement();
                        localName=localNode.getName();
                        if(localName.equals(next)){
                            nextNode=localNode;
                        }
                    }

                    //I place my CWWT again::
                    localToken = null;
                    localToken = new Token(this.name,currentNodeName,direction,"CWWT",false,2);
                    this.currentNode.addToken(localToken);

                    System.out.print("nextNode="+next+",by port["+direction+"];\n");

                }//end_phase_1

            }//if(!Continue);

        }//else(not_UET,or_alteredCWWT)

        this.previousNode=this.currentNode;
        return (nextNode);

    }//endCalculateNextScatteredSingle(){


    protected Node calculateNextNodeAtRandom(){
        int i = this.steps + 1;
        Node localObject = this.currentNode;
        Node nextNode;
        int j = 0;
        Vector<Node> localVector = null;
        Enumeration localEnumeration = this.currentNode.getReachableNodes().elements();

        if(this.steps==0){
            System.out.println("step["+ this.steps+"]::Agent["+this.name+"] in Node["+this.currentNode+"]");
        }

        while (localEnumeration.hasMoreElements()){
            Node localNode = (Node)localEnumeration.nextElement();
            Integer localInteger = this.knownNodes.get(localNode);
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

            }
            else if(localInteger.intValue() == i){
                System.out.println("WARNING! two nodes were last visited at steps=" + localInteger.intValue());
            }
        }
        if (j == 0){
            System.out.println("step["+(this.steps+1)+"]::Agent["+this.name+"] in Node["+localObject.getName()+"] (already visited)");
            return localObject;
        }
        nextNode=(Node)localVector.elementAt((int)(Math.random() * localVector.size()));
        System.out.println("step["+(this.steps+1)+"]::Agent["+this.name+"] in Node["+nextNode.getName()+"]");
        return (nextNode);
    }

}//end_class_MobAgent


