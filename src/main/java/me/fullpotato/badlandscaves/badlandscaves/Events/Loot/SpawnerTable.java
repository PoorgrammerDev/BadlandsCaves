package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
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
    private HashMap<EntityType, String> matchSoul = new HashMap<>();

    public SpawnerTable(BadlandsCaves plugin, Player player, EntityType spawnerType, int fortune) {
        this.plugin = plugin;
        this.player = player;
        this.spawnerType = spawnerType;

        key = new NamespacedKey(plugin, "mob_spawner_treasure");
        this.fortune = fortune;

        matchSoul.put(EntityType.ZOMBIE, "zombie_soul");
        matchSoul.put(EntityType.CREEPER, "creeper_soul");
        matchSoul.put(EntityType.SKELETON, "skeleton_soul");
        matchSoul.put(EntityType.WITHER_SKELETON, "skeleton_soul");
        matchSoul.put(EntityType.SPIDER, "spider_soul");
        matchSoul.put(EntityType.CAVE_SPIDER, "spider_soul");
        matchSoul.put(EntityType.SILVERFISH, "silverfish_soul");
        matchSoul.put(EntityType.GHAST, "ghast_soul");
        matchSoul.put(EntityType.PIG_ZOMBIE, "pigzombie_soul");
        matchSoul.put(EntityType.PHANTOM, "phantom_soul");
        matchSoul.put(EntityType.WITCH, "witch_soul");
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext context) {
        final double luck = context.getLuck();
        final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
        final int count = Math.max(Math.min(random.nextInt((int) Math.floor(Math.pow((luck + 10.0) / 4.0, 1.79) + 3.0 + (chaos / 10.0))) + fortune, 50), 5);
        final boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        final boolean supernatural = player.getMetadata("has_supernatural_powers").get(0).asBoolean();


        final ItemStack rune = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.rune").getValues(true));
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
            generic.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate_hardmode").getValues(true)));

            if (supernatural) {
                //HARDMODE SUPERNATURAL------------------------------------------------------------------------
                specific.add(rune);
                specific.add(new ItemStack(Material.LAPIS_BLOCK, 32));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)));
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
            generic.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate").getValues(true)));

            if (supernatural) {
                //PREHARDMODE SUPERNATURAL---------------------------------------------------------------------
                specific.add(new ItemStack(Material.LAPIS_LAZULI, 32));
                for (EntityType type : matchSoul.keySet()) {
                    specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + matchSoul.get(type)).getValues(true)));
                }

                if (matchSoul.containsKey(spawnerType)) {
                    ItemStack soul = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + matchSoul.get(spawnerType)).getValues(true));
                    soul.setAmount(16);
                    specific.add(soul);
                }

            }
            else {
                //PREHARDMODE PURESOUL---------------------------------------------------------------------------
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_battery").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_shocker").getValues(true)));
                specific.add(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true)));
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
