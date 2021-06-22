package src;

import src.Simulator.NetworkType;

public class Main{

    public static void main(String[] ArgV){

        int nStations = 150;//TODO: num. stations, to be adapted from GUI
        int nAgents = 1;//TODO: num. agents, to be adapted from GUI
        int xNet = 700;//TODO: Network_X, to be adapted from GUI
        int yNet = 700;//TODO: Network_Y, to be adapted from GUI
        int nTokens = 7;//TODO: num. tokens, to be adapted from GUI
        int nBH = 0;//TODO: num. agents, to be adapted from GUI
        int showLabels=0;//TODO:  to be adapted from GUI
        int testA2=0;

        //get sdin/argv parameters
        try{
            if (ArgV.length >= 1){
                nStations = Integer.parseInt(ArgV[0]);
                if (ArgV.length >= 2){
                    nAgents = Integer.parseInt(ArgV[1]);
                    if (ArgV.length >= 3){
                        xNet = Integer.parseInt(ArgV[2]);
                        if (ArgV.length >= 4){
                            yNet = Integer.parseInt(ArgV[3]);
                            if (ArgV.length >= 5){
                                int type = Integer.parseInt(ArgV[4]);
                                switch (type) {
                                case 0:
                                    Simulator.type=NetworkType.RANDOM;
                                    break;
                                case 1:
                                    Simulator.type=NetworkType.TORUS;
                                    break;
                                case 2:
                                    Simulator.type=NetworkType.HYPERCUBE;
                                    break;
                                }
                                if (ArgV.length >= 6){
                                    nTokens = Integer.parseInt(ArgV[5]);
                                    if (ArgV.length >= 7){
                                        nBH = Integer.parseInt(ArgV[6]);
                                        if (ArgV.length >= 8){
                                            showLabels = Integer.parseInt(ArgV[7]);
                                            if (ArgV.length >= 9){
                                                testA2 = Integer.parseInt(ArgV[8]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }//try
        catch (Exception localException){
            System.out.println("Usage: Simulator numNodes numAgents xNet yNet type numTokens numBHs showLabels testA2");
            System.out.println(localException.toString());
        }

        GUI.create(nStations, nAgents, xNet, yNet, nTokens, nBH, showLabels, testA2);
    }//end_main

}//end_class
