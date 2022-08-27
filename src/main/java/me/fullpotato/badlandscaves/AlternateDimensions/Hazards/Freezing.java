package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Freezing extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards environmentalHazards;
    private final static String title = "Temperature";
    private final HashSet<Material> warmBlocks;
    private final HashMap<UUID, Block> cachedBlocks;
    private final int range;

    public Freezing(BadlandsCaves plugin, EnvironmentalHazards environmentalHazards) {
        this.plugin = plugin;
        this.environmentalHazards = environmentalHazards;
        this.cachedBlocks = new HashMap<>();
        this.range = plugin.getOptionsConfig().getInt("alternate_dimensions.hazards.freezing_warm_block_range");

        this.warmBlocks = new HashSet<>();
        this.warmBlocks.add(Material.FIRE);
        this.warmBlocks.add(Material.CAMPFIRE);
        this.warmBlocks.add(Material.LAVA);
        this.warmBlocks.add(Material.MAGMA_BLOCK);
        this.warmBlocks.add(Material.TORCH);
        this.warmBlocks.add(Material.WALL_TORCH);
        this.warmBlocks.add(Material.LANTERN);
        this.warmBlocks.add(Material.JACK_O_LANTERN);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            double temp = (double) PlayerScore.TEMPERATURE.getScore(plugin, player);
            Bukkit.broadcastMessage("" + temp);
            if (environmentalHazards.isDimension(world) && environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.FREEZING)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    world.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.5, 0), 50, 5, 5, 5, 0, new Particle.DustOptions(Color.fromRGB(203, 239, 245), 1));

                    KeyedBossBar tempBar = getTemperatureBar(player, true);
                    tempBar.setVisible(true);

                    //TODO: Continue working on this, clamp values [0, 100]
                    //calc score
                    if (isWarm(player, this.range)) {
                        if (temp < 100) {
                            temp++;
                        }
                    }
                    else if (temp > 0) {
                        Bukkit.broadcastMessage("a");
                        final Block standingIn = player.getLocation().getBlock();
                        final Block standingOn = standingIn.getRelative(BlockFace.DOWN);
                        double decrease = 1.0;

                        //Standing on ice - 1.5x
                        if (standingOn.getType() == Material.ICE || standingOn.getType() == Material.PACKED_ICE) {
                            decrease *= 1.5;
                        }

                        //Standing in water - 1.25x
                        if (standingIn.getType() == Material.WATER) {
                            decrease *= 1.25;
                        }

                        Bukkit.broadcastMessage("decrease: " + decrease);
                        temp -= decrease;
                        Bukkit.broadcastMessage("new temp: " + temp);
                    }

                    //update bar and change color, also slowness
                    tempBar.setProgress(Math.max(Math.min(temp / 100.0, 1.0), 0.0));
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
                        player.setHealth(Math.floor(player.getHealth() / 2));
                    }

                    PlayerScore.TEMPERATURE.setScore(plugin, player, temp);
                }
            }
            else {
                KeyedBossBar tempBar = getTemperatureBar(player, true);
                if (tempBar != null && tempBar.isVisible()) {
                    tempBar.setVisible(false);
                }

                if (temp < 100) PlayerScore.TEMPERATURE.setScore(plugin, player, 100.0);
            }
        }
    }

    public boolean isWarm (Player player, int radius) {
        if (player.getFireTicks() > 0) return true;

        //Cached block override
        final UUID uuid = player.getUniqueId();
        if (this.cachedBlocks.containsKey(uuid)) {
            final Block block = this.cachedBlocks.get(uuid);
            final Location cachedLocation = block.getLocation();
            final Location playerLocation = player.getLocation();

            //Check if block is still a valid type
            if (this.warmBlocks.contains(block.getType())) {
                //Check if world is still the same
                if (playerLocation.getWorld().equals(cachedLocation.getWorld())) {
                    //Check if in range
                    if (
                        Math.abs(playerLocation.getBlockX() - cachedLocation.getBlockX()) <= radius &&
                        Math.abs(playerLocation.getBlockY() - cachedLocation.getBlockY()) <= radius &&
                        Math.abs(playerLocation.getBlockZ() - cachedLocation.getBlockZ()) <= radius
                    ) {
                        return true;
                    }
                }
            }
            
            //Did exist, but is no longer valid (for any of the above reasons) -> delete
            this.cachedBlocks.remove(uuid);
        }
   
        //Manually search for the blocks
        final Location origin = player.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final Block block = origin.clone().add(x, y, z).getBlock();
                    if (this.warmBlocks.contains(block.getType())) {
                        this.cachedBlocks.put(player.getUniqueId(), block);
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

    public boolean isWarmingMaterial(Material material) {
        return this.warmBlocks.contains(material);
    }
}
