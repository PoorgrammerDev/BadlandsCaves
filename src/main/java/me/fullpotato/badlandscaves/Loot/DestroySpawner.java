package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class DestroySpawner implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    private final Map<Material, Material> mossMap = new HashMap<>();
    private static Location newLoc;
    private final ArrayList<Material> pickaxes = new ArrayList<>();
    private final Map<Integer[], BlockFace> chestInfo = new HashMap<>();
    private final String chestName = ChatColor.RESET.toString() + ChatColor.of("#8f8f8f") + "Dungeon Chest";
    private final NamespacedKey key;
    private ParticleShapes particleShapes;
    private final EntityType[] mobTypes = {
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.WITCH,
            EntityType.CREEPER,
    };


    public DestroySpawner(BadlandsCaves plugin, Random random, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "spawner_id");
        this.random = random;
        this.particleShapes = particleShapes;
        mossMap.put(Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS);
        mossMap.put(Material.STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_SLAB);
        mossMap.put(Material.STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_STAIRS);

        pickaxes.add(Material.WOODEN_PICKAXE);
        pickaxes.add(Material.STONE_PICKAXE);
        pickaxes.add(Material.IRON_PICKAXE);
        pickaxes.add(Material.GOLDEN_PICKAXE);
        pickaxes.add(Material.DIAMOND_PICKAXE);
        pickaxes.add(Material.NETHERITE_PICKAXE);

        chestInfo.put(new Integer[]{5, -9, 4}, BlockFace.NORTH);
        chestInfo.put(new Integer[]{4, -9, 5}, BlockFace.WEST);
        chestInfo.put(new Integer[]{-5, -9, 4}, BlockFace.NORTH);
        chestInfo.put(new Integer[]{-4, -9, 5}, BlockFace.EAST);
        chestInfo.put(new Integer[]{-5, -9, -4}, BlockFace.SOUTH);
        chestInfo.put(new Integer[]{-4, -9, -5}, BlockFace.EAST);
        chestInfo.put(new Integer[]{5, -9, -4}, BlockFace.SOUTH);
        chestInfo.put(new Integer[]{4, -9, -5}, BlockFace.WEST);
    }

    @EventHandler
    public void breakSpawner (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            final Player player = event.getPlayer();
            if (pickaxes.contains(player.getInventory().getItemInMainHand().getType())) {
                incrementChaos(false, 1);
                final CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
                final Location location = block.getLocation().add(0.5, 0.5, 0.5);
                final World world = location.getWorld();
                world.spawnParticle(Particle.FLAME, location, 50, 0.1, 0.1, 0.1, 1);
                world.playSound(location, Sound.ENTITY_BLAZE_HURT, SoundCategory.BLOCKS, 0.5F, 0.4F);
                world.playSound(location, Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1, 1);
                world.playSound(location, Sound.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.1F, 0.5F);


                if (!spawner.getSpawnedType().equals(EntityType.BLAZE)) {
                    if (particleShapes == null) particleShapes = new ParticleShapes(plugin);
                    particleShapes.line(null, Particle.FLASH, location.clone().add(0, 1, 0), location.clone().add(0, 255, 0), 0, null, 1);
                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                        online.playSound(online.getLocation(), "custom.darkrooms_whispers", SoundCategory.BLOCKS, 0.4F, 0.7F);
                        online.playSound(online.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.6F, 0.5F);
                    }

                    getNewLocation(block.getLocation(), random, 500);

                    //Possiblity for spawner to change mobs / mutate on break if the setting in config is enabled.
                    //Passing in a null EntityType to this function will make it roll for a random mob.
                    EntityType type = spawner.getSpawnedType();
                    if (plugin.getOptionsConfig().getBoolean("spawner_reroll") && random.nextBoolean()) {
                        type = null;
                    }

                    makeDungeon(type, random, false, false);
                }

                loot(player, block.getLocation(), random, spawner.getSpawnedType());
            }
            else if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.sendMessage(ChatColor.RED + "You need a stronger tool.");
                event.setCancelled(true);
                return;
            }

            if (!event.isCancelled() && block.getState() instanceof CreatureSpawner) {
                final CreatureSpawner state = (CreatureSpawner) block.getState();
                final PersistentDataContainer container = state.getPersistentDataContainer();
                if (container.has(key, PersistentDataType.STRING)) {
                    final String uuid = container.get(key, PersistentDataType.STRING);
                    if (uuid != null && !uuid.isEmpty()) {
                        plugin.getSystemConfig().set("dungeons." + uuid, null);
                        plugin.saveSystemConfig();
                    }
                }
            }

        }
    }

    public void incrementChaos(boolean silent, int amount) {
        int current_chaos = plugin.getSystemConfig().getInt("chaos_level");
        if (current_chaos < 100) {
            plugin.getSystemConfig().set("chaos_level", current_chaos + amount);
            plugin.saveSystemConfig();
            if (!silent) plugin.getServer().broadcastMessage("§cChaos seeps into this realm...");
        }
        else if (!silent) {
            plugin.getServer().broadcastMessage("§cChaos has consumed the world...");
        }
    }

    public void getNewLocation(Location oldSpawnerLocation, Random random, int range) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //calculating world border
                WorldBorder border = oldSpawnerLocation.getWorld().getWorldBorder();
                int[] x_bounds = {
                        Math.max(oldSpawnerLocation.getBlockX() - range, (int) (border.getCenter().getBlockX() - (border.getSize() / 2))),
                        Math.min(oldSpawnerLocation.getBlockX() + range, (int) (border.getCenter().getBlockX() + (border.getSize() / 2))),
                };
                int[] z_bounds = {
                        Math.max(oldSpawnerLocation.getBlockZ() - range, (int) (border.getCenter().getBlockZ() - border.getSize() / 2)),
                        Math.min(oldSpawnerLocation.getBlockZ() + range, (int) (border.getCenter().getBlockZ() + border.getSize() / 2)),
                };

                //getting location
                final int[] spread = new int[2];
                while (Math.abs(spread[0]) < 50 || Math.abs(spread[1]) < 50) {
                    spread[0] = random.ints(1, x_bounds[0], x_bounds[1]).toArray()[0];
                    spread[1] = random.ints(1, z_bounds[0], z_bounds[1]).toArray()[0];
                }
                final int y = random.nextInt(120) + 7;
                Location output = new Location(oldSpawnerLocation.getWorld(), spread[0], y, spread[1]);

                //test if viable
                if (isViable(output)) {
                    do {
                        output.subtract(0, 1, 0);
                    } while (isViable(output));

                    newLoc = output.add(0, 1, 0);
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 30);
    }

    public boolean isViable (Location location) {
        if (location.getWorld() != null && location.getWorld().getWorldBorder().isInside(location)) {
            Location[] doors = {
                    location.clone().add(8, -9, 0),
                    location.clone().add(-8, -9, 0),
                    location.clone().add(0, -9, 8),
                    location.clone().add(0, -9, -8),
            };
            for (Location door : doors) {
                if (door.getBlock().isPassable() && !door.getBlock().isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void makeDungeon(@Nullable EntityType type, Random random, boolean silent, boolean starting) {
        if (type == null) type = mobTypes[random.nextInt(mobTypes.length)];
        final EntityType finalType = type;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (newLoc == null) return;
                final int chaos = plugin.getSystemConfig().getInt("chaos_level");

                //spawn in structure
                final StructureTrack track = new StructureTrack(plugin, newLoc.clone(), -7, -10, -7, 0, 0, 0, "badlandscaves:dungeon", BlockFace.DOWN);
                track.load();

                //replace with mossy and add holes based on chaos
                for (int x = -7; x <= 7; x++) {
                    for (int z = -7; z <= 7; z++) {
                        for (int y = -11; y <= 8; y++) {
                            if (random.nextInt(100) < ((0.65 * chaos) + 10)) {
                                Location parse = newLoc.clone().add(x, y, z);
                                if (mossMap.containsKey(parse.getBlock().getType())) {
                                    if (random.nextInt(100) < 75) {
                                        final Block block = parse.getBlock();
                                        if (block.getBlockData() instanceof Slab) {
                                            Slab slab = (Slab) block.getBlockData();
                                            block.setType(mossMap.get(block.getType()));

                                            if (block.getBlockData() instanceof Slab) {
                                                Slab newSlab = (Slab) block.getBlockData();
                                                newSlab.setType(slab.getType());
                                                block.setBlockData(newSlab);
                                            }
                                        }
                                        else if (block.getBlockData() instanceof Stairs) {
                                            Stairs stairs = (Stairs) block.getBlockData();
                                            block.setType(mossMap.get(block.getType()));
                                            block.setBlockData(stairs);

                                            if (block.getBlockData() instanceof Stairs) {
                                                Stairs newStairs = (Stairs) block.getBlockData();
                                                newStairs.setShape(stairs.getShape());
                                                block.setBlockData(newStairs);
                                            }
                                        }
                                        else {
                                            block.setType(mossMap.get(block.getType()));
                                        }
                                    }
                                    else if (chaos > 0) {
                                        parse.getBlock().setType(Material.CAVE_AIR);
                                    }
                                }
                            }
                        }
                    }
                }

                //put in spawner
                newLoc.getBlock().setType(Material.SPAWNER);
                CreatureSpawner spawner = (CreatureSpawner) newLoc.getBlock().getState();
                spawner.setSpawnedType(finalType);
                spawner.setMinSpawnDelay(200 - chaos);
                spawner.setMaxSpawnDelay(800 - (5 * chaos));
                spawner.setMaxNearbyEntities((int) (6 + (0.1 * chaos)));

                final UUID uuid = UUID.randomUUID();
                spawner.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());

                plugin.getSystemConfig().set("dungeons." + uuid + ".location", newLoc);
                plugin.getSystemConfig().set("dungeons." + uuid + ".type", finalType.name());
                plugin.getSystemConfig().set("dungeons." + uuid + ".chaos_spawned", chaos);
                plugin.saveSystemConfig();

                spawner.update();

                //put in chests
                for (Integer[] offset : chestInfo.keySet()) {
                    if (random.nextInt(100) < (chaos / 1.5)) {
                        final Location location = newLoc.clone().add(offset[0], offset[1], offset[2]);
                        location.getBlock().setType(Material.CHEST);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                final Block block = location.getBlock();
                                //set facing
                                if (block.getBlockData() instanceof Chest) {
                                    Chest chestData = (Chest) block.getBlockData();
                                    chestData.setFacing(chestInfo.get(offset));
                                    block.setBlockData(chestData);
                                }

                                if (block.getState() instanceof org.bukkit.block.Chest) {
                                    org.bukkit.block.Chest chestBlock = (org.bukkit.block.Chest) block.getState();
                                    chestBlock.setCustomName(chestName);
                                    chestBlock.setLootTable(LootTables.SIMPLE_DUNGEON.getLootTable());
                                    chestBlock.update(true);
                                }
                            }
                        }.runTaskLater(plugin, 1);
                    }
                }

                if (!silent) plugin.getServer().broadcastMessage("§3A new Dungeon has appeared!");
                if (starting) {
                    plugin.getSystemConfig().set("starting_dungeons_spawned", plugin.getSystemConfig().getInt("starting_dungeons_spawned") + 1);
                    plugin.saveSystemConfig();
                }
                this.cancel();

                newLoc = null;
            }
        }.runTaskTimer(plugin, 5, 120);
    }

    public void loot (Player player, Location location, Random random, EntityType spawnerType) {
        final World world = location.getWorld();
        final ItemStack pickaxe = player.getInventory().getItemInMainHand();
        final int fortune = pickaxe.hasItemMeta() && pickaxe.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) ? pickaxe.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0;

        LootContext.Builder builder = new LootContext.Builder(location);
        builder.luck((float) player.getAttribute(Attribute.GENERIC_LUCK).getValue());
        builder.killer(player);
        LootContext lootContext = builder.build();

        SpawnerTable spawnerTable = new SpawnerTable(plugin, random, player, spawnerType, fortune);
        Collection<ItemStack> collection = spawnerTable.populateLoot(random, lootContext);
        ArrayList<ItemStack> treasure_list = (ArrayList<ItemStack>) collection;

        for (ItemStack treasure : treasure_list) {
            world.dropItemNaturally(location, treasure);
        }

    }
}
