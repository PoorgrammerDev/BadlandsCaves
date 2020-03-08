package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ReflectionBuild implements Listener {
    private BadlandsCaves plugin;
    private final World reflection_world = Bukkit.getWorld("world_reflection");
    public ReflectionBuild (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void breakBlock (final BlockBreakEvent event) {
        if (!event.getBlock().getWorld().equals(reflection_world)) return;

        final Block block = event.getBlock();
        final Material original_material = block.getType();
        final Location block_loc = block.getLocation();

        ArrayList<Material> natural = new ArrayList<>();
        natural.add(Material.ICE);
        natural.add(Material.PACKED_ICE);
        natural.add(Material.BLUE_ICE);
        natural.add(Material.LIGHT_BLUE_CONCRETE);
        natural.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        natural.add(Material.LIGHT_BLUE_STAINED_GLASS);
        natural.add(Material.LIGHT_BLUE_WOOL);
        natural.add(Material.LIGHT_BLUE_CONCRETE_POWDER);
        natural.add(Material.SEA_LANTERN);
        natural.add(Material.SNOW_BLOCK);

        if (natural.contains(original_material)) {
            event.setDropItems(false);
            undoEdit(block_loc, original_material, Material.AIR, 10, true);
            if (original_material.equals(Material.ICE)) {
                block.setType(Material.AIR);
            }
        }
        else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void noItemDamage (final PlayerItemDamageEvent event) {
        if (!event.getPlayer().getWorld().equals(reflection_world)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void placeBlock (final BlockPlaceEvent event) {
        if (!event.getBlock().getWorld().equals(reflection_world) || !event.getPlayer().getWorld().equals(reflection_world)) return;
        final Player player = event.getPlayer();
        final Location location = event.getBlockPlaced().getLocation();

        ArrayList<Material> blacklisted = new ArrayList<>();
        blacklisted.add(Material.BEDROCK);
        blacklisted.add(Material.BARRIER);
        blacklisted.add(Material.COMMAND_BLOCK);
        blacklisted.add(Material.STRUCTURE_BLOCK);
        blacklisted.add(Material.TNT);
        blacklisted.add(Material.CHEST);
        blacklisted.add(Material.SHULKER_BOX);
        blacklisted.add(Material.BLACK_SHULKER_BOX);
        blacklisted.add(Material.BLUE_SHULKER_BOX);
        blacklisted.add(Material.BROWN_SHULKER_BOX);
        blacklisted.add(Material.CYAN_SHULKER_BOX);
        blacklisted.add(Material.GRAY_SHULKER_BOX);
        blacklisted.add(Material.GREEN_SHULKER_BOX);
        blacklisted.add(Material.LIGHT_BLUE_SHULKER_BOX);
        blacklisted.add(Material.LIGHT_GRAY_SHULKER_BOX);
        blacklisted.add(Material.LIME_SHULKER_BOX);
        blacklisted.add(Material.MAGENTA_SHULKER_BOX);
        blacklisted.add(Material.ORANGE_SHULKER_BOX);
        blacklisted.add(Material.PINK_SHULKER_BOX);
        blacklisted.add(Material.PURPLE_SHULKER_BOX);
        blacklisted.add(Material.RED_SHULKER_BOX);
        blacklisted.add(Material.WHITE_SHULKER_BOX);
        blacklisted.add(Material.YELLOW_SHULKER_BOX);
        blacklisted.add(Material.ENDER_CHEST);
        blacklisted.add(Material.BARREL);
        blacklisted.add(Material.FURNACE);
        blacklisted.add(Material.BLAST_FURNACE);
        blacklisted.add(Material.DISPENSER);
        blacklisted.add(Material.DROPPER);
        blacklisted.add(Material.TRAPPED_CHEST);
        if (blacklisted.contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        undoEdit(location, Material.AIR,location.getBlock().getType(), 10, true);

        if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
            ItemStack main = event.getItemInHand();
            ItemStack clone = main.clone();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (main.getAmount() > 1) {
                        main.setAmount(main.getAmount() + 1);
                    }
                    else {
                        player.getInventory().addItem(clone);
                    }
                }
            }.runTaskLaterAsynchronously(plugin, 2);
        }
    }

    @EventHandler
    public void wrapAround (final PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().equals(reflection_world)) {
            final Player player = event.getPlayer();
            final Location location = player.getLocation();

            if (location.getY() < -40) {
                location.setY(330);
                player.teleport(location);
            }
        }
    }

    public void undoEdit (final Location location, final Material original_material, final Material current_material, final int delay_in_seconds, final boolean effect) {
        final int delay = delay_in_seconds * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (location.getBlock().getType().equals(current_material)) {
                    location.getBlock().setType(original_material);

                    if (effect) {
                        location.add(0.5, 0.5,0.5);
                        location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 20, 0.25, 0.25,0.25, 0);
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.2F, 1);
                    }
                }
            }
        }.runTaskLater(plugin, delay);
    }
}
