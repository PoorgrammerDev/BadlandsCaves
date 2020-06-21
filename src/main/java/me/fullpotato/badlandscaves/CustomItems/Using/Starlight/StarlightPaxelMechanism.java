package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class StarlightPaxelMechanism extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final StarlightTools toolManager;

    public List<Material> pickaxeBlocks;
    public List<Material> axeBlocks;
    public List<Material> shovelBlocks;


    public StarlightPaxelMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.toolManager = new StarlightTools(plugin);
        pickaxeBlocks = Arrays.asList(Material.ACTIVATOR_RAIL, Material.ANDESITE, Material.ANDESITE_STAIRS, Material.ANDESITE_SLAB, Material.ANDESITE_STAIRS, Material.ANDESITE_WALL, Material.ANVIL, Material.BEACON, Material.BELL, Material.BLACK_CONCRETE, Material.BLACK_GLAZED_TERRACOTTA, Material.BLACK_SHULKER_BOX, Material.BLACK_TERRACOTTA, Material.BLAST_FURNACE, Material.BLUE_CONCRETE, Material.BLUE_GLAZED_TERRACOTTA, Material.BLUE_ICE, Material.BLUE_SHULKER_BOX, Material.BLUE_TERRACOTTA, Material.BONE_BLOCK, Material.BRAIN_CORAL_BLOCK, Material.BREWING_STAND, Material.BRICKS, Material.BRICK_SLAB, Material.BRICK_WALL, Material.BRICK_STAIRS, Material.BROWN_CONCRETE, Material.BROWN_GLAZED_TERRACOTTA, Material.BROWN_SHULKER_BOX, Material.BROWN_TERRACOTTA, Material.BUBBLE_CORAL_BLOCK, Material.CAULDRON, Material.CHIPPED_ANVIL, Material.CHISELED_QUARTZ_BLOCK, Material.CHISELED_RED_SANDSTONE, Material.CHISELED_SANDSTONE, Material.CHISELED_STONE_BRICKS, Material.COAL_BLOCK, Material.COAL_ORE, Material.COBBLESTONE, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_WALL, Material.CONDUIT, Material.CRACKED_STONE_BRICKS, Material.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE_SLAB, Material.CUT_SANDSTONE, Material.CUT_SANDSTONE_SLAB, Material.CYAN_CONCRETE, Material.CYAN_GLAZED_TERRACOTTA, Material.CYAN_SHULKER_BOX, Material.CYAN_TERRACOTTA, Material.DAMAGED_ANVIL, Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_SLAB, Material.DARK_PRISMARINE_STAIRS, Material.DEAD_BRAIN_CORAL_BLOCK, Material.DEAD_BUBBLE_CORAL_BLOCK, Material.DEAD_FIRE_CORAL_BLOCK, Material.DEAD_HORN_CORAL_BLOCK, Material.DEAD_TUBE_CORAL_BLOCK, Material.DETECTOR_RAIL, Material.DIAMOND_BLOCK, Material.DIAMOND_ORE, Material.DIORITE, Material.DIORITE_STAIRS, Material.DIORITE_WALL, Material.DISPENSER, Material.DROPPER, Material.EMERALD_BLOCK, Material.EMERALD_ORE, Material.ENCHANTING_TABLE, Material.ENDER_CHEST, Material.END_STONE, Material.END_STONE_BRICKS, Material.END_STONE_BRICK_SLAB, Material.END_STONE_BRICK_STAIRS, Material.END_STONE_BRICK_WALL, Material.FIRE_CORAL_BLOCK, Material.FURNACE, Material.GOLD_BLOCK, Material.GOLD_ORE, Material.GRANITE, Material.GRANITE_STAIRS, Material.GRANITE_WALL, Material.GRAY_CONCRETE, Material.GRAY_GLAZED_TERRACOTTA, Material.GRAY_SHULKER_BOX, Material.GRAY_TERRACOTTA, Material.GREEN_CONCRETE, Material.GREEN_GLAZED_TERRACOTTA, Material.GREEN_SHULKER_BOX, Material.GREEN_TERRACOTTA, Material.GRINDSTONE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.HOPPER, Material.HORN_CORAL_BLOCK, Material.ICE, Material.IRON_BARS, Material.IRON_BLOCK, Material.IRON_DOOR, Material.IRON_ORE, Material.IRON_TRAPDOOR, Material.LANTERN, Material.LAPIS_BLOCK, Material.LAPIS_ORE, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_SHULKER_BOX, Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.LIME_CONCRETE, Material.LIME_GLAZED_TERRACOTTA, Material.LIME_SHULKER_BOX, Material.LIME_TERRACOTTA, Material.MAGENTA_CONCRETE, Material.MAGENTA_GLAZED_TERRACOTTA, Material.MAGENTA_SHULKER_BOX, Material.MAGENTA_TERRACOTTA, Material.MAGMA_BLOCK, Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_WALL, Material.MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_WALL, Material.NETHERRACK, Material.NETHER_BRICKS, Material.NETHER_BRICK_FENCE, Material.NETHER_BRICK_SLAB, Material.NETHER_BRICK_STAIRS, Material.NETHER_BRICK_WALL, Material.NETHER_QUARTZ_ORE, Material.OBSERVER, Material.OBSIDIAN, Material.ORANGE_CONCRETE, Material.ORANGE_GLAZED_TERRACOTTA, Material.ORANGE_SHULKER_BOX, Material.ORANGE_TERRACOTTA, Material.PACKED_ICE, Material.PINK_CONCRETE, Material.PINK_GLAZED_TERRACOTTA, Material.PINK_SHULKER_BOX, Material.PINK_TERRACOTTA, Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB, Material.POLISHED_ANDESITE_STAIRS, Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_SLAB, Material.POLISHED_DIORITE_STAIRS, Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_SLAB, Material.POLISHED_GRANITE_STAIRS, Material.POWERED_RAIL, Material.PRISMARINE, Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_SLAB, Material.PRISMARINE_STAIRS, Material.PRISMARINE_WALL, Material.PURPLE_CONCRETE, Material.PURPLE_GLAZED_TERRACOTTA, Material.PURPLE_SHULKER_BOX, Material.PURPLE_TERRACOTTA, Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, Material.PURPUR_SLAB, Material.PURPUR_STAIRS, Material.QUARTZ_BLOCK, Material.QUARTZ_PILLAR, Material.QUARTZ_SLAB, Material.QUARTZ_STAIRS, Material.RAIL, Material.REDSTONE_BLOCK, Material.REDSTONE_ORE, Material.RED_CONCRETE, Material.RED_GLAZED_TERRACOTTA, Material.RED_NETHER_BRICKS, Material.RED_NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_WALL, Material.RED_SANDSTONE, Material.RED_SANDSTONE_SLAB, Material.RED_SANDSTONE_STAIRS, Material.RED_SANDSTONE_WALL, Material.RED_SHULKER_BOX, Material.RED_TERRACOTTA, Material.SANDSTONE, Material.SANDSTONE_SLAB, Material.SANDSTONE_STAIRS, Material.SANDSTONE_WALL, Material.SHULKER_BOX, Material.SMOKER, Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_SLAB, Material.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE_SLAB, Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE, Material.SMOOTH_SANDSTONE_SLAB, Material.SMOOTH_SANDSTONE_STAIRS, Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB, Material.SPAWNER, Material.STONE, Material.STONECUTTER, Material.STONE_BRICKS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_WALL, Material.STONE_BUTTON, Material.STONE_PRESSURE_PLATE, Material.STONE_SLAB, Material.STONE_STAIRS, Material.TERRACOTTA, Material.TUBE_CORAL_BLOCK, Material.WHITE_CONCRETE, Material.WHITE_GLAZED_TERRACOTTA, Material.WHITE_SHULKER_BOX, Material.WHITE_TERRACOTTA, Material.YELLOW_CONCRETE, Material.YELLOW_GLAZED_TERRACOTTA, Material.YELLOW_SHULKER_BOX, Material.YELLOW_TERRACOTTA);
        axeBlocks = Arrays.asList(Material.ACACIA_BUTTON, Material.ACACIA_DOOR, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE, Material.ACACIA_LOG, Material.ACACIA_PLANKS, Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_SIGN, Material.ACACIA_SLAB, Material.ACACIA_STAIRS, Material.ACACIA_TRAPDOOR, Material.ACACIA_WALL_SIGN, Material.ACACIA_WOOD, Material.BARREL, Material.BEEHIVE, Material.BEE_NEST, Material.BIRCH_BUTTON, Material.BIRCH_DOOR, Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE, Material.BIRCH_LOG, Material.BIRCH_PLANKS, Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_SIGN, Material.BIRCH_SLAB, Material.BIRCH_STAIRS, Material.BIRCH_TRAPDOOR, Material.BIRCH_WALL_SIGN, Material.BIRCH_WOOD, Material.BLACK_BANNER, Material.BLACK_WALL_BANNER, Material.BLUE_BANNER, Material.BLUE_WALL_BANNER, Material.BOOKSHELF, Material.BROWN_BANNER, Material.BROWN_MUSHROOM_BLOCK, Material.BROWN_WALL_BANNER, Material.CAMPFIRE, Material.CARTOGRAPHY_TABLE, Material.CARVED_PUMPKIN, Material.CHEST, Material.CHORUS_PLANT, Material.COCOA, Material.COMPOSTER, Material.CRAFTING_TABLE, Material.CYAN_BANNER, Material.CYAN_WALL_BANNER, Material.DARK_OAK_BUTTON, Material.DARK_OAK_DOOR, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_SIGN, Material.DARK_OAK_SLAB, Material.DARK_OAK_STAIRS, Material.DARK_OAK_TRAPDOOR, Material.DARK_OAK_WALL_SIGN, Material.DARK_OAK_WOOD, Material.DAYLIGHT_DETECTOR, Material.FLETCHING_TABLE, Material.GRAY_BANNER, Material.GRAY_WALL_BANNER, Material.GREEN_BANNER, Material.GREEN_WALL_BANNER, Material.JACK_O_LANTERN, Material.JUKEBOX, Material.JUNGLE_BUTTON, Material.JUNGLE_DOOR, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE, Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_SIGN, Material.JUNGLE_SLAB, Material.JUNGLE_STAIRS, Material.JUNGLE_TRAPDOOR, Material.JUNGLE_WALL_SIGN, Material.JUNGLE_WOOD, Material.LADDER, Material.LECTERN, Material.LOOM, Material.MAGENTA_BANNER, Material.MAGENTA_WALL_BANNER, Material.MELON, Material.NOTE_BLOCK, Material.OAK_BUTTON, Material.OAK_DOOR, Material.OAK_FENCE, Material.OAK_FENCE_GATE, Material.OAK_LOG, Material.OAK_PLANKS, Material.OAK_PRESSURE_PLATE, Material.OAK_SIGN, Material.OAK_SLAB, Material.OAK_STAIRS, Material.OAK_TRAPDOOR, Material.OAK_WALL_SIGN, Material.OAK_WOOD, Material.PINK_BANNER, Material.PINK_WALL_BANNER, Material.PUMPKIN, Material.PURPLE_BANNER, Material.PURPLE_WALL_BANNER, Material.RED_BANNER, Material.RED_MUSHROOM_BLOCK, Material.RED_WALL_BANNER, Material.SMITHING_TABLE, Material.SPRUCE_BUTTON, Material.SPRUCE_DOOR, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE, Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_SIGN, Material.SPRUCE_SLAB, Material.SPRUCE_STAIRS, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_WALL_SIGN, Material.SPRUCE_WOOD, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD, Material.TRAPPED_CHEST, Material.WHITE_BANNER, Material.WHITE_WALL_BANNER, Material.YELLOW_BANNER, Material.YELLOW_WALL_BANNER);
        shovelBlocks = Arrays.asList(Material.BLACK_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER, Material.CLAY, Material.COARSE_DIRT, Material.CYAN_CONCRETE_POWDER, Material.DIRT, Material.FARMLAND, Material.GRASS_BLOCK, Material.GRASS_PATH, Material.GRAVEL, Material.GRAY_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER, Material.MYCELIUM, Material.ORANGE_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER, Material.PODZOL, Material.PURPLE_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER, Material.RED_SAND, Material.SAND, Material.SNOW, Material.SNOW_BLOCK, Material.SOUL_SAND, Material.WHITE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER);
    }


    @Override
    public void run() {
        boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
        if (hardmode) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                EntityEquipment equipment = player.getEquipment();
                if (equipment != null) {
                    ItemStack paxel = equipment.getItemInMainHand();
                    if (paxel != null) {
                        if (toolManager.isStarlightPaxel(paxel)) {
                            Block block = player.getTargetBlockExact(5);
                            if (block != null) {
                                Material material = block.getType();

                                if (pickaxeBlocks.contains(material) && !paxel.getType().equals(Material.DIAMOND_PICKAXE)) {
                                    paxel.setType(Material.DIAMOND_PICKAXE);
                                }
                                else if (axeBlocks.contains(material) && !paxel.getType().equals(Material.DIAMOND_AXE)) {
                                    paxel.setType(Material.DIAMOND_AXE);
                                }
                                else if (shovelBlocks.contains(material) && !paxel.getType().equals(Material.DIAMOND_SHOVEL)) {
                                    paxel.setType(Material.DIAMOND_SHOVEL);
                                }
                                else return;

                                equipment.setItemInMainHand(paxel);
                            }
                        }
                    }
                }
            }
        }
    }

}