package me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.MatchCrafting;
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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyCore extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final ItemStack energyCore;
    private final ItemStack chargedQuartz;
    private final ItemStack energium;
    private final double defaultRatio; // XP:Charge Ratio when no catalyst is applied

    private final Map<ItemStack, Double> chargeMultiplierMap;

    public EnergyCore(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        energyCore = customItemManager.getItem(CustomItem.ENERGY_CORE);
        chargedQuartz = customItemManager.getItem(CustomItem.CHARGED_QUARTZ);
        energium = customItemManager.getItem(CustomItem.ENERGIUM);

        chargeMultiplierMap = new HashMap<>();
        chargeMultiplierMap.put(new ItemStack(Material.REDSTONE), plugin.getOptionsConfig().getDouble("energy_core_mult.redstone"));
        chargeMultiplierMap.put(new ItemStack(Material.GLOWSTONE_DUST), plugin.getOptionsConfig().getDouble("energy_core_mult.glowstone"));
        chargeMultiplierMap.put(chargedQuartz, plugin.getOptionsConfig().getDouble("energy_core_mult.charged_quartz"));
        chargeMultiplierMap.put(energium, plugin.getOptionsConfig().getDouble("energy_core_mult.energium"));

        this.defaultRatio = plugin.getOptionsConfig().getDouble("energy_core_mult.empty");
    }

    public void energyCoreRecipes() {
        ShapelessRecipe withModifier = new ShapelessRecipe(new NamespacedKey(plugin, "energy_core_modifier"), energyCore);
        withModifier.addIngredient(new RecipeChoice.MaterialChoice(Material.COMMAND_BLOCK, Material.GLOWSTONE_DUST, Material.REDSTONE));
        withModifier.addIngredient(Material.EXPERIENCE_BOTTLE);
        plugin.getServer().addRecipe(withModifier);

        ShapelessRecipe noModifier = new ShapelessRecipe(new NamespacedKey(plugin, "energy_core_no_modifier"), energyCore);
        noModifier.addIngredient(Material.EXPERIENCE_BOTTLE);
        plugin.getServer().addRecipe(noModifier);
    }

    public boolean isEnergyCore(ItemStack item) {
        if (item.getType().equals(energyCore.getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_energy_core"), PersistentDataType.BYTE) ) {
                        Byte isEnergyCore = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_energy_core"), PersistentDataType.BYTE);
                        if (isEnergyCore != null) {
                            return isEnergyCore == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int getCharge(ItemStack item) {
        if (isEnergyCore(item)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "energy_core_charge"), PersistentDataType.INTEGER);
        }
        return -1;
    }

    public boolean setCharge (ItemStack item, int charge) {
        if (isEnergyCore(item)) {
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "energy_core_charge"), PersistentDataType.INTEGER, charge);

            List<String> lore = meta.getLore();
            assert lore != null;
            lore.set(0, "§7Charge: " + charge);
            meta.setLore(lore);

            item.setItemMeta(meta);
            return true;
        }
        return false;
    }

    @EventHandler
    public void craftEnergyCore(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        //Make sure recipe is for energy core
        final ItemStack result = event.getRecipe().getResult();
        if (!result.isSimilar(energyCore)) return;

        if (event.getViewers().isEmpty() || event.getViewers().size() < 1) {
            event.getInventory().setResult(null);
            return;
        }

        //Tech class only
        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();

            //Find the exp bottle and the catalyst used
            ItemStack exp_bottle = null;
            ItemStack catalyst = null;
            for (ItemStack ingredient : matrix) {
                if (ingredient != null) {
                    if (ingredient.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                        exp_bottle = ingredient;
                    }
                    else {
                        catalyst = ingredient;
                    }
                }
            }

            if (exp_bottle != null) {
                int charge = 0;
                final ItemMeta bottleMeta = exp_bottle.getItemMeta();
                if (bottleMeta != null && bottleMeta.hasLore()) {
                   final List<String> lore = bottleMeta.getLore();

                   if (lore != null) {
                       try {
                           charge = Integer.parseInt(lore.get(0).split(" ")[0].substring(2));
                       }
                       catch (NumberFormatException ignored) {
                       }

                       //Apply catalyst charge multiplier
                       if (catalyst != null) {
                           final ItemStack multiplierClone = catalyst.clone();
                           multiplierClone.setAmount(1);

                           if (chargeMultiplierMap.containsKey(multiplierClone)) {
                               charge *= chargeMultiplierMap.get(multiplierClone);
                           }
                       }
                       //Apply default
                       else {
                           charge *= defaultRatio;
                       }

                       //Set final result's charge and set result
                       if (charge > 0) {
                           setCharge(result, charge);
                           event.getInventory().setResult(result);
                           return;
                       }
                   }


                }
            }
        event.getInventory().setResult(null);
    }
}
