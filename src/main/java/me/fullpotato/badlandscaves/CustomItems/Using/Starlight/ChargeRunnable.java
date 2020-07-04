package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class ChargeRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final StarlightArmor armorManager;
    private final StarlightTools toolManager;
    private final StarlightCharge chargeManager;
    public ChargeRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.armorManager = new StarlightArmor(plugin);
        this.toolManager = new StarlightTools(plugin);
        this.chargeManager = new StarlightCharge(plugin);
    }

    @Override
    public void run() {
        boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
        if (hardmode) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null) {
                        if (chargeManager.isStarlight(item)) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta != null) {
                                int charge = chargeManager.getCharge(item);
                                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 && charge > 0) {
                                    if (!meta.hasEnchants()) {
                                        // TODO: 7/3/2020 change this to use persistent data to store enchantments, then load them from that
                                        if (armorManager.isStarlightArmor(item)) {
                                            Short platingObject = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_plating"), PersistentDataType.SHORT);
                                            short plating = platingObject != null ? platingObject : 0;
                                            meta.addEnchant(Enchantment.DURABILITY, 7, true);
                                            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (2 * plating), true);
                                        }
                                        else if (toolManager.isStarlightSaber(item)) {
                                            meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
                                            meta.addEnchant(Enchantment.DURABILITY, 5, true);
                                        }
                                        else if (toolManager.isStarlightShield(item)) {
                                            meta.addEnchant(Enchantment.DURABILITY, 5, true);
                                        }
                                        else if (toolManager.isStarlightBlaster(item)) {
                                            meta.addEnchant(Enchantment.DURABILITY, 1, true);
                                        }
                                        else if (toolManager.isStarlightPaxel(item)) {
                                            meta.addEnchant(Enchantment.DIG_SPEED, 7, true);
                                            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 5, true);
                                            meta.addEnchant(Enchantment.DURABILITY, 5, true);
                                        }

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
