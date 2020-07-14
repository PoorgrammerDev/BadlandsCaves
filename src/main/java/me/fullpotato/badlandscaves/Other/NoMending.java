package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class NoMending implements Listener {

    @EventHandler
    public void villagerTrade (VillagerAcquireTradeEvent event) {
        ItemStack item = event.getRecipe().getResult();
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta.hasStoredEnchant(Enchantment.MENDING)) {
                event.setCancelled(true);
            }
        }
    }
}
