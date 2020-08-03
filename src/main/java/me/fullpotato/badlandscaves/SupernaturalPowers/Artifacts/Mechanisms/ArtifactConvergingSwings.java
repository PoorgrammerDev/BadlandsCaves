package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtifactConvergingSwings extends ArtifactMechanisms implements Listener {
    private final int cost;
    public ArtifactConvergingSwings(BadlandsCaves plugin) {
        super(plugin);
        cost = plugin.getOptionsConfig().getInt("hardmode_values.artifact_costs.converging_swings");
    }

    @EventHandler
    public void attack (PlayerInteractEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Action action = event.getAction();
            if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {

                final Player player = event.getPlayer();
                if (player.getAttackCooldown() >= 1) {
                    final ItemStack item = event.getItem();
                    if (item != null && voidmatter.isVoidmatterBlade(item) && artifactManager.hasArtifact(player, Artifact.CONVERGING_SWINGS)) {
                        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                        if (mana >= cost) {
                            final TargetEntity targetEntityFinder = new TargetEntity();
                            final LivingEntity entity = targetEntityFinder.findTargetLivingEntity(player.getLocation(), 5, 0.2, 3, false, player);
                            if (entity == null) {
                                attemptConverge(player);
                            }
                        }
                    }
                }
            }
        }
    }

    public void attemptConverge(Player player) {
        final World world = player.getWorld();
        final Location front = player.getEyeLocation().add(player.getLocation().getDirection().normalize());
        final Location[] scout = {front.clone()};

        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        PlayerScore.MANA.setScore(plugin, player, mana - cost);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)), 30));

        final int times = 25;
        final int[] ran = {0};
        final double range = 0.5;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ran[0] > times || !scout[0].getBlock().isPassable()) {
                    this.cancel();
                    return;
                }

                scout[0] = scout[0].add(player.getLocation().getDirection().normalize());
                world.spawnParticle(Particle.REDSTONE, scout[0], 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(75, 0, 145), 1));

                world.getNearbyEntities(scout[0], range, range, range).forEach(target -> {
                    if (target instanceof LivingEntity) {
                        final LivingEntity livingTarget = (LivingEntity) target;
                        if (!livingTarget.equals(player)) {
                            this.cancel();
                            livingTarget.teleport(front, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            world.spawnParticle(Particle.SPELL_WITCH, scout[0], 25, 0.5, 1, 0.5);
                            world.spawnParticle(Particle.SPELL_WITCH, front, 25, 0.5, 1, 0.5);
                            player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);

                            for (Player powered : plugin.getServer().getOnlinePlayers()) {
                                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) {
                                    powered.playSound(front, "custom.supernatural.displace.warp", SoundCategory.HOSTILE, 0.3F, 1);
                                    powered.spawnParticle(Particle.SPELL_WITCH, front, 5, 0.1, 0.1, 0.1, 1);
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
