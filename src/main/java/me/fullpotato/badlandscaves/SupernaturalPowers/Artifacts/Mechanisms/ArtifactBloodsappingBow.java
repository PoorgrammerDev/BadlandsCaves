package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ArtifactBloodsappingBow extends ArtifactMechanisms implements Listener {
    private final NamespacedKey bloodsappingKey;
    private final NamespacedKey forceKey;
    public ArtifactBloodsappingBow(BadlandsCaves plugin) {
        super(plugin);
        this.bloodsappingKey = new NamespacedKey(plugin, "bloodsapping");
        this.forceKey = new NamespacedKey(plugin, "bloodsapping_force");
    }

    @EventHandler
    public void arrowShoot(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof AbstractArrow && event.getEntity() instanceof Player) {
            final AbstractArrow arrow = (AbstractArrow) event.getProjectile();
            final Player player = (Player) event.getEntity();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack bow = event.getBow();
                    if (bow != null && voidmatter.isVoidmatterBow(bow)) {
                        if (artifactManager.hasArtifact(player, Artifact.BLOODSAPPING_BOW)) {
                            arrow.getPersistentDataContainer().set(bloodsappingKey, PersistentDataType.BYTE, (byte) 1);
                            arrow.getPersistentDataContainer().set(forceKey, PersistentDataType.FLOAT, event.getForce());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void arrowDamage (EntityDamageByEntityEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getDamager() instanceof AbstractArrow) {
                final AbstractArrow arrow = (AbstractArrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    final Player player = (Player) arrow.getShooter();
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        if (arrow.getPersistentDataContainer().has(bloodsappingKey, PersistentDataType.BYTE)) {
                            final Byte result = arrow.getPersistentDataContainer().get(bloodsappingKey, PersistentDataType.BYTE);
                            if (result != null && result == (byte) 1) {
                                if (arrow.getPersistentDataContainer().has(forceKey, PersistentDataType.FLOAT)) {
                                    final Float forceResult = arrow.getPersistentDataContainer().get(forceKey, PersistentDataType.FLOAT);
                                    if (forceResult != null && forceResult > 0) {
                                        final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                                        final double maxMana = ((double) PlayerScore.MAX_MANA.getScore(plugin, player));
                                        final double mult = artifactManager.hasArtifact(player, Artifact.BLOODSAPPING_BAYONET) ? 1.25 : 0.625;

                                        PlayerScore.MANA.setScore(plugin, player, Math.min(mana + (Math.pow(event.getFinalDamage(), 0.5) * mult * forceResult), maxMana));
                                        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
