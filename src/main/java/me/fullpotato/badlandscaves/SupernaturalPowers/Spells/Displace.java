package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Blocks.SilencerBlock;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Silencer;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
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
import org.bukkit.util.BlockIterator;

public class Displace extends UsePowers implements Listener {
    public Displace(BadlandsCaves bcav) {
        super(bcav);
    }

    @EventHandler
    public void use_displace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        World world = player.getWorld();
        ItemStack displace = CustomItem.DISPLACE.getItem();
        if (player.getInventory().getItemInOffHand().isSimilar(displace)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1)) return;

                    SilencerBlock silencerBlock = new SilencerBlock(plugin);
                    if (silencerBlock.getNearbySilencer()) {
                        // TODO: 7/8/2020
                    }


                    final ManaBarManager manaBar = new ManaBarManager(plugin);
                    int displace_level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
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

                    boolean has_displace_marker = ((byte) PlayerScore.HAS_DISPLACE_MARKER.getScore(plugin, player) == 1);
                    if (has_displace_marker) {
                        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                        int displace_mana_cost = plugin.getOptionsConfig().getInt("spell_costs.displace_mana_cost");

                        double x = (double) PlayerScore.DISPLACE_X.getScore(plugin, player);
                        double y = (double) PlayerScore.DISPLACE_Y.getScore(plugin, player);
                        double z = (double) PlayerScore.DISPLACE_Z.getScore(plugin, player);
                        float current_yaw = player.getLocation().getYaw();
                        float current_pitch = player.getLocation().getPitch();
                        Location displace_marker = new Location(world, x, y, z, current_yaw, current_pitch);

                        if (player.getLocation().distance(displace_marker) <= warp_range) {
                            LineOfSightNMS nms = plugin.getLineOfSightNMS();
                            if (nms.hasLineOfSight(player, displace_marker)) {
                                if (mana >= displace_mana_cost) {
                                    preventDoubleClick(player);
                                    if (cancel_fall) player.setFallDistance(0);
                                    player.teleport(displace_marker, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                    PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);

                                    player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.5F, 1);
                                    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                        if (entity instanceof Player) {
                                            Player powered = (Player) entity;
                                            if (!powered.equals(player) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) {
                                                    powered.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.3F, 1);
                                                    powered.spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 5, 0.1, 0.1, 0.1, 1);
                                                }
                                            }
                                        }
                                    }

                                    double new_mana = mana - (double) (displace_mana_cost);
                                    PlayerScore.MANA.setScore(plugin, player, new_mana);
                                    PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);
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
                            PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
                        }
                    }
                    else {
                        preventDoubleClick(player);
                        BlockIterator iter = new BlockIterator(player, place_range);
                        Location lastLastBlockLoc = null;
                        Block lastBlock = iter.next();
                        Location lastBlockLocation = lastBlock.getLocation().add(0.5, 0.5, 0.5);
                        LineOfSightNMS nms = plugin.getLineOfSightNMS();

                        while (iter.hasNext()) {
                            lastLastBlockLoc = lastBlockLocation;
                            lastBlockLocation = lastBlock.getLocation().add(0.5, 0.5, 0.5);
                            lastBlock = iter.next();
                            if (lastBlock.getType().isSolid() || !nms.hasLineOfSight(player, lastBlockLocation) || !lastBlockLocation.getWorld().getWorldBorder().isInside(lastBlockLocation)) {
                                break;
                            }
                        }

                        assert lastLastBlockLoc != null;
                        Location location = lastLastBlockLoc.clone();
                        if (nms.hasLineOfSight(player, location)) {
                            PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 1);
                            player.playSound(player.getLocation(), "custom.supernatural.displace.place_marker", SoundCategory.PLAYERS, 0.5F, 1);

                            PlayerScore.DISPLACE_X.setScore(plugin, player, location.getX());
                            PlayerScore.DISPLACE_Y.setScore(plugin, player, location.getY());
                            PlayerScore.DISPLACE_Z.setScore(plugin, player, location.getZ());
                        }
                        else {
                            manaBar.displayMessage(player, "§cLine of Sight required!", 2, false);
                        }
                        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                    }
                }
            }
        }
    }
}
