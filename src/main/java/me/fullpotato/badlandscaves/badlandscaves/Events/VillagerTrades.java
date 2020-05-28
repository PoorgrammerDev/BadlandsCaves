package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class VillagerTrades implements Listener {
    private BadlandsCaves plugin;

    public VillagerTrades(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

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
