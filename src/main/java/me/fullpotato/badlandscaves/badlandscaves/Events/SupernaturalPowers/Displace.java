package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.LineOfSight;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

public class Displace extends UsePowers implements Listener {
    public Displace(BadlandsCaves bcav) {
        super(bcav);
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
                    event.setCancelled(true);
                    if (player.getMetadata("spell_cooldown").get(0).asBoolean()) return;

                    final ManaBarManager manaBar = new ManaBarManager(plugin);
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
                            if (LineOfSight.hasLineOfSight(player, displace_marker)) {
                                if (mana >= displace_mana_cost) {
                                    preventDoubleClick(player);
                                    if (cancel_fall) player.setFallDistance(0);
                                    player.teleport(displace_marker, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                    player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));

                                    player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);
                                    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                        if (entity instanceof Player) {
                                            Player powered = (Player) entity;
                                            if (!powered.equals(player) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                                if (powered.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                                                    powered.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.3F, 1);
                                                    powered.spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 5, 0.1, 0.1, 0.1, 1);
                                                }
                                            }
                                        }
                                    }

                                    double new_mana = mana - (double) (displace_mana_cost);
                                    player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                                    player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 15));
                                }
                                else {
                                    notEnoughMana(player);
                                }
                            }
                            else {
                                manaBar.displayMessage(player, "§cLine of Sight required!", 2, false);
                            }
                        }
                        else if (player.getLocation().distance(displace_marker) < 20) {
                        }
                        else {
                            player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));
                        }
                    }
                    else {
                        preventDoubleClick(player);
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

                        if (LineOfSight.hasLineOfSight(player, location)) {

                            location.setX(location.getX() + 0.5);
                            location.setY(location.getY() + 1.5);
                            location.setZ(location.getZ() + 0.5);

                            player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, true));
                            player.playSound(player.getLocation(), "custom.supernatural.displace.place_marker", SoundCategory.PLAYERS, 0.5F, 1);

                            player.setMetadata("displace_x", new FixedMetadataValue(plugin, location.getX()));
                            player.setMetadata("displace_y", new FixedMetadataValue(plugin, location.getY()));
                            player.setMetadata("displace_z", new FixedMetadataValue(plugin, location.getZ()));
                        }
                        else {
                            manaBar.displayMessage(player, "§cLine of Sight required!", 2, false);
                        }
                        player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                    }
                }
            }
        }
    }
}
