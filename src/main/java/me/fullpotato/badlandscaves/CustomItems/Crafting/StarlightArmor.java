package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StarlightArmor extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;

    public StarlightArmor(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void helmetRecipe() {
        final ItemStack item = CustomItem.STARLIGHT_HELMET.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_helmet"), item);
        recipe.shape("#@#", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chestplateRecipe() {
        final ItemStack item = CustomItem.STARLIGHT_CHESTPLATE.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_chestplate"), item);
        recipe.shape("# #", "#@#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void leggingsRecipe() {
        final ItemStack item = CustomItem.STARLIGHT_LEGGINGS.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_leggings"), item);
        recipe.shape("#@#", "# #", "# #");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void bootsRecipe() {
        final ItemStack item = CustomItem.STARLIGHT_BOOTS.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_boots"), item);
        recipe.shape("# #", "#@#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void chargeArmorRecipes() {
        final ItemStack item = CustomItem.STARLIGHT_CHARGE_PLACEHOLDER.getItem();
        ShapelessRecipe helmet_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "starlight_helmet_charge"), item);
        helmet_recipe.addIngredient(Material.DIAMOND_HELMET);
        helmet_recipe.addIngredient(Material.KNOWLEDGE_BOOK);

        ShapelessRecipe chestplate_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "starlight_chestplate_charge"), item);
        chestplate_recipe.addIngredient(Material.DIAMOND_CHESTPLATE);
        chestplate_recipe.addIngredient(Material.KNOWLEDGE_BOOK);

        ShapelessRecipe leggings_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "starlight_leggings_charge"), item);
        leggings_recipe.addIngredient(Material.DIAMOND_LEGGINGS);
        leggings_recipe.addIngredient(Material.KNOWLEDGE_BOOK);

        ShapelessRecipe boots_recipe = new ShapelessRecipe(new NamespacedKey(plugin, "starlight_boots_charge"), item);
        boots_recipe.addIngredient(Material.DIAMOND_BOOTS);
        boots_recipe.addIngredient(Material.KNOWLEDGE_BOOK);

        plugin.getServer().addRecipe(helmet_recipe);
        plugin.getServer().addRecipe(chestplate_recipe);
        plugin.getServer().addRecipe(leggings_recipe);
        plugin.getServer().addRecipe(boots_recipe);
    }

    @EventHandler
    public void checkCraftingRecipes (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_helmet = CustomItem.STARLIGHT_HELMET.getItem();
        final ItemStack starlight_chestplate = CustomItem.STARLIGHT_CHESTPLATE.getItem();
        final ItemStack starlight_leggings = CustomItem.STARLIGHT_LEGGINGS.getItem();
        final ItemStack starlight_boots = CustomItem.STARLIGHT_BOOTS.getItem();

        if (!result.isSimilar(starlight_helmet) &&
                !result.isSimilar(starlight_chestplate) &&
                !result.isSimilar(starlight_leggings) &&
                !result.isSimilar(starlight_boots)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = CustomItem.TITANIUM_INGOT.getItem();
        final ItemStack module = CustomItem.STARLIGHT_MODULE.getItem();
        if (!isMatching(matrix, titanium_ingot) || !isMatching(matrix, module)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void checkChargingRecipes (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = CustomItem.STARLIGHT_CHARGE_PLACEHOLDER.getItem();
        if (!result.isSimilar(placeholder)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        EnergyCore coreChecker = new EnergyCore(plugin);
        StarlightArmor armorChecker = new StarlightArmor(plugin);

        ItemStack armor = null;
        ItemStack energyCore = null;
        for (ItemStack item : matrix) {
            if (item != null) {
                if (coreChecker.isEnergyCore(item)) {
                    energyCore = item;
                }
                else if (armorChecker.isStarlightArmor(item)) {
                    armor = item;
                }
            }
        }

        if (armor != null && energyCore != null) {
            int newCharge = coreChecker.getCharge(energyCore);
            armorChecker.setCharge(armor, armorChecker.getCharge(armor) + newCharge);
            event.getInventory().setResult(armor);
            return;
        }
        event.getInventory().setResult(null);
    }

    public boolean isStarlightArmor(ItemStack item) {
        ArrayList<Material> armorTypes = new ArrayList<>();
        armorTypes.add(CustomItem.STARLIGHT_HELMET.getItem().getType());
        armorTypes.add(CustomItem.STARLIGHT_CHESTPLATE.getItem().getType());
        armorTypes.add(CustomItem.STARLIGHT_LEGGINGS.getItem().getType());
        armorTypes.add(CustomItem.STARLIGHT_BOOTS.getItem().getType());

        if (armorTypes.contains(item.getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "is_starlight_armor");
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte isArmor = container.get(key, PersistentDataType.BYTE);
                    if (isArmor != null) {
                        return isArmor == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public int getCharge(ItemStack item) {
        if (isStarlightArmor(item)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_charge"), PersistentDataType.INTEGER);
        }
        return -1;
    }

    public int getMaxCharge(ItemStack item) {
        if (isStarlightArmor(item)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_max_charge"), PersistentDataType.INTEGER);
        }
        return -1;
    }

    public void setCharge (ItemStack item, int charge) {
        if (isStarlightArmor(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && !lore.isEmpty()) {
                    int maxCharge = getMaxCharge(item);
                    int newCharge = Math.max(Math.min(charge, maxCharge), 0);
                    //change stat
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "starlight_charge"), PersistentDataType.INTEGER, newCharge);

                    //change lore
                    String[] chargeDesc = lore.get(0).split(" ");
                    chargeDesc[1] = newCharge + "";
                    lore.set(0, chargeDesc[0] + " " + chargeDesc[1] + " " + chargeDesc[2] + " " + chargeDesc[3]);
                    meta.setLore(lore);

                    //change "durability" bar
                    Damageable durabilityMeta = (Damageable) meta;

                    final int max = item.getType().getMaxDurability();
                    durabilityMeta.setDamage(Math.max(Math.min(max - (int) ((1.0 * newCharge / maxCharge) * max), max - 1), 1));

                    item.setItemMeta((ItemMeta) durabilityMeta);
                }
            }
        }
    }

    public void setMaxCharge (ItemStack item, int charge) {
        if (isStarlightArmor(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && !lore.isEmpty()) {
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "starlight_max_charge"), PersistentDataType.INTEGER, charge);

                    String[] chargeDesc = lore.get(0).split(" ");
                    chargeDesc[3] = charge + "";

                    lore.set(0, chargeDesc[0] + " " + chargeDesc[1] + " " + chargeDesc[2] + " " + chargeDesc[3]);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
