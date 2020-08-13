package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SoulLantern implements Listener {
    private final BadlandsCaves plugin;
    final ItemStack lantern;
    public SoulLantern(BadlandsCaves plugin) {
        this.plugin = plugin;
        lantern = plugin.getCustomItemManager().getItem(CustomItem.SOUL_LANTERN);
    }

    public void soulLanternRecipe () {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "soul_lantern"), lantern);
        recipe.addIngredient(Material.SOUL_LANTERN);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void preventReCrafting (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        if (!result.isSimilar(lantern)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        for (ItemStack item : matrix) {
            if (item != null && isSoulLantern(item)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    public boolean isSoulLantern (ItemStack item) {
        if (item != null) {
            if (item.getType().equals((lantern).getType())) {
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_soul_lantern"), PersistentDataType.BYTE);
                        if (result != null) return result == (byte) 1;
                    }
                }
            }
        }
        return false;
    }
}
