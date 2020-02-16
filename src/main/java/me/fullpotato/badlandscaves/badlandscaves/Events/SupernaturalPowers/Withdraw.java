package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class Withdraw implements Listener {
    private BadlandsCaves plugin;

    public Withdraw(BadlandsCaves bcav) {
        plugin = bcav;
    }

    private World void_world = Bukkit.getWorld("world_empty");

    @EventHandler
    public void use_withdraw(PlayerInteractEvent event) {
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
                    if (player.getLocation().getWorld().equals(void_world))
                        event.setCancelled(true);
                    else {
                        double mana = player.getMetadata("Mana").get(0).asDouble();
                        int withdraw_mana_cost = plugin.getConfig().getInt("game_values.withdraw_mana_cost");

                        event.setCancelled(true);
                        if (mana >= withdraw_mana_cost) {
                            boolean in_possession = player.getMetadata("in_possession").get(0).asBoolean();
                            if (!in_possession) {
                                Random random = new Random();

                                //generating the void chunk
                                for (int x = 0; x < 16; x++) {
                                    for (int y = 0; y < 256; y++) {
                                        for (int z = 0; z < 16; z++) {
                                            Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                            Location block_loc = block.getLocation();
                                            block_loc.setWorld(void_world);
                                            if (block.getType().isSolid()) {
                                                int rand = random.nextInt(3);
                                                if (rand == 0) block_loc.getBlock().setType(Material.COAL_BLOCK);
                                                else if (rand == 1) block_loc.getBlock().setType(Material.BLACK_CONCRETE);
                                                else block_loc.getBlock().setType(Material.BLACK_WOOL);
                                            } else {
                                                block_loc.getBlock().setType(Material.AIR);
                                            }
                                        }
                                    }
                                }

                                Location location = player.getLocation();
                                plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".withdraw_orig_world", location.getWorld().getName());

                                Location voidloc = player.getLocation();
                                voidloc.setWorld(void_world);

                                Chunk voidchunk = voidloc.getChunk();

                                player.setMetadata("withdraw_x", new FixedMetadataValue(plugin, voidloc.getX()));
                                player.setMetadata("withdraw_y", new FixedMetadataValue(plugin, voidloc.getY()));
                                player.setMetadata("withdraw_z", new FixedMetadataValue(plugin, voidloc.getZ()));
                                player.setMetadata("withdraw_chunk_x", new FixedMetadataValue(plugin, voidchunk.getX()));
                                player.setMetadata("withdraw_chunk_z", new FixedMetadataValue(plugin, voidchunk.getZ()));

                                player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, random.nextInt(200) + 500));

                                if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);
                                player.teleport(voidloc);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 0), true);

                                BukkitTask decrement_timer = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        int withdraw_timer = player.getMetadata("withdraw_timer").get(0).asInt();

                                        if (withdraw_timer <= 0) {
                                            int withdraw_level = player.getMetadata("withdraw_level").get(0).asInt();
                                            if (withdraw_level == 1) {
                                                player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, 0));
                                            }

                                            player.setFallDistance(0);
                                            player.teleport(location);
                                            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
                                            boolean ready_to_clear = true;

                                            for (Player player : Bukkit.getOnlinePlayers()) {
                                                if (player.getLocation().getWorld().equals(voidloc.getWorld()))
                                                    ready_to_clear = false;
                                            }

                                            if (ready_to_clear) {
                                                for (int x = 0; x < 16; x++) {
                                                    for (int y = 0; y < 256; y++) {
                                                        for (int z = 0; z < 16; z++) {
                                                            Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                                            Location block_loc = block.getLocation();
                                                            block_loc.setWorld(void_world);
                                                            block_loc.getBlock().setType(Material.AIR);
                                                        }
                                                    }
                                                }
                                            }

                                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                                        }
                                        else {
                                            player.spawnParticle(Particle.ENCHANTMENT_TABLE, voidloc, 10, 0, 1, 0);
                                            player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, withdraw_timer - 1));
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 0), true);
                                        }
                                    }
                                }.runTaskTimer(plugin, 0, 0);


                                double new_mana = mana - (double) (withdraw_mana_cost);
                                player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                                player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
                            }
                        }
                        else {
                            player.setMetadata("mana_needed_timer", new FixedMetadataValue(plugin, 5));
                        }
                    }
                    player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
                }
            }
        }
    }

    @EventHandler
    public void keep_in_chunk (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        Location location = player.getLocation();
        World world = location.getWorld();
        Chunk chunk = location.getChunk();

        assert world != null;
        if (!world.equals(void_world)) return;

        double void_x = player.getMetadata("withdraw_x").get(0).asDouble();
        double void_y = player.getMetadata("withdraw_y").get(0).asDouble();
        double void_z = player.getMetadata("withdraw_z").get(0).asDouble();
        int chunk_x = player.getMetadata("withdraw_chunk_x").get(0).asInt();
        int chunk_z = player.getMetadata("withdraw_chunk_z").get(0).asInt();

        if (location.getY() < 0 || chunk.getX() != chunk_x || chunk.getZ() != chunk_z) {
            Location origin = new Location(void_world, void_x, void_y, void_z, location.getYaw(), location.getPitch());
            player.teleport(origin);
        }
    }

}
