package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactSoulHeist;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class WithdrawIndicatorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final ArtifactManager artifactManager;
    private final ParticleShapes particleShapes;
    private final TargetEntity targetEntity;
    private final World voidWorld;
    private final ItemStack withdrawSpell;
    private final PossessionNMS possessionNMS;
    private final Particle.DustOptions particleOptions;

    public WithdrawIndicatorRunnable(BadlandsCaves plugin, ArtifactManager artifactManager, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.artifactManager = artifactManager;
        this.particleShapes = particleShapes;

        this.targetEntity = new TargetEntity();
        this.voidWorld = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
        this.withdrawSpell = plugin.getCustomItemManager().getItem(CustomItem.WITHDRAW);
        this.possessionNMS = plugin.getPossessionNMS();
        this.particleOptions = new Particle.DustOptions(Color.fromRGB(107, 3, 252), 1);
    }


    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean hasPowers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;

            //Guard clauses
            if (!hasPowers) continue;                                                               // User must be sorcerer
            if (!player.getInventory().getItemInOffHand().isSimilar(withdrawSpell)) continue;       // User must have spell equipped
            if (player.getWorld().equals(voidWorld)) continue;                                      // User is not already using Withdraw
            if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0) continue;     // User is not being silenced

            //Artifact Soul Heist extra behaviour
            if (artifactManager.hasArtifact(player, Artifact.SOUL_HEIST)) {
                final LivingEntity target = targetEntity.findTargetLivingEntity(player.getEyeLocation(), 10, 0.2, 0.2, false, player);
                //More guard glauses
                if (target == null || !player.hasLineOfSight(target)) continue;                                                     // Target exists and player has line of sight
                if (target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean()) continue;          // Target is not under possession
                if (target instanceof Player && (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, (Player) target) != 0) continue; // Target is not a player who has unlocked Withdraw

                final Location location = target.getLocation();
                final Location headLocation = target.getLocation().add(0, target.getHeight(), 0);

                //Visual Effects
                possessionNMS.setIndicator(player, target);
                particleShapes.line(player, Particle.REDSTONE, location, headLocation, 0, particleOptions, 1);
                particleShapes.circle(player, Particle.REDSTONE, location, target.getWidth() * 2, 0, particleOptions);
                particleShapes.circle(player, Particle.REDSTONE, headLocation, target.getWidth() * 2, 0, particleOptions);
            }



        }


    }
}
