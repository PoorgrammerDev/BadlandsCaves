package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class Displace implements Listener {
    private BadlandsCaves plugin;

    public Displace(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_displace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        World world = player.getWorld();
        ItemStack displace = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.displace").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(displace)) {
        //if (player.getInventory().getItemInOffHand().getType().equals(Material.KNOWLEDGE_BOOK)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {

                    int displace_level = player.getMetadata("displace_level").get(0).asInt();
                    int place_range, warp_range;
                    boolean cancel_fall;

                    if (displace_level == 1) {
                        place_range = 10;
                        warp_range = 15;
                        cancel_fall = false;
                    }
                    else {
                        place_range = 20;
                        warp_range = 30;
                        cancel_fall = true;
                    }

                    boolean has_displace_marker = player.getMetadata("has_displace_marker").get(0).asBoolean();
                    event.setCancelled(true);
                    if (has_displace_marker) {
                        double mana = player.getMetadata("Mana").get(0).asDouble();
                        int displace_mana_cost = plugin.getConfig().getInt("game_values.displace_mana_cost");

                        double x = player.getMetadata("displace_x").get(0).asDouble();
                        double y = player.getMetadata("displace_y").get(0).asDouble();
                        double z = player.getMetadata("displace_z").get(0).asDouble();
                        float current_yaw = player.getLocation().getYaw();
                        float current_pitch = player.getLocation().getPitch();
                        Location displace_marker = new Location(world, x, y, z, current_yaw, current_pitch);

                        if (player.getLocation().distance(displace_marker) <= warp_range) {
                            if (mana >= displace_mana_cost) {
                                if (cancel_fall) player.setFallDistance(0);
                                player.teleport(displace_marker, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));

                                player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);
                                for (Player powered : plugin.getServer().getOnlinePlayers()) {
                                    if (!powered.equals(player)) {
                                        if (powered.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                                            powered.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.3F, 1);
                                            powered.spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 5, 0.1, 0.1, 0.1, 1);
                                        }
                                    }
                                }

                                double new_mana = mana - (double) (displace_mana_cost);
                                player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                                player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 15));
                            }
                            else {
                                player.setMetadata("mana_needed_timer", new FixedMetadataValue(plugin, 5));
                            }
                        }
                        else if (player.getLocation().distance(displace_marker) < 20) {
                        }
                        else {
                            player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));
                        }
                    }
                    else {
                        BlockIterator iter = new BlockIterator(player, place_range);

                        Block lastBlock = iter.next();

                        while (iter.hasNext()) {

                            lastBlock = iter.next();

                            if (!lastBlock.getType().isSolid()) {
                                continue;
                            }
                            break;
                        }

                        Location location = lastBlock.getLocation();

                        location.setX(location.getX() + 0.5);
                        location.setY(location.getY() + 1.5);
                        location.setZ(location.getZ() + 0.5);

                        player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, true));
                        player.playSound(player.getLocation(), "custom.supernatural.displace.place_marker", SoundCategory.PLAYERS, 0.5F, 1);

                        player.setMetadata("displace_x", new FixedMetadataValue(plugin, location.getX()));
                        player.setMetadata("displace_y", new FixedMetadataValue(plugin, location.getY()));
                        player.setMetadata("displace_z", new FixedMetadataValue(plugin, location.getZ()));
                    }
                    player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                }
            }
        }
    }
}
