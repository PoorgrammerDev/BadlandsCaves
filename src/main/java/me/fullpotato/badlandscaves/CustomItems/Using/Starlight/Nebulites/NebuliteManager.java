package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NebuliteManager {
    private final BadlandsCaves plugin;
    private final StarlightCharge chargeManager;
    private final NamespacedKey key;

    public NebuliteManager(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.chargeManager = new StarlightCharge(plugin);
        this.key = new NamespacedKey(plugin, "nebulites");
    }

    public boolean isNebulite (ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                final ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_nebulite"), PersistentDataType.BYTE)) {
                        Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_nebulite"), PersistentDataType.BYTE);
                        if (result != null) {
                            return result == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Not to be confused with getNebulites(ItemStack item).
     * This one is intended to get a Nebulite Object from a Nebulite Item.
     * @param item Nebulite Item
     * */
    public Nebulite getNebulite (ItemStack item) {
        if (item != null) {
            if (isNebulite(item)) {
                for (Nebulite nebulite : Nebulite.values()) {
                    if (item.isSimilar(nebulite.getNebuliteItem().getItem())) {
                        return nebulite;
                    }
                }
            }
        }
        return null;
    }

    /** Not to be confused with getNebulite(ItemStack item).
     * This one is intended to get all applied Nebulites from a Starlight Item.
     * @param item Starlight Item
     * */
    public Nebulite[] getNebulites (ItemStack item) {
        if (item != null) {
            if (chargeManager.isStarlight(item)) {
                final String str = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (str != null) {
                    return getNebulitesFromString(str);
                }
            }
        }
        return null;
    }

    public void setNebulites (ItemStack item, Nebulite... nebulites) {
        if (item != null) {
            if (chargeManager.isStarlight(item)) {
                final String str = getStringFromNebulites(nebulites);
                final ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);
                }
                item.setItemMeta(meta);
            }
        }
    }

    public String getStringFromNebulites (Nebulite... nebulites) {
        StringBuilder builder = new StringBuilder();
        for (Nebulite nebulite : nebulites) {
            builder.append(nebulite.name()).append(";");
        }
        return builder.toString();
    }

    public Nebulite[] getNebulitesFromString (String string) {
        final String[] entries = string.split(";");
        final ArrayList<Nebulite> nebulites = new ArrayList<>();

        for (String entry : entries) {
            try {
                nebulites.add(Nebulite.valueOf(entry));
            }
            catch (IllegalArgumentException ignored) {
            }
        }

        final Nebulite[] output = new Nebulite[nebulites.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = nebulites.get(i);
        }

        return output;
    }
}
