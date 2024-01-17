package com.github.kismalax.smithingmanager.listener;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.data.Brushable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import com.github.kismalax.smithingmanager.PhoenixSmithingManager;

public class BrushListener implements Listener {
	private final Predicate<Item> removable = t -> PhoenixSmithingManager.getInstance().getRemoveBrushLoot().contains(t.getItemStack().getType());
	
	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent e) {
		if (e.getBlock().getType() != Material.SUSPICIOUS_GRAVEL && e.getBlock().getType() != Material.SUSPICIOUS_SAND) return;
		
		List<String> removables = null;
		if (PhoenixSmithingManager.getInstance().isDebug()) {
			removables = e.getItems().stream().filter(removable).map(t -> t.getItemStack().getType().name()).toList();
		}
		
		boolean removed = e.getItems().removeIf(removable);
		
		if (removed && PhoenixSmithingManager.getInstance().isDebug()) {
			Location loc = e.getBlock().getLocation();
			String player = e.getPlayer().getName();
			for (String material : removables) {
				PhoenixSmithingManager.getInstance().getLogger().log(Level.INFO, "Removed loot from brush drop at x:{0} y:{1} z:{2} @{3} for player: {4}: {5}",
						new Object[] {loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), player, material});
			}
		}
	}
	
	@EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
		final ItemStack item = e.getItem();
        if (item == null) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!item.getType().equals(Material.BRUSH)) return;
        
        final Block block = e.getClickedBlock();
        if (block.getType() != Material.SUSPICIOUS_GRAVEL && block.getType() != Material.SUSPICIOUS_SAND) return;
        
        if (block.getState() instanceof BrushableBlock bb && bb.getLootTable() != null) {
        	Bukkit.getScheduler().runTaskLater(PhoenixSmithingManager.getInstance(), new BrushCheckerTask(bb.getLootTable(), e.getPlayer(), block), 6);
        }
	}
	
	private class BrushCheckerTask implements Runnable {
		LootTable lootTable;
		Player player;
		Block block;
		
		public BrushCheckerTask(LootTable lootTable, Player player, Block block) {
			super();
			this.lootTable = lootTable;
			this.player = player;
			this.block = block;
		}

		@Override
		public void run() {
			if (block.getState() instanceof BrushableBlock brushableBlock) {
				if (brushableBlock.getItem() == null) return;

				if (PhoenixSmithingManager.getInstance().getRemoveBrushLoot().contains(brushableBlock.getItem().getType())) {
					if (PhoenixSmithingManager.getInstance().isDebug()) {
						PhoenixSmithingManager.getInstance().getLogger().log(Level.INFO, "Removed loot from brush at x:{0} y:{1} z:{2} @{3} for player: {4}: {5}",
								new Object[] { player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(),
										player.getLocation().getWorld().getName(), player.getName(), brushableBlock.getItem().getType() });
					}

					brushableBlock.setItem(null);
					brushableBlock.setLootTable(lootTable);
					brushableBlock.update();

					Brushable b = (Brushable) block.getBlockData();
					b.setDusted(0);
					block.setBlockData(b);

					Bukkit.getScheduler().runTaskLater(PhoenixSmithingManager.getInstance(), new BrushCheckerTask(lootTable, player, block), 10);
				}
			}
		}

	}
}
