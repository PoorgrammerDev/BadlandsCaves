package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactSoulHeist;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.DescensionReset;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.MakeDescensionStage;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.WorldGeneration.PreventDragon;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Withdraw extends UsePowers implements Listener {
    private final Random random;
    final int cost = plugin.getOptionsConfig().getInt("spell_costs.withdraw_mana_cost");
    private final World void_world;
    private final ArtifactManager artifactManager;
    private final ArtifactSoulHeist artifactSoulHeist;
    private final World backrooms;
    private final ParticleShapes particleShapes;
    private final LineOfSightNMS lineOfSightNMS;

    private final BlockFace[] adjacentFaces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.SOUTH,
            BlockFace.UP,
            BlockFace.DOWN,
    };

    public Withdraw(Random random, BadlandsCaves plugin, ArtifactManager artifactManager, Possession possession, Voidmatter voidmatter, ParticleShapes particleShapes, LineOfSightNMS lineOfSightNMS) {
        super(plugin, particleShapes);
        this.random = random;
        this.backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
        void_world = plugin.getServer().getWorld(plugin.getWithdrawWorldName());
        this.particleShapes = particleShapes;
        artifactSoulHeist = new ArtifactSoulHeist(plugin, this, possession, voidmatter, artifactManager);
        this.artifactManager = artifactManager;
        this.lineOfSightNMS = lineOfSightNMS;
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

                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1 ||
                            ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0) ||
                            attemptSilence(player)) return;
                    if (player.getWorld().equals(backrooms)) return;

                    //Exit Withdraw world manually
                    if (player.getWorld().equals(void_world)) {
                        String worldName = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".withdraw_orig_world");
                        if (worldName == null) return;

                        final double originX = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
                        final double originY = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
                        final double originZ = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);
                        final Location originVoid = new Location(void_world, originX, originY, originZ);
                        final Location voidLoc = player.getLocation();
                        final Location origin = new Location(plugin.getServer().getWorld(worldName), originX, originY, originZ, voidLoc.getYaw(), voidLoc.getPitch());
                        final Location location = new Location(plugin.getServer().getWorld(worldName), voidLoc.getX(), voidLoc.getY(), voidLoc.getZ(), voidLoc.getYaw(), voidLoc.getPitch());

                        exitWithdraw(player, lineOfSightNMS.hasLineOfSight(player, originVoid) ? location : origin);
                        return;
                    }

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
        final int duration = random.nextInt(250) + 750;

        //Generates the Withdraw world terrain
        generateWithdrawClone(player, player.getLocation(), (withdraw_level > 1 ? 30 : 15), plugin.getOptionsConfig().getInt("withdraw_minimum_initial_layers"), plugin.getOptionsConfig().getInt("withdraw_max_blocks_per_tick"));

        //Gets entrance position
        final Location location = player.getLocation();
        final Location voidLoc = location.clone();
        voidLoc.setWorld(void_world);

        //Set original world to return player to
        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".withdraw_orig_world", player.getWorld().getName());

        //Sets entrance pos and timer
        PlayerScore.WITHDRAW_X.setScore(plugin, player, voidLoc.getX());
        PlayerScore.WITHDRAW_Y.setScore(plugin, player, voidLoc.getY());
        PlayerScore.WITHDRAW_Z.setScore(plugin, player, voidLoc.getZ());
        PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, duration);

        if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);

        //Play sound to other nearby sorcerers
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof Player) {
                Player powered = (Player) entity;
                if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                    powered.playSound(player.getLocation(), "custom.supernatural.withdraw.enter", SoundCategory.PLAYERS, 0.3F, 1);
                    powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GRAY, 1));
                }
            }
        }

        //Enter world + sound
        player.teleport(voidLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.playSound(player.getLocation(), "custom.supernatural.withdraw.enter", SoundCategory.PLAYERS, 0.5F, 1);

        //Apply potion effects
        if (supernatural) {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0));
        }
        else {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, duration, 2));
        }

        final Location raisedVoidLoc = voidLoc.clone().add(0, 0.75f, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                final int timer = (int) PlayerScore.WITHDRAW_TIMER.getScore(plugin, player);
                //Player has manually left
                if (!player.getWorld().equals(void_world)) {
                    this.cancel();
                    return;
                }

                //Exit - out of time
                if (timer <= 0) {
                    this.cancel();

                    Location exitLoc;
                    if (lineOfSightNMS.hasLineOfSight(player, voidLoc)) {
                        exitLoc = player.getLocation();
                        exitLoc.setWorld(location.getWorld());
                    }
                    else {
                        exitLoc = location.clone();
                        exitLoc.setYaw(player.getLocation().getYaw());
                        exitLoc.setPitch(player.getLocation().getPitch());
                    }

                    exitWithdraw(player, exitLoc);
                    return;
                }

                // Withdraw continuous behaviour ----------
                //Healing
                if (timer % 70 == 0) {
                    AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    double maxHealth = (attribute != null ? attribute.getValue() : 20);
                    double healing = (withdraw_level > 1 ? 1.5 : 1);

                    if (player.getHealth() < maxHealth && player.getHealth() > 0) {
                        player.setHealth(Math.max(Math.min(player.getHealth() + healing, maxHealth), 0));
                    }

                    double thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
                    if (thirst >= 50) {
                        healing /= (thirst >= 80 ? 2.0 : 4.0);

                        PlayerScore.TOXICITY.setScore(plugin, player, Math.max((double) PlayerScore.TOXICITY.getScore(plugin, player) - healing, 0));
                    }
                }

                //Entrance particle
                player.spawnParticle(Particle.ENCHANTMENT_TABLE, voidLoc, 10, 0, 1, 0);

                //Tether particle
                Color tetherColor = lineOfSightNMS.hasLineOfSight(player, voidLoc) ? Color.fromRGB(255, 0, 255) : Color.GRAY;
                Particle.DustOptions tetherOptions = new Particle.DustOptions(tetherColor, 0.5F);
                particleShapes.line(player, Particle.REDSTONE, raisedVoidLoc, player.getLocation().add(0, 1, 0), 0, tetherOptions, 0.5f);

                //Prevent mana regen and tick timer
                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
                PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, timer - 1);
            }
        }.runTaskTimer(plugin, 0, 0);

        //Boundary particle
        new BukkitRunnable() {

            @Override
            public void run() {
                //Ended
                if (!player.getWorld().equals(void_world)) {
                    this.cancel();
                    return;
                }

                particleShapes.sphereDelayed(player, Particle.REDSTONE, voidLoc, (withdraw_level == 2 ? 30 : 15), 0, new Particle.DustOptions(Color.GRAY, 1), 2, false);
            }
        }.runTaskTimer(plugin, 0, 40);

        //Only subtracts Mana cost if the player is the initiator
        if (subtractCost) {
            final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            PlayerScore.MANA.setScore(plugin, player, mana - cost);
        }
    }

    /**
     * This function generates the clone pocket dimension in the Withdraw skill.
     * Due to its heavy performance impact, the task can be split among muliple game ticks,
     * with a limit of how much work can be done per tick.
     * @author Thomas Tran
     *
     * @param player Player who initiated this generation
     * @param center Location where Withdraw generation is centered around
     * @param radius Max radius of the generation
     * @param minInitialLayers Minimum layers that must be generated before the player enters. Must be larger than 0.
     * @param maxBlocksPerTick Maximum amount of blocks that can be generated per tick after the first tick. Must be larger than 0.
     * */
    public void generateWithdrawClone(final Player player, final Location center, final int radius, final int minInitialLayers, final int maxBlocksPerTick) {
        //This method will use iterative BFS floodfill to clone all the visible blocks within a radius

        /* Steps:
        * 1. Start at center. Add all of its neighbours to a queue
        * 2. Save the current queue size -> intraLayer
        * 3. Begin loop; repeat until travelled > radius OR queue is empty:
        * 3a. Dequeue location. If solid, set Withdraw solid.
        * 3b. If air, set Withdraw air and enqueue all its neighbours.
        * 3c. Add location to visited
        * 3d. Subtract one from intraLayer.
        * 3e. If intraLayer == 0:
        * 3ea. Add 1 to travelled
        * 3eb. Save the current queue size -> intraLayer
        * */

        Queue<Location> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        final String[] encoded = new String[1];
        final int[] intraLayer = new int[1];
        final int[] travelled = {0};

        final Block[] block = {center.getBlock()};
        final Location[] sourceLoc = {center};
        Location voidLoc = sourceLoc[0].clone();
        voidLoc.setWorld(void_world);

        //Add neighbors of center to queue
        for (BlockFace adj : this.adjacentFaces) {
            queue.add(block[0].getRelative(adj).getLocation());
        }
        intraLayer[0] = queue.size();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (travelled[0] > radius || queue.isEmpty() || player == null || !player.getWorld().equals(void_world) || player.isDead() || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                int count = 0;

                while ((travelled[0] == 0 && travelled[0] < minInitialLayers) || (travelled[0] != 0 && count < maxBlocksPerTick)) {
                    sourceLoc[0] = queue.poll();
                    encoded[0] = encodeLocation(sourceLoc[0]);
                    if (visited.contains(encoded[0])) continue;

                    block[0] = sourceLoc[0].getBlock();

                    voidLoc.setX(sourceLoc[0].getX());
                    voidLoc.setY(sourceLoc[0].getY());
                    voidLoc.setZ(sourceLoc[0].getZ());

                    //Add location to visited
                    visited.add(encoded[0]);

                    //If solid: set Withdraw block to solid
                    if (block[0].getType().isSolid()) {
                        voidLoc.getBlock().setType(MakeDescensionStage.getVoidMat(random));
                    }

                    //If air: set Withdraw block to air and enqueue all neighbours
                    else {
                        voidLoc.getBlock().setType(Material.AIR);

                        //Add all neighbours
                        for (BlockFace adj : adjacentFaces) {
                            Location adjLoc = block[0].getRelative(adj).getLocation();
                            if (!visited.contains(encodeLocation(adjLoc))) queue.add(adjLoc);
                        }
                    }

                    intraLayer[0]--;

                    //Determine if layer is completed
                    if (intraLayer[0] <= 0) {
                        travelled[0]++;
                        intraLayer[0] = queue.size();
                    }

                    ++count;
                }

            }
        }.runTaskTimer(plugin, 0, 1);

    }

    /**
     * Forces player to stay within the generated radius.
     * Also prevents them from falling out of the world
     * @author Thomas Tran
     * */
    @EventHandler
    public void confinePlayer (PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        if (!player.getWorld().equals(void_world)) return;

        // Get player's entrance location and current location
        final double originX = (double) PlayerScore.WITHDRAW_X.getScore(plugin, player);
        final double originY = (double) PlayerScore.WITHDRAW_Y.getScore(plugin, player);
        final double originZ = (double) PlayerScore.WITHDRAW_Z.getScore(plugin, player);
        final Location origin = new Location(void_world, originX, originY, originZ);
        final Location location = player.getLocation();

        //Determine max distance based on Withdraw level
        final int withdrawLevel = (int) PlayerScore.WITHDRAW_LEVEL.getScore(plugin, player);
        final double distSq = Math.pow((withdrawLevel == 2 ? 30 : 15), 2);

        //Fell out of world or exited bounds -> return to entrance
        if (location.getY() <= 0 || (location.distanceSquared(origin) > distSq)) {
            player.teleport(origin, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    private String encodeLocation(final Location location) {
        return (location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
    }

    public void exitWithdraw(Player player, Location returnLocation) {
        final int withdraw_timer = (int) PlayerScore.WITHDRAW_TIMER.getScore(plugin, player);

        player.setFallDistance(0);
        if (withdraw_timer != -255) {
            //Teleport and sound to player
            player.teleport(returnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.playSound(returnLocation, "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.5F, 1);

            //Play sfx/vfx to nearby sorcerers
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(returnLocation.getWorld()) && powered.getLocation().distanceSquared(returnLocation) < 100) {
                        powered.playSound(returnLocation, "custom.supernatural.withdraw.leave", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, returnLocation, 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GRAY, 1));
                    }
                }
            }
        }
        if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);

        PreventDragon.preventDragonSpawn(void_world);
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
