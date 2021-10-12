package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * This is the class Demon which extends the class Thread.
 * @author Anghel Florina-Catalina
 *
 */
public class Demon extends Thread {
	/**
	 * This is the demon's id
	 */
	protected Integer demonId;
	
	/**
	 * This variable is used to store the row of the demon's current room.
	 */
	protected volatile Integer row;
	
	/**
	 * This variable is used to store the column of the demon's current room.
	 */
	protected volatile Integer column;
	
	/**
	 * This is the coven of the current demon.
	 */
	protected Coven coven;
	
	/**
	 * This is the current demon's social skill. After 10 walls kicked, the demon will be demoted with 
	 * 100 levels on their social skills. When a demon encounters another demon while trying to move, 
	 * they both increase their social skills rating.
	 */
	protected Integer socialSkill;
	
	/**
	 * This represents the current number of kicked walls.
	 */
	protected Integer noKickedWalls = 0;
	
	/**
	 * This is the list of ingredients produced by the current demon. The demon will send them to the coven if
	 * he is not interrupted. He can be interrupted when the coven scares him, or when an undead attacks the coven.
	 */
	protected List<String> ingredients = new ArrayList<String>();
	
	/**
	 * This variable shows if the current demon is scared. This variable is initially false and when an undead
	 * attacks the coven and scares some demons including the current demon, this variable is set to true for 
	 * every demon that was scared.
	 */
	protected Boolean isScared;
	
	/**
	 * This is a Boolean variable. If it is set to false, then the demon is not retired. If it is set to true, 
	 * then the demon is retired.
	 */
	protected Boolean retired;
	
	/**
	 * This is the semaphore called retireSem. When a demon wants to be retired, he will try to acquire it and if 
	 * he can do it, he will send a retire request to the retirement manager and then he will ask the coven to be
	 * removed from its list of demons.
	 */
	protected static Semaphore retireSem = new Semaphore(0);
	
	/**
	 * This is the retirement manager. He will be asked by the demons to retire them. 
	 */
	protected static DemonRetireManager manager;
	
	/**
	 * This is the constructor of the class Demon.
	 * @param id Integer - the id of the demon
	 * @param coven Coven - the coven that has the current demon
	 * @param row Integer -  the row of the demon's room
	 * @param column Integer - the column of the demon's room
	 * @param socialSkill Integer - the demon's social skill
	 * @param manager DemonRetireManager - the manager used to retire demons
	 */
	public Demon(Integer id, Coven coven, Integer row, Integer column, Integer socialSkill, DemonRetireManager manager) {
		this.demonId = id;
		this.coven = coven;
		this.row = row;
		this.column = column;
		this.socialSkill = socialSkill;
		this.isScared = false;
		this.retired = false;
		Demon.manager = manager;
	}
	
	/**
	 * This is a getter.
	 * @return the row of the demon's room
	 */
	public Integer getRow() {
		return this.row;
	}
	
	/**
	 * This is a getter.
	 * @return the column of the demon's room
	 */
	public Integer getColumn() {
		return this.column;
	}
	
	/**
	 * This is the method used by the demon when he tries to move up. Firstly, the row is decremented. If the 
	 * row &lt; 0, the demon kicked a wall and he will go back in the previous room. If he kicked 10 walls, the demon
	 * will be demoted with 100 levels on their social skills and the number of kicked walls is set to zero.<br>
	 * If row &ge; 0, then the demon will check to see if there is someone in the targeted room. If there is another
	 * demon in the desired room, the demon will go back in his previous room and they both increase their social 
	 * skills rating. Else, if the targeted room is empty, the demon will go there and a local variable(initially 
	 * set to false) will be set to true. 
	 * @return true if the move action was successful and false otherwise
	 */
	public synchronized Boolean moveUp() {
		Boolean success = false;
		this.row--;
		if(row < 0) {
			row++;
			this.noKickedWalls++;
			if(this.noKickedWalls == 10) {
				this.socialSkill -= 100;
				this.noKickedWalls = 0;
			}
		}else if(coven.checkRoom(row, column) != null) {
			if(socialSkill < 900) {
				coven.checkRoom(row, column).socialSkill++;
			}
			this.row++;
			socialSkill++;
			
		}else{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * This is the method used by the demon when he tries to move down. Firstly, the row is incremented. If the 
	 * row &gt; coven's capacity, the demon kicked a wall and he will go back in the previous room. If he kicked 10 walls, the demon
	 * will be demoted with 100 levels on their social skills and the number of kicked walls is set to zero.<br>
	 * If row &le; coven's capacity, then the demon will check to see if there is someone in the target room. If there is another
	 * demon in the desired room, the demon will go back in his previous room and they both increase their social 
	 * skills rating. Else, if the targeted room is empty, the demon will go there and a local variable(initially 
	 * set to false) will be set to true. 
	 * @return true if the move action was successful and false otherwise
	 */
	public synchronized Boolean moveDown() {
		Boolean success = false;
		this.row++;
		if( row >= coven.getCapacity()) {
			row--;
			this.noKickedWalls++;
			if(this.noKickedWalls == 10) {
				this.socialSkill -= 100;
				this.noKickedWalls = 0;
			}
		}else if(coven.checkRoom(row, column)!=null) {
			if(socialSkill < 900) {
				coven.checkRoom(row, column).socialSkill++;
			}
			this.row--;
			socialSkill++;
			
		}else{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * This is the method used by the demon when he tries to move to the left. Firstly, the column is decremented. If the 
	 * column &lt; 0, the demon kicked a wall and he will go back in the previous room. If he kicked 10 walls, the demon
	 * will be demoted with 100 levels on their social skills and the number of kicked walls is set to zero.<br>
	 * If column &ge; 0, then the demon will check to see if there is someone in the targeted room. If there is another
	 * demon in the desired room, the demon will go back in his previous room and they both increase their social 
	 * skills rating. Else, if the targeted room is empty, the demon will go there and a local variable(initially 
	 * set to false) will be set to true. 
	 * @return true if the move action was successful and false otherwise
	 */
	public synchronized Boolean moveLeft() {
		Boolean success = false;
		this.column--;
		if(column < 0 ) {
			column++;
			this.noKickedWalls++;
			if(this.noKickedWalls == 10) {
				this.socialSkill -= 100;
				this.noKickedWalls = 0;
			}
		}else if(coven.checkRoom(row, column)!=null) {
			if(socialSkill < 900) {
				coven.checkRoom(row, column).socialSkill += 100;
			}
			this.column++;
			socialSkill += 100;
			
		}else{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * This is the method used by the demon to register into his coven after he is spawned by the demon distribution
	 * manager.
	 */
	public void registerIntoTheCoven() {
		System.out.println("The demon no." + demonId + " was spawned into the coven no. " + coven.getCovenId());
		coven.addDemonIntoTheCoven(this);
	}
	
	/**
	 * This is the method used by the demon when he tries to move to the right. Firstly, the column is incremented. If the 
	 * column &gt; coven's capacity, the demon kicked a wall and he will go back in the previous room. If he kicked 10 walls, the demon
	 * will be demoted with 100 levels on their social skills and the number of kicked walls is set to zero.<br>
	 * If column &le; coven's capacity, then the demon will check to see if there is someone in the targeted room. If there is another
	 * demon in the desired room, the demon will go back in his previous room and they both increase their social 
	 * skills rating. Else, if the targeted room is empty, the demon will go there and a local variable(initially 
	 * set to false) will be set to true. 
	 * @return true if the move action was successful and false otherwise
	 */
	public synchronized Boolean moveRight() {
		Boolean success = false;
		this.column++;
		if(column >= coven.getCapacity()) {
			this.column--;
			this.noKickedWalls++;
			if(this.noKickedWalls == 10) {
				this.socialSkill -= 100;
				this.noKickedWalls = 0;
			}
		}else if(coven.checkRoom(row, column)!=null) {
			if(socialSkill < 900) {
				coven.checkRoom(row, column).socialSkill += 100;
			}
			this.column--;
			socialSkill += 100;
			
		}else{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * This is the method used by the demons to send the ingredients to their coven.
	 */
	public void giveTheIngredientsToTheCoven() {
		coven.receiveIngredient(this.ingredients);
		this.ingredients.clear();
	}
	
	/**
	 * This is the method used by the demons to create ingredients. This method is used to make the ingredients
	 * produced after just one move. The demons can create maximum 10 ingredients at once depending on their
	 * social skill. 
	 */
	public synchronized void createIngredients() {
		Random random = new Random();
		for(Integer iterator = 0; iterator < socialSkill/100 + 1; iterator++) {
			Integer index = random.nextInt(10);
			ingredients.add(coven.ingredientTypes[index]);
			System.out.println("The ingredient: " + coven.ingredientTypes[index] + " was created by the demon no." + demonId +" from coven no. " + this.coven.getCovenId());
		}
	}
	
	/**
	 * This is the method run which overrides the method with the same name from the class Thread.<br>
	 * While the demon is not scared, he will try to change his room. If he can change it, he will create ingredients
	 * and he will send them to the coven. Then he will rest for 30 milliseconds. If he can not move, then he will
	 * sleep for a random time (between 10 and 50 milliseconds). If the demon can acquire the retireSem, he will 
	 * send a retire request to the retirement manager and then he will ask the coven to remove him from the 
	 * coven's list of demons.
	 */
	@Override
	public void run() {
			while(!retired) {
				if(!isScared) {
					Integer originalRow = this.row;
					Integer originalColumn = this.column;
					if(moveUp() || moveLeft() || moveDown() || moveRight()) {
						coven.changeRoom(this, originalRow, originalColumn, row, column);
						createIngredients();
						giveTheIngredientsToTheCoven();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else {
						Random random = new Random();
						try {
							System.out.println("The demon" + demonId +" could not move and he is sleeping.");
							Thread.sleep(random.nextInt(40) + 10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
				// Try to acquire the semaphore in order to be retired
				if(retireSem.tryAcquire()) {
					// send the retirement request to the retirement manager
					Demon.manager.sendRetireRequest(this);
					System.out.println("The demon "+demonId+" sent the request to be retired. ");
					
				}
				
			}
			
			if(retired == true) {
				// if the demon is marked as retired, he will ask the coven to remove him from the list of demons.
				coven.askToBeRemovedFromCoven(this);
			}
	}
	
	/**
	 * This is the method that marks the demon as scared. It sets the variable isScared to true.
	 */
	public void scare() {
		this.isScared = true;
	}

	
	/**
	 * This is a setter. It is used to mark a demon as retired.
	 * @param retired Boolean - true if the demon is retired or false if he is not retired.
	 */
	public synchronized void setRetired(Boolean retired) {
		this.retired = retired;
	}
	
	/**
	 * When a coven scares a demon, the demon will lose his ingredients.
	 */
	public synchronized void loseIngredients() {
		this.ingredients.clear();
		System.out.println("Sadly, the demon no." + demonId + "was scared and he lost his ingredients.");
	}

	/**
	 * This is a getter.
	 * @return the semaphore used to retire demons.
	 */
	public static Semaphore getRetireSem() {
		return retireSem;
	}
}
