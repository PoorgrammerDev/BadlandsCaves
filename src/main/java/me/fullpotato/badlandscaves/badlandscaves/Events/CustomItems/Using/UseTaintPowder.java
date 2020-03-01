package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.TaintedPowderVelCheck;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class UseTaintPowder implements Listener {
    private BadlandsCaves plugin;
    public UseTaintPowder(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void taint_powder_use (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {

                ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));

                if (item.isSimilar(tainted_powder)) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    Location loc = player.getLocation();
                    loc.setDirection(loc.getDirection());
                    loc.setY(loc.getY() + 1);

                    ItemStack powder = item.clone();
                    powder.setAmount(1);
                    Item drop = loc.getWorld().dropItemNaturally(loc, powder);

                    item.setAmount(item.getAmount() - 1);
                    drop.setInvulnerable(true);
                    drop.setPickupDelay(Integer.MAX_VALUE);
                    drop.setVelocity(loc.getDirection().multiply(1.2));
                    drop.setMetadata("time", new FixedMetadataValue(plugin, 0));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            double init_x_vel = drop.getVelocity().getX();
                            double init_y_vel = drop.getVelocity().getY();
                            double init_z_vel = drop.getVelocity().getZ();
                            BukkitTask check_vel = new TaintedPowderVelCheck(plugin, player, drop, init_x_vel, init_y_vel, init_z_vel).runTaskTimerAsynchronously(plugin, 0, 2);
                        }
                    }.runTaskLater(plugin, 3);
                }
            }
        }
    }
}
