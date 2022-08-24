package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables.ConvergingProjectile;
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
    public ArtifactConvergingSwings(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
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
        final Location front = player.getEyeLocation().add(player.getLocation().getDirection().normalize());

        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        PlayerScore.MANA.setScore(plugin, player, mana - cost);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 3
        ));

        final int times = 25;
        final double range = 0.5;
        new ConvergingProjectile(plugin, player, front, range, times).runTaskTimer(plugin, 0, 0);
    }
}
