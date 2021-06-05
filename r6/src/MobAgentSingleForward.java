package src;

import src.Token.Direction;
import src.Token.TypeToken;

public class MobAgentSingleForward extends Agent {

    private int nCount = 0;
    private int xCount = 1;
    private Node checkPoint = null;
    private Node checkPoint2 = null;

    @Override
	protected Node calculateNextNode() {
        if (nCount == 0) {
            initData();
        }

        Node localObject = null;
        if (this.NetworkType == "TORUS") {
            switch (AlgoStatus) {
            case single:
                localObject = calculateSingle();
                break;
            case forward:
                localObject = calculateForward();
                break;
            case checking:
                localObject = calculateChecking();
                break;
            case done:
                System.out.println(this.name + "::status=DONE!");
                localObject = this.currentNode;
                break;
            default:
                System.err.println("WRONG STATE: It must no enter on this state");
                localObject = calculateNextNodeAtRandom();
                break;
            }
        } else {
            localObject = calculateNextNodeAtRandom();
        }
        previousNode = currentNode;
        return localObject;
    }

    private Node calculateForward() {
        Node nextNode = currentNode;
        Node Pn = Simulator.get().nodeList.get(currentNode.getNorthPort());
        Node Pe = Simulator.get().nodeList.get(currentNode.getEastPort());

        boolean atCheckPoint = false;


        if (currentNode.equals(checkPoint)){
            atCheckPoint = true;
            //System.out.println("" + name + " atCheckPoint = true");
        }

        if (!atCheckPoint) { //forward agent at a node other than its checkpoint
            if (isFirstOptionBlocked() && isSecondOptionBlocked()) {
                checkPoint2 = currentNode;
                AlgoStatus = AgentStatus.checking;
                System.out.println("" + name + ":: North and East ports of node " + currentNode.name+ " are blocked. Next Status: Checking." );
                nextNode = calculateChecking();
            }else{
                //System.out.println("" + name + " at node" + currentNode.name+ "[CODE test04]");
                nextNode = defaultMovement(currentNode);
            }
        } else{ //forward agent at its checkpoint
            System.out.println("" + name + " at checkpoint (node "+checkPoint.name+"). Verification in progress.");
            if ( !isFirstOptionBlocked() ){
                AlgoStatus = AgentStatus.single;
                System.out.println("" + name + ":: First option free. Change status to SINGLE");
                nextNode = goToNorth();
            } else if ( !isSecondOptionBlocked() ) {
                checkPoint = currentNode;
                System.out.println("** " + name +"::1st option still blocked. 2nd option free. So, agent "+name+" remains in status FORWARD, with checkpoint=="+checkPoint.name+".** \n");
                System.out.println("Agent "+name + " at node " + currentNode.name + " moves East.");
                System.out.println("(the first action of agent "+name+", once in the new NS ring, will be xCount--)");
                xCount=0; //second exception, it assumes 0 instead of 1, to override the xcount-- on the following NS-ring
                nextNode = goToEast();
            } else if (isSecondOptionBlocked() && isFirstOptionBlocked()) {
                System.out.println("" + name +":: First & Second options blocked.");
                // Second choise port is also blocked
                int actualTokens = currentNode.getMiddleTokensNumber();
                boolean E = false; // There is a change in the number of tokens
                // in the middle
                boolean F = false; // A port is unblocked
                System.out.println("" + name + " WAITING at " + currentNode.name);
                while (!E && !F) {
                    // There is a change in the number of tokens in the middle
                    if (actualTokens != currentNode.getMiddleTokensNumber()) {
                        E = true;
                    } else if (!isFirstOptionBlocked() || !isSecondOptionBlocked()) {
                        // A port is unblocked
                        F = true;
                    }
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("" + name +" NOT WAITING at " + currentNode.name);

                if (E) {
                    checkPoint = currentNode;
                    AlgoStatus = AgentStatus.checking;
                    System.out.println("" + name + " at node " + currentNode.name + " changes to CHECKING, checkPoint_1:" + checkPoint.name);
                } else {
                    if (!isSecondOptionBlocked()) {
                        System.out.println("" + name + " here!! at CODE test07 " + currentNode.name);
                        System.out.println("Agent "+name + " at node " + currentNode.name + " moves East, xCount == "+xCount);
                        xCount=1;
                        nextNode = goToEast();
                    } else {
                        //joaquin
                        System.out.println("" + name + " here!! at CODE test08 " + currentNode.name);
                        nextNode = goToNorth();
                    }
                }
            }
        }

        return nextNode;
    }

    private Node calculateChecking() {

        Node nextNode = null;
        Node u = currentNode;
        Node v = checkPoint;

        Token middleToken = new Token(this.name, currentNode.name, Direction.Middle, TypeToken.CWWT, false, 1);
        if (isFirstOptionBlocked(v)) {
            // is a token in first choice

            // Leave a token in the middle of the node and go to u
            v.addToken(middleToken);

            if (u.getMiddleTokensNumber() > 0) {
                // u has a token in the middle
                // Go back v and wait there
                //TODO: fix this!!!
                System.out.println("" + name + " at node " + currentNode.name + " Checking CODE 01 (jump to v)");
                nextNode = v;

            } else {
                // There is no token in v then
                if (isFirstOptionBlocked() && isSecondOptionBlocked()) {
                    // Both north and east ports are with tokens
                    v.removeToken(middleToken);
                    nextNode = u;
                    System.out.println("" + name + " at node " + currentNode.name + " Checking CODE 02 ( Change to FORWARD )");
                    AlgoStatus = AgentStatus.forward;

                } else if (isFirstOptionBlocked(v)) {
                    // The first choice is without token
                    nextNode = u;
                    System.out.println("" + name + " at node " + currentNode.name + " Checking CODE 03 ( Change to FORWARD )");
                    AlgoStatus = AgentStatus.forward;

                } else {
                    // remember this node as checkPoint and Forward
                    checkPoint = currentNode;
                    nextNode = u;
                    AlgoStatus = AgentStatus.forward;
                    System.out.println("" + name + " at node " + currentNode.name + " Checking CODE 04 ( Change to FORWARD )");

                }//else
            }//else
        } else {
            // Execute single agent
            AlgoStatus = AgentStatus.single;
            System.out.println("" + name + " at node " + currentNode.name + " Checking CODE 04 ( Change to SINGLE )");
            nextNode = v;
        }

        return nextNode;
    }

    private Node calculateSingle() {
        Node nextNode = null;
        boolean aHappens = false;// Agent at a node with the 'first' choice blocked
        boolean bHappens = false;// nCount=n-1

        // Check status
        Node Pn = Simulator.get().nodeList.get(currentNode.getNorthPort());

        if (isFirstOptionBlocked()){
            aHappens = true;
        }

        //if (nCount == Simulator.get().nodeList.size() - 1){
        //    bHappens = true;
        //}

        if (aHappens) {
            /* there is a token on the second choice */
            if (isSecondOptionBlocked()) {
                // repeat wait in the node
                // until first or second choice port becomes unblocked
                // wait until first or second becomes unblocked
                System.out.println(name + " at node " + currentNode.name + " WAITING at " + currentNode + " [single code01]");
                while (isFirstOptionBlocked() && isSecondOptionBlocked()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(name + "=" + currentNode.name + " NOT WAITING at " + currentNode);

                if (!isFirstOptionBlocked()) {
                    nextNode = goToNorth();

                } else {
                    // Put a token on that port, remember it as a Check Point
                    // and execute Forward
                    checkPoint = currentNode;
                    AlgoStatus = AgentStatus.forward;
                    System.out.println(name + "=" + currentNode.name + ": Change to FORWARD, checkPoint:" + checkPoint.name+ " [single code02]");
                    System.out.println("(the first action of agent "+name+", once in the new NS ring, will be xCount--)");
                    xCount=0; //third exception, the xCount=0 overrides the xCount-- in the following NSring
                    nextNode = goToEast();
                }

            } else {
                // Put a token on that port, remember it as a Check Point and
                // execute Forward
                checkPoint = currentNode;
                AlgoStatus = AgentStatus.forward;
                System.out.println(name + "=" + currentNode.name + ": Change to FORWARD, checkPoint:" + checkPoint.name+ "");
                System.out.println("(the first action of agent "+name+", once in the new NS ring, will be xCount--)");
                xCount=0; //third exception, the xCount=0 overrides the xCount-- in the following NSring
                nextNode = goToEast();
            }

        } else if (bHappens) {
            // Finished
            AlgoStatus = AgentStatus.done;
            System.out.println(name + "=" + currentNode.name + ": Change to DONE at " + currentNode.name);
            executing = false;

        } else {
            // Keep exploring as usual
            nextNode = defaultMovement(Pn);
        }

        previousNode = currentNode;
        if (nextNode != null){
            return nextNode;
        }
        else{
            return currentNode;
        }
    }

    private Node goToEast() {
        if(isSecondOptionBlocked()){
            System.err.print("WARNING!!!: East port is blocked, but agent ["+name+":"+AlgoStatus+"] wants to go east");
        }
        //System.out.println("Agent "+name + " at node " + currentNode.name + " moves East, xCount == "+xCount);
        nCount++;
        Node nextNode = Simulator.get().nodeList.get(currentNode.getEastPort());

        currentNode.removeMoveTokens(name);
        currentNode.addToken(new Token(this.name, currentNode.name, Direction.East, TypeToken.CWWT, false, 1));
        nextNode.addToken(new Token(this.name, nextNode.name, Direction.West, TypeToken.CWWT, false, 1));

        if (previousNode != null) {
            previousNode.removeMoveTokens(name);
            previousNode.addToken(new Token(this.name, currentNode.name, Direction.Done, TypeToken.CWWT, false, 1));
        }
        return nextNode;
    }

    private Node goToNorth() {
        if(isFirstOptionBlocked()){
            System.err.print(name+"["+AlgoStatus+"]__Warning: North blocked, but going anyway!!");
        }
        //System.out.println(name + "=" + currentNode.name + " move North");
        xCount++;
        nCount++;
        Node nextNode = Simulator.get().nodeList.get(currentNode.getNorthPort());
        currentNode.removeMoveTokens(name);
        currentNode.addToken(new Token(this.name, currentNode.name, Direction.North, TypeToken.CWWT, false, 1));
        nextNode.addToken(new Token(this.name, nextNode.name, Direction.South, TypeToken.CWWT, false, 1));

        if (previousNode != null) {
            previousNode.removeMoveTokens(name);
            previousNode.addToken(new Token(this.name, currentNode.name, Direction.Done, TypeToken.CWWT, false, 1));

        }
        return nextNode;
    }

    private void initData() {
        this.numNodesNS = this.dataCollector.getNumNodesNS();
        this.numNodesEW = this.dataCollector.getNumNodesEW();
    }

    private Boolean isFirstOptionBlocked(Node n) {

        for (Token t : n.getTokenList()) {
            if (t.port == Direction.North)
                return true;
        }

        Node destiny = Simulator.get().nodeList.get(n.getNorthPort());
        for (Token t : destiny.getTokenList()) {
            if (t.port == Direction.South || t.port == Direction.Middle)
                return true;
        }
        return false;
    }

    private Boolean isFirstOptionBlocked() {

        return isFirstOptionBlocked(currentNode);
    }

    private Boolean isSecondOptionBlocked(Node n) {

        for (Token t : n.getTokenList()) {
            if (t.port == Direction.East)
                return true;
        }

        Node destiny = Simulator.get().nodeList.get(n.getEastPort());
        for (Token t : destiny.getTokenList()) {
            if (t.port == Direction.West || t.port == Direction.Middle)
                return true;
        }
        return false;
    }

    private Boolean isSecondOptionBlocked() {
        return isSecondOptionBlocked(currentNode);
    }

    private Node defaultMovement(Node Pn) {
        Node nextNode = currentNode;
        boolean freeNorth=!isFirstOptionBlocked();
        boolean freeEast=!isSecondOptionBlocked();

        if ( xCount < (numNodesNS+1) ){
            if( freeNorth ){
                nextNode = goToNorth();
            }else if ( freeEast ){
                xCount=1;
                nextNode = goToEast();
            }else{//we shouldn't be here, it must be handled above!!!
                System.err.println(name+" at node "+currentNode.name+"warning, code DEF01!!!!!");
            }
        }else{
            if( freeEast ){
                xCount=1;
                nextNode = goToEast();
            }else if( freeNorth ){
                System.err.println(name+" at node "+currentNode.name+" with xCount=="+xCount+", decreases xCount to "+(xCount-1)+" and goes north again.");
                xCount--;
                nextNode = goToNorth();
            }else{
                System.err.println(name+" at node "+currentNode.name+"warning, code DEF02!!!");
            }

        }

        return nextNode;

        //         if(freeNorth && xCount < numNodesNS){
        //             //System.out.println("DefTest01");
        //             nextNode = goToNorth();
        //         }else if(xCount >=numNodesNS && freeEast){
        //             //System.out.println(name+" at node "+currentNode.name+" with xCount=="+xCount+" goes east");
        //             xCount=1;
        //             nextNode = goToEast();
        //         } else if (!freeEast && freeNorth && xCount >=numNodesNS){ //first exception
        //             System.err.println(name+" at node "+currentNode.name+" with xCount=="+xCount+", decreases xCount to "+(xCount-1)+" and goes north again.");
        //             xCount--;
        //             nextNode = goToNorth();
        //         } else {
        //             System.err.println(name+" warning, code DEF02");
        //         }

    }

}
