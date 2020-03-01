package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EyesRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private Location origin;
    private ArrayList<Integer> shulker_ids;

    public EyesRunnable (BadlandsCaves bcav, Player ply, Location og, ArrayList<Integer> ids) {
        plugin = bcav;
        player = ply;
        shulker_ids = ids;
        origin = og;
    }


    @Override
    public void run() {
        EnhancedEyesNMS nms = new EnhancedEyesNMS(player);
        final int eyes_level = player.hasMetadata("eyes_level") ? player.getMetadata("eyes_level").get(0).asInt() : 0;
        final int constant_mana_drain = plugin.getConfig().getInt("game_values.eyes_mana_drain");
        final int block_range = (eyes_level >= 2) ? 15 : 7;
        final double dist_range = Math.pow(block_range - 1, 2);
        final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();
        double drain_per_tick = constant_mana_drain / 20.0;
        int mana = player.getMetadata("Mana").get(0).asInt();

        if (using_eyes && mana >= drain_per_tick) {
            //night vision
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, 10, 0, true, false), true);

            //highlights living entities
            for (Entity entity : origin.getWorld().getNearbyEntities(origin, block_range, block_range, block_range)) {
                if (entity instanceof LivingEntity) {
                    if (origin.distanceSquared(entity.getLocation()) < dist_range) {
                        nms.highlightEntity(entity);
                    }
                }
            }

            //mana stuffs
            mana -= drain_per_tick;
            player.setMetadata("Mana", new FixedMetadataValue(plugin, mana));
            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
        }
        else {
            player.setMetadata("using_eyes", new FixedMetadataValue(plugin, false));

            //removing indicators
            for (int id : shulker_ids) {
                nms.removeIndicator(id);
            }

            this.cancel();
        }

    }
}
