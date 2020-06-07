package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

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

        HashMap<Material, Material> stones = new HashMap<>();
        stones.put(Material.INFESTED_STONE, Material.STONE);
        stones.put(Material.INFESTED_COBBLESTONE, Material.COBBLESTONE);
        stones.put(Material.INFESTED_STONE_BRICKS, Material.STONE_BRICKS);
        stones.put(Material.INFESTED_CHISELED_STONE_BRICKS, Material.CHISELED_STONE_BRICKS);
        stones.put(Material.INFESTED_CRACKED_STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        stones.put(Material.INFESTED_MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICKS);

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
                        if (stones.containsKey(affected_block.getType())) {
                            affected_block.setType(stones.get(affected_block.getType()));
                            foundblock = true;
                        }

                        affected_block.getState().update(true);
                        if (foundblock) {
                            world.spawnParticle(Particle.SMOKE_NORMAL, affected_block_loc, 1);
                            thrower.playSound(affected_block_loc,Sound.ENTITY_SILVERFISH_DEATH, SoundCategory.HOSTILE, (float) 0.1, (float) Math.random() * 2);
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
                    int duration = isHardmode ? 50 : 9999;
                    int amplifier = isHardmode ? 0 : 4;
                    int count = isHardmode ? 5 : 50;
                    LivingEntity silverfish = (LivingEntity) entity_list[a];
                    silverfish.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, amplifier));
                    world.spawnParticle(Particle.DAMAGE_INDICATOR, entity_list[a].getLocation(), count,0.2,0.5,0.2);

                }
                else if (entity_list[a].getType().equals(EntityType.PLAYER)) {
                    Player player = (Player) entity_list[a];
                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        if (true) { //!player.getUniqueId().equals(thrower.getUniqueId())
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,20, 1));
                            double tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
                            final Random random = new Random();
                            if (!player.hasPotionEffect(PotionEffectType.WATER_BREATHING) && !player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
                                final double tox_incr = random.nextDouble() / 5;
                                final int part_num = random.nextInt(3) + 5;

                                if (tox_incr > 0) {
                                    PlayerScore.TOXICITY.setScore(plugin, player, tox + tox_incr);
                                }
                                world.spawnParticle(Particle.DAMAGE_INDICATOR, entity_list[a].getLocation(), part_num,0.2, 0.5 ,0.2);
                            }
                        }
                    }
                }
                else {
                    if (entity_list[a] instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) entity_list[a];
                        living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,20, 1));
                        world.spawnParticle(Particle.DAMAGE_INDICATOR, living.getLocation(), 5,0.2, 0.5 ,0.2);
                    }
                }
            }
        }

        assert world != null;
        world.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1, (float) 1.2);
        world.spawnParticle(Particle.REDSTONE, location, 50, 0.5 ,0.5 ,0.5, new Particle.DustOptions(Color.fromRGB(0,127,0),3));

        item.remove();

        if (vel_check_ID != 0) {
            plugin.getServer().getScheduler().cancelTask(vel_check_ID);
        }

    }
}
