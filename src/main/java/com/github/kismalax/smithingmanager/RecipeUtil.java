package com.github.kismalax.smithingmanager;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

public class RecipeUtil {
	private static final String minecraft = "minecraft";
	
	private RecipeUtil() {}
	
	public static int removeRecipes(Collection<Material> materials) {
		int cnt = 0;
		Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
        Recipe recipe;
        while(iterator.hasNext())
        {
            recipe = iterator.next();
			if (recipe != null && materials.contains(recipe.getResult().getType()) && recipe instanceof Keyed sr
					&& sr.getKey().getNamespace().equals(minecraft)) {
				
				iterator.remove();
				cnt++;
			}
        }
        return cnt;
	}
}
