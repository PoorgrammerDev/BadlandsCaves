package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtifactHasteWind extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final Voidmatter voidmatter;
    private final ArtifactManager artifactManager;

    public ArtifactHasteWind(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        this.plugin = plugin;
        this.voidmatter = voidmatter;
        this.artifactManager = artifactManager;
    }

    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final ItemStack mainhand = player.getInventory().getItemInMainHand();
                    final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                    if (attribute != null) {
                        attribute.setBaseValue(voidmatter.isVoidmatterBlade(mainhand) && artifactManager.hasArtifact(player, Artifact.HASTE_WIND) ? 1000001.4 : attribute.getDefaultValue());
                    }
                }
            });
        }
    }

    @EventHandler
    public void hitEntity (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                final ItemStack mainhand = player.getInventory().getItemInMainHand();
                if (voidmatter.isVoidmatterBlade(mainhand) && artifactManager.hasArtifact(player, Artifact.HASTE_WIND)) {
                    if (event.getEntity() instanceof LivingEntity) {
                        final LivingEntity entity = (LivingEntity) event.getEntity();
                        entity.setVelocity(player.getLocation().getDirection().normalize().multiply(1.1).setY(0.2));
                        entity.setNoDamageTicks(0);
                    }
                }
            }
        }
    }
}
