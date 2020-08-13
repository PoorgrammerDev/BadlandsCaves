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
            }
        });
    }
}
