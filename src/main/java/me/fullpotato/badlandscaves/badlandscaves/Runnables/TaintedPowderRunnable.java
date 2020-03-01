package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TaintedPowderRunnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Item item;
    private Player thrower;
    private int vel_check_ID;
    public TaintedPowderRunnable(BadlandsCaves bcav, Item itm, Player ply, int id) {
        plugin = bcav;
        item = itm;
        thrower = ply;
        vel_check_ID = id;
    }

    @Override
    public void run() {
        Location location = item.getLocation();
        World world = location.getWorld();
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();



        for (int x = X - 3; x < X + 3; x++) {
            for (int y = Y - 3; y < Y + 3; y++) {
                for (int z = Z - 3; z < Z + 3; z++) {
                    Location affected_block_loc = new Location(world, x, y, z);
                    Block affected_block = affected_block_loc.getBlock();
                    boolean foundblock = false;

                    if (affected_block.getType() != null) {
                        if (affected_block.getType().equals(Material.INFESTED_STONE)) {
                            affected_block.setType(Material.STONE);
                            foundblock = true;
                        }
                        else if (affected_block.getType().equals(Material.INFESTED_COBBLESTONE)) {
                            affected_block.setType(Material.COBBLESTONE);
                            foundblock = true;
                        }
                        else if (affected_block.getType().equals(Material.INFESTED_STONE_BRICKS)) {
                            affected_block.setType(Material.STONE_BRICKS);
                            foundblock = true;
                        }
                        else if (affected_block.getType().equals(Material.INFESTED_CHISELED_STONE_BRICKS)) {
                            affected_block.setType(Material.CHISELED_STONE_BRICKS);
                            foundblock = true;
                        }
                        else if (affected_block.getType().equals(Material.INFESTED_CRACKED_STONE_BRICKS)) {
                            affected_block.setType(Material.CRACKED_STONE_BRICKS);
                            foundblock = true;
                        }
                        else if (affected_block.getType().equals(Material.INFESTED_MOSSY_STONE_BRICKS)) {
                            affected_block.setType(Material.MOSSY_STONE_BRICKS);
                            foundblock = true;
                        }
                        affected_block.getState().update(true);

                        if (foundblock) {
                            world.spawnParticle(Particle.SMOKE_NORMAL, affected_block_loc, 1);
                            thrower.playSound(affected_block_loc,Sound.ENTITY_SILVERFISH_DEATH, (float) 0.1, (float) Math.random() * 2);
                        }
                    }
                }
            }
        }

        Entity[] entity_list = location.getChunk().getEntities();
        for (int a = 0; a < entity_list.length; a++) {
            if (location.distance(entity_list[a].getLocation()) <= 3) {
                boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
                if (entity_list[a].getType().equals(EntityType.SILVERFISH)) {
                    int duration, amplifier, count;
                    if (isHardmode) {
                        duration = 50;
                        amplifier = 0;
                        count = 5;
                    }
                    else {
                        duration = 9999;
                        amplifier = 4;
                        count = 50;
                    }

                    LivingEntity silverfish = (LivingEntity) entity_list[a];
                    silverfish.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, amplifier));
                    world.spawnParticle(Particle.DAMAGE_INDICATOR, entity_list[a].getLocation(), count,0.2,0.5,0.2);

                }
                else if (entity_list[a].getType().equals(EntityType.PLAYER)) {
                    Player player = (Player) entity_list[a];
                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        if (!player.getUniqueId().equals(thrower.getUniqueId())) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,100, 1));
                            double tox = player.getMetadata("Toxicity").get(0).asDouble();
                            int tox_incr, part_num;
                            if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
                                tox_incr = 1;
                                part_num = 10;
                            }
                            else {
                                tox_incr = 10;
                                part_num = 20;
                            }
                            player.setMetadata("Toxicity", new FixedMetadataValue(plugin, tox + tox_incr));
                            world.spawnParticle(Particle.DAMAGE_INDICATOR, entity_list[a].getLocation(), part_num,0.2, 0.5 ,0.2);
                        }
                    }
                }
                else {
                    try {
                        LivingEntity living = (LivingEntity) entity_list[a];
                        living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,100, 1));
                        world.spawnParticle(Particle.DAMAGE_INDICATOR, living.getLocation(), 5,0.2, 0.5 ,0.2);
                    }
                    catch (ClassCastException ignore) {
                    }
                }
            }
        }

        assert world != null;
        world.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 1, (float) 1.2);
        world.spawnParticle(Particle.REDSTONE, location, 50, 0.5 ,0.5 ,0.5, new Particle.DustOptions(Color.fromRGB(0,127,0),3));

        item.remove();

        if (vel_check_ID != 0) {
            Bukkit.getScheduler().cancelTask(vel_check_ID);
        }

    }
}
