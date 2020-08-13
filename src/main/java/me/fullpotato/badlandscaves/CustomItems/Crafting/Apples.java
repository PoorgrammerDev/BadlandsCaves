package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

public class Apples extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;

    public Apples(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
    }

    public void craftNotchApple() {
        ItemStack notch_apple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);

        ShapedRecipe notch_apple_recipe = new ShapedRecipe(new NamespacedKey(plugin, "notch_apple"), notch_apple);

        /*SHAPE:
         *  ###
         *  #@#
         *  ###
         *
         * where # = gold blocks, @ = apple
         * */
        notch_apple_recipe.shape("###","#@#","###");
        notch_apple_recipe.setIngredient('#', Material.GOLD_BLOCK);
        notch_apple_recipe.setIngredient('@', Material.APPLE);

        plugin.getServer().addRecipe(notch_apple_recipe);
    }

    public void craftBlessedApple() {
        final ItemStack blessed_apple = customItemManager.getItem(CustomItem.BLESSED_APPLE);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "blessed_apple"), blessed_apple);
        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftEnchantedBlessedApple() {
        final ItemStack enchanted_blessed_apple = customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "enchanted_blessed_apple"), enchanted_blessed_apple);


        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.ENCHANTED_GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void blessedApple (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack blessed_apple = customItemManager.getItem(CustomItem.BLESSED_APPLE);
        final ItemStack enchanted_blessed_apple = customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE);
        if (!result.isSimilar(blessed_apple) && !result.isSimilar(enchanted_blessed_apple)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        if (matrix[4].isSimilar(blessed_apple) || matrix[4].isSimilar(enchanted_blessed_apple)) {
            event.getInventory().setResult(null);
            return;
        }

        final ItemStack purge_essence = customItemManager.getItem(CustomItem.PURGE_ESSENCE);
        if (!isMatching(matrix, purge_essence, 3, 5)) {
            event.getInventory().setResult(null);
            return;
        }

        ArrayList<ItemStack> souls = new ArrayList<>();
        souls.add(customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        souls.add(customItemManager.getItem(CustomItem.CREEPER_SOUL));
        souls.add(customItemManager.getItem(CustomItem.SPIDER_SOUL));
        souls.add(customItemManager.getItem(CustomItem.SKELETON_SOUL));
        souls.add(customItemManager.getItem(CustomItem.GHAST_SOUL));
        souls.add(customItemManager.getItem(CustomItem.SILVERFISH_SOUL));
        souls.add(customItemManager.getItem(CustomItem.PHANTOM_SOUL));
        souls.add(customItemManager.getItem(CustomItem.PIGZOMBIE_SOUL));
        souls.add(customItemManager.getItem(CustomItem.WITCH_SOUL));

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
