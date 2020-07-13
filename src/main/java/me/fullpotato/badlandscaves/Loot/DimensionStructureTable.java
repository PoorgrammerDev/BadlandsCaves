package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class DimensionStructureTable implements LootTable {
    private BadlandsCaves plugin;
    private final NamespacedKey key;
    private final DimensionsWorlds.Habitation habitation;

    public DimensionStructureTable(BadlandsCaves plugin, DimensionsWorlds.Habitation habitation) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "dimension_structure_table");
        this.habitation = habitation;
    }

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext lootContext) {
        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<ItemStack> trash = new ArrayList<>();

        //trash
        trash.add(new ItemStack(Material.STRING, randomCount(random, 1, 8)));
        trash.add(new ItemStack(Material.ROTTEN_FLESH, randomCount(random, 1, 8)));
        trash.add(new ItemStack(Material.BONE, randomCount(random, 1, 8)));
        trash.add(new ItemStack(Material.GUNPOWDER, randomCount(random, 1, 8)));
        trash.add(new ItemStack(Material.SPIDER_EYE, randomCount(random, 1, 8)));
        trash.add(new ItemStack(Material.FLINT, randomCount(random, 1, 8)));

        //loot
        items.add(new ItemStack(Material.IRON_INGOT, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.DIAMOND, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.EMERALD, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.QUARTZ, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.GOLD_INGOT, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.REDSTONE, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.IRON_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.DIAMOND_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.EMERALD_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.QUARTZ_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.GOLD_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.REDSTONE_BLOCK, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.BLAZE_POWDER, randomCount(random, 1, 4)));

        items.add(new ItemStack(Material.SHULKER_SHELL, randomCount(random, 1, 2)));
        items.add(new ItemStack(Material.LEAD, randomCount(random, 1, 2)));

        items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
        items.add(CustomItem.TOTEM_OF_PRESERVATION.getItem());
        items.add(CustomItem.FISHING_CRATE_HARDMODE.getItem());
        items.add(CustomItem.RECALL_POTION.getItem());
        items.add(CustomItem.MANA_POTION.getItem());
        items.add(CustomItem.ANTIDOTE.getItem());
        items.add(CustomItem.PURIFIED_WATER.getItem());
        items.add(CustomItem.DIMENSIONAL_ANCHOR.getItem());
        items.add(CustomItem.MERGED_SOULS.getItem());
        items.add(CustomItem.ENERGIUM.getItem());
        items.add(CustomItem.VOIDMATTER.getItem());

        ItemStack taintedPowder = CustomItem.TAINTED_POWDER.getItem();
        taintedPowder.setAmount(randomCount(random, 1, 8));
        items.add(taintedPowder);

        ItemStack titaniumFragment = CustomItem.TITANIUM_FRAGMENT.getItem();
        titaniumFragment.setAmount(randomCount(random, 1, 4));
        items.add(titaniumFragment);

        ItemStack blessedApple = CustomItem.BLESSED_APPLE.getItem();
        blessedApple.setAmount(randomCount(random, 1, 4));
        items.add(blessedApple);

        ItemStack enchantedBlessedApple = CustomItem.ENCHANTED_BLESSED_APPLE.getItem();
        enchantedBlessedApple.setAmount(randomCount(random, 1, 2));
        items.add(enchantedBlessedApple);

        items.add(CustomItem.TREASURE_GEAR_VOUCHER.getItem());

        TreasureGear treasureGear = new TreasureGear();
        Collection<ItemStack> output = new ArrayList<>();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int count = (chaos / 10) > 0 ? random.nextInt(chaos / 10) + random.nextInt(3) + 1 : random.nextInt(3) + 1;
        int failed = 0;
        for (int i = 0; i < count; i++) {
            if (failed > 100) break;
            ItemStack item = habitation.equals(DimensionsWorlds.Habitation.INHABITED) || random.nextBoolean() ? items.get(random.nextInt(items.size())) : trash.get(random.nextInt(trash.size()));

            if (item.isSimilar(CustomItem.TREASURE_GEAR_VOUCHER.getItem())) {
                output.add(treasureGear.getTreasureGear(true, random));
            }
            else {
                if (!output.contains(item)) {
                    output.add(item);
                }
                else {
                    i--;
                    failed++;
                }
            }
        }

        return output;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext lootContext) {
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    /**Returns a random integer between min and max, inclusive. */
    private int randomCount (Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
