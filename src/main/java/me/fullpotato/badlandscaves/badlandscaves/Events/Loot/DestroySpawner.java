package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Structure;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class DestroySpawner implements Listener {
    private BadlandsCaves plugin;
    private HashMap<Material, Material> matMap = new HashMap<>();
    private static Location newLoc;


    public DestroySpawner(BadlandsCaves plugin) {
        this.plugin = plugin;
        matMap.put(Material.COBBLESTONE , Material.MOSSY_COBBLESTONE);
        matMap.put(Material.COBBLESTONE_SLAB , Material.MOSSY_COBBLESTONE_SLAB);
        matMap.put(Material.COBBLESTONE_WALL , Material.MOSSY_COBBLESTONE_WALL);
    }

    @EventHandler
    public void breakSpawner (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            final Player player = event.getPlayer();

            ArrayList<Material> pickaxes = new ArrayList<>();
            pickaxes.add(Material.WOODEN_PICKAXE);
            pickaxes.add(Material.STONE_PICKAXE);
            pickaxes.add(Material.IRON_PICKAXE);
            pickaxes.add(Material.GOLDEN_PICKAXE);
            pickaxes.add(Material.DIAMOND_PICKAXE);


            if (pickaxes.contains(player.getInventory().getItemInMainHand().getType())) {
                int current_chaos = plugin.getConfig().getInt("game_values.chaos_level");
                if (current_chaos < 100) {
                    plugin.getConfig().set("game_values.chaos_level", current_chaos + 1);
                    plugin.saveConfig();
                    plugin.getServer().broadcastMessage("§cChaos seeps into this realm...");
                }
                else {
                    plugin.getServer().broadcastMessage("§cThe world has reached its breaking point.");
                }


                Random random = new Random();
                CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();

                Location location = block.getLocation().add(0.5, 0.5, 0.5);
                World world = location.getWorld();
                world.spawnParticle(Particle.FLAME, location, 50, 0.1, 0.1, 0.1, 1);
                world.playSound(location, Sound.ENTITY_BLAZE_HURT, SoundCategory.BLOCKS, 0.5F, 0.4F);
                world.playSound(location, Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1, 1);
                world.playSound(location, Sound.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.1F, 0.5F);


                if (!spawner.getSpawnedType().equals(EntityType.BLAZE) && !spawner.getSpawnedType().equals(EntityType.SILVERFISH) && !spawner.getSpawnedType().equals(EntityType.CAVE_SPIDER)) {
                    ParticleShapes.particleLine(null, Particle.FLASH, location.clone().add(0, 1, 0), location.clone().add(0, 255, 0), 0, null, 1);
                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                        online.playSound(online.getLocation(), "custom.darkrooms_whispers", SoundCategory.BLOCKS, 0.5F, 0.7F);
                        online.playSound(online.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 1, 0.5F);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getServer().broadcastMessage("§3A new Dungeon has appeared!");
                        }
                    }.runTaskLaterAsynchronously(plugin, 60);

                    getNewLocation(block.getLocation(), random);
                    makeDungeon(spawner.getSpawnedType(), random);
                }

                loot(player, block.getLocation(), random, spawner.getSpawnedType());
            }
        }
    }

    public void getNewLocation(Location oldSpawnerLocation, Random random) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location output = null;
                int tries = 0;
                while ((output == null || !isViable(output)) && tries < 100) {
                    int[] offset = new int[2];
                    while (Math.abs(offset[0]) < 50 || Math.abs(offset[1]) < 50) {
                        offset = random.ints(2, -500, 501).toArray();
                    }
                    int y = random.nextInt(48) + 7;

                    output = oldSpawnerLocation.clone().add(offset[0], 0, offset[1]);
                    output.setY(y);
                    tries++;
                }

                newLoc = output;
            }
        }.runTaskAsynchronously(plugin);
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

    public void makeDungeon(EntityType type, Random random) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (newLoc == null) return;
                final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
                for (int x = -4; x <= 4; x++) {
                    for (int z = -4; z <= 4; z++) {
                        for (int y = -1; y <= 4; y++) {
                            Location parse = newLoc.clone().add(x, y, z);
                            parse.getBlock().setType(Material.CAVE_AIR);
                        }
                    }
                }

                Location cloned = newLoc.clone();
                cloned.subtract(4, 2 ,4);
                BlockData block = cloned.getBlock().getBlockData();
                cloned.getBlock().setType(Material.STRUCTURE_BLOCK);
                Structure structure = (Structure) cloned.getBlock().getState();
                structure.setUsageMode(UsageMode.LOAD);
                structure.setStructureName("badlandscaves:dungeon");
                structure.setIntegrity((float) (1 - (0.003 * chaos)));
                structure.update();
                cloned.add(0, 1, 0);
                BlockData over = cloned.getBlock().getBlockData();
                cloned.getBlock().setType(Material.REDSTONE_BLOCK);


                newLoc.getBlock().setType(Material.SPAWNER);
                CreatureSpawner spawner = (CreatureSpawner) newLoc.getBlock().getState();
                spawner.setSpawnedType(type);
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

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cloned.getBlock().setBlockData(over);
                        cloned.subtract(0, 1, 0);
                        cloned.getBlock().setBlockData(block);
                    }
                }.runTaskLater(plugin, 1);
                this.cancel();
                newLoc = null;
            }
        }.runTaskTimer(plugin, 5, 5);
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
