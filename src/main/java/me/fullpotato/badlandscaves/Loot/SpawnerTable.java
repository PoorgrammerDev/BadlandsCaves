package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class SpawnerTable implements LootTable {
    private final BadlandsCaves plugin;
    private final NamespacedKey key;
    private final Player player;
    private final EntityType spawnerType;
    private final int fortune;
    private final HashMap<EntityType, ItemStack> matchSoul = new HashMap<>();

    public SpawnerTable(BadlandsCaves plugin, Player player, EntityType spawnerType, int fortune) {
        this.plugin = plugin;
        this.player = player;
        this.spawnerType = spawnerType;

        key = new NamespacedKey(plugin, "mob_spawner_treasure");
        this.fortune = fortune;

        matchSoul.put(EntityType.ZOMBIE, CustomItem.ZOMBIE_SOUL.getItem());
        matchSoul.put(EntityType.CREEPER, CustomItem.CREEPER_SOUL.getItem());
        matchSoul.put(EntityType.SKELETON, CustomItem.SKELETON_SOUL.getItem());
        matchSoul.put(EntityType.WITHER_SKELETON, CustomItem.SKELETON_SOUL.getItem());
        matchSoul.put(EntityType.SPIDER, CustomItem.SPIDER_SOUL.getItem());
        matchSoul.put(EntityType.CAVE_SPIDER, CustomItem.SPIDER_SOUL.getItem());
        matchSoul.put(EntityType.SILVERFISH, CustomItem.SILVERFISH_SOUL.getItem());
        matchSoul.put(EntityType.GHAST, CustomItem.GHAST_SOUL.getItem());
        matchSoul.put(EntityType.ZOMBIFIED_PIGLIN, CustomItem.PIGZOMBIE_SOUL.getItem());
        matchSoul.put(EntityType.PHANTOM, CustomItem.PHANTOM_SOUL.getItem());
        matchSoul.put(EntityType.WITCH, CustomItem.WITCH_SOUL.getItem());
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, LootContext context) {
        final double luck = context.getLuck();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int count = Math.max(Math.min(random.nextInt((int) Math.floor(Math.pow((luck + 10.0) / 4.0, 1.79) + 3.0 + (chaos / 10.0))) + fortune, 50), 10);
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final boolean supernatural = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;


        final ItemStack rune = CustomItem.RUNE.getItem();
        ArrayList<ItemStack> generic = new ArrayList<>();
        ArrayList<ItemStack> specific = new ArrayList<>();
        if (hardmode) {
            //HARDMODE NONSPECIFIC-----------------------------------------------------------------------------
            generic.add(new ItemStack(Material.IRON_BLOCK, 16));
            generic.add(new ItemStack(Material.REDSTONE_BLOCK, 32));
            generic.add(new ItemStack(Material.LAPIS_BLOCK, 32));
            generic.add(new ItemStack(Material.GOLD_BLOCK, 16));
            generic.add(new ItemStack(Material.DIAMOND_BLOCK, 4));
            generic.add(new ItemStack(Material.EMERALD_BLOCK, 4));
            generic.add(new ItemStack(Material.QUARTZ_BLOCK, 16));
            generic.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 8));
            generic.add(new ItemStack(Material.TOTEM_OF_UNDYING));
            generic.add(CustomItem.TOTEM_OF_PRESERVATION.getItem());
            generic.add(CustomItem.FISHING_CRATE_HARDMODE.getItem());
            generic.add(CustomItem.RECALL_POTION.getItem());
            generic.add(CustomItem.DIMENSIONAL_ANCHOR.getItem());

            if (supernatural) {
                //HARDMODE SUPERNATURAL------------------------------------------------------------------------
                specific.add(rune);
                specific.add(new ItemStack(Material.LAPIS_BLOCK, 32));
                specific.add(CustomItem.ZOMBIE_SOUL.getItem());
                specific.add(CustomItem.CREEPER_SOUL.getItem());
                specific.add(CustomItem.SILVERFISH_SOUL.getItem());
                specific.add(CustomItem.SKELETON_SOUL.getItem());
                specific.add(CustomItem.SPIDER_SOUL.getItem());
                specific.add(CustomItem.SILVERFISH_SOUL.getItem());
                specific.add(CustomItem.GHAST_SOUL.getItem());
                specific.add(CustomItem.PIGZOMBIE_SOUL.getItem());
                specific.add(CustomItem.PHANTOM_SOUL.getItem());
                specific.add(CustomItem.WITCH_SOUL.getItem());

            }
            else {
                //HARDMODE PURESOUL------------------------------------------------------------------------------
                specific.add(CustomItem.TITANIUM_FRAGMENT.getItem());
            }
        }
        else {
            //PREHARDMODE NONSPECIFIC--------------------------------------------------------------------------
            generic.add(new ItemStack(Material.IRON_INGOT, 16));
            generic.add(new ItemStack(Material.REDSTONE, 32));
            generic.add(new ItemStack(Material.LAPIS_LAZULI, 32));
            generic.add(new ItemStack(Material.GOLD_INGOT, 16));
            generic.add(new ItemStack(Material.DIAMOND, 4));
            generic.add(new ItemStack(Material.EMERALD, 4));
            generic.add(new ItemStack(Material.QUARTZ, 16));
            generic.add(new ItemStack(Material.NETHER_WART, 8));
            generic.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 8));
            generic.add(new ItemStack(Material.TOTEM_OF_UNDYING));
            generic.add(new ItemStack(Material.ACACIA_SAPLING));
            generic.add(new ItemStack(Material.BIRCH_SAPLING));
            generic.add(new ItemStack(Material.SPRUCE_SAPLING));
            generic.add(new ItemStack(Material.JUNGLE_SAPLING));
            generic.add(new ItemStack(Material.DARK_OAK_SAPLING));
            generic.add(CustomItem.TOTEM_OF_PRESERVATION.getItem());
            generic.add(CustomItem.RECALL_POTION.getItem());

            if (supernatural) {
                //PREHARDMODE SUPERNATURAL---------------------------------------------------------------------
                specific.add(new ItemStack(Material.LAPIS_LAZULI, 32));
                specific.addAll(matchSoul.values());

                if (matchSoul.containsKey(spawnerType)) {
                    final ItemStack soul = matchSoul.get(spawnerType);
                    soul.setAmount(16);
                    specific.add(soul);
                }
            }
            else {
                //PREHARDMODE PURESOUL---------------------------------------------------------------------------
                specific.add(CustomItem.VOLTSHOCK_BATTERY.getItem());
                specific.add(CustomItem.VOLTSHOCK_SHOCKER.getItem());
                specific.add(CustomItem.CORROSIVE_SUBSTANCE.getItem());
                specific.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 32));
            }
        }


        ArrayList<ItemStack> output = new ArrayList<>();
        if (supernatural) output.add(rune);

        final int threshold = 20;
        int failed = 0;
        for (int i = 0; i < count; i++) {
            if (failed > threshold) break;

            ItemStack item;
            if (random.nextInt(100) < 10) {
                item = specific.get(random.nextInt(specific.size()));
            }
            else {
                item = generic.get(random.nextInt(generic.size()));
            }

            if (output.contains(item)) {
                i--;
                failed++;
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
        return key;
    }
}
