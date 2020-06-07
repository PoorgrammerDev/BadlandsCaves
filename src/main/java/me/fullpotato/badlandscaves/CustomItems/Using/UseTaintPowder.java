package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

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

                ItemStack tainted_powder = CustomItem.TAINTED_POWDER.getItem();

                if (item.isSimilar(tainted_powder)) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    Location loc = player.getEyeLocation();
                    player.playSound(loc, Sound.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.5F, 0);

                    ItemStack powder = item.clone();
                    powder.setAmount(1);
                    Item drop = loc.getWorld().dropItem(loc, powder);


                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        item.setAmount(item.getAmount() - 1);
                    }
                    drop.setInvulnerable(true);
                    drop.setPickupDelay(Integer.MAX_VALUE);
                    drop.setVelocity(loc.getDirection().multiply(1.1));
                    drop.setMetadata("time", new FixedMetadataValue(plugin, 0));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            double init_x_vel = drop.getVelocity().getX();
                            double init_y_vel = drop.getVelocity().getY();
                            double init_z_vel = drop.getVelocity().getZ();
                            new TaintedPowderVelCheck(plugin, player, drop, init_x_vel, init_y_vel, init_z_vel).runTaskTimerAsynchronously(plugin, 0, 2);
                        }
                    }.runTaskLater(plugin, 3);
                }
            }
        }
    }
}
