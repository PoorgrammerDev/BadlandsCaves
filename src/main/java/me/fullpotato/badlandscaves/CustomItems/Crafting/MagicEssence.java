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

public class MagicEssence extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    public MagicEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void magic_essence_craft () {
        final ItemStack magic_essence = CustomItem.MAGIC_ESSENCE.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "magic_essence"), magic_essence);

        /* SHAPE:
         *  ###
         *  #*#
         *  ###
         * where # = lapis, * = witch soul
         */
        recipe.shape("###", "#*#", "###");
        recipe.setIngredient('#', Material.LAPIS_LAZULI);
        recipe.setIngredient('*', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void magic_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack magic_essence = CustomItem.MAGIC_ESSENCE.getItem();
        if (!result.isSimilar(magic_essence)) return;

        final ItemStack witch_soul = CustomItem.WITCH_SOUL.getItem();

        if (!isMatching(event.getInventory().getMatrix(), witch_soul)) {
            event.getInventory().setResult(null);
        }
    }
}
