package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class DimensionStructureTable implements LootTable {
    private final NamespacedKey key;
    private final Material[] armor = {
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE,
    };

    public DimensionStructureTable(BadlandsCaves plugin) {
        this.key = new NamespacedKey(plugin, "dimension_structure_table");
    }

    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
        ArrayList<ItemStack> items = new ArrayList<>();

        //trash
        items.add(new ItemStack(Material.STRING, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.ROTTEN_FLESH, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.BONE, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.GUNPOWDER, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.SPIDER_EYE, randomCount(random, 1, 8)));
        items.add(new ItemStack(Material.FLINT, randomCount(random, 1, 8)));

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

        items.add(new ItemStack(Material.MUSIC_DISC_STAL));
        items.add(new ItemStack(Material.SHULKER_SHELL, randomCount(random, 1, 4)));
        items.add(new ItemStack(Material.LEAD, randomCount(random, 1, 2)));

        items.add(new ItemStack(Material.NETHER_STAR));
        items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
        items.add(CustomItem.FISHING_CRATE_HARDMODE.getItem());
        items.add(CustomItem.RECALL_POTION.getItem());
        items.add(CustomItem.MANA_POTION.getItem());
        items.add(CustomItem.ANTIDOTE.getItem());
        items.add(CustomItem.PURIFIED_WATER.getItem());
        items.add(CustomItem.DIMENSIONAL_ANCHOR.getItem());
        items.add(CustomItem.MERGED_SOULS.getItem());
        items.add(CustomItem.ENERGIUM.getItem());

        ItemStack taintedPowder = CustomItem.TAINTED_POWDER.getItem();
        taintedPowder.setAmount(randomCount(random, 1, 8));
        items.add(taintedPowder);

        ItemStack titaniumFragment = CustomItem.TITANIUM_FRAGMENT.getItem();
        titaniumFragment.setAmount(randomCount(random, 1, 8));
        items.add(titaniumFragment);

        ItemStack voidmatter = CustomItem.VOIDMATTER.getItem();
        voidmatter.setAmount(randomCount(random, 1, 2));
        items.add(voidmatter);

        ItemStack blessedApple = CustomItem.BLESSED_APPLE.getItem();
        blessedApple.setAmount(randomCount(random, 1, 6));
        items.add(blessedApple);

        ItemStack enchantedBlessedApple = CustomItem.ENCHANTED_BLESSED_APPLE.getItem();
        enchantedBlessedApple.setAmount(randomCount(random, 1, 4));
        items.add(enchantedBlessedApple);

        ItemStack treasureGearVoucher = new ItemStack(Material.PAPER);
        ItemMeta voucherMeta = treasureGearVoucher.getItemMeta();
        voucherMeta.setDisplayName("§rTreasure Gear Voucher");
        treasureGearVoucher.setItemMeta(voucherMeta);
        items.add(treasureGearVoucher);

        TreasureGear treasureGear = new TreasureGear();
        Collection<ItemStack> output = new ArrayList<>();
        final int count = random.nextInt(16) + 1;
        for (int i = 0; i < count; i++) {
            ItemStack item = items.get(random.nextInt(items.size()));
            if (item.isSimilar(treasureGearVoucher)) {
                output.add(treasureGear.getTreasureGear(true, armor[random.nextInt(armor.length)], random));
            }
            else {
                output.add(item);
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
