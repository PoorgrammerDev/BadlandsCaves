package me.fullpotato.badlandscaves.Loot;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class RandomlyEnchantedGear {
    /**Randomly enchants ItemMeta with Enchantment. Respects vanilla's max level.
     * @param meta item's meta
     * @param enchantment enchantment being applied
     * @param lower_bound enchantment is at least this level. cannot be negative.
     */
    public void randomlyEnchant (ItemMeta meta, Enchantment enchantment, Random random, int lower_bound) {
        randomlyEnchant(meta, enchantment, random, lower_bound, enchantment.getMaxLevel());
    }



    /**Randomly enchants ItemMeta with Enchantment, disregards vanilla max level.
     * @param meta item's meta
     * @param enchantment enchantment being applied
     * @param lower_bound enchantment is at least this level. cannot be negative.
     * @param upper_bound enchantment is at max this level. has to be more than lower bound.
     * */
    public void randomlyEnchant (ItemMeta meta, Enchantment enchantment, Random random, int lower_bound, int upper_bound) {
        if (upper_bound > lower_bound) {
            if (lower_bound > 0) {
                meta.addEnchant(enchantment, random.nextInt(upper_bound - lower_bound) + lower_bound, true);
            }
            else {
                final int level = random.nextInt(upper_bound);
                if (level > 0) {
                    meta.addEnchant(enchantment, level, true);
                }
            }
        }
    }
}
