package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


public class enhancedEyes implements Listener {
    private BadlandsCaves plugin;

    public enhancedEyes (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) {
        Player player = event.getPlayer();

        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);

                    Location location = player.getLocation();
                    double x = location.getX();
                    double y = location.getY();
                    double z = location.getZ();

                    ArrayList<Material> keepVisible = new ArrayList<>();
                    keepVisible.add(Material.COAL_ORE);
                    keepVisible.add(Material.DIAMOND_ORE);
                    keepVisible.add(Material.EMERALD_ORE);
                    keepVisible.add(Material.GOLD_ORE);
                    keepVisible.add(Material.IRON_ORE);
                    keepVisible.add(Material.LAPIS_ORE);
                    keepVisible.add(Material.REDSTONE_ORE);
                    keepVisible.add(Material.NETHER_QUARTZ_ORE);
                    keepVisible.add(Material.CHEST);
                    keepVisible.add(Material.BARREL);
                    keepVisible.add(Material.SHULKER_BOX);
                    keepVisible.add(Material.BLACK_SHULKER_BOX);
                    keepVisible.add(Material.BLUE_SHULKER_BOX);
                    keepVisible.add(Material.BROWN_SHULKER_BOX);
                    keepVisible.add(Material.CYAN_SHULKER_BOX);
                    keepVisible.add(Material.GRAY_SHULKER_BOX);
                    keepVisible.add(Material.GREEN_SHULKER_BOX);
                    keepVisible.add(Material.LIGHT_BLUE_SHULKER_BOX);
                    keepVisible.add(Material.LIGHT_GRAY_SHULKER_BOX);
                    keepVisible.add(Material.LIME_SHULKER_BOX);
                    keepVisible.add(Material.MAGENTA_SHULKER_BOX);
                    keepVisible.add(Material.ORANGE_SHULKER_BOX);
                    keepVisible.add(Material.PINK_SHULKER_BOX);
                    keepVisible.add(Material.PURPLE_SHULKER_BOX);
                    keepVisible.add(Material.YELLOW_SHULKER_BOX);
                    keepVisible.add(Material.RED_SHULKER_BOX);
                    keepVisible.add(Material.WHITE_SHULKER_BOX);
                    keepVisible.add(Material.SPAWNER);
                    keepVisible.add(Material.COAL_BLOCK);
                    keepVisible.add(Material.DIAMOND_BLOCK);
                    keepVisible.add(Material.EMERALD_BLOCK);
                    keepVisible.add(Material.GOLD_BLOCK);
                    keepVisible.add(Material.IRON_BLOCK);
                    keepVisible.add(Material.LAPIS_BLOCK);
                    keepVisible.add(Material.REDSTONE_BLOCK);


                    for (int x_offset = -5; x_offset <= 5; x_offset++) {
                        for (int y_offset = -5; y_offset <= 5; y_offset++) {
                            for (int z_offset = -5; z_offset <= 5; z_offset++) {
                                Location blockReplaceLoc = new Location(location.getWorld(), x + x_offset, y + y_offset, z + z_offset);

                                if (location.distanceSquared(blockReplaceLoc) < 16) {
                                    Block block = blockReplaceLoc.getBlock();
                                    Material blockMat = block.getType();

                                    BlockData data = Bukkit.createBlockData(Material.BARRIER);
                                    if (blockMat.isSolid()) {
                                        if (!keepVisible.contains(blockMat)) {
                                            player.sendBlockChange(blockReplaceLoc, data);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
