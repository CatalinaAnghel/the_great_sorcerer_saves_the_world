package TheWorldSaviors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Semaphore;


/**
 * This is the class Witch that extends the class Thread. 
 * @author Anghel Florina-Catalina
 *
 */
public class Witch extends Thread {
	protected static Semaphore sem = new Semaphore(1);

	/**
	 * This is the id of the witch.
	 */
	protected Integer witchId;
	/**
	 * This is the list of the ingredients that are taken from the covens.
	 */
	protected List<String> ingredients = new ArrayList<String>();
	
	/**
	 * This is the vector of covens.
	 */
	protected Vector<Coven> covens;
	
	/**
	 * This is the list of potions that are produced or received by the witch.
	 */
	protected List<String> potions = new ArrayList<String>();
	
	/**
	 * This is the list of recipes used to make the potions.
	 */
	protected List<Potion> recipes = new ArrayList<Potion>();
	
	/**
	 * This is the array of strings that represent the ingredient used in the recipes.
	 */
	protected String[] ingredientTypes;
	
	/**
	 * This is a PrintWriter object used by the witch to communicate with the Great Sorcerer. It is used 
	 * to send the messages. The messages represents the call for help or the potion that the witch tries to 
	 * send to the Great Sorcerer.
	 */
	protected PrintWriter pr;
	
	/**
	 * This is the BufferedReader object used to receive the messages from the Great Sorcerer. The messages 
	 * represent his response after the call for help. The response can represent a string. This string can
	 * be a potion or the message that says that the Great Sorcerer does not have enough potions to help her. 
	 */
	protected BufferedReader br;
	
	/**
	 * This is the constructor of the class Witch.
	 * @param id Integer - the id of the witch
	 * @param covens Vector &lt; Coven &gt; - the vector of covens
	 * @param recipes List &lt; Potion &gt; - the list of recipes
	 * @param pr PrintWriter - used to send messages
	 * @param br BufferedReader - used to read messages 
	 */
	public Witch(Integer id, Vector<Coven> covens, List<Potion> recipes, PrintWriter pr, BufferedReader br) {
		this.covens = covens;
		this.recipes = recipes;
		this.pr = pr;
		this.br = br;
		this.witchId = id;
	}
	
	/**
	 * This is a method used when the witch receives potions from the Great Sorcerer and she will use them in the 
	 * fight.
	 */
	public void usePotions() {
		this.potions.clear();
	}
	
	/**
	 * This is the method used by a witch to get help from the Great Sorcerer. 
	 * It is used when the witch fights the undead. If the portal is not used, the witch will send a message through
	 * it and she will wait for the response. There are two types of responses:
	 * <ul>
	 * <li><em style="color: teal">I don't have enough potions to give you. Good luck!</em> - if the Grand Sorcerer
	 * does not have enought potions</li>
	 * <li><em style="color: teal">A potion</em> - the potion send by the Grand Sorcerer</li>
	 * </ul>
	 * @throws IOException
	 */
	public synchronized void getHelpFromTheSorcerer() throws IOException {
//		try {
//			sem.acquire();
			pr.println("A coven is attaked by an undead. We need some potions to defend it.");
			pr.flush();
			while(br.ready()) {
				String response = br.readLine();
				System.out.println("The sorcerer responsed: " + response);
				if(!response.equals("I don't have enough potions to give you. Good luck!")) {
					this.potions.add(br.readLine());
				}else {
					System.out.println("The Grand Sorcerer does not have any potions.");
				}
			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//			sem.release();
//		}
		
	}
	
	/**
	 * This is the method used by a witch to make a potion. Firstly, she choose a potion and she tries to make it.
	 *  She checks if she has all the ingredients and if she has all of them, she will make the potion.
	 */
	public synchronized void makePotion() {
		Random random = new Random();
		Integer potionNumber = random.nextInt(20);
		Potion recipe = recipes.get(potionNumber);
		System.out.println("The witch checks if she has the ingredients used to make the potion: " + recipe.getName());
		System.out.println("She has: " + ingredients);
		System.out.println("She needs: " + recipe.getRecipe());
		if(ingredients.containsAll(recipe.getRecipe())) {
			System.out.println("The potion " + recipe.getName() + " was made.");
			potions.add(recipe.getName());
			ingredients.removeAll(recipe.getRecipe());
		}else {
			System.out.println("She doesn't enough ingredients to make the potion called: " + recipe.getName());
		}
	}

	/**
	 * This is the method run which overrides the method with the same name from the class Thread.<br>
	 * The witch will visit just the covens that have ingredients. If she has more than 4 ingredients, she will try
	 * to make a potion using one of the 20 available recipes.<br>
	 * If the witch made potions, she will send them to the Grand Sorcerer through the portal. And then she will let 
	 * the waiting witches to use the portal.
	 */
	@Override
	public void run() {
		while(true) {
			synchronized(this) {
				Integer covenIndex;
			
				for(covenIndex = 0; covenIndex < covens.size(); covenIndex++) {
					if(!covens.get(covenIndex).getIngredientsInfo().isEmpty()) {	
						System.out.println("The witch no."+witchId+ " is visiting the coven no."+covenIndex+" to get the ingredients");
						covens.get(covenIndex).visit(this.ingredients, this);
						Integer it = 0;
						while(ingredients.size() >= 4 && it < 20) {
							System.out.println("The witch tries to make a potion");
							this.makePotion();
							it++;
						}
					}
				}
			
				if(potions.size() > 0) {
					System.out.println("She sends the potions through the Magic Portal");
				
					for(int i = 0; i< potions.size(); i++) {
						pr.println(potions.get(i));
						pr.flush();
						potions.remove(i);
					}	
				}
			}
		}
	}

	/**
	 * This is a method used by the witches when they try to get the ingredients from the coven.
	 * @param ing List &lt; String &gt; - the received ingredients
	 */
	public void takeIngredients(List<String> ing) {
		this.ingredients.addAll(ing);
	}

	/**
	 * This is a getter.
	 * @return the list of potions received or produced by the witch.
	 */
	public List<String> getPotions() {
		return potions;
	}
}
