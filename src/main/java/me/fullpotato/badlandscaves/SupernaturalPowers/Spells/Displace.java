package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactDistractingDoppelganger;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class Displace extends UsePowers implements Listener {
    private final LineOfSightNMS nms;
    private final ManaBarManager manaBar;
    private final ArtifactManager artifactManager;
    private final ArtifactDistractingDoppelganger artifactDistractingDoppelganger;
    private final int cost;
    private final World backrooms;
    private final ParticleShapes particleShapes;

    public Displace(BadlandsCaves plugin, ArtifactManager artifactManager, ManaBarManager manaBar, ArtifactDistractingDoppelganger artifactDistractingDoppelganger, ParticleShapes particleShapes) {
        super(plugin, particleShapes);
        this.artifactManager = artifactManager;
        this.backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
        nms = plugin.getLineOfSightNMS();
        cost = plugin.getOptionsConfig().getInt("spell_costs.displace_mana_cost");
        this.manaBar = manaBar;
        this.artifactDistractingDoppelganger = artifactDistractingDoppelganger;
        this.particleShapes = particleShapes;
    }

    @EventHandler
    public void useDisplace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        if (player.getInventory().getItemInOffHand().isSimilar(plugin.getCustomItemManager().getItem(CustomItem.DISPLACE))) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1)) return;
                    if (((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0)) return;
                    if (attemptSilence(player)) return;
                    if (player.getWorld().equals(backrooms)) return;

                    boolean has_displace_marker = ((byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 1);
                    if (has_displace_marker) {
                        attemptTeleport(player);
                    }
                    else {
                        preventDoubleClick(player);
                        final int displace_level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
                        final int place_range = displace_level > 1 ? 20 : 10;
                        final BlockIterator iter = new BlockIterator(player, place_range);

                        Location lastLastBlockLoc = null;
                        Block lastBlock = iter.next();
                        Location lastBlockLocation = lastBlock.getLocation().add(0.5, 0.5, 0.5);
                        while (iter.hasNext()) {
                            lastLastBlockLoc = lastBlockLocation;
                            lastBlockLocation = lastBlock.getLocation().add(0.5, 0.5, 0.5);
                            lastBlock = iter.next();
                            if (lastBlock.getType().isSolid() || !nms.hasLineOfSight(player, lastBlockLocation) || !lastBlockLocation.getWorld().getWorldBorder().isInside(lastBlockLocation)) {
                                break;
                            }
                        }

                        assert lastLastBlockLoc != null;
                        attemptPlaceDisplaceMarker(player, lastLastBlockLoc, false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void destroyMarker (PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
            final ItemStack item = event.getMainHandItem();
            if (item != null && item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.DISPLACE))) {
                event.setCancelled(true);
                PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
            }
        }
    }

    public void attemptPlaceDisplaceMarker (Player player, Location location, boolean force) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
            if ((int) ActivePowers.DISPLACE.getLevelScore().getScore(plugin, player) > 0) {
                if (force || (byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 0) {
                    if (force || nms.hasLineOfSight(player, location)) {
                        PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 1);
                        player.playSound(player.getLocation(), "custom.supernatural.displace.place_marker", SoundCategory.PLAYERS, 0.5F, 1);

                        PlayerScore.DISPLACE_X.setScore(plugin, player, location.getX());
                        PlayerScore.DISPLACE_Y.setScore(plugin, player, location.getY());
                        PlayerScore.DISPLACE_Z.setScore(plugin, player, location.getZ());

                    }
                    else {
                        manaBar.displayMessage(player, "§cLine of Sight required!", 2, false);
                    }
                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                }
            }
        }
    }

    public void attemptTeleport (Player player) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
            final int displaceLevel = (int) ActivePowers.DISPLACE.getLevelScore().getScore(plugin, player);
            if (displaceLevel > 0) {
                if ((byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 1) {
                    final int warp_range = displaceLevel > 1 ? 30 : 15;
                    final boolean cancel_fall = displaceLevel > 1;
                    final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));

                    final double x = (double) PlayerScore.DISPLACE_X.getScore(plugin, player);
                    final double y = (double) PlayerScore.DISPLACE_Y.getScore(plugin, player);
                    final double z = (double) PlayerScore.DISPLACE_Z.getScore(plugin, player);
                    final float current_yaw = player.getLocation().getYaw();
                    final float current_pitch = player.getLocation().getPitch();
                    final Location displace_marker = new Location(player.getWorld(), x, y, z, current_yaw, current_pitch);

                    if (player.getLocation().distance(displace_marker) <= warp_range) {
                        if (nms.hasLineOfSight(player, displace_marker)) {
                            if (mana >= cost) {
                                preventDoubleClick(player);
                                if (cancel_fall) player.setFallDistance(0);
                                final Location originalLoc = player.getLocation();
                                player.teleport(displace_marker, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);

                                player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);
                                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                    if (entity instanceof Player) {
                                        Player powered = (Player) entity;
                                        if (!powered.equals(player) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) {
                                                powered.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.3F, 1);
                                                powered.spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 5, 0.1, 0.1, 0.1, 1);
                                            }
                                        }
                                    }
                                }

                                double new_mana = mana - (double) (cost);
                                PlayerScore.MANA.setScore(plugin, player, new_mana);
                                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);

                                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                                    //Undisplace Artifact
                                    if (artifactManager.hasArtifact(player, Artifact.UNDISPLACE)) {
                                        attemptPlaceDisplaceMarker(player, originalLoc, false);
                                    }

                                    //Momentous Momentum
                                    else if (artifactManager.hasArtifact(player, Artifact.MOMENTOUS_MOMENTUM)) {
                                        PlayerScore.MOMENTOUS_MOMENTUM_TIMER.setScore(plugin, player, 10);
                                    }

                                    //Distraction Clone
                                    else if (artifactManager.hasArtifact(player, Artifact.DISTRACTING_DOPPELGANGER)) {
                                        artifactDistractingDoppelganger.summonDistractionClone(player, originalLoc);
                                    }
                                }

                            }
                            else {
                                notEnoughMana(player);
                            }
                        }
                        else {
                            manaBar.displayMessage(player, "§cLine of Sight required!", 2, false);
                        }
                    }
                    else if (player.getLocation().distance(displace_marker) > (warp_range * 1.5)) {
                        PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
                    }
                }
            }
        }
    }
}
