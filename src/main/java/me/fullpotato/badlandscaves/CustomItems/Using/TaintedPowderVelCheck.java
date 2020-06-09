package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class TaintedPowderVelCheck extends BukkitRunnable {

    private final BadlandsCaves plugin;
    private final Player player;
    private final Item powder;
    private final double initial_x_vel;
    private final double initial_y_vel;
    private final double initial_z_vel;

    public TaintedPowderVelCheck(BadlandsCaves bcav, Player ply, Item powd, double init_x, double init_y, double init_z) {
        plugin = bcav;
        player = ply;
        powder = powd;
        initial_x_vel = init_x;
        initial_y_vel = init_y;
        initial_z_vel = init_z;
    }

    @Override
    public void run() {
        double vel_x = powder.getVelocity().getX();
        double vel_y = powder.getVelocity().getY();
        double vel_z = powder.getVelocity().getZ();

        boolean x_stopped = vel_x == 0 && vel_x != initial_x_vel;
        boolean y_stopped = vel_y == 0 && vel_y != initial_y_vel;
        boolean z_stopped = vel_z == 0 && vel_z != initial_z_vel;
        boolean ready_to_run = false;


        if (x_stopped || y_stopped || z_stopped) {
            ready_to_run = true;
        }
        else {
            NamespacedKey key = new NamespacedKey(plugin, "time");
            short powder_time = powder.getPersistentDataContainer().has(key, PersistentDataType.SHORT) ? powder.getPersistentDataContainer().get(key, PersistentDataType.SHORT) : (short) 0;
            if (powder_time > 15) {
                ready_to_run = true;
            }
            else {
                powder.getPersistentDataContainer().set(key, PersistentDataType.SHORT, (short) (powder_time + 1));
            }
        }

        if (ready_to_run) {
            new TaintedPowderRunnable(plugin, powder, player, this.getTaskId()).runTask(plugin);
        }
    }
}
