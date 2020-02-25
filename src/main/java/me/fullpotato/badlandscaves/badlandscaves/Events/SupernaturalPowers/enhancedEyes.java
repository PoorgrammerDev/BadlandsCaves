package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.enhancedEyesNMS;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.EyesRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


public class enhancedEyes implements Listener {
    private BadlandsCaves plugin;
    public enhancedEyes (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) {
        Player player = event.getPlayer();

        final int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();

                    if (using_eyes) {
                        player.setMetadata("using_eyes", new FixedMetadataValue(plugin, false));
                    }
                    else {
                        final int initial_mana_cost = plugin.getConfig().getInt("game_values.eyes_mana_cost");
                        final int constant_mana_drain = plugin.getConfig().getInt("game_values.eyes_mana_drain");
                        int mana = player.getMetadata("Mana").get(0).asInt();

                        if (mana >= initial_mana_cost) {

                            ArrayList<Material> important_blocks = new ArrayList<>();
                            important_blocks.add(Material.COAL_ORE);
                            important_blocks.add(Material.DIAMOND_ORE);
                            important_blocks.add(Material.EMERALD_ORE);
                            important_blocks.add(Material.GOLD_ORE);
                            important_blocks.add(Material.IRON_ORE);
                            important_blocks.add(Material.LAPIS_ORE);
                            important_blocks.add(Material.REDSTONE_ORE);
                            important_blocks.add(Material.NETHER_QUARTZ_ORE);
                            important_blocks.add(Material.CHEST);
                            important_blocks.add(Material.BARREL);
                            important_blocks.add(Material.SHULKER_BOX);
                            important_blocks.add(Material.BLACK_SHULKER_BOX);
                            important_blocks.add(Material.BLUE_SHULKER_BOX);
                            important_blocks.add(Material.BROWN_SHULKER_BOX);
                            important_blocks.add(Material.CYAN_SHULKER_BOX);
                            important_blocks.add(Material.GRAY_SHULKER_BOX);
                            important_blocks.add(Material.GREEN_SHULKER_BOX);
                            important_blocks.add(Material.LIGHT_BLUE_SHULKER_BOX);
                            important_blocks.add(Material.LIGHT_GRAY_SHULKER_BOX);
                            important_blocks.add(Material.LIME_SHULKER_BOX);
                            important_blocks.add(Material.MAGENTA_SHULKER_BOX);
                            important_blocks.add(Material.ORANGE_SHULKER_BOX);
                            important_blocks.add(Material.PINK_SHULKER_BOX);
                            important_blocks.add(Material.PURPLE_SHULKER_BOX);
                            important_blocks.add(Material.YELLOW_SHULKER_BOX);
                            important_blocks.add(Material.RED_SHULKER_BOX);
                            important_blocks.add(Material.WHITE_SHULKER_BOX);
                            important_blocks.add(Material.SPAWNER);
                            important_blocks.add(Material.COAL_BLOCK);
                            important_blocks.add(Material.DIAMOND_BLOCK);
                            important_blocks.add(Material.EMERALD_BLOCK);
                            important_blocks.add(Material.GOLD_BLOCK);
                            important_blocks.add(Material.IRON_BLOCK);
                            important_blocks.add(Material.LAPIS_BLOCK);
                            important_blocks.add(Material.REDSTONE_BLOCK);

                            ArrayList<Integer> ids = new ArrayList<>();
                            StringBuilder builder = new StringBuilder();
                            enhancedEyesNMS nms = new enhancedEyesNMS(player);
                            Location location = player.getLocation();
                            final double x = location.getX();
                            final double y = location.getY();
                            final double z = location.getZ();
                            final int block_range = 7;
                            final double dist_range = Math.pow(block_range - 1, 2);

                            //highlights important blocks
                            for (int x_offset = -block_range; x_offset <= block_range; x_offset++) {
                                for (int y_offset = -block_range; y_offset <= block_range; y_offset++) {
                                    for (int z_offset = -block_range; z_offset <= block_range; z_offset++) {
                                        Location blockReplaceLoc = new Location(location.getWorld(), x + x_offset, y + y_offset, z + z_offset);

                                        if (location.distanceSquared(blockReplaceLoc) < dist_range) {
                                            Block block = blockReplaceLoc.getBlock();
                                            Material blockMat = block.getType();

                                            if (blockMat.isSolid()) {
                                                if (important_blocks.contains(blockMat)) {
                                                    int nms_id = nms.spawnIndicator(blockReplaceLoc);
                                                    ids.add(nms_id);
                                                    builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            String eyes_map = builder.toString();
                            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".eyes_map", eyes_map);
                            plugin.saveConfig();

                            mana -= (initial_mana_cost - (constant_mana_drain / 20.0));
                            player.setMetadata("Mana", new FixedMetadataValue(plugin, mana));
                            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
                            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                            player.setMetadata("using_eyes", new FixedMetadataValue(plugin, true));

                            //TODO experimental clone
                            int cloneid = nms.spawnFakeClone(location);

                            BukkitTask runnable = new EyesRunnable(plugin, player, location, ids, cloneid).runTaskTimer(plugin, 0, 0);
                        }
                        else {
                            player.setMetadata("mana_needed_timer", new FixedMetadataValue(plugin, 5));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void breakBlock (BlockBreakEvent event) {
        Player player = event.getPlayer();

        final int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();
        if (!using_eyes) return;

        String string_map = plugin.getConfig().getString("Scores.users." + player.getUniqueId() + ".eyes_map");

        assert string_map != null;
        HashMap<Location, Integer> blocks_maps = stringToMap(player, string_map);

        Location block_location = event.getBlock().getLocation();
        if (blocks_maps.containsKey(block_location)) {
            enhancedEyesNMS nms = new enhancedEyesNMS(player);
            nms.removeIndicator(blocks_maps.get(block_location));
        }

    }

    public HashMap<Location, Integer> stringToMap(Player player, String string_map) {
        World world = player.getWorld();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Location> locations = new ArrayList<>();
        HashMap<Location, Integer> output = new HashMap<>();

        String[] pairs = string_map.split("_");

        for (String str : pairs) {
            String[] pairs_layer_two = str.split(":");
            ids.add(Integer.parseInt(pairs_layer_two[1]));

            String[] pairs_layer_three = pairs_layer_two[0].split(",");
            ArrayList<Integer> coords = new ArrayList<>();

            for (String str_coord : pairs_layer_three) {
                coords.add(Integer.parseInt(str_coord));
            }

            if (coords.size() == 3) {
                Location location = new Location(world, coords.get(0), coords.get(1), coords.get(2));
                locations.add(location);
            }
        }

        if (ids.size() == locations.size()) {
            for (int a = 0; a < ids.size(); a++) {
                output.put(locations.get(a), ids.get(a));
            }
        }

        return output;
    }

}
