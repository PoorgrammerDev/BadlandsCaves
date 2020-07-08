package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Loot.TreasureGear;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Corrosive extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    private String corrosiveLore = "§2Corrosive";
    private NamespacedKey hitsLeftKey;
    private Material[] swords = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
    };

    public Corrosive(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.hitsLeftKey = new NamespacedKey(plugin, "corrosiveHitsLeft");
    }

    public void craftCorrosiveSubstance() {
        final ItemStack corrosive_substance = CustomItem.CORROSIVE_SUBSTANCE.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "corrosive_substance"), corrosive_substance);

        /*
         * X**
         * *&*
         * **X
         *
         * where X is spider eye, * is tainted powder, and & is poison 2 potion
         * */
        recipe.shape("X**", "*&*", "**X");
        recipe.setIngredient('X', Material.SPIDER_EYE);
        recipe.setIngredient('*', Material.COMMAND_BLOCK);
        recipe.setIngredient('&', Material.POTION);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftCorrosiveSword() {
        final ItemStack corrosive_placeholder = CustomItem.CORROSIVE_PLACEHOLDER.getItem();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "corrosive_sword"), corrosive_placeholder);
        recipe.addIngredient(Material.COMMAND_BLOCK);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftCorrosiveArrow() {
        final ItemStack corrosive_arrow = CustomItem.CORROSIVE_ARROW.getItem();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "corrosive_arrow"), corrosive_arrow);
        recipe.addIngredient(Material.ARROW);
        recipe.addIngredient(Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);

    }

    @EventHandler
    public void craftSubstance (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack corrosive_substance = CustomItem.CORROSIVE_SUBSTANCE.getItem();
        if (!result.isSimilar(corrosive_substance)) return;

        final ItemStack tainted_powder = CustomItem.TAINTED_POWDER.getItem();
        final ItemStack poison_potion = getPoisonPotion();
        final ItemStack[] matrix = event.getInventory().getMatrix();

        ArrayList<ItemStack> taint = new ArrayList<>();
        taint.add(tainted_powder);

        if (((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, (Player) event.getViewers().get(0)) == 1) || !isMatching(matrix, taint) || !isMatching(matrix, poison_potion)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftArrow (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack arrow = CustomItem.CORROSIVE_ARROW.getItem();
            if (result.isSimilar(arrow)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                final ItemStack substance = CustomItem.CORROSIVE_SUBSTANCE.getItem();
                if (!isMatching(matrix, substance)) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void craftSword (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = CustomItem.CORROSIVE_PLACEHOLDER.getItem();
        if (!result.isSimilar(placeholder)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack corrosive_substance = CustomItem.CORROSIVE_SUBSTANCE.getItem();
        if (isMatching(matrix, corrosive_substance)) {
                ItemStack sword = null;
                for (ItemStack item : matrix) {
                    if (item != null && Arrays.asList(swords).contains(item.getType())) {
                        sword = item;
                        break;
                    }
                }
                if (sword != null) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, (Player) event.getViewers().get(0)) != 1) {
                    Voltshock voltshock = new Voltshock(plugin);
                    SerratedSwords serrated = new SerratedSwords(plugin);
                    TreasureGear treasureGear = new TreasureGear();
                    if (!voltshock.isVoltshock(sword) && !serrated.isSerrated(sword) && !treasureGear.isTreasureGear(sword)) {
                        if (isCorrosive(sword)) {
                            ItemStack output = sword.clone();
                            setHitsLeft(output, 10);
                            event.getInventory().setResult(output);
                            return;
                        }
                        else {
                            ItemStack output = sword.clone();
                            ItemMeta meta = output.getItemMeta();
                            meta.setCustomModelData(130);
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(corrosiveLore);
                            lore.add("");
                            meta.setLore(lore);
                            output.setItemMeta(meta);
                            setHitsLeft(output, 10);
                            event.getInventory().setResult(output);
                            return;
                        }
                    }
                }
            }
        }
        event.getInventory().setResult(null);
    }


    public boolean isCorrosive(ItemStack item) {
        if (Arrays.asList(swords).contains(item.getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null && lore.size() >= 1) {
                    return lore.get(0).equals(corrosiveLore);
                }
            }
        }
        return false;
    }

    public int getHitsLeft (ItemStack item) {
        if (isCorrosive(item)) {
            return item.getItemMeta().getPersistentDataContainer().get(hitsLeftKey, PersistentDataType.INTEGER);
        }
        return -1;
    }

    public void setHitsLeft (ItemStack item, int hits) {
        if (isCorrosive(item)) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(hitsLeftKey, PersistentDataType.INTEGER, hits);

            List<String> lore = meta.getLore();
            lore.set(1, "§7" + hits + " Hits Left");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public ItemStack getPoisonPotion() {
        ItemStack poison_potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) poison_potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.POISON, false, true));
        poison_potion.setItemMeta(meta);

        return poison_potion;
    }
}
