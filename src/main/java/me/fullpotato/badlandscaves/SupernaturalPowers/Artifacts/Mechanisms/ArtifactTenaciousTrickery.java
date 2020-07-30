package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactBaseItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ArtifactTenaciousTrickery extends ArtifactMechanisms implements Listener {
    public ArtifactTenaciousTrickery(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void preventDurabilityLoss(PlayerItemDamageEvent event) {
        if (event.getDamage() <= 0) return;

        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                final double maxMana = (double) PlayerScore.MAX_MANA.getScore(plugin, player);

                if (mana / maxMana >= 0.75D) {
                    final ItemStack item = event.getItem();
                    if (voidmatter.isVoidmatter(item)) {
                        final Map<ArtifactBaseItem, Artifact> artifactMap = artifactManager.getArtifacts(player);
                        Artifact artifact = null;
                        double multiplier = 1.0;
                        if (voidmatter.isVoidmatterArmor(item)) {
                            artifact = artifactMap.get(ArtifactBaseItem.VOIDMATTER_ARMOR);
                        } else if (voidmatter.isVoidmatterBlade(item)) {
                            artifact = artifactMap.get(ArtifactBaseItem.VOIDMATTER_BLADE);
                            multiplier = 1.5;
                        } else if (voidmatter.isVoidmatterBow(item)) {
                            artifact = artifactMap.get(ArtifactBaseItem.VOIDMATTER_BOW);
                            multiplier = 1.5;
                        } else if (voidmatter.isVoidmatterTool(item)) {
                            artifact = artifactMap.get(ArtifactBaseItem.VOIDMATTER_TOOLS);
                            multiplier = 2.0;
                        }

                        if (artifact != null && artifact.equals(Artifact.TENACIOUS_TRICKERY)) {
                            event.setCancelled(true);
                            PlayerScore.MANA.setScore(plugin, player, mana - (event.getDamage() * multiplier));
                            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                        }

                    }
                }
            }
        }

    }
}
