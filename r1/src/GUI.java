import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI{

    public static void create(final int nStations, final int nAgents, final int xNet, final int yNet){

        boolean records=false;//TODO: set true/false from GUI, to log/unlog records
        float f = 0.0F;
        int m = -1;
        MobAgent lMobAgent = new MobAgent();
        Class<? extends MobAgent> localClass = lMobAgent.getClass();
        Record localRecord;

        JFrame mainFrame = new JFrame("Sample GUI for the simulator");
        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        JFrame logFrame = new JFrame("Records");
        JScrollPane scrollingResult;

        JComboBox algBox;

        final JTextArea logArea; // print out simulation records
       // JButton restartButton;
        //JButton infoButton;
        JButton stopButton;
        JButton nextButton;
        //JButton startButton;
        JButton drawButton;
        JButton loopButton;
        JButton resetButton;
        JButton expButton1;
        JButton expButton2;

 
        String[] Labels = { "Random Network", "Hypercube", "Torus"};
      //  String[] colorLabels = { "Black", "White", "Red", "Green", "Blue" };

        Thread simThread=null;

        localRecord = new Record(records);
        
        final Simulator lSim =  new Simulator(localRecord, nStations, nAgents, xNet, yNet, localClass, f, m);
       

        algBox = new JComboBox(Labels);
        algBox.setSelectedIndex(0);
        algBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        algBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox cb = (JComboBox) e.getSource();
                    System.out.println("setting up structure "+cb.getSelectedIndex());
                    switch (cb.getSelectedIndex()) {
					case 0:
						 lSim.type=Simulator.NetworkType.RANDOM;
						break;
					case 1:
						 lSim.type=Simulator.NetworkType.HYPERCUBE;
						break;
					case 2:
						 lSim.type=Simulator.NetworkType.TORUS;
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
                        System.out.println("draw");
                        lSim.run();
                        lSim.network.repaint();
                        
                }
            });
        panel1.add(drawButton);

        nextButton = new JButton("next");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("next");
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
                    System.out.println("stop");
                }
            });
        panel2.add(stopButton);

        resetButton = new JButton("reset");
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
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
        mainFrame.add(panel2, BorderLayout.SOUTH);
        mainFrame.pack();
        mainFrame.setVisible(true);

        simThread = new Thread(lSim);
        simThread.setPriority(Thread.currentThread().getPriority() - 1);

        simThread.start();

    }//end_main

}

