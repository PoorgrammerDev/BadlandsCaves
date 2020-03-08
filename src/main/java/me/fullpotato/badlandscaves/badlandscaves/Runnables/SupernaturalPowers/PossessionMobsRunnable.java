package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.PossessionNMS;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class PossessionMobsRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private LivingEntity target;
    private Team team;

    public PossessionMobsRunnable(BadlandsCaves bcav, Player ply, LivingEntity trg, Team tm) {
        plugin = bcav;
        player = ply;
        target = trg;
        team = tm;
    }

    @Override
    public void run() {
        final boolean in_possession = player.getMetadata("in_possession").get(0).asBoolean();
        double mana = player.getMetadata("Mana").get(0).asDouble();
        final int possession_mana_drain = plugin.getConfig().getInt("game_values.possess_mana_drain");
        final double possession_mana_drain_tick = possession_mana_drain / 20.0;
        PossessionNMS nms = new PossessionNMS(player);

        if (in_possession && mana > possession_mana_drain_tick) {
            if (target.isDead() || player.isDead()) {
                player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
            }

            for (Player online_player : Bukkit.getOnlinePlayers()) {
                if (!online_player.equals(player)) {
                    online_player.hidePlayer(plugin, player);
                }
            }


            //cancel mana regen and keep mana bar active
            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));

            //make target invis to player
            nms.markTarget(target);

            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0));
            target.teleport(player.getLocation());

            player.setMetadata("Mana", new FixedMetadataValue(plugin, mana - possession_mana_drain_tick));
        }
        else {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
            //set target and player back to normal
            team.unregister();

            nms.unmarkTarget(target);
            target.setMetadata("possessed", new FixedMetadataValue(plugin, false));
            player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
            target.setAI(true);
            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);

            for (Player online_player : Bukkit.getOnlinePlayers()) {
                if (!online_player.equals(player)) {
                    online_player.showPlayer(plugin, player);
                }
            }

            //teleport player back to orig location
            if (player.hasMetadata("possess_orig_world") && !player.getMetadata("possess_orig_world").get(0).asString().equalsIgnoreCase("__REMOVED__")) {
                int possess_level = player.getMetadata("possess_level").get(0).asInt();
                float current_yaw = player.getLocation().getYaw();
                float current_pitch = player.getLocation().getPitch();

                //LEVEL 2: WARP TO DISPLACE------------
                boolean has_displace_marker = player.getMetadata("has_displace_marker").get(0).asInt() == 1;
                int displace_cost = plugin.getConfig().getInt("game_values.displace_mana_cost");
                if (possess_level > 1 && has_displace_marker && mana >= displace_cost) {
                    int displace_level = player.getMetadata("displace_level").get(0).asInt();
                    double disp_x = player.getMetadata("displace_x").get(0).asDouble();
                    double disp_y = player.getMetadata("displace_y").get(0).asDouble();
                    double disp_z = player.getMetadata("displace_z").get(0).asDouble();
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
                        player.teleport(displace_marker);

                        //mana
                        player.setMetadata("Mana", new FixedMetadataValue(plugin, mana - displace_cost));
                        player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
                        player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                        return;
                    }
                }
                //WARP TO ORIG---------------
                World orig_world = Bukkit.getWorld(player.getMetadata("possess_orig_world").get(0).asString());
                double orig_x = player.getMetadata("possess_orig_x").get(0).asDouble();
                double orig_y = player.getMetadata("possess_orig_y").get(0).asDouble();
                double orig_z = player.getMetadata("possess_orig_z").get(0).asDouble();
                Location orig_loc = new Location(orig_world, orig_x, orig_y, orig_z, current_yaw, current_pitch);
                player.teleport(orig_loc);
            }
        }
    }
}
