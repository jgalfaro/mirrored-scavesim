package src;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.Simulator.NetworkType;

public class Main {

	public static ArrayList<Integer> initBH;
	public static ArrayList<Integer> initAgents;
	public static void main(String[] ArgV) {
		String fileNameBH 		= "BHs.txt";
		String fileNameAgents 	= "Agents.txt";

		int nStations = 30;// TODO: num. stations, to be adapted from GUI
		int nAgents = 5;// TODO: num. agents, to be adapted from GUI
		int xNet = 700;// TODO: Network_X, to be adapted from GUI
		int yNet = 700;// TODO: Network_Y, to be adapted from GUI
		int nTokens = 2 ;// TODO: num. tokens, to be adapted from GUI
		int nBH = 1;// TODO: num. agents, to be adapted from GUI
		int showLabels = 1;// TODO: to be adapted from GUI
		int testA2 = 0;

		// get sdin/argv parameters
		try {
			if (ArgV.length >= 1) {
				nStations = Integer.parseInt(ArgV[0]);
				if (ArgV.length >= 2) {
					nAgents = Integer.parseInt(ArgV[1]);
					if (ArgV.length >= 3) {
						xNet = Integer.parseInt(ArgV[2]);
						if (ArgV.length >= 4) {
							yNet = Integer.parseInt(ArgV[3]);
							if (ArgV.length >= 5) {
								int type = Integer.parseInt(ArgV[4]);
								switch (type) {
								case 0:
									Simulator.type = NetworkType.RANDOM;
									break;
								case 1:
									Simulator.type = NetworkType.TORUS;
									break;
								case 2:
									Simulator.type = NetworkType.HYPERCUBE;
									break;
								}
								if (ArgV.length >= 6) {
									nTokens = Integer.parseInt(ArgV[5]);
									if (ArgV.length >= 7) {
										nBH = Integer.parseInt(ArgV[6]);
										if (ArgV.length >= 8) {
											showLabels = Integer.parseInt(ArgV[7]);
											if (ArgV.length >= 9) {
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
		}// try
		catch (Exception localException) {
			System.out.println("Usage: Simulator numNodes numAgents xNet yNet type numTokens numBHs showLabels testA2");
			System.out.println(localException.toString());
		}

		//Joaquin
		//initBH=readFile(fileNameBH);
		//initAgents=readFile(fileNameAgents);
		//Joaquin
		initBH=null;
		initAgents=null;
		GUI.create(nStations, nAgents, xNet, yNet, nTokens, nBH, showLabels, testA2);
	}// end_main

	public static ArrayList<Integer> readFile(String filename){
		ArrayList<Integer> ret= new ArrayList<Integer>();

		try{
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int line=1;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)  {

				try{
					ret.add(Integer.parseInt(strLine));
				}catch (NumberFormatException e){
					if(line==0){
						System.err.println("Error: "+strLine+" isn't a valid numbee for BH");
					}else{
						System.err.println("Error: "+strLine+" isn't a valid number for agent "+(line-1));
					}
				}

				line++;
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return ret;

	}
}// end_class
