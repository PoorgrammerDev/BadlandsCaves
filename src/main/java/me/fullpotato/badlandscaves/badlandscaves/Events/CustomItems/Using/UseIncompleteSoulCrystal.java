package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


public class UseIncompleteSoulCrystal implements Listener {
    private BadlandsCaves plugin;
    public UseIncompleteSoulCrystal (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;

        final ItemStack item = event.getItem();
        if (item == null) return;

        final ItemStack soul_crystal_incomplete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));
        if (!item.isSimilar(soul_crystal_incomplete)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();
        if (in_reflection) return;


        final World reflection = Bukkit.getWorld("world_reflection");
        if (reflection == null) return;

        Location worldspawn = reflection.getSpawnLocation();
        worldspawn.setY(255);
        player.teleport(worldspawn);
        player.setMetadata("in_reflection", new FixedMetadataValue(plugin, true));
    }
}
