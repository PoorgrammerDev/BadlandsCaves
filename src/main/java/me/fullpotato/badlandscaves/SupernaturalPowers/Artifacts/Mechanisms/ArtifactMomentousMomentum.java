package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ArtifactMomentousMomentum extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    public ArtifactMomentousMomentum(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void damage (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            final Player player = (Player) event.getDamager();
            final LivingEntity entity = (LivingEntity) event.getEntity();
            if (player.getAttackCooldown() >= 1 && plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final int timer = (int) PlayerScore.MOMENTOUS_MOMENTUM_TIMER.getScore(plugin, player);
                    if (timer > 0) {
                        PlayerScore.MOMENTOUS_MOMENTUM_TIMER.setScore(plugin, player, 0);
                        final int mult = random.nextInt(timer) + 1;

                        if (mult > 1) {
                            event.setDamage(event.getDamage() * mult * 5);
                            entity.setVelocity(player.getLocation().getDirection().normalize().multiply(mult * 1.5).setY(mult / 2.0));
                            player.getWorld().playSound(entity.getLocation(), "custom.vampire.ability", SoundCategory.PLAYERS, mult, 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    final int timer = (int) PlayerScore.MOMENTOUS_MOMENTUM_TIMER.getScore(plugin, player);
                    if (timer > 0) {
                        PlayerScore.MOMENTOUS_MOMENTUM_TIMER.setScore(plugin, player, timer - 1);
                    }
                }
            });
        }
    }
}
