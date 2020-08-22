package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.PositionManager;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class NebuliteDecisiveDisintegration extends NebuliteMechanisms implements Listener {
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(98, 0, 255), 1);
    private final PositionManager positionManager = new PositionManager();
    private final List<Material> ores = Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE, Material.ANCIENT_DEBRIS, Material.GILDED_BLACKSTONE);
    private final List<Material> logs = Arrays.asList(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG);
    private final List<Material> leaves = Arrays.asList(Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES);
    private final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN,
    };


    public NebuliteDecisiveDisintegration(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if (player.isSneaking()) return;
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;


            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                final Block block = event.getBlock();
                final EntityEquipment equipment = player.getEquipment();
                if (equipment != null) {
                    final ItemStack item = equipment.getItemInMainHand();
                    if (starlightTools.isStarlightPaxel(item) && starlightCharge.getCharge(item) > 0) {
                        final Nebulite[] nebulites = nebuliteManager.getNebulites(item);
                        for (Nebulite nebulite : nebulites) {
                            if (nebulite != null && nebulite.equals(Nebulite.DECISIVE_DISINTEGRATION)) {
                                final int limit = plugin.getOptionsConfig().getInt("hardmode_values.decisive_disintegration_limit");
                                if (ores.contains(block.getType())) {
                                    int[] count = {0};
                                    recursiveVeinMiner(player, block.getType(), block, item, item.clone(), count, limit);
                                }
                                else if (logs.contains(block.getType())) {
                                    Set<Block> treeParts = new HashSet<>();
                                    if (blockMinedIsTree(treeParts, block, limit)) {
                                        destroyTree(player, treeParts, item);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: 7/21/2020 maybe add a sound for this and vein miner
    public void destroyTree (Player player, Set<Block> treeParts, ItemStack item) {
        final Iterator<Block> iterator = treeParts.iterator();
        final ItemStack clone = item.clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || player.isSneaking() || !iterator.hasNext() || starlightCharge.getCharge(item) <= 0) {
                    this.cancel();
                    return;
                }

                final Block block = iterator.next();
                if (logs.contains(block.getType())) {
                    block.breakNaturally(clone);
                    block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().add(0.5, 0.5, 0.5), 1, dustOptions);

                    if (!leaves.contains(block.getType())) {
                        starlightCharge.setCharge(item, starlightCharge.getCharge(item) - 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    public boolean blockMinedIsTree (Set<Block> checked, Block block, int limit) {
        if (logs.contains(block.getType())) {
            int[] count = {0};
            getAllConnectedTreeParts(checked, block.getType(), block, count, limit);

            for (final Block adjacent : checked) {
                if (leaves.contains(adjacent.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void getAllConnectedTreeParts (Set<Block> checked, Material type, Block block, int[] count, int limit) {
        if (limit != -1 && count[0] > limit) return;

        for (BlockFace face : BlockFace.values()) {
            Block relative = block.getRelative(face);
            if ((relative.getType().equals(type) || leaves.contains(relative.getType())) && !checked.contains(relative)) {
                checked.add(relative);
                count[0]++;
                if (relative.getType().equals(type)) {
                    getAllConnectedTreeParts(checked, type, relative, count, limit);
                }
            }
        }
    }

    public void recursiveVeinMiner (Player player, Material type, Block block, ItemStack item, ItemStack clone, int[] count, int limit) {
        if (limit != -1 && count[0] > limit) return;
        if (!starlightTools.isStarlightPaxel(item) || starlightCharge.getCharge(item) <= 0) return;
        if (!player.isOnline() || player.isDead() || player.isSneaking()) return;

        for (BlockFace face : faces) {
            Block relative = block.getRelative(face);
            if (relative.getType().equals(type)) {
                relative.breakNaturally(clone);
                relative.getWorld().spawnParticle(Particle.REDSTONE, relative.getLocation().add(0.5, 0.5, 0.5), 1, dustOptions);

                count[0]++;
                starlightCharge.setCharge(item, starlightCharge.getCharge(item) - 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        recursiveVeinMiner(player, type, relative, item, clone, count, limit);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}
