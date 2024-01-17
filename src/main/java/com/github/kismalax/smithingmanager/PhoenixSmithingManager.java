package com.github.kismalax.smithingmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kismalax.smithingmanager.listener.BrushListener;
import com.github.kismalax.smithingmanager.listener.LootGenerateListener;
import com.github.kismalax.smithingmanager.listener.MobLootListener;

public class PhoenixSmithingManager extends JavaPlugin {
	private static final ArrayList<String> allSmithingTemplates = new ArrayList<>();
	private static PhoenixSmithingManager instance = null;
	
	private Set<Material> disableCrafting = new HashSet<>();
	private Set<Material> removeGeneratedLoot = new HashSet<>();
	private Set<Material> removeBrushLoot = new HashSet<>();

	private boolean removeMobLoot = true;
	private boolean debug = true;
	
	static {
		allSmithingTemplates.addAll(Tag.ITEMS_TRIM_TEMPLATES.getValues().stream().map(Enum::name).toList());
		allSmithingTemplates.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE.name());
	}
	
	@Override
	public void onEnable() {
		instance = this;
		prepareConfig();
		
		if (!removeGeneratedLoot.isEmpty()) {
			getLogger().info("Registering Listener for Generated Loot...");
			getServer().getPluginManager().registerEvents(new LootGenerateListener(), this);
		}
		if (!removeBrushLoot.isEmpty()) {
			getLogger().info("Registering Listener for Brush Loot...");
			getServer().getPluginManager().registerEvents(new BrushListener(), this);
		}
		if (removeMobLoot) {
			getLogger().info("Registering Listener for Mob Loot...");
			getServer().getPluginManager().registerEvents(new MobLootListener(), this);
		}
		
		if (!disableCrafting.isEmpty()) {
			int cnt = RecipeUtil.removeRecipes(disableCrafting);
			getLogger().log(Level.INFO, "{0} crafting recipes disabled", cnt);
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
		
		config.addDefault("disableCrafting", allSmithingTemplates);
		config.addDefault("removeGeneratedLoot", allSmithingTemplates);
		config.addDefault("removeBrushLoot", List.of(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE.name(), Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE.name(),
				Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE.name(), Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE.name()));
		config.addDefault("removeMobLoot", true);
		config.addDefault("debug", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		applyMaterials(disableCrafting, "disableCrafting");
		applyMaterials(removeGeneratedLoot, "removeGeneratedLoot");
		applyMaterials(removeBrushLoot, "removeBrushLoot");
		
		removeMobLoot = config.getBoolean("removeMobLoot");
	}
	
	private void applyMaterials(Set<Material> target, String config) {
		for (String templateMaterial : getConfig().getStringList(config)) {
			if (allSmithingTemplates.contains(templateMaterial.toUpperCase())) {
				target.add(Material.getMaterial(templateMaterial));
			} else {
				getLogger().log(Level.WARNING, "Given MATERIAL name {0} is not a valid Smithing Template material name.", templateMaterial);
			}
		}
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
	
	public Set<Material> getRemoveBrushLoot() {
		return removeBrushLoot;
	}

	public boolean isRemoveMobLoot() {
		return removeMobLoot;
	}

	public boolean isDebug() {
		return debug;
	}
}
