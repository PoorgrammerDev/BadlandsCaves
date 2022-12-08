package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Domino;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;

public class DominoParticleRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Domino domino;
    private final TargetEntity targetManager;
    private final PossessionNMS outlineManager;
    private final ItemStack dominoItem;

    public DominoParticleRunnable(BadlandsCaves plugin, Domino domino) {
        this.plugin = plugin;
        this.domino = domino;
        this.targetManager = new TargetEntity();
        this.outlineManager = plugin.getPossessionNMS();
        this.dominoItem = plugin.getCustomItemManager().getItem(CustomItem.DOMINO);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            //must be magic class
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 0) continue;

            //if already using domino -> show the links
            domino.ShowVisuals(player);

            //max capacity -> do not show the indicator
            if (!domino.HasCapacity(player.getUniqueId())) continue;

            final ItemStack item = player.getEquipment().getItemInOffHand();
            if (item != null && item.isSimilar(this.dominoItem)) {
                //show indicator for new target entities
                for (LivingEntity target : this.targetManager.findTargetLivingEntities(player.getEyeLocation(), 15, 0.2, 3, false, player)) {
                    this.outlineManager.setIndicator(player, target);
                    player.spawnParticle(Particle.REDSTONE, target.getEyeLocation(), 2, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 196, 0), 2F));
                }
            }


        }
    }
    
}
