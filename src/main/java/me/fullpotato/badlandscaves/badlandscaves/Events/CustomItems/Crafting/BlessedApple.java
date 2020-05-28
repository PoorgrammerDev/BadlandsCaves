package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlessedApple extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public BlessedApple(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void blessedApple (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack blessed_apple = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.blessed_apple").getValues(true));
        final ItemStack enchanted_blessed_apple = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enchanted_blessed_apple").getValues(true));
        if (!result.isSimilar(blessed_apple) && !result.isSimilar(enchanted_blessed_apple)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        if (matrix[4].isSimilar(blessed_apple) || matrix[4].isSimilar(enchanted_blessed_apple)) {
            event.getInventory().setResult(null);
            return;
        }

        final ItemStack purge_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true));
        if (!isMatching(matrix, purge_essence, 3) || !isMatching(matrix, purge_essence, 5)) {
            event.getInventory().setResult(null);
            return;
        }

        ArrayList<ItemStack> souls = new ArrayList<>();
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)));
        souls.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)));

        int[] slots = {0,1,2,6,7,8};
        for (int slot : slots) {
            ItemStack item = matrix[slot].clone();
            item.setAmount(1);
            if (!souls.contains(item)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }
}
