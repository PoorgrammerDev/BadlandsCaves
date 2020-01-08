package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class zombieLootTable implements LootTable {

    private Plugin plugin = BadlandsCaves.getPlugin(BadlandsCaves.class);
    private NamespacedKey key = new NamespacedKey(plugin, "zombie_loot");
    private Collection<ItemStack> items = new ArrayList<ItemStack>();

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        double looting_modifier;
        double luck_modifier;

        int looting_level = context.getLootingModifier();
        if (looting_level > 0) {
            looting_modifier = (Math.pow(looting_level, 0.8) + 2) / 2;
        }
        else {
            looting_modifier = 1;
        }

        double luck_level = context.getLuck();
        if (luck_level > 0) {
            luck_modifier = Math.pow(luck_level, 0.5) + 0.5;
        }
        else if (luck_level == 0){
            luck_modifier = 1;
        }
        else {
            luck_modifier = 0;
        }
        int random_modifier = random.nextInt(5);
        int item_count = (int) (looting_modifier * luck_modifier * random_modifier) / 2;

        int leather_count;
        int flesh_count;

        if (item_count / 2 > 0) {
            leather_count = random.nextInt(item_count / 2);
            flesh_count = item_count - leather_count;
        }
        else if (item_count > 0) {
            leather_count = 0;
            flesh_count = item_count;
        }
        else {
            leather_count = 0;
            flesh_count = 0;
        }

        //System.out.print("\nLoot:" + looting_modifier + "\nLuck:" + luck_modifier + "\nRand:" + random_modifier + "\nItem:" + item_count + "\nLeather:" + leather_count + "\nFlesh:" + flesh_count);


        ItemStack rotten_flesh = new ItemStack(Material.ROTTEN_FLESH, flesh_count);
        ItemStack leather = new ItemStack(Material.LEATHER, leather_count);

        items.add(rotten_flesh);
        items.add(leather);

        return items;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {

    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
