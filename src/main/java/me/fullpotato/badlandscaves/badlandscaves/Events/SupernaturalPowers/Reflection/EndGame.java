package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.UseIncompleteSoulCrystal;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.badlandscaves.NMS.FakePlayer;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.badlandscaves.Util.InventorySerialize;
import org.bukkit.*;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class EndGame implements Listener {
    private BadlandsCaves plugin;
    private World world;

    public EndGame(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.world = plugin.getServer().getWorld(plugin.reflectionWorldName);
    }

    @EventHandler
    public void PlayerLose (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (player.getWorld().equals(world)) {
            event.setDeathMessage(null);
            player.setMetadata("refl_respawn_inv", new FixedMetadataValue(plugin, true));
            resetWorld();
        }
    }

    @EventHandler
    public void playerRespawnAfterLosing (PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player.getMetadata("refl_respawn_inv").get(0).asBoolean()) {
            restoreInventory(player);
            player.setMetadata("refl_respawn_inv", new FixedMetadataValue(plugin,  false));
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
                resetWorld();

                final Player ply = player;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ply.setHealth(20);
                        resetter.resetPlayer(ply, false, true, false);
                        restoreInventory(ply);
                        completeSoul(ply);
                    }
                }.runTaskLater(plugin, 5);
            }
        }
    }

    public void resetWorld() {
        ClearEntities();
        removeClone();
        world.setTime(6000);

        final NamespacedKey key = new NamespacedKey(plugin, "reflection_world_boss_health");
        KeyedBossBar health_bar = plugin.getServer().getBossBar(key);
        if (health_bar != null) {
            health_bar.setVisible(false);
        }

    }

    public void ClearEntities () {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
    }

    public void removeClone() {
        FakePlayer nms = new FakePlayer(plugin, world);
        nms.remove(ZombieBossBehavior.fakePlayer);

        ZombieBossBehavior.fakePlayer = null;
    }

    public void completeSoul (final Player player) {
        UseIncompleteSoulCrystal usecrystal = new UseIncompleteSoulCrystal(plugin);
        final ItemStack incomplete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));
        final ItemStack complete = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal").getValues(true));
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (usecrystal.checkMatchIgnoreUses(item, incomplete, 2)) {
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }
        player.getInventory().addItem(complete);
    }

    public void restoreInventory (final Player player) {
        final InventorySerialize inv = new InventorySerialize(plugin);
        inv.loadInventory(player, "reflection_inv", true, true);
    }
}
