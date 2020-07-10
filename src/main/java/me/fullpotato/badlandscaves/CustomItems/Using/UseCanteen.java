package me.fullpotato.badlandscaves.CustomItems.Using;

import com.google.gson.internal.$Gson$Preconditions;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Canteen;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Thirst.Drinking;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class UseCanteen implements Listener {
    private final BadlandsCaves plugin;

    public UseCanteen(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void useCanteen (PlayerItemConsumeEvent event) {
        final ItemStack item = event.getItem();
        final Canteen canteen = new Canteen(plugin);
        if (canteen.isCanteen(item)) {
            final Player player = event.getPlayer();
            final Drinking drinking = new Drinking(plugin);
            final String type = canteen.getType(item);

            event.setCancelled(true);
            if (type.equals("EMPTY")) {
                player.sendMessage(ChatColor.RED + "The canteen is empty.");
                return;
            }
            else if (type.equals(CustomItem.TOXIC_WATER.name())) {
                drinking.drinkToxicWater(player);
            }
            else if (type.equals(CustomItem.PURIFIED_WATER.name())) {
                drinking.drinkPurifiedWater(player);
            }
            else if (type.equals(CustomItem.ANTIDOTE.name())) {
                drinking.drinkAntidote(player);
            }
            else if (type.equals(CustomItem.MANA_POTION.name())) {
                drinking.drinkManaPotion(player);
            }

            ItemStack clone = item.clone();
            canteen.setAmount(clone, canteen.getAmount(item) - 1);

            if (player.getInventory().getItemInMainHand().isSimilar(item)) {
                player.getInventory().setItemInMainHand(clone);
            }
            else if (player.getInventory().getItemInOffHand().isSimilar(item)) {
                player.getInventory().setItemInOffHand(clone);
            }

            player.updateInventory();
        }
    }

}
