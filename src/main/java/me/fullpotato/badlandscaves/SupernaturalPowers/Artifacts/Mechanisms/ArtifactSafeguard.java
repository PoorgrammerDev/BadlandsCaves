package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;

public class ArtifactSafeguard extends ArtifactMechanisms implements Listener {

    public ArtifactSafeguard(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
    }

    @EventHandler
    public void FatalHit(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        //damaged entity must be a player
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        //must be a fatal hit
        if (event.getFinalDamage() < player.getHealth()) return;

        //Player must be in possession
        if (!PlayerScore.IN_POSSESSION.hasScore(plugin, player) ||
            (byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == (byte) 0) return; 
            
        //must have safeguard artifact
        if (!this.artifactManager.hasArtifact(player, Artifact.SAFEGUARD)) return;

        //now, cancel possession and cancel the hit
        event.setCancelled(true);
        PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);

        //extinguish the player, remove damaging effects
        player.setFireTicks(0);
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WITHER);
    }
    
}
