package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class ArtifactDistractingDoppelganger extends ArtifactMechanisms {
    private final FakePlayerNMS nms;
    private final int cost;
    private final int range = 10;
    private final int limit = 50;
    public ArtifactDistractingDoppelganger(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
        this.nms = plugin.getFakePlayerNMS();
        this.cost = plugin.getOptionsConfig().getInt("hardmode_values.artifact_costs.distraction_clone");
    }

    public void summonDistractionClone (Player player, Location location) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                if (mana >= cost) {
                    final Location onGround = setOnGround(location.clone());

                    final Player clone = nms.summonFakePlayer(location, player, null, null, true);
                    nms.move(location, clone, null, true);

                    final int[] timer = {0};
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline() || player.isDead() || timer[0] > limit) {
                                this.cancel();
                                return;
                            }
                            timer[0]++;

                            nms.giveHandItem(clone, null, player.getInventory().getItemInMainHand());
                            nms.giveHandItem(clone, null, player.getInventory().getItemInOffHand(), true);

                            //Get Nearby Monsters and Entities
                            Set<Monster> monsters = new HashSet<>();
                            Set<LivingEntity> living = new HashSet<>();
                            for (Entity entity : clone.getNearbyEntities(range, range, range)) {
                                if (!entity.equals(player) && entity instanceof LivingEntity) {
                                    living.add((LivingEntity) entity);

                                    if (entity instanceof Monster) {
                                        monsters.add((Monster) entity);
                                    }
                                }
                            }

                            //Set Target
                            monsters.forEach(monster -> {
                                if (monster.getTarget() != null && monster.getTarget().equals(player)) {
                                    monster.setTarget(clone);
                                }
                            });

                            //Find closest entity
                            LivingEntity closest = null;
                            double closestDist = Double.MAX_VALUE;
                            for (LivingEntity livingEntity : living) {
                                if (livingEntity.getWorld().equals(clone.getWorld())) {
                                    final double dist = livingEntity.getLocation().distanceSquared(clone.getLocation());
                                    if (dist < closestDist) {
                                        closest = livingEntity;
                                        closestDist = dist;
                                    }
                                }
                            }

                            final Location cloneLoc = clone.getLocation();
                            //Set clone hit closest entity
                            if (closest != null) {
                                final Location closestLoc = closest.getLocation();
                                cloneLoc.setDirection(closestLoc.clone().subtract(cloneLoc).toVector());
                                nms.move(cloneLoc, clone, null, true);
                                nms.damage(clone, null,false);

                                if (closestLoc.distanceSquared(cloneLoc) < 9) {
                                    closest.damage(1, player);
                                }
                            }

                            //Fall
                            if (cloneLoc.getY() > onGround.getY()) {
                                cloneLoc.subtract(0, 0.3, 0);
                                nms.move(cloneLoc, clone, null, true);
                            }
                        }

                        @Override
                        public synchronized void cancel() throws IllegalStateException {
                            super.cancel();

                            //Get Nearby Monsters and Entities
                            for (Entity entity : clone.getNearbyEntities(range, range, range)) {
                                if (entity instanceof Monster) {
                                    Monster monster = (Monster) entity;
                                    if (monster.getTarget() != null && monster.getTarget().equals(clone)) {
                                        monster.setTarget(null);
                                    }
                                }
                            }

                            clone.getWorld().spawnParticle(Particle.FLASH, clone.getLocation(), 1);
                            nms.remove(clone);
                        }
                    }.runTaskTimer(plugin, 0, 0);
                }
            }
        }
    }

    public Location setOnGround(Location location) {
        final Block block = location.getBlock();
        final Block under = block.getRelative(BlockFace.DOWN);
        if (under.isPassable()) return setOnGround(under.getLocation());
        return block.getLocation();
    }

}
