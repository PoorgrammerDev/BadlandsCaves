package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;

public class CorrosiveSubstance extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public CorrosiveSubstance(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craft_corrosive_substance (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack corrosive_substance = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true));
        if (!result.isSimilar(corrosive_substance)) return;

        final ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));
        final ItemStack poison_potion = getPoisonPotion();
        final ItemStack[] matrix = event.getInventory().getMatrix();

        ArrayList<ItemStack> taint = new ArrayList<>();
        taint.add(tainted_powder);

        if (!isMatching(matrix, taint) || !isMatching(matrix, poison_potion)) {
            plugin.getServer().broadcastMessage("asdh9a8uyrfw");
            event.getInventory().setResult(null);
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
