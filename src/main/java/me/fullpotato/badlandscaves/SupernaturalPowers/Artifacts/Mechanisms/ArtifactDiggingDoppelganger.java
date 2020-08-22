package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.StarlightPaxelMechanism;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class ArtifactDiggingDoppelganger extends ArtifactMechanisms implements Listener {
    private final Random random;
    private final StarlightPaxelMechanism paxelMechanism;
    private final FakePlayerNMS fakePlayerNMS;
    private final EnhancedEyesNMS enhancedEyesNMS;
    private final int cost;
    private final int drain;
    private final Map<BlockFace, Integer> faceYawMap;
    private final Set<Material> blacklisted;
    private final Map<Material, Integer> lightLevelMap;
    private final Material[] bridgingBlocks = {
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,
            Material.COBBLESTONE,
            Material.DIRT,
            Material.NETHERRACK,
            Material.BASALT,
            Material.BLACKSTONE
    };

    public ArtifactDiggingDoppelganger(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, Random random, StarlightPaxelMechanism paxelMechanism) {
        super(plugin, voidmatter, artifactManager);
        fakePlayerNMS = plugin.getFakePlayerNMS();
        enhancedEyesNMS = plugin.getEnhancedEyesNMS();
        this.random = random;
        this.paxelMechanism = paxelMechanism;

        this.faceYawMap = new HashMap<>();
        this.faceYawMap.put(BlockFace.SOUTH, 0);
        this.faceYawMap.put(BlockFace.WEST, 90);
        this.faceYawMap.put(BlockFace.NORTH, 180);
        this.faceYawMap.put(BlockFace.EAST, 270);

        this.blacklisted = new HashSet<>();
        blacklisted.add(Material.LAVA);
        blacklisted.add(Material.WATER);

        this.lightLevelMap = new HashMap<>();
        lightLevelMap.put(Material.TORCH, 1);
        lightLevelMap.put(Material.SOUL_TORCH, 5);
        cost = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_cost") * 3;
        drain = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_drain") * 5;
    }

    @EventHandler
    public void mineBlock (BlockBreakEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if (player.isSneaking()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack mainhand = player.getInventory().getItemInMainHand();
                    if (voidmatter.isVoidmatterTool(mainhand) && artifactManager.hasArtifact(player, Artifact.DIGGING_DOPPELGANGER)) {
                        final ItemStack offhand = player.getInventory().getItemInOffHand();
                        if (offhand.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.POSSESS))) {
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
                                                final Player clone = fakePlayerNMS.summonFakePlayer(spawn, player, null, null);
                                                fakePlayerNMS.move(spawn, clone, null, true);
                                                fakePlayerNMS.giveHandItem(clone, null, mainhand);

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
                enhancedEyesNMS.highlightEntity(original, clone);

                //PLACE BLOCKS------------------------------------------------------------------------------------------
                final Block platform = spawnClone[0].getBlock().getRelative(BlockFace.DOWN);
                if (platform.isPassable()) {
                    for (Material bridgeType : bridgingBlocks) {
                        final int bridgeSlot = original.getInventory().first(bridgeType);
                        if (bridgeSlot != -1) {
                            final ItemStack bridgeItem = original.getInventory().getItem(bridgeSlot);
                            if (bridgeItem != null) {
                                fakePlayerNMS.giveHandItem(clone, null, bridgeItem.clone());
                                bridgeItem.setAmount(bridgeItem.getAmount() - 1);
                                platform.setType(bridgeType);
                                spawnClone[0].setPitch(90);

                                fakePlayerNMS.move(spawnClone[0], clone, null, true);
                                fakePlayerNMS.damage(clone, null, false);
                                return;
                            }
                        }
                    }
                    this.cancel();
                    return;
                }

                //PLACE TORCHES-----------------------------------------------------------------------------------------
                final int lightLevel = spawnClone[0].getBlock().getLightLevel();
                if (lightLevel < 8) {
                    for (final Material lightType : lightLevelMap.keySet()) {
                        final int lightSlot = original.getInventory().first(lightType);
                        if (lightSlot != -1) {
                            final ItemStack light = original.getInventory().getItem(lightSlot);
                            if (light != null) {
                                if (lightLevel <= lightLevelMap.get(lightType)) {
                                    fakePlayerNMS.giveHandItem(clone, null, light.clone());
                                    light.setAmount(light.getAmount() - 1);
                                    spawnClone[0].getBlock().setType(lightType);
                                    spawnClone[0].setPitch(90);

                                    fakePlayerNMS.move(spawnClone[0], clone, null, true);
                                    fakePlayerNMS.damage(clone, null, false);
                                    return;
                                }
                                break;
                            }
                        }
                    }
                }


                //MINE BLOCKS-------------------------------------------------------------------------------------------
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

                //MOVE FORWARD------------------------------------------------------------------------------------------
                spawnClone[0] = spawnClone[0].add(doubleCloneLoc.getDirection().normalize().multiply(0.2));
                fakePlayerNMS.move(spawnClone[0], clone, null, true);

                //MANA DRAIN
                PlayerScore.MANA.setScore(plugin, original, mana - (drain / 20.0));
                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, original, 300);
                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, original, 60);
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.setScore(plugin, original, 0);
                fakePlayerNMS.remove(clone);

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
                boolean pick = paxelMechanism.pickaxeBlocks.contains(block.getType());
                boolean axe = paxelMechanism.axeBlocks.contains(block.getType());
                boolean shovel = paxelMechanism.shovelBlocks.contains(block.getType());
                boolean rightTool = (voidmatter.isVoidmatterPickaxe(tool) && pick) || (voidmatter.isVoidmatterAxe(tool) && axe) || (voidmatter.isVoidmatterShovel(tool) && shovel);
                if (!rightTool && random.nextBoolean()) {
                    fakePlayerNMS.damage(clone, null, false);
                    return;
                }

                if (hasFailed(block.getLocation())) return;
                fakePlayerNMS.giveHandItem(clone, null, tool);
                fakePlayerNMS.move(spawnClone[0], clone, null, true);

                Sound sound = null;
                if (pick) sound = Sound.BLOCK_STONE_BREAK;
                else if (axe) sound = Sound.BLOCK_WOOD_BREAK;
                else if (shovel) sound = Sound.BLOCK_GRAVEL_BREAK;
                if (sound != null) world.playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 1, 1);

                world.spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.1, 0.1, 0.1, 0, block.getType().createBlockData());
                block.breakNaturally(tool);
                fakePlayerNMS.damage(clone, null, false);

                if (original.getInventory().first(tool) != -1) {
                    final ItemMeta meta = tool.getItemMeta();
                    if (meta != null) {
                        if (random.nextInt(100) < (200 / (meta.getEnchantLevel(Enchantment.DURABILITY) + 1))) {
                            final Damageable damageable = (Damageable) meta;
                            final int durability = damageable.getDamage();
                            if (durability < tool.getType().getMaxDurability() - 1) {
                                damageable.setDamage(durability + 1);
                                tool.setItemMeta((ItemMeta) damageable);
                                return;
                            }
                        }
                        else return;
                    }
                }
                this.cancel();
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
