package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AugmentedSpider extends BukkitRunnable {
    private BadlandsCaves plugin;

    public AugmentedSpider(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(7, 7, 7)) {
                if (entity instanceof Spider) {
                    final Spider spider = (Spider) entity;
                    if (spider.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && spider.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1) {
                        if (spider.getTarget() != null && spider.getTarget().equals(player)) {
                            final Random random = new Random();

                            final Location spider_loc = spider.getLocation();
                            final Location player_loc = player.getLocation();

                            if (spider_loc.getBlock().isPassable()) spider_loc.getBlock().setType(Material.COBWEB);
                            if (random.nextInt(100) < 25) {
                                if (spider_loc.distanceSquared(player_loc) < 25 && player_loc.getBlock().isPassable() && !player_loc.getBlock().getType().equals(Material.COBWEB)) {
                                    spider_loc.add(0, 0.5, 0);
                                    ParticleShapes.particleLine(null, Particle.REDSTONE, spider_loc, player.getEyeLocation(), 0, new Particle.DustOptions(Color.fromRGB(200, 200, 200), 0.5F), 0.05);
                                    ParticleShapes.particleSphere(null, Particle.BLOCK_DUST, player_loc, 1, 0, Material.COBWEB.createBlockData());
                                    player_loc.getBlock().setType(Material.COBWEB);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
