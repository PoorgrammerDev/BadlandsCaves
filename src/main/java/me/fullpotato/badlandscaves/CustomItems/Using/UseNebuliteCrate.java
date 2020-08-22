package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class UseNebuliteCrate implements Listener {
    private final BadlandsCaves plugin;
    private final Nebulite[] nebulites = Nebulite.values();
    private final Random random;

    public UseNebuliteCrate(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void useNebuliteCrate (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
            final ItemStack item = event.getItem();
            if (item != null && item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.NEBULITE_CRATE))) {
                event.setUseItemInHand(Event.Result.DENY);
                item.setAmount(item.getAmount() - 1);

                final ItemStack nebulite = plugin.getCustomItemManager().getItem(nebulites[random.nextInt(nebulites.length)].getNebuliteItem());
                final Player player = event.getPlayer();

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), nebulite);
                }
                else {
                    player.getInventory().addItem(nebulite);
                }
            }
        }
    }
}
