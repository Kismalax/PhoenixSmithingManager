package com.github.kismalax.smithingmanager.listener;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import com.github.kismalax.smithingmanager.PhoenixSmithingManager;

public class LootGenerateListener implements Listener {
	private final Predicate<ItemStack> removable = t -> PhoenixSmithingManager.getInstance().getRemoveGeneratedLoot().contains(t.getType());

	@EventHandler
	public void onLootGenerateEvent(LootGenerateEvent e) {
		filterLoot(e.getLoot(), e.getEntity(), e.getLootContext().getLocation());
	}
	
	@EventHandler
	public void onBlockDispenseLoot(BlockDispenseLootEvent e) {
		filterLoot(e.getDispensedLoot(), e.getPlayer(), e.getBlock().getLocation());
	}
	
	private void filterLoot(List<ItemStack> loot, Entity entity, Location location) {
		List<String> removables = null;
		if (PhoenixSmithingManager.getInstance().isDebug()) {
			removables = loot.stream().filter(removable).map(t -> t.getType().name()).toList();
		}
		
		boolean removed = loot.removeIf(removable);
		
		if (removed && PhoenixSmithingManager.getInstance().isDebug()) {
			String player = entity instanceof Player ? entity.getName() : "Unknown";
			for (String material : removables) {
				PhoenixSmithingManager.getInstance().getLogger().log(Level.INFO, "Removed loot from dispense at x:{0} y:{1} z:{2} @{3} for player: {4}: {5}",
						new Object[] {location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName(), player, material});
			}
		}
	}
}
