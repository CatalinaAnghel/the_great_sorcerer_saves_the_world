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
	 * The vector of covens
	 */
	static Vector<Coven> covens = new Vector<Coven>();
	/**
	 * The list with the ingredients used to make the potions
	 */
	protected static String[] ingredientTypes = {"Anethum graveolens", 
			"the legs of the Funnel-web spider", 
			"student's tears",
			"ambrosia seeds",
			"the skin of the Black Mamba",
			"the venom of Inland Taipan",
			"a crow's claws",
			"sand from the Mariana Trench",
			"Aloe Vera",
			"a rock from the Vesuvius vulcano"};
	
	/**
	 * This is the main method used to connect to the port 4999 in order to create the "portal". 
	 * Here, the following objects are created:
	 * <ol><li>covens</li>
	 * <li>the undeads</li>
	 * <li>the witches</li>
	 * <li>the Witch Craft Book</li>
	 * <li>the potions</li>
	 * <li>the manager used to create the demons</li>
	 * <li>the manager used to retire the demons</li>
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
		
		Integer noCovens = random.nextInt(7) + 3;
		for(int iterator = 0; iterator < noCovens; iterator++) {
			covens.add(new Coven(iterator, random.nextInt(400) + 100, ingredientTypes));
			covens.get(iterator).start();
		}
		WitchCraftBook book = new WitchCraftBook(ingredientTypes);
		book.createBook();
		List<Potion> recipes = book.getPotions();
		System.out.println(recipes);
		Witch witches[] = new Witch[10];
		for(int iterator = 0; iterator < 10; iterator++) {
			witches[iterator] = new Witch(iterator, covens, recipes, pr, br);
			witches[iterator].start();
		}
		Integer noUndead = random.nextInt(30) + 20;
		Undead undeads[] = new Undead[noUndead];
		for(Integer iterator = 0; iterator < noUndead; iterator++) {
			undeads[iterator] = new Undead(iterator, covens);
			undeads[iterator].start();
		}
		// create the retirement manager
		DemonRetireManager retireManager = new DemonRetireManager(covens);
		DemonDistributionManager manager = new DemonDistributionManager(covens, retireManager);
		manager.start();
		// start the retirement manager
		retireManager.start();
		
		try {
			manager.join();
			// call the method join() for the retirement manager
			retireManager.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s.close();
	}
}
