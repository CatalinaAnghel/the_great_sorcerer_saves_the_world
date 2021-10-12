package TheWorldSaviors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is the class Coven. It extends the class Thread and it is accessed by its demons, by the witches and
 * it can be accessed by every undead.<br>
 * @author Anghel Florina-Catalina
 *
 */
public class Coven extends Thread{
	/**
	 * This is the semaphore used to synchronize the threads that need to access and modify the list of demons.
	 */
	protected Semaphore demonsSem = new Semaphore(1);
	
	/**
	 *  This is the semaphore used to synchronize the threads that need to access and modify the list of ingredients
	 *  produced by the coven's demons.
	 */
	protected Semaphore ingredientsSem = new Semaphore(1);
	
	/**
	 *  This is the semaphore used to synchronize the threads that need to access and modify the coven's 
	 *  matrix of rooms. 
	 */
	protected Semaphore roomsSem = new Semaphore(1);
	
	/**
	 * This is the coven's id, used to identify the coven.
	 */
	protected Integer covenId;
	
	/**
	 * This is the number of rows and columns used to create the matrix of rooms.
	 */
	protected Integer capacity;

	/**
	 * This is the list with the current coven's demons. This list is accessed by every undead that scares the demons.
	 * It is accessed by the current coven because it scares its demons every 10 seconds. It is also accessed by
	 * every demon when he asks to be registered into the coven. 
	 */
	protected List<Demon> demons = new ArrayList<Demon>();

	/**
	 * This is the list with the current coven's ingredients. This list is accessed by every undead when it attacks the coven 
	 * and there is not a witch that will try to defend the coven or when there is a witch, but she does not have potions.
	 * It is also accessed by every demon when he asks to send the produced ingredients and by the witches when they
	 * want to take the produced ingredients. 
	 */
	protected List<String> ingredients = new ArrayList<String>();

	/**
	 * This is the matrix of rooms. It is a capacity*capacity matrix.
	 */
	protected Demon[][] rooms; 

	/**
	 * This is the array of possible ingredients.
	 */
	protected String[] ingredientTypes;

	/**
	 * This is a Boolean variable used to synchronize the witches and the demons when they want to access the
	 * list of produced ingredients.
	 */
	protected volatile Boolean turn;

	/**
	 * This is an Integer used by the Grand Sorcerer's helper when it creates new demons. It represents the id of
	 * the next demon that will be created.
	 */
	protected volatile Integer nextDemonId = 0;

	/**
	 * This is the vector of witches that are visiting the coven and that are not fighting an undead.
	 */
	protected Vector<Witch> witches = new Vector<Witch>();
	
	/**
	 * This is a lock used to synchronize the threads that are trying to access the vector of witches.
	 */
	protected static Lock lock = new ReentrantLock();

	/**
	 * This is the vector of attackers that are attacking the coven and that are not fighting a witch.
	 * 
	 */
	protected Vector<Undead> attackers = new Vector<Undead>();

	/**
	 * This is the semaphore used to synchronize the threads that are accessing the vector of attackers.
	 */
	protected Semaphore undeadSem = new Semaphore(1);

	/**
	 * This is a semaphore used to synchronize the threads that want to acquire the semaphore of demons.
	 */
	protected Semaphore queue = new Semaphore(1);
	
	/**
	 * This is the barrier used to wake up the demons.
	 */
	protected CyclicBarrier cyclicBarrier;
	
	/**
	 * This is the method run which overrides the method with the same name from the class Thread.<br>
	 * Firstly, the thread will sleep for 10 seconds. Then it will stay in line to acquire the semaphore of demons
	 * and then it will let the next thread to stay in line. It will scare its demons and then it will release the
	 * semaphore of demons.
	 */
	@Override
	public void run() {
		while(true) {
			try {
				// the thread will sleep for 10 seconds
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				// Stay in line to acquire the semaphore of demons
				queue.acquire();
				// Acquire the semaphore of demons
				demonsSem.acquire();
				// Let the next thread to stay in line
				queue.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("The coven no." + covenId + " will scare its demons.");
			for(int iterator = 0; iterator < demons.size(); iterator++) {
				// scare the demons
				demons.get(iterator).loseIngredients();
			}
			// release the semaphore
			demonsSem.release();
		}
	}
	
	/**
	 * This is the constructor of the class Coven.
	 * @param id Integer - the coven's id
	 * @param capacity Integer - the capacity used to make the capacity*capacity matrix (the matrix of rooms)
	 * @param types String[] - the array of ingredients that can be produced.
	 * @param cb CycliBarrier - the barrier used to wake up the sleeping demons
	 */
	public Coven(Integer id, Integer capacity, String[] types, CyclicBarrier cb) {
		Integer iteratorRow, iteratorColumn;
		this.covenId = id;
		this.capacity = capacity;
		rooms = new Demon[capacity][capacity];
		for(iteratorRow = 0; iteratorRow < capacity; iteratorRow++) {
			for(iteratorColumn = 0; iteratorColumn < capacity; iteratorColumn++) {
				rooms[iteratorRow][iteratorColumn] = null;
			}
		}
		this.turn = false;
		this.ingredientTypes = types;
		this.cyclicBarrier = cb;
	}
	
	/**
	 * This is a getter.
	 * @return an integer representing the coven's id.
	 */
	public Integer getCovenId() {
		return this.covenId;
	}

	/**
	 * This is a getter.
	 * @return an integer which represents the coven's capacity.
	 */
	public Integer getCapacity() {
		return this.capacity;
	}

	/**
	 * This is a getter.
	 * @return an Integer which represents the id of the next demon that will be created.
	 */
	public Integer getNextDemonId() {
		return this.nextDemonId;
	}

	/**
	 * This is the method used to update the next demon's id.
	 */
	public void increaseNextDemonId() {
		this.nextDemonId++;
	}
	
	/**
	 * This is the method used to get the content of a room from the matrix of rooms.
	 * @param row Integer - the room's row
	 * @param column Integer - the room's column
	 * @return null if the room is empty or the demon that sits there.
	 */
	public Demon checkRoom(Integer row, Integer column) {
		return rooms[row][column];
	}
	
	/**
	 * This is the method used by a demon when he wants to be added into the coven.<br>
	 * Firstly, the demon will wait in line to be serviced then he will acquire the semaphore for the 
	 * list of demons and he will let the next one in line to be serviced. Then the demon is added into the coven's
	 * list of demons and the demons semaphore is released.<br>
	 * Then he will want to go in his room assigned by the Demon Distribution Manager. He will acquire the rooms
	 * semaphore and he will "enter" in that room, then he will release the rooms semaphore.
	 * @param demon Demon - the demon that asks to be added into the coven.
	 */
	public void addDemonIntoTheCoven(Demon demon) {
		try {
			// wait in line to be serviced
			queue.acquire();
			// request exclusive access to the list of demons
			demonsSem.acquire();
			// request exclusive access to the list of demons
			queue.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// add the demon in the coven's list of demons
		this.demons.add(demon);
		// release the access for the next thread that wants to acquire the semaphore 
		demonsSem.release();
		try {
			roomsSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// put the demon into the assigned room
		this.rooms[demon.getRow()][demon.getColumn()] = demon;
		// release the semaphore
		roomsSem.release();
		
	}
	
	/**
	 * This is the method used by a demon when he wants to send the produced ingredients to the coven.<br>
	 * If it is the witch's turn to get the ingredients, the demons will wait until it is their turn to send the ingredients.
	 * Firstly, the current demon will acquire the semaphore for the ingredients list and then he will add his
	 * ingredients into the ingredients list. Then he will release the semaphore and if there are witches that are
	 * visiting the coven and there are ingredients into the list of ingredients, turn will become true. Then the
	 * witches can take the ingredients.
	 * @param ingredients List &lt; String &gt; - the list of produced ingredients.
	 */
	public synchronized void receiveIngredient(List<String> ingredients) {
		while(turn) {
			/* if the the variable turn is true, then the witches can take the ingredients and the demons will
			wait until turn is false.
			 */
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// if it is the demon's turn, the current demon will try to acquire the ingredients semaphore.

		try {
			ingredientsSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// the demon will send his ingredients
		this.ingredients.addAll(ingredients);
		// the demon releases the ingredients semaphore
		ingredientsSem.release();
		System.out.println("The number of ingredients from the coven no." + covenId + " is " + this.ingredients.size());
		if(this.witches.size() != 0 && this.ingredients.size() != 0) {
			// if there are witches visiting the coven and there are ingredients, then 
			// the witches have the permission to take the ingredients
			turn = true;
			notifyAll();
		} 
		
	}
	
	/**
	 * This is the method used when a demon wants to change its room.<br>
	 * Firstly, the current demon will acquire the semaphore for the rooms and then he will leave the old room and
	 * he will stay in the new room. Then the rooms semaphore is released.
	 * @param demon Demon - the demon that wants to change the room
	 * @param oldRow Integer - the row of his old room
	 * @param oldColumn Integer - the column of his old room
	 * @param row Integer - the row of the new room
	 * @param column Integer - the column of the new room
	 */
	public synchronized void changeRoom(Demon demon, Integer oldRow, Integer oldColumn, Integer row, Integer column) {
		try {
			roomsSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		rooms[oldRow][oldColumn] = null;
		rooms[row][column] = demon;
		roomsSem.release();
	}
	
	/**
	 * This is a getter.
	 * @return It gets the coven's list of ingredients. 
	 */
	public synchronized List<String> getIngredientsInfo() {
		return ingredients;
	}
	
	/**
	 * This is the method used by the witches when they want to visit the coven to get the ingredients they need.<br>
	 * The current witch will lock the vector of visiting witches and she will be added into it. Then she will 
	 * unlock the vector. If turn = false, the witch will wait until turn = true. When turn = true, she will check
	 * if the ingredients list is empty. If there are ingredients, she will take just the ingredients she does not 
	 * have. The taken ingredients will be removed from the coven's list of ingredients and the witch will check if
	 * the coven is attacked. If the coven is attacked, she will pick an undead and she will start the fight. 
	 * After the fight, she will be removed from the vector of witches and if there are not witches visiting the 
	 * coven or there are not ingredients left, turn will be false and the demons will send their ingredients. Then 
	 * the witch leaves the coven.
	 * @param wIngredients List &lt; String &gt; - the ingredients of the witch
	 * @param witch Witch - the witch that visits the coven
	 */
	public synchronized void visit(List<String> wIngredients, Witch witch) {
		
		List<String> receivedIngredients = new ArrayList<String>();
		lock.lock();
		this.witches.add(witch);
		lock.unlock();
		
		while(!turn) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("The coven no." + covenId + " is visited by a witch.");
		if(!this.ingredients.isEmpty()) {
		try {
			ingredientsSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int iterator = 0; iterator < ingredients.size(); iterator++) {
			if(!wIngredients.contains(ingredients.get(iterator)) && !receivedIngredients.contains(ingredients.get(iterator))) {
				receivedIngredients.add(ingredients.get(iterator));
			}
		}
		this.ingredients.removeAll(receivedIngredients);
		ingredientsSem.release();
		System.out.println("Coven: ingredients: " + this.ingredients);
		witches.get(witches.indexOf(witch)).takeIngredients(receivedIngredients);
		}
		try {
				undeadSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		if(!attackers.isEmpty()) {
			attackers.remove(attackers.size() - 1);
			fight(witch);
		}
		undeadSem.release();
			
		lock.lock();
		witches.remove(witch);
		lock.unlock();
		if(this.ingredients.isEmpty() ||this.witches.isEmpty()) {
			turn = false;
			notifyAll();
		}
		System.out.println("The witch leaves the coven");
	}
	
	/**
	 * This is the method used to fight an undead.<br>
	 * The witch asks for help from the Grand Sorcerer. If the sorcerer has potions and if the witch has the potions,
	 * she will use them and the undead will be scared. If the witch does not have potions and 10% of ingredients
	 * are gone.
	 * @param witch Witch - the witch that fights the undead.
	 */
	public synchronized void fight(Witch witch) {
		try {
			witch.getHelpFromTheSorcerer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(witch.getPotions().size() > 0) {
			System.out.println("The witch has the potions from the Grand Sorcerer and she defends the coven");
			witch.usePotions();
			System.out.println("The Undead is scared and he run away");
		}else {
			System.out.println("The witch doesn't have the potions and 10% of the ingredients will be lost. :(");
			System.out.println("Ingredients before: " + ingredients.size());
			try {
				ingredientsSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int lostIngredients = (int) (0.1 * ingredients.size());
			for(int iterator = 0; iterator < lostIngredients; iterator++) {
				ingredients.remove(iterator);
			}
			ingredientsSem.release();
			System.out.println("Ingredients after: " + ingredients.size());
		}
	}
	

	/**
	 * This is the method used by an undead to attack the coven.<br>
	 * The undead that attacks the coven is added to the vector of attackers after he acquires the semaphore for the
	 * attackers. Then the semaphore is released.<br>
	 * If there is a witch visiting the coven, the udead will sleep for 1 second.
	 * If the coven is not visited by witches, the undead will scare some demons(if the coven has demons) and the
	 * coven's ingredients will be gone. Then the undead will leave the coven.
	 * @param undead Undead - the undead that attacks the coven.
	 */
	public synchronized void attack(Undead undead) {
		try {
			undeadSem.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		attackers.add(undead);
		undeadSem.release();
		System.out.println("The coven no." + covenId + " is attacked. HELP!");
		
		if(!witches.isEmpty()) {
			System.out.println("A witch is visiting the coven "+covenId+", so she will try to fight the undead");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			Integer iterator = 0;
			Random random = new Random();
			Integer noRetiredDemons = random.nextInt(5)+5;
			try {
				queue.acquire();
				demonsSem.acquire();
				queue.release();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if(demons.size() <= noRetiredDemons || !demons.isEmpty()) {
				for(int i = 0; i < demons.size(); i++) {
					demons.get(i).scare();
				}
			}else if(!demons.isEmpty()){
				while(iterator <= noRetiredDemons) {
					Integer number = random.nextInt(demons.size());
					System.out.println("The demon no." + number + " was scared.");
					demons.get(number).scare();
					iterator++;
				}
			}else {
				System.out.println("The coven did not have demons. But the ingredients are gone");
			}
			demonsSem.release();
			try {
				ingredientsSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.ingredients.clear();
			ingredientsSem.release();
		}
		

		System.out.println("The undead leaves the coven");
	}

	/**
	 * This is a getter.
	 * @return the coven's list of demons
	 */
	public List<Demon> getDemons() {
		return demons;
	}
	

	/**
	 * This is a getter.
	 * @return the coven's list of witches.
	 */
	public Vector<Witch> getWitch() {
		return witches;
	}

}
