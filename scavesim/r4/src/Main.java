package src;
public class Main{

    public static void main(String[] ArgV){

        int nStations = 250;//TODO: num. stations, to be adapted from GUI
        int nAgents = 2;//TODO: num. agents, to be adapted from GUI
        int xNet = 700;//TODO: Network_X, to be adapted from GUI
        int yNet = 600;//TODO: Network_Y, to be adapted from GUI

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
                        }
                    }
                }
            }
        }//try
        catch (Exception localException){
            System.err.println("Usage: Simulator numNodes numAgents [xNet yNet]");
            System.err.println(localException.toString());
        }

       // GUI lGUI = new GUI();

        GUI.create(nStations, nAgents, xNet, yNet);

    }//end_main

}//end_class
