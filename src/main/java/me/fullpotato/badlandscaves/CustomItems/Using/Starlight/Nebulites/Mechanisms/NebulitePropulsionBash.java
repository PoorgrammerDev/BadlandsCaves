package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NebulitePropulsionBash extends NebuliteMechanisms implements Listener {
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 30), 1);
    public NebulitePropulsionBash(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void doubleShift (PlayerToggleSneakEvent event) {
        if (!plugin.getSystemConfig().getBoolean("hardmode")) return;

        final Player player = event.getPlayer();
        final boolean nopower = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0;
        if (!nopower) return;

        if (!((LivingEntity) player).isOnGround() || !player.isBlocking()) return;
        if ((byte) PlayerScore.PROPULSION_COOLDOWN.getScore(plugin, player) > 0) return;

        final ItemStack mainHand = player.getEquipment().getItemInMainHand();
        final ItemStack offHand = player.getEquipment().getItemInOffHand();

        ItemStack shield = null;
        if (starlightTools.isStarlightShield(mainHand)) shield = mainHand;
        else if (starlightTools.isStarlightShield(offHand)) shield = offHand;

        if (shield != null && starlightCharge.getCharge(shield) > 0) {
            final Nebulite[] nebulites = nebuliteManager.getNebulites(shield);

            for (Nebulite nebulite : nebulites) {
                if (nebulite != null && nebulite.equals(Nebulite.PROPULSION_BASH)) {
                    if (!player.isSneaking()) {
                        final boolean doubleshift_window = (PlayerScore.PROPULSION_DOUBLESHIFT_WINDOW.hasScore(plugin, player)) && ((byte) PlayerScore.PROPULSION_DOUBLESHIFT_WINDOW.getScore(plugin, player) == 1);
                        if (doubleshift_window) {
                            shieldBash(player, shield);
                        }
                        else {
                            PlayerScore.PROPULSION_DOUBLESHIFT_WINDOW.setScore(plugin, player, 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    PlayerScore.PROPULSION_DOUBLESHIFT_WINDOW.setScore(plugin, player, 0);
                                }
                            }.runTaskLaterAsynchronously(plugin, 10);
                        }
                    }
                    return;
                }
            }
        }
    }

    public void shieldBash (Player player, ItemStack shield) {
        starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) - 10);
        player.setVelocity(player.getEyeLocation().getDirection().multiply(3));

        final World world = player.getWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                double velocity = player.getVelocity().lengthSquared();

                if (velocity < 0.1) {
                    this.cancel();
                    return;
                }

                final Location front = player.getEyeLocation().add(player.getEyeLocation().getDirection());
                final Location behind = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-1));
                world.spawnParticle(Particle.REDSTONE, behind, 20, 0.5, 0.5, 0.5, dustOptions);
                PlayerScore.PROPULSION_COOLDOWN.setScore(plugin, player, 3);

                final double range = 1;
                final Collection<Entity> rawList = world.getNearbyEntities(front, range, range, range, entity -> (entity instanceof LivingEntity));
                final Set<LivingEntity> possibleTargets = new HashSet<>();

                for (final Entity entity : rawList) {
                    if (!entity.equals(player) && entity instanceof LivingEntity) {
                        possibleTargets.add((LivingEntity) entity);
                    }
                }

                if (!possibleTargets.isEmpty()) {
                    double minDist = Double.MAX_VALUE;
                    LivingEntity target = null;

                    for (final LivingEntity entity : possibleTargets) {
                        final double dist = entity.getLocation().distanceSquared(player.getLocation());
                        if (dist < minDist) {
                            minDist = dist;
                            target = entity;
                        }
                    }

                    if (target != null) {
                        starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) - 5);
                        player.setVelocity(player.getVelocity().multiply(-0.25));
                        target.damage(((velocity * -3) + 30), player);
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    public void cooldownMechanism () {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getSystemConfig().getBoolean("hardmode")) {
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        byte cooldown = (byte) PlayerScore.PROPULSION_COOLDOWN.getScore(plugin, player);
                        if (cooldown > 0) {
                            if (cooldown == 1) {
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1);
                            }

                            PlayerScore.PROPULSION_COOLDOWN.setScore(plugin, player, --cooldown);
                        }
                    });
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
