package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ArtifactBloodsappingBayonet extends ArtifactMechanisms implements Listener {
    public ArtifactBloodsappingBayonet(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void hitEnemy (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack item = player.getInventory().getItemInMainHand();
                    if (voidmatter.isVoidmatterBlade(item) && artifactManager.hasArtifact(player, Artifact.BLOODSAPPING_BAYONET)) {
                        final double mult = artifactManager.hasArtifact(player, Artifact.BLOODSAPPING_BOW) ? 0.5 : 0.25;

                        player.setHealth(Math.max(Math.min(player.getHealth() + (Math.pow(event.getFinalDamage(), 0.5) * mult * player.getAttackCooldown()), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()), 0));
                    }
                }
            }
        }
    }
}
