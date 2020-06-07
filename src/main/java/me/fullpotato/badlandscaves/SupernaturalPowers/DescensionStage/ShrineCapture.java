package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
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
    private World world;
    private Location[] crystal_locations = {
            new Location(world, 46, 80, 46),
            new Location(world, -46, 80, 46),
            new Location(world, -46, 80, -46),
            new Location(world, 46, 80, -46),
    };
    private Location origin = new Location(world, 0, 80, 0);

    public ShrineCapture(BadlandsCaves bcav) {
        plugin = bcav;
        world = plugin.getServer().getWorld(plugin.descensionWorldName);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isDead() || (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE))) continue;
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
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
                                    int capped = (PlayerScore.DESCENSION_SHRINES_CAPPED.hasScore(plugin, player)) ? ((int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player)) : 0;

                                    if (capped >= a) {
                                        Location crystal_location = crystal.getLocation();

                                        player_loc.subtract(0, 0.2, 0);
                                        crystal.setBeamTarget(player_loc);

                                        int charge = crystal.hasMetadata("charge") ? crystal.getMetadata("charge").get(0).asInt() + 1 : 1;
                                        crystal.setMetadata("charge", new FixedMetadataValue(plugin, charge));

                                        if (crystal.hasMetadata("charge") && crystal.getMetadata("charge").get(0).asInt() % 50 == 1) {
                                            player.playSound(player_loc, Sound.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 1, 0.3f);
                                        }

                                        //done charging

                                        if (crystal.getMetadata("charge").get(0).asInt() >= 400) {
                                            //crystal
                                            crystal.setMetadata("charged", new FixedMetadataValue(plugin, true));

                                            //visual effects
                                            world.spawnParticle(Particle.EXPLOSION_HUGE, crystal_location, 5);
                                            player.playSound(player_loc, Sound.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1, 0.3f);
                                            crystal.setGlowing(true);
                                            crystal.setBeamTarget(origin);

                                            //gives more time
                                            PlayerScore.DESCENSION_TIMER.setScore(plugin, player, (int) PlayerScore.DESCENSION_TIMER.getScore(plugin, player) + 60);

                                            //spawns more mobs
                                            int normal_mob_cap = plugin.getConfig().getInt("game_values.descension_mob_limit");
                                            DescensionReset descReset = new DescensionReset(plugin);
                                            Team desc_team = descReset.getDescensionTeam();
                                            descReset.spawnMobs(desc_team, normal_mob_cap / 4);

                                            //metadata changes
                                            PlayerScore.DESCENSION_SHRINES_CAPPED.setScore(plugin, player, a + 1);
                                            switch (a) {
                                                case 0:
                                                    PlayerScore.HAS_SUPERNATURAL_POWERS.setScore(plugin, player, 1);
                                                    PlayerScore.AGILITY_LEVEL.setScore(plugin, player, 1);
                                                    PlayerScore.DISPLACE_LEVEL.setScore(plugin, player, 1);
                                                    break;
                                                case 1:
                                                    PlayerScore.AGILITY_LEVEL.setScore(plugin, player, 2);
                                                    PlayerScore.POSSESS_LEVEL.setScore(plugin, player, 1);
                                                    break;
                                                case 2:
                                                    PlayerScore.DISPLACE_LEVEL.setScore(plugin, player, 2);
                                                    PlayerScore.POSSESS_LEVEL.setScore(plugin, player, 2);
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
