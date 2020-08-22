package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GetFishingCrate implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    public GetFishingCrate(BadlandsCaves bcav, Random random) {
        plugin = bcav;
        this.random = random;
    }


    @EventHandler
    public void fishing (PlayerFishEvent event) {
        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if (event.getCaught() != null && event.getCaught() instanceof Item) {
                final Player player = event.getPlayer();
                Item item = (Item) event.getCaught();
                final int default_bound = plugin.getOptionsConfig().getInt("fishing_crate_chance");
                final double player_luck = player.getAttribute(Attribute.GENERIC_LUCK).getValue();

                int rod_luck = 0;
                if (player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)) {
                    rod_luck = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK);
                }
                else if (player.getInventory().getItemInOffHand().getType().equals(Material.FISHING_ROD)) {
                    rod_luck = player.getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.LUCK);
                }


                // TODO: 4/6/2020 review this
                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                final double chance = 100 * (Math.pow(rod_luck / 15.0, 1.5) + (player_luck / 150.0)) + default_bound + (chaos / 10.0);

                if (chance > 0 && random.nextInt(100) < chance) {
                    final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                    final ItemStack crate = hardmode ? plugin.getCustomItemManager().getItem(CustomItem.FISHING_CRATE_HARDMODE) : plugin.getCustomItemManager().getItem(CustomItem.FISHING_CRATE);
                    item.setItemStack(crate);
                }
            }
        }
    }
}
