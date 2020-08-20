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
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StarlightTools extends MatchCrafting implements Listener {
    protected final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;

    public StarlightTools(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
    }

    public void saberRecipe() {
        final ItemStack saber = customItemManager.getItem(CustomItem.STARLIGHT_SABER);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_saber"), saber);
        recipe.shape(" # ", "@# ", " | ");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void shieldRecipe() {
        final ItemStack shield = customItemManager.getItem(CustomItem.STARLIGHT_SHIELD);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_shield"), shield);
        recipe.shape("#@#", "###", " # ");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void blasterRecipe() {
        final ItemStack blaster = customItemManager.getItem(CustomItem.STARLIGHT_BLASTER);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_blaster"), blaster);
        recipe.shape("--@", "##%", " ##");
        recipe.setIngredient('-', Material.REDSTONE);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('%', Material.COMPARATOR);

        plugin.getServer().addRecipe(recipe);
    }

    public void paxelRecipe() {
        final ItemStack paxel = customItemManager.getItem(CustomItem.STARLIGHT_PAXEL);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_paxel"), paxel);
        recipe.shape("#@#", "#| ", " | ");

        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void sentryRecipe() {
        final ItemStack sentry = customItemManager.getItem(CustomItem.STARLIGHT_SENTRY);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_sentry"), sentry);
        recipe.shape("#@#", "#%#", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.ENDER_EYE);
        recipe.setIngredient('%', Material.CROSSBOW);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void craftSaber(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack saber = customItemManager.getItem(CustomItem.STARLIGHT_SABER);
        if (!result.isSimilar(saber)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);
        final ItemStack titanium_rod = customItemManager.getItem(CustomItem.TITANIUM_ROD);
        final ItemStack photon_emitter = customItemManager.getItem(CustomItem.PHOTON_EMITTER);

        if (!isMatching(matrix, photon_emitter) || !isMatching(matrix, titanium_ingot, 1, 4) || !isMatching(matrix, titanium_rod, 7)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftShield(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack shield = customItemManager.getItem(CustomItem.STARLIGHT_SHIELD);
        if (!result.isSimilar(shield)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);
        final ItemStack starlight_module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);

        if (!isMatching(matrix, starlight_module) && !isMatching(matrix, titanium_ingot)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftBlaster(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack blaster = customItemManager.getItem(CustomItem.STARLIGHT_BLASTER);
        if (!result.isSimilar(blaster)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);
        final ItemStack photon_emitter = customItemManager.getItem(CustomItem.PHOTON_EMITTER);

        if (!isMatching(matrix, photon_emitter) || !isMatching(matrix, titanium_ingot)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftPaxel(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack paxel = customItemManager.getItem(CustomItem.STARLIGHT_PAXEL);
        if (!result.isSimilar(paxel)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);
        final ItemStack titanium_rod = customItemManager.getItem(CustomItem.TITANIUM_ROD);
        final ItemStack starlight_module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);

        if (!isMatching(matrix, starlight_module) || !isMatching(matrix, titanium_ingot, 0, 2, 3) || !isMatching(matrix, titanium_rod, 4, 7)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftSentry(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack sentry = customItemManager.getItem(CustomItem.STARLIGHT_SENTRY);
        if (!result.isSimilar(sentry)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium = customItemManager.getItem(CustomItem.REINFORCED_TITANIUM);

        if (isMatching(matrix, titanium)) {
            for (ItemStack item : matrix) {
                if (item != null && item.getType().equals(Material.CROSSBOW)) {
                    if (isStarlightBlaster(item)) {
                        event.getInventory().setResult(sentry);
                        return;
                    }
                }
            }
        }
        event.getInventory().setResult(null);
    }


    public boolean isStarlightSaber (ItemStack item) {
        if (item.getType().equals(customItemManager.getItem(CustomItem.STARLIGHT_SABER).getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "is_starlight_saber");
                    if (container.has(key, PersistentDataType.BYTE)) {
                        Byte matches = container.get(key, PersistentDataType.BYTE);
                        if (matches != null) {
                            return matches == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isStarlightShield (ItemStack item) {
        if (item.getType().equals(customItemManager.getItem(CustomItem.STARLIGHT_SHIELD).getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "is_starlight_shield");
                    if (container.has(key, PersistentDataType.BYTE)) {
                        Byte matches = container.get(key, PersistentDataType.BYTE);
                        if (matches != null) {
                            return matches == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isStarlightBlaster (ItemStack item) {
        if (item.getType().equals(customItemManager.getItem(CustomItem.STARLIGHT_BLASTER).getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "is_starlight_blaster");
                    if (container.has(key, PersistentDataType.BYTE)) {
                        Byte matches = container.get(key, PersistentDataType.BYTE);
                        if (matches != null) {
                            return matches == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isStarlightPaxel (ItemStack item) {
        if (item.getType().equals(Material.NETHERITE_PICKAXE) ||
                item.getType().equals(Material.NETHERITE_SHOVEL) ||
                item.getType().equals(Material.NETHERITE_AXE)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "is_starlight_paxel");
                    if (container.has(key, PersistentDataType.BYTE)) {
                        Byte matches = container.get(key, PersistentDataType.BYTE);
                        if (matches != null) {
                            return matches == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isStarlightSentry (ItemStack item) {
        if (item.getType().equals(customItemManager.getItem(CustomItem.STARLIGHT_SENTRY).getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "is_starlight_sentry");
                    if (container.has(key, PersistentDataType.BYTE)) {
                        Byte matches = container.get(key, PersistentDataType.BYTE);
                        if (matches != null) {
                            return matches == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

}
