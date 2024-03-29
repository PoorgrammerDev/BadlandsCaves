package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.NameTagHide;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class CreeperBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    private final ParticleShapes particleShapes;
    private final World voidWorld;

    public CreeperBuff(BadlandsCaves bcav, Random random, ParticleShapes particleShapes) {
        plugin = bcav;
        this.random = random;
        this.particleShapes = particleShapes;
        this.voidWorld = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
    }

    @EventHandler
    public void HMcreeper (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        final Creeper creeper = (Creeper) event.getEntity();

        //Exception for entering Withdraw
        if (creeper.getWorld().equals(voidWorld)) return;

        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        creeper.setMaxFuseTicks((int) (30.0 - ((isHardmode ? 0.1 : 0.05) * chaos)));

        if (!isHardmode) return;

        final int radius = plugin.getOptionsConfig().getInt("hardmode_values.creeper_radius");
        final int ascend = (chaos / 5) + plugin.getOptionsConfig().getInt("hardmode_values.ascended_spawn_chance");
        creeper.setExplosionRadius(radius);
        creeper.setSilent(true);

        //Ascended Monster
        if (random.nextInt(100) < ascend) {
            creeper.getPersistentDataContainer().set(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE, (byte) 1);
            creeper.setCustomName(ChatColor.RED.toString() + ChatColor.BOLD + "The Fustercluck");
            creeper.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(11.0);
            creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            creeper.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);
            return;
        }

        //Change behaviour if the monster is spawned in The Void
        if (event.getLocation().getBlock().getBiome() == Biome.GRAVELLY_MOUNTAINS && event.getLocation().getWorld().getEnvironment() == Environment.NORMAL) {
            creeper.setCustomName("Void Creeper");
            creeper.getPersistentDataContainer().set(new NamespacedKey(plugin, "voidMonster"), PersistentDataType.BYTE, (byte) 1);

            creeper.setExplosionRadius((int) (radius * 2 * Math.max(chaos / 32.0, 1)));
            creeper.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8.0);
            NameTagHide.getInstance().Hide(creeper);
        }

    }

    @EventHandler
    public void ascendedExplode (EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
            if (hardmode) {
                final Creeper creeper = (Creeper) event.getEntity();
                if (!creeper.isDead()) {
                    if (creeper.getPersistentDataContainer().has(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) && creeper.getPersistentDataContainer().get(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) == (byte) 1) {
                        final Location location = creeper.getLocation();
                        final World world = creeper.getWorld();
                        final int size = creeper.getExplosionRadius();
                        final int chaos = plugin.getSystemConfig().getInt("chaos_level");

                        final int repeats = random.nextInt(Math.max((chaos / 10), 2)) + 5;
                        int[] repeated = {0};
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (repeated[0] >= repeats) {
                                    this.cancel();
                                }
                                else {
                                    double x = location.getX() + random.nextInt(10) - 5;
                                    double y = location.getY() + random.nextInt(10) - 5;
                                    double z = location.getZ() + random.nextInt(10) - 5;
                                    Location explode = new Location(world, x, y, z);
                                    world.createExplosion(explode, size, false, true, creeper);
                                    repeated[0]++;
                                }
                            }
                        }.runTaskTimer(plugin, 5, 5);
                    }
                }
            }
        }
    }

    @EventHandler
    public void intraCreeperBehavior (EntityDamageByEntityEvent event) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (hardmode) {
            if (event.getEntity() instanceof Creeper) {
                if (event.getDamager() instanceof Creeper) {
                    //immune to each other's explosions
                    event.setCancelled(true);

                    //ascended chain explode
                    final Creeper source = (Creeper) event.getDamager();
                    final Creeper surrounding = (Creeper) event.getEntity();
                    if (source.getPersistentDataContainer().has(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) && source.getPersistentDataContainer().get(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) == (byte) 1) {
                        if (random.nextBoolean()) {
                            Location top = source.getLocation().clone();
                            top.add(0, 5, 0);
                            particleShapes.line(null, Particle.REDSTONE, source.getLocation(), top, 0, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1), 1);
                            particleShapes.line(null, Particle.REDSTONE, top, surrounding.getLocation(), 0, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1), 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    surrounding.ignite();
                                }
                            }.runTaskLater(plugin, random.nextInt(20) + 10);
                        }
                    }
                }
                //ascended deflect arrow
                else if (event.getDamager() instanceof Arrow) {
                    final Arrow arrow = (Arrow) event.getDamager();
                    final Creeper creeper = (Creeper) event.getEntity();
                    if (creeper.getPersistentDataContainer().has(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) && creeper.getPersistentDataContainer().get(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) == (byte) 1) {
                        event.setCancelled(true);
                        creeper.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, arrow.getLocation(), 1, 0, 0, 0, 0);
                        arrow.setVelocity(arrow.getVelocity().multiply(2));
                    }
                }

            }
        }
    }
}
