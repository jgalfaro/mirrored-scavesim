package src;

import src.Simulator.Status;

public class Render extends Thread {

	Simulator simulator;

	public Render(Simulator simulator) {
		this.simulator=simulator;
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(50);
				//Simulator.printStatusAgents();
				//increase == slower
				//decrease == faster
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(Simulator.status==Status.STEPPING){
				simulator.network.repaint();
			}
		}
	}
}
