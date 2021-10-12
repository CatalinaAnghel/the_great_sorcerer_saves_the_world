package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class Potion. It is represented by a recipe and a name.
 * @author Anghel Florina-Catalina
 *
 */
public class Potion {
	/**
	 * This is the list of ingredients used to make the potion.
	 */
	protected List<String> recipe = new ArrayList<String>();
	
	/**
	 * This is the name of the potion.
	 */
	protected String name;	
	
	/**
	 * This is the constructor for the class Potion.
	 * @param recipe List &lt; String &gt; - the list of ingredients used to make the potion.
	 * @param name String - the name of the potion.
	 */
	public Potion(List<String> recipe, String name) {
		this.recipe = recipe;
		this.name = name;
	}
	
	/**
	 * This is a getter.
	 * @return a String which represents the name of the potion
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This is a getter.
	 * @return a list of strings which represents the ingredients used to make the potions.
	 */
	public List<String> getRecipe() {
		return this.recipe;
	}
}
