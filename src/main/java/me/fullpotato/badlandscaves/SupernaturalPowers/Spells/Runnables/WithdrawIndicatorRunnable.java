package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

public class WithdrawIndicatorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World empty;

    public WithdrawIndicatorRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.empty = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
    }

    @Override
    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (has_powers) {
                if (player.getInventory().getItemInOffHand().isSimilar(plugin.getCustomItemManager().getItem(CustomItem.WITHDRAW))) {
                    if (!player.getWorld().equals(empty) && ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0)) {
                        Location location = player.getEyeLocation();
                        final int y = location.getBlockY();
                        if (y < location.getWorld().getMaxHeight()) {
                            final Chunk chunk = location.getChunk();
                            player.spawnParticle(Particle.REDSTONE, chunk.getBlock(0, y, 8).getLocation().add(0.5, 0.5, 0.5), 30, 0, 5, 4, 0, new Particle.DustOptions(Color.GRAY, 1));
                            player.spawnParticle(Particle.REDSTONE, chunk.getBlock(8, y, 0).getLocation().add(0.5, 0.5, 0.5), 30, 4, 5, 0, 0, new Particle.DustOptions(Color.GRAY, 1));
                            player.spawnParticle(Particle.REDSTONE, chunk.getBlock(15, y, 8).getLocation().add(0.5, 0.5, 0.5), 30, 0, 5, 4, 0, new Particle.DustOptions(Color.GRAY, 1));
                            player.spawnParticle(Particle.REDSTONE, chunk.getBlock(8, y, 15).getLocation().add(0.5, 0.5, 0.5), 30, 4, 5, 0, 0, new Particle.DustOptions(Color.GRAY, 1));
                        }
                    }
                }
            }
        });
    }
}
