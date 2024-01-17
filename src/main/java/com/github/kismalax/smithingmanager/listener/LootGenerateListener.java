package com.github.kismalax.smithingmanager.listener;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import com.github.kismalax.smithingmanager.PhoenixSmithingManager;

public class LootGenerateListener implements Listener {
	private final Predicate<ItemStack> removable = t -> PhoenixSmithingManager.getInstance().getRemoveGeneratedLoot().contains(t.getType());

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
