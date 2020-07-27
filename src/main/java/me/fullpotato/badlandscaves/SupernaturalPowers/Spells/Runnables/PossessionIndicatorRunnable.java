package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PossessionIndicatorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final TargetEntity targetEntity = new TargetEntity();

    public PossessionIndicatorRunnable(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (has_powers) {
                if (player.getInventory().getItemInOffHand().isSimilar(CustomItem.POSSESS.getItem())) {
                    if (((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) != 1)
                            && ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0)) {
                        final LivingEntity target = targetEntity.findTargetLivingEntity(player.getEyeLocation(), 15, 0.2, 0.2, false, player);
                        if (target != null && !(target instanceof Player) && !(target instanceof EnderDragon) && !(target instanceof Wither) &&
                                !(target.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) &&
                                        target.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1)) {
                            if (player.hasLineOfSight(target)) {
                                boolean target_already_pos = target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean();
                                if (!target_already_pos) {
                                    PossessionNMS nms = plugin.getPossessionNMS();
                                    nms.setIndicator(player, target);

                                    Location location = target.getLocation();
                                    makeParticleLine(player, location, location.getY() + target.getHeight());

                                    location = target.getLocation();
                                    makeParticleCircle(player, location, target.getWidth() * 2);

                                    location.add(0, target.getHeight(), 0);
                                    makeParticleCircle(player, location, target.getWidth() * 2);
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    public void makeParticleCircle (Player player, Location location, double radius) {
        for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI / 10) {
            double x = (radius / 2) * Math.cos(theta);
            double z = (radius / 2) * Math.sin(theta);

            location.add(x, 0, z);
            spawnParticle(player, location);
            location.subtract(x, 0, z);
        }
    }

    public void spawnParticle(Player player, Location location) {
        Random random = new Random();
        int rand_r = random.nextInt(76) + 6;
        int rand_g = random.nextInt(11) + 104;
        int rand_b = random.nextInt(115);

        player.spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.fromRGB(rand_r, rand_g, rand_b), 1));
    }

    public void makeParticleLine (Player player, Location location, double y_top) {
        if (location.getY() <= y_top) {
            for (double y = location.getY(); y < y_top; y += 0.1) {
                location.setY(y);
                spawnParticle(player, location);
            }
        }
    }
}
