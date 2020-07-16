package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentStorage {
    private final NamespacedKey key;

    public EnchantmentStorage(BadlandsCaves plugin) {
        this.key = new NamespacedKey(plugin, "stored_enchantments");
    }

    //FORMAT ENCHANTMENT_NAME:LEVEL;ENCHANTMENT_NAME:LEVEL;
    public String getStringFromEnchantments (Map<Enchantment, Integer> enchantments) {
        StringBuilder builder = new StringBuilder();
        for (Enchantment enchantment : enchantments.keySet()) {
            builder.append(enchantment.getKey().getKey()).append(":").append(enchantments.get(enchantment)).append(";");
        }
        return builder.toString();
    }

    @SuppressWarnings("deprecation")
    public Map<Enchantment, Integer> getEnchantmentsFromString (String string) {
        Map<Enchantment, Integer> output = new HashMap<>();
        final String[] entries = string.split(";");
        for (String entry : entries) {
            final String[] separateNameAndLevel = entry.split(":");
            final String name = separateNameAndLevel[0];
            final String levelStr = separateNameAndLevel[1];

            final NamespacedKey key = new NamespacedKey(NamespacedKey.MINECRAFT, name);
            final Enchantment enchantment = Enchantment.getByKey(key);
            if (enchantment != null) {
                try {
                    final Integer level = Integer.parseInt(levelStr);
                    output.put(enchantment, level);
                }
                catch (NumberFormatException ignored) {
                }
            }
        }
        return output;
    }


    public void storeEnchantments(ItemStack item, boolean disenchant) {
        if (item != null) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    final String enchantments = getStringFromEnchantments(item.getEnchantments());
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, enchantments);
                    item.setItemMeta(meta);

                    if (disenchant) {
                        disenchantItem(item);
                    }
                }
            }
        }
    }

    public void disenchantItem (ItemStack item) {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            item.removeEnchantment(enchantment);
        }
    }

    public void loadEnchantments(ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        final String enchantmentsStr = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                        if (enchantmentsStr != null) {
                            final Map<Enchantment, Integer> enchantments = getEnchantmentsFromString(enchantmentsStr);

                            for (Enchantment enchantment : enchantments.keySet()) {
                                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
                            }

                            item.setItemMeta(meta);
                        }
                    }
                }
            }
        }
    }

    public void setEnchantments (ItemStack item, Map<Enchantment, Integer> enchantments) {
        final String str = getStringFromEnchantments(enchantments);
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);
        item.setItemMeta(meta);

        disenchantItem(item);
        loadEnchantments(item);
    }


}
