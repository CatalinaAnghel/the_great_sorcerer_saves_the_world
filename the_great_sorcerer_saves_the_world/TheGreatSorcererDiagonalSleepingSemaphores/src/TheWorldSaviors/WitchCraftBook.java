package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the Witch Craft Book. It has all the recipes used to make the potions.
 * The potions are:
 * <ol>
 * <li><em style="color: magenta">Healing Potion</em></li>
 * <li><em style="color: magenta">Fearless Elixir</em></li>
 * <li><em style="color: magenta">The Sun's Elixir</em></li>
 * <li><em style="color: magenta">The Moon's Elixir</em></li>
 * <li><em style="color: magenta">The Death's Taste</em></li>
 * <li><em style="color: magenta">The Venom</em></li>
 * <li><em style="color: magenta">Black Armour</em></li>
 * <li><em style="color: magenta">Anti PTSD Elixir</em></li>
 * <li><em style="color: magenta">Future Reading Potion</em></li>
 * <li><em style="color: magenta">Localisation Potion</em></li>
 * <li><em style="color: magenta">The strongest shield</em></li>
 * <li><em style="color: magenta">The indestructible sword</em></li>
 * <li><em style="color: magenta">Light-Speed Elixir</em></li>
 * <li><em style="color: magenta">Invisibility Potion</em></li>
 * <li><em style="color: magenta">Undead Camouflage</em></li>
 * <li><em style="color: magenta">Dead for an hour</em></li>
 * <li><em style="color: magenta">Teleportation Potion</em></li>
 * <li><em style="color: magenta">The strongest for 60 seconds</em></li>
 * <li><em style="color: magenta">Sleeping Potion</em></li>
 * <li><em style="color: magenta">Freezing Potion</em></li>
 * </ol>
 * @author Anghel Florina-Catalina
 *
 */
public class WitchCraftBook {
	/**
	 * This is the array of ingredients.
	 */
	protected String[] ingredientTypes;
	
	/**
	 * This is the array of possible potion names.
	 */
	protected String[] recipeNames = {"Healing Potion", "Fearless Elixir", "The Sun's Elixir", 
			"The Moon's Elixir", "The Death's Taste", "The Venom", "Black Armour", 
			"Anti PTSD Elixir", "Future Reading Potion", "Localisation Potion", "The strongest shield", 
			"The indestructible sword", "Light-Speed Elixir", "Invisibility Potion", 
			"Undead Camouflage", "Dead for an hour", "Teleportation Potion", 
			"The strongest for 60 seconds", "Sleeping Potion", "Freezing Potion"};
	
	/**
	 * This is the list of potions that are included into the Witch Craft Book.
	 */
	protected List<Potion> potions = new ArrayList<Potion>();
	
	/**
	 * This is the constructor of the class WitchCraftBook.
	 * @param ingredientTypes String[] - the array of possible ingredients that can be used to make the potions.
	 */
	public WitchCraftBook(String[] ingredientTypes) {
		this.ingredientTypes = ingredientTypes;
		
	}
	
	/**
	 * This is the method used to create the book's recipes. It creates 20 recipes with distinct ingredients. 
	 * Every recipe will have a number of ingredients included in the interval [4, 8].
	 */
	public void createBook() {
		Random random = new Random();
		for(Integer iterator = 0; iterator < 20; iterator++) {
			// generate the number of ingredients for the current recipe
			Integer noIngredients = random.nextInt(4) + 4;
			List<String> list = new ArrayList<String>();
			int position;
			int i = 0;
			while(i < noIngredients) {
				// choose random distinct ingredients.
				position = random.nextInt(10);
				String ingredient = ingredientTypes[position];
				if(!list.contains(ingredient)) {
					// if the ingredient does not appear in the current recipe, add it.
					list.add(ingredient);
					i++;
				}
				
			}
			System.out.println(recipeNames[iterator] +": list:"+ list);
			// add the recipe into the book
			potions.add(new Potion(list, recipeNames[iterator]));
		}
	}
	
	/**
	 * This is a getter.
	 * @return the list of available potion recipes.
	 */
	public List<Potion> getPotions(){
		return this.potions;
	}
}
