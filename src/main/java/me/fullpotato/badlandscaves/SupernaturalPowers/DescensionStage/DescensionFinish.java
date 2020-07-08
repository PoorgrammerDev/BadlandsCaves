package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class DescensionFinish extends BukkitRunnable {
    private final World world;
    private final Location[] crystal_locations;

    public DescensionFinish(BadlandsCaves plugin) {
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());

        crystal_locations = new Location[]{
                new Location(world, 46, 80, 46, 135, 0),
                new Location(world, -46, 80, 46, -135, 0),
                new Location(world, -46, 80, -46, -45, 0),
                new Location(world, 46, 80, -46, 45, 0),
        };
    }

    @Override
    public void run() {
        //remove entities
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Item || entity instanceof Zombie || entity instanceof ExperienceOrb) {
                entity.remove();
            }
        }

        //make bridges
        for (Location location : crystal_locations) {
            BlockIterator iterator = new BlockIterator(location, 4, 65);

            Block lastBlock = iterator.next();

            while (iterator.hasNext()) {

                lastBlock = iterator.next();

                if (!lastBlock.getType().isSolid()) {
                    lastBlock.setType(Material.BARRIER);
                }
            }
        }
    }

}
