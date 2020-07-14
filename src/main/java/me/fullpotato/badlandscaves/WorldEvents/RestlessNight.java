package me.fullpotato.badlandscaves.WorldEvents;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.TitleEffects;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class RestlessNight /*extends BukkitRunnable*/ implements Listener {
    private final BadlandsCaves plugin;
    private final World mainWorld;
    private final Random random = new Random();

    public RestlessNight(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.mainWorld = plugin.getServer().getWorld(plugin.getMainWorldName());
    }

    //@Override
    public void run() {
        if (mainWorld.getTime() == 12300) {
            final int chaos = plugin.getSystemConfig().getInt("chaos_level");
            final double chance = Math.pow(1.04, chaos) - 1;
            if (random.nextInt(100) < chance) {
                plugin.getSystemConfig().set("world_event", WorldEvents.RESTLESS_NIGHT.name());
                plugin.getServer().broadcastMessage(ChatColor.of("#000cb3") + "A Restless Night has fallen.");
                TitleEffects titleEffects = new TitleEffects(plugin);

                mainWorld.getPlayers().forEach(player -> {
                    titleEffects.sendDecodingTitle(player, "RESTLESS NIGHT", ChatColor.of("#000cb3") + ChatColor.BOLD.toString(), "", "", 0, 20, 10, 2, false);
                });


            }
        }
        else {

        }
    }

    //@EventHandler
    public void spawnAtNight (CreatureSpawnEvent event) {
        final LivingEntity entity = event.getEntity();
        if (entity instanceof Mob) {
            Mob mob = (Mob) entity;
            final World world = entity.getWorld();
            if (world.equals(mainWorld)) {
                long time = world.getTime();
                if (time >= 12300 && time <= 23850) {
                    mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(99999);

                    if (mob.getTarget() == null) {
                        double minDist = Double.MAX_VALUE;
                        Player target = null;
                        for (Player player : world.getPlayers()) {
                            if (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL)) {
                                double dist = player.getLocation().distanceSquared(mob.getLocation());
                                if (dist < minDist) {
                                    minDist = dist;
                                    target = player;
                                }
                            }
                        }

                        if (target != null) {
                            mob.setTarget(target);
                        }
                    }
                }
            }
        }
    }
}
