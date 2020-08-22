package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

@SuppressWarnings("deprecation")
public class NebuliteInstallerSmelt {
    private final BadlandsCaves plugin;
    private final ItemStack installer;
    private final ItemStack titaniumFragment;

    public NebuliteInstallerSmelt(BadlandsCaves plugin) {
        this.plugin = plugin;
        installer = plugin.getCustomItemManager().getItem(CustomItem.NEBULITE_INSTALLER);

        titaniumFragment = plugin.getCustomItemManager().getItem(CustomItem.TITANIUM_FRAGMENT);
        titaniumFragment.setAmount(2);
    }

    public void deconstructRecipe () {
        final FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(plugin, "nebulite_installer_deconstruct"), titaniumFragment, new RecipeChoice.ExactChoice(installer), 1, 200);
        plugin.getServer().addRecipe(furnaceRecipe);
    }
}
