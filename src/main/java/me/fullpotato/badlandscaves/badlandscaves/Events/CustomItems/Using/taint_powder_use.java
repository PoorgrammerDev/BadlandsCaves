package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.tainted_powder_runnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.tpowd_run_check_vel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class taint_powder_use implements Listener {
    private BadlandsCaves plugin;
    public taint_powder_use (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void taint_powder_use (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {

                ItemStack tainted_powder = new ItemStack(Material.COMMAND_BLOCK, 2);
                ItemMeta powder_meta = tainted_powder.getItemMeta();
                powder_meta.setDisplayName(ChatColor.DARK_GREEN + "Tainted Powder");
                ArrayList<String> powder_lore = new ArrayList<>();
                powder_lore.add(ChatColor.GREEN + "Right click to use.");
                powder_lore.add("");
                powder_lore.add(ChatColor.GRAY + "Formulated to cripple silverfish's nervous systems.");
                powder_lore.add(ChatColor.GRAY + "Works best when they are sleeping, in their blocks.");
                powder_lore.add(ChatColor.GRAY + "Also works on other living organisms, but not as well.");
                powder_meta.setLore(powder_lore);
                tainted_powder.setItemMeta(powder_meta);

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
                            BukkitTask check_vel = new tpowd_run_check_vel(plugin, player, drop, init_x_vel, init_y_vel, init_z_vel).runTaskTimerAsynchronously(plugin, 0, 2);
                        }
                    }.runTaskLater(plugin, 3);
                }
            }
        }
    }
}
