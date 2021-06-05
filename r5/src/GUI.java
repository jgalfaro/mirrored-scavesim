package src;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.vecmath.*;

public class GUI{
	static boolean records=true;
	static float f = 0.0F;
	static int m = -1;
	static Simulator lSim;
	static JFrame mainFrame = new JFrame("BHs Search Simulator");
	static Component networkComponent;
	static Thread simThread=null;

	static JButton stopButton;
	static JComboBox algBox;

        public static void create(final int nStations, final int nAgents, final int xNet,
                                  final int yNet, final int nTokens,  final int nBH, final int showLabels,
                                  final int testA2){


		MobAgent lMobAgent = new MobAgent();
		Class<? extends MobAgent> localClass = lMobAgent.getClass();


		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		JFrame logFrame = new JFrame("Records");
		JScrollPane scrollingResult;



		JButton newExpButton;
		JButton nextButton;
		JButton drawButton;
		JButton loopButton;
		JButton labelsButton;
		JButton expButton1;
		JButton expButton2;

		String[] Labels = { "Torus"};

		lSim =  new Simulator(new Record(records,nBH), nStations, nAgents, xNet, yNet,0,
                                      localClass, f, m, nTokens, nBH, showLabels, testA2);


		nextButton = new JButton("next");
		nextButton.setActionCommand("next");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.one_step=true;
				Simulator.running=true;
				stopButton.setText("start");
			}
		});
		panel2.add(nextButton);

		stopButton = new JButton("stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.running=!Simulator.running;
				updateGUI();
			}
		});
		panel2.add(stopButton);


		loopButton = new JButton("Show Network");
		loopButton.setActionCommand("DumpState");
		loopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                            System.out.println("DumpState");
                            lSim.dumpState();
			}
		});
                panel2.add(loopButton);

		newExpButton = new JButton("new execution");
		newExpButton.setActionCommand("newExp");
		newExpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                            //TODO
                            System.out.print("TODO ... al pulsar sobre esta opcion, deberia reinicializarse la simulacion, es decir, un nuevo toro (aun sin explorar) y los tres agentes (al igual que el BH) en tres posiciones nuevas.\n");
                        }
		});
		panel2.add(newExpButton);

		mainFrame.add(panel1, BorderLayout.NORTH);
		mainFrame.add(lSim.getNetwork(), BorderLayout.CENTER);
		mainFrame.add(panel2, BorderLayout.SOUTH);
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setSize(500,750);
		mainFrame.setLocation(0,0);

		simThread = new Thread(lSim);

		simThread.start();

		updateGUI();

	}//end_main

	public static void updateGUI(){
            if(Simulator.running){
			stopButton.setText("stop");
            }
            else{
                stopButton.setText("start");
            }

	}//updateGUI

}//CLASS_GUI

