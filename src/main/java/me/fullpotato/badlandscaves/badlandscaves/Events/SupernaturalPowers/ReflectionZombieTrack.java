package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class ReflectionZombieTrack extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Zombie zombie;
    private World world = Bukkit.getWorld("world_reflection");
    public ReflectionZombieTrack (BadlandsCaves bcav) {
        plugin = bcav;
    }

    //TODO read below
    //put the player teleporting and the zombie teleporting stuff into their own seperate methods
    //also have the player, if they have food, it can just randomly eat it
    //also if the player has placeable blocks, it can just randomly place it
    //also the zombie will not only tp behind the player but around the player as well
    //give the zombie a medium ranged attack that'll incentivize the player to run away
    //the zombie, if it gets stuck in a wall, will teleport out of it

    @Override
    public void run () {
        final Random random = new Random();
        ArrayList<Zombie> zombie_list = (ArrayList<Zombie>) (world.getEntitiesByClass(Zombie.class));
        if (zombie_list.isEmpty()) return;

        zombie = zombie_list.get(0);
        if (!zombie.getWorld().equals(world)) return;

        int cooldown = zombie.hasMetadata("teleport_cooldown") ? zombie.getMetadata("teleport_cooldown").get(0).asInt() : 0;
        if (zombie.getTarget() == null || !(zombie.getTarget() instanceof Player) || cooldown > 0) {
            if (cooldown > 0) {
                zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, cooldown - 1));
            }
            return;
        }

        final Player player = (Player) zombie.getTarget();
        final Location player_loc = player.getEyeLocation();
        final Location zombie_orig_loc = zombie.getLocation();

        if (zombie.getLocation().distanceSquared(player_loc) > 25 || random.nextInt(1000) < 1) {
            BlockIterator iterator = new BlockIterator(world, player_loc.toVector(), player_loc.getDirection().multiply(-1), 0, 2);
            Block block = iterator.next();
            while (iterator.hasNext()) {
                block = iterator.next();
                if (!block.isPassable()) {
                    break;
                }
            }

            Location tele = block.getLocation();

            if (tele.distanceSquared(player_loc) > 0.25) {
                Location check_viable = tele.clone();

                check_viable.subtract(0, 1, 0);
                if (!check_viable.getBlock().isPassable()) {
                    zombie.teleport(tele);
                    zombie.setMetadata("teleport_cooldown" , new FixedMetadataValue(plugin, random.nextInt(100)));


                    //player moving
                    BlockIterator player_moving_iterate = new BlockIterator(world, player_loc.toVector(), player_loc.getDirection(), 0, random.nextInt(3) + 2);
                    Block player_moving_iterate_block = player_moving_iterate.next();
                    while (player_moving_iterate.hasNext()) {
                        player_moving_iterate_block = player_moving_iterate.next();
                        if (!player_moving_iterate_block.isPassable()) {
                            break;
                        }
                    }

                    Location player_moving = player_moving_iterate_block.getLocation();
                    player_moving.setYaw(player_loc.getYaw());
                    player_moving.setPitch(player_loc.getPitch());
                    player.teleport(player_moving);

                    if (random.nextBoolean()) {
                        player.getInventory().setHeldItemSlot(random.nextInt(9));
                    }

                    if (random.nextInt(100) < 20) {
                        player.damage(random.nextDouble() * 10, zombie);
                    }

                    //effects
                    player.spawnParticle(Particle.FLASH, zombie_orig_loc, 1);
                    player.spawnParticle(Particle.PORTAL, zombie_orig_loc, 20, 1, 1, 1);
                    player.spawnParticle(Particle.SMOKE_NORMAL, player_loc, 20, 1, 1, 1);
                    player.playSound(player_moving, Sound.BLOCK_BELL_USE, 1, 0.2F);

                }
            }
        }
    }
}
