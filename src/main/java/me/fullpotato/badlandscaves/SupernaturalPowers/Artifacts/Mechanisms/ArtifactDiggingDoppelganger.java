package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArtifactDiggingDoppelganger extends ArtifactMechanisms implements Listener {
    private final FakePlayerNMS nms;
    private final int cost = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_cost") * 3;
    private final int drain = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_drain") * 5;
    private final Map<BlockFace, Integer> faceYawMap;
    private final Set<Material> blacklisted;

    public ArtifactDiggingDoppelganger(BadlandsCaves plugin) {
        super(plugin);
        nms = plugin.getFakePlayerNMS();

        this.faceYawMap = new HashMap<>();
        this.faceYawMap.put(BlockFace.SOUTH, 0);
        this.faceYawMap.put(BlockFace.WEST, 90);
        this.faceYawMap.put(BlockFace.NORTH, 180);
        this.faceYawMap.put(BlockFace.EAST, 270);

        this.blacklisted = new HashSet<>();
        blacklisted.add(Material.LAVA);
        blacklisted.add(Material.WATER);
    }

    @EventHandler
    public void mineBlock (BlockBreakEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if (player.isSneaking()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack mainhand = player.getInventory().getItemInMainHand().clone();
                    if (voidmatter.isVoidmatterTool(mainhand) && artifactManager.hasArtifact(player, Artifact.DIGGING_DOPPELGANGER)) {
                        final ItemStack offhand = player.getInventory().getItemInOffHand();
                        if (offhand.isSimilar(CustomItem.POSSESS.getItem())) {
                            if ((byte) PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.getScore(plugin, player) == 0) {
                                final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                                if (mana >= cost + (drain / 20.0)) {
                                    final Block block = event.getBlock();
                                    final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
                                    if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlock().equals(block)) {
                                        final BlockFace blockFace = rayTraceResult.getHitBlockFace();
                                        if (blockFace != null) {
                                            final BlockFace opposite = blockFace.getOppositeFace();
                                            if (faceYawMap.containsKey(opposite)) {
                                                //Mana costs, setting score to active
                                                PlayerScore.MANA.setScore(plugin, player, mana - (cost + drain / 20.0));
                                                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);
                                                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                                PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.setScore(plugin, player, 1);

                                                //Spawn in clone
                                                final Location location = player.getLocation();
                                                final Location spawn = new Location(player.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5, faceYawMap.get(opposite), 0);
                                                final Player clone = nms.summonFakePlayer(spawn, player, null, null);
                                                nms.move(spawn, clone, null, true);
                                                nms.giveHandItem(clone, null, mainhand);

                                                //Runnable mechanism
                                                cloneMechanism(player, clone, spawn, mainhand);

                                                //Effects
                                                player.playSound(player.getLocation(), "custom.supernatural.possession.enter", SoundCategory.PLAYERS, 0.5F, 1);
                                                player.spawnParticle(Particle.REDSTONE, player.getLocation(), 20, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
                                                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                                    if (entity instanceof Player) {
                                                        Player other = (Player) entity;
                                                        if (!(other.equals(player)) && other.getWorld().equals(player.getWorld()) && other.getLocation().distanceSquared(player.getLocation()) < 100) {
                                                            other.playSound(player.getLocation(), "custom.supernatural.possession.enter", SoundCategory.PLAYERS, 0.3F, 1);
                                                            other.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
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
        }
    }

    public void cloneMechanism (Player original, Player clone, Location spawn, ItemStack tool) {
        final World world = original.getWorld();
        final Location[] spawnClone = {spawn.clone()};
        final int[] timer = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                final boolean active = (byte) PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.getScore(plugin, original) == 1;
                final double mana = (double) PlayerScore.MANA.getScore(plugin, original);
                if (original.isDead() || !original.isOnline() || mana <= (drain / 20.0) || hasFailed(spawnClone[0]) || !active) {
                    this.cancel();
                    return;
                }
                timer[0]++;

                final Location doubleCloneLoc = spawnClone[0].clone();
                doubleCloneLoc.setPitch(0);
                final BlockIterator blockIterator = new BlockIterator(doubleCloneLoc, 1, 1);
                final Set<Block> blocksInFront = new HashSet<>();
                while (blockIterator.hasNext()) {
                    blocksInFront.add(blockIterator.next());
                }

                for (Block block : blocksInFront) {
                    if (!block.isPassable()) {
                        if (timer[0] % 10 == 0) {
                            spawnClone[0].setPitch(0);
                            destroyBlock(block);
                        }
                        return;
                    }
                    else {
                        final Block under = block.getRelative(BlockFace.DOWN);
                        if (!under.isPassable()) {
                            if (timer[0] % 10 == 0) {
                                spawnClone[0].setPitch(45);
                                destroyBlock(under);
                            }
                            return;
                        }
                    }
                }

                spawnClone[0] = spawnClone[0].add(doubleCloneLoc.getDirection().normalize().multiply(0.2));
                nms.move(spawnClone[0], clone, null, true);

                PlayerScore.MANA.setScore(plugin, original, mana - (drain / 20.0));
                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, original, 300);
                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, original, 60);
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.setScore(plugin, original, 0);
                nms.remove(clone);

                original.playSound(original.getLocation(), "custom.supernatural.possession.leave", SoundCategory.PLAYERS, 0.5F, 1);
                original.spawnParticle(Particle.REDSTONE, clone.getLocation(), 20, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
                for (Entity entity : clone.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Player) {
                        Player powered = (Player) entity;
                        if (!(powered.equals(original)) && powered.getWorld().equals(world) && powered.getLocation().distanceSquared(clone.getLocation()) < 100) {
                            powered.playSound(clone.getLocation(), "custom.supernatural.possession.leave", SoundCategory.PLAYERS, 0.3F, 1);
                            powered.spawnParticle(Particle.REDSTONE, clone.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
                        }
                    }
                }
            }

            public void destroyBlock (Block block) {
                if (hasFailed(block.getLocation())) return;

                nms.move(spawnClone[0], clone, null, true);

                world.spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.1, 0.1, 0.1, 0, block.getType().createBlockData());
                world.playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1, 1);
                block.breakNaturally(tool);
                nms.damage(clone, null, false);
            }

            public boolean hasFailed (Location location) {
                final Block block = location.getBlock();
                if (block.getType().getBlastResistance() >= Material.BEDROCK.getBlastResistance() || blacklisted.contains(block.getType())) {
                    this.cancel();
                    return true;
                }
                return false;
            }
        }.runTaskTimer(plugin, 0, 0);
    }
}
