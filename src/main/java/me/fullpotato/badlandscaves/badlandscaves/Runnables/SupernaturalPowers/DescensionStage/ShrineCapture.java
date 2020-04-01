package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class ShrineCapture extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_descension");
    private Location[] crystal_locations = {
            new Location(world, 46, 80, 46),
            new Location(world, -46, 80, 46),
            new Location(world, -46, 80, -46),
            new Location(world, 46, 80, -46),
    };
    private Location origin = new Location(world, 0, 80, 0);

    public ShrineCapture(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead() || (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE))) continue;
            int in_descension = player.getMetadata("in_descension").get(0).asInt();
            if (in_descension == 2 && player.getWorld().equals(world)) {
                Location player_loc = player.getLocation();
                for (int a = 0; a < crystal_locations.length; a++) {
                    crystal_locations[a].setY(world.getHighestBlockYAt(crystal_locations[a].getBlockX(),crystal_locations[a].getBlockZ()));
                    if (player_loc.distanceSquared(crystal_locations[a]) < 16) {
                        crystal_locations[a].setY(80);
                        ArrayList<EnderCrystal> crystals = new ArrayList<>();
                        for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                            if (entity instanceof EnderCrystal) crystals.add((EnderCrystal) entity);
                        }

                        for (EnderCrystal crystal : crystals) {
                            if (crystal.getLocation().distanceSquared(crystal_locations[a]) < 16) {
                                boolean charged = crystal.hasMetadata("charged") && crystal.getMetadata("charged").get(0).asBoolean();
                                if (!charged) {
                                    int capped = player.hasMetadata("descension_shrines_capped") ? player.getMetadata("descension_shrines_capped").get(0).asInt() : 0;

                                    if (capped >= a) {
                                        Location crystal_location = crystal.getLocation();

                                        player_loc.subtract(0, 0.2, 0);
                                        crystal.setBeamTarget(player_loc);

                                        int charge = crystal.hasMetadata("charge") ? crystal.getMetadata("charge").get(0).asInt() + 1 : 1;
                                        crystal.setMetadata("charge", new FixedMetadataValue(plugin, charge));
                                        //player.sendMessage(crystal.getMetadata("charge").get(0).asInt() + "");

                                        if (crystal.hasMetadata("charge") && crystal.getMetadata("charge").get(0).asInt() % 50 == 1) {
                                            player.playSound(player_loc, Sound.BLOCK_PORTAL_AMBIENT, 1, 0.3f);
                                        }

                                        //done charging

                                        if (crystal.getMetadata("charge").get(0).asInt() >= 400) {
                                            //crystal
                                            crystal.setMetadata("charged", new FixedMetadataValue(plugin, true));

                                            //visual effects
                                            world.spawnParticle(Particle.EXPLOSION_HUGE, crystal_location, 5);
                                            player.playSound(player_loc, Sound.ENTITY_WITHER_SPAWN, 1, 0.3f);
                                            crystal.setGlowing(true);
                                            crystal.setBeamTarget(origin);

                                            //gives more time
                                            player.setMetadata("descension_timer", new FixedMetadataValue(plugin, player.getMetadata("descension_timer").get(0).asInt() + 60));

                                            //spawns more mobs
                                            int normal_mob_cap = plugin.getConfig().getInt("game_values.descension_mob_limit");
                                            DescensionReset descReset = new DescensionReset(plugin);
                                            Team desc_team = descReset.getDescensionTeam();
                                            descReset.spawnMobs(desc_team, normal_mob_cap / 4);

                                            //metadata changes
                                            player.setMetadata("descension_shrines_capped", new FixedMetadataValue(plugin, a + 1));
                                            switch (a) {
                                                case 0:
                                                    player.setMetadata("has_supernatural_powers", new FixedMetadataValue(plugin, true));
                                                    player.setMetadata("agility_level", new FixedMetadataValue(plugin, 1));
                                                    player.setMetadata("displace_level", new FixedMetadataValue(plugin, 1));
                                                    break;
                                                case 1:
                                                    player.setMetadata("agility_level", new FixedMetadataValue(plugin, 2));
                                                    player.setMetadata("possess_level", new FixedMetadataValue(plugin, 1));
                                                    break;
                                                case 2:
                                                    player.setMetadata("displace_level", new FixedMetadataValue(plugin, 2));
                                                    player.setMetadata("possess_level", new FixedMetadataValue(plugin, 2));
                                                    break;
                                                case 3:
                                                    new DescensionFinish(plugin).runTask(plugin);
                                                    break;
                                            }
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
}
