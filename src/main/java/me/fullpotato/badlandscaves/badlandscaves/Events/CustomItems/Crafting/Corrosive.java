package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
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
    };

    public Corrosive(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.hitsLeftKey = new NamespacedKey(plugin, "corrosiveHitsLeft");
    }

    @EventHandler
    public void craftSubstance (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack corrosive_substance = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true));
        if (!result.isSimilar(corrosive_substance)) return;

        final ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));
        final ItemStack poison_potion = getPoisonPotion();
        final ItemStack[] matrix = event.getInventory().getMatrix();

        ArrayList<ItemStack> taint = new ArrayList<>();
        taint.add(tainted_powder);

        if (event.getViewers().get(0).getMetadata("has_supernatural_powers").get(0).asBoolean() || !isMatching(matrix, taint) || !isMatching(matrix, poison_potion)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftSword (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_placeholder").getValues(true));
        if (!result.isSimilar(placeholder)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack corrosive_substance = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true));
        if (isMatching(matrix, corrosive_substance)) {
                ItemStack sword = null;
                for (ItemStack item : matrix) {
                    if (item != null && Arrays.asList(swords).contains(item.getType())) {
                        sword = item;
                        break;
                    }
                }
                if (sword != null) {
                    if (!event.getViewers().get(0).getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                    Voltshock voltshock = new Voltshock(plugin);
                    SerratedSwords serrated = new SerratedSwords(plugin);
                    if (!voltshock.isVoltshock(sword) && !serrated.isSerrated(sword)) {
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
