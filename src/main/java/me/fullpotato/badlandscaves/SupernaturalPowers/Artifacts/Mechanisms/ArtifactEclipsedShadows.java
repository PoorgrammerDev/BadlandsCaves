package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.EclipsedShadows.EclipsedShadowsNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ArtifactEclipsedShadows extends ArtifactMechanisms implements Listener {
    private final Random random = new Random();
    private final EclipsedShadowsNMS nms;
    public ArtifactEclipsedShadows(BadlandsCaves plugin) {
        super(plugin);
        nms = plugin.getEclipsedShadowsNMS();
    }

    @EventHandler
    public void activateSneak (PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (player.isSneaking()) {
                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    disable(player);
                }
            }
            else {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            for (ItemStack armor : equipment.getArmorContents()) {
                                if (voidmatter.isVoidmatterArmor(armor)) {
                                    if (artifactManager.hasArtifact(player, Artifact.ECLIPSED_SHADOWS)) {
                                        enable(player);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventTarget (EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            final Player player = (Player) event.getTarget();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    int chance = 100;

                    final double distance = player.getLocation().distanceSquared(event.getEntity().getLocation());
                    if (distance <= 0) chance = 0;
                    else {
                        chance -= (25.0 / distance);

                        final ItemStack main = player.getInventory().getItemInMainHand();
                        if (!main.getType().equals(Material.AIR)) chance /= 2;

                        final ItemStack off = player.getInventory().getItemInOffHand();
                        if (!off.getType().equals(Material.AIR)) chance /= 2;
                    }

                    if (chance > 0 && random.nextInt(100) < chance) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void damageCancel(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    disable(player);
                }
            }
        }
    }

    @EventHandler
    public void dieCancel (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                disable(player);
            }
        }
    }

    @EventHandler
    public void leaveCancel (PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                disable(player);
            }
        }
    }


    public void enable (Player player) {
        PlayerScore.ECLIPSED_SHADOWS_ACTIVE.setScore(plugin, player, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 999999, 10, false, false));
        nms.setArmorVisible(player, false);
    }

    public void disable (Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        nms.setArmorVisible(player, true);
        PlayerScore.ECLIPSED_SHADOWS_ACTIVE.setScore(plugin, player, 0);
    }
}
