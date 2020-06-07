package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Shield implements Listener {
    private BadlandsCaves plugin;

    public Shield(BadlandsCaves plugin) {
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
        final ItemStack shield = CustomItem.STONE_SHIELD.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "stone_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.COBBLESTONE);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftIronShield() {
        final ItemStack shield = CustomItem.IRON_SHIELD.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "iron_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.IRON_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftDiamondShield() {
        final ItemStack shield = CustomItem.DIAMOND_SHIELD.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "diamond_shield"), shield);
        recipe.shape("# #", "###", " # ");
        recipe.setIngredient('#', Material.DIAMOND);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void craftShield(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack stoneShield = CustomItem.STONE_SHIELD.getItem();
        final ItemStack ironShield = CustomItem.IRON_SHIELD.getItem();
        final ItemStack diamondShield = CustomItem.DIAMOND_SHIELD.getItem();

        if (result.isSimilar(stoneShield) || result.isSimilar(ironShield) || result.isSimilar(diamondShield)) {
            for (HumanEntity humanEntity : event.getViewers()) {
                Player player = (Player) humanEntity;
                if (player.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
    }
}
