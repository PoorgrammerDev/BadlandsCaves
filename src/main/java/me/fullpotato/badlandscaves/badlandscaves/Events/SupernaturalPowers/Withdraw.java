package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class Withdraw implements Listener {
    private BadlandsCaves plugin;

    public Withdraw(BadlandsCaves bcav) {
        plugin = bcav;
    }

    private Location voidloc;
    private World void_world = Bukkit.getWorld("world_empty");

    @EventHandler
    public void use_withdraw(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    if (player.getLocation().getWorld().equals(void_world))
                        event.setCancelled(true);
                    else {
                        event.setCancelled(true);
                        Random random = new Random();
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 256; y++) {
                                for (int z = 0; z < 16; z++) {
                                    Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                    Location block_loc = block.getLocation();
                                    block_loc.setWorld(void_world);
                                    if (block.getType().isSolid()) {
                                        int rand = random.nextInt(3);
                                        if (rand == 0) block_loc.getBlock().setType(Material.COAL_BLOCK);
                                        else if (rand == 1) block_loc.getBlock().setType(Material.BLACK_CONCRETE);
                                        else block_loc.getBlock().setType(Material.BLACK_WOOL);
                                    } else {
                                        block_loc.getBlock().setType(Material.AIR);
                                    }
                                }
                            }
                        }

                        Location location = player.getLocation();
                        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".withdraw_orig_world", location.getWorld().getName());

                        voidloc = player.getLocation();
                        voidloc.setWorld(void_world);

                        player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, random.nextInt(200) + 500));

                        if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);
                        player.teleport(voidloc);

                        BukkitTask decrement_timer = new BukkitRunnable() {
                            @Override
                            public void run() {
                                int withdraw_timer = player.getMetadata("withdraw_timer").get(0).asInt();

                                if (withdraw_timer <= 0) {
                                    player.teleport(location);
                                    if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
                                    boolean ready_to_clear = true;

                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (player.getLocation().getWorld().equals(voidloc.getWorld()))
                                            ready_to_clear = false;
                                    }

                                    if (ready_to_clear) {
                                        for (int x = 0; x < 16; x++) {
                                            for (int y = 0; y < 256; y++) {
                                                for (int z = 0; z < 16; z++) {
                                                    Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                                    Location block_loc = block.getLocation();
                                                    block_loc.setWorld(void_world);
                                                    block_loc.getBlock().setType(Material.AIR);
                                                }
                                            }
                                        }
                                    }

                                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                                } else {
                                    player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, withdraw_timer - 1));
                                }
                            }
                        }.runTaskTimer(plugin, 0, 0);

                    }
                }
            }
        }
    }

    @EventHandler
    public void keep_in_chunk (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        Location location = player.getLocation();
        World world = location.getWorld();

        assert world != null;
        if (!world.equals(void_world)) return;

        Chunk chunk = location.getChunk();
        if (!player.getLocation().getChunk().equals(voidloc.getChunk())) {
            Location voidloc_keeplook = voidloc;
            voidloc_keeplook.setYaw(player.getLocation().getYaw());
            voidloc_keeplook.setPitch(player.getLocation().getPitch());

            player.teleport(voidloc_keeplook);
        }
    }


    //if you can, find a better way to prevent the dragon from existing
    //best case scenario: the dragon, the portal, and the egg never spawn in the first place
    //could be possible with protocollib? idk

    @EventHandler
    public void prevent_dragon (CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof EnderDragon || entity.getType().equals(EntityType.ENDER_DRAGON)) {
            if (entity.getWorld().equals(void_world)) {
                EnderDragon dragon = (EnderDragon) entity;
                dragon.setPhase(EnderDragon.Phase.DYING);
                Location location = new Location(void_world, 0, -300, 0);
                dragon.teleport(location);
            }
        }
    }

    @EventHandler
    public void remove_egg (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(void_world)) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) return;

        event.setCancelled(true);
        event.getClickedBlock().setType(Material.AIR);

    }

    @EventHandler
    public void prevent_portals (PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(void_world)) return;

        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause.equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY) || cause.equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
        }
    }

}
