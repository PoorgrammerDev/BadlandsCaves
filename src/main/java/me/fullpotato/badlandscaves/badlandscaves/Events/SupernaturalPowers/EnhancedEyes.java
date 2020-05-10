package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.EyesRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;


public class EnhancedEyes implements Listener {
    private BadlandsCaves plugin;
    public EnhancedEyes(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) {
        Player player = event.getPlayer();

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);

                    final int eyes_level = player.hasMetadata("eyes_level") ? player.getMetadata("eyes_level").get(0).asInt() : 0;
                    if (eyes_level < 1) return;


                    final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();
                    if (using_eyes) {
                        player.setMetadata("using_eyes", new FixedMetadataValue(plugin, false));
                    }
                    else {
                        final int initial_mana_cost = plugin.getConfig().getInt("game_values.eyes_mana_cost");
                        final int constant_mana_drain = plugin.getConfig().getInt("game_values.eyes_mana_drain");
                        int mana = player.getMetadata("Mana").get(0).asInt();

                        if (mana >= initial_mana_cost) {
                            player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.5F, 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (player.getMetadata("using_eyes").get(0).asBoolean()) {
                                        player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.ambience", 0.4F, 1);
                                    }
                                    else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimerAsynchronously(plugin, 5, 330);

                            ArrayList<Material> minerals_tier1 = new ArrayList<>();
                            minerals_tier1.add(Material.COAL_ORE);
                            minerals_tier1.add(Material.IRON_ORE);
                            minerals_tier1.add(Material.COAL_BLOCK);
                            minerals_tier1.add(Material.IRON_BLOCK);

                            ArrayList<Material> minerals_tier2 = new ArrayList<>();

                            minerals_tier2.add(Material.DIAMOND_ORE);
                            minerals_tier2.add(Material.EMERALD_ORE);
                            minerals_tier2.add(Material.GOLD_ORE);
                            minerals_tier2.add(Material.LAPIS_ORE);
                            minerals_tier2.add(Material.REDSTONE_ORE);
                            minerals_tier2.add(Material.NETHER_QUARTZ_ORE);
                            minerals_tier2.add(Material.DIAMOND_BLOCK);
                            minerals_tier2.add(Material.EMERALD_BLOCK);
                            minerals_tier2.add(Material.GOLD_BLOCK);
                            minerals_tier2.add(Material.LAPIS_BLOCK);
                            minerals_tier2.add(Material.REDSTONE_BLOCK);
                            minerals_tier2.add(Material.SPAWNER);


                            ArrayList<Material> storage = new ArrayList<>();
                            storage.add(Material.CHEST);
                            storage.add(Material.BARREL);
                            storage.add(Material.SHULKER_BOX);
                            storage.add(Material.BLACK_SHULKER_BOX);
                            storage.add(Material.BLUE_SHULKER_BOX);
                            storage.add(Material.BROWN_SHULKER_BOX);
                            storage.add(Material.CYAN_SHULKER_BOX);
                            storage.add(Material.GRAY_SHULKER_BOX);
                            storage.add(Material.GREEN_SHULKER_BOX);
                            storage.add(Material.LIGHT_BLUE_SHULKER_BOX);
                            storage.add(Material.LIGHT_GRAY_SHULKER_BOX);
                            storage.add(Material.LIME_SHULKER_BOX);
                            storage.add(Material.MAGENTA_SHULKER_BOX);
                            storage.add(Material.ORANGE_SHULKER_BOX);
                            storage.add(Material.PINK_SHULKER_BOX);
                            storage.add(Material.PURPLE_SHULKER_BOX);
                            storage.add(Material.YELLOW_SHULKER_BOX);
                            storage.add(Material.RED_SHULKER_BOX);
                            storage.add(Material.WHITE_SHULKER_BOX);

                            ArrayList<Material> danger = new ArrayList<>();
                            danger.add(Material.LAVA);
                            danger.add(Material.TNT);

                            ArrayList<Integer> ids = new ArrayList<>();
                            StringBuilder builder = new StringBuilder();
                            EnhancedEyesNMS nms = new EnhancedEyesNMS(player);
                            Location location = player.getLocation();
                            final double x = location.getX();
                            final double y = location.getY();
                            final double z = location.getZ();
                            final int block_range = (eyes_level >= 2) ? 10 : 5;
                            final double dist_range = Math.pow(block_range - 1, 2);

                            //highlights important blocks
                            for (int x_offset = -block_range; x_offset <= block_range; x_offset++) {
                                for (int y_offset = -block_range; y_offset <= block_range; y_offset++) {
                                    for (int z_offset = -block_range; z_offset <= block_range; z_offset++) {
                                        Location blockReplaceLoc = new Location(location.getWorld(), x + x_offset, y + y_offset, z + z_offset);

                                        if (location.distanceSquared(blockReplaceLoc) < dist_range) {
                                            Block block = blockReplaceLoc.getBlock();
                                            Material blockMat = block.getType();

                                            if (blockMat.isSolid() || (block.isLiquid() && ((Levelled) block.getBlockData()).getLevel() == 0)) {
                                                if (eyes_level > 1) {
                                                    if (minerals_tier1.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(blockReplaceLoc, ChatColor.GRAY);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                    else if (minerals_tier2.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(blockReplaceLoc, ChatColor.BLUE);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                    else if (storage.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(blockReplaceLoc, ChatColor.GREEN);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                    else if (danger.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(blockReplaceLoc, ChatColor.RED);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                }
                                                else {
                                                    if (minerals_tier1.contains(blockMat) || minerals_tier2.contains(blockMat) || storage.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(blockReplaceLoc);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
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
                            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 15));
                            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                            player.setMetadata("using_eyes", new FixedMetadataValue(plugin, true));

                            ParticleShapes.particleSphere(player, Particle.REDSTONE, player.getLocation(), block_range - 1, 0, new Particle.DustOptions(Color.BLUE, 1));
                            for (Player powered : plugin.getServer().getOnlinePlayers()) {
                                if (!(powered.equals(player)) && powered.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                                    powered.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.3F, 1);
                                    powered.spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 0.05, 0.05, 0.05, 0, new Particle.DustOptions(Color.BLUE, 1));
                                }
                            }


                            new EyesRunnable(plugin, player, location, ids, player.hasPotionEffect(PotionEffectType.NIGHT_VISION)).runTaskTimer(plugin, 0, 0);
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

        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        final boolean using_eyes = player.getMetadata("using_eyes").get(0).asBoolean();
        if (!using_eyes) return;

        String string_map = plugin.getConfig().getString("Scores.users." + player.getUniqueId() + ".eyes_map");

        assert string_map != null;
        HashMap<Location, Integer> blocks_maps = stringToMap(player, string_map);

        Location block_location = event.getBlock().getLocation();
        if (blocks_maps.containsKey(block_location)) {
            EnhancedEyesNMS nms = new EnhancedEyesNMS(player);
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
