package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.StarlightPaxelMechanism;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NebuliteHammer extends NebuliteMechanisms implements Listener {
    public Map<BlockFace, int[][]> blockFaceHashMap = new HashMap<>();
    private StarlightPaxelMechanism starlightPaxelMechanism;

    public NebuliteHammer(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager, StarlightPaxelMechanism starlightPaxelMechanism) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        this.starlightPaxelMechanism = starlightPaxelMechanism;
        initializeHashMap();
    }

    @EventHandler
    public void hammerDestroy(BlockBreakEvent event) {
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
                            if (nebulite != null && nebulite.equals(Nebulite.STARLIGHT_HAMMER)) {
                                final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
                                if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlock().equals(block)) {
                                    final BlockFace blockFace = rayTraceResult.getHitBlockFace();
                                    if (blockFace != null && blockFaceHashMap.containsKey(blockFace)) {
                                        final Location location = block.getLocation();
                                        final float hardness = block.getType().getHardness() + 3;
                                        final int[][] directions = blockFaceHashMap.get(blockFace);
                                        for (int[] direction : directions) {
                                            Block offset = location.clone().add(direction[0], direction[1], direction[2]).getBlock();
                                            if (offset.getType().getHardness() > 0 && offset.getType().getHardness() <= hardness) {
                                                //Check if block can be auto-smelted
                                                if (starlightPaxelMechanism.SmeltBreakBlock(offset, item)) {
                                                    offset.breakNaturally(new ItemStack(Material.AIR)); //break ore with nothing to drop nothing
                                                }
                                                //Otherwise break normally
                                                else {
                                                    offset.breakNaturally(item);
                                                }
                                            }
                                        }
                                    }
                                }

                                starlightCharge.setCharge(item, starlightCharge.getCharge(item) - 9);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void initializeHashMap () {
        int[][] NS = {
                {0, 1, 0},
                {0, -1, 0},
                {1, 0, 0},
                {1, 1, 0},
                {1, -1, 0},
                {-1, 0, 0},
                {-1, 1, 0},
                {-1, -1, 0},
        };

        int[][] EW = {
                {0, 1, 0},
                {0, -1, 0},
                {0, 0, 1},
                {0, 1, 1},
                {0, -1, 1},
                {0, 0, -1},
                {0, 1, -1},
                {0, -1, -1},
        };

        int[][] UD = {
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {1, 0, 1},
                {-1, 0, 1},
                {0, 0, -1},
                {1, 0, -1},
                {-1, 0, -1},
        };

        blockFaceHashMap.put(BlockFace.NORTH, NS);
        blockFaceHashMap.put(BlockFace.SOUTH, NS);
        blockFaceHashMap.put(BlockFace.EAST, EW);
        blockFaceHashMap.put(BlockFace.WEST, EW);
        blockFaceHashMap.put(BlockFace.UP, UD);
        blockFaceHashMap.put(BlockFace.DOWN, UD);
    }
}
