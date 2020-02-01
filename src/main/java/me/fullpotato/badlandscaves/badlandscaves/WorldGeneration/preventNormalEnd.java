package me.fullpotato.badlandscaves.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.makeReincarnationWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class preventNormalEnd implements Listener {
    private BadlandsCaves plugin;

    public preventNormalEnd (BadlandsCaves bcav) {
        plugin = bcav;
    }

    private World[] fake_ends = {
            Bukkit.getWorld("world_empty"),
            Bukkit.getWorld("world_reincarnation"),
    };

    //if you can, find a better way to prevent the dragon from existing
    //best case scenario: the dragon, the portal, and the egg never spawn in the first place
    //could be possible with protocollib? idk

    @EventHandler
    public void prevent_dragon (CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof EnderDragon || entity.getType().equals(EntityType.ENDER_DRAGON)) {
            for (World world : fake_ends) {
                if (entity.getWorld().equals(world)) {
                    EnderDragon dragon = (EnderDragon) entity;
                    dragon.setPhase(EnderDragon.Phase.DYING);
                    Location location = new Location(world, 0, -300, 0);
                    dragon.teleport(location);

                    if (world.equals(Bukkit.getWorld("world_reincarnation"))) {
                        new makeReincarnationWorld(plugin, world).runTaskLater(plugin, 201);
                    }
                    else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (int x = -5; x <= 5; x++) {
                                    for (int z = -5; z <= 5; z++) {
                                        for (int y = 0; y <= 255; y++) {
                                            Location loc = new Location(world, x, y, z);
                                            Block block = loc.getBlock();
                                            if (block.getType().equals(Material.BEDROCK) ||  block.getType().equals(Material.END_PORTAL) || block.getType().equals(Material.END_STONE)) {
                                                block.setType(Material.EMERALD_BLOCK);
                                            }
                                            else if (block.getType().equals(Material.WALL_TORCH) || block.getType().equals(Material.DRAGON_EGG)) {
                                                block.setType(Material.AIR);
                                            }
                                        }
                                    }
                                }
                            }
                        }.runTaskLater(plugin, 201); //perfect number right as the completed portal spawns
                    }
                }
            }
        }
    }

    @EventHandler
    public void remove_egg (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        for (World world : fake_ends) {
            if (!player.getWorld().equals(world)) continue;
            if (event.getClickedBlock() == null) continue;
            if (!event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) continue;

            event.setCancelled(true);
            event.getClickedBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void prevent_portals (PlayerPortalEvent event) {
        Player player = event.getPlayer();
        for (World world : fake_ends) {
            if (!player.getWorld().equals(world)) continue;

            PlayerTeleportEvent.TeleportCause cause = event.getCause();
            if (cause.equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY) || cause.equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void prevent_portal_spawn (PortalCreateEvent event) {
        for (World world : fake_ends) {
            if (!event.getWorld().equals(world)) continue;
            event.setCancelled(true);
        }
    }
}
