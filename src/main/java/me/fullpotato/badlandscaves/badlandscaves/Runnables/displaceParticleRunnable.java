package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class displaceParticleRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public displaceParticleRunnable(BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }


    @Override
    public void run() {
        ItemStack item = player.getInventory().getItemInOffHand();
        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        if (!item.isSimilar(displace)) {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
            return;
        }

        int has_displace_marker = player.getMetadata("has_displace_marker").get(0).asInt();
        if (has_displace_marker > 0.5) {
            World world = player.getWorld();
            double marker_x = player.getMetadata("displace_x").get(0).asDouble();
            double marker_y = player.getMetadata("displace_y").get(0).asDouble();
            double marker_z = player.getMetadata("displace_z").get(0).asDouble();
            Location displace_marker = new Location(world, marker_x, marker_y, marker_z);
            if (player.getLocation().distance(displace_marker) < 15) {
                player.spawnParticle(Particle.SPELL_WITCH, displace_marker, 10, 0,1, 0);
            }
        }
        else {
            BlockIterator iter = new BlockIterator(player, 10);
            Block lastBlock = iter.next();

            while (iter.hasNext()) {
                lastBlock = iter.next();
                if (!lastBlock.getType().isSolid()) {
                    continue;
                }
                break;
            }

            Location location = lastBlock.getLocation();
            location.setX(location.getX() + 0.5);
            location.setY(location.getY() + 0.5);
            location.setZ(location.getZ() + 0.5);

            player.spawnParticle(Particle.SPELL_WITCH, location, 5);
        }
    }
}
