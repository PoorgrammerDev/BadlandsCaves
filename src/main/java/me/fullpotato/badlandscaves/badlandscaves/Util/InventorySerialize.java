package me.fullpotato.badlandscaves.badlandscaves.Util;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventorySerialize {
    private BadlandsCaves plugin;

    public InventorySerialize(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    /** Saves a player's current inventory to config.
     * @param player A valid online player.
     * @param inventoryName A name to put the items in the config under.
     */
    public void saveInventory (final Player player, final String inventoryName) {
        final PlayerInventory inventory = player.getInventory();
        for (int a = 0; a < inventory.getSize(); a++) {
            final ItemStack item = inventory.getItem(a);
            if (item != null) {
                plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".saved_inventories." + inventoryName + "." + a, item.serialize());
                plugin.saveConfig();
            }
        }
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    /** Takes the saved inventory in the player's config and gives it to the player.
     * @param player A valid online player.
     * @param inventoryName Name of inventory in the config.
     * @param clearCurrent If true, clear the player's current inventory and replace it with this. If false, simply just give them the items on top of their current inventory.
     * @param clearConfig If true, remove the saved inventory in the config after giving it to the player.
     */
    public void loadInventory (final Player player, final String inventoryName, final boolean clearCurrent, final boolean clearConfig) {
        final String base_key = "Scores.users." + player.getUniqueId() + ".saved_inventories." + inventoryName;
        final ConfigurationSection inv_section = plugin.getConfig().getConfigurationSection(base_key);
        if (inv_section != null) {
            if (clearCurrent) {
                player.getInventory().clear();
            }
            for (int a = 0; a <= 40; a++) {
                final ConfigurationSection item_section = plugin.getConfig().getConfigurationSection(base_key + "." + a);
                if (item_section != null) {
                    final ItemStack item = ItemStack.deserialize(item_section.getValues(true));
                    if (clearCurrent) {
                        player.getInventory().setItem(a, item);
                    }
                    else {
                        player.getInventory().addItem(item);
                    }
                }
            }

            if (clearConfig) {
                plugin.getConfig().set(base_key, null);
                plugin.saveConfig();
            }
        }
    }
}
