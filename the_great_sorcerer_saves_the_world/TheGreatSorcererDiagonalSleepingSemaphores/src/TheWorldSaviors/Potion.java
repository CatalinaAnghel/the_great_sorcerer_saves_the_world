package TheWorldSaviors;

import java.util.ArrayList;
import java.util.List;

public class Potion {
	protected List<String> recipe = new ArrayList<String>();
	protected String name;	
	public Potion(List<String> recipe, String name) {
		this.recipe = recipe;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public List<String> getRecipe() {
		return this.recipe;
	}
}
