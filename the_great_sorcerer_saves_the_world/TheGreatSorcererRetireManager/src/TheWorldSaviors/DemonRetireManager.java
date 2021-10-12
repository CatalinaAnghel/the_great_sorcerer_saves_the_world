package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Semaphore;

/**
 * This is the manager that retires the demons. In reality, this manager just marks a demon as retired.<br>
 * This class extends the class Thread.
 * @author Anghel Florina-Catalina
 *
 */
public class DemonRetireManager extends Thread {
	/**
	 * This is the vector of covens.
	 */
	protected Vector<Coven> covens;
	/**
	 * This is the list of demons that will be retired.
	 */
	protected static List<Demon> demonsToRetire = new ArrayList<Demon>();
	
	/**
	 * This is the constructor of the class DemonRetireManager.
	 * @param covens Vector&lt;Coven&gt; - the vector of covens 
	 */
	public DemonRetireManager(Vector<Coven> covens) {
		this.covens = covens;
		
	}
	
	/**
	 * This is the method run that overrides the method with the same name from the class Thread.<br>
	 * Firstly, the manager will release the semaphore from the class Demon called retireSem and he will let the
	 * demons to send him the retirement requests. Then, in a infinite loop, he will sleep for a random time
	 * (between 200 and 300 milliseconds) and then he will check if he has demons that sent retirement requests. 
	 * If there are requests, he will pick a random demon and he will mark him as retired and then he will 
	 * remove the demon from the list and he will release the semaphore.
	 */
	@Override
	public void run() {
		System.out.println("The demon retirenment manager has started");
		// let the demons to send their retirement requests.
		Demon.getRetireSem().release();
		while(true) {
			Random random = new Random();
			try {
				// sleep
				Thread.sleep(random.nextInt(100) + 200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(!demonsToRetire.isEmpty()) {
				// there are requests, pick a random demon and retire him.
				Integer demonNumber = random.nextInt(demonsToRetire.size());
				Demon demon = demonsToRetire.get(demonNumber);
				demon.setRetired(true);
				// remove the retired demon from the list of demons that sent the requests.
				demonsToRetire.remove(demon);
				System.out.println("The demon was removed from the list of retired demons");
				// release the semaphore.
				Demon.getRetireSem().release();
			}
		}
	}


	/**
	 * This is the method used to receive the requests and the demons that sent the request will be added into the
	 * list.
	 * @param retireDemon Demon - the demon that sent the request.
	 */
	public synchronized void sendRetireRequest(Demon retireDemon) {
		demonsToRetire.add(retireDemon);
		System.out.println("The demon no." + retireDemon.demonId +"from the coven no." + retireDemon.coven.getCovenId() + " was added in the list of demons that will be retired.");
	}
}
