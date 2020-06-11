package me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.MatchCrafting;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StarlightTools extends MatchCrafting implements Listener {
    protected final BadlandsCaves plugin;

    public StarlightTools(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void saberRecipe() {
        final ItemStack saber = CustomItem.STARLIGHT_SABER.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_saber"), saber);
        recipe.shape(" # ", "@# ", " | ");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('|', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void blasterRecipe() {
        final ItemStack blaster = CustomItem.STARLIGHT_BLASTER.getItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_blaster"), blaster);
        recipe.shape("--@", "##%", " ##");
        recipe.setIngredient('-', Material.REDSTONE);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('%', Material.COMPARATOR);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void craftSaber(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack saber = CustomItem.STARLIGHT_SABER.getItem();
        if (!result.isSimilar(saber)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = CustomItem.TITANIUM_INGOT.getItem();
        final ItemStack titanium_rod = CustomItem.TITANIUM_ROD.getItem();
        final ItemStack photon_emitter = CustomItem.PHOTON_EMITTER.getItem();

        if (!isMatching(matrix, photon_emitter) || !isMatching(matrix, titanium_ingot, 1) || !isMatching(matrix, titanium_ingot, 4) || !isMatching(matrix, titanium_rod, 7)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftBlaster(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack blaster = CustomItem.STARLIGHT_BLASTER.getItem();
        if (!result.isSimilar(blaster)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = CustomItem.TITANIUM_INGOT.getItem();
        final ItemStack photon_emitter = CustomItem.PHOTON_EMITTER.getItem();

        if (!isMatching(matrix, photon_emitter) || !isMatching(matrix, titanium_ingot)) {
            event.getInventory().setResult(null);
        }
    }

    public boolean isStarlightSaber (ItemStack item) {
        if (item.getType().equals(CustomItem.STARLIGHT_SABER.getItem().getType())) {
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

    public boolean isStarlightBlaster (ItemStack item) {
        if (item.getType().equals(CustomItem.STARLIGHT_BLASTER.getItem().getType())) {
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
        if (item.getType().equals(Material.DIAMOND_PICKAXE) ||
                item.getType().equals(Material.DIAMOND_SHOVEL) ||
                item.getType().equals(Material.DIAMOND_AXE)) {
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
}
