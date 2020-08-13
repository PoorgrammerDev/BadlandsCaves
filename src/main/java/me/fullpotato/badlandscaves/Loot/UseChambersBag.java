package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import java.util.Random;

public class UseChambersBag implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public UseChambersBag(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void useBag (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = event.getItem();
            if (item != null) {
                if (item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.HALLOWED_CHAMBERS_TREASURE_BAG))) {
                    event.setCancelled(true);
                    final Player player = event.getPlayer();
                    final WitherFightLootTable lootTable = new WitherFightLootTable(plugin);

                    item.setAmount(item.getAmount() - 1);
                    LootContext.Builder builder = new LootContext.Builder(player.getLocation());
                    builder.luck((float) player.getAttribute(Attribute.GENERIC_LUCK).getValue());
                    lootTable.populateLoot(random, builder.build()).forEach(loot -> {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItemNaturally(player.getLocation(), loot);
                        }
                        else {
                            player.getInventory().addItem(loot);
                        }
                    });
                }
            }
        }
    }
}
