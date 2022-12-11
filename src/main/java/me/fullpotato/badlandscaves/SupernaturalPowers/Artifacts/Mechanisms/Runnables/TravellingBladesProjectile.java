package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables;

import java.util.Random;
import java.util.function.Consumer;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class TravellingBladesProjectile extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Random random;
    private final LivingEntity user;
    private final int count;
    private final double damage;
    private final Consumer<Integer> finishFunction; 

    private final Location location;
    private final Location front;
    private final World world;
    private int spawned;

    public TravellingBladesProjectile(final BadlandsCaves plugin, final Random random, final LivingEntity user, final int count, final double damage, final Consumer<Integer> finishFunction) {
        this.plugin = plugin;
        this.random = random;
        this.user = user;
        this.count = count;
        this.damage = damage;
        this.finishFunction = finishFunction;

        this.location = user.getLocation();
        this.front = user.getLocation().add(0, 1, 0).add(user.getLocation().getDirection().normalize());
        this.world = user.getWorld();
        this.spawned = 0;
    }

    @Override
    public void run() {
        if (spawned > count) {
            this.cancel();
            return;
        }
        final Location[] scout = {front.clone().add(random.nextDouble() * 2, random.nextDouble() / 5, random.nextDouble() * 2)};
        final int times = 25;
        final int[] ran = {0};
        final double range = 1.5;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ran[0] > times || !scout[0].getBlock().isPassable()) {
                    if (finishFunction != null) finishFunction.accept(0);
                    this.cancel();
                    return;
                }

                scout[0] = scout[0].add(location.getDirection().normalize());
                world.spawnParticle(Particle.SWEEP_ATTACK, scout[0], 1);
                world.playSound(scout[0], Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 2);

                world.getNearbyEntities(scout[0], range, range, range).forEach(target -> {
                    if (target instanceof EnderDragon) {
                        EnderDragon enderDragon = (EnderDragon) target;
                        this.cancel();
                        enderDragon.setHealth(enderDragon.getHealth() - (damage / 5.0));
                        enderDragon.setNoDamageTicks(0);
                        enderDragon.playEffect(EntityEffect.HURT);
                        world.playSound(enderDragon.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, SoundCategory.HOSTILE, 10, 1);
                    }
                    else if (target instanceof LivingEntity) {
                        final LivingEntity livingTarget = (LivingEntity) target;
                        if (!livingTarget.equals(user)) {
                            this.cancel();
                            livingTarget.damage(damage, user);
                            livingTarget.setNoDamageTicks(0);
                        }
                    }
                });
                ran[0]++;
            }
        }.runTaskTimer(plugin, 0, 0);
        spawned++;
    }
    
}
