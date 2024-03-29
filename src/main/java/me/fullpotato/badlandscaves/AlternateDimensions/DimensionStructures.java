package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lectern;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class DimensionStructures {
    private final BadlandsCaves plugin;
    private final Random random;
    private final List<Material> blacklistedMats = Arrays.asList(Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG, Material.OAK_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.SPRUCE_LEAVES, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);
    private final int MINIMUM_HEIGHT = 5;
    
    public DimensionStructures(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    /**
     * GenerateStructures
     * @param world World to spawn structures in
     * @param radius Radius around the world's origin (0, 256, 0) to generate structures
     * @param count Count of structures spawned per layer. A count of 1 will spawn 1 structure in every layer.
     */
    public void generateStructures (World world, int radius, int count) {
        final Location origin = new Location(world, 0, 256, 0);
        final int x = origin.getBlockX();
        final int z = origin.getBlockZ();
        final int[] ticker = {0};

        final List<Structure> surfaceStructures = Structure.GetStructuresByType(StructureType.SURFACE_STRUCTURE);
        final List<Structure> voidStructures = Structure.GetStructuresByType(StructureType.VOID_STRUCTURE);
        final List<Structure> voidDecorations = Structure.GetStructuresByType(StructureType.VOID_DECORATION);
       
        final int layerMultiplier = 10;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticker[0] >= (count * layerMultiplier)) {
                    this.cancel();
                    return;
                }

                //Find randomized (X,Z) location
                Location location = new Location(world, x + (random.nextInt(radius * 2) - radius), world.getMaxHeight(), z + (random.nextInt(radius * 2) - radius));
                Structure structure = null;

                world.loadChunk(location.getChunk());

                //Surface structure ----------
                if (ticker[0] % layerMultiplier == 0) {
                    //select a random structure
                    structure = surfaceStructures.get(random.nextInt(surfaceStructures.size()));

                    //find surface height
                    Material type = null;
                    do {
                        location.setY(location.getY() - 1);
                        type = location.getBlock().getType();
                    } while (!type.isSolid() || blacklistedMats.contains(type));
                }
                //Void structure ------------
                else if (ticker[0] % layerMultiplier == 1) {
                    //select a random structure
                    structure = voidStructures.get(random.nextInt(voidStructures.size()));

                    //Calculate an appropriate height in the void layer from this location
                    location = TryCalcVoidHeight(location, 1);
                    
                    //Do not force place -- if the height is less than minimum then ignore
                    if (location.getY() < MINIMUM_HEIGHT) structure = null;
                }
                else {
                    //Calculate an appropriate height in the void layer from this location
                    location = TryCalcVoidHeight(location, 1);

                    //Do not force place -- if the height is less than minimum then ignore
                    if (location.getY() < MINIMUM_HEIGHT) structure = null;

                    //otherwise select random structure
                    else structure = voidDecorations.get(random.nextInt(voidDecorations.size()));
                }

                if (structure != null) {
                    generateStructure(location, structure, false, false);
                    ticker[0]++;
                }
            }
        }.runTaskTimer(plugin, 100, 40);
    }

    public void generateStructure (Location origin, Structure structure, boolean leaveStructureBlocks, boolean force) {
        final World world = origin.getWorld();
        if (!force && !world.getName().startsWith(plugin.getDimensionPrefixName())) return;

        world.loadChunk(origin.getChunk());
        new BukkitRunnable() {
            @Override
            public void run() {
                loadStructure(structure, origin, leaveStructureBlocks);
            }
        }.runTaskLater(plugin, 20);
    }

    private void loadStructure(Structure queried, Location origin, boolean leaveStructureBlocks) {
        //center ground level world origin ~(0, 60, 0)
        StructureTrack[] multiStructure = null;
        StructureTrack structure = null;

        switch (queried) {
            case BUNKER:
                multiStructure = new StructureTrack[]{
                        new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse", BlockFace.DOWN),
                        new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube", BlockFace.DOWN),
                        new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer", BlockFace.DOWN),
                        new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom", BlockFace.DOWN),
                        new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine", BlockFace.DOWN),
                        new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm", BlockFace.UP),
                };
                break;

            case BUNKER2:
                multiStructure = new StructureTrack[]{
                        new StructureTrack(plugin, -6, -9, -14, -1, 1, 0, "badlandscaves:bunker2_tophouse", BlockFace.DOWN),
                        new StructureTrack(plugin, -9, -33, -10, -2, 1, -6, "badlandscaves:bunker2_intertube", BlockFace.DOWN),
                        new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker2_foyer", BlockFace.DOWN),
                        new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker2_bedroom", BlockFace.DOWN),
                        new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker2_dine", BlockFace.DOWN),
                        new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker2_farm", BlockFace.UP),
                };
                break;

            case BUNKER_AB:
                multiStructure = new StructureTrack[]{
                        new StructureTrack(plugin, -6, -9, -14, 0, 1, 0, "badlandscaves:bunker_tophouse_ab", BlockFace.DOWN),
                        new StructureTrack(plugin, -9, -33, -10, 0, 1, 0, "badlandscaves:bunker_intertube_ab", BlockFace.DOWN),
                        new StructureTrack(plugin, -13, -33, 5, 0, 1, 0, "badlandscaves:bunker_foyer_ab", BlockFace.DOWN),
                        new StructureTrack(plugin, -42, -30, 9, 0, 1, 0, "badlandscaves:bunker_bedroom_ab", BlockFace.DOWN),
                        new StructureTrack(plugin, 8, -29, 10, 0, 1, 0, "badlandscaves:bunker_dine_ab", BlockFace.DOWN),
                        new StructureTrack(plugin, 5, -28, 27, -15, 1, 0, "badlandscaves:bunker_farm_ab", BlockFace.UP),
                };
                break;

            case CASTLE:
                multiStructure = new StructureTrack[]{
                    new StructureTrack(plugin, -39, -3, -24, 0, 1, 0, "badlandscaves:castle_nw_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, -24, 0, 1, 0, "badlandscaves:castle_ne_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, -39, -3, 24, 0, 1, 0, "badlandscaves:castle_w_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, 24, 0, 1, 0, "badlandscaves:castle_e_bottom", BlockFace.DOWN),

                    new StructureTrack(plugin, -39, -3, 72, 0, 1, 0, "badlandscaves:castle_sw", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, 72, 0, 1, 0, "badlandscaves:castle_se", BlockFace.DOWN),

                    new StructureTrack(plugin, -39, 56, -24, 0, -10, 0, "badlandscaves:castle_nw_top", BlockFace.UP),
                    new StructureTrack(plugin, 9, 56, -24, 0, -10, 0, "badlandscaves:castle_ne_top", BlockFace.UP),
                    new StructureTrack(plugin, -39, 56, 24, 0, -10, 0, "badlandscaves:castle_w_top", BlockFace.UP),
                    new StructureTrack(plugin, 9, 56, 24, 0, -10, 0, "badlandscaves:castle_e_top", BlockFace.UP),
                };
                break;

            case CASTLE_VOID:
                multiStructure = new StructureTrack[]{
                    new StructureTrack(plugin, -39, -3, -24, 0, 1, 0, "badlandscaves:void_castle_nw_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, -24, 0, 1, 0, "badlandscaves:void_castle_ne_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, -39, -3, 24, 0, 1, 0, "badlandscaves:void_castle_w_bottom", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, 24, 0, 1, 0, "badlandscaves:void_castle_e_bottom", BlockFace.DOWN),

                    new StructureTrack(plugin, -39, -3, 72, 0, 1, 0, "badlandscaves:void_castle_sw", BlockFace.DOWN),
                    new StructureTrack(plugin, 9, -3, 72, 0, 1, 0, "badlandscaves:void_castle_se", BlockFace.DOWN),

                    new StructureTrack(plugin, -39, 56, -24, 0, -10, 0, "badlandscaves:void_castle_nw_top", BlockFace.UP),
                    new StructureTrack(plugin, 9, 56, -24, 0, -10, 0, "badlandscaves:void_castle_ne_top", BlockFace.UP),
                    new StructureTrack(plugin, -39, 56, 24, 0, -10, 0, "badlandscaves:void_castle_w_top", BlockFace.UP),
                    new StructureTrack(plugin, 9, 56, 24, 0, -10, 0, "badlandscaves:void_castle_e_top", BlockFace.UP),
                };
                break;

            case CURSED_HOUSE:
                structure = new StructureTrack(plugin, 10, -1, -11, -19, 0, 1, "badlandscaves:" + Structure.CURSED_HOUSE.name().toLowerCase(), BlockFace.UP);
                break;

            case HOUSE_VOID:
                structure = new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:void_house", BlockFace.UP);
                break;

            case HOUSE:
                structure = new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE.name().toLowerCase(), BlockFace.UP);
                break;

            case HOUSE_ABANDONED:
                structure = new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE_ABANDONED.name().toLowerCase(), BlockFace.UP);
                break;

            case HOUSE_DESTROYED:
                structure = new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:" + Structure.HOUSE_DESTROYED.name().toLowerCase(), BlockFace.UP);
                break;

            case HOUSE_DESTROYED_VOID:
                structure = new StructureTrack(plugin, -9, 0, -8, 1, 0, 1, "badlandscaves:void_house_destroyed", BlockFace.UP);
                break;

            case JAIL:
                structure = new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL.name().toLowerCase(), BlockFace.UP);
                break;

            case JAIL_ABANDONED:
                structure = new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL_ABANDONED.name().toLowerCase(), BlockFace.UP);
                break;

            case LAB_VOID:
                structure = new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:void_lab", BlockFace.UP);
                break;

            case LAB:
                structure = new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB.name().toLowerCase(), BlockFace.UP);
                break;

            case LAB_ABANDONED:
                structure = new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB_ABANDONED.name().toLowerCase(), BlockFace.UP);
                break;

            case LAB_DESTROYED:
                structure = new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:" + Structure.LAB_DESTROYED.name().toLowerCase(), BlockFace.UP);
                break;

            case LAB_DESTROYED_VOID:
                structure = new StructureTrack(plugin, 11, 0, -15, -22, 0, 1, "badlandscaves:void_lab_destroyed", BlockFace.UP);
                break;

            case MANABAR:
                structure = new StructureTrack(plugin, 11, 0, 10, -21, 0, -19, "badlandscaves:" + Structure.MANABAR.name().toLowerCase(), BlockFace.UP);
                break;

            case SHRINE:
                structure = new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.SHRINE.name().toLowerCase(), BlockFace.UP);
                break;

            case SHRINE_DESTROYED:
                structure = new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.SHRINE_DESTROYED.name().toLowerCase(), BlockFace.UP);
                break;

            case TENT:
                structure = new StructureTrack(plugin, 6, 0, -6, -11, 0, 1, "badlandscaves:" + Structure.TENT.name().toLowerCase(), BlockFace.UP);
                break;

            case SHRINE_VOID:
                structure = new StructureTrack(plugin, -16, 0, -16, 1, 0, 1, "badlandscaves:void_shrine", BlockFace.UP);
                break;

            //Void layer decorations
            case LANTERN_STATUE_GHOUL_1:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_ghoul_1", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;
            case LANTERN_STATUE_GHOUL_2:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_ghoul_2", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;
            case LANTERN_STATUE_GHOUL_3:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_ghoul_3", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;
            case LANTERN_STATUE_GHOUL_4:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_ghoul_4", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;

            case LANTERN_STATUE_LARGE_1:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_statue_large_1", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;
            case LANTERN_STATUE_LARGE_2:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_lantern_statue_large_2", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;

            case STATUE_HOUND_1:
                structure = new StructureTrack(plugin, null, 0, -1, 0, 0, 1, 0, "badlandscaves:void_hound_statue_1", BlockFace.WEST, StructureRotation.values()[random.nextInt(StructureRotation.values().length)]);
                break;

            default:
                return;
        }

        if (multiStructure != null) {
            MultiStructureLoader loader = new MultiStructureLoader(multiStructure);
            loader.loadAll(origin, leaveStructureBlocks);
        }
        else if (structure != null) {
            structure.load(origin, leaveStructureBlocks);
        }         

        //Special extra instructions
        ExtraInstructions(queried, origin);
    }

    private void ExtraInstructions(Structure structure, Location origin) {
        switch (structure) {
            case CASTLE_VOID:
            case CASTLE: {
                //Enemy spawn offsets
                final Vector[] meleeEnemies = {
                    // Southeast tent
                    new Vector(8, 0, 8),
                    new Vector(13, 0, 10),
                    new Vector(8, 0, 12),
                    
                    // East tent
                    new Vector(8, 0, 2),
                    new Vector(13, 0, 0),
                    new Vector(8, 0, -2),

                    // Northeast tent
                    new Vector(8, 0, -8),
                    new Vector(13, 0, -10),
                    new Vector(8, 0, -12),

                    // Southwest tower
                    new Vector(28, 0, -19),
                    new Vector(27, 5, -19),
                    new Vector(27, 9, -19),

                    //East tunnel
                    new Vector(28, 0, 0),
                    new Vector(28, 0, 5),
                    new Vector(28, 0, -5),
                    new Vector(28, 0, -10),

                    //East tower
                    new Vector(28, 7, 19),
                    new Vector(28, 0, 18),

                    // Southwest tent
                    new Vector(-8, 0, 8),
                    new Vector(-13, 0, 10),
                    new Vector(-8, 0, 12),
                    
                    // West tent
                    new Vector(-8, 0, 2),
                    new Vector(-13, 0, 0),
                    new Vector(-8, 0, -2),

                    // Northwest tent
                    new Vector(-8, 0, -8),
                    new Vector(-13, 0, -10),
                    new Vector(-8, 0, -12),

                    // Northwest tower
                    new Vector(-28, 0, -19),
                    new Vector(-27, 5, -19),
                    new Vector(-27, 9, -19),

                    //West tunnel
                    new Vector(-28, 0, 0),
                    new Vector(-28, 0, 5),
                    new Vector(-28, 0, -5),
                    new Vector(-28, 0, -10),

                    //West tower
                    new Vector(-28, 7, 19),
                    new Vector(-28, 0, 18),
                };
                
                final Vector[] rangedEnemies = {
                    //East walkway
                    new Vector(28, 7, 0),
                    new Vector(28, 7, 5),
                    new Vector(28, 7, -5),
                    new Vector(28, 7, -10),

                    //West walkway
                    new Vector(-28, 7, 0),
                    new Vector(-28, 7, 5),
                    new Vector(-28, 7, -5),
                    new Vector(-28, 7, -10),

                    //North walkway
                    new Vector(-20, 7, -18),
                    new Vector(-15, 7, -18),
                    new Vector(-10, 7, -18),
                    new Vector(-5, 7, -18),
                    new Vector(0, 7, -18),
                    new Vector(5, 7, -18),
                    new Vector(10, 7, -18),
                    new Vector(15, 7, -18),
                    new Vector(20, 7, -18),
                };

                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                final ItemStack netheriteAxe = new ItemStack(Material.NETHERITE_AXE);
                final ItemStack diamondAxe = new ItemStack(Material.DIAMOND_AXE);

                //Spawn melee enemies ==============
                final World world = origin.getWorld();
                for (Vector meleeOffset : meleeEnemies) {
                    final Location spawn = origin.clone().add(meleeOffset);

                    LivingEntity guard;
                    if (random.nextInt(100) < 25) {
                        guard = (Evoker) world.spawnEntity(spawn, EntityType.EVOKER);
                    }
                    else {
                        guard = (Vindicator) world.spawnEntity(spawn, EntityType.VINDICATOR);
                        guard.getEquipment().setItemInMainHand((random.nextInt(100) < (0.25 * chaos) + 50) ? netheriteAxe : diamondAxe);
                    }

                    guard.getPersistentDataContainer().set(new NamespacedKey(plugin, "castle_guard"), PersistentDataType.BYTE, (byte) 1);
                    guard.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(10.0f + (chaos / 20.0f));
                    guard.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(4.0f + (chaos / 45.0f));
                    guard.setPersistent(true);
                    guard.setRemoveWhenFarAway(false);
                    guard.setCustomName(structure == Structure.CASTLE ? "Castle Guard" : ChatColor.MAGIC + "Void Castle Guard");
                    guard.setSilent(structure == Structure.CASTLE_VOID);
                    guard.setCustomNameVisible(false);
                }

                //Spawn ranged enemies =================
                for (Vector rangedOffset : rangedEnemies) {
                    final Location spawn = origin.clone().add(rangedOffset);

                    LivingEntity guard;
                    if (random.nextInt(100) < 25) {
                        guard = (Witch) world.spawnEntity(spawn, EntityType.WITCH);
                    }
                    else {
                        guard = (Pillager) world.spawnEntity(spawn, EntityType.PILLAGER);

                        final ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                        final ItemMeta meta = crossbow.getItemMeta();
                        if ((random.nextInt(100) < (0.25 * chaos) + 50) && meta != null) {
                            meta.addEnchant(Enchantment.QUICK_CHARGE, 3, false);
                            if (random.nextInt(100) < (0.25 * chaos) + 50) meta.addEnchant(Enchantment.MULTISHOT, 1, false);
                        }

                        guard.getEquipment().setItemInMainHand(crossbow);

                        final ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET, (int) ((0.48 * chaos) + 16));
                        final FireworkMeta rocketMeta = (FireworkMeta) rocket.getItemMeta();

                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.YELLOW).build());
                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.ORANGE).build());
                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.STAR).withColor(Color.RED).build());
                        rocket.setItemMeta(rocketMeta);

                        guard.getEquipment().setItemInOffHand(rocket);
                        guard.getEquipment().setItemInOffHandDropChance(-999);
                    }

                    guard.getPersistentDataContainer().set(new NamespacedKey(plugin, "castle_guard"), PersistentDataType.BYTE, (byte) 1);
                    guard.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(10.0f + (chaos / 20.0f));
                    guard.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(4.0f + (chaos / 45.0f));
                    guard.setPersistent(true);
                    guard.setRemoveWhenFarAway(false);
                    guard.setCustomName(structure == Structure.CASTLE ? "Castle Guard" : ChatColor.MAGIC + "Void Castle Guard");
                    guard.setSilent(structure == Structure.CASTLE_VOID);
                    guard.setCustomNameVisible(false);
                }

                //Log location of lectern
                ConfigurationSection section = plugin.getSystemConfig().getConfigurationSection("castle_lectern_locations");
                if (section == null) {
                    plugin.getSystemConfig().createSection("castle_lectern_locations");
                    plugin.saveSystemConfig();

                    section = plugin.getSystemConfig().getConfigurationSection("castle_lectern_locations");
                }

                final UUID uuid = UUID.randomUUID();
                final Location lecternLoc = origin.clone().add(0, 0, 17);
                section.set(uuid.toString(), lecternLoc);
                plugin.getSystemConfig().set("castle_lectern_locations", section);
                plugin.saveSystemConfig();

                //Update lectern with UUID
                if (lecternLoc.getBlock().getType() == Material.LECTERN) {
                    final Lectern lectern = (Lectern) lecternLoc.getBlock().getState(); 
                    lectern.getPersistentDataContainer().set(new NamespacedKey(plugin, "location_uuid"), PersistentDataType.STRING, uuid.toString());
                    if (structure == Structure.CASTLE_VOID) lectern.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_void"), PersistentDataType.BYTE, (byte) 1);
                    lectern.update();
                }

                break;
            }

            default:
                return;
        }
    }

    private Location TryCalcVoidHeight(Location location, int tries) {
        //first, get the height of the void layer by searching from the bottom of the world
        location.setY(MINIMUM_HEIGHT);
        Biome biome = null;
        Material type = null;
        do {
            location.setY(location.getY() + 1); //iterate upwards
            biome = location.getBlock().getBiome();
        } while (biome == Biome.GRAVELLY_MOUNTAINS && location.getY() < 128); //reserved biome value for VOID

        int maxHeight = location.getBlockY();

        for (int i = 0; i < tries; i++) {
            //get a random height in [minHeight, maxHeight] to place the structure
            //with maxHeight being the value we just calculated via searching
            location.setY(random.nextInt(maxHeight - MINIMUM_HEIGHT) + MINIMUM_HEIGHT);

            //then search downwards for a pocket of air
            int savedY = location.getBlockY();
            for (int j = 0; j < savedY - MINIMUM_HEIGHT; j++) {
                type = location.getBlock().getType();
                if (type.isAir() || location.getY() < MINIMUM_HEIGHT) break; //if hit air or reach min height then break

                location.setY(location.getY() - 1); //iterate downwards
            }

            //then search downwards for ground
            savedY = location.getBlockY();
            for (int j = 0; j < savedY - MINIMUM_HEIGHT; j++) {
                type = location.getBlock().getType();
                if (type.isSolid() || location.getY() < MINIMUM_HEIGHT) break; //if hit ground or reach min height then break

                location.setY(location.getY() - 1); //iterate downwards
            }

            //logic to check if desired Y has been reached
            if (location.getY() >= MINIMUM_HEIGHT) break;

        }
                    
        //NOTE: is this necessary? or is it already returned via reference?
        return location;
    }


}
