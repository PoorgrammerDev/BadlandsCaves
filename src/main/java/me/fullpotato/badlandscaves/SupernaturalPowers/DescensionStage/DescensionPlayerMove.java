package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.Using.UseIncompleteSoulCrystal;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TitleEffects;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DescensionPlayerMove implements Listener {
    private final World world;
    private final BadlandsCaves plugin;
    private final NamespacedKey key;
    private final DeathHandler reset;
    private final UseIncompleteSoulCrystal tester;
    private final InventorySerialize inventorySerialize;
    public DescensionPlayerMove(BadlandsCaves bcav, DeathHandler reset, UseIncompleteSoulCrystal tester, InventorySerialize inventorySerialize) {
        plugin = bcav;
        this.reset = reset;
        this.tester = tester;
        this.inventorySerialize = inventorySerialize;
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        key = new NamespacedKey(plugin, "descension_detected_bar");
    }

    @EventHandler
    public void playerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(world)) return;

        Location player_location = player.getLocation();
        Location center_loc = new Location(world, 0, 85, 0);

        boolean fallOff = (player_location.getY() < 0);
        boolean usePortal = (player_location.distanceSquared(center_loc) < 25);
        boolean gamemodeMatches = (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL));

        int in_descension = (int) PlayerScore.IN_DESCENSION.getScore(plugin, player);

        if (fallOff || (usePortal && gamemodeMatches && in_descension == 2)) {
            int captures = (int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player);

            //Reset player
            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
            resetPlayer(player, (captures == 4));
            player.setFallDistance(0);

            if (captures == 4) {
                //Send player messages
                player.sendTitle(ChatColor.of("#4c158f") + "You are now a Heretic.", ChatColor.of("#25005c") + "The strange sensation follows you back to reality.", 20, 60, 20);
                player.sendMessage(ChatColor.of("#4c158f") + "You are now a Heretic.");
                player.sendMessage(ChatColor.of("#25005c") + "The strange sensation follows you back to reality.");

                //Play exit portal sound
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 0.5F, 1);
                    }
                }.runTaskLaterAsynchronously(plugin, 1);
            }
            else {
                PlayerScore.IN_DESCENSION.setScore(plugin, player, 3);
            }
            return;
        }

        if (!gamemodeMatches || in_descension != 2) return;

        boolean moved_x = (Math.abs(event.getTo().getX() - event.getFrom().getX()) > 0);
        boolean moved_y = (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0);
        boolean moved_z = (Math.abs(event.getTo().getZ() - event.getFrom().getZ()) > 0);
        boolean moved = moved_x || moved_y || moved_z;
        if (!moved) return;

        boolean sneaking = player.isSneaking();
        boolean sprinting = player.isSprinting();
        double detection;
        List<Zombie> zombies = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Zombie) {
                zombies.add((Zombie) entity);
            }
        }

        for (Zombie zombie : zombies) {
            if (!player.getWorld().equals(zombie.getWorld())) continue;

            if (player.hasLineOfSight(zombie)) {
                boolean in_possession = ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);
                if (!in_possession) {
                    Location entity_location = zombie.getLocation();
                    detection = ((double) PlayerScore.DESCENSION_DETECT.getScore(plugin, player));

                    int multiplier = 1;
                    if (player_location.distance(entity_location) < 0.5) multiplier = 16;
                    else if (player_location.distance(entity_location) < 1) multiplier = 8;
                    else if (player_location.distance(entity_location) < 3) multiplier = 4;
                    else if (player_location.distance(entity_location) < 5) multiplier = 2;

                    if (sprinting) {
                        detection += multiplier;
                    }
                    else if (sneaking) {
                        detection += 0.05 * multiplier;
                    }
                    else {
                        detection += 0.1 * multiplier;
                    }

                    PlayerScore.DESCENSION_DETECT.setScore(plugin, player, detection);
                    PlayerScore.DESCENSION_DETECT_COOLDOWN.setScore(plugin, player, 15);
                }
            }
        }

        detection = ((double) PlayerScore.DESCENSION_DETECT.getScore(plugin, player));
        int detect_max = plugin.getOptionsConfig().getInt("descension_max_detect");
        if (detection >= detect_max) {
            playerLost(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    TitleEffects titleEffects = new TitleEffects(plugin);
                    titleEffects.sendDecodingTitle(player, "DETECTED", ChatColor.of("#ff0000") + ChatColor.BOLD.toString(), "", "", 0, 20, 10, 2, false);
                }
            }.runTaskLaterAsynchronously(plugin, 5);
        }
    }

    @EventHandler
    public void playerDisconnected (PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.getWorld().equals(world)) {
            final int inDescension = (int) PlayerScore.IN_DESCENSION.getScore(plugin, player);
            if (inDescension == 2) {
                playerLost(player);
            }
        }
    }

    @EventHandler
    public void cancelMobTarget (EntityTargetLivingEntityEvent event) {
        if (!event.getEntity().getWorld().equals(world) ||
                event.getTarget() == null ||
                !event.getTarget().getWorld().equals(world) ||
                !(event.getEntity() instanceof Zombie) ||
                !(event.getTarget() instanceof Player)) return;

        Player player = (Player) event.getTarget();
        int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
        double detected = ((double) PlayerScore.DESCENSION_DETECT.getScore(plugin, player));

        if (in_descension == 2 && detected < 100) {
            event.setTarget(null);
            event.setCancelled(true);
        }

    }

    public void playerLost(Player player) {
        Location player_location = player.getLocation();
        ArrayList<EnderCrystal> crystals = (ArrayList<EnderCrystal>) world.getEntitiesByClass(EnderCrystal.class);
        player_location.subtract(0, 0.5, 0);
        for (EnderCrystal crystal : crystals) {
            crystal.setBeamTarget(player_location);
        }
        player.setFallDistance(0);
        resetPlayer(player);
    }

    public void resetPlayer (Player player) {
        resetPlayer(player, false);
    }

    public void resetPlayer (Player player, boolean win) {
        reset.resetPlayer(player, true, true, false);
        inventorySerialize.loadInventory(player, "descension_inv", true, true);

        final KeyedBossBar bossBar = plugin.getServer().getBossBar(key);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }

        if (win) {
            final ItemStack soul_crystal = plugin.getCustomItemManager().getItem(CustomItem.SOUL_CRYSTAL);
            for (ItemStack item : player.getInventory()) {
                if (item != null) {
                    if (tester.checkMatchIgnoreUses(item, soul_crystal, 2)) {
                        item.setAmount(0);
                        player.updateInventory();
                        return;
                    }
                }
            }
        }
    }
}
