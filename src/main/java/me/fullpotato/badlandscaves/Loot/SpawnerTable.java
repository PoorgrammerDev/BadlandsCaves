package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.Util.ItemBuilder;
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
    private final CustomItemManager customItemManager;
    private final NamespacedKey key;
    private final Player player;
    private final EntityType spawnerType;
    private final int fortune;
    private final HashMap<EntityType, ItemStack> matchSoul = new HashMap<>();
    private final ItemStack saplingVoucher = new ItemBuilder(Material.PAPER).setName("Sapling Voucher").setCustomModelData(0).build();
    private final Material[] saplings = {
            Material.ACACIA_SAPLING,
            Material.BIRCH_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.DARK_OAK_SAPLING,
    };

    public SpawnerTable(BadlandsCaves plugin, Player player, EntityType spawnerType, int fortune) {
        this.plugin = plugin;
        this.player = player;
        this.spawnerType = spawnerType;

        key = new NamespacedKey(plugin, "mob_spawner_treasure");
        this.fortune = fortune;
        customItemManager = plugin.getCustomItemManager();

        matchSoul.put(EntityType.ZOMBIE, customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        matchSoul.put(EntityType.CREEPER, customItemManager.getItem(CustomItem.CREEPER_SOUL));
        matchSoul.put(EntityType.SKELETON, customItemManager.getItem(CustomItem.SKELETON_SOUL));
        matchSoul.put(EntityType.WITHER_SKELETON, customItemManager.getItem(CustomItem.SKELETON_SOUL));
        matchSoul.put(EntityType.SPIDER, customItemManager.getItem(CustomItem.SPIDER_SOUL));
        matchSoul.put(EntityType.CAVE_SPIDER, customItemManager.getItem(CustomItem.SPIDER_SOUL));
        matchSoul.put(EntityType.SILVERFISH, customItemManager.getItem(CustomItem.SILVERFISH_SOUL));
        matchSoul.put(EntityType.GHAST, customItemManager.getItem(CustomItem.GHAST_SOUL));
        matchSoul.put(EntityType.ZOMBIFIED_PIGLIN, customItemManager.getItem(CustomItem.PIGZOMBIE_SOUL));
        matchSoul.put(EntityType.PHANTOM, customItemManager.getItem(CustomItem.PHANTOM_SOUL));
        matchSoul.put(EntityType.WITCH, customItemManager.getItem(CustomItem.WITCH_SOUL));
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, LootContext context) {
        final double luck = context.getLuck();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int count = Math.max(Math.min(random.nextInt((int) Math.floor(Math.pow((luck + 10.0) / 4.0, 1.79) + 3.0 + (chaos / 10.0))) + fortune, 50), 10);
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final boolean supernatural = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;


        final ItemStack rune = customItemManager.getItem(CustomItem.RUNE);
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
            generic.add(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION));
            generic.add(customItemManager.getItem(CustomItem.FISHING_CRATE_HARDMODE));
            generic.add(customItemManager.getItem(CustomItem.RECALL_POTION));
            generic.add(customItemManager.getItem(CustomItem.DIMENSIONAL_ANCHOR));

            if (supernatural) {
                //HARDMODE SUPERNATURAL------------------------------------------------------------------------
                specific.add(rune);
                specific.add(new ItemStack(Material.LAPIS_BLOCK, 32));
                specific.add(customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
                specific.add(customItemManager.getItem(CustomItem.CREEPER_SOUL));
                specific.add(customItemManager.getItem(CustomItem.SILVERFISH_SOUL));
                specific.add(customItemManager.getItem(CustomItem.SKELETON_SOUL));
                specific.add(customItemManager.getItem(CustomItem.SPIDER_SOUL));
                specific.add(customItemManager.getItem(CustomItem.SILVERFISH_SOUL));
                specific.add(customItemManager.getItem(CustomItem.GHAST_SOUL));
                specific.add(customItemManager.getItem(CustomItem.PIGZOMBIE_SOUL));
                specific.add(customItemManager.getItem(CustomItem.PHANTOM_SOUL));
                specific.add(customItemManager.getItem(CustomItem.WITCH_SOUL));

            }
            else {
                //HARDMODE PURESOUL------------------------------------------------------------------------------
                specific.add(customItemManager.getItem(CustomItem.TITANIUM_FRAGMENT));
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
            generic.add(saplingVoucher);
            generic.add(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION));
            generic.add(customItemManager.getItem(CustomItem.RECALL_POTION));

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
                specific.add(customItemManager.getItem(CustomItem.VOLTSHOCK_BATTERY));
                specific.add(customItemManager.getItem(CustomItem.VOLTSHOCK_SHOCKER));
                specific.add(customItemManager.getItem(CustomItem.CORROSIVE_SUBSTANCE));
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

            //special items
            if (item.isSimilar(saplingVoucher)) {
                item = new ItemStack(saplings[random.nextInt(saplings.length)]);
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
