package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.PreventDragon;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class Withdraw extends UsePowers implements Listener {
    public Withdraw(BadlandsCaves bcav) {
        super(bcav);
    }

    private World void_world = Bukkit.getWorld("world_empty");

    @EventHandler
    public void use_withdraw(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack withdraw = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.withdraw").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (player.getLocation().getWorld().equals(void_world)) return;
                    if (player.getMetadata("spell_cooldown").get(0).asBoolean()) return;
                    else {
                        int withdraw_level = player.getMetadata("withdraw_level").get(0).asInt();
                        if (withdraw_level > 0) {
                            double mana = player.getMetadata("Mana").get(0).asDouble();
                            int withdraw_mana_cost = plugin.getConfig().getInt("game_values.withdraw_mana_cost");

                            event.setCancelled(true);
                            if (mana >= withdraw_mana_cost) {
                                preventDoubleClick(player);
                                boolean in_possession = player.getMetadata("in_possession").get(0).asBoolean();
                                if (!in_possession) {
                                    Random random = new Random();
                                    if (checkOtherPlayers(player)) {
                                        //generating the void chunk
                                        for (int x = 0; x < 16; x++) {
                                            for (int y = 0; y < 256; y++) {
                                                for (int z = 0; z < 16; z++) {
                                                    Block block = player.getLocation().getChunk().getBlock(x, y, z);
                                                    Location block_loc = block.getLocation();
                                                    block_loc.setWorld(void_world);
                                                    if (block.getType().isSolid()) {
                                                        block_loc.getBlock().setType(MakeDescensionStage.getVoidMat(random));
                                                    } else if (!block.getType().isAir()) {
                                                        block_loc.getBlock().setType(Material.AIR);
                                                    }
                                                }
                                            }
                                        }
                                        PreventDragon.preventDragonSpawn(void_world);

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

                                        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                            if (entity instanceof Player) {
                                                Player powered = (Player) entity;
                                                if (!(powered.equals(player)) && powered.getMetadata("has_supernatural_powers").get(0).asBoolean() && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                                    powered.playSound(player.getLocation(), "custom.supernatural.withdraw.enter", SoundCategory.PLAYERS, 0.3F, 1);
                                                    powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GRAY, 1));
                                                }
                                            }
                                        }


                                        player.teleport(voidloc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                        player.playSound(player.getLocation(), "custom.supernatural.withdraw.enter", SoundCategory.PLAYERS, 0.5F, 1);

                                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 0));

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                final int withdraw_timer = player.getMetadata("withdraw_timer").get(0).asInt();
                                                if (withdraw_timer <= 0) {
                                                    getOuttaHere(player, location, voidloc, true, this.getTaskId());
                                                }
                                                else {
                                                    if (withdraw_level > 1 && withdraw_timer % (70) == 0) {
                                                        if (player.getHealth() < 20 && player.getHealth() > 0) player.setHealth(Math.max(Math.min(player.getHealth() + 1, 20), 0));
                                                        player.setFoodLevel(player.getFoodLevel() + 1);
                                                        player.setMetadata("Thirst", new FixedMetadataValue(plugin, Math.min(player.getMetadata("Thirst").get(0).asDouble() + 0.5, 100)));
                                                        player.setMetadata("Toxicity", new FixedMetadataValue(plugin, Math.max(player.getMetadata("Toxicity").get(0).asDouble() - 0.5, 0)));
                                                    }
                                                    player.spawnParticle(Particle.ENCHANTMENT_TABLE, voidloc, 10, 0, 1, 0);
                                                    player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, withdraw_timer - 1));
                                                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 0));

                                                    player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 15));
                                                }
                                            }
                                        }.runTaskTimer(plugin, 0, 0);

                                        double new_mana = mana - (double) (withdraw_mana_cost);
                                        player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                                    }
                                }
                            }
                            else {
                                notEnoughMana(player);
                            }
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
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

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
            player.teleport(origin, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void getOuttaHere (Player player, Location returnLocation, Location voidLocation) {
        getOuttaHere(player, returnLocation, voidLocation, false, 0);
    }

    public void getOuttaHere (Player player, Location returnLocation, Location voidLocation, boolean cancel, int taskID) {
        final int withdraw_timer = player.getMetadata("withdraw_timer").get(0).asInt();
        final int withdraw_level = player.getMetadata("withdraw_level").get(0).asInt();
        if (withdraw_level == 1) {
            player.setMetadata("has_displace_marker", new FixedMetadataValue(plugin, false));
        }


        player.setFallDistance(0);
        if (withdraw_timer != -255) {
            player.teleport(returnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.playSound(player.getLocation(), "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.5F, 1);

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && powered.getMetadata("has_supernatural_powers").get(0).asBoolean() && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GRAY, 1));
                    }
                }
            }
        }
        if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
        boolean ready_to_clear = true;

        for (Player scout : Bukkit.getOnlinePlayers()) {
            if (!scout.equals(player) && scout.getLocation().getWorld().equals(voidLocation.getWorld()) && scout.getLocation().getChunk().equals(voidLocation.getChunk())) {
                ready_to_clear = false;
                break;
            }
        }

        if (ready_to_clear) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = player.getLocation().getChunk().getBlock(x, y, z);
                        if (!block.getType().isAir()) {
                            Location block_loc = block.getLocation();
                            block_loc.setWorld(void_world);
                            block_loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
            PreventDragon.preventDragonSpawn(void_world);
        }

        player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, 0));
        if (cancel) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public boolean checkOtherPlayers (Player player) {
        World world = player.getWorld();
        Chunk chunk = player.getLocation().getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();

        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.getWorld().equals(void_world)) {
                Chunk other_chunk = other.getLocation().getChunk();
                if (other_chunk.getX() == x && other_chunk.getZ() == z) {
                    String worldname = plugin.getConfig().getString("Scores.users." + player.getUniqueId() + ".withdraw_orig_world");
                    if (worldname != null && !worldname.isEmpty()) {
                        World other_world = Bukkit.getWorld(worldname);
                        return (world.equals(other_world));
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void fixCombat (EntityDamageByEntityEvent event) {
        if (!event.getDamager().getWorld().equals(event.getEntity().getWorld())) event.setCancelled(true);
    }

}
