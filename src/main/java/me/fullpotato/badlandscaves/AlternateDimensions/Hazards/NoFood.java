package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class NoFood implements Listener {
    private final EnvironmentalHazards dims;

    public NoFood(BadlandsCaves plugin) {
        this.dims = new EnvironmentalHazards(plugin);
    }

    @EventHandler
    public void eat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (dims.isDimension(world) && dims.hasHazard(world, EnvironmentalHazards.Hazard.NO_FOOD)) {
            ItemStack item = event.getItem();
            if (!item.getType().equals(Material.POTION)) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 1);
                event.setCancelled(true);
            }
        }
    }
}
