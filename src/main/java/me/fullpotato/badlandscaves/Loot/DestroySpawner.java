package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class DestroySpawner implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();
    private final HashMap<Material, Material> matMap = new HashMap<>();
    private static Location newLoc;
    private final ArrayList<Material> pickaxes = new ArrayList<>();
    private final EntityType[] mobTypes = {
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.WITCH,
            EntityType.CREEPER,
    };


    public DestroySpawner(BadlandsCaves plugin) {
        this.plugin = plugin;
        matMap.put(Material.COBBLESTONE , Material.MOSSY_COBBLESTONE);
        matMap.put(Material.COBBLESTONE_SLAB , Material.MOSSY_COBBLESTONE_SLAB);
        matMap.put(Material.COBBLESTONE_WALL , Material.MOSSY_COBBLESTONE_WALL);

        pickaxes.add(Material.WOODEN_PICKAXE);
        pickaxes.add(Material.STONE_PICKAXE);
        pickaxes.add(Material.IRON_PICKAXE);
        pickaxes.add(Material.GOLDEN_PICKAXE);
        pickaxes.add(Material.DIAMOND_PICKAXE);
        pickaxes.add(Material.NETHERITE_PICKAXE);
    }

    @EventHandler
    public void breakSpawner (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            final Player player = event.getPlayer();
            if (pickaxes.contains(player.getInventory().getItemInMainHand().getType())) {
                incrementChaos(false);
                final CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
                final Location location = block.getLocation().add(0.5, 0.5, 0.5);
                final World world = location.getWorld();
                world.spawnParticle(Particle.FLAME, location, 50, 0.1, 0.1, 0.1, 1);
                world.playSound(location, Sound.ENTITY_BLAZE_HURT, SoundCategory.BLOCKS, 0.5F, 0.4F);
                world.playSound(location, Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1, 1);
                world.playSound(location, Sound.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.1F, 0.5F);


                if (!spawner.getSpawnedType().equals(EntityType.BLAZE)) {
                    ParticleShapes.particleLine(null, Particle.FLASH, location.clone().add(0, 1, 0), location.clone().add(0, 255, 0), 0, null, 1);
                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                        online.playSound(online.getLocation(), "custom.darkrooms_whispers", SoundCategory.BLOCKS, 0.4F, 0.7F);
                        online.playSound(online.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.6F, 0.5F);
                    }

                    getNewLocation(block.getLocation(), random, 500);
                    makeDungeon(spawner.getSpawnedType(), random, false, false);
                }

                loot(player, block.getLocation(), random, spawner.getSpawnedType());
            }
            else if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        }
    }

    public void incrementChaos(boolean silent) {
        int current_chaos = plugin.getSystemConfig().getInt("chaos_level");
        if (current_chaos < 100) {
            plugin.getSystemConfig().set("chaos_level", current_chaos + 1);
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
                    location.clone().add(5, 0, 0),
                    location.clone().add(-5, 0, 0),
                    location.clone().add(0, 0, 5),
                    location.clone().add(0, 0, -5),
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
                for (int x = -4; x <= 4; x++) {
                    for (int z = -4; z <= 4; z++) {
                        for (int y = -1; y <= 4; y++) {
                            Location parse = newLoc.clone().add(x, y, z);
                            parse.getBlock().setType(Material.CAVE_AIR);
                        }
                    }
                }

                Location cloned = newLoc.clone();
                StructureTrack track = new StructureTrack(plugin, cloned, -4, -1, -4, 0, 0, 0, 1 - (0.003 * chaos), "badlandscaves:dungeon", BlockFace.DOWN);
                track.load();

                newLoc.getBlock().setType(Material.SPAWNER);
                CreatureSpawner spawner = (CreatureSpawner) newLoc.getBlock().getState();
                spawner.setSpawnedType(finalType);
                spawner.setMinSpawnDelay(200 - chaos);
                spawner.setMaxSpawnDelay(800 - (5 * chaos));
                spawner.setMaxNearbyEntities((int) (6 + (0.1 * chaos)));
                spawner.update();

                for (int x = -4; x <= 4; x++) {
                    for (int z = -4; z <= 4; z++) {
                        for (int y = -1; y <= 4; y++) {
                            if (random.nextInt(100) < ((0.65 * chaos) + 10)) {
                                Location parse = newLoc.clone().add(x, y, z);
                                if (matMap.containsKey(parse.getBlock().getType())) {
                                    parse.getBlock().setType(matMap.get(parse.getBlock().getType()));
                                }
                            }
                        }
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

        SpawnerTable spawnerTable = new SpawnerTable(plugin, player, spawnerType, fortune);
        Collection<ItemStack> collection = spawnerTable.populateLoot(random, lootContext);
        ArrayList<ItemStack> treasure_list = (ArrayList<ItemStack>) collection;

        for (ItemStack treasure : treasure_list) {
            world.dropItemNaturally(location, treasure);
        }

    }
}
