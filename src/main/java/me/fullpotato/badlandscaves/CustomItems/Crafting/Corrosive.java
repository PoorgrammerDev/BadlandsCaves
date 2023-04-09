package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.EnergyCore;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
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
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final String corrosiveLore = "§2Corrosive";
    private final NamespacedKey hitsLeftKey;
    private final StarlightTools starlightsword;
    private final EnergyCore energyCore;
    private final TreasureGear treasureGear = new TreasureGear();
    private final Material[] swords = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
    };

    public Corrosive(BadlandsCaves plugin, StarlightTools starlightsword, EnergyCore energyCore) {
        this.plugin = plugin;
        this.hitsLeftKey = new NamespacedKey(plugin, "corrosiveHitsLeft");
        customItemManager = plugin.getCustomItemManager();
        this.starlightsword = starlightsword;
        this.energyCore = energyCore;
    }

    public void craftCorrosiveSubstance() {
        final ItemStack corrosive_substance = customItemManager.getItem(CustomItem.CORROSIVE_SUBSTANCE);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "corrosive_substance"), corrosive_substance);

        /*
         * unshaped recipe: one water bottle with one corrosive substance
         * */
        recipe.addIngredient(Material.COMMAND_BLOCK);
        recipe.addIngredient(Material.POTION);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftCorrosiveSword() {
        final ItemStack corrosive_placeholder = customItemManager.getItem(CustomItem.CORROSIVE_PLACEHOLDER);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "corrosive_sword"), corrosive_placeholder);
        recipe.addIngredient(Material.COMMAND_BLOCK);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftCorrosiveArrow() {
        final ItemStack corrosive_arrow = customItemManager.getItem(CustomItem.CORROSIVE_ARROW);
        corrosive_arrow.setAmount(8);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "corrosive_arrow"), corrosive_arrow);

        /*
         * ###
         * #X#
         * ###
         * 
         * where # is arrow ; X is corrosive substance
         */
        recipe.shape("###","#X#","###");
        recipe.setIngredient('#', Material.ARROW);
        recipe.setIngredient('X', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);

    }

    @EventHandler
    public void craftSubstance (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack corrosive_substance = customItemManager.getItem(CustomItem.CORROSIVE_SUBSTANCE);
        if (!result.isSimilar(corrosive_substance)) return;

        final ItemStack tainted_powder = customItemManager.getItem(CustomItem.TAINTED_POWDER);
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
            final ItemStack arrow = customItemManager.getItem(CustomItem.CORROSIVE_ARROW);
            if (result.isSimilar(arrow)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                final ItemStack substance = customItemManager.getItem(CustomItem.CORROSIVE_SUBSTANCE);
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
        final ItemStack placeholder = customItemManager.getItem(CustomItem.CORROSIVE_PLACEHOLDER);
        if (!result.isSimilar(placeholder)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack corrosive_substance = customItemManager.getItem(CustomItem.CORROSIVE_SUBSTANCE);
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
                    Voltshock voltshock = new Voltshock(plugin, energyCore, starlightsword);
                    SerratedSwords serrated = new SerratedSwords(plugin, energyCore, starlightsword);
                    if (!voltshock.isVoltshock(sword) && !serrated.isSerrated(sword) && !treasureGear.isTreasureGear(sword)&& !starlightsword.isStarlightSaber(sword)) {
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
