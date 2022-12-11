package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables.TravellingBladesProjectile;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ArtifactTravellingBlades extends ArtifactMechanisms implements Listener {
    private final int cost;
    private final Random random;
    public ArtifactTravellingBlades(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, Random random) {
        super(plugin, voidmatter, artifactManager);
        cost = plugin.getOptionsConfig().getInt("hardmode_values.artifact_costs.travelling_blades");
        this.random = random;
    }

    @EventHandler
    public void hit (PlayerInteractEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Action action = event.getAction();
            if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {

                final Player player = event.getPlayer();
                if (player.getAttackCooldown() >= 1) {
                    final ItemStack item = event.getItem();
                    if (item != null && voidmatter.isVoidmatterBlade(item) && artifactManager.hasArtifact(player, Artifact.TRAVELING_BLADES)) {
                        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                        if (mana >= cost) {
                            spawnTravellingBlades(player, 10, player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() / 2.0);
                        }
                    }
                }
            }
        }
    }

    public void spawnTravellingBlades (Player player, int count, double damage) {
        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        PlayerScore.MANA.setScore(plugin, player, mana - cost);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(
                ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)),
                plugin.getOptionsConfig().getInt("mana_regen_cooldown") / 3
        ));

        new TravellingBladesProjectile(plugin, random, player, count, damage, null).runTaskTimer(plugin, 0, 0);
    }
}
