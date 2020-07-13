package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ChargeRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final StarlightArmor armorManager;
    private final StarlightTools toolManager;
    private final StarlightCharge chargeManager;
    final EnchantmentStorage enchantmentStorage;
    public ChargeRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.armorManager = new StarlightArmor(plugin);
        this.toolManager = new StarlightTools(plugin);
        this.chargeManager = new StarlightCharge(plugin);
        enchantmentStorage = new EnchantmentStorage(plugin);
    }

    @Override
    public void run() {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");

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
                                        enchantmentStorage.loadEnchantments(item);
                                    }
                                }
                                else {
                                    if (meta.hasEnchants()) {
                                        enchantmentStorage.disenchantItem(item);
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
