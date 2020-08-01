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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Voidmatter extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;

    public Voidmatter(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void stickRecipe () {
        final ItemStack stick = CustomItem.VOIDMATTER_STICK.getItem();
        stick.setAmount(4);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_stick"), stick);
        recipe.shape("#", "#");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }

    public void stringRecipe () {
        final ItemStack string = CustomItem.VOIDMATTER_STRING.getItem();
        string.setAmount(2);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "voidmatter_string"), string);
        recipe.addIngredient(Material.STRUCTURE_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }

    public void helmetRecipe () {
        final ItemStack helmet = CustomItem.VOIDMATTER_HELMET.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_helmet"), helmet);
        recipe.shape("###", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chestplateRecipe () {
        final ItemStack chestplate = CustomItem.VOIDMATTER_CHESTPLATE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_chestplate"), chestplate);
        recipe.shape("# #", "###", "###");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void leggingsRecipe () {
        final ItemStack leggings = CustomItem.VOIDMATTER_LEGGINGS.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_leggings"), leggings);
        recipe.shape("###", "# #", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bootsRecipe () {
        final ItemStack boots = CustomItem.VOIDMATTER_BOOTS.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_boots"), boots);
        recipe.shape("# #", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bladeRecipe () {
        final ItemStack blade = CustomItem.VOIDMATTER_BLADE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_blade"), blade);
        recipe.shape("#", "#", "|");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bowRecipe () {
        final ItemStack bow = CustomItem.VOIDMATTER_BOW.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_bow"), bow);
        recipe.shape("@| ", "@ |", "@| ");
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void pickaxeRecipe () {
        final ItemStack pick = CustomItem.VOIDMATTER_PICKAXE.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_pickaxe"), pick);
        recipe.shape("###", " | ", " | ");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void shovelRecipe () {
        final ItemStack shovel = CustomItem.VOIDMATTER_SHOVEL.getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_shovel"), shovel);
        recipe.shape("#", "|", "|");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void axeRecipe () {
        final ItemStack axe = CustomItem.VOIDMATTER_AXE.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_axe"), axe);
        recipe.shape("##", "#|", " |");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }



    @EventHandler
    public void checkRecipes(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack helmet = CustomItem.VOIDMATTER_HELMET.getItem();
        final ItemStack chestplate = CustomItem.VOIDMATTER_CHESTPLATE.getItem();
        final ItemStack leggings = CustomItem.VOIDMATTER_LEGGINGS.getItem();
        final ItemStack boots = CustomItem.VOIDMATTER_BOOTS.getItem();
        final ItemStack stick = CustomItem.VOIDMATTER_STICK.getItem();
        final ItemStack string = CustomItem.VOIDMATTER_STRING.getItem();
        final ItemStack blade = CustomItem.VOIDMATTER_BLADE.getItem();
        final ItemStack bow = CustomItem.VOIDMATTER_BOW.getItem();
        final ItemStack pick = CustomItem.VOIDMATTER_PICKAXE.getItem();
        final ItemStack shovel = CustomItem.VOIDMATTER_SHOVEL.getItem();
        final ItemStack axe = CustomItem.VOIDMATTER_AXE.getItem();

        if (!result.isSimilar(helmet) && !result.isSimilar(chestplate) &&
                !result.isSimilar(leggings) && !result.isSimilar(boots) &&
                !result.isSimilar(stick) && !result.isSimilar(string) &&
                !result.isSimilar(blade) && !result.isSimilar(bow) &&
                !result.isSimilar(pick) && !result.isSimilar(shovel) &&
                !result.isSimilar(axe)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack voidmatter = CustomItem.VOIDMATTER.getItem();
        if (result.isSimilar(helmet) || result.isSimilar(chestplate) || result.isSimilar(leggings) || result.isSimilar(boots) || result.isSimilar(string) || result.isSimilar(stick)) {
            if (isMatching(matrix, voidmatter)) return;
        }
        else if (result.isSimilar(bow)) {
            if (isMatching(matrix, string, 0, 3, 6) && isMatching(matrix, stick, 1, 5, 7)) return;
        }
        else if (result.isSimilar(blade)) {
            if (isMatching(matrix, stick, 7) && isMatching(matrix, voidmatter, 1, 4)) return;
        }
        else if (result.isSimilar(pick)) {
            if (isMatching(matrix, stick, 4, 7) && isMatching(matrix, voidmatter, 0, 1, 2)) return;
        }
        else if (result.isSimilar(shovel)) {
            if (isMatching(matrix, stick, 4, 7) && isMatching(matrix, voidmatter, 1)) return;
        }
        else if (result.isSimilar(axe)) {
            if (isMatching(matrix, stick, 4, 7) && (isMatching(matrix, voidmatter, 0, 1, 3) || isMatching(matrix, voidmatter, 1, 2, 5))) return;
        }

        event.getInventory().setResult(null);
    }

    public boolean isVoidmatter (ItemStack item) {
        return isVoidmatterArmor(item) || isVoidmatterBow(item) || isVoidmatterBlade(item) || isVoidmatterTool(item);
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

    public boolean isVoidmatterBlade (ItemStack item) {
        if (item.getType().equals(CustomItem.VOIDMATTER_BLADE.getItem().getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_blade");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isBlade = container.get(key, PersistentDataType.BYTE);
                    if (isBlade != null) {
                        return isBlade == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public boolean isVoidmatterBow (ItemStack item) {
        if (item.getType().equals(CustomItem.VOIDMATTER_BOW.getItem().getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_bow");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isBow = container.get(key, PersistentDataType.BYTE);
                    if (isBow != null) {
                        return isBow == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public boolean isVoidmatterTool (ItemStack item) {
        return isVoidmatterPickaxe(item) || isVoidmatterShovel(item) || isVoidmatterAxe(item);
    }

    public boolean isVoidmatterPickaxe (ItemStack item) {
        if (item.getType().equals(CustomItem.VOIDMATTER_PICKAXE.getItem().getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_pickaxe");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isPick = container.get(key, PersistentDataType.BYTE);
                    if (isPick != null) {
                        return isPick == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public boolean isVoidmatterShovel (ItemStack item) {
        if (item.getType().equals(CustomItem.VOIDMATTER_SHOVEL.getItem().getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_shovel");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isShov = container.get(key, PersistentDataType.BYTE);
                    if (isShov != null) {
                        return isShov == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public boolean isVoidmatterAxe (ItemStack item) {
        if (item.getType().equals(CustomItem.VOIDMATTER_AXE.getItem().getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_voidmatter_axe");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isAxe = container.get(key, PersistentDataType.BYTE);
                    if (isAxe != null) {
                        return isAxe == (byte) 1;
                    }
                }
            }
        }
        return false;
    }


}
