package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.badlandscaves.NMS.ReflectionWorldNMS;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EndGame implements Listener {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_reflection");

    public EndGame(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerLose (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (player.getWorld().equals(world)) {
            event.setDeathMessage(null);
            resetWorld(player);
        }
    }

    @EventHandler
    public void PlayerWin (EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie && event.getEntity().getWorld().equals(world)) {
            //get player
            Player player = null;
            for (Player search : world.getEntitiesByClass(Player.class)) {
                if (search.getGameMode().equals(GameMode.SURVIVAL) || search.getGameMode().equals(GameMode.ADVENTURE)) {
                    player = search;
                    break;
                }
            }

            if (player != null) {
                DeathHandler resetter = new DeathHandler(plugin);
                resetWorld(player);
                resetter.resetPlayer(player, false, true);
                completeSoul(player);
            }
        }
    }

    public void resetWorld(Player player) {
        ClearEntities();
        removeClone(player);
        world.setTime(6000);
    }

    public void ClearEntities () {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
    }

    public void removeClone(Player player) {
        ReflectionWorldNMS nms = new ReflectionWorldNMS(player);
        nms.remove();
    }

    public void completeSoul (Player player) {
        final ItemStack incomplete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));
        final ItemStack complete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal").getValues(true));
        for (ItemStack item : player.getInventory()) {
            if (item.isSimilar(incomplete)) {
                item.setAmount(0);
                player.getInventory().addItem(complete);
                return;
            }
        }
    }
}
