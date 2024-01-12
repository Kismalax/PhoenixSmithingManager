package com.github.kismalax.smithingmanager;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

public class PhoenixSmithingManagerListener implements Listener {
	private final Predicate<ItemStack> tideArmorTrim = t -> t.getType() == Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
	private final Predicate<ItemStack> removable = t -> PhoenixSmithingManager.getInstance().getRemoveGeneratedLoot().contains(t.getType());

	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent e) {
		if (PhoenixSmithingManager.getInstance().isRemoveMobLoot() && e.getEntityType() == EntityType.ELDER_GUARDIAN) {
			boolean removed = e.getDrops().removeIf(tideArmorTrim);
			if (removed && PhoenixSmithingManager.getInstance().isDebug()) {
				PhoenixSmithingManager.getInstance().getLogger().info("Removed drop from ELDER_GUARDIAN: TIDE_ARMOR_TRIM_SMITHING_TEMPLATE");
			}
		}
	}

	@EventHandler
	public void onLootGenerateEvent(LootGenerateEvent e) {
		List<String> removables = null;
		if (PhoenixSmithingManager.getInstance().isDebug()) {
			removables = e.getLoot().stream().filter(removable).map(t -> t.getType().name()).toList();
		}
		
		boolean removed = e.getLoot().removeIf(removable);
		
		if (removed && PhoenixSmithingManager.getInstance().isDebug()) {
			Location loc = e.getLootContext().getLocation();
			String player = e.getEntity() instanceof Player ? e.getEntity().getName() : "Unknown";
			for (String material : removables) {
				PhoenixSmithingManager.getInstance().getLogger().log(Level.INFO, "Removed loot from generation at x:{0} y:{1} z:{2} @{3} for player: {4}: {5}",
						new Object[] {loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), player, material});
			}
		}
	}
}
