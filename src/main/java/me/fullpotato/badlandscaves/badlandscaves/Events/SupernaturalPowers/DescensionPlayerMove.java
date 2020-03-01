package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.DeathHandler;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class DescensionPlayerMove implements Listener {
    private World world = Bukkit.getWorld("world_descension");
    private BadlandsCaves plugin;
    public DescensionPlayerMove(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void playerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(world)) return;

        Location player_location = player.getLocation();

        //leaving descension stage (quitting)
        if (player_location.getY() < 0) {
            player.setHealth(0);
            player.setMetadata("in_descension", new FixedMetadataValue(plugin, 3));
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        int in_descension = player.getMetadata("in_descension").get(0).asInt();
        if (in_descension != 2) return;

        //leaving descension stage (winning)
        Location center_loc = new Location(world, 0, 85, 0);
        if (player_location.distanceSquared(center_loc) < 25) {
            int towers_capped = player.getMetadata("descension_shrines_capped").get(0).asInt();
            if (towers_capped == 4) {
                player.sendMessage(ChatColor.GRAY + "The strange sensation follows you back to reality.");

                DeathHandler reset = new DeathHandler(plugin);
                reset.resetPlayer(player, true, true);

            }
        }




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
            if (player.hasLineOfSight(zombie)) {
                boolean in_possession = player.getMetadata("in_possession").get(0).asBoolean();
                if (!in_possession) {
                    Location entity_location = zombie.getLocation();
                    detection = player.getMetadata("descension_detect").get(0).asDouble();

                    int multiplier = 1;
                    if (player_location.distance(entity_location) < 0.5) multiplier = 8;
                    else if (player_location.distance(entity_location) < 1) multiplier = 4;
                    else if (player_location.distance(entity_location) < 3) multiplier = 2;

                    if (sprinting) {
                        detection += multiplier;
                    }
                    else if (sneaking) {
                        detection += 0.05 * multiplier;
                    }
                    else {
                        detection += 0.1 * multiplier;
                    }

                    player.setMetadata("descension_detect", new FixedMetadataValue(plugin, detection));
                    player.setMetadata("descension_detect_cooldown", new FixedMetadataValue(plugin, 30));
                }
            }
        }

        detection = player.getMetadata("descension_detect").get(0).asDouble();
        int detect_max = plugin.getConfig().getInt("game_values.descension_max_detect");
        if (detection >= detect_max) {
            playerDetected(player);
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
        int in_descension = player.getMetadata("in_descension").get(0).asInt();
        double detected = player.getMetadata("descension_detect").get(0).asDouble();

        if (in_descension == 2 && detected < 100) {
            event.setTarget(null);
            event.setCancelled(true);
        }

    }

    public void playerDetected (Player player) {
        Location player_location = player.getLocation();

        ArrayList<EnderCrystal> crystals = (ArrayList<EnderCrystal>) world.getEntitiesByClass(EnderCrystal.class);
        player_location.subtract(0, 0.5, 0);
        for (EnderCrystal crystal : crystals) {
                crystal.setBeamTarget(player_location);
                double health = player.getHealth();
                if (!player.isDead()) {
                    health = health > 1 ? health / 2 : 0;
                    player.setHealth(health);
                }
        }
    }
}
