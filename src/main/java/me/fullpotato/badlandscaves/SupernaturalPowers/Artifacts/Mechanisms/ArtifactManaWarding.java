package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ArtifactManaWarding extends ArtifactMechanisms implements Listener {
    public ArtifactManaWarding(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        super(plugin, voidmatter, artifactManager);
    }

    @EventHandler
    public void decreaseDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getFinalDamage() > 0) {
                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            for (ItemStack armor : equipment.getArmorContents()) {
                                if (armor != null && voidmatter.isVoidmatterArmor(armor)) {
                                    if (artifactManager.hasArtifact(player, Artifact.MANA_WARDING)) {
                                        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                                        final double cost = event.getFinalDamage() * 7.5;
                                        if (mana >= cost) {
                                            PlayerScore.MANA.setScore(plugin, player, mana - (cost));
                                            PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                                                    ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                                                    plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 3
                                            ));
                                            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                                            event.setDamage(event.getDamage() * 0.25);
                                        }
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
}
