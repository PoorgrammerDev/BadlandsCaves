package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArtifactSummonersRift extends ArtifactMechanisms implements Listener {
    private final NamespacedKey key;
    private final ZombieBossBehavior locationFinder;
    private final ArtifactFleetingSpirits fleetingSpirits;
    private final Random random;
    public ArtifactSummonersRift(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, ArtifactFleetingSpirits fleetingSpirits, Random random) {
        super(plugin, voidmatter, artifactManager);
        this.key = new NamespacedKey(plugin, "summoner");
        locationFinder = new ZombieBossBehavior(plugin, random);
        this.fleetingSpirits = fleetingSpirits;
        this.random = random;
    }

    @EventHandler
    public void arrowShoot(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof AbstractArrow && event.getEntity() instanceof Player) {
            if (event.getForce() >= 1) {
                final AbstractArrow arrow = (AbstractArrow) event.getProjectile();
                final Player player = (Player) event.getEntity();
                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        final ItemStack bow = event.getBow();
                        if (bow != null && voidmatter.isVoidmatterBow(bow)) {
                            if (artifactManager.hasArtifact(player, Artifact.SUMMONERS_RIFT)) {
                                final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                                final int cost = plugin.getOptionsConfig().getInt("hardmode_values.artifact_costs.summoners_rift");
                                final int clones = 1 + (chaos / 10);

                                if (mana >= cost) {
                                    PlayerScore.MANA.setScore(plugin, player, mana - cost);
                                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                    PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                                            ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                                            plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 3
                                    ));

                                    arrow.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

                                    final Location location = arrow.getLocation();
                                    final Vector velocity = arrow.getVelocity();

                                    int[] ran = {0};
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                                            if (ran[0] > clones || mana < cost) {
                                                this.cancel();
                                            }
                                            else {
                                                PlayerScore.MANA.setScore(plugin, player, mana - cost);
                                                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                                                        ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                                                        plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 3
                                                ));

                                                final Arrow trail = (Arrow) arrow.getWorld().spawnEntity(location, EntityType.ARROW);
                                                trail.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                                                trail.setVelocity(velocity);
                                                trail.setShooter(player);
                                                trail.setDamage(arrow.getDamage());
                                                trail.setCritical(arrow.isCritical());
                                                trail.setFireTicks(arrow.getFireTicks());
                                                trail.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
                                                ran[0]++;
                                            }
                                        }
                                    }.runTaskTimer(plugin, 0, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void arrowHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof AbstractArrow && event.getEntity() instanceof LivingEntity) {
            final AbstractArrow arrow = (AbstractArrow) event.getDamager();
            final LivingEntity entity = (LivingEntity) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player) arrow.getShooter();
                
                // summons should not spawn if they shoot themselves
                if (entity.equals(player)) return;

                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        if (arrow.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                            final Byte result = arrow.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                            if (result != null && result == (byte) 1) {
                                if (entity instanceof Vindicator) {
                                    final Vindicator vindicator = (Vindicator) entity;
                                    final PersistentDataContainer dataContainer = vindicator.getPersistentDataContainer();
                                    if (dataContainer.has(new NamespacedKey(plugin, "minion"), PersistentDataType.BYTE)) {
                                        final Byte isMinionResult = dataContainer.get(new NamespacedKey(plugin, "minion"), PersistentDataType.BYTE);
                                        if (isMinionResult != null && isMinionResult == 1) {
                                            if (dataContainer.has(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING)) {
                                                final String ownerResult = dataContainer.get(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING);
                                                if (ownerResult != null && player.getUniqueId().toString().equals(ownerResult)) {
                                                    event.setCancelled(true);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }


                                entity.setNoDamageTicks(0);
                                final Location spawnLoc = locationFinder.getNearbyLocation(arrow.getLocation(), random, 5);
                                if (spawnLoc != null && spawnLoc.distanceSquared(entity.getLocation()) > 2) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            arrow.getWorld().spawnParticle(Particle.FLASH, spawnLoc.clone().add(0, 0.5, 0), 1, 0, 0, 0, 0);
                                            fleetingSpirits.spawnVindicator(player, entity, spawnLoc, false);
                                        }
                                    }.runTaskLater(plugin, 20);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
