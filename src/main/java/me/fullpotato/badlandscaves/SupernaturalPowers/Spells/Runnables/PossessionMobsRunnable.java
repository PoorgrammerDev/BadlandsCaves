package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class PossessionMobsRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Player player;
    private final LivingEntity target;
    private final Team team;

    public PossessionMobsRunnable(BadlandsCaves bcav, Player ply, LivingEntity trg, Team tm) {
        plugin = bcav;
        player = ply;
        target = trg;
        team = tm;
    }

    @Override
    public void run() {
        final boolean in_possession = (byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1;
        double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        final int possession_mana_drain = plugin.getConfig().getInt("options.spell_costs.possess_mana_drain");
        final double possession_mana_drain_tick = possession_mana_drain / 20.0;
        PossessionNMS nms = plugin.possessionNMS;

        if (in_possession && mana > possession_mana_drain_tick) {
            if (target.isDead() || player.isDead()) {
                PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
            }

            for (Player online_player : plugin.getServer().getOnlinePlayers()) {
                if (!online_player.equals(player)) {
                    online_player.hidePlayer(plugin, player);
                }
            }


            //cancel mana regen and keep mana bar active
            PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 15);
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);

            //make target invis to player
            nms.markTarget(player, target);

            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0));
            target.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

            PlayerScore.MANA.setScore(plugin, player, mana - possession_mana_drain_tick);
        }
        else {
            plugin.getServer().getScheduler().cancelTask(this.getTaskId());
            //set target and player back to normal
            team.unregister();

            nms.unmarkTarget(player, target);
            target.setMetadata("possessed", new FixedMetadataValue(plugin, false));
            PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
            target.setAI(true);


            final World descension = plugin.getServer().getWorld(plugin.descensionWorldName);
            if (player.getGameMode().equals(GameMode.ADVENTURE) && !player.getWorld().equals(descension)) player.setGameMode(GameMode.SURVIVAL);

            for (Player online_player : plugin.getServer().getOnlinePlayers()) {
                if (!online_player.equals(player)) {
                    online_player.showPlayer(plugin, player);
                }
            }

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), "custom.supernatural.possession.leave", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
                    }
                }
            }

            //teleport player back to orig location
            if ((PlayerScore.POSSESS_ORIG_WORLD.hasScore(plugin, player)) && !((String) PlayerScore.POSSESS_ORIG_WORLD.getScore(plugin, player)).equalsIgnoreCase("__REMOVED__")) {
                int possess_level = (int) PlayerScore.POSSESS_LEVEL.getScore(plugin, player);
                float current_yaw = player.getLocation().getYaw();
                float current_pitch = player.getLocation().getPitch();

                //LEVEL 2: WARP TO DISPLACE------------ (no mana cost)
                boolean has_displace_marker = (byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 1;
                if (possess_level > 1 && has_displace_marker) {
                    int displace_level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
                    double disp_x = (double) PlayerScore.DISPLACE_X.getScore(plugin, player);
                    double disp_y = (double) PlayerScore.DISPLACE_Y.getScore(plugin, player);
                    double disp_z = (double) PlayerScore.DISPLACE_Z.getScore(plugin, player);
                    Location displace_marker = new Location(player.getWorld(), disp_x, disp_y, disp_z, current_yaw, current_pitch);

                    int warp_range;
                    boolean cancel_fall;

                    if (displace_level == 1) {
                        warp_range = 15;
                        cancel_fall = false;
                    }
                    else {
                        warp_range = 30;
                        cancel_fall = true;
                    }

                    if (player.getLocation().distance(displace_marker) <= warp_range) {
                        if (cancel_fall) player.setFallDistance(0);
                        PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
                        player.teleport(displace_marker, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        player.playSound(player.getLocation(), "custom.supernatural.possession.leave", SoundCategory.PLAYERS, 0.5F, 1);
                        player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);
                        return;
                    }
                }
                //WARP TO ORIG---------------
                World orig_world = plugin.getServer().getWorld((String) PlayerScore.POSSESS_ORIG_WORLD.getScore(plugin, player));
                double orig_x = (double) PlayerScore.POSSESS_ORIG_X.getScore(plugin, player);
                double orig_y = (double) PlayerScore.POSSESS_ORIG_Y.getScore(plugin, player);
                double orig_z = (double) PlayerScore.POSSESS_ORIG_Z.getScore(plugin, player);
                Location orig_loc = new Location(orig_world, orig_x, orig_y, orig_z, current_yaw, current_pitch);
                player.teleport(orig_loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.playSound(player.getLocation(), "custom.supernatural.possession.leave", SoundCategory.PLAYERS, 0.5F, 1);
            }
        }
    }
}
