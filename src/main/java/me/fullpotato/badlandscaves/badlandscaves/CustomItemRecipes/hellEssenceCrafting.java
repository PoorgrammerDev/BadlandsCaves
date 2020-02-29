package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class hellEssenceCrafting {
    private BadlandsCaves plugin;
    public hellEssenceCrafting(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void craft_hell_essence () {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        ItemStack hell_essence = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true));

        ShapelessRecipe hell_ess_craft = new ShapelessRecipe(new NamespacedKey(plugin, "hell_essence"), hell_essence);
        hell_ess_craft.addIngredient(Material.BLAZE_POWDER);
        hell_ess_craft.addIngredient(Material.MAGMA_CREAM);
        hell_ess_craft.addIngredient(Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(hell_ess_craft);
    }
}
