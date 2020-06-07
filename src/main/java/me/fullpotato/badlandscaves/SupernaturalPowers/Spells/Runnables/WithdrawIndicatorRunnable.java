package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WithdrawIndicatorRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private final World empty;

    public WithdrawIndicatorRunnable(BadlandsCaves plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.empty = plugin.getServer().getWorld(plugin.withdrawWorldName);
    }

    @Override
    public void run() {
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        final ItemStack withdraw = CustomItem.WITHDRAW.getItem();
        if (!player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            plugin.getServer().getScheduler().cancelTask(this.getTaskId());
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
