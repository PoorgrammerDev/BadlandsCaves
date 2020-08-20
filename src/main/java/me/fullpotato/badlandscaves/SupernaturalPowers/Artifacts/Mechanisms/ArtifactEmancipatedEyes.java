package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.EnhancedEyes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ArtifactEmancipatedEyes extends ArtifactMechanisms implements Listener {
    private final EnhancedEyes enhancedEyes;
    private final int initial_mana_cost;
    private final int constant_mana_drain;
    private final BlockFace[] blockFaces = {
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.EAST,
            BlockFace.WEST,
    };

    public ArtifactEmancipatedEyes(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, EnhancedEyes enhancedEyes) {
        super(plugin, voidmatter, artifactManager);
        this.enhancedEyes = enhancedEyes;
        initial_mana_cost = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_cost") / 3;
        constant_mana_drain = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_drain") / 2;
    }

    @EventHandler
    public void mineBlock (BlockBreakEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if (player.isSneaking()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack mainhand = player.getInventory().getItemInMainHand();
                    if (voidmatter.isVoidmatterTool(mainhand) && artifactManager.hasArtifact(player, Artifact.EMANCIPATED_EYES)) {
                        final ItemStack offhand = player.getInventory().getItemInOffHand();
                        if (offhand.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.ENHANCED_EYES))) {
                            final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                            if (mana >= initial_mana_cost + (constant_mana_drain / 20.0)) {
                                final Block block = event.getBlock();
                                final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
                                if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlock().equals(block)) {
                                    final BlockFace blockFace = rayTraceResult.getHitBlockFace();
                                    if (blockFace != null) {
                                        final BlockFace opposite = blockFace.getOppositeFace();
                                        final Set<Block> blocks = new HashSet<>();

                                        recursivelyFindBlocks(blocks, block, opposite, 25, 0);
                                        for (BlockFace face : blockFaces) {
                                            recursivelyFindBlocks(blocks, block.getRelative(face), opposite, 25, 0);
                                        }

                                        if (!blocks.isEmpty()) {
                                            final Set<Block> ores = blocks.stream().filter(test -> enhancedEyes.isOre(test.getType())).collect(Collectors.toSet());
                                            if (!ores.isEmpty()) {
                                                if ((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 0) {
                                                    enhancedEyes.enableEnhancedEyes(player, ores, null, false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void recursivelyFindBlocks (Set<Block> blocks, Block origin, BlockFace face, int range, int tracker) {
        if (tracker > range) return;

        final Block block = origin.getRelative(face);
        blocks.add(block);

        recursivelyFindBlocks(blocks, block, face, range, ++tracker);
    }
}
