package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.EyesRunnable;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;


public class EnhancedEyes extends UsePowers implements Listener {
    public EnhancedEyes(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) {
        Player player = event.getPlayer();

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final ItemStack eyes = CustomItem.ENHANCED_EYES.getItem();
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);

                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1) return;
                    final int eyes_level = (PlayerScore.EYES_LEVEL.hasScore(plugin, player)) ? (int) PlayerScore.EYES_LEVEL.getScore(plugin, player) : 0;
                    if (eyes_level < 1) return;

                    final boolean using_eyes = (byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1;

                    preventDoubleClick(player);
                    if (using_eyes) {
                        PlayerScore.USING_EYES.setScore(plugin, player, 0);
                    }
                    else {
                        final int initial_mana_cost = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_cost");
                        final int constant_mana_drain = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_drain");
                        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));

                        if (mana >= initial_mana_cost) {
                            player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.5F, 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1)) {
                                        player.stopSound("custom.supernatural.enhanced_eyes.ambience");
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
                            minerals_tier2.add(Material.NETHER_GOLD_ORE);
                            minerals_tier2.add(Material.GILDED_BLACKSTONE);
                            minerals_tier2.add(Material.LAPIS_ORE);
                            minerals_tier2.add(Material.REDSTONE_ORE);
                            minerals_tier2.add(Material.NETHER_QUARTZ_ORE);
                            minerals_tier2.add(Material.DIAMOND_BLOCK);
                            minerals_tier2.add(Material.EMERALD_BLOCK);
                            minerals_tier2.add(Material.GOLD_BLOCK);
                            minerals_tier2.add(Material.LAPIS_BLOCK);
                            minerals_tier2.add(Material.REDSTONE_BLOCK);
                            minerals_tier2.add(Material.DEAD_TUBE_CORAL_BLOCK);
                            minerals_tier2.add(Material.ANCIENT_DEBRIS);

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
                            storage.add(Material.SPAWNER);

                            ArrayList<Integer> ids = new ArrayList<>();
                            StringBuilder builder = new StringBuilder();
                            EnhancedEyesNMS nms = plugin.getEnhancedEyesNMS();
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
                                                        int nms_id = nms.spawnIndicator(player, blockReplaceLoc, ChatColor.GRAY);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                    else if (minerals_tier2.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(player, blockReplaceLoc, ChatColor.BLUE);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                    else if (storage.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(player, blockReplaceLoc, ChatColor.GREEN);
                                                        ids.add(nms_id);
                                                        builder.append(blockReplaceLoc.getBlockX()).append(",").append(blockReplaceLoc.getBlockY()).append(",").append(blockReplaceLoc.getBlockZ()).append(":").append(nms_id).append("_");
                                                    }
                                                }
                                                else {
                                                    if (minerals_tier1.contains(blockMat) || minerals_tier2.contains(blockMat) || storage.contains(blockMat)) {
                                                        int nms_id = nms.spawnIndicator(player, blockReplaceLoc);
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
                            plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".eyes_map", eyes_map);
                            plugin.saveSystemConfig();

                            mana -= (initial_mana_cost - (constant_mana_drain / 20.0));
                            PlayerScore.MANA.setScore(plugin, player, mana);
                            PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);
                            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                            PlayerScore.USING_EYES.setScore(plugin, player, 1);

                            ParticleShapes.particleSphere(player, Particle.REDSTONE, player.getLocation(), block_range - 1, 0, new Particle.DustOptions(Color.BLUE, 1));
                            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                if (entity instanceof Player) {
                                    Player powered = (Player) entity;
                                    if (!(powered.equals(player)) && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1 && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                        powered.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.3F, 1);
                                        powered.spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 0.05, 0.05, 0.05, 0, new Particle.DustOptions(Color.BLUE, 1));
                                    }
                                }
                            }


                            new EyesRunnable(plugin, player, location, ids, player.hasPotionEffect(PotionEffectType.NIGHT_VISION)).runTaskTimer(plugin, 0, 0);
                        }
                        else {
                            notEnoughMana(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void breakBlock (BlockBreakEvent event) {
        Player player = event.getPlayer();

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final boolean using_eyes = ((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1);
        if (!using_eyes) return;

        String string_map = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".eyes_map");

        assert string_map != null;
        HashMap<Location, Integer> blocks_maps = stringToMap(player, string_map);

        Location block_location = event.getBlock().getLocation();
        if (blocks_maps.containsKey(block_location)) {
            EnhancedEyesNMS nms = plugin.getEnhancedEyesNMS();
            nms.removeIndicator(player, blocks_maps.get(block_location));
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
