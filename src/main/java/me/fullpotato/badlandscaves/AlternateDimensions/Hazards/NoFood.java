package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class NoFood implements Listener {
    private final EnvironmentalHazards environmentalHazards;

    public NoFood(EnvironmentalHazards environmentalHazards) {
        this.environmentalHazards = environmentalHazards;
    }

    @EventHandler
    public void eat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (environmentalHazards.isDimension(world) && environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.NO_FOOD)) {
            ItemStack item = event.getItem();
            if (!item.getType().equals(Material.POTION)) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 1);
                event.setCancelled(true);
            }
        }
    }
}
