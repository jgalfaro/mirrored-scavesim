package src;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI{
	static boolean records=false;
	static boolean stepByStep= true;
	static float f = 0.0F;
	static int m = -1;
	static Simulator lSim;
	private static JFrame mainFrame = new JFrame("Sample GUI for the simulator");
	static Component networkComponent;
	static Thread simThread=null;
	
	static JButton stopButton;
	static JComboBox algBox;

	public static void create(final int nStations, final int nAgents, final int xNet, final int yNet, final String output, 
			final float nodeSize, final float agentSize, final int numbLandmarkToCross) throws IOException{
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		JFrame logFrame = new JFrame("Records");
		JScrollPane scrollingResult;		

		final JTextArea logArea; // print out simulation records
		// JButton restartButton;
		//JButton infoButton;
		JButton nextButton;
		//JButton startButton;
		JButton drawButton;
		JButton loopButton;
		JButton resetButton;
		JButton expButton1;
		JButton expButton2;

		String[] Labels = { "Random Network", "Torus", "Hypercube", "Scenario"};
		
		lSim =  new Simulator(new Record(records, stepByStep), nStations, nAgents, xNet, yNet,0, f, m, output, nodeSize, agentSize, numbLandmarkToCross);
		
		algBox = new JComboBox(Labels);

		switch (Simulator.type) {
		case RANDOM:
			algBox.setSelectedIndex(0);
			break;
		case TORUS:
			algBox.setSelectedIndex(1);
			break;
		case HYPERCUBE:
			algBox.setSelectedIndex(2);
			break;
		case SCENARIO:
			algBox.setSelectedIndex(3);
			break;
		}


		algBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		algBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				System.out.println("setting up structure "+cb.getSelectedIndex());
				switch (cb.getSelectedIndex()) {
				case 0:
					Simulator.type=Simulator.NetworkType.RANDOM;
					break;
				case 1:
					Simulator.type=Simulator.NetworkType.HYPERCUBE;
					break;
				case 2:
					Simulator.type=Simulator.NetworkType.TORUS;
					break;
				case 3:
					Simulator.type=Simulator.NetworkType.SCENARIO;
					break;
				}
				
			}
		});

		panel1.add(new JLabel(" ", JLabel.LEFT));
		panel1.add(new JLabel("Structure:", JLabel.LEFT));
		panel1.add(algBox);

		drawButton = new JButton("draw");
		drawButton.setActionCommand("draw");
		drawButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Simulator.finished=true;
								
				mainFrame.getContentPane().remove(mainFrame.getContentPane().getComponent(1));
				
				Simulator.clean();
				try {
					lSim =  new Simulator(new Record(records, stepByStep), nStations, nAgents, xNet, yNet,0, f, m, output, nodeSize, agentSize, numbLandmarkToCross);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
				Simulator.finished=false;

				mainFrame.add(lSim.getNetwork(), BorderLayout.CENTER);

				simThread = new Thread(lSim);
				//simThread.setPriority(Thread.currentThread().getPriority() - 1);

				simThread.start();
				updateGUI();
			}
		});

		panel1.add(drawButton);

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

		loopButton = new JButton("loop");
		loopButton.setActionCommand("loop");
		loopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("loop");
			}
		});
		panel2.add(loopButton);

		stopButton = new JButton("stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.running=!Simulator.running;
				updateGUI();
			}
		});
		panel2.add(stopButton);

		resetButton = new JButton("reset");
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lSim.getNetwork().reset();
				System.out.println("reset");
			}
		});

		panel2.add(resetButton);


		logArea = new JTextArea(100,100);
		scrollingResult = new JScrollPane(logArea);
		logFrame.add(scrollingResult);
		logFrame.setSize(380,380);
		logFrame.setVisible(true);


		expButton1 = new JButton("CLR-Records");
		expButton1.setActionCommand("watches-clr");

		expButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clear Records");
				logArea.setText(null);
			}
		});

		panel2.add(expButton1);

		expButton2 = new JButton("Export-to-log-window");
		expButton2.setActionCommand("watches");
		expButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("export-to-log");
				logArea.append("------------------------------ \n\n");
				logArea.append("This function is still empty  \n");
				logArea.append("------------------------------ \n\n");
			}
		});

		panel2.add(expButton2);


		mainFrame.add(panel1, BorderLayout.NORTH);
		mainFrame.add(lSim.getNetwork(), BorderLayout.CENTER);
		// mainFrame.add(,BorderLayout.CENTER);
		mainFrame.add(panel2, BorderLayout.SOUTH);
		mainFrame.pack();
		mainFrame.setVisible(true);
		//mainFrame.setMinimumSize(new Dimension(400,200));
		mainFrame.setSize(600,600);
		mainFrame.setLocation(50,0);

		simThread = new Thread(lSim);
		//simThread.setPriority(Thread.currentThread().getPriority() - 1);
		
		
		simThread.start();
		
		
		updateGUI();
		
	}//end_main

	public static void updateGUI(){
		if(Simulator.running)
			stopButton.setText("stop");
		else
			stopButton.setText("start");
		
		switch (Simulator.type) {
		case RANDOM:
			algBox.setSelectedIndex(0);
			break;
		case HYPERCUBE:
			algBox.setSelectedIndex(1);
			break;
		case TORUS:
			algBox.setSelectedIndex(2);
			break;
		case SCENARIO:
			algBox.setSelectedIndex(3);
			break;
		}	
	}
	
	public static JFrame getMainFrame() {
		return mainFrame;
	}
}

