package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WithdrawIndicatorRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private final World empty = Bukkit.getWorld("world_empty");

    public WithdrawIndicatorRunnable(BadlandsCaves plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        final ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        if (!player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
            return;
        }
        if (player.getWorld().equals(empty)) return;


        Location location = player.getEyeLocation();
        if (location.getBlockY() < location.getWorld().getMaxHeight()) {
            Chunk chunk = location.getChunk();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (x == 0 || z == 0 || x == 15 || z == 15) {
                        Location loc = chunk.getBlock(x, location.getBlockY(), z).getLocation().add(0.5, 0.5, 0.5);
                        player.spawnParticle(Particle.REDSTONE, loc, 1, 0, 5, 0, 0, new Particle.DustOptions(Color.GRAY, 1));
                    }
                }
            }
        }
    }
}
