package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

public class Apples extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public Apples(BadlandsCaves plugin) {
        this.plugin = plugin;
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
        final ItemStack blessed_apple = CustomItem.BLESSED_APPLE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "blessed_apple"), blessed_apple);
        recipe.shape("###", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftEnchantedBlessedApple() {
        final ItemStack enchanted_blessed_apple = CustomItem.ENCHANTED_BLESSED_APPLE.getItem();

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
        final ItemStack blessed_apple = CustomItem.BLESSED_APPLE.getItem();
        final ItemStack enchanted_blessed_apple = CustomItem.ENCHANTED_BLESSED_APPLE.getItem();
        if (!result.isSimilar(blessed_apple) && !result.isSimilar(enchanted_blessed_apple)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        if (matrix[4].isSimilar(blessed_apple) || matrix[4].isSimilar(enchanted_blessed_apple)) {
            event.getInventory().setResult(null);
            return;
        }

        final ItemStack purge_essence = CustomItem.PURGE_ESSENCE.getItem();
        if (!isMatching(matrix, purge_essence, 3) || !isMatching(matrix, purge_essence, 5)) {
            event.getInventory().setResult(null);
            return;
        }

        ArrayList<ItemStack> souls = new ArrayList<>();
        souls.add(CustomItem.ZOMBIE_SOUL.getItem());
        souls.add(CustomItem.CREEPER_SOUL.getItem());
        souls.add(CustomItem.SPIDER_SOUL.getItem());
        souls.add(CustomItem.SKELETON_SOUL.getItem());
        souls.add(CustomItem.GHAST_SOUL.getItem());
        souls.add(CustomItem.SILVERFISH_SOUL.getItem());
        souls.add(CustomItem.PHANTOM_SOUL.getItem());
        souls.add(CustomItem.PIGZOMBIE_SOUL.getItem());
        souls.add(CustomItem.WITCH_SOUL.getItem());

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
