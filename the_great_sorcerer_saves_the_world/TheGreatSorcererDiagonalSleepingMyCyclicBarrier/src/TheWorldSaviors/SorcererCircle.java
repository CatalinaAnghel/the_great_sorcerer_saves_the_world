package TheWorldSaviors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * This is the class SorcererCircle which is used to communicate with the Great Sorcerer.
 * It has two data members:
 * <ul>
 * <li><strong><em style="color: teal">covens</em></strong> - this is the vector of covens.</li>
 * <li><strong><em style="color: teal">ingredientTypes</em></strong> - this is the array with the used ingredients.
 * There are 10 types of ingredients:
 * 	<ol>
 * 	<li><em style="color: magenta">Anethum graveolens</em></li>
 * 	<li><em style="color: magenta">The legs of the Funnel-web spider</em></li>
 * 	<li><em style="color: magenta">Student's tears</em></li>
 * 	<li><em style="color: magenta">Ambrosia seeds</em></li>
 * 	<li><em style="color: magenta">The skin of a Black Mamba snake</em></li>
 * 	<li><em style="color: magenta">The venom of an Inland Taipan</em></li>
 * 	<li><em style="color: magenta">A crow's claws</em></li>
 * 	<li><em style="color: magenta">Sand from the Mariana Trench</em></li>
 * 	<li><em style="color: magenta">Aloe Vera</em></li>  
 * 	<li><em style="color: magenta">A rock from the Vesuvius vulcano</em></li>
 * 	</ol></li>
 * </ul>
 * This class has just one method: <strong><em>main</em></strong>, used to create the socket used to communicate with the 
 * Sorcerer. Here, the covens, the witches, the demon manager and the undeads are created. The craft book for the witches
 * is also created here.<br>
 * @author Anghel Florina-Catalina
 *
 */
public class SorcererCircle{
	/**
	 *  This is the vector of covens.
	 */
	static Vector<Coven> covens = new Vector<Coven>();

	/**
	 *  These are the ingredients used to make the potions.
	 */
	protected static String[] ingredientTypes = {"Anethum graveolens", 
			"The legs of the Funnel-web spider", 
			"Student's tears",
			"Ambrosia seeds",
			"The skin of a Black Mamba snake",
			"The venom of an Inland Taipan",
			"A crow's claws",
			"Sand from the Mariana Trench",
			"Aloe Vera",
			"A rock from the Vesuvius vulcano"};
	
	/**
	 * This is the method main used to connect to the port 4999. Here, the following objects are created:
	 * <ol><li>covens</li>
	 * <li>the undeads</li>
	 * <li>the witches</li>
	 * <li>the Witch Craft Book</li>
	 * <li>the potions</li>
	 * <li>the manager used to create the demons</li>
	 * </ol>
	 * @param args command line arguments
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		Random random = new Random();
		Socket s = new Socket("localhost", 4999);
		
		PrintWriter pr = new PrintWriter(s.getOutputStream());
		InputStreamReader in = new InputStreamReader(s.getInputStream());
		BufferedReader br = new BufferedReader(in);
		System.out.println("~~~ This is the end of the world. Let's try to save it!!! ~~~\n");
		// Build the covens:
		Integer noCovens = random.nextInt(7) + 3;
		
		System.out.println("Step 1: Build the covens.");
		MyCyclicBarrier cyclicBarrier[] = new MyCyclicBarrier[noCovens];
		for(int iterator = 0; iterator < noCovens; iterator++) {
			Integer noDemons = random.nextInt(400) + 100;
			cyclicBarrier[iterator] = new MyCyclicBarrier(noDemons);
			covens.add(new Coven(iterator, noDemons, ingredientTypes, cyclicBarrier[iterator]));
			covens.get(iterator).start();
		}
		
		// Create the witches and the Witch Craft Book:
		System.out.println("Step 2: Prepare the witches and give them the Witch Craft Book.\nThe recipes are:");
		WitchCraftBook book = new WitchCraftBook(ingredientTypes);
		book.createBook();
		List<Potion> recipes = book.getPotions();
		System.out.println(recipes);
		Witch witches[] = new Witch[10];
		for(int iterator = 0; iterator < 10; iterator++) {
			witches[iterator] = new Witch(iterator, covens, recipes, pr, br);
			witches[iterator].start();
		}
		
		// Create the array of undeads: 
		Integer noUndead = random.nextInt(30) + 20;
		Undead undeads[] = new Undead[noUndead];
		for(Integer iterator = 0; iterator < noUndead; iterator++) {
			undeads[iterator] = new Undead(iterator, covens);
			undeads[iterator].start();
		}
		
		// Create the demon manager:
		System.out.println("Step 3: Get the demon manager and ask him to hire some demons.\n");
		DemonDistributionManager manager = new DemonDistributionManager(covens);
		manager.start();
		
		try {
			manager.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		s.close();
	}
}
