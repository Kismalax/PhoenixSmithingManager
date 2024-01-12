package com.github.kismalax.smithingmanager;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

public class RecipeUtil {
	private RecipeUtil() {}
	
	public static void removeRecipes(Collection<Material> materials) {
		Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
        Recipe recipe;
        while(iterator.hasNext())
        {
            recipe = iterator.next();
            if (recipe != null && materials.contains(recipe.getResult().getType())) iterator.remove();
        }
	}
}
