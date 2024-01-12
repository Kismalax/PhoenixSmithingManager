package com.github.kismalax.smithingmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class PhoenixSmithingManager extends JavaPlugin {
	private static PhoenixSmithingManager instance = null;
	
	private Set<Material> disableCrafting = new HashSet<>();
	private Set<Material> removeGeneratedLoot = new HashSet<>();
	private boolean removeMobLoot = true;
	private boolean debug = true;
	
	@Override
	public void onEnable() {
		instance = this;
		prepareConfig();
		
		getServer().getPluginManager().registerEvents(new PhoenixSmithingManagerListener(), this);
		getLogger().info("Listener registered");
		
		if (!disableCrafting.isEmpty()) {
			RecipeUtil.removeRecipes(disableCrafting);
			getLogger().log(Level.INFO, "{0} crafting recipes disabled", disableCrafting.size());
		}
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(instance);
		getLogger().info("Listener unregistered");
		instance = null;
	}
	
	private void prepareConfig() {
		FileConfiguration config = getConfig();
		
		ArrayList<String> allSmithingTemplates = new ArrayList<>();
		allSmithingTemplates.addAll(Tag.ITEMS_TRIM_TEMPLATES.getValues().stream().map(Enum::name).toList());
		allSmithingTemplates.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE.name());
		
		config.addDefault("disableCrafting", allSmithingTemplates);
		config.addDefault("removeGeneratedLoot", allSmithingTemplates);
		config.addDefault("removeMobLoot", true);
		config.addDefault("debug", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		for (String templateMaterial : getConfig().getStringList("disableCrafting")) {
			if (allSmithingTemplates.contains(templateMaterial.toUpperCase())) {
				disableCrafting.add(Material.getMaterial(templateMaterial));
			} else {
				getLogger().log(Level.WARNING, "Given MATERIAL name {0} is not a valid Smithing Template material name.", templateMaterial);
			}
		}
		
		for (String templateMaterial : getConfig().getStringList("removeGeneratedLoot")) {
			if (allSmithingTemplates.contains(templateMaterial.toUpperCase())) {
				removeGeneratedLoot.add(Material.getMaterial(templateMaterial));
			} else {
				getLogger().log(Level.WARNING, "Given MATERIAL name {0} is not a valid Smithing Template material name.", templateMaterial);
			}
		}
		
		removeMobLoot = getConfig().getBoolean("removeMobLoot");
	}
	
	public static PhoenixSmithingManager getInstance() {
		if (instance == null) throw new IllegalStateException("PhoenixSmithingManager is not loaded");
		return instance;
	}

	public Set<Material> getDisableCrafting() {
		return disableCrafting;
	}

	public Set<Material> getRemoveGeneratedLoot() {
		return removeGeneratedLoot;
	}

	public boolean isRemoveMobLoot() {
		return removeMobLoot;
	}

	public boolean isDebug() {
		return debug;
	}
}
