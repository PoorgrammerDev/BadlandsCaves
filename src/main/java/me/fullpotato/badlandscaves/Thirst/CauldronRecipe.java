package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CauldronRecipe {
    PURIFIED_WATER_PREHARDMODE(RecipeAvailability.PREHARDMODE_ONLY, "Purified Water", CustomItem.PURIFIED_WATER, new ItemStack(Material.GLASS_BOTTLE), new ItemStack(Material.BLAZE_POWDER)),
    PURIFIED_WATER_HARDMODE(RecipeAvailability.HARDMODE_ONLY, "Purified Water", CustomItem.PURIFIED_WATER, new ItemStack(Material.GLASS_BOTTLE), CustomItem.HELL_ESSENCE.getItem()),
    ANTIDOTE(RecipeAvailability.ALWAYS, "Antidote", CustomItem.ANTIDOTE, new ItemStack(Material.GLASS_BOTTLE), CustomItem.PURGE_ESSENCE.getItem()),
    TAINTED_POWDER(RecipeAvailability.ALWAYS, "Tainted Powder", CustomItem.TAINTED_POWDER, new ItemStack(Material.SUGAR), new ItemStack(Material.BONE_MEAL)),
    MANA_POTION(RecipeAvailability.ALWAYS, "Mana Potion", CustomItem.MANA_POTION, new ItemStack(Material.GLASS_BOTTLE), CustomItem.MAGIC_ESSENCE.getItem());

    private final RecipeAvailability recipeAvailability;
    private final String displayName;
    private final CustomItem result;
    private final Set<ItemStack> ingredients = new HashSet<>();

    CauldronRecipe(RecipeAvailability recipeAvailability, String displayName, CustomItem result, ItemStack... ingredients) {
        this.recipeAvailability = recipeAvailability;
        this.displayName = displayName;
        this.result = result;
        this.ingredients.addAll(Arrays.asList(ingredients));
    }

    public Set<ItemStack> getIngredients() {
        return this.ingredients;
    }

    public CustomItem getResult() {
        return this.result;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RecipeAvailability getRecipeAvailability() {
        return recipeAvailability;
    }
}
