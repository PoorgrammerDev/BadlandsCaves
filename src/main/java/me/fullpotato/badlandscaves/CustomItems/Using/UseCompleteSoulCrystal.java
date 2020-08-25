package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.*;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UseCompleteSoulCrystal extends LimitedUseItems implements Listener {
    private final BadlandsCaves plugin;
    private final InventorySerialize inventorySerialize;
    private final DeathHandler deathHandler;
    private final DescensionPlayerMove descensionPlayerMove;
    private final Random random;
    private final ParticleShapes particleShapes;

    public UseCompleteSoulCrystal(BadlandsCaves plugin, InventorySerialize inventorySerialize, DeathHandler deathHandler, DescensionPlayerMove descensionPlayerMove, Random random, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.inventorySerialize = inventorySerialize;
        this.deathHandler = deathHandler;
        this.descensionPlayerMove = descensionPlayerMove;
        this.random = random;
        this.particleShapes = particleShapes;
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;

        final ItemStack current = event.getItem();
        final ItemStack soul_crystal = plugin.getCustomItemManager().getItem(CustomItem.SOUL_CRYSTAL);
        if (!checkMatchIgnoreUses(current, soul_crystal, 2)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final World descension = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        final World reflection = plugin.getServer().getWorld(plugin.getReflectionWorldName());
        if (player.getWorld().equals(reflection) || player.getWorld().equals(descension)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) return;

        //deplete use
        depleteUse(current, 2);

        //save inventory
        inventorySerialize.saveInventory(player, "descension_inv");

        //clear it
        deathHandler.resetPlayer(player);
        player.getInventory().clear();

        //remove potion effects
        for (PotionEffectType value : PotionEffectType.values()) {
            player.removePotionEffect(value);
        }

        //readying descension stage
        new StageEnter(plugin, player).runTask(plugin);
        new ShrineCapture(plugin, random).runTaskTimer(plugin, 0 ,5);
        new DescensionTimeLimit(plugin, descensionPlayerMove).runTaskTimer(plugin, 0, 20);
        new DetectedBar(plugin).runTaskTimer(plugin, 0, 3);
        new LostSoulParticle(plugin).runTaskTimer(plugin, 0, 3);
        new DetectionDecrease(plugin).runTaskTimer(plugin, 0, 20);
        new ExitPortal(plugin, particleShapes).runTaskTimer(plugin, 0, 5);

        //tutorial text
        player.sendTitle(ChatColor.DARK_PURPLE + "Capture the Four Shrines.", ChatColor.of("#6c2b9e") + "Stand under each one for a while to capture them.", 20, 60, 20);
        player.sendMessage(ChatColor.DARK_PURPLE + "Capture the Four Shrines.");
        player.sendMessage(net.md_5.bungee.api.ChatColor.of("#6c2b9e") + "Stand under each one for a while to capture them.");

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(ChatColor.GOLD + "Don't let the Lost Souls find you.", ChatColor.of("#ff6a00") + "Moving near any of them will raise Detection.", 20, 60, 20);
                player.sendMessage(ChatColor.GOLD + "Don't let the Lost Souls find you.");
                player.sendMessage(ChatColor.of("#ff6a00") + "Moving near any of them will raise Detection.");
            }
        }.runTaskLaterAsynchronously(plugin, 100);

    }
}
