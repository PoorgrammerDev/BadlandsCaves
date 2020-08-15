package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

@SuppressWarnings("deprecation")

public class ResearchTableItems implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final ItemStack lens;
    private final ItemStack magnifyingGlass;
    private final ItemStack researchTable;

    public ResearchTableItems(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        lens = customItemManager.getItem(CustomItem.CONVEX_LENS);
        magnifyingGlass = customItemManager.getItem(CustomItem.MAGNIFYING_GLASS);
        researchTable = customItemManager.getItem(CustomItem.RESEARCH_TABLE);
    }

    public void convexLensRecipe () {
        final ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "convex_lens"), lens);
        recipe.shape("#  ", " # ", "#  ");
        recipe.setIngredient('#', Material.GLASS);

        plugin.getServer().addRecipe(recipe);
    }

    public void magnifyingGlassRecipe() {
        final ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "magnifying_glass"), magnifyingGlass);
        recipe.shape("###", "#*#", "###");
        recipe.setIngredient('#', Material.IRON_NUGGET);
        recipe.setIngredient('*', new RecipeChoice.ExactChoice(lens));

        plugin.getServer().addRecipe(recipe);
    }

    public void researchTableRecipe() {
        final ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "research_table"), researchTable);
        recipe.shape("-#-", "@&%", "_^_");
        recipe.setIngredient('-', Material.REDSTONE);
        recipe.setIngredient('#', Material.REDSTONE_LAMP);
        recipe.setIngredient('@', Material.CLOCK);
        recipe.setIngredient('&', new RecipeChoice.ExactChoice(magnifyingGlass));
        recipe.setIngredient('%', Material.COMPASS);
        recipe.setIngredient('_', new RecipeChoice.MaterialChoice(Material.OAK_SLAB, Material.ACACIA_SLAB, Material.BIRCH_SLAB, Material.SPRUCE_SLAB, Material.JUNGLE_SLAB, Material.DARK_OAK_SLAB));
        recipe.setIngredient('^', Material.LECTERN);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void enforceTechOnly (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null) {
            final ItemStack item = event.getRecipe().getResult();
            if (item.isSimilar(lens) || item.isSimilar(magnifyingGlass) || item.isSimilar(researchTable)) {
                if (!event.getViewers().isEmpty()) {
                    if (event.getViewers().get(0) instanceof Player) {
                        final Player player = (Player) event.getViewers().get(0);
                        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                            event.getInventory().setResult(null);
                        }
                    }
                }
            }
        }
    }


}
