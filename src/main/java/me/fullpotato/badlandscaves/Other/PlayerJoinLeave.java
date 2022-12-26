package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Info.GuideBook;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Withdraw;
import me.fullpotato.badlandscaves.Util.InitializePlayer;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerJoinLeave implements Listener {
    private final BadlandsCaves plugin;
    private final GuideBook guideBook;
    private final Withdraw withdraw;
    private final InitializePlayer data;
    private final PlayerEffects playerEffects;
    private final Random random;

    public PlayerJoinLeave(BadlandsCaves bcav, GuideBook guideBook, Withdraw withdraw, PlayerEffects playerEffects, Random random) {
        plugin = bcav;
        this.guideBook = guideBook;
        this.withdraw = withdraw;
        this.playerEffects = playerEffects;
        data = new InitializePlayer(plugin);
        this.random = random;
    }

    @EventHandler
    public void player_join (PlayerJoinEvent event) {
        //give items
        Player player = event.getPlayer();
        final World world = player.getWorld();

        //NEW PLAYER--------------------------------
        if (!player.hasPlayedBefore() || (!(PlayerScore.INITIALIZED.hasScore(plugin, player))) || (byte) PlayerScore.INITIALIZED.getScore(plugin, player) == (byte) 0) {
            data.initializePlayer(player);

            if (!player.hasPlayedBefore()) {
                player.getInventory().addItem(plugin.getCustomItemManager().getItem(CustomItem.STARTER_SAPLING));
                player.getInventory().addItem(new ItemStack(Material.TORCH, 4));
                player.getInventory().addItem(guideBook.getGuideBook());

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

        //alternate dimension logout spot
        DimensionJoinCheck(player);

        //reload effects
        playerEffects.applyEffects(player, true);


        //REGARDING SUPERNATURAL POWERS----------------------------------
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (has_powers) {
            //if they log off in the withdraw pocket dimension, it sends them back to the real world when they log back in
            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.getWithdrawWorldName()))) {
                String origworldname = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".withdraw_orig_world");
                Location location = player.getBedSpawnLocation();

                if (origworldname != null) {
                    final World origWorld = plugin.getServer().getWorld(origworldname);
                    final double originX = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
                    final double originY = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
                    final double originZ = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);
                    location = new Location(origWorld, originX, originY, originZ);

                }

                withdraw.exitWithdraw(player, location);
            }

            //reset agility jump timer
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));

            //reset cooldowns
            PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 0);
            PlayerScore.SWAP_WINDOW.setScore(plugin, player, 0);
            PlayerScore.SWAP_COOLDOWN.setScore(plugin, player, 0);
            PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 0);
        }
    }

    //if in alternate dimension, check if player is logging in in the same world.
    //if not, then warp them back to their spawn or force the world to load and warp them back there
    private void DimensionJoinCheck(Player player) {
        //last logged out in an alternate dimension
        if (!plugin.getSystemConfig().contains("alternate_dimensions_logouts." + player.getUniqueId() + ".world_name")) return;
        final String worldName = plugin.getSystemConfig().getString("alternate_dimensions_logouts." + player.getUniqueId() + ".world_name");
        final Vector vector = plugin.getSystemConfig().getVector("alternate_dimensions_logouts." + player.getUniqueId() + ".location_vector");

        //clear the previous logout alternate dimension
        plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".world_name", null);
        plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".location_vector", null);
        plugin.saveSystemConfig();

        if (worldName != null) {
            //logout world and login world names match -> everything's good, return
            if (player.getWorld().getName().equals(worldName)) return;

            //worlds don't match
            //if world is not loaded, then load it
            World logoutWorld = null;
            if (plugin.getServer().getWorld(worldName) == null && plugin.getOptionsConfig().getBoolean("alternate_dimensions.allow_load_on_join")) {
                final DimensionsWorlds dimensions = new DimensionsWorlds(plugin, random);

                //here, worldName is the entire name: "world_dim_UUID"
                //however, dimensions.generate() expects just the UUID, and it manually appends "world_dim_" onto the beginning
                //so, this splits by delimiter of _ into {"world", "dim", "UUID"} and selects the 3rd element "UUID" to feed into the function
                logoutWorld = dimensions.generate(worldName.split("_")[2], false);
            }

            if (logoutWorld != null) {
                final Location playerLoc = player.getLocation();
                final Location logoutLocation = new Location(logoutWorld, vector.getX(), vector.getY(), vector.getZ(), playerLoc.getYaw(), playerLoc.getPitch());

                if (logoutLocation != null) {
                    if (plugin.getServer().getWorlds().contains(logoutWorld)) {
                        player.teleport(logoutLocation, TeleportCause.PLUGIN);
                        return;
                    }
                }
            }

        }

        //at this point, world is unloaded. now we fallback to teleporting them to their bed spawn
        Location location = player.getBedSpawnLocation();
        if (location == null) location = plugin.getServer().getWorld(plugin.getMainWorldName()).getSpawnLocation();
        player.teleport(location, TeleportCause.PLUGIN);
    }

    /**
     * Saves log-off location if player logged off in an Alternate Dimension
     */
    @EventHandler
    public void playerLeaveInDimension (PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        //player logged off in an alternate dimension -- save this info
        if (world.getName().startsWith(plugin.getDimensionPrefixName())) {
            plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".world_name", player.getWorld().getName());
            plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".location_vector", player.getLocation().toVector());
            plugin.saveSystemConfig();
        }

        //player did not log off in an alternate dimension
        else {
            //clear any old data that might still be there from a previous logout
            plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".world_name", null);
            plugin.getSystemConfig().set("alternate_dimensions_logouts." + player.getUniqueId().toString() + ".location_vector", null);
            plugin.saveSystemConfig();
        }
    }
}
