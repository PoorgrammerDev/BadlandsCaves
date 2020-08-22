package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AugmentedZombie extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Random random;

    public AugmentedZombie(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }


    @Override
    public void run() {
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        for (World world : plugin.getServer().getWorlds()) {
            for (Zombie zombie : world.getEntitiesByClass(Zombie.class)) {
                if (zombie.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && zombie.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1 && !zombie.isDead()) {
                    //WRYY----------------------
                    byte wryyCooldown = zombie.getPersistentDataContainer().get(new NamespacedKey(plugin, "wryy_cooldown"), PersistentDataType.BYTE);
                    if (wryyCooldown <= 0) {
                        if (random.nextInt(100) < 5) {
                            zombie.getWorld().playSound(zombie.getLocation(), "custom.vampire.ambient", SoundCategory.HOSTILE, 0.5F, 1);
                            zombie.getPersistentDataContainer().set(new NamespacedKey(plugin, "wryy_cooldown"), PersistentDataType.BYTE, (byte) 15);
                        }
                    }
                    else {
                        zombie.getPersistentDataContainer().set(new NamespacedKey(plugin, "wryy_cooldown"), PersistentDataType.BYTE, (byte) (wryyCooldown - 1));
                    }

                    //time stopping
                    byte timeStopCooldown = zombie.getPersistentDataContainer().get(new NamespacedKey(plugin, "time_stop_cooldown"), PersistentDataType.BYTE);
                    if (timeStopCooldown <= 0) {
                        if (zombie.getTarget() != null && zombie.getTarget() instanceof Player) {
                            Player player = (Player) zombie.getTarget();
                            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                Location zombieLocation = zombie.getEyeLocation();
                                Location playerLocation = player.getEyeLocation();
                                if (zombieLocation.getWorld() != null && zombieLocation.getWorld().equals(playerLocation.getWorld()) && zombieLocation.distanceSquared(playerLocation) < 25) {
                                    int attackType = getAttackType(zombieLocation.distanceSquared(playerLocation));
                                    if (attackType == 1) {
                                        //DONUT----------------------------------------------
                                        playerLocation.setYaw(zombie.getLocation().getYaw());
                                        playerLocation.setPitch(zombie.getLocation().getPitch());

                                        zombie.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                        world.playSound(player.getLocation(), "custom.vampire.ability", SoundCategory.HOSTILE, 10, 1);

                                        double damage = zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * 4;
                                        player.setVelocity(zombie.getVelocity().subtract(player.getVelocity()).multiply(damage));
                                        player.damage(damage, zombie);
                                        healZombie(zombie, damage / ((chaos / -50.0) + 5));
                                    }
                                    else if (attackType == 2) {
                                        //KNIVES-----------------------------------------------
                                        Location knifeSpawn = zombie.getEyeLocation();
                                        double phi = 0;
                                        int knives = 0;

                                        final int maxTries = 100;
                                        int tries = 0;
                                        while (phi <= Math.PI && tries < maxTries) {
                                            phi += Math.PI / (5);

                                            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / (10)) {
                                                if (knives < Math.max(chaos / 2, 5)) {
                                                    double x = 2 * Math.cos(theta) * Math.sin(phi);
                                                    double y = 2 * Math.cos(phi) + 1.5;
                                                    double z = 2 * Math.sin(theta) * Math.sin(phi);

                                                    knifeSpawn.add(x, y, z);
                                                    knifeSpawn.setDirection(playerLocation.clone().subtract(0, 0.5, 0).subtract(knifeSpawn).toVector());
                                                    ArmorStand knife = (ArmorStand) world.spawnEntity(knifeSpawn, EntityType.ARMOR_STAND);
                                                    knife.setArms(true);
                                                    knife.setSmall(true);
                                                    knife.setVisible(false);
                                                    knife.setMarker(true);
                                                    knife.setInvulnerable(true);
                                                    knife.setGravity(false);
                                                    knife.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

                                                    final int lifespan = 60;
                                                    int[] time = {0};
                                                    new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            if (knife.isDead() || time[0] > lifespan) {
                                                                knife.remove();
                                                                this.cancel();
                                                            } else {
                                                                if (knife.getWorld().equals(player.getWorld()) && knife.getLocation().distanceSquared(player.getLocation()) < 1.5) {
                                                                    double health = player.getHealth();
                                                                    if (health > 0) {
                                                                        double damage = 3;
                                                                        healZombie(zombie, damage / ((chaos / -50.0) + 5));
                                                                        player.damage(damage, zombie);
                                                                        player.setNoDamageTicks(0);
                                                                    }
                                                                    knife.remove();
                                                                    this.cancel();
                                                                } else if (!knife.getLocation().getBlock().isPassable()) {
                                                                    knife.remove();
                                                                    this.cancel();
                                                                }

                                                                Location move = knife.getLocation().clone().add(knife.getLocation().getDirection());
                                                                knife.teleport(move, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                                                time[0]++;
                                                            }
                                                        }
                                                    }.runTaskTimer(plugin, 0, 0);

                                                    knifeSpawn.subtract(x, y, z);
                                                    knives++;
                                                } else break;
                                            }
                                            tries++;
                                        }
                                    }
                                    zombie.getPersistentDataContainer().set(new NamespacedKey(plugin, "time_stop_cooldown"), PersistentDataType.BYTE, (byte) ((-1 * (chaos / 5)) + 30));
                                }
                            }
                        }
                    }
                    else {
                        zombie.getPersistentDataContainer().set(new NamespacedKey(plugin, "time_stop_cooldown"), PersistentDataType.BYTE, (byte) (timeStopCooldown - 1));
                    }
                }

            }
        }
    }

    public int getAttackType (double distance) {
        int attackType;
        //Type 1 = DONUT
        //Type 2 = KNIVES
        if (distance < 4) {
            attackType = 1;
        }
        else if (distance < 9) {
            attackType = random.nextInt(100) < 75 ? 1 : 2;
        }
        else if (distance < 16) {
            return random.nextInt(2) + 1;
        }
        else {
            return 2;
        }
        return attackType;
    }

    public void healZombie (Zombie zombie, double heal) {
        if (zombie.isDead()) return;
        double health = zombie.getHealth();
        health = Math.min(health + heal, 100);
        if (health > zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + heal);
        }
        zombie.setHealth(health);
        zombie.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, zombie.getLocation().add(0, 0.5, 0), Math.max(Math.min((int) heal, 20), 1), 0.5, 0.5, 0.5, 0);
    }
}
