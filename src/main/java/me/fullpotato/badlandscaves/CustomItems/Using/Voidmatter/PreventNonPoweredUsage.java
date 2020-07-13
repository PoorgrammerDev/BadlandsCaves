package me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PreventNonPoweredUsage extends BukkitRunnable {
    private final BadlandsCaves plugin;
    final EnchantmentStorage enchantmentStorage;

    public PreventNonPoweredUsage(BadlandsCaves plugin) {
        this.plugin = plugin;
        enchantmentStorage = new EnchantmentStorage(plugin);
    }


    @Override
    public void run() {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (hardmode) {
            Voidmatter voidmatter = new Voidmatter(plugin);
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null) {
                            if (voidmatter.isVoidmatter(item)) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta != null && meta.hasEnchants()) {
                                    enchantmentStorage.disenchantItem(item);
                                }
                            }
                        }
                    }
                }
                else {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null) {
                            if (voidmatter.isVoidmatter(item)) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta != null && !meta.hasEnchants()) {
                                    enchantmentStorage.loadEnchantments(item);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
