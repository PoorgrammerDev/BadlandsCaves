package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ShieldCrafting {
    private BadlandsCaves plugin;

    public ShieldCrafting(BadlandsCaves plugin) {
        this.plugin = plugin;
    }


    public void craftRegularShield () {
        plugin.getServer().removeRecipe(new NamespacedKey(NamespacedKey.MINECRAFT, "shield"));

        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();
        meta.setDisplayName("§rWooden Shield");
        shield.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "wooden_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', new RecipeChoice.MaterialChoice(Material.OAK_PLANKS, Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.DARK_OAK_PLANKS, Material.JUNGLE_PLANKS, Material.SPRUCE_PLANKS));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStoneShield() {
        final ItemStack shield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.stone_shield").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "stone_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.COBBLESTONE);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftIronShield() {
        final ItemStack shield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.iron_shield").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "iron_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.IRON_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftDiamondShield() {
        final ItemStack shield = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.diamond_shield").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "diamond_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.DIAMOND);

        plugin.getServer().addRecipe(recipe);
    }


}
