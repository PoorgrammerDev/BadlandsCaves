package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Info.GuideBook;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Withdraw;
import me.fullpotato.badlandscaves.Util.InitializePlayer;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinLeave implements Listener {

    private final BadlandsCaves plugin;
    public PlayerJoinLeave(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void player_join (PlayerJoinEvent event) {
        //give items
        Player player = event.getPlayer();

        //NEW PLAYER--------------------------------
        if (!player.hasPlayedBefore() || (!(PlayerScore.INITIALIZED.hasScore(plugin, player))) || (byte) PlayerScore.INITIALIZED.getScore(plugin, player) == (byte) 0) {
            InitializePlayer data = new InitializePlayer(plugin);
            data.initializePlayer(player);

            if (!player.hasPlayedBefore()) {
                ItemStack starter_sapling = CustomItem.STARTER_SAPLING.getItem();
                ItemStack starter_bone_meal = CustomItem.STARTER_BONE_MEAL.getItem();
                player.getInventory().addItem(starter_sapling);
                player.getInventory().addItem(starter_bone_meal);
                player.getInventory().addItem(GuideBook.getGuideBook(plugin));



                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setHealth(40.0);
                        player.setSaturation(20.0F);
                    }
                }.runTaskLaterAsynchronously(plugin, 5);
            }
        }

        //EVERYONE---------------------------------------

        //if in wither fight, tp out
        if (player.getWorld().equals(plugin.getServer().getWorld(plugin.getChambersWorldName()))) {
            if (plugin.getSystemConfig().getInt("wither_fight.fight_stage") == -1) {
                Location warp = player.getBedSpawnLocation() == null ? plugin.getServer().getWorld(plugin.getMainWorldName()).getSpawnLocation() : player.getBedSpawnLocation();
                warp.setYaw(player.getLocation().getYaw());
                warp.setPitch(player.getLocation().getPitch());

                player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }



        //REGARDING SUPERNATURAL POWERS----------------------------------

        //if they log off in the withdraw pocket dimension, it sends them back to the real world when they log back in
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (has_powers) {
            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.getWithdrawWorldName()))) {
                String origworldname = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".withdraw_orig_world");
                if (origworldname == null) {
                    player.setHealth(0);
                }
                else {
                    PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, 0);
                    final World origworld = plugin.getServer().getWorld(origworldname);
                    final double x = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
                    final double y = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
                    final double z = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);

                    final Location location = new Location(origworld, x, y, z);
                    Withdraw sendback = new Withdraw(plugin);
                    sendback.getOuttaHere(player, location, player.getLocation());
                }
            }

            //reset agility jump timer
            // FIXME: 6/7/2020 idk man
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));

            //reset cooldowns
            PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 0);
            PlayerScore.SWAP_WINDOW.setScore(plugin, player, 0);
            PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 0);
        }
    }

    @EventHandler
    public void player_leave (PlayerQuitEvent event) {
        Player player = event.getPlayer();
    }
}
