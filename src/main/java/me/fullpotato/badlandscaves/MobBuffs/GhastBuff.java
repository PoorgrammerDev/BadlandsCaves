package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GhastBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    public GhastBuff(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void fireballExplode (ProjectileHitEvent event) {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!hardmode) return;

        if (event.getEntity() instanceof Fireball) {
            final Fireball fireball = (Fireball) event.getEntity();
            if (fireball.getShooter() instanceof Ghast) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fireball.isDead()) return;

                        final Ghast ghast = (Ghast) fireball.getShooter();
                        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                        final double chance = Math.pow(1.045, chaos) - 1;
                        Location location = fireball.getLocation();

                        fireball.getWorld().createExplosion(location, 3, true, true, ghast);

                        int[] times = {0};
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (random.nextInt(100) < chance && times[0] < 5) {
                                    fireball.getWorld().createExplosion(location.add(0, 2, 0), 3, true, true, ghast);
                                    times[0]++;
                                }
                                else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 5, 5);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}
