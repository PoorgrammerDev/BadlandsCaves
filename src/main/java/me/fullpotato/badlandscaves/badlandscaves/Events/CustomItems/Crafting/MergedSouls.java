package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MergedSouls extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;
    public MergedSouls(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void merged_souls (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack merged_souls = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true));

        if (!result.isSimilar(merged_souls)) return;

        HashMap<ItemStack, Integer> souls = new HashMap<>();
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)), 1);
        souls.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)), 1);


        if (!isMatching(event.getInventory().getMatrix(), souls)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftSound (CraftItemEvent event) {
        final ItemStack merged_souls = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true));
        if (event.getRecipe().getResult().isSimilar(merged_souls)) {
            for (HumanEntity human : event.getViewers()) {
                if (human instanceof Player) {
                    Player player = (Player) human;
                    player.playSound(player.getLocation(), "custom.darkrooms_whispers", SoundCategory.BLOCKS, 0.05F, 1);
                }
            }
        }
    }

}
