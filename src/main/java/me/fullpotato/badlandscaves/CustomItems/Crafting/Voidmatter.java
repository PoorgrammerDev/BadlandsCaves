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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Voidmatter extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final ItemStack voidmatter;
    private final List<Material> types;
    private final ItemStack stick;
    private final ItemStack string;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final ItemStack blade;
    private final ItemStack bow;
    private final ItemStack pick;
    private final ItemStack shovel;
    private final ItemStack axe;
    private final ItemStack repairPlaceholder;
    private final ItemStack magicEssence;
    private final ItemStack mergedSouls;

    public Voidmatter(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        voidmatter = customItemManager.getItem(CustomItem.VOIDMATTER);

        stick = customItemManager.getItem(CustomItem.VOIDMATTER_STICK);
        string = customItemManager.getItem(CustomItem.VOIDMATTER_STRING);
        helmet = customItemManager.getItem(CustomItem.VOIDMATTER_HELMET);
        chestplate = customItemManager.getItem(CustomItem.VOIDMATTER_CHESTPLATE);
        leggings = customItemManager.getItem(CustomItem.VOIDMATTER_LEGGINGS);
        boots = customItemManager.getItem(CustomItem.VOIDMATTER_BOOTS);
        blade = customItemManager.getItem(CustomItem.VOIDMATTER_BLADE);
        bow = customItemManager.getItem(CustomItem.VOIDMATTER_BOW);
        pick = customItemManager.getItem(CustomItem.VOIDMATTER_PICKAXE);
        shovel = customItemManager.getItem(CustomItem.VOIDMATTER_SHOVEL);
        axe = customItemManager.getItem(CustomItem.VOIDMATTER_AXE);
        repairPlaceholder = customItemManager.getItem(CustomItem.VOIDMATTER_REPAIR_PLACEHOLDER);
        magicEssence = customItemManager.getItem(CustomItem.MAGIC_ESSENCE);
        mergedSouls = customItemManager.getItem(CustomItem.MERGED_SOULS);

        stick.setAmount(4);
        string.setAmount(2);

        types = new ArrayList<>();
        types.add(helmet.getType());
        types.add(chestplate.getType());
        types.add(leggings.getType());
        types.add(boots.getType());
        types.add(blade.getType());
        types.add(bow.getType());
        types.add(pick.getType());
        types.add(shovel.getType());
        types.add(axe.getType());
    }

    public void stickRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_stick"), stick);
        recipe.shape("#", "#");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }

    public void stringRecipe () {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "voidmatter_string"), string);
        recipe.addIngredient(Material.STRUCTURE_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }

    public void helmetRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_helmet"), helmet);
        recipe.shape("###", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chestplateRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_chestplate"), chestplate);
        recipe.shape("# #", "###", "###");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void leggingsRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_leggings"), leggings);
        recipe.shape("###", "# #", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bootsRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_boots"), boots);
        recipe.shape("# #", "# #");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bladeRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_blade"), blade);
        recipe.shape("#", "#", "|");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bowRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_bow"), bow);
        recipe.shape("@| ", "@ |", "@| ");
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void pickaxeRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_pickaxe"), pick);
        recipe.shape("###", " | ", " | ");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void shovelRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_shovel"), shovel);
        recipe.shape("#", "|", "|");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void axeRecipe () {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voidmatter_axe"), axe);
        recipe.shape("##", "#|", " |");
        recipe.setIngredient('#', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void repairRecipe () {
        final ItemStack repairPlaceholder = customItemManager.getItem(CustomItem.VOIDMATTER_REPAIR_PLACEHOLDER);
        final ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "voidmatter_repair"), repairPlaceholder);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(types));
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.STRUCTURE_BLOCK, Material.COMMAND_BLOCK));

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void checkRecipes(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack helmet = customItemManager.getItem(CustomItem.VOIDMATTER_HELMET);
        final ItemStack chestplate = customItemManager.getItem(CustomItem.VOIDMATTER_CHESTPLATE);
        final ItemStack leggings = customItemManager.getItem(CustomItem.VOIDMATTER_LEGGINGS);
        final ItemStack boots = customItemManager.getItem(CustomItem.VOIDMATTER_BOOTS);
        final ItemStack stick = customItemManager.getItem(CustomItem.VOIDMATTER_STICK);
        final ItemStack string = customItemManager.getItem(CustomItem.VOIDMATTER_STRING);
        final ItemStack blade = customItemManager.getItem(CustomItem.VOIDMATTER_BLADE);
        final ItemStack bow = customItemManager.getItem(CustomItem.VOIDMATTER_BOW);
        final ItemStack pick = customItemManager.getItem(CustomItem.VOIDMATTER_PICKAXE);
        final ItemStack shovel = customItemManager.getItem(CustomItem.VOIDMATTER_SHOVEL);
        final ItemStack axe = customItemManager.getItem(CustomItem.VOIDMATTER_AXE);

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

    @EventHandler
    public void checkRepairRecipe (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            if (!result.isSimilar(repairPlaceholder)) return;

            final ItemStack[] matrix = event.getInventory().getMatrix();
            ItemStack voidmatterTool = null;
            ItemStack repairElement = null;
            double repairAmt = -1;

            for (ItemStack item : matrix) {
                if (item != null) {
                    if (isVoidmatter(item)) {
                        voidmatterTool = item;
                    }
                    else if (item.isSimilar(this.voidmatter)) {
                        repairElement = item;
                        repairAmt = 0.5;
                    }
                    else if (item.isSimilar(mergedSouls)) {
                        repairElement = item;
                        repairAmt = 0.25;
                    }
                    else if (item.isSimilar(magicEssence)) {
                        repairElement = item;
                        repairAmt = 0.125;
                    }
                }
            }

            if (repairElement == null || voidmatterTool == null) return;
            final ItemStack newResult = voidmatterTool.clone();
            if (newResult.getItemMeta() instanceof Damageable) {
                final Damageable meta = (Damageable) newResult.getItemMeta();
                meta.setDamage((int) Math.max(meta.getDamage() - (newResult.getType().getMaxDurability() * repairAmt), 0));
                newResult.setItemMeta((ItemMeta) meta);
            }
            event.getInventory().setResult(newResult);
        }
    }

    public boolean isVoidmatter (ItemStack item) {
        return isVoidmatterArmor(item) || isVoidmatterBow(item) || isVoidmatterBlade(item) || isVoidmatterTool(item);
    }

    public boolean isVoidmatterArmor (ItemStack item) {
        ArrayList<Material> armorTypes = new ArrayList<>();
        armorTypes.add(customItemManager.getItem(CustomItem.VOIDMATTER_HELMET).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.VOIDMATTER_CHESTPLATE).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.VOIDMATTER_LEGGINGS).getType());
        armorTypes.add(customItemManager.getItem(CustomItem.VOIDMATTER_BOOTS).getType());

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
        if (item.getType().equals(customItemManager.getItem(CustomItem.VOIDMATTER_BLADE).getType())) {
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
        if (item.getType().equals(customItemManager.getItem(CustomItem.VOIDMATTER_BOW).getType())) {
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
        if (item.getType().equals(customItemManager.getItem(CustomItem.VOIDMATTER_PICKAXE).getType())) {
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
        if (item.getType().equals(customItemManager.getItem(CustomItem.VOIDMATTER_SHOVEL).getType())) {
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
        if (item.getType().equals(customItemManager.getItem(CustomItem.VOIDMATTER_AXE).getType())) {
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
