package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.MultiStructureLoader;
import me.fullpotato.badlandscaves.Util.StructureTrack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lectern;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class DimensionStructures {
    public enum Structure {
        MANABAR,
        LAB,
        LAB_ABANDONED,
        LAB_DESTROYED,
        JAIL,
        JAIL_ABANDONED,
        SHRINE,
        SHRINE_DESTROYED,
        CURSED_HOUSE,
        TENT,
        HOUSE,
        HOUSE_ABANDONED,
        HOUSE_DESTROYED,
        BUNKER,
        BUNKER2,
        BUNKER_AB,
        CASTLE,
    }
    
    private final BadlandsCaves plugin;
    private final Random random;
    private final List<Material> blacklistedMats = Arrays.asList(Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG, Material.OAK_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.SPRUCE_LEAVES, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK);

    public DimensionStructures(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    public void generateStructures (World world, @Nullable Location origin, int radius, int count) {
        if (origin == null) origin = new Location(world, 0, 256, 0);

        final int x = origin.getBlockX();
        final int z = origin.getBlockZ();
        final int[] ticker = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticker[0] > count) {
                    this.cancel();
                    return;
                }

                //Find randomized location and then find the surface
                final Location location = new Location(world, x + (random.nextInt(radius * 2) - radius), world.getMaxHeight(), z + (random.nextInt(radius * 2) - radius));
                Material type = null;
                do {
                    location.setY(location.getY() - 1);
                    type = location.getBlock().getType();
                } while (!type.isSolid() || blacklistedMats.contains(type));

                Bukkit.broadcastMessage("structure spawning at " + location.toString());

                generateStructure(location, null, false, false);
                ticker[0]++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void generateStructure (Location origin, @Nullable Structure structure, boolean leaveStructureBlocks, boolean force) {
        final World world = origin.getWorld();
        if (!force && !world.getName().startsWith(plugin.getDimensionPrefixName())) return;

        if (structure == null) {
            structure = Structure.values()[random.nextInt(Structure.values().length)];
        }

        world.loadChunk(origin.getChunk());
        final Structure finalStructure = structure;
        new BukkitRunnable() {
            @Override
            public void run() {
                loadStructure(finalStructure, origin, leaveStructureBlocks);
            }
        }.runTaskLater(plugin, 20);
    }

    public void loadStructure(Structure queried, Location origin, boolean leaveStructureBlocks) {
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

            case CURSED_HOUSE:
                structure = new StructureTrack(plugin, 10, -1, -11, -19, 0, 1, "badlandscaves:" + Structure.CURSED_HOUSE.name().toLowerCase(), BlockFace.UP);
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

            case JAIL:
                structure = new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL.name().toLowerCase(), BlockFace.UP);
                break;

            case JAIL_ABANDONED:
                structure = new StructureTrack(plugin, -8, -6, 6, 1, 0, -29, "badlandscaves:" + Structure.JAIL_ABANDONED.name().toLowerCase(), BlockFace.UP);
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
                        guard.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_AXE));
                    }

                    guard.getPersistentDataContainer().set(new NamespacedKey(plugin, "castle_guard"), PersistentDataType.BYTE, (byte) 1);
                    guard.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20.0f);
                    guard.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(8.0f);
                    guard.setPersistent(true);
                    guard.setRemoveWhenFarAway(false);
                    guard.setCustomName("Castle Guard");
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
                        if (meta != null) {
                            meta.addEnchant(Enchantment.QUICK_CHARGE, 3, false);
                            meta.addEnchant(Enchantment.MULTISHOT, 1, false);
                        }

                        guard.getEquipment().setItemInMainHand(crossbow);

                        final ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET, 64);
                        final FireworkMeta rocketMeta = (FireworkMeta) rocket.getItemMeta();

                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.YELLOW).build());
                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.ORANGE).build());
                        rocketMeta.addEffect(FireworkEffect.builder().with(Type.STAR).withColor(Color.RED).build());
                        rocket.setItemMeta(rocketMeta);

                        guard.getEquipment().setItemInOffHand(rocket);
                        guard.getEquipment().setItemInOffHandDropChance(-999);
                    }

                    guard.getPersistentDataContainer().set(new NamespacedKey(plugin, "castle_guard"), PersistentDataType.BYTE, (byte) 1);
                    guard.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20.0f);
                    guard.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(8.0f);
                    guard.setPersistent(true);
                    guard.setRemoveWhenFarAway(false);
                    guard.setCustomName("Castle Guard");
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
                Bukkit.broadcastMessage(lecternLoc.toString());
                if (lecternLoc.getBlock().getType() == Material.LECTERN) {
                    final Lectern lectern = (Lectern) lecternLoc.getBlock().getState(); 
                    lectern.getPersistentDataContainer().set(new NamespacedKey(plugin, "location_uuid"), PersistentDataType.STRING, uuid.toString());
                    lectern.update();
                }

                break;
            }

            default:
                return;
        }
    }
}
