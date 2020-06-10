package me.fullpotato.badlandscaves.CustomItems.Using.Armor;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.StarlightArmor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class StarlightArmorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;

    public StarlightArmorRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
        if (hardmode) {
            StarlightArmor armor = new StarlightArmor(plugin);
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null) {
                        if (armor.isStarlightArmor(item)) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta != null) {
                                int charge = armor.getCharge(item);
                                if (charge > 0) {
                                    if (!meta.hasEnchants()) {
                                       short plating = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_plating"), PersistentDataType.SHORT);
                                        meta.addEnchant(Enchantment.DURABILITY, 5, true);
                                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (2 * plating), true);

                                        item.setItemMeta(meta);
                                    }
                                }
                                else {
                                    if (meta.hasEnchants()) {
                                        for (Enchantment enchantment : meta.getEnchants().keySet()) {
                                            meta.removeEnchant(enchantment);
                                        }
                                        item.setItemMeta(meta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
