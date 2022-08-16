package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactSoulHeist;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Withdraw extends UsePowers implements Listener {
    private final Random random;
    final int cost = plugin.getOptionsConfig().getInt("spell_costs.withdraw_mana_cost");
    private final World void_world;
    private final ArtifactManager artifactManager;
    private final ArtifactSoulHeist artifactSoulHeist;
    private final World backrooms;
    private final ParticleShapes particleShapes;

    public Withdraw(Random random, BadlandsCaves plugin, ArtifactManager artifactManager, Possession possession, Voidmatter voidmatter, ParticleShapes particleShapes) {
        super(plugin, particleShapes);
        this.random = random;
        this.backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
        void_world = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
        this.particleShapes = particleShapes;
        artifactSoulHeist = new ArtifactSoulHeist(plugin, this, possession, voidmatter, artifactManager);
        this.artifactManager = artifactManager;
    }

    @EventHandler
    public void useWithdrawSpell(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        if (player.getInventory().getItemInOffHand().isSimilar(plugin.getCustomItemManager().getItem(CustomItem.WITHDRAW))) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (player.getWorld().equals(void_world) || (byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1
                     || ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0) || attemptSilence(player)) return;
                    if (player.getWorld().equals(backrooms)) return;
                        final int withdraw_level = (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player);
                        if (withdraw_level > 0) {
                            attemptEnterWithdraw(player);
                        }
                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                }
            }
        }
    }

    public void attemptEnterWithdraw (Player player) {
        final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
        if (mana >= cost) {
            preventDoubleClick(player);
            boolean in_possession = ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);
            if (!in_possession) {
                if (checkOtherPlayers(player)) {
                    if (artifactManager.hasArtifact(player, Artifact.SOUL_HEIST)) {
                        PlayerScore.MANA.setScore(plugin, player, mana - cost);

                        artifactSoulHeist.launchProjectile(player);
                        return;
                    }
                    enterWithdraw(player, true);
                }
            }
        }
        else {
            notEnoughMana(player);
        }
    }

    public void enterWithdraw(Player player, boolean subtractCost) {
        final boolean supernatural = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        final int withdraw_level = (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player);
        generateVoidChunk(player);

        final Location location = player.getLocation();
        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".withdraw_orig_world", player.getWorld().getName());

        final Location voidLoc = location.clone();
        voidLoc.setWorld(void_world);

        final Chunk voidChunk = voidLoc.getChunk();
        final int duration = random.nextInt(200) + 500;

        PlayerScore.WITHDRAW_X.setScore(plugin, player, voidLoc.getX());
        PlayerScore.WITHDRAW_Y.setScore(plugin, player, voidLoc.getY());
        PlayerScore.WITHDRAW_Z.setScore(plugin, player, voidLoc.getZ());
        PlayerScore.WITHDRAW_CHUNK_X.setScore(plugin, player, voidChunk.getX());
        PlayerScore.WITHDRAW_CHUNK_Z.setScore(plugin, player, voidChunk.getZ());

        PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, duration);

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

        player.teleport(voidLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.playSound(player.getLocation(), "custom.supernatural.withdraw.enter", SoundCategory.PLAYERS, 0.5F, 1);

        if (supernatural) {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0));
        }
        else {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, duration, 2));
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                final int timer = (int) PlayerScore.WITHDRAW_TIMER.getScore(plugin, player);
                if (timer <= 0) {
                    this.cancel();
                    exitWithdraw(player, location, voidLoc);
                }
                else {
                    if (withdraw_level > 1 && timer % (70) == 0) {
                        if (player.getHealth() < 20 && player.getHealth() > 0)
                            player.setHealth(Math.max(Math.min(player.getHealth() + 1, 20), 0));
                        PlayerScore.TOXICITY.setScore(plugin, player, Math.max((double) PlayerScore.TOXICITY.getScore(plugin, player) - 0.5, 0));
                    }
                    player.spawnParticle(Particle.ENCHANTMENT_TABLE, voidLoc, 10, 0, 1, 0);
                    PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, timer - 1);

                    PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
                }
            }
        }.runTaskTimer(plugin, 0, 0);

        if (subtractCost) {
            final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            PlayerScore.MANA.setScore(plugin, player, mana - cost);
        }
    }

    public void generateVoidChunk (Player player) {
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
    }

    @EventHandler
    public void keepInChunk(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(void_world)) return;

        final Location location = player.getLocation();
        final Chunk chunk = location.getChunk();
        double void_x = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
        double void_y = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
        double void_z = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);
        int chunk_x = (int) PlayerScore.WITHDRAW_CHUNK_X.getScore(plugin, player);
        int chunk_z = (int) PlayerScore.WITHDRAW_CHUNK_Z.getScore(plugin, player);

        if (location.getY() < 0 || chunk.getX() != chunk_x || chunk.getZ() != chunk_z) {
            final Location origin = new Location(void_world, void_x, void_y, void_z, location.getYaw(), location.getPitch());
            player.teleport(origin, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    public void exitWithdraw(Player player, Location returnLocation, Location voidLocation) {
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
