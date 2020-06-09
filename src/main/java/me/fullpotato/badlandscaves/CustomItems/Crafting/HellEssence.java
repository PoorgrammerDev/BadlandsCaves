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

public class HellEssence extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    public HellEssence(BadlandsCaves bcav) {
        plugin = bcav;
    }

    public void craft_hell_essence () {
        boolean isHardmode = plugin.getConfig().getBoolean("system.hardmode");
        if (!isHardmode) return;

        final ItemStack hell_essence = CustomItem.HELL_ESSENCE.getItem();

        ShapelessRecipe hell_ess_craft = new ShapelessRecipe(new NamespacedKey(plugin, "hell_essence"), hell_essence);
        hell_ess_craft.addIngredient(Material.BLAZE_POWDER);
        hell_ess_craft.addIngredient(Material.MAGMA_CREAM);

        //pigman soul
        hell_ess_craft.addIngredient(Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(hell_ess_craft);
    }

    @EventHandler
    public void hell_ess (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack hell_essence = CustomItem.HELL_ESSENCE.getItem();
        if (!result.isSimilar(hell_essence)) return;

        final ItemStack pigzombie_soul = CustomItem.PIGZOMBIE_SOUL.getItem();

        if (!isMatching(event.getInventory().getMatrix(), pigzombie_soul)) {
            event.getInventory().setResult(null);
        }

    }


}
