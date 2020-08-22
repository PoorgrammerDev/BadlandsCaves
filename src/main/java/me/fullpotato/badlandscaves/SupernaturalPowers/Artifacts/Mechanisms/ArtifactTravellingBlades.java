package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final Location front = player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection().normalize());

        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        PlayerScore.MANA.setScore(plugin, player, mana - cost);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, Math.max(((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player)), 30));

        final int[] spawned = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spawned[0] > count) {
                    this.cancel();
                    return;
                }
                final Location[] scout = {front.clone().add(random.nextDouble() * 2, random.nextDouble() / 5, random.nextDouble() * 2)};
                final int times = 25;
                final int[] ran = {0};
                final double range = 1.5;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (ran[0] > times || !scout[0].getBlock().isPassable()) {
                            this.cancel();
                            return;
                        }

                        scout[0] = scout[0].add(location.getDirection().normalize());
                        world.spawnParticle(Particle.SWEEP_ATTACK, scout[0], 1);
                        world.playSound(scout[0], Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 2);

                        world.getNearbyEntities(scout[0], range, range, range).forEach(target -> {
                            if (target instanceof EnderDragon) {
                                EnderDragon enderDragon = (EnderDragon) target;
                                this.cancel();
                                enderDragon.setHealth(enderDragon.getHealth() - (damage / 5.0));
                                enderDragon.setNoDamageTicks(0);
                                enderDragon.playEffect(EntityEffect.HURT);
                                world.playSound(enderDragon.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, SoundCategory.HOSTILE, 10, 1);
                            }
                            else if (target instanceof LivingEntity) {
                                final LivingEntity livingTarget = (LivingEntity) target;
                                if (!livingTarget.equals(player)) {
                                    this.cancel();
                                    livingTarget.damage(damage, player);
                                    livingTarget.setNoDamageTicks(0);
                                }
                            }
                        });
                        ran[0]++;
                    }
                }.runTaskTimer(plugin, 0, 0);
                spawned[0]++;
            }
        }.runTaskTimer(plugin, 0, 0);
    }
}
