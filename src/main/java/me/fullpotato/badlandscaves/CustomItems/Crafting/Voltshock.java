package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.EnergyCore;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.Loot.TreasureGear;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;

import org.bukkit.Bukkit;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Voltshock extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final EnergyCore energyCore;
    private final StarlightTools starlightsword;
    private final String shock_lore = "§3Voltshock";
    private final TreasureGear treasureGear = new TreasureGear();
    private final Material[] swords = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
    };
    private final int VOLTSHOCK_MAX_CHARGE = 100;

    public Voltshock(BadlandsCaves plugin, EnergyCore energyCore, StarlightTools starlightsword) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        this.energyCore = energyCore;
        this.starlightsword = starlightsword;
    }

    public void craft_battery() {
        final ItemStack battery = customItemManager.getItem(CustomItem.VOLTSHOCK_BATTERY);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "battery"), battery);
        /*
         *  %
         * #*#
         * #*#
         * # is iron ingot, * is redstone dust, % is comparator
         */

        recipe.shape(" % ", "#*#", "#*#");
        recipe.setIngredient('%', Material.COMPARATOR);
        recipe.setIngredient('*', Material.REDSTONE);
        recipe.setIngredient('#', Material.IRON_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craft_shocker() {
        final ItemStack shocker = customItemManager.getItem(CustomItem.VOLTSHOCK_SHOCKER);
        shocker.setAmount(3);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "shocker"), shocker);
        /*
         * *#*
         * *#*
         * *#*
         * * is redstone, # is iron bar
         */
        recipe.shape("*#*", "*#*", "*#*");
        recipe.setIngredient('*', Material.REDSTONE);
        recipe.setIngredient('#', Material.CHAIN);

        plugin.getServer().addRecipe(recipe);
    }

    public void modify_sword() {
        final ItemStack placeholder = customItemManager.getItem(CustomItem.VOLTSHOCK_PLACEHOLDER);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voltshock_sword"), placeholder);

        /*
         *  *|
         *  *|
         * #&
         * # = battery, * = redstone, | = shocker
         * */
        recipe.shape(" *|", " *|", "#& ");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('*', Material.REDSTONE);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);
        recipe.setIngredient('&', new RecipeChoice.MaterialChoice(swords));

        plugin.getServer().addRecipe(recipe);
    }

    public void charge_sword() {
        final ItemStack voltshock_sword_charge_placeholder = customItemManager.getItem(CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "charge_voltshock_sword"), voltshock_sword_charge_placeholder);
        recipe.addIngredient(Material.KNOWLEDGE_BOOK);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(swords));

        plugin.getServer().addRecipe(recipe);

    }

    public void craft_arrow() {
        final ItemStack voltshock_arrow = customItemManager.getItem(CustomItem.VOLTSHOCK_ARROW);
        voltshock_arrow.setAmount(7);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voltshock_arrow"), voltshock_arrow);

         /*
            %#%
            %%%
            %&%

            where & is battery, % is arrow, and # is shocker
          */

        recipe.shape("%#%", "%%%", "%&%");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('&', Material.COMMAND_BLOCK);
        recipe.setIngredient('%', Material.ARROW);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void batteryAndShock (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack battery = customItemManager.getItem(CustomItem.VOLTSHOCK_BATTERY);
            final ItemStack shocker = customItemManager.getItem(CustomItem.VOLTSHOCK_SHOCKER);
            if (result.isSimilar(battery) || result.isSimilar(shocker)) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, (Player) event.getViewers().get(0)) == 1) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }


    @EventHandler
    public void applyToSword(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack placeholder = customItemManager.getItem(CustomItem.VOLTSHOCK_PLACEHOLDER);
            if (result.isSimilar(placeholder)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                if (matrix != null && matrix.length == 9) {
                    final ItemStack battery = customItemManager.getItem(CustomItem.VOLTSHOCK_BATTERY);
                    final ItemStack shocker = customItemManager.getItem(CustomItem.VOLTSHOCK_SHOCKER);

                    if (battery != null && shocker != null) {
                        if (isMatching(matrix, battery, 6) && isMatching(matrix, shocker, 2, 5)) {
                            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, (Player) event.getViewers().get(0)) != 1) {
                                SerratedSwords serrated = new SerratedSwords(plugin, energyCore, starlightsword);
                                Corrosive corrosive = new Corrosive(plugin, starlightsword, energyCore);
                                for (ItemStack item : matrix) {
                                    if (item != null && Arrays.asList(swords).contains(item.getType())) {
                                        boolean sword_ready = !serrated.isSerrated(item) && !isVoltshock(item) && !corrosive.isCorrosive(item) && !treasureGear.isTreasureGear(item) &&
                                                !starlightsword.isStarlightSaber(item);

                                        if (sword_ready) {
                                            ItemStack modified_sword = item.clone();
                                            ItemMeta meta = modified_sword.getItemMeta();
                                            meta.setCustomModelData(127);
                                            PersistentDataContainer data = meta.getPersistentDataContainer();
                                            NamespacedKey charge_key = new NamespacedKey(plugin, "charge");
                                            data.set(charge_key, PersistentDataType.INTEGER, 0);

                                            ArrayList<String> lore = new ArrayList<>();
                                            lore.add(shock_lore);
                                            lore.add("§70 / " + VOLTSHOCK_MAX_CHARGE + " Charge");
                                            meta.setLore(lore);
                                            modified_sword.setItemMeta(meta);

                                            event.getInventory().setResult(modified_sword);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void chargeSword(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack placeholder = customItemManager.getItem(CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER);
            if (result.isSimilar(placeholder)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                ItemStack sword = null;
                ItemStack energyCore = null;

                for (ItemStack ingredient : matrix) {
                    if (ingredient != null) {
                        if (Arrays.asList(swords).contains(ingredient.getType())) {
                            if (isVoltshock(ingredient)) {
                                sword = ingredient;
                            }
                        } else if (this.energyCore.isEnergyCore(ingredient))
                            energyCore = ingredient;
                    }
                }

                if (sword != null && energyCore != null) {
                    final int current_charge = getCharge(sword);
                    if (current_charge < VOLTSHOCK_MAX_CHARGE) {
                        final int addedCharge = this.energyCore.getCharge(energyCore);

                        if (addedCharge > 0) {
                            final int new_charge = Math.min(VOLTSHOCK_MAX_CHARGE, current_charge + addedCharge);
                            ItemStack new_result = sword.clone();
                            setCharge(new_result, new_charge);
                            event.getInventory().setResult(new_result);
                            return;
                        }
                    }
                }
                event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void craftArrow (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack arrow = customItemManager.getItem(CustomItem.VOLTSHOCK_ARROW);
            if (result.isSimilar(arrow)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                final ItemStack battery = customItemManager.getItem(CustomItem.VOLTSHOCK_BATTERY);
                final ItemStack shocker = customItemManager.getItem(CustomItem.VOLTSHOCK_SHOCKER);

                if (!isMatching(matrix, shocker, 1) || !isMatching(matrix, battery, 7)) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }

    public boolean isVoltshock(ItemStack item) {
        if (Arrays.asList(swords).contains(item.getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    if (lore != null && lore.size() >= 1) {
                        return lore.get(0).equalsIgnoreCase(shock_lore);
                    }
                }
            }
        }
        return false;
    }

    public int getCharge(ItemStack item) {
        return isVoltshock(item) ? item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER) : -1;
    }

    public void setCharge(ItemStack item, int charge) {
        if (isVoltshock(item)) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER, charge);
            List<String> lore = meta.getLore();
            lore.set(1, "§7" + charge + " / " + VOLTSHOCK_MAX_CHARGE + " Charge");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public void setOnCooldown(ItemStack item, boolean onCooldown) {
        if (isVoltshock(item)) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "on_cooldown"), PersistentDataType.BYTE, onCooldown ? (byte) 1 : (byte) 0);
            meta.setCustomModelData(128); // TODO: 4/20/2020
            item.setItemMeta(meta);
        }
    }

    public boolean getOnCooldown(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (isVoltshock(item)) {
                if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "on_cooldown"), PersistentDataType.BYTE)) {
                    final Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "on_cooldown"), PersistentDataType.BYTE);
                    return (result != null && result == 1);
                }
            }
        }
        return false;
    }
}
