package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.AlternateDimensions.PregenerateDimensions;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DimensionStructureTable implements LootTable, Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final NamespacedKey key;
    private final Artifact[] artifacts = Artifact.GetLootItems();
    private final PregenerateDimensions pregenerateDimensions;
    private final Map<ItemStack, Integer> surfaceItemMap = new HashMap<>();
    private final Map<ItemStack, Integer> voidItemMap = new HashMap<>();
    private final TreasureGear treasureGear = new TreasureGear();
    private final Random random;

    public DimensionStructureTable(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "dimension_structure_table");
        pregenerateDimensions = new PregenerateDimensions(plugin, random);
        customItemManager = plugin.getCustomItemManager();
        this.random = random;

        surfaceItemMap.put(new ItemStack(Material.NETHERITE_SCRAP), 4);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.VOIDMATTER), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.TITANIUM_INGOT), 1);
        surfaceItemMap.put(new ItemStack(Material.IRON_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.DIAMOND_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.EMERALD_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.QUARTZ_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.GOLD_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.REDSTONE_BLOCK), 2);
        surfaceItemMap.put(new ItemStack(Material.BLAZE_POWDER), 4);
        surfaceItemMap.put(new ItemStack(Material.SHULKER_SHELL), 2);
        surfaceItemMap.put(new ItemStack(Material.LEAD), 2);
        surfaceItemMap.put(new ItemStack(Material.TOTEM_OF_UNDYING), 1);
        surfaceItemMap.put(new ItemStack(Material.WITHER_SKELETON_SKULL), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.FISHING_CRATE_HARDMODE), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.RECALL_POTION), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.MANA_POTION), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.ANTIDOTE), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.PURIFIED_WATER), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.DIMENSIONAL_ANCHOR), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.MERGED_SOULS), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER), 1);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.TAINTED_POWDER), 8);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.BLESSED_APPLE), 4);
        surfaceItemMap.put(customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE), 2);

        voidItemMap.put(new ItemStack(Material.NETHERITE_INGOT), 4);
        voidItemMap.put(customItemManager.getItem(CustomItem.VOIDMATTER), 8);
        voidItemMap.put(customItemManager.getItem(CustomItem.TITANIUM_INGOT), 8);
        voidItemMap.put(customItemManager.getItem(CustomItem.ARTIFACT_VOUCHER), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.NEBULITE_CRATE), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.ENERGIUM), 8);
        voidItemMap.put(new ItemStack(Material.IRON_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.DIAMOND_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.EMERALD_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.QUARTZ_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.GOLD_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.REDSTONE_BLOCK), 8);
        voidItemMap.put(new ItemStack(Material.BLAZE_POWDER), 16);
        voidItemMap.put(new ItemStack(Material.SHULKER_SHELL), 8);
        voidItemMap.put(new ItemStack(Material.TOTEM_OF_UNDYING), 1);
        voidItemMap.put(new ItemStack(Material.WITHER_SKELETON_SKULL), 3);
        voidItemMap.put(customItemManager.getItem(CustomItem.TOTEM_OF_PRESERVATION), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.FISHING_CRATE_HARDMODE), 4);
        voidItemMap.put(customItemManager.getItem(CustomItem.RECALL_POTION), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.MANA_POTION), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.DIMENSIONAL_ANCHOR), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.NEBULITE_INSTALLER), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER), 1);
        voidItemMap.put(customItemManager.getItem(CustomItem.ENCHANTED_BLESSED_APPLE), 4);
    }

    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
        final ArrayList<ItemStack> surfaceList = new ArrayList<>(surfaceItemMap.keySet());
        final ArrayList<ItemStack> voidList = new ArrayList<>(voidItemMap.keySet());

        final Collection<ItemStack> output = new ArrayList<>();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        int count = ((chaos / 7) > 0 ? random.nextInt(chaos / 7) : 0) + random.nextInt(5) + 5;

        final boolean isVoid = lootContext.getLocation() != null && lootContext.getLocation().getBlock().getBiome() == Biome.GRAVELLY_MOUNTAINS;
        if (isVoid) count *= 2;
        count = Math.min(count, 27);

        for (int i = 0; i < count; i++) {
            final ItemStack item;
            if (isVoid) {
                item = voidList.get(random.nextInt(voidList.size()));
                if (voidItemMap.containsKey(item)) {
                    final int amount = voidItemMap.get(item);
                    item.setAmount(amount > 1 ? randomCount(random, 1, amount) : 1);
                }
            }
            else {
                item = surfaceList.get(random.nextInt(surfaceList.size()));
                if (surfaceItemMap.containsKey(item)) {
                    final int amount = surfaceItemMap.get(item);
                    item.setAmount(amount > 1 ? randomCount(random, 1, amount) : 1);
                }
            }

                if (item.isSimilar(customItemManager.getItem(CustomItem.TREASURE_GEAR_VOUCHER))) {
                    output.add(treasureGear.getTreasureGear(true, random));
                }
                else if (item.isSimilar(customItemManager.getItem(CustomItem.ARTIFACT_VOUCHER))) {
                    output.add(customItemManager.getItem(artifacts[random.nextInt(artifacts.length)].getArtifactItem()));
                }
                else {
                    if (item.isSimilar(customItemManager.getItem(CustomItem.DIMENSIONAL_ANCHOR))) {
                        output.add(pregenerateDimensions.getDimensionalAnchor());
                    }
                    else {
                        output.add(item);
                    }
                }
        }
        return output;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {
        for (ItemStack item : populateLoot(random, lootContext)) {
            final int slot = random.nextInt(inventory.getSize());
            final ItemStack current = inventory.getItem(slot);
            if (current == null || current.getType().isAir()) {
                inventory.setItem(slot, item);
            }
            else {
                inventory.addItem(item);
            }
        }
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    /**Returns a random integer between min and max, inclusive. */
    private int randomCount (Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    @EventHandler
    public void openBarrel(LootGenerateEvent event) {
        if (event.getInventoryHolder() instanceof Barrel) {
            Barrel barrel = (Barrel) event.getInventoryHolder();
            if (attemptGenerateLoot(barrel, event.getLootContext())) event.setCancelled(true);
        }
    }

    @EventHandler
    public void breakBarrel (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.BARREL)) {
            if (block.getState() instanceof Barrel) {
                Barrel state = (Barrel) block.getState();
                attemptGenerateLoot(state, new LootContext.Builder(block.getLocation()).build());
            }
        }
    }

    public boolean attemptGenerateLoot(Barrel barrel, LootContext lootContext) {
        if (barrel.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            final Byte result = barrel.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
            if (result != null && result == 1) {
                barrel.getPersistentDataContainer().remove(key);
                barrel.update(true);
                fillInventory(barrel.getInventory(), random, lootContext);
                return true;
            }
        }
        return false;
    }
}
