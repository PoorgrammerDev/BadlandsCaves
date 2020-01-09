package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class getFishingCrate implements Listener {
    private BadlandsCaves plugin;
    public getFishingCrate(BadlandsCaves bcav) {
        plugin = bcav;
    }


    @EventHandler
    public void fishing (PlayerFishEvent event) {
        if (event.getCaught() == null) return;

        if (event.getCaught() instanceof Item) {
            Player player = event.getPlayer();
            Item item = (Item) event.getCaught();
            Material itemtype = item.getItemStack().getType();

            if (itemtype.equals(Material.TROPICAL_FISH)) {
                int default_bound = plugin.getConfig().getInt("game_values.fishing_crate_chance");

                int luck_effect = 0;
                if (player.hasPotionEffect(PotionEffectType.LUCK)) {
                    luck_effect = player.getPotionEffect(PotionEffectType.LUCK).getAmplifier();
                }

                int rod_luck = 0;
                if (player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD)) {
                    rod_luck = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK);
                }
                else if (player.getInventory().getItemInOffHand().getType().equals(Material.FISHING_ROD)) {
                    rod_luck = player.getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.LUCK);
                }

                int bound = default_bound - (luck_effect + rod_luck);
                int random = 0;
                if (bound > 0) {
                    random = new Random().nextInt(bound);
                }
                if (random == 0) {
                    ItemStack crate = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate").getValues(true));
                    item.setItemStack(crate);
                }
            }
        }
    }
}
