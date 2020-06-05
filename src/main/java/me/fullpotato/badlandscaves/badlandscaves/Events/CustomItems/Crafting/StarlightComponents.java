package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class StarlightComponents extends MatchCrafting implements Listener {
    private BadlandsCaves plugin;

    public StarlightComponents(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craftCable (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack golden_cable = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.golden_cable").getValues(true));
        if (!result.isSimilar(golden_cable)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack binding = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.binding").getValues(true));
        if (!isMatching(matrix, binding)) event.getInventory().setResult(null);
    }

    @EventHandler
    public void craftCircuit (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_circuit = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_circuit").getValues(true));
        if (!result.isSimilar(starlight_circuit)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack golden_cable = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.golden_cable").getValues(true));
        final ItemStack nether_star_fragment = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.nether_star_fragment").getValues(true));

        boolean matches = true;
        final int[] slots = {0, 1, 2, 6, 7, 8};
        for (int slot : slots) {
            if (!matrix[slot].isSimilar(golden_cable)) {
                matches = false;
            }
        }

        if (matches) {
            if (!isMatching(matrix, nether_star_fragment, 4)) {
                matches = false;
            }
        }

        if (!matches) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftBattery (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_battery = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_battery").getValues(true));
        if (!result.isSimilar(starlight_battery)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.titanium_ingot").getValues(true));
        final ItemStack nether_star_fragment = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.nether_star_fragment").getValues(true));

        boolean matches = true;
        final int[] titanium_slots = {3, 5, 6, 8};
        for (int slot : titanium_slots) {
            if (!matrix[slot].isSimilar(titanium_ingot)) {
                matches = false;
            }
        }

        if (matches) {
            final int[] fragment_slots = {4, 7};
            for (int slot : fragment_slots) {
                if (!matrix[slot].isSimilar(nether_star_fragment)) {
                    matches = false;
                }
            }
        }

        if (!matches) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftModule (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_module = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_module").getValues(true));
        if (!result.isSimilar(starlight_module)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.titanium_ingot").getValues(true));
        final ItemStack starlight_battery = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_battery").getValues(true));
        final ItemStack starlight_circuit = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starlight_circuit").getValues(true));

        boolean matches = true;
        final int[] titanium_slots = {0, 1, 2, 3, 5, 6, 8};
        for (int slot : titanium_slots) {
            if (!matrix[slot].isSimilar(titanium_ingot)) {
                matches = false;
            }
        }

        if (matches) {
            if (!isMatching(matrix, starlight_circuit)) {
                matches = false;
            }
            if (matches) {
                if (!isMatching(matrix, starlight_battery, 7)) {
                    matches = false;
                }
            }
        }

        if (!matches) {
            event.getInventory().setResult(null);
        }
    }
}
