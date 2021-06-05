package src;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import src.Simulator.Status;

public class GUI{
	static boolean records=true;
	static float f = 0.0F;
	static int m = -1;
	static Simulator lSim;
	static JFrame mainFrame = new JFrame("BHs Search Simulator");
	static Component networkComponent;
	static Thread simThread = null;

	static JButton stopButton;
	static JComboBox algBox;

	public static void create(final int nStations, final int nAgents, final int xNet,
			final int yNet, final int nTokens,  final int nBH, final int showLabels,
			final int testA2){


		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel(true);
		
		//		JFrame logFrame = new JFrame("Records");
		//		JScrollPane scrollingResult;



		JButton newExpButton;
		JButton nextButton;
		JButton loopButton;

		lSim = Simulator.get();
		lSim.Init(new Record(records,nBH), nStations, nAgents, xNet, yNet,0,f, m, nTokens, nBH, showLabels, testA2);


		panel2.setLayout(new MigLayout("", "[][][][][]", "[][]"));
		
		nextButton = new JButton("next");
		nextButton.setActionCommand("next");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.get().one_step=true;
				Simulator.status=Status.STEPPING;
				stopButton.setText("start");
			}
		});
		panel2.add(nextButton,"cell 0 0");

		stopButton = new JButton("stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Simulator.status==Status.STEPPING)
					Simulator.status=Status.PAUSED;
				else if(Simulator.status==Status.PAUSED||Simulator.status==Status.INIT)
					Simulator.status=Status.STEPPING;

				updateGUI();
			}
		});
		panel2.add(stopButton,"cell 1 0");


		loopButton = new JButton("Show Network");
		loopButton.setActionCommand("DumpState");
		loopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("DumpState");
				lSim.dumpState();
			}
		});
		panel2.add(loopButton,"cell 2 0");


		// Create a agent spinner
		int maxSpinnerA=nStations-1;
		SpinnerModel spinnerA = new SpinnerNumberModel(nAgents, 1, maxSpinnerA, 1);
		final JSpinner numAgentsSpinner = new JSpinner(spinnerA);

		// Create a BH spinner
		int maxSpinnerBH=nStations-1;
		SpinnerModel spinnerBH = new SpinnerNumberModel(nBH, 0, maxSpinnerBH, 1);
		final JSpinner numBHSpinner = new JSpinner(spinnerBH);


		panel2.add(new JLabel("N. Agents:"), "cell 2 1");
		panel2.add(numAgentsSpinner, "cell 2 1");
		panel2.add(new JLabel("N. BH:"), "cell 3 1");
		panel2.add(numBHSpinner, "cell 3 1");
		newExpButton = new JButton("new execution");
		newExpButton.setActionCommand("newExp");
		newExpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.get().recalc(nStations,((Integer)numAgentsSpinner.getValue()),((Integer)numBHSpinner.getValue()));
				Simulator.get().restart();
				numAgentsSpinner.setValue(Simulator.get().numAgents);
			}
		});
		panel2.add(newExpButton,"cell 3 0");



		mainFrame.add(panel1, BorderLayout.NORTH);
		mainFrame.add(lSim.getNetwork(),BorderLayout.CENTER);
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
		if(Simulator.status==Status.STEPPING){
			stopButton.setText("stop");
		}else{
			stopButton.setText("start");
		}

	}//updateGUI

}//CLASS_GUI

