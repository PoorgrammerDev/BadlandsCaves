package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtifactSightStealing extends ArtifactMechanisms implements Listener {
    private final NamespacedKey key;
    private final EnhancedEyesNMS nms;

    public ArtifactSightStealing(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
        this.key = new NamespacedKey(plugin, "sight_stealing");
        this.nms = plugin.getEnhancedEyesNMS();
    }

    @EventHandler
    public void arrowShoot(EntityShootBowEvent event) {
        if (event.getForce() >= 1) {
            if (event.getProjectile() instanceof AbstractArrow && event.getEntity() instanceof Player) {
                final AbstractArrow arrow = (AbstractArrow) event.getProjectile();
                final Player player = (Player) event.getEntity();
                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        final ItemStack bow = event.getBow();
                        if (bow != null && voidmatter.isVoidmatterBow(bow)) {
                            if (artifactManager.hasArtifact(player, Artifact.SIGHT_STEALING)) {
                                arrow.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void arrowHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof AbstractArrow && event.getHitEntity() instanceof LivingEntity) {
            final AbstractArrow arrow = (AbstractArrow) event.getEntity();
            final LivingEntity target = (LivingEntity) event.getHitEntity();

            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player) arrow.getShooter();
                if (arrow.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    final Byte result = arrow.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                    if (result != null && result == (byte) 1) {
                        final int duration = 100;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false));

                        final int[] elapsed = {0};

                        Player targetPlayer = null;
                        if (target instanceof Player) {
                            targetPlayer = (Player) target;
                            targetPlayer.hidePlayer(plugin, player);
                        }

                        final Player targetPlayerFinal = targetPlayer;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (elapsed[0] > duration) {
                                    this.cancel();
                                    if (targetPlayerFinal != null) targetPlayerFinal.showPlayer(plugin, player);
                                    return;
                                }

                                nms.highlightEntity(player, target, ChatColor.BLUE);
                                elapsed[0]++;
                            }
                        }.runTaskTimer(plugin, 0, 0);
                    }
                }
            }
        }
    }
}