package TheWorldSaviors;

import java.util.Random;
import java.util.Vector;

/**
 * This is the class Undead. It extends the class Thread.
 * @author Anghel Florina-Catalina
 *
 */
public class Undead extends Thread {
	/**
	 * This is the undead's id.
	 */
	protected Integer undeadId;
	/**
	 * This is the vector of covens.
	 */
	protected Vector<Coven> covens;
	
	/**
	 * This is the constructor of the class Undead.
	 * @param id Integer - the id of the undead.
	 * @param covens Vector &lt; Coven &gt; - the vector of covens.
	 */
	public Undead(Integer id, Vector<Coven> covens) {
		this.undeadId = id;
		this.covens = covens;
	}
	
	/**
	 * This is a getter.
	 * @return the id of the undead.
	 */
	public Integer getUndeadId() {
		return this.undeadId;
	}
	
	/**
	 * This is the method run that overrides the method with the same name from the class Thread.<br>
	 * The undead will sleep for a random time(between 500 and 1000 milliseconds). Then he will attack a random
	 * coven.
	 */
	@Override
	public void run() {
		while(true) {
			Random random = new Random();
			try {
				// the thread will sleep for 500-1000 seconds
				Thread.sleep(random.nextInt(500) + 500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// select a random coven and then attack it
			Integer number = random.nextInt(covens.size());
			// attack a coven
			covens.get(number).attack(this);
		}
	}
}
