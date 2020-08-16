package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class DimensionStructureTable implements LootTable {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final NamespacedKey key;
    private final DimensionsWorlds.NativeLife habitation;

    public DimensionStructureTable(BadlandsCaves plugin, DimensionsWorlds.NativeLife habitation) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "dimension_structure_table");
        this.habitation = habitation;
        customItemManager = plugin.getCustomItemManager();
    }

    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
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
        items.add(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION));
        items.add(customItemManager.getItem(CustomItem.FISHING_CRATE_HARDMODE));
        items.add(customItemManager.getItem(CustomItem.RECALL_POTION));
        items.add(customItemManager.getItem(CustomItem.MANA_POTION));
        items.add(customItemManager.getItem(CustomItem.ANTIDOTE));
        items.add(customItemManager.getItem(CustomItem.PURIFIED_WATER));
        items.add(customItemManager.getItem(CustomItem.DIMENSIONAL_ANCHOR));
        items.add(customItemManager.getItem(CustomItem.MERGED_SOULS));
        items.add(customItemManager.getItem(CustomItem.ENERGIUM));
        items.add(customItemManager.getItem(CustomItem.VOIDMATTER));

        ItemStack taintedPowder = customItemManager.getItem(CustomItem.TAINTED_POWDER);
        taintedPowder.setAmount(randomCount(random, 1, 8));
        items.add(taintedPowder);

        ItemStack titaniumFragment = customItemManager.getItem(CustomItem.TITANIUM_FRAGMENT);
        titaniumFragment.setAmount(randomCount(random, 1, 4));
        items.add(titaniumFragment);

        ItemStack blessedApple = customItemManager.getItem(CustomItem.BLESSED_APPLE);
        blessedApple.setAmount(randomCount(random, 1, 4));
        items.add(blessedApple);

        ItemStack enchantedBlessedApple = customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE);
        enchantedBlessedApple.setAmount(randomCount(random, 1, 2));
        items.add(enchantedBlessedApple);

        items.add(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER));

        TreasureGear treasureGear = new TreasureGear();
        Collection<ItemStack> output = new ArrayList<>();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int count = (chaos / 10) > 0 ? random.nextInt(chaos / 10) + random.nextInt(3) + 1 : random.nextInt(3) + 1;
        int failed = 0;
        for (int i = 0; i < count; i++) {
            if (failed > 100) break;
            ItemStack item = habitation.equals(DimensionsWorlds.NativeLife.ILLAGERS) || random.nextBoolean() ? items.get(random.nextInt(items.size())) : trash.get(random.nextInt(trash.size()));

            if (item.isSimilar(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER))) {
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
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    /**Returns a random integer between min and max, inclusive. */
    private int randomCount (Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
