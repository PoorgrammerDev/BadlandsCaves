package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DescensionFinish extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_descension");
    private Location portal_center = new Location(world, 0, 85, 0);
    private Location[] crystal_locations = {
            new Location(world, 46, 80, 46, 135, 0),
            new Location(world, -46, 80, 46, -135, 0),
            new Location(world, -46, 80, -46, -45, 0),
            new Location(world, 46, 80, -46, 45, 0),
    };

    public DescensionFinish(BadlandsCaves bcav) {
        plugin = bcav;
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
