package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class fishingCrateTreasureTable implements LootTable {

    private Plugin plugin = BadlandsCaves.getPlugin(BadlandsCaves.class);
    private NamespacedKey key = new NamespacedKey(plugin, "fishing_crate_treasure");
    private ArrayList<ItemStack> items = new ArrayList<>();

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        double luck_modifier;

        double luck_level = context.getLuck();
        if (luck_level > 0) {
            luck_modifier = Math.pow(luck_level, 0.5) + 5;
        }
        else if (luck_level == 0){
            luck_modifier = 2;
        }
        else {
            luck_modifier = 1;
        }
        int random_modifier = random.nextInt(5) + 2;
        int item_count = (int) (luck_modifier * random_modifier) / 3;

        ItemStack antidote = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.antidote").getValues(true));
        ItemStack purified_water = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purified_water").getValues(true));

        ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));
        tainted_powder.setAmount(8);

        //TODO clean this mess up by serializing it
        ItemStack diamond_helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack diamond_chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack diamond_leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack diamond_boots = new ItemStack(Material.DIAMOND_BOOTS);

        ItemMeta diamond_armor_meta = diamond_chestplate.getItemMeta();
        diamond_armor_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, item_count / 3, false);

        diamond_helmet .setItemMeta(diamond_armor_meta);
        diamond_chestplate.setItemMeta(diamond_armor_meta);
        diamond_leggings.setItemMeta(diamond_armor_meta);
        diamond_boots.setItemMeta(diamond_armor_meta);

        ItemStack diamond_sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack diamond_axe = new ItemStack(Material.DIAMOND_AXE);
        ItemStack diamond_shovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemStack diamond_pick = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta diamond_sword_meta = diamond_sword.getItemMeta();
        ItemMeta diamond_tools_meta = diamond_pick.getItemMeta();

        diamond_sword_meta.addEnchant(Enchantment.DAMAGE_ALL, item_count / 2, false);
        diamond_tools_meta.addEnchant(Enchantment.DIG_SPEED, item_count / 2, false);

        diamond_sword.setItemMeta(diamond_sword_meta);
        diamond_axe.setItemMeta(diamond_tools_meta);
        diamond_shovel.setItemMeta(diamond_tools_meta);
        diamond_pick.setItemMeta(diamond_tools_meta);


        items.add(new ItemStack(Material.DIAMOND_BLOCK, 1));
        items.add(new ItemStack(Material.IRON_BLOCK, 4));
        items.add(new ItemStack(Material.GOLD_BLOCK, 2));
        items.add(new ItemStack(Material.EMERALD_BLOCK, 1));
        items.add(new ItemStack(Material.COAL_BLOCK, 4));
        items.add(new ItemStack(Material.QUARTZ, 16));
        items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
        items.add(new ItemStack(Material.GOLDEN_APPLE, 4));
        items.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2));
        items.add(new ItemStack(Material.BLAZE_POWDER, 4));
        items.add(new ItemStack(Material.WHEAT_SEEDS, 16));
        items.add(new ItemStack(Material.APPLE, 16));
        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 16));
        items.add(new ItemStack(Material.SHULKER_SHELL));
        items.add(new ItemStack(Material.NETHER_WART, 16));
        items.add(diamond_sword);
        items.add(diamond_axe);
        items.add(diamond_shovel);
        items.add(diamond_pick);
        items.add(diamond_helmet);
        items.add(diamond_chestplate);
        items.add(diamond_leggings);
        items.add(diamond_boots);
        items.add(antidote);
        items.add(purified_water);
        items.add(tainted_powder);

        Collection<ItemStack> output = new ArrayList<>();
        for (int a = 0; a < item_count; a++) {
            int rand_search = random.nextInt(items.size() - 1);
            output.add(items.get(rand_search));
        }

        return output;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
