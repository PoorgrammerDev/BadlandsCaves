package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;

public class ArtifactRecoveryRoll extends ArtifactMechanisms implements Listener {

    public ArtifactRecoveryRoll(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
    }

    @EventHandler
    public void FallDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        //Fall damage or velocity damage
        if (event.getCause() != DamageCause.FALL && event.getCause() != DamageCause.FLY_INTO_WALL) return;

        //Is a player
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        //Is Sorcerer
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 0) return;

        //Has ARTIFACT recovery roll
        if (!this.artifactManager.hasArtifact(player, Artifact.RECOVERY_ROLL)) return;

        event.setCancelled(true);
    }
    
}
