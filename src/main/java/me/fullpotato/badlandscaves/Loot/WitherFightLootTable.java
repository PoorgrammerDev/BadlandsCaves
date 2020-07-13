package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WitherFightLootTable implements LootTable {
    private final BadlandsCaves plugin;
    private final NamespacedKey key;
    public WitherFightLootTable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "wither_fight_table");
    }

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext lootContext) {
        ArrayList<ItemStack> items = new ArrayList<>();

        items.add(CustomItem.BLESSED_APPLE.getItem());
        items.add(CustomItem.ENCHANTED_BLESSED_APPLE.getItem());
        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        items.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
        items.add(CustomItem.TOTEM_OF_PRESERVATION.getItem());
        items.add(CustomItem.RECALL_POTION.getItem());
        items.add(new ItemStack(Material.SHULKER_SHELL, 2));
        items.add(new ItemStack(Material.DIAMOND_BLOCK, 2));
        items.add(new ItemStack(Material.NETHERITE_SCRAP));
        items.add(CustomItem.TITANIUM_FRAGMENT.getItem());
        items.add(CustomItem.VOIDMATTER.getItem());
        items.add(CustomItem.TREASURE_GEAR_VOUCHER.getItem());

        TreasureGear treasureGear = new TreasureGear();
        Collection<ItemStack> output = new ArrayList<>();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int luck = (int) lootContext.getLuck();
        final int count = random.nextInt(5) + 2 + ((chaos / 10) > 0 ? random.nextInt(chaos / 10) : 0) + (luck > 0 ? random.nextInt(luck) : 0);
        int failed = 0;
        for (int i = 0; i < count; i++) {
            if (failed > 100) break;
            final ItemStack item = items.get(random.nextInt(items.size()));

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

        output.add(new ItemStack(Material.NETHER_STAR));
        return output;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext lootContext) {

    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
