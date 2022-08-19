package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Possession;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Withdraw;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ArtifactSoulHeist extends ArtifactMechanisms {
    private final Withdraw withdraw;
    private final Possession possession;
    private final TargetEntity targetEntity;
    private final World voidWorld;
    public ArtifactSoulHeist(BadlandsCaves plugin, Withdraw withdraw, Possession possession, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
        this.withdraw = withdraw;
        this.possession = possession;
        this.targetEntity = new TargetEntity();
        this.voidWorld = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
    }

    public void run(Player player) {
        final LivingEntity target = targetEntity.findTargetLivingEntity(player.getEyeLocation(), 10, 0.2, 0.2, false, player);
        //Guard clauses
        if (target == null || !player.hasLineOfSight(target)) return; // Target exists and player has line of sight
        if (!isValidTarget(target)) return;                           // Target is valid

        //Target is a player
        if (target instanceof Player) {
            withdraw.enterWithdraw((Player) target, false);
        }

        //Target is not a player
        else {
            final Location targetLoc = target.getLocation();
            final Location voidLoc = targetLoc.clone();
            final int duration = 500;

            voidLoc.setWorld(voidWorld);
            target.teleport(voidLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

            if (possession.canPossess(target)) {
                target.setAI(false);
            }
            else {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 2));
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!target.isDead()) {
                        target.teleport(targetLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        target.setAI(true);
                    }
                }
            }.runTaskLater(plugin, duration);
        }

    }

    public boolean isValidTarget(LivingEntity target) {
        return (
                (!target.hasMetadata("possessed") || !target.getMetadata("possessed").get(0).asBoolean()) && // Target is not under possession
                (!(target instanceof Player) || (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, (Player) target) <= 0) // Target is not a player who has unlocked Withdraw
        );
    }

}
