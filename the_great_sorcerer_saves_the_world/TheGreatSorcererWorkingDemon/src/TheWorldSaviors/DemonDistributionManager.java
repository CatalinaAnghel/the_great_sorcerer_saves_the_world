package TheWorldSaviors;

import java.util.Random;
import java.util.Vector;

/**
 * This is the class DemonDistributionManager used to create the demons and to spawn them into a coven. It extends
 * the class Thread.
 * @author Anghel Florina-Catalina
 *
 */
public class DemonDistributionManager extends Thread {
	/**
	 * This is the vector of covens.
	 */
	protected Vector<Coven> covens;
	
	/**
	 * This is the constructor of the class Demon
	 * @param covens Vector&lt;Coven&gt; - the vector of covens.
	 */
	public DemonDistributionManager(Vector<Coven> covens) {
		this.covens = covens;
	}
	
	/**
	 * This is the method run which overrides the method with the same name from the class Thread.<br>
	 * The manager will spawn a demon into a random coven at a random time between 500 and 1000 milliseconds.
	 * He will try to find an empty room for the demon and he will create him and put him to register into his coven.
	 */
	@Override
	public void run() {
		Random random = new Random();
		Integer row, column;
		
		while(true) {
			Integer covenNumber = random.nextInt(covens.size());
			try {
				// the thread will sleep 500-1000 seconds.
				Thread.sleep(random.nextInt(500)+500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//  generate a random row and a random column for the demon that will be created
			row = random.nextInt(covens.get(covenNumber).getCapacity());
			column = random.nextInt(covens.get(covenNumber).getCapacity());
			if(covens.get(covenNumber).demons.size() <= covens.get(covenNumber).getCapacity()/2) {
				// if in the selected room is a demon, then the row and the column will be generated again
				while(covens.get(covenNumber).checkRoom(row, column) != null) {
					row = random.nextInt(covens.get(covenNumber).getCapacity());
					column = random.nextInt(covens.get(covenNumber).getCapacity());
				}
				
				// create a new demon
				System.out.println("A demon is created");
				Demon demon = new Demon(covens.get(covenNumber).getNextDemonId(), covens.get(covenNumber), row, column, 0);
				covens.get(covenNumber).increaseNextDemonId();
				// put the demon to register into the coven
				demon.registerIntoTheCoven();
				// start the demon
				demon.start();
			}
		}
		
	}
}
