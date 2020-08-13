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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EnergyCore extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final ItemStack energyCore;

    public EnergyCore(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        energyCore = customItemManager.getItem(CustomItem.ENERGY_CORE);
    }

    public void energyCoreRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "energy_core"), energyCore);
        recipe.addIngredient(Material.COMMAND_BLOCK);
        recipe.addIngredient(Material.EXPERIENCE_BOTTLE);

        plugin.getServer().addRecipe(recipe);
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

        final ItemStack result = event.getRecipe().getResult();
        if (!result.isSimilar(energyCore)) return;

        if (event.getViewers().isEmpty() || event.getViewers().size() < 1) {
            event.getInventory().setResult(null);
            return;
        }

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack energium = customItemManager.getItem(CustomItem.ENERGIUM);
        final ItemStack starFragment = customItemManager.getItem(CustomItem.NETHER_STAR_FRAGMENT);

        if (isMatching(matrix, energium) || isMatching(matrix, starFragment)) {
            ItemStack exp_bottle = null;
            for (ItemStack ingredient : matrix) {
                if (ingredient != null && ingredient.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                    exp_bottle = ingredient;
                    break;
                }
            }

            if (exp_bottle != null) {
                int exp_stored = 0;
                final ItemMeta xp_meta = exp_bottle.getItemMeta();
                if (xp_meta.hasLore()) {
                    List<String> lore = xp_meta.getLore();
                    try {
                        exp_stored = Integer.parseInt(lore.get(0).split(" ")[0].substring(2));
                    }
                    catch (NumberFormatException ignored) {
                    }

                    int addCharge = isMatching(matrix, starFragment) ? exp_stored / 5 : exp_stored / 10;
                    if (addCharge > 0) {
                        setCharge(result, addCharge);
                        event.getInventory().setResult(result);
                        return;
                    }
                }
            }
        }
        event.getInventory().setResult(null);
    }
}
