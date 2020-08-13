package me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.MatchCrafting;
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
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class StarlightArmor extends MatchCrafting implements Listener {
    protected final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;

    public StarlightArmor(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
    }

    public void helmetRecipe() {
        final ItemStack item = customItemManager.getItem(CustomItem.STARLIGHT_HELMET);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_helmet"), item);
        recipe.shape("#@#", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chestplateRecipe() {
        final ItemStack item = customItemManager.getItem(CustomItem.STARLIGHT_CHESTPLATE);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_chestplate"), item);
        recipe.shape("# #", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void leggingsRecipe() {
        final ItemStack item = customItemManager.getItem(CustomItem.STARLIGHT_LEGGINGS);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_leggings"), item);
        recipe.shape("#@#", "# #", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bootsRecipe() {
        final ItemStack item = customItemManager.getItem(CustomItem.STARLIGHT_BOOTS);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_boots"), item);
        recipe.shape("# #", "#@#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void checkCraftingRecipes (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_helmet = customItemManager.getItem(CustomItem.STARLIGHT_HELMET);
        final ItemStack starlight_chestplate = customItemManager.getItem(CustomItem.STARLIGHT_CHESTPLATE);
        final ItemStack starlight_leggings = customItemManager.getItem(CustomItem.STARLIGHT_LEGGINGS);
        final ItemStack starlight_boots = customItemManager.getItem(CustomItem.STARLIGHT_BOOTS);

        if (!result.isSimilar(starlight_helmet) &&
                !result.isSimilar(starlight_chestplate) &&
                !result.isSimilar(starlight_leggings) &&
                !result.isSimilar(starlight_boots)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);
        final ItemStack module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);
        if (!isMatching(matrix, titanium_ingot) || !isMatching(matrix, module)) {
            event.getInventory().setResult(null);
        }
    }

    public boolean isStarlightArmor(ItemStack item) {
        ArrayList<Material> armorTypes = new ArrayList<>();
        armorTypes.add(customItemManager.getItem(CustomItem.STARLIGHT_HELMET).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.STARLIGHT_CHESTPLATE).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.STARLIGHT_LEGGINGS).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.STARLIGHT_BOOTS).getType());

        if (armorTypes.contains(item.getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_starlight_armor");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isArmor = container.get(key, PersistentDataType.BYTE);
                    if (isArmor != null) {
                        return isArmor == (byte) 1;
                    }
                }
            }
        }
        return false;
    }
}
