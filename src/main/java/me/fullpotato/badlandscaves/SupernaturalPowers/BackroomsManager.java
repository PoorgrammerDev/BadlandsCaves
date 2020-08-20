package me.fullpotato.badlandscaves.SupernaturalPowers;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BackroomsManager implements Listener {
    private final BadlandsCaves plugin;
    private final World backrooms;
    private final InventorySerialize inventorySerialize;
    private final FakePlayerNMS fakePlayerManager;

    public BackroomsManager(BadlandsCaves plugin, InventorySerialize inventorySerialize) {
        this.plugin = plugin;
        backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
        fakePlayerManager = plugin.getFakePlayerNMS();
        this.inventorySerialize = inventorySerialize;
    }

    public enum BackroomsType {
        BACKROOMS,
        DARKROOMS
    }

    public void enterBackRooms(Player player, Random random) {
        enterBackRooms(player, random, null);
    }

    public void enterBackRooms (Player player, Random random, @Nullable BackroomsType type) {
        if (player.getWorld().equals(backrooms)) return;

        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".backrooms_saved_location", player.getLocation().serialize());
        inventorySerialize.saveInventory(player, "backrooms");
        player.getInventory().clear();

        if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 99999, 127, false, false));

        boolean special_surprise = ((type != null && !type.equals(BackroomsType.BACKROOMS) && (type.equals(BackroomsType.DARKROOMS))) || (((byte) PlayerScore.HAS_SEEN_BACKROOMS.getScore(plugin, player) == 1) && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1 && random.nextInt(100) < 25));

        if (special_surprise) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0, false, false));
            player.teleport(backrooms.getSpawnLocation().subtract(0, 20, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

            player.playSound(getNearbyLocation(player.getLocation(), random, 5, 1), "custom.darkrooms_water_drip", SoundCategory.AMBIENT, 2, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && player.getWorld().equals(backrooms)) {
                        player.playSound(getNearbyLocation(player.getLocation(), random, 5, 1), "custom.darkrooms_ambience", SoundCategory.AMBIENT, 0.5F, 1);
                    }
                    else {
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 1200);


            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && player.getWorld().equals(backrooms)) {
                        if (random.nextBoolean()) {
                            player.playSound(getNearbyLocation(player.getLocation(), random, 5, 1), "custom.darkrooms_whispers", SoundCategory.AMBIENT, 0.2F, 1);
                        }
                    }
                    else {
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 120);

        }
        else {
            player.teleport(backrooms.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && player.getWorld().equals(backrooms)) {
                        player.playSound(player.getLocation(), "custom.backrooms", SoundCategory.AMBIENT, 0.6F, 1);
                    }
                    else {
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 185);
        }

        PlayerScore.HAS_SEEN_BACKROOMS.setScore(plugin, player, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (special_surprise) {
                    cursedModels(player, random);
                }
                else {
                    summonTarget(player, random);
                }
            }
        }.runTaskLater(plugin, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getWorld().equals(backrooms)) {
                    if (random.nextBoolean()) {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0, false, false));
                            }
                        }.runTaskLater(plugin, 3);
                    }
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 200, 200);

        PlayerScore.BACKROOMS_TIMER.setScore(plugin, player, random.nextInt(60) + 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getWorld().equals(backrooms)) {
                    int timer = (int) PlayerScore.BACKROOMS_TIMER.getScore(plugin, player);
                    if (timer > 0) {
                        PlayerScore.BACKROOMS_TIMER.setScore(plugin, player, timer - 1);
                    }
                    else {
                        leaveBackRooms(player);
                        this.cancel();
                    }
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void leaveBackRooms (Player player) {
        if (!player.getWorld().equals(backrooms)) return;

        player.stopSound("custom.darkrooms_ambience");
        player.stopSound("custom.darkrooms_water_drip");
        player.stopSound("custom.darkrooms_whispers");
        player.stopSound("custom.backrooms");

        PlayerScore.BACKROOMS_TIMER.setScore(plugin, player, 0);

        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);

        inventorySerialize.loadInventory(player, "backrooms", true, true);

        Location location = Location.deserialize(plugin.getSystemConfig().getConfigurationSection("player_info." + player.getUniqueId() + ".backrooms_saved_location").getValues(true));
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".backrooms_saved_location", null);
    }

    public void summonTarget(Player player, Random random) {
        ArrayList<Player> online = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        final Player fake = online.get(random.nextInt(online.size()));

        Location spawnLoc = getNearbyLocation(player.getLocation(), random, 10, 5);
        final Player cloned = fakePlayerManager.summonFakePlayer(spawnLoc, fake, player, null);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getWorld().equals(backrooms)) {
                    final Location player_loc = player.getLocation();
                    Location cloned_loc = cloned.getLocation();
                    cloned_loc.setWorld(backrooms);

                    if (player_loc.distanceSquared(cloned_loc) < 400) {
                        double x_offset = player_loc.getX() > cloned_loc.getX() ? -0.5 : 0.5;
                        double z_offset = player_loc.getZ() > cloned_loc.getZ() ? -0.5 : 0.5;

                        fakePlayerManager.move(cloned_loc.add(x_offset, 0, z_offset), cloned, player, true);
                    }

                }
                else {
                    this.cancel();
                    fakePlayerManager.remove(cloned);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 0);
    }

    public void cursedModels (Player player, Random random) {
        final ArrayList<Player> all_online = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        final int clone_count = 100;
        final Map<Player, Boolean> clones = new HashMap<>();

        for (int i = 0; i < clone_count; i++) {
            Location spawnLoc = enforceCursedLocationViable(player, random, clones.keySet());

            randomRotation(spawnLoc, random);
            clones.put(fakePlayerManager.summonFakePlayer(spawnLoc, all_online.get(random.nextInt(all_online.size())), player, "§r"),
                    random.nextInt(100) < 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getWorld().equals(backrooms)) {
                    for (Player clone : clones.keySet()) {
                        Location cloneCurrentLoc = clone.getLocation();
                        cloneCurrentLoc.setWorld(backrooms);

                        final double dist = cloneCurrentLoc.distanceSquared(player.getLocation());
                        if (dist > 1225) {
                            Location cloneMoveLoc = enforceCursedLocationViable(player, random, clones.keySet());
                            cloneMoveLoc.setY(player.getLocation().getY());
                            randomRotation(cloneMoveLoc, random);

                            fakePlayerManager.move(clone.getLocation().add(0, 10, 0), clone, player, true);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    fakePlayerManager.move(cloneMoveLoc.clone().add(0, 10, 0), clone, player, true);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            fakePlayerManager.move(cloneMoveLoc, clone, player, true);
                                        }
                                    }.runTaskLater(plugin, 5);
                                }
                            }.runTaskLater(plugin, 5);
                        }
                        else if (dist < 25 && clones.get(clone)) {
                            cloneCurrentLoc = clone.getLocation();
                            cloneCurrentLoc.setDirection(player.getLocation().subtract(cloneCurrentLoc.clone()).toVector());
                            fakePlayerManager.move(cloneCurrentLoc, clone, player, true);
                        }
                    }
                }
                else {
                    for (Player clone : clones.keySet()) {
                        fakePlayerManager.remove(clone);
                        this.cancel();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 60);


        for (Player online : all_online) {
            fakePlayerManager.remove(fakePlayerManager.summonFakePlayer(player.getLocation().add(0, 20, 0), online, player, null));
        }
    }

    public Location getNearbyLocation (final Location location, final Random random, final int range, final int minimum_dist) {
        final int max_tries = 100;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int random_bound_range = (range * 2) + 1;
        final double distance_range = Math.pow(range - 1, 2);
        final double min = Math.pow(minimum_dist - 1, 2);

        int test_x, test_z;
        Location test_location;

        for (int a = 0; a < max_tries; a++) {
            test_x = random.nextInt(random_bound_range) + (x - range);
            test_z = random.nextInt(random_bound_range) + (z - range);

            test_location = new Location(location.getWorld(), test_x, y, test_z);
            if (location.distanceSquared(test_location) >= (min) && location.distanceSquared(test_location) <= distance_range) {
                if (test_location.getBlock().isPassable()) {
                    return test_location;
                }
            }
        }
        return null;
    }

    private Location enforceCursedLocationViable (Player player, Random random, Collection<Player> clones) {
        Location location;
        boolean isSpacedOut;
        int tries = 0;
        LineOfSightNMS nms = plugin.getLineOfSightNMS();
        do {
            location = getNearbyLocation(player.getLocation(), random, 30, 10);
            tries++;
            isSpacedOut = isSpacedOut(location, clones);
        } while (nms.hasLineOfSight(player, location) && tries < 100 && !isSpacedOut);

        return location;
    }

    private boolean isSpacedOut(Location location, Collection<Player> clones) {
        for (Player clone : clones) {
            Location clone_loc = clone.getLocation();
            clone_loc.setWorld(backrooms);
            if (clone_loc.distanceSquared(location) < 4) return false;
        }
        return true;
    }

    public void randomRotation(Location location, Random random) {
        location.setYaw(random.nextInt(360));
        location.setPitch(random.nextInt(180) - 90);
    }

    @EventHandler
    public void betweenDimensions(PlayerChangedWorldEvent event) {
        final Random random = new Random();
        if (random.nextInt(1000) < 5) {
            final Player player = event.getPlayer();
            final World from = event.getFrom();
            final World to = player.getWorld();

            final ArrayList<World> blacklisted = new ArrayList<>();
            blacklisted.add(backrooms);
            blacklisted.add(plugin.getServer().getWorld(plugin.getWithdrawWorldName()));
            blacklisted.add(plugin.getServer().getWorld(plugin.getReflectionWorldName()));
            blacklisted.add(plugin.getServer().getWorld(plugin.getDescensionWorldName()));
            blacklisted.add(plugin.getServer().getWorld(plugin.getChambersWorldName()));


            if (!blacklisted.contains(from) && !blacklisted.contains(to)) {
                enterBackRooms(player, random);
            }
        }
    }

    @EventHandler
    public void cancelChat (AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (player.getWorld().equals(backrooms)) {
            if (player.getLocation().getY() < 55) {
                String displayName = player.getDisplayName();
                player.setDisplayName("§r");
                event.getRecipients().removeIf(player1 -> !(player1.equals(player)));
                player.setDisplayName(displayName);
            }
            else {
                event.getRecipients().removeIf(player1 -> !(player1.equals(player)));
            }
        }
        else {
            event.getRecipients().removeIf(recipient -> recipient.getWorld().equals(backrooms));
        }
    }

    @EventHandler
    public void reLog (PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.getWorld().equals(backrooms)) {
            boolean darkrooms = player.getLocation().getY() < 55;
            int backrooms_timer = (int) PlayerScore.BACKROOMS_TIMER.getScore(plugin, player);
            leaveBackRooms(player);
            if (backrooms_timer > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (darkrooms) {
                            enterBackRooms(player, new Random(), BackroomsType.DARKROOMS);
                        }
                        else {
                            enterBackRooms(player, new Random(), BackroomsType.BACKROOMS);
                        }
                        PlayerScore.BACKROOMS_TIMER.setScore(plugin, player, backrooms_timer);
                    }
                }.runTaskLater(plugin, 2);
            }
        }
    }
}
