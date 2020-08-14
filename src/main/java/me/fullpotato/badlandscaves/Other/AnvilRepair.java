package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashSet;
import java.util.Set;

public class AnvilRepair implements Listener {
    private final BadlandsCaves plugin;
    private final Set<Material> repairMaterials = new HashSet<>();

    public AnvilRepair(BadlandsCaves plugin) {
        this.plugin = plugin;
        repairMaterials.add(Material.OAK_PLANKS);
        repairMaterials.add(Material.ACACIA_PLANKS);
        repairMaterials.add(Material.BIRCH_PLANKS);
        repairMaterials.add(Material.DARK_OAK_PLANKS);
        repairMaterials.add(Material.JUNGLE_PLANKS);
        repairMaterials.add(Material.SPRUCE_PLANKS);
        repairMaterials.add(Material.CRIMSON_PLANKS);
        repairMaterials.add(Material.WARPED_PLANKS);
        repairMaterials.add(Material.COBBLESTONE);
        repairMaterials.add(Material.IRON_INGOT);
        repairMaterials.add(Material.GOLD_INGOT);
        repairMaterials.add(Material.DIAMOND);
        repairMaterials.add(Material.NETHERITE_INGOT);
    }

    @EventHandler
    public void preventRepairCost (PrepareAnvilEvent event) {
        final AnvilInventory inventory = event.getInventory();
        final ItemStack result = event.getResult();
        if (result != null) {
            if (result.getItemMeta() instanceof Repairable) {
                final Repairable resultRepairable = (Repairable) result.getItemMeta();
                final ItemStack tool = inventory.getItem(0);
                final ItemStack addition = inventory.getItem(1);
                if (tool != null && addition != null) {
                    if (tool.getItemMeta() instanceof Repairable) {
                        final Repairable toolRepairable = (Repairable) tool.getItemMeta();
                        if (repairMaterials.contains(addition.getType()) || (tool.getType().equals(addition.getType()) && (!tool.getItemMeta().hasEnchants() || (addition.getItemMeta() != null && !addition.getItemMeta().hasEnchants())))) {
                            resultRepairable.setRepairCost(toolRepairable.getRepairCost());
                            result.setItemMeta((ItemMeta) resultRepairable);
                            event.setResult(result);
                        }
                    }
                }
            }
        }
    }
}
