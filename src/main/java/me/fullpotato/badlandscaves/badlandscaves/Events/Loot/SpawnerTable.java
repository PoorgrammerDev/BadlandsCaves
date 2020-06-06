package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
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
    private BadlandsCaves plugin;
    private NamespacedKey key;
    private Player player;
    private EntityType spawnerType;
    private int fortune;
    private HashMap<EntityType, ItemStack> matchSoul = new HashMap<>();

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
        matchSoul.put(EntityType.PIG_ZOMBIE, CustomItem.PIGZOMBIE_SOUL.getItem());
        matchSoul.put(EntityType.PHANTOM, CustomItem.PHANTOM_SOUL.getItem());
        matchSoul.put(EntityType.WITCH, CustomItem.WITCH_SOUL.getItem());
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext context) {
        final double luck = context.getLuck();
        final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
        final int count = Math.max(Math.min(random.nextInt((int) Math.floor(Math.pow((luck + 10.0) / 4.0, 1.79) + 3.0 + (chaos / 10.0))) + fortune, 50), 5);
        final boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        final boolean supernatural = player.getMetadata("has_supernatural_powers").get(0).asBoolean();


        final ItemStack rune = CustomItem.RUNE.getItem();
        ArrayList<ItemStack> generic = new ArrayList<>();
        ArrayList<ItemStack> specific = new ArrayList<>();
        if (hardmode) {
            //HARDMODE NONSPECIFIC-----------------------------------------------------------------------------
            generic.add(new ItemStack(Material.IRON_BLOCK, 4));
            generic.add(new ItemStack(Material.REDSTONE_BLOCK, 16));
            generic.add(new ItemStack(Material.LAPIS_BLOCK, 8));
            generic.add(new ItemStack(Material.GOLD_BLOCK, 4));
            generic.add(new ItemStack(Material.DIAMOND_BLOCK, 2));
            generic.add(new ItemStack(Material.EMERALD_BLOCK, 2));
            generic.add(new ItemStack(Material.QUARTZ_BLOCK, 8));
            generic.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 16));
            generic.add(CustomItem.FISHING_CRATE_HARDMODE.getItem());

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
                // TODO: 4/25/2020 ARTIFACTS

            }
            else {
                //HARDMODE PURESOUL------------------------------------------------------------------------------
                // TODO: 4/6/2020 AUGMENTS

            }
        }
        else {
            //PREHARDMODE NONSPECIFIC--------------------------------------------------------------------------
            generic.add(new ItemStack(Material.IRON_INGOT, 4));
            generic.add(new ItemStack(Material.REDSTONE, 16));
            generic.add(new ItemStack(Material.LAPIS_LAZULI, 8));
            generic.add(new ItemStack(Material.GOLD_INGOT, 4));
            generic.add(new ItemStack(Material.DIAMOND, 2));
            generic.add(new ItemStack(Material.EMERALD, 2));
            generic.add(new ItemStack(Material.QUARTZ, 8));
            generic.add(new ItemStack(Material.NETHER_WART, 8));
            generic.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 4));
            generic.add(CustomItem.FISHING_CRATE.getItem());

            if (supernatural) {
                //PREHARDMODE SUPERNATURAL---------------------------------------------------------------------
                specific.add(new ItemStack(Material.LAPIS_LAZULI, 32));
                specific.addAll(matchSoul.values());

                if (matchSoul.containsKey(spawnerType)) {
                    ItemStack soul = matchSoul.get(spawnerType);
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
                // TODO: 4/25/2020 maybe add blueprints or something? idk

            }
        }


        ArrayList<ItemStack> output = new ArrayList<>();
        if (supernatural) output.add(rune);

        for (int i = 0; i < count; i++) {
            if (random.nextInt(100) < 10) {
                output.add(specific.get(random.nextInt(specific.size())));
            }
            else {
                output.add(generic.get(random.nextInt(generic.size())));
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
