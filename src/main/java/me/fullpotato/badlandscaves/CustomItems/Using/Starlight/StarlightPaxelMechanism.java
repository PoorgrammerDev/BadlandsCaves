package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class StarlightPaxelMechanism implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightTools toolManager;
    private final StarlightCharge chargeManager;
    private final NebuliteManager nebuliteManager;
    private final Random random;

    //TODO: refactor these, should not be public
    public final HashSet<Material> pickaxeBlocks;
    public final HashSet<Material> axeBlocks;
    public final HashSet<Material> shovelBlocks;

    private final HashMap<Material, Material> smeltingBlocks;

    public StarlightPaxelMechanism(BadlandsCaves plugin, StarlightTools toolManager, StarlightCharge chargeManager, NebuliteManager nebuliteManager, Random random) {
        this.plugin = plugin;
        this.toolManager = toolManager;
        this.chargeManager = chargeManager;
        this.nebuliteManager = nebuliteManager;
        this.random = random;

        pickaxeBlocks = new HashSet<>(Arrays.asList(Material.ACTIVATOR_RAIL, Material.ANCIENT_DEBRIS, Material.ANDESITE, Material.ANDESITE_STAIRS, Material.ANDESITE_SLAB, Material.ANDESITE_WALL, Material.ANVIL, Material.BASALT, Material.BEACON, Material.BELL, Material.BLACKSTONE, Material.BLACK_CONCRETE, Material.BLACK_GLAZED_TERRACOTTA, Material.BLACK_SHULKER_BOX, Material.BLACK_TERRACOTTA, Material.BLAST_FURNACE, Material.BLUE_CONCRETE, Material.BLUE_GLAZED_TERRACOTTA, Material.BLUE_ICE, Material.BLUE_SHULKER_BOX, Material.BLUE_TERRACOTTA, Material.BONE_BLOCK, Material.BRAIN_CORAL_BLOCK, Material.BREWING_STAND, Material.BRICKS, Material.BRICK_SLAB, Material.BRICK_WALL, Material.BRICK_STAIRS, Material.BROWN_CONCRETE, Material.BROWN_GLAZED_TERRACOTTA, Material.BROWN_SHULKER_BOX, Material.BROWN_TERRACOTTA, Material.BUBBLE_CORAL_BLOCK, Material.CAULDRON, Material.CHAIN, Material.CHIPPED_ANVIL, Material.CHISELED_NETHER_BRICKS, Material.CHISELED_QUARTZ_BLOCK, Material.CHISELED_RED_SANDSTONE, Material.CHISELED_SANDSTONE, Material.CHISELED_STONE_BRICKS, Material.COAL_BLOCK, Material.COAL_ORE, Material.COBBLESTONE, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_WALL, Material.CONDUIT, Material.CRACKED_NETHER_BRICKS, Material.CRACKED_STONE_BRICKS, Material.CRIMSON_NYLIUM, Material.CRYING_OBSIDIAN, Material.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE_SLAB, Material.CUT_SANDSTONE, Material.CUT_SANDSTONE_SLAB, Material.CYAN_CONCRETE, Material.CYAN_GLAZED_TERRACOTTA, Material.CYAN_SHULKER_BOX, Material.CYAN_TERRACOTTA, Material.DAMAGED_ANVIL, Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_SLAB, Material.DARK_PRISMARINE_STAIRS, Material.DEAD_BRAIN_CORAL_BLOCK, Material.DEAD_BUBBLE_CORAL_BLOCK, Material.DEAD_FIRE_CORAL_BLOCK, Material.DEAD_HORN_CORAL_BLOCK, Material.DEAD_TUBE_CORAL_BLOCK, Material.DETECTOR_RAIL, Material.DIAMOND_BLOCK, Material.DIAMOND_ORE, Material.DIORITE, Material.DIORITE_STAIRS, Material.DIORITE_WALL, Material.DISPENSER, Material.DROPPER, Material.EMERALD_BLOCK, Material.EMERALD_ORE, Material.ENCHANTING_TABLE, Material.ENDER_CHEST, Material.END_STONE, Material.END_STONE_BRICKS, Material.END_STONE_BRICK_SLAB, Material.END_STONE_BRICK_STAIRS, Material.END_STONE_BRICK_WALL, Material.FIRE_CORAL_BLOCK, Material.FURNACE, Material.GILDED_BLACKSTONE, Material.GOLD_BLOCK, Material.GOLD_ORE, Material.GRANITE, Material.GRANITE_STAIRS, Material.GRANITE_WALL, Material.GRAY_CONCRETE, Material.GRAY_GLAZED_TERRACOTTA, Material.GRAY_SHULKER_BOX, Material.GRAY_TERRACOTTA, Material.GREEN_CONCRETE, Material.GREEN_GLAZED_TERRACOTTA, Material.GREEN_SHULKER_BOX, Material.GREEN_TERRACOTTA, Material.GRINDSTONE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.HOPPER, Material.HORN_CORAL_BLOCK, Material.ICE, Material.IRON_BARS, Material.IRON_BLOCK, Material.IRON_DOOR, Material.IRON_ORE, Material.IRON_TRAPDOOR, Material.LANTERN, Material.LAPIS_BLOCK, Material.LAPIS_ORE, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_SHULKER_BOX, Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.LIME_CONCRETE, Material.LIME_GLAZED_TERRACOTTA, Material.LIME_SHULKER_BOX, Material.LIME_TERRACOTTA, Material.LODESTONE, Material.MAGENTA_CONCRETE, Material.MAGENTA_GLAZED_TERRACOTTA, Material.MAGENTA_SHULKER_BOX, Material.MAGENTA_TERRACOTTA, Material.MAGMA_BLOCK, Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_WALL, Material.MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_WALL, Material.NETHERITE_BLOCK, Material.NETHERRACK, Material.NETHER_BRICKS, Material.NETHER_BRICK_FENCE, Material.NETHER_BRICK_SLAB, Material.NETHER_BRICK_STAIRS, Material.NETHER_BRICK_WALL, Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE, Material.OBSERVER, Material.OBSIDIAN, Material.ORANGE_CONCRETE, Material.ORANGE_GLAZED_TERRACOTTA, Material.ORANGE_SHULKER_BOX, Material.ORANGE_TERRACOTTA, Material.PACKED_ICE, Material.PINK_CONCRETE, Material.PINK_GLAZED_TERRACOTTA, Material.PINK_SHULKER_BOX, Material.PINK_TERRACOTTA, Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB, Material.POLISHED_ANDESITE_STAIRS, Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_SLAB, Material.POLISHED_DIORITE_STAIRS, Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_SLAB, Material.POLISHED_GRANITE_STAIRS, Material.POWERED_RAIL, Material.PRISMARINE, Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_SLAB, Material.PRISMARINE_STAIRS, Material.PRISMARINE_WALL, Material.PURPLE_CONCRETE, Material.PURPLE_GLAZED_TERRACOTTA, Material.PURPLE_SHULKER_BOX, Material.PURPLE_TERRACOTTA, Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, Material.PURPUR_SLAB, Material.PURPUR_STAIRS, Material.QUARTZ_BLOCK, Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR, Material.QUARTZ_SLAB, Material.QUARTZ_STAIRS, Material.RAIL, Material.REDSTONE_BLOCK, Material.REDSTONE_ORE, Material.RED_CONCRETE, Material.RED_GLAZED_TERRACOTTA, Material.RED_NETHER_BRICKS, Material.RED_NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_WALL, Material.RED_SANDSTONE, Material.RED_SANDSTONE_SLAB, Material.RED_SANDSTONE_STAIRS, Material.RED_SANDSTONE_WALL, Material.RED_SHULKER_BOX, Material.RED_TERRACOTTA, Material.RESPAWN_ANCHOR, Material.SANDSTONE, Material.SANDSTONE_SLAB, Material.SANDSTONE_STAIRS, Material.SANDSTONE_WALL, Material.SHULKER_BOX, Material.SMOKER, Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_SLAB, Material.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE_SLAB, Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE, Material.SMOOTH_SANDSTONE_SLAB, Material.SMOOTH_SANDSTONE_STAIRS, Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB, Material.SOUL_LANTERN, Material.SPAWNER, Material.STONE, Material.STONECUTTER, Material.STONE_BRICKS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_WALL, Material.STONE_BUTTON, Material.STONE_PRESSURE_PLATE, Material.STONE_SLAB, Material.STONE_STAIRS, Material.TERRACOTTA, Material.TUBE_CORAL_BLOCK, Material.WARPED_NYLIUM, Material.WHITE_CONCRETE, Material.WHITE_GLAZED_TERRACOTTA, Material.WHITE_SHULKER_BOX, Material.WHITE_TERRACOTTA, Material.YELLOW_CONCRETE, Material.YELLOW_GLAZED_TERRACOTTA, Material.YELLOW_SHULKER_BOX, Material.YELLOW_TERRACOTTA, Material.POLISHED_BASALT, Material.POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, Material.POLISHED_BLACKSTONE, Material.BLACKSTONE_SLAB, Material.CHISELED_POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_STAIRS, Material.POLISHED_BLACKSTONE_BRICK_STAIRS, Material.POLISHED_BLACKSTONE_SLAB, Material.POLISHED_BLACKSTONE_BRICK_SLAB, Material.POLISHED_BLACKSTONE_BRICK_WALL, Material.POLISHED_BLACKSTONE_WALL, Material.BLACKSTONE_WALL, Material.STRUCTURE_BLOCK));
        axeBlocks = new HashSet<>(Arrays.asList(Material.ACACIA_BUTTON, Material.ACACIA_DOOR, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE, Material.ACACIA_LOG, Material.ACACIA_PLANKS, Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_SIGN, Material.ACACIA_SLAB, Material.ACACIA_STAIRS, Material.ACACIA_TRAPDOOR, Material.ACACIA_WALL_SIGN, Material.ACACIA_WOOD, Material.BARREL, Material.BEEHIVE, Material.BEE_NEST, Material.BIRCH_BUTTON, Material.BIRCH_DOOR, Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE, Material.BIRCH_LOG, Material.BIRCH_PLANKS, Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_SIGN, Material.BIRCH_SLAB, Material.BIRCH_STAIRS, Material.BIRCH_TRAPDOOR, Material.BIRCH_WALL_SIGN, Material.BIRCH_WOOD, Material.BLACK_BANNER, Material.BLACK_WALL_BANNER, Material.BLUE_BANNER, Material.BLUE_WALL_BANNER, Material.BOOKSHELF, Material.BROWN_BANNER, Material.BROWN_MUSHROOM_BLOCK, Material.BROWN_WALL_BANNER, Material.CAMPFIRE, Material.CARTOGRAPHY_TABLE, Material.CARVED_PUMPKIN, Material.CHEST, Material.CHORUS_PLANT, Material.COCOA, Material.COMPOSTER, Material.CRAFTING_TABLE, Material.CRIMSON_STEM, Material.CYAN_BANNER, Material.CYAN_WALL_BANNER, Material.DARK_OAK_BUTTON, Material.DARK_OAK_DOOR, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_SIGN, Material.DARK_OAK_SLAB, Material.DARK_OAK_STAIRS, Material.DARK_OAK_TRAPDOOR, Material.DARK_OAK_WALL_SIGN, Material.DARK_OAK_WOOD, Material.DAYLIGHT_DETECTOR, Material.FLETCHING_TABLE, Material.GRAY_BANNER, Material.GRAY_WALL_BANNER, Material.GREEN_BANNER, Material.GREEN_WALL_BANNER, Material.JACK_O_LANTERN, Material.JUKEBOX, Material.JUNGLE_BUTTON, Material.JUNGLE_DOOR, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE, Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_SIGN, Material.JUNGLE_SLAB, Material.JUNGLE_STAIRS, Material.JUNGLE_TRAPDOOR, Material.JUNGLE_WALL_SIGN, Material.JUNGLE_WOOD, Material.LADDER, Material.LECTERN, Material.LOOM, Material.MAGENTA_BANNER, Material.MAGENTA_WALL_BANNER, Material.MELON, Material.NOTE_BLOCK, Material.OAK_BUTTON, Material.OAK_DOOR, Material.OAK_FENCE, Material.OAK_FENCE_GATE, Material.OAK_LOG, Material.OAK_PLANKS, Material.OAK_PRESSURE_PLATE, Material.OAK_SIGN, Material.OAK_SLAB, Material.OAK_STAIRS, Material.OAK_TRAPDOOR, Material.OAK_WALL_SIGN, Material.OAK_WOOD, Material.PINK_BANNER, Material.PINK_WALL_BANNER, Material.PUMPKIN, Material.PURPLE_BANNER, Material.PURPLE_WALL_BANNER, Material.RED_BANNER, Material.RED_MUSHROOM_BLOCK, Material.RED_WALL_BANNER, Material.SMITHING_TABLE, Material.SOUL_CAMPFIRE, Material.SPRUCE_BUTTON, Material.SPRUCE_DOOR, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE, Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_SIGN, Material.SPRUCE_SLAB, Material.SPRUCE_STAIRS, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_WALL_SIGN, Material.SPRUCE_WOOD, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD, Material.STRIPPED_WARPED_HYPHAE, Material.STRIPPED_WARPED_STEM, Material.TRAPPED_CHEST, Material.WARPED_STEM, Material.WHITE_BANNER, Material.WHITE_WALL_BANNER, Material.YELLOW_BANNER, Material.YELLOW_WALL_BANNER, Material.CRIMSON_PLANKS, Material.WARPED_PLANKS, Material.CRIMSON_HYPHAE, Material.WARPED_HYPHAE, Material.WARPED_FENCE, Material.WARPED_FENCE_GATE, Material.CRIMSON_FENCE, Material.CRIMSON_FENCE_GATE, Material.CRIMSON_DOOR, Material.CRIMSON_TRAPDOOR, Material.WARPED_DOOR, Material.WARPED_TRAPDOOR, Material.WARPED_STAIRS, Material.CRIMSON_STAIRS));
        shovelBlocks = new HashSet<>(Arrays.asList(Material.BLACK_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER, Material.CLAY, Material.COARSE_DIRT, Material.CYAN_CONCRETE_POWDER, Material.DIRT, Material.FARMLAND, Material.GRASS_BLOCK, Material.GRASS_PATH, Material.GRAVEL, Material.GRAY_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER, Material.MYCELIUM, Material.ORANGE_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER, Material.PODZOL, Material.PURPLE_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER, Material.RED_SAND, Material.SAND, Material.SNOW, Material.SNOW_BLOCK, Material.SOUL_SAND, Material.SOUL_SOIL, Material.WHITE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER));

        smeltingBlocks = new HashMap<>();
        smeltingBlocks.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltingBlocks.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        smeltingBlocks.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
    }

    @EventHandler
    public void blockDamage (BlockDamageEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                final ItemStack item = event.getItemInHand();
                if (toolManager.isStarlightPaxel(item) && chargeManager.getCharge(item) > 0) {
                    final Material type = event.getBlock().getType();

                    if (pickaxeBlocks.contains(type) && !item.getType().equals(Material.NETHERITE_PICKAXE)) {
                        item.setType(Material.NETHERITE_PICKAXE);
                    }
                    else if (axeBlocks.contains(type) && !item.getType().equals(Material.NETHERITE_AXE)) {
                        item.setType(Material.NETHERITE_AXE);
                    }
                    else if (shovelBlocks.contains(type) && !item.getType().equals(Material.NETHERITE_SHOVEL)) {
                        item.setType(Material.NETHERITE_SHOVEL);
                    }
                }
            }
        }
    }

    /**
     * Paxel will auto-smelt and apply fortune
     */
    @EventHandler
    public void AutoSmelt(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        //Must be a valid smeltable block
        final Block block = event.getBlock();
        final Material type = block.getType();        
        if (!this.smeltingBlocks.containsKey(type)) return;

        final Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;
        
        //Player cannot be a magic class
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != (byte) 0) return;
        
        //Must be a charged paxel
        final ItemStack tool = player.getEquipment().getItemInMainHand();

        boolean success = SmeltBreakBlock(block, tool);
        event.setDropItems(!success);
    }

    /**
     * Inner smelt break mechanism
     * @param block Block the player broke
     * @param paxel Paxel tool the player used
     * @return success
     */
    public boolean SmeltBreakBlock(Block block, ItemStack paxel) {
        if (!this.smeltingBlocks.containsKey(block.getType())) return false;
        if (!toolManager.isStarlightPaxel(paxel) || chargeManager.getCharge(paxel) <= 0) return false;

        //Must not have the silk touch nebulite
        if (nebuliteManager.hasNebulite(paxel, Nebulite.DISASSEMBLER)) return false;

        //Immediate disqualification for fortune if efficiency nebulite is installed, otherwise follow default vanilla calculation.
        //Source for vanilla calculation: Minecraft Wiki (https://minecraft.fandom.com/wiki/Fortune)
        int fortuneLevel = paxel.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
        boolean noFortune = nebuliteManager.hasNebulite(paxel, Nebulite.SUPERSONIC_PROPULSORS) || (random.nextInt(100) < (2 / (fortuneLevel + 2)));
        int count = noFortune ? 1 : random.nextInt(fortuneLevel) + 2; // [2, F+1] is the same as [0, F) + 2

        //Cancel regular item drop and drop smelted version
        ItemStack item = new ItemStack(this.smeltingBlocks.get(block.getType()), count);
        block.getWorld().dropItemNaturally(block.getLocation(), item);
        return true;
    }

}
