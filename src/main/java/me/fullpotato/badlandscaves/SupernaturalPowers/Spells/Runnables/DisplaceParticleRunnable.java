package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.LineOfSight;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.PositionManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.Random;

public class DisplaceParticleRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public DisplaceParticleRunnable(BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }


    @Override
    public void run() {
        ItemStack item = player.getInventory().getItemInOffHand();
        ItemStack displace = CustomItem.DISPLACE.getItem();
        if (!item.isSimilar(displace)) {
            plugin.getServer().getScheduler().cancelTask(this.getTaskId());
            return;
        }

        int displace_level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
        int place_range, warp_range;

        if (displace_level == 1) {
            place_range = 10;
            warp_range = 15;
        }
        else {
            place_range = 20;
            warp_range = 30;
        }

        boolean has_displace_marker = ((byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 1);
        if (has_displace_marker) {
            //yes marker
            World world = player.getWorld();
            final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            final int mana_cost = plugin.getConfig().getInt("game_values.displace_mana_cost");
            double marker_x = (double) PlayerScore.DISPLACE_X.getScore(plugin, player);
            double marker_y = (double) PlayerScore.DISPLACE_Y.getScore(plugin, player);
            double marker_z = (double) PlayerScore.DISPLACE_Z.getScore(plugin, player);
            Location displace_marker = new Location(world, marker_x, marker_y, marker_z);
            boolean active = false;
            if (player.getLocation().distance(displace_marker) < warp_range && mana >= mana_cost) {
                if (LineOfSight.hasLineOfSight(player, displace_marker)) {
                    active = true;
                    trackerParticle(displace_marker);
                }
            }

            markerParticle(displace_marker, active);
        }
        else {
            //no marker
            BlockIterator iter = new BlockIterator(player, place_range);
            Block lastLastBlock = null;
            Block lastBlock = iter.next();

            while (iter.hasNext()) {
                lastLastBlock = lastBlock;
                lastBlock = iter.next();
                if (lastBlock.getType().isSolid() || !LineOfSight.hasLineOfSight(player, lastBlock.getLocation()) || !lastBlock.getLocation().getWorld().getWorldBorder().isInside(lastBlock.getLocation())) {
                    break;
                }
            }

            assert lastLastBlock != null;
            Location location = lastLastBlock.getLocation();
            if (LineOfSight.hasLineOfSight(player, location)) {
                location.setX(location.getX() + 0.5);
                //location.setY(location.getY() + 0.5);
                location.setZ(location.getZ() + 0.5);

                scoutingParticle(location);
            }
        }
    }

    public void scoutingParticle (Location location) {
        Random random = new Random();
        location.add(0, location.getBlock().isPassable() ? 0.5 : 1.1, 0);
        if (random.nextBoolean()) player.spawnParticle(Particle.SPELL_WITCH, location, 1, 0.25, 0.25, 0.25, 1);
        player.spawnParticle(Particle.BLOCK_DUST, location, 5, 0.25, 0.25, 0.25, 0, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
    }

    public void markerParticle (Location location, boolean active) {
        final double radius = 1;

        if (active) {
            ParticleShapes.particleCircle(player, Particle.SPELL_WITCH, location, radius, 0, null);
        }

        player.spawnParticle(Particle.BLOCK_DUST, location, 10, 0.05, 0.5, 0.05, 0, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
    }

    public void trackerParticle (Location target) {
        Location player_loc = player.getLocation();
        player_loc.add(0,  1, 0);
        PositionManager position = new PositionManager();
        final Location origin = player.getMainHand().equals(MainHand.RIGHT) ? position.getLeftSide(player_loc, 1) : position.getRightSide(player_loc, 1);
        ParticleShapes.particleLine(player, Particle.REDSTONE, origin, target, 0, new Particle.DustOptions(Color.fromRGB(255, 0, 255), 0.5F), 1);
    }
}
