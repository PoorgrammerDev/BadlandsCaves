package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class EyesRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Player player;
    private final Location origin;
    private final ArrayList<Integer> shulkerIDs;
    private final Collection<Entity> entities;
    private final boolean hasNightVision;
    private final EnhancedEyesNMS nms;
    private final int drain;

    public EyesRunnable(BadlandsCaves plugin, Player player, Location origin, ArrayList<Integer> ids, @Nullable Collection<Entity> entities) {
        this.plugin = plugin;
        this.player = player;
        this.shulkerIDs = ids;
        this.entities = entities;
        this.origin = origin;
        this.hasNightVision = player.hasPotionEffect(PotionEffectType.NIGHT_VISION);
        this.nms = plugin.getEnhancedEyesNMS();
        this.drain = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_drain");
    }


    @Override
    public void run() {
        final int eyes_level = (PlayerScore.EYES_LEVEL.hasScore(plugin, player)) ? (int) PlayerScore.EYES_LEVEL.getScore(plugin, player) : 0;
        final int block_range = (eyes_level >= 2) ? 15 : 7;
        final double dist_range = Math.pow(block_range - 1, 2);
        final boolean using_eyes = ((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1);
        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));

        if (using_eyes && mana >= (drain / 20.0)) {
            //night vision
            if (!hasNightVision) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, true, false));
            }

            //highlights living entities
            if (entities != null) {
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        if (origin.distanceSquared(entity.getLocation()) < dist_range) {
                            if (eyes_level > 1) {
                                if (entity instanceof Player) {
                                    nms.highlightEntity(player, entity, ChatColor.GOLD);
                                }
                                else if (entity instanceof Monster) {
                                    nms.highlightEntity(player, entity, ChatColor.RED);
                                }
                                else if (entity instanceof Animals) {
                                    nms.highlightEntity(player, entity, ChatColor.GREEN);
                                }
                                else {
                                    nms.highlightEntity(player, entity, ChatColor.GRAY);
                                }
                            }
                            else {
                                nms.highlightEntity(player, entity);
                            }
                        }
                    }
                }
            }

            //mana stuffs
            mana -= (drain / 20.0);
            PlayerScore.MANA.setScore(plugin, player, mana);
            PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        }
        else {
            PlayerScore.USING_EYES.setScore(plugin, player, 0);
            player.stopSound("custom.supernatural.enhanced_eyes.ambience");
            player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.end", SoundCategory.PLAYERS, 0.5F, 1);

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.end", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 0.05, 0.05, 0.05, 0, new Particle.DustOptions(Color.BLUE, 1));
                    }
                }
            }

            //removing indicators
            for (int id : shulkerIDs) {
                nms.removeIndicator(player, id);
            }

            if (!hasNightVision) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            this.cancel();
        }

    }
}
