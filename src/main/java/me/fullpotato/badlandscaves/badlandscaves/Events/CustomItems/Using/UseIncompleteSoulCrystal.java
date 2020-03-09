package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage.SpawnBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;


public class UseIncompleteSoulCrystal implements Listener {
    private BadlandsCaves plugin;
    public UseIncompleteSoulCrystal (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        final ItemStack item = event.getItem();
        if (item == null) return;

        final ItemStack soul_crystal_incomplete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));
        if (!item.isSimilar(soul_crystal_incomplete)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();
        if (in_reflection) return;

        final World reflection = Bukkit.getWorld("world_reflection");
        if (reflection == null) return;

        PlayerInventory inventory = player.getInventory();




        Location worldspawn = reflection.getSpawnLocation();
        worldspawn.setY(255);

        //removing all effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        //entering
        player.setMetadata("in_reflection", new FixedMetadataValue(plugin, true));
        player.teleport(worldspawn);

        //heal and full hunger
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setSaturation(20);
                player.setFoodLevel(20);
            }
        }.runTaskLaterAsynchronously(plugin, 1);


        //spawning the boss, delay of 10 seconds
        new SpawnBoss(plugin, player).runTaskLater(plugin, 200);
    }
}
