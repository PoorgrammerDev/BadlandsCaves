package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MeteorShowerRunnable extends BukkitRunnable{
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards dims;
    private final Random random;
    private final Freezing freezingManager;
    private final boolean destroyBlocks;
    private final BlockData iceBlockData;

    public MeteorShowerRunnable(BadlandsCaves plugin, Random random, Freezing freezingManager) {
        this.plugin = plugin;
        this.dims = new EnvironmentalHazards(plugin, random);
        this.random = random;
        this.freezingManager = freezingManager;
        this.destroyBlocks = plugin.getOptionsConfig().getBoolean("alternate_dimensions.hazards.meteors_destroy_blocks");
        this.iceBlockData = Material.ICE.createBlockData();
    }


    @Override
    public void run() {
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final World world = player.getWorld();
            //Must be alt. dimension, player is in survival/adv, player is exposed to the sky
            if (!dims.isDimension(world) || !dims.hasHazard(world, EnvironmentalHazards.Hazard.METEOR_SHOWERS)) continue;
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;
            if (player.getLocation().getBlock().getLightFromSky() == 0) continue;

            for (int i = 0; i < (chaos / 20) + 5; i++) {
                if (random.nextInt(100) >= Math.max(chaos / 1.25, 25)) continue;

                //Find nearby location
                Location nearby = player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
                if (nearby == null) continue;

                //Send location into the sky
                nearby.add(0, 50, 0);

                //Point direction at the player or at the ground
                if (random.nextInt(100) < Math.max(chaos / 2, 10) && random.nextInt(255) < player.getLocation().getY()) {
                    nearby.setDirection(player.getLocation().subtract(nearby.clone()).toVector().multiply(5));
                }
                else {
                    nearby.setDirection(new Vector(random.nextDouble() / 2.0, -1, random.nextDouble() / 2.0).normalize().multiply(5));
                }

                spawnMeteor(nearby, (dims.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)));
            }
        }
    }

    public void spawnMeteor(Location location, boolean ice) {
        Location clone = location.clone();
        Vector dir = clone.getDirection();
        World world = clone.getWorld();
        if (world != null) {
            final int limit = 1000;
            int[] times = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!clone.getBlock().isPassable() || times[0] > limit) {
                        world.createExplosion(clone, 5, !ice, destroyBlocks);

                        //ICE METEOR BEHAVIOUR
                        if (ice) {
                            for (int x = -5; x <= 5; x++) {
                                for (int y = -5; y <= 5; y++) {
                                    for (int z = -5; z <= 5; z++) {
                                        final Block block = clone.clone().add(x, y, z).getBlock();
                                        if (block.getLocation().distanceSquared(clone) < 25) {
                                            final Material type = block.getType();
                                            if (type == Material.LAVA) {
                                                block.setType(Material.COBBLESTONE);
                                            }
                                            else if (freezingManager.isWarmingMaterial(type)) {
                                                block.breakNaturally();
                                            }
                                            else if (type.isSolid() && type.getBlastResistance() < 100) {
                                                block.setType(random.nextBoolean() ? Material.ICE : Material.PACKED_ICE);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        this.cancel();
                    }
                    else {
                        if (ice) {
                            world.spawnParticle(Particle.REDSTONE, clone, 10, 0.25, 0.25, 0.25, 0, new Particle.DustOptions(Color.fromRGB(5, 221, 245), 1));
                            world.spawnParticle(Particle.BLOCK_DUST, clone, 2, 0, 0, 0, 0, iceBlockData);
                        }
                        else {
                            world.spawnParticle(Particle.LAVA, clone, 2, 0, 0, 0, 0);
                        }

                        clone.add(dir);
                        times[0]++;
                    }
                }
            }.runTaskTimer(plugin, 0, 0);
        }
    }
}
