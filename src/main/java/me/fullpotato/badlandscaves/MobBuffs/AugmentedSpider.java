package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AugmentedSpider extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Random random;
    private final ParticleShapes particleShapes;

    public AugmentedSpider(BadlandsCaves plugin, Random random, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.random = random;
        this.particleShapes = particleShapes;
    }

    @Override
    public void run() {
        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!isHardmode) return;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(7, 7, 7)) {
                if (entity instanceof Spider) {
                    final Spider spider = (Spider) entity;
                    if (spider.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && spider.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1) {
                        if (spider.getTarget() != null && spider.getTarget().equals(player)) {

                            final Location spider_loc = spider.getLocation();
                            final Location player_loc = player.getLocation();

                            if (spider_loc.getBlock().isPassable()) spider_loc.getBlock().setType(Material.COBWEB);
                            if (random.nextInt(100) < 25) {
                                if (spider_loc.distanceSquared(player_loc) < 25 && player_loc.getBlock().isPassable() && !player_loc.getBlock().getType().equals(Material.COBWEB)) {
                                    spider_loc.add(0, 0.5, 0);
                                    particleShapes.line(null, Particle.REDSTONE, spider_loc, player.getEyeLocation(), 0, new Particle.DustOptions(Color.fromRGB(200, 200, 200), 0.5F), 0.05);
                                    particleShapes.sphere(null, Particle.BLOCK_DUST, player_loc, 1, 0, Material.COBWEB.createBlockData());
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
