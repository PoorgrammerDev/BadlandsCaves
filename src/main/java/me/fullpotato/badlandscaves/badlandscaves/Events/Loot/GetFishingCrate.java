package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class GetFishingCrate implements Listener {
    private BadlandsCaves plugin;
    public GetFishingCrate(BadlandsCaves bcav) {
        plugin = bcav;
    }


    @EventHandler
    public void fishing (PlayerFishEvent event) {
        if (event.getCaught() != null && event.getCaught() instanceof Item) {
            final Player player = event.getPlayer();
            Item item = (Item) event.getCaught();
            final Random random = new Random();
            final int default_bound = plugin.getConfig().getInt("game_values.fishing_crate_chance");
            final double player_luck = player.getAttribute(Attribute.GENERIC_LUCK).getValue();

            int rod_luck = 0;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)) {
                rod_luck = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK);
            }
            else if (player.getInventory().getItemInOffHand().getType().equals(Material.FISHING_ROD)) {
                rod_luck = player.getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.LUCK);
            }


            // TODO: 4/6/2020 review this
            final double chance = 100 * (Math.pow(rod_luck / 15.0, 1.5) + (player_luck / 150.0)) + default_bound;

            if (chance > 0 && random.nextInt(100) < chance) {
                final boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
                final ItemStack crate = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate" + (hardmode ? "_hardmode" : "")).getValues(true));
                item.setItemStack(crate);
            }
        }
    }
}
