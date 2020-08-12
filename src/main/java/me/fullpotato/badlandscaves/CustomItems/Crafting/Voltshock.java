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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Voltshock extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final String shock_lore = "§3Voltshock";
    private final Material[] swords = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
    };

    public Voltshock(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void craft_battery() {
        final ItemStack battery = CustomItem.VOLTSHOCK_BATTERY.getItem();
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
        final ItemStack shocker = CustomItem.VOLTSHOCK_SHOCKER.getItem();
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
        final ItemStack placeholder = CustomItem.VOLTSHOCK_PLACEHOLDER.getItem();
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
        final ItemStack voltshock_sword_charge_placeholder = CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER.getItem();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "charge_voltshock_sword"), voltshock_sword_charge_placeholder);
        recipe.addIngredient(Material.EXPERIENCE_BOTTLE);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(swords));

        plugin.getServer().addRecipe(recipe);

    }

    public void craft_arrow() {
        final ItemStack voltshock_arrow = CustomItem.VOLTSHOCK_ARROW.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "voltshock_arrow"), voltshock_arrow);

        /*
         *   #
         *  *
         * &%
         * where & is battery, % is arrow, * is redstone, and # is shocker
         * */

        recipe.shape("  #", " * ", "&% ");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('*', Material.REDSTONE);
        recipe.setIngredient('&', Material.COMMAND_BLOCK);
        recipe.setIngredient('%', Material.ARROW);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void batteryAndShock (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
            final ItemStack result = event.getRecipe().getResult();
            final ItemStack battery = CustomItem.VOLTSHOCK_BATTERY.getItem();
            final ItemStack shocker = CustomItem.VOLTSHOCK_SHOCKER.getItem();
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
            final ItemStack placeholder = CustomItem.VOLTSHOCK_PLACEHOLDER.getItem();
            if (result.isSimilar(placeholder)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                if (matrix != null && matrix.length == 9) {
                    final ItemStack battery = CustomItem.VOLTSHOCK_BATTERY.getItem();
                    final ItemStack shocker = CustomItem.VOLTSHOCK_SHOCKER.getItem();

                    if (battery != null && shocker != null) {
                        if (isMatching(matrix, battery, 6) && isMatching(matrix, shocker, 2, 5)) {
                            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, (Player) event.getViewers().get(0)) != 1) {
                                SerratedSwords serrated = new SerratedSwords(plugin);
                                Corrosive corrosive = new Corrosive(plugin);
                                TreasureGear treasureGear = new TreasureGear();
                                for (ItemStack item : matrix) {
                                    if (item != null && Arrays.asList(swords).contains(item.getType())) {
                                        boolean sword_ready = !serrated.isSerrated(item) && !isVoltshock(item) && !corrosive.isCorrosive(item) && !treasureGear.isTreasureGear(item);
                                        if (sword_ready) {
                                            ItemStack modified_sword = item.clone();
                                            ItemMeta meta = modified_sword.getItemMeta();
                                            meta.setCustomModelData(127);
                                            PersistentDataContainer data = meta.getPersistentDataContainer();
                                            NamespacedKey charge_key = new NamespacedKey(plugin, "charge");
                                            data.set(charge_key, PersistentDataType.INTEGER, 0);

                                            ArrayList<String> lore = new ArrayList<>();
                                            lore.add(shock_lore);
                                            lore.add("§70 / 50 Charge");
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
            final ItemStack placeholder = CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER.getItem();
            if (result.isSimilar(placeholder)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                ItemStack sword = null;
                ItemStack exp_bottle = null;

                for (ItemStack ingredient : matrix) {
                    if (ingredient != null) {
                        if (Arrays.asList(swords).contains(ingredient.getType())) {
                            if (isVoltshock(ingredient)) {
                                sword = ingredient;
                            }
                        } else if (ingredient.getType().equals(Material.EXPERIENCE_BOTTLE))
                            exp_bottle = ingredient;
                    }
                }

                if (sword != null && exp_bottle != null) {
                    final int current_charge = getCharge(sword);
                    if (current_charge < 50) {
                        int exp_stored = 0;
                        final ItemMeta xp_meta = exp_bottle.getItemMeta();
                        if (xp_meta.hasLore()) {
                            List<String> lore = xp_meta.getLore();
                            try {
                                exp_stored = Integer.parseInt(lore.get(0).split(" ")[0].substring(2));
                            } catch (NumberFormatException ignored) {
                            }

                            int added_charge = exp_stored / 20;
                            if (added_charge > 0) {
                                final int new_charge = Math.min(50, current_charge + added_charge);

                                ItemStack new_result = sword.clone();
                                setCharge(new_result, new_charge);

                                event.getInventory().setResult(new_result);
                                return;
                            }
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
            final ItemStack arrow = CustomItem.VOLTSHOCK_ARROW.getItem();
            if (result.isSimilar(arrow)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                final ItemStack battery = CustomItem.VOLTSHOCK_BATTERY.getItem();
                final ItemStack shocker = CustomItem.VOLTSHOCK_SHOCKER.getItem();

                if (!isMatching(matrix, shocker, 2) || !isMatching(matrix, battery, 6)) {
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
            lore.set(1, "§7" + charge + " / 50 Charge");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public void setOnCooldown(ItemStack item, boolean onCooldown) {
        if (isVoltshock(item)) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "isOnCooldown"), PersistentDataType.STRING, Boolean.toString(onCooldown));
            meta.setCustomModelData(128); // TODO: 4/20/2020
            item.setItemMeta(meta);
        }
    }

    public boolean getOnCooldown(ItemStack item) {
        return !isVoltshock(item) || Boolean.parseBoolean(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "isOnCooldown"), PersistentDataType.STRING));
    }
}
