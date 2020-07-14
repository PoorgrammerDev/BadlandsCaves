package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class Canteen extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final HashMap<CustomItem, String> liquidTypes = new HashMap<>();

    public Canteen(BadlandsCaves plugin) {
        this.plugin = plugin;

        liquidTypes.put(CustomItem.TOXIC_WATER, ChatColor.DARK_GREEN + "Toxic Water");
        liquidTypes.put(CustomItem.PURIFIED_WATER, ChatColor.DARK_AQUA + "Purified Water");
        liquidTypes.put(CustomItem.ANTIDOTE, ChatColor.LIGHT_PURPLE + "Antidote");
        liquidTypes.put(CustomItem.MANA_POTION, ChatColor.BLUE + "Mana Potion");
    }

    public void canteenRecipe() {
        final ItemStack canteen = CustomItem.CANTEEN.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "canteen"), canteen);
        recipe.shape(" @ ", "# #", "@#@");
        recipe.setIngredient('@', Material.IRON_NUGGET);
        recipe.setIngredient('#', Material.IRON_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void fillCanteen() {
        final ItemStack placeholder = CustomItem.CANTEEN_FILL_PLACEHOLDER.getItem();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "canteen_fill"), placeholder);

        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.POTION);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void canteenFillMechanism (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = CustomItem.CANTEEN_FILL_PLACEHOLDER.getItem();
        if (!result.isSimilar(placeholder)) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        ItemStack canteenItem = null;
        ItemStack otherPotion = null;

        for (ItemStack item : matrix) {
            if (item != null) {
                if (canteenItem == null && isCanteen(item)) {
                    canteenItem = item;
                }
                else if (item.getType().equals(Material.POTION)) {
                    otherPotion = item;
                }
            }
        }

        if (canteenItem != null && otherPotion != null) {
            final int limit = plugin.getOptionsConfig().getInt("canteen_limit");
            if (getAmount(canteenItem) < limit) {
                final ItemStack canteenResult = canteenItem.clone();
                for (CustomItem type : liquidTypes.keySet()) {
                    if (type.getItem().isSimilar(otherPotion)) {
                        String canteenType = getType(canteenResult);
                        if (canteenType != null && (canteenType.equals("EMPTY") || canteenType.equals(type.name()))) {
                            setType(canteenResult, type);
                            setAmount(canteenResult, getAmount(canteenResult) + 1);

                            event.getInventory().setResult(canteenResult);
                            return;
                        }
                    }
                }
            }
        }
        event.getInventory().setResult(null);
    }

    @EventHandler
    public void returnGlassBottle (CraftItemEvent event) {
        final ItemStack result = event.getRecipe().getResult();
        if (!result.isSimilar(CustomItem.CANTEEN_FILL_PLACEHOLDER.getItem())) return;

        final ItemStack[] matrix = event.getInventory().getMatrix();
        int slot = -1;

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null && !isCanteen(matrix[i]) && matrix[i].getType().equals(Material.POTION)) {
                slot = i + 1;
            }
        }

        ItemStack glassBottle = new ItemStack(Material.GLASS_BOTTLE);
        if (slot == -1) {
            List<HumanEntity> viewers = event.getViewers();
            if (!viewers.isEmpty()) {
                HumanEntity player = viewers.get(0);
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), glassBottle);
                }
                else {
                    player.getInventory().addItem(glassBottle);
                }
            }
        }
        else {
            int finalSlot = slot;
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getInventory().setItem(finalSlot, glassBottle);
                }
            }.runTaskLater(plugin, 1);
        }
    }

    public boolean isCanteen (ItemStack item) {
        if (item != null) {
            if (item.getType().equals(CustomItem.CANTEEN.getItem().getType())) {
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_canteen"), PersistentDataType.BYTE);
                        if (result != null) return result == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public String getType (ItemStack item) {
        if (item != null) {
            if (isCanteen(item)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    return meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "canteen_liquid"), PersistentDataType.STRING);
                }
            }
        }
        return null;
    }

    public void setType (ItemStack item, CustomItem type) {
        if (liquidTypes.containsKey(type)) {
            setType(item, type.name(), liquidTypes.get(type));
        }
    }

    public void setType (ItemStack item, String dataType, String descType) {
        if (item != null) {
            if (isCanteen(item)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "canteen_liquid"), PersistentDataType.STRING, dataType);

                    final List<String> lore = meta.getLore();
                    assert lore != null;
                    lore.set(0, descType);
                    meta.setLore(lore);

                    item.setItemMeta(meta);
                }
            }
        }
    }

    public int getAmount (ItemStack item) {
        if (item != null) {
            if (isCanteen(item)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Integer result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "canteen_amount"), PersistentDataType.INTEGER);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return -1;
    }

    public void setAmount (ItemStack item, int amount) {
        if (item != null) {
            if (isCanteen(item)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (amount == 0) {
                        setType(item, "EMPTY", ChatColor.GRAY + "Empty");
                        meta = item.getItemMeta();
                    }

                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "canteen_amount"), PersistentDataType.INTEGER, amount);

                    final List<String> lore = meta.getLore();
                    assert lore != null;

                    if (amount > 0) {
                        if (lore.size() > 1) {
                            lore.set(1, ChatColor.GRAY.toString() + amount + " Bottles");
                        }
                        else {
                            lore.add(1, ChatColor.GRAY.toString() + amount + " Bottles");
                        }
                    }
                    else {
                        if (lore.size() > 1) {
                            lore.remove(1);
                        }
                    }

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
