package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class ParanoiaRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards hazards;
    private final ZombieBossBehavior locationFinder;
    private final Random random = new Random();

    public ParanoiaRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.hazards = new EnvironmentalHazards(plugin);
        this.locationFinder = new ZombieBossBehavior(plugin);
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());

        for (Player player : players) {
            World world = player.getWorld();
            if (hazards.isDimension(world) && hazards.hasHazard(world, EnvironmentalHazards.Hazard.PARANOIA)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, false, false));
                    if (random.nextInt(100) < 25) {
                        player.playSound(locationFinder.getNearbyLocation(player.getLocation(), random, 5), Sound.values()[random.nextInt(Sound.values().length)], SoundCategory.AMBIENT, 0.7F, 1);
                    }

                    if (random.nextInt(100) < 5) {
                        FakePlayerNMS fakePlayerManager = plugin.getFakePlayerNMS();
                        Location fakePlayerLocation = locationFinder.getNearbyLocation(player.getLocation(), random, 10);

                        if (fakePlayerLocation != null && fakePlayerLocation.distanceSquared(player.getLocation()) > 25) {
                            fakePlayerLocation.setDirection(player.getLocation().subtract(fakePlayerLocation.clone()).toVector());

                            Player copyOf = players.get(random.nextInt(players.size()));
                            boolean copyItems = random.nextBoolean();

                            Player fakePlayer = fakePlayerManager.summonFakePlayer(fakePlayerLocation, copyOf, player, null, copyItems);
                            fakePlayerManager.move(fakePlayerLocation, fakePlayer, player, true);

                            final int lifespan = random.nextInt(200) + 100;
                            int[] ticker = {0};
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    double dist = fakePlayerLocation.distanceSquared(player.getLocation());
                                    if ((player.isDead() || !player.isOnline() || !player.getWorld().equals(fakePlayerLocation.getWorld())) || (ticker[0] > lifespan && dist > 25)) {
                                        this.cancel();
                                        fakePlayerManager.remove(fakePlayer);
                                    }
                                    else {
                                        if (dist < 9 && random.nextInt(100) < 10) {
                                            fakePlayerManager.damage(fakePlayer, player, false);
                                        }

                                        if (copyItems) {
                                            EntityEquipment equipment = copyOf.getEquipment();
                                            if (equipment != null) {
                                                fakePlayerManager.giveHandItem(fakePlayer, player, equipment.getItemInMainHand());
                                            }
                                        }

                                        fakePlayerLocation.setDirection(player.getLocation().subtract(fakePlayerLocation.clone()).toVector());
                                        fakePlayerManager.move(fakePlayerLocation, fakePlayer, player, true);
                                        ticker[0]++;
                                    }
                                }
                            }.runTaskTimerAsynchronously(plugin, 0, 0);
                        }
                    }
                }
            }
        }
    }
}
