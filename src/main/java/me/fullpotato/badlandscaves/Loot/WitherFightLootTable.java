package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
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

public class WitherFightLootTable implements LootTable {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final NamespacedKey key;
    public WitherFightLootTable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "wither_fight_table");
        customItemManager = plugin.getCustomItemManager();
    }

    @Override
    public @NotNull Collection<ItemStack> populateLoot(Random random, LootContext lootContext) {
        ArrayList<ItemStack> items = new ArrayList<>();

        items.add(customItemManager.getItem(CustomItem.BLESSED_APPLE));
        items.add(customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE));
        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        items.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
        items.add(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION));
        items.add(customItemManager.getItem(CustomItem.RECALL_POTION));
        items.add(new ItemStack(Material.SHULKER_SHELL, 2));
        items.add(new ItemStack(Material.DIAMOND_BLOCK, 2));
        items.add(new ItemStack(Material.NETHERITE_SCRAP));
        items.add(customItemManager.getItem(CustomItem.TITANIUM_FRAGMENT));
        items.add(customItemManager.getItem(CustomItem.VOIDMATTER));
        items.add(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER));

        TreasureGear treasureGear = new TreasureGear();
        Collection<ItemStack> output = new ArrayList<>();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int luck = (int) lootContext.getLuck();
        final int count = random.nextInt(5) + 2 + ((chaos / 10) > 0 ? random.nextInt(chaos / 10) : 0) + (luck > 0 ? random.nextInt(luck) : 0);
        int failed = 0;
        for (int i = 0; i < count; i++) {
            if (failed > 100) break;
            final ItemStack item = items.get(random.nextInt(items.size()));

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

        output.add(new ItemStack(Material.NETHER_STAR));
        return output;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {

    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
