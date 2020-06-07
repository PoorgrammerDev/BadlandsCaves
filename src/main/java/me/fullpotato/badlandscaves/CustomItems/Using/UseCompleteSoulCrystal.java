package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.StageEnter;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseCompleteSoulCrystal extends LimitedUseItems implements Listener {
    private BadlandsCaves plugin;

    public UseCompleteSoulCrystal(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;

        final ItemStack current = event.getItem();
        final ItemStack soul_crystal = CustomItem.SOUL_CRYSTAL.getItem();
        if (!checkMatchIgnoreUses(current, soul_crystal, 2)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final World descension = plugin.getServer().getWorld(plugin.descensionWorldName);
        final World reflection = plugin.getServer().getWorld(plugin.reflectionWorldName);
        if (player.getWorld().equals(reflection) || player.getWorld().equals(descension)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if (player.getMetadata("has_supernatural_powers").get(0).asBoolean()) return;

        //deplete use
        depleteUse(current, 2);

        //save inventory
        InventorySerialize invser = new InventorySerialize(plugin);
        invser.saveInventory(player, "descension_inv");

        //clear it
        DeathHandler reset = new DeathHandler(plugin);
        reset.resetPlayer(player);
        player.getInventory().clear();

        //put you into the descension stage
        new StageEnter(plugin, player).runTask(plugin);
    }
}
