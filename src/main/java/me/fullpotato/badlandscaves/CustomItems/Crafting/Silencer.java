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

public class Silencer extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;

    public Silencer(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void wavelengthDisruptorRecipe () {
        final ItemStack disruptor = CustomItem.WAVELENGTH_DISRUPTOR.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "wavelength_disruptor"), disruptor);
        recipe.shape("#*#", "#@#", "#*#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK); //witch soul
        recipe.setIngredient('*', Material.STRUCTURE_BLOCK); //voidmatter
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK); //photon emitter

        plugin.getServer().addRecipe(recipe);
    }

    public void silencerRecipe () {
        final ItemStack silencer = CustomItem.SILENCER.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "silencer"), silencer);
        recipe.shape("#@#", "#%#", "###");

        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('%', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void enforceDisruptorRecipe (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack disruptor = CustomItem.WAVELENGTH_DISRUPTOR.getItem();
        if (!result.isSimilar(disruptor)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack witch_soul = CustomItem.WITCH_SOUL.getItem();
        final ItemStack voidmatter = CustomItem.VOIDMATTER.getItem();
        final ItemStack photon_emitter = CustomItem.PHOTON_EMITTER.getItem();

        if (!isMatching(matrix, photon_emitter, 4) ||
                !isMatching(matrix, witch_soul, 0, 2, 3, 5, 6, 8) ||
                !isMatching(matrix, voidmatter, 1, 7)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void enforceSilencerRecipe (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack silencer = CustomItem.SILENCER.getItem();
        if (!result.isSimilar(silencer)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium = CustomItem.TITANIUM_INGOT.getItem();
        final ItemStack battery = CustomItem.STARLIGHT_BATTERY.getItem();
        final ItemStack wavelength_disruptor = CustomItem.WAVELENGTH_DISRUPTOR.getItem();

        if (!isMatching(matrix, wavelength_disruptor, 1) ||
                !isMatching(matrix, titanium) ||
                !isMatching(matrix, battery, 4)) {
            event.getInventory().setResult(null);
        }
    }
}
