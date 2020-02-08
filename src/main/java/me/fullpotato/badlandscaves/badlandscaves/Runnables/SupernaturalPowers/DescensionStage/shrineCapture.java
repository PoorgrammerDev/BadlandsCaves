package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class shrineCapture extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_descension");
    private Location[] crystal_locations = {
            new Location(world, 46, 80, 46),
            new Location(world, -46, 80, 46),
            new Location(world, 46, 80, -46),
            new Location(world, -46, 80, -46),
    };
    private Collection<Entity> entities = world.getEntities();

    public shrineCapture (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        ArrayList<EnderCrystal> crystals = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof EnderCrystal) {
                crystals.add((EnderCrystal) entity);
            }
        }



        for (Player player : Bukkit.getOnlinePlayers()) {
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2) {
                Location player_loc = player.getLocation();
                for (Location crystal_location : crystal_locations) {
                    crystal_location.setY(world.getHighestBlockYAt(crystal_location.getBlockX(),crystal_location.getBlockZ()));
                    if (player_loc.distanceSquared(crystal_location) < 16) {
                        crystal_location.setY(80);

                        for (EnderCrystal crystal : crystals) {
                            if (crystal.getLocation().distanceSquared(crystal_location) < 16) {
                                if (crystal != null) {
                                    player.sendMessage("BEAM");
                                    crystal.setBeamTarget(player_loc);
                                    crystal.setGlowing(true);
                                    crystal.setMetadata("charge", new FixedMetadataValue(plugin, crystal.getMetadata("charge").get(0).asInt() + 1));
                                    player.sendMessage(crystal.getMetadata("charge").get(0).asInt() + "");
                                    if (crystal.getMetadata("charge").get(0).asInt() >= 400) {
                                        player.sendMessage(ChatColor.RED + "oof");
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
