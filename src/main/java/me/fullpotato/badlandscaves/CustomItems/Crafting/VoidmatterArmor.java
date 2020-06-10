package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
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

public class VoidmatterArmor extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;

    public VoidmatterArmor(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void helmetRecipe () {
        final ItemStack helmet = CustomItem.VOIDMATTER_HELMET.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_helmet"), helmet);
        recipe.shape("###", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chestplateRecipe () {
        final ItemStack chestplate = CustomItem.VOIDMATTER_CHESTPLATE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_chestplate"), chestplate);
        recipe.shape("# #", "###", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void leggingsRecipe () {
        final ItemStack leggings = CustomItem.VOIDMATTER_LEGGINGS.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_leggings"), leggings);
        recipe.shape("###", "# #", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bootsRecipe () {
        final ItemStack boots = CustomItem.VOIDMATTER_BOOTS.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_boots"), boots);
        recipe.shape("# #", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void checkRecipes(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack voidmatter_helmet = CustomItem.VOIDMATTER_HELMET.getItem();
        final ItemStack voidmatter_chestplate = CustomItem.VOIDMATTER_CHESTPLATE.getItem();
        final ItemStack voidmatter_leggings = CustomItem.VOIDMATTER_LEGGINGS.getItem();
        final ItemStack voidmatter_boots = CustomItem.VOIDMATTER_BOOTS.getItem();

        if (!result.isSimilar(voidmatter_helmet) &&
                !result.isSimilar(voidmatter_chestplate) &&
                !result.isSimilar(voidmatter_leggings) &&
                !result.isSimilar(voidmatter_boots)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack voidmatter = CustomItem.VOIDMATTER.getItem();
        if (!isMatching(matrix, voidmatter)) {
            event.getInventory().setResult(null);
        }
    }

    public boolean isVoidmatterArmor (ItemStack item) {
        ArrayList<Material> armorTypes = new ArrayList<>();
        armorTypes.add(CustomItem.VOIDMATTER_HELMET.getItem().getType());
        armorTypes.add(CustomItem.VOIDMATTER_CHESTPLATE.getItem().getType());
        armorTypes.add(CustomItem.VOIDMATTER_LEGGINGS.getItem().getType());
        armorTypes.add(CustomItem.VOIDMATTER_BOOTS.getItem().getType());

        if (armorTypes.contains(item.getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_armor");
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
