package src;

import java.io.IOException;
import java.io.File;


public class Main{

	public static void main(String[] ArgV) throws NumberFormatException, IOException{
		int nStations = 250;//TODO: num. stations, to be adapted from GUI
		int xNet = 1000;//TODO: Network_X, to be adapted from GUI
		int yNet = 1000 ;//300TODO: Network_Y, to be adapted from GUI
		String path = new File(".").getAbsolutePath().toString();

		int nAgents = Integer.parseInt(ArgV[0]);
		
		src.network.Scenario.f_edges = path +"/scenarios/"+ ArgV[1];
		src.network.Scenario.f_nodes = path +"/scenarios/"+ ArgV[2];
								
				// GUI lGUI = new GUI();
		String output =  ArgV[3];
		float nodeSize = Float.parseFloat(ArgV[4]);
		float agentSize = Float.parseFloat(ArgV[5]);
		int landmarkToCross = Integer.parseInt(ArgV[6]);
		
		
		GUI.create(nStations, nAgents, xNet, yNet, output, nodeSize, agentSize, landmarkToCross);

	}//end_main

}//end_class
