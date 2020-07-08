package me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PreventNonPoweredUsage extends BukkitRunnable {
    private final BadlandsCaves plugin;

    public PreventNonPoweredUsage(BadlandsCaves plugin) {
        this.plugin = plugin;
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
                                    for (Enchantment enchantment : meta.getEnchants().keySet()) {
                                        meta.removeEnchant(enchantment);
                                    }
                                    item.setItemMeta(meta);
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
                                    // TODO: 7/3/2020 change this to use persistent data to store enchantments, then load them from that
                                    if (voidmatter.isVoidmatterArmor(item)) {
                                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                                    }
                                    else if (voidmatter.isVoidmatterBlade(item)) {
                                        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                                    }
                                    else if (voidmatter.isVoidmatterBow(item)) {
                                        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                                        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                                    }
                                    else if (voidmatter.isVoidmatterPickaxe(item)) {
                                        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                                        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                                    }
                                    else if (voidmatter.isVoidmatterAxe(item)) {
                                        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                                        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                                    }
                                    else if (voidmatter.isVoidmatterShovel(item)) {
                                        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                                        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
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
