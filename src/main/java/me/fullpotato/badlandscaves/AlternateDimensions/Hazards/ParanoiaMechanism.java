package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class ParanoiaMechanism extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards hazards;
    private final Random random;

    private HashMap<UUID, HashSet<Vector>> map;
    private HashMap<UUID, Vector> centerMap;

    public ParanoiaMechanism(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.hazards = new EnvironmentalHazards(plugin, random);
        this.random = random;
        this.map = new HashMap<>();
        this.centerMap = new HashMap<>();
    }

    @EventHandler
    public void unloadWorld(PlayerChangedWorldEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        if (map.containsKey(uuid)) map.remove(uuid);
        if (centerMap.containsKey(uuid)) centerMap.remove(uuid);
    }

    @EventHandler
    public void changeGamemode(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.CREATIVE && event.getNewGameMode() != GameMode.SPECTATOR) return;

        removeBounds(event.getPlayer());
    }

    @EventHandler
    public void resetAll(PluginDisableEvent event) {
        Bukkit.broadcastMessage("message");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removeBounds(player);
        }
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        
        for (Player player : players) {
            final World world = player.getWorld();

            //Guard statements - relevant dimension & in right gamemode
            if (!hazards.isDimension(world) || !hazards.hasHazard(world, EnvironmentalHazards.Hazard.PARANOIA)) continue;
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;

            //Bounding box effect does not work well when the player is in free fall
            //Player can collide with it and take fall damage even if the block isn't actually there server-side
            //Therefore, if the player is falling too fast, fallback to blindness effect
            Vector velocity = player.getVelocity();
            if (velocity.getY() <= -1.0f) {
                removeBounds(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
            }
            //Limit the player's vision with a box of faux blocks
            else {
                limitVision(player, 15);
            }
            

            //Play random sounds around the player
            if (random.nextInt(100) < 5) {
                soundEffects(player);
            }

        }
    }

    /**
     * Removes a bounding box for a player, if there is any stored
     */
    public void removeBounds(Player player) {
        final UUID uuid = player.getUniqueId();
        if (this.map.containsKey(uuid)) {
            final World world = player.getWorld();

            if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.PARANOIA)) {
                for (Vector vector : this.map.get(uuid)) {
                    //Reset block data
                    Location location = vector.toLocation(world);
                    player.sendBlockChange(location, location.getBlock().getBlockData());
                }
            }

            this.map.remove(uuid);
        }
    }

    /**
     * Places an occluding box around the player to limit their vision (client-side packets)
     * @param player Player
     * @param distance Radius from the player
     */
    private void limitVision(Player player, int distance) {
        final World world = player.getWorld();
        final Vector origin = player.getEyeLocation().toVector();
        final UUID uuid = player.getUniqueId();
        final int distanceSq = (distance * distance) / 2;
        final BlockData blockData = Material.BLACK_CONCRETE.createBlockData();

        if (this.centerMap.containsKey(uuid)) {
            final Vector oldOrigin = this.centerMap.get(uuid);

            if (origin.distanceSquared(oldOrigin) < distanceSq) {
                return;
            }
        }

        final HashSet<Vector> blocks = new HashSet<>();

        //XY plane (z is constant)
        for (int x = -distance; x <= distance; x++) {
            for (int y = -distance; y <= distance; y++) {
                blocks.add(new Vector(x, y, -distance).add(origin));
                blocks.add(new Vector(x, y, distance).add(origin));
            }
        }

        //XZ plane (y is constant)
        for (int x = -distance; x <= distance; x++) {
            for (int z = -distance; z <= distance; z++) {
                blocks.add(new Vector(x, -distance, z).add(origin));
                blocks.add(new Vector(x, distance, z).add(origin));
            }
        }

        //YZ plane (x is constant)
        for (int y = -distance; y <= distance; y++) {
            for (int z = -distance; z <= distance; z++) {
                blocks.add(new Vector(-distance, y, z).add(origin));
                blocks.add(new Vector(distance, y, z).add(origin));
            }
        }

        HashSet<Vector> existing;
        if (map.containsKey(uuid)) {
            existing = new HashSet<>(map.get(uuid));

            for (final Vector vector : existing) {
                if (blocks.contains(vector)) {
                    blocks.remove(vector);
                }
                else {
                    map.get(uuid).remove(vector);

                    //Reset block data
                    Location location = vector.toLocation(world);
                    player.sendBlockChange(location, location.getBlock().getBlockData());
                }
            }
        }
        else {
            map.put(uuid, new HashSet<>());
        }

        existing = map.get(uuid);
        for (final Vector vector : blocks) {
            Location location = vector.toLocation(world);
            if (location.getBlock().getType().isOccluding()) continue;

            existing.add(vector);
            player.sendBlockChange(vector.toLocation(world), blockData);
        }

        this.centerMap.put(uuid, origin);
    }

    /**
     * Plays random sound effects around the player.
     * Certain groups of sound effects can play in a sequence instead, to be more believable.
     */
    private void soundEffects(Player player) {
        final Sound sound = Sound.values()[random.nextInt(Sound.values().length)];
        final String name = sound.name();
        final Location location = player.getLocation().add(getRandomOffset(3, 7));

        // SPECIAL BEHAVIOUR (50% chance) =======
        if (random.nextBoolean()) {
            //ENTITY DAMAGE OVERRIDE - Play continuously, and then end in a death sound
            if (name.endsWith("HURT")) {
                Sound[] list = new Sound[random.nextInt(9) + 1];
                Arrays.fill(list, 0, list.length, sound);

                //Tries to fetch the corresponding death sound
                try {
                    list[list.length - 1] = Sound.valueOf(name.substring(0, name.length() - 5) + "_DEATH");
                } catch (IllegalArgumentException e) {
                    //Ignore, the final sound will remain a hurt sound
                }

                playSoundRepeatedly(player, list, location, random.nextInt(40) + 10, false, 1);
                return;
            }

            //ENTITY AMBIENT OVERRIDE - Play repeatedly but slower
            else if (name.startsWith("ENTITY") && name.endsWith("AMBIENT")) {
                Sound[] list = new Sound[random.nextInt(9) + 1];
                Arrays.fill(list, 0, list.length, sound);

                playSoundRepeatedly(player, list, location, random.nextInt(60) + 60, true, 1);
                return;
            }

            //BLOCK HIT - Play continuously, litter break sounds in between
            else if (name.startsWith("BLOCK") && name.endsWith("HIT")) {
                Sound[] list = new Sound[random.nextInt(20) + 4];
                Arrays.fill(list, 0, list.length, sound);

                //Find the corresponding break sound
                Sound breakSound = null;
                try {
                    breakSound = Sound.valueOf(name.substring(0, name.length() - 4) + "_BREAK");
                } catch (IllegalArgumentException e) {
                    //Ignore, there will only be hit sounds
                }

                //Replace every {x} sound with the break sound
                if (breakSound != null) {
                    int breakRatio = random.nextInt(2) + 2;
                    for (int i = 0; i < list.length; i += breakRatio) {
                        list[i] = breakSound;
                    }
                }

                playSoundRepeatedly(player, list, location, 5, true, 0.75f);
                return;
            }

            //FOOTSTEPS - Play continuously, randomized location offsets
            else if (name.startsWith("BLOCK") && name.endsWith("STEP")) {
                Sound[] list = new Sound[random.nextInt(20) + 10];
                Arrays.fill(list, 0, list.length, sound);

                playSoundRepeatedly(player, list, location, 6, true, 0.5f);
                return;
            }
        }

        //GUARANTEED SPECIAL BEHAVIOUR =======

        // CHEST OPEN/CLOSE OVERRIDE - Always open first, close after
        final boolean open = name.endsWith("OPEN");
        final boolean closed = name.endsWith("CLOSE");

        if (open || closed) {
            try {
                final Sound[] list = {
                    open ? sound : Sound.valueOf(name.substring(0, name.length() - 6) + "_OPEN"),
                    closed ? sound : Sound.valueOf(name.substring(0, name.length() - 5) + "_CLOSE"),
                };

                final int delay = (name.contains("DOOR") ? random.nextInt(10) : random.nextInt(60)) + 20;

                playSoundRepeatedly(player, list, location, delay, false, 1);
                return;
            }
            catch (IllegalArgumentException e) {
                //Ignore, play sound normally
            }
                
        }
        
        //DEFAULT BEHAVIOUR ======
        player.playSound(location, sound, SoundCategory.MASTER, random.nextFloat(), 1);
    }

    private Vector getRandomOffset(int minRange, int maxRange) {
        return new Vector(
            random.nextInt(maxRange - minRange) + minRange,
            random.nextInt(maxRange - minRange) + minRange,
            random.nextInt(maxRange - minRange) + minRange
        );
    }

    private void playSoundRepeatedly(Player player, Sound[] sound, Location origin, int delay, boolean randomOffset, float volumeFactor) {
        final int[] count = {0};
        final float volume = ((random.nextFloat() + 1.0f) / 2.0f) * volumeFactor;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] >= sound.length) {
                    this.cancel();
                    return;
                }

                Location location = randomOffset ? origin.clone().add(getRandomOffset(0, 3)) : origin;
                player.playSound(location, sound[count[0]], SoundCategory.MASTER, volume, 1);

                count[0]++;
            }
        }.runTaskTimer(plugin, 0, delay);

    }

}
