package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class Withdraw implements Listener {
    private BadlandsCaves plugin;

    public Withdraw(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_displace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    //TODO prevent the player from falling off the edge
                    //TODO prevent the player from activating Withdraw while being inside of the pocket dimension (this causes issues)
                    //TODO set the player to adventure mode while in the pocket dimension
                    event.setCancelled(true);
                    Random random = new Random();
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 256; y++) {
                            for (int z = 0; z < 16; z++) {
                                Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                Location block_loc = block.getLocation();
                                block_loc.setWorld(Bukkit.getWorld("world_empty"));
                                if (block.getType().isSolid()) {
                                    int rand = random.nextInt(2);
                                    if (rand == 0) block_loc.getBlock().setType(Material.COAL_BLOCK);
                                    else if (rand == 1) block_loc.getBlock().setType(Material.BLACK_CONCRETE);
                                    else block_loc.getBlock().setType(Material.BLACK_WOOL);
                                }
                                else {
                                    block_loc.getBlock().setType(Material.AIR);
                                }
                            }
                        }
                    }

                    Location location = player.getLocation();
                    Location voidloc = player.getLocation();
                    voidloc.setWorld(Bukkit.getWorld("world_empty"));
                    player.teleport(voidloc);

                    BukkitTask back = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(location);
                        }
                    }.runTaskLater(plugin, 600);

                }
            }
        }
    }
}
