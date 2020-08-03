package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Withdraw;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ArtifactSoulHeist extends ArtifactMechanisms {
    private final Random random = new Random();
    private final Withdraw withdraw;
    public ArtifactSoulHeist(BadlandsCaves plugin) {
        super(plugin);
        withdraw = new Withdraw(plugin);
    }

    public void launchProjectile(Player player) {
        final World world = player.getWorld();
        final Location front = player.getEyeLocation().add(player.getLocation().getDirection().normalize());
        final Location[] scout = {front.clone()};

        final int times = 25;
        final int[] ran = {0};
        final double range = 1;
        new BukkitRunnable() {
            @Override
            public void run() {
                final Chunk playerChunk = player.getLocation().getChunk();
                final Chunk scoutChunk = scout[0].getChunk();

                if (ran[0] > times || !scout[0].getBlock().isPassable() || (playerChunk.getX() != scoutChunk.getX() || playerChunk.getZ() != scoutChunk.getZ())) {
                    this.cancel();
                    return;
                }

                scout[0] = scout[0].add(player.getLocation().getDirection().normalize());
                world.spawnParticle(Particle.REDSTONE, scout[0], 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(107, 3, 252), 1));

                world.getNearbyEntities(scout[0], range, range, range).forEach(target -> {
                    if (target instanceof LivingEntity) {
                        final LivingEntity livingTarget = (LivingEntity) target;
                        if (!livingTarget.equals(player)) {
                            this.cancel();

                            final Location targetLoc = livingTarget.getLocation();
                            final Chunk targetChunk = targetLoc.getChunk();
                            if (playerChunk.getX() == targetChunk.getX() && playerChunk.getZ() == targetChunk.getZ()) {
                                if (livingTarget instanceof Player) {
                                    final Player targetPlayer = (Player) livingTarget;
                                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, targetPlayer) == 0 ||
                                            (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, targetPlayer) <= 0) {
                                        withdraw.enterWithdraw(player, false);
                                        withdraw.enterWithdraw(targetPlayer, false);
                                    }
                                }
                                else {
                                    withdraw.enterWithdraw(player, false);

                                    final Location voidLoc = targetLoc.clone();
                                    voidLoc.setWorld(player.getWorld());
                                    livingTarget.teleport(voidLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (!livingTarget.isDead()) {
                                                livingTarget.teleport(targetLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                            }
                                        }
                                    }.runTaskLater(plugin, random.nextInt(200) + 500);

                                }
                            }
                        }
                    }
                });
                ran[0]++;
            }
        }.runTaskTimer(plugin, 0, 0);
    }

}
