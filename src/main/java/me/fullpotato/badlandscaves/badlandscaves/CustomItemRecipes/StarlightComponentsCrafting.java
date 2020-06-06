package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public class StarlightComponentsCrafting {
    private BadlandsCaves plugin;

    public StarlightComponentsCrafting(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void craftBinding () {
        ItemStack binding = CustomItem.BINDING.getItem();
        binding.setAmount(8);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "raw_binding"), binding);
        recipe.shape("#@", "@#");

        recipe.setIngredient('@', Material.GRAVEL);
        recipe.setIngredient('#', new RecipeChoice.MaterialChoice(Material.SAND, Material.RED_SAND));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftGoldCable () {
        ItemStack cable = CustomItem.GOLDEN_CABLE.getItem();
        cable.setAmount(6);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "golden_cable"), cable);
        recipe.shape("###", "@@@", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLD_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftNetherStarFragment() {
        ItemStack fragment = CustomItem.NETHER_STAR_FRAGMENT.getItem();
        fragment.setAmount(4);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "nether_star_fragment"), fragment);
        recipe.addIngredient(Material.NETHER_STAR);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightCircuit () {
        ItemStack circuit = CustomItem.STARLIGHT_CIRCUIT.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_circuit"), circuit);
        recipe.shape("---", "#@#", "---");

        recipe.setIngredient('-', Material.COMMAND_BLOCK);
        recipe.setIngredient('#', Material.REDSTONE);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightBattery() {
        ItemStack battery = CustomItem.STARLIGHT_BATTERY.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_battery"), battery);
        recipe.shape(" % ", "#@#", "#@#");

        recipe.setIngredient('%', Material.COMPARATOR);
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightModule() {
        ItemStack module = CustomItem.STARLIGHT_MODULE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_module"), module);
        recipe.shape("###", "#@#", "#%#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('%', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }


}
