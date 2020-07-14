package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.WorldGeneration.PreventDragon;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class MakeDescensionStage extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    public MakeDescensionStage(BadlandsCaves bcav, World wrld) {
        plugin = bcav;
        world = wrld;
    }

    @Override
    public void run() {
        int clear_lim = 100;
        //clears the area first
        for (int x = -clear_lim; x <= clear_lim; x++) {
            for (int z = -clear_lim; z <= clear_lim; z++) {
                for (int y = 0; y <= 256; y++) {
                    Location clear = new Location(world, x, y, z);
                    clear.getBlock().setType(Material.AIR);
                }
            }
        }

        //barrier cage
        Location cage_origin = new Location(world, 0, 200, 0);
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = 180; y <= 220; y++) {
                    Location test = new Location(world, x, y, z);
                    if (cage_origin.distance(test) > 3 && cage_origin.distance(test) < 5) {
                        test.getBlock().setType(Material.BARRIER);
                    }
                }
            }
        }

        Random random = new Random();
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(random, 8);
        generator.setScale(0.1D);
        Location origin = new Location(world, 0, 64, 0);

        int currentHeight;
        int inner_lim = 10;
        int upper_lim = 80;
        int lower_lim = 50;

        //generate islands
        for (int x = -upper_lim; x <= upper_lim; x++) {
            for (int z = -upper_lim; z <= upper_lim; z++) {
                Location test = new Location(world, x, 64, z);
                if (test.distance(origin) < inner_lim || (test.distance(origin) > lower_lim && test.distance(origin) < upper_lim)) {
                    currentHeight = (int) ((generator.noise(x, z, 0.5D, 0.05D, true) + 1) *5D + 60D);
                    int thickness =  random.nextInt(5) + 5;
                    for (int y = currentHeight; y >= currentHeight - thickness; y--) {
                        new Location(world, x, y, z).getBlock().setType(getVoidMat(random));
                    }
                }
            }
        }
        genDefaultShrines();

        PreventDragon.preventDragonSpawn(world);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }

    public void genDefaultShrines() {
        generateShrine(world, 46, 46);
        generateShrine(world, 46, -46);
        generateShrine(world, -46, 46);
        generateShrine(world, -46, -46);
    }

    public void generateShrine (World world, int x, int z) {
        int origin_y = 80;
        Location top_origin = new Location(world, x, origin_y, z);

        for (int x_offset = -5; x_offset <= 5; x_offset++) {
            for (int z_offset = -5; z_offset <= 5; z_offset++) {
                int new_x = x + x_offset;
                int new_z = z + z_offset;
                Location test = new Location(world, new_x, origin_y, new_z);
                if (top_origin.distance(test) == 5) {
                    for (int y = origin_y; world.getBlockAt(new_x, y, new_z).getType().isAir(); y--) {
                        world.getBlockAt(new_x, y, new_z).setType(Material.BLACK_GLAZED_TERRACOTTA);
                    }
                }
            }
        }

        top_origin.add(0.5, 3.5, 0.5);
        EnderCrystal crystal = (EnderCrystal) world.spawnEntity(top_origin, EntityType.ENDER_CRYSTAL);
        crystal.setShowingBottom(false);
        crystal.setInvulnerable(true);
        crystal.getPersistentDataContainer().set(new NamespacedKey(plugin, "descension_crystal_charge"), PersistentDataType.SHORT, (short) 0);
    }

    public static Material getVoidMat(Random random) {
        int rand = random.nextInt(3);
        if (rand == 0) return Material.COAL_BLOCK;
        else if (rand == 1) return Material.BLACK_CONCRETE;
        else return Material.BLACK_WOOL;
    }

}