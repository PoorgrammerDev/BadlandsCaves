package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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

public class descensionMobDetection implements Listener {
    private World world = Bukkit.getWorld("world_descension");
    private BadlandsCaves plugin;
    public descensionMobDetection (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void playerMove (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if (!player.getWorld().equals(world)) return;

        int in_descension = player.getMetadata("in_descension").get(0).asInt();
        if (in_descension != 2) return;

        boolean moved_x = (Math.abs(event.getTo().getX() - event.getFrom().getX()) > 0);
        boolean moved_y = (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0);
        boolean moved_z = (Math.abs(event.getTo().getZ() - event.getFrom().getZ()) > 0);
        boolean moved = moved_x || moved_y || moved_z;
        if (!moved) return;

        boolean sneaking = player.isSneaking();
        boolean sprinting = player.isSprinting();
        double detection;
        List<Entity> entities = player.getNearbyEntities(5, 5, 5);
        for (Entity entity : entities) {
            if (player.hasLineOfSight(entity)) {
                Location player_location = player.getLocation();
                Location entity_location = entity.getLocation();
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
