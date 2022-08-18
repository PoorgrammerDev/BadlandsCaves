package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class ShrineCapture extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;
    private final Location origin;
    private final NamespacedKey chargedKey;
    private final NamespacedKey chargeKey;
    private final Random random;

    public ShrineCapture(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        this.random = random;
        origin = new Location(world, 0, 80, 0);
        chargedKey = new NamespacedKey(plugin, "descension_crystal_charged");
        chargeKey = new NamespacedKey(plugin, "descension_crystal_charge");
    }

    @Override
    public void run() {
        if (world.getEntitiesByClass(Player.class).isEmpty()) {
            this.cancel();
            return;
        }

        world.getEntitiesByClass(EnderCrystal.class).forEach(crystal -> {
            boolean charged = crystal.getPersistentDataContainer().has(chargedKey, PersistentDataType.BYTE) && crystal.getPersistentDataContainer().get(chargedKey, PersistentDataType.BYTE) == (byte) 1;
            if (!charged) {
                final Location crystalLocation = crystal.getLocation();
                final Player player = findPlayer(crystalLocation, 5);
                if (player != null) {
                    final Location playerLocation = player.getLocation();
                    crystal.setBeamTarget(playerLocation.clone().subtract(0, 0.2, 0));

                    final short charge = (short) (crystal.getPersistentDataContainer().getOrDefault(chargeKey, PersistentDataType.SHORT, (short) 0) + 1);
                    crystal.getPersistentDataContainer().set(chargeKey, PersistentDataType.SHORT, charge);

                    if (charge == 0) {
                        player.playSound(playerLocation, Sound.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 1, 0.3f);
                    }

                    //done charging
                    if (charge >= 25) {
                        //crystal
                        crystal.getPersistentDataContainer().set(chargedKey, PersistentDataType.BYTE, (byte) 1);

                        //visual effects
                        world.spawnParticle(Particle.EXPLOSION_HUGE, crystalLocation, 5);
                        player.playSound(playerLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1, 0.3f);
                        crystal.setGlowing(true);
                        crystal.setBeamTarget(origin);

                        //gives more time
                        PlayerScore.DESCENSION_TIMER.setScore(plugin, player, (int) PlayerScore.DESCENSION_TIMER.getScore(plugin, player) + 60);

                        //spawns more mobs
                        int normal_mob_cap = plugin.getOptionsConfig().getInt("descension_mob_limit");
                        DescensionReset descReset = new DescensionReset(plugin, random);
                        Team desc_team = descReset.getDescensionTeam();
                        descReset.spawnMobs(desc_team, normal_mob_cap / 4);

                        //metadata changes
                        final int capped = (int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player) + 1;
                        PlayerScore.DESCENSION_SHRINES_CAPPED.setScore(plugin, player, capped);
                        switch (capped) {
                            case 1:
                                player.sendTitle(ChatColor.LIGHT_PURPLE + "You now have access to Abilities.", net.md_5.bungee.api.ChatColor.of("#7c00a6") + "Double Shift, then hold it down to access them.", 20, 60, 20);
                                player.sendMessage(ChatColor.LIGHT_PURPLE + "You now have access to Abilities.");
                                player.sendMessage(net.md_5.bungee.api.ChatColor.of("#7c00a6") + "Double Shift, then hold it down to access them.");

                                PlayerScore.HAS_SUPERNATURAL_POWERS.setScore(plugin, player, 1);
                                PlayerScore.AGILITY_LEVEL.setScore(plugin, player, 1);
                                PlayerScore.DISPLACE_LEVEL.setScore(plugin, player, 1);
                                break;
                            case 2:
                                PlayerScore.AGILITY_LEVEL.setScore(plugin, player, 2);
                                PlayerScore.POSSESS_LEVEL.setScore(plugin, player, 1);
                                break;
                            case 3:
                                PlayerScore.DISPLACE_LEVEL.setScore(plugin, player, 2);
                                PlayerScore.POSSESS_LEVEL.setScore(plugin, player, 2);
                                break;
                            case 4:
                                //Final capture message
                                player.sendTitle(net.md_5.bungee.api.ChatColor.of("#3b26de") + "You can now walk along the beam.", net.md_5.bungee.api.ChatColor.of("#5600bf") + "Go towards the center to leave.", 20, 60, 20);
                                player.sendMessage(net.md_5.bungee.api.ChatColor.of("#3b26de") + "You can now walk along the beam.");
                                player.sendMessage(net.md_5.bungee.api.ChatColor.of("#5600bf") + "Go towards the center to leave.");

                                //Fully recover mana and build bridges
                                PlayerScore.MANA.setScore(plugin, player, PlayerScore.MAX_MANA.getScore(plugin, player));
                                new DescensionFinish(plugin).runTask(plugin);
                                break;
                        }
                    }
                }
                else {
                    crystal.setBeamTarget(null);
                }
            }
        });
    }

    public @Nullable Player findPlayer (Location location, int range) {
        if (location.getWorld() != null && location.getWorld().equals(world)) {
            final Location clone = location.clone();
            for (int i = 0; i < location.getBlockY(); i++) {
                clone.subtract(0, 1, 0);

                final Collection<Entity> players = world.getNearbyEntities(clone, range, range, range, entity -> (entity instanceof Player));
                if (!players.isEmpty()) {
                    Player output = null;
                    double minDist = Double.MAX_VALUE;

                    for (Entity entity : players) {
                        if (entity instanceof Player) {
                            final Player player = (Player) entity;
                            if ((player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL)) &&
                                    ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player) == 2)) {
                                final double dist = player.getLocation().distanceSquared(clone);
                                if (dist < minDist) {
                                    output = player;
                                    minDist = dist;
                                }
                            }
                        }
                    }
                    return output;
                }

                if (!clone.getBlock().isPassable()) {
                    break;
                }
            }
        }

        return null;
    }
}
