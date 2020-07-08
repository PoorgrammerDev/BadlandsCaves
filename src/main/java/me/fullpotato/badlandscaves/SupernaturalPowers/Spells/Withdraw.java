package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.WorldGeneration.PreventDragon;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Withdraw extends UsePowers implements Listener {
    private final World void_world;
    public Withdraw(BadlandsCaves bcav) {
        super(bcav);
        void_world = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
    }


    @EventHandler
    public void use_withdraw(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final ItemStack withdraw = CustomItem.WITHDRAW.getItem();
        if (player.getInventory().getItemInOffHand().isSimilar(withdraw)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (player.getLocation().getWorld().equals(void_world)) return;
                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1) return;
                    else {
                        int withdraw_level = (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player);
                        if (withdraw_level > 0) {
                            double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                            int withdraw_mana_cost = plugin.getOptionsConfig().getInt("spell_costs.withdraw_mana_cost");

                            event.setCancelled(true);
                            if (mana >= withdraw_mana_cost) {
                                preventDoubleClick(player);
                                boolean in_possession = ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);
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
                                        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".withdraw_orig_world", location.getWorld().getName());

                                        Location voidloc = player.getLocation();
                                        voidloc.setWorld(void_world);

                                        Chunk voidchunk = voidloc.getChunk();

                                        PlayerScore.WITHDRAW_X.setScore(plugin, player, voidloc.getX());
                                        PlayerScore.WITHDRAW_Y.setScore(plugin, player, voidloc.getY());
                                        PlayerScore.WITHDRAW_Z.setScore(plugin, player, voidloc.getZ());
                                        PlayerScore.WITHDRAW_CHUNK_X.setScore(plugin, player, voidchunk.getX());
                                        PlayerScore.WITHDRAW_CHUNK_Z.setScore(plugin, player, voidchunk.getZ());

                                        PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, random.nextInt(200) + 500);

                                        if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);

                                        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                            if (entity instanceof Player) {
                                                Player powered = (Player) entity;
                                                if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
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
                                                final int withdraw_timer = (int) PlayerScore.WITHDRAW_TIMER.getScore(plugin, player);
                                                if (withdraw_timer <= 0) {
                                                    getOuttaHere(player, location, voidloc, true, this.getTaskId());
                                                }
                                                else {
                                                    if (withdraw_level > 1 && withdraw_timer % (70) == 0) {
                                                        if (player.getHealth() < 20 && player.getHealth() > 0) player.setHealth(Math.max(Math.min(player.getHealth() + 1, 20), 0));
                                                        player.setFoodLevel(player.getFoodLevel() + 1);
                                                        PlayerScore.THIRST.setScore(plugin, player, Math.min((double) PlayerScore.THIRST.getScore(plugin, player) + 0.5, 100));
                                                        PlayerScore.TOXICITY.setScore(plugin, player, Math.max((double) PlayerScore.TOXICITY.getScore(plugin, player) - 0.5, 0));
                                                    }
                                                    player.spawnParticle(Particle.ENCHANTMENT_TABLE, voidloc, 10, 0, 1, 0);
                                                    PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, withdraw_timer - 1);
                                                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, 30, 0));

                                                    PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);
                                                }
                                            }
                                        }.runTaskTimer(plugin, 0, 0);

                                        double new_mana = mana - (double) (withdraw_mana_cost);
                                        PlayerScore.MANA.setScore(plugin, player, new_mana);
                                    }
                                }
                            }
                            else {
                                notEnoughMana(player);
                            }
                        }
                    }
                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                }
            }
        }
    }

    @EventHandler
    public void keep_in_chunk (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        Location location = player.getLocation();
        World world = location.getWorld();
        Chunk chunk = location.getChunk();

        assert world != null;
        if (!world.equals(void_world)) return;

        double void_x = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
        double void_y = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
        double void_z = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);
        int chunk_x = (int) PlayerScore.WITHDRAW_CHUNK_X.getScore(plugin, player);
        int chunk_z = (int) PlayerScore.WITHDRAW_CHUNK_Z.getScore(plugin, player);

        if (location.getY() < 0 || chunk.getX() != chunk_x || chunk.getZ() != chunk_z) {
            Location origin = new Location(void_world, void_x, void_y, void_z, location.getYaw(), location.getPitch());
            player.teleport(origin, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void getOuttaHere (Player player, Location returnLocation, Location voidLocation) {
        getOuttaHere(player, returnLocation, voidLocation, false, 0);
    }

    public void getOuttaHere (Player player, Location returnLocation, Location voidLocation, boolean cancel, int taskID) {
        final int withdraw_timer = (int) PlayerScore.WITHDRAW_TIMER.getScore(plugin, player);
        final int withdraw_level = (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player);
        if (withdraw_level == 1) {
            PlayerScore.HAS_DISPLACE_MARKER.setScore(plugin, player, 0);
        }


        player.setFallDistance(0);
        if (withdraw_timer != -255) {
            player.teleport(returnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.playSound(player.getLocation(), "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.5F, 1);

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GRAY, 1));
                    }
                }
            }
        }
        if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
        boolean ready_to_clear = true;

        for (Player scout : plugin.getServer().getOnlinePlayers()) {
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

        PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, 0);
        if (cancel) {
            plugin.getServer().getScheduler().cancelTask(taskID);
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
                    String worldname = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".withdraw_orig_world");
                    if (worldname != null && !worldname.isEmpty()) {
                        World other_world = plugin.getServer().getWorld(worldname);
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
