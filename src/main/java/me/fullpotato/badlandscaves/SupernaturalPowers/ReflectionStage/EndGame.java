package me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.Using.UseIncompleteSoulCrystal;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
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
import org.bukkit.scheduler.BukkitRunnable;

public class EndGame implements Listener {
    private final BadlandsCaves plugin;
    private final World world;

    public EndGame(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.world = plugin.getServer().getWorld(plugin.reflectionWorldName);
    }

    @EventHandler
    public void PlayerLose (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (player.getWorld().equals(world)) {
            event.setDeathMessage(null);
            PlayerScore.REFL_RESPAWN_INV.setScore(plugin, player, 1);
            resetWorld();
        }
    }

    @EventHandler
    public void playerRespawnAfterLosing (PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (((byte) PlayerScore.REFL_RESPAWN_INV.getScore(plugin, player) == 1)) {
            restoreInventory(player);
            PlayerScore.REFL_RESPAWN_INV.setScore(plugin, player, (byte) 0);
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
        FakePlayerNMS nms = plugin.fakePlayerNMS;
        nms.remove(ZombieBossBehavior.fakePlayer);

        ZombieBossBehavior.fakePlayer = null;
    }

    public void completeSoul (final Player player) {
        UseIncompleteSoulCrystal usecrystal = new UseIncompleteSoulCrystal(plugin);
        final ItemStack incomplete = CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem();
        final ItemStack complete = CustomItem.SOUL_CRYSTAL.getItem();
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
