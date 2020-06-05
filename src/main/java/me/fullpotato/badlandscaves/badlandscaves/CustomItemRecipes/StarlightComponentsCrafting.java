package me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public class StarlightComponentsCrafting {
    private BadlandsCaves plugin;

    public StarlightComponentsCrafting(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void craftBinding () {
        ItemStack binding = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.binding").getValues(true));
        binding.setAmount(8);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "raw_binding"), binding);
        recipe.shape("#@", "@#");

        recipe.setIngredient('@', Material.GRAVEL);
        recipe.setIngredient('#', new RecipeChoice.MaterialChoice(Material.SAND, Material.RED_SAND));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftGoldCable () {
        ItemStack cable = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.golden_cable").getValues(true));
        cable.setAmount(6);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "golden_cable"), cable);
        recipe.shape("###", "@@@", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLD_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftNetherStarFragment() {
        ItemStack fragment = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.nether_star_fragment").getValues(true));
        fragment.setAmount(4);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "nether_star_fragment"), fragment);
        recipe.addIngredient(Material.NETHER_STAR);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightCircuit () {
        ItemStack circuit = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_circuit").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_circuit"), circuit);
        recipe.shape("---", "#@#", "---");

        recipe.setIngredient('-', Material.COMMAND_BLOCK);
        recipe.setIngredient('#', Material.REDSTONE);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightBattery() {
        ItemStack battery = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_battery").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_battery"), battery);
        recipe.shape(" % ", "#@#", "#@#");

        recipe.setIngredient('%', Material.COMPARATOR);
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightModule() {
        ItemStack module = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_module").getValues(true));

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_module"), module);
        recipe.shape("###", "#@#", "#%#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('%', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }


}
