package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Freezing extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards hazards;
    private final static String title = "Temperature";

    private final Material[] warmBlocks = {
            Material.FIRE,
            Material.CAMPFIRE,
            Material.LAVA,
            Material.MAGMA_BLOCK,
            Material.TORCH,
    };

    public Freezing(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.hazards = new EnvironmentalHazards(plugin);
    }

    @EventHandler
    public void stopFrozenMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                if ((int) PlayerScore.TEMPERATURE.getScore(plugin, player) <= 0) {
                    if (event.getTo() != null && event.getTo().distanceSquared(event.getFrom()) > 0) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void stopFrozenBreak (BlockBreakEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                if ((int) PlayerScore.TEMPERATURE.getScore(plugin, player) <= 0) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void stopFrozenPlace (BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                if ((int) PlayerScore.TEMPERATURE.getScore(plugin, player) <= 0) {
                    event.setCancelled(true);
                }
            }
        }
    }


    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            int temp = (int) PlayerScore.TEMPERATURE.getScore(plugin, player);
            if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    world.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.5, 0), 50, 5, 5, 5, 0, new Particle.DustOptions(Color.fromRGB(203, 239, 245), 1));


                    KeyedBossBar tempBar = getTemperatureBar(player, true);
                    tempBar.setVisible(true);

                    //calc score
                    if (isWarm(player, 3)) {
                        if (temp < 100) {
                            temp++;
                        }
                    }
                    else {
                        temp--;
                    }

                    //damage
                    if (temp <= 0) {
                        world.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.5, 0), 15, 0.25, 1, 0.25, 0, new Particle.DustOptions(Color.fromRGB(95, 201, 217), 0.5F));

                        if (temp < -20) {
                            player.damage(player.getHealth() / 8.0);
                            temp = 0;
                        }
                    }

                    //update bar and change color, also slowness
                    tempBar.setProgress(Math.max(Math.min(temp / 100.0, 1), 0));
                    if (temp > 80) {
                        tempBar.setColor(BarColor.RED);
                        tempBar.setTitle(ChatColor.RED + title);
                    }
                    else if (temp > 50) {
                        tempBar.setColor(BarColor.YELLOW);
                        tempBar.setTitle(ChatColor.YELLOW + title);
                    }
                    else if (temp > 30) {
                        tempBar.setColor(BarColor.GREEN);
                        tempBar.setTitle(ChatColor.GREEN + title);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 0, false, false));
                    }
                    else if (temp > 0) {
                        tempBar.setColor(BarColor.BLUE);
                        tempBar.setTitle(ChatColor.AQUA + title);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 1, false, false));
                    }
                    else {
                        tempBar.setColor(BarColor.BLUE);
                        tempBar.setTitle(ChatColor.BLUE.toString() + ChatColor.BOLD + "Frozen");
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 99, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 15, 99, false, false));
                    }


                    PlayerScore.TEMPERATURE.setScore(plugin, player, temp);
                }
            }
            else {
                KeyedBossBar tempBar = getTemperatureBar(player, true);
                if (tempBar != null && tempBar.isVisible()) {
                    tempBar.setVisible(false);
                }

                if (temp < 100) PlayerScore.TEMPERATURE.setScore(plugin, player, 100);
            }
        }
    }

    public boolean isWarm (Player player, int radius) {
        if (player.getFireTicks() > 0) return true;

        final Location location = player.getLocation();
        final List<Material> warmBlocks = Arrays.asList(this.warmBlocks);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final Location iter = location.clone().add(x, y, z);
                    if (warmBlocks.contains(iter.getBlock().getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public KeyedBossBar getTemperatureBar(Player player, boolean create) {
        NamespacedKey key = new NamespacedKey(plugin, "temp_" + player.getUniqueId());
        KeyedBossBar bar = plugin.getServer().getBossBar(key);
        if (create && bar == null) {
            bar = plugin.getServer().createBossBar(key, title, BarColor.RED, BarStyle.SEGMENTED_10);
            bar.addPlayer(player);
        }
        return bar;
    }
}
