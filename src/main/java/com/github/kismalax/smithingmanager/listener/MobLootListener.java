package com.github.kismalax.smithingmanager.listener;

import java.util.function.Predicate;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.kismalax.smithingmanager.PhoenixSmithingManager;

public class MobLootListener implements Listener {
	private final Predicate<ItemStack> tideArmorTrim = t -> t.getType() == Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent e) {
		if (PhoenixSmithingManager.getInstance().isRemoveMobLoot() && e.getEntityType() == EntityType.ELDER_GUARDIAN) {
			boolean removed = e.getDrops().removeIf(tideArmorTrim);
			if (removed && PhoenixSmithingManager.getInstance().isDebug()) {
				Location loc = e.getEntity().getLocation();
				PhoenixSmithingManager.getInstance().getLogger().log(Level.INFO, "Removed drop from ELDER_GUARDIAN at x:{0} y:{1} z:{2} @{3}: TIDE_ARMOR_TRIM_SMITHING_TEMPLATE",
						new Object[] { loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName() });
			}
		}
	}
}
