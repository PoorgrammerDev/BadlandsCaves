package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.enhancedEyesNMS;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EyesRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private Location origin;
    private ArrayList<Integer> shulker_ids;
    private int clone_id;

    public EyesRunnable (BadlandsCaves bcav, Player ply, Location og, ArrayList<Integer> ids, int cln_id) {
        plugin = bcav;
        player = ply;
        shulker_ids = ids;
        origin = og;
        clone_id = cln_id;
    }


    @Override
    public void run() {
        enhancedEyesNMS nms = new enhancedEyesNMS(player);
        final int constant_mana_drain = plugin.getConfig().getInt("game_values.eyes_mana_drain");
        final int block_range = 7;
        final double dist_range = Math.pow(block_range - 1, 2);
        final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();
        double drain_per_tick = constant_mana_drain / 20.0;
        int mana = player.getMetadata("Mana").get(0).asInt();

        if (using_eyes && mana >= drain_per_tick) {
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

            nms.removeIndicator(clone_id);


            this.cancel();
        }

    }
}
