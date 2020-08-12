package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

public class NaturalThirstDecrease implements Listener {
    private final Random random = new Random();
    private final BadlandsCaves plugin;
    private final World descension;
            private final World reflection;
            private final World backrooms;
    public NaturalThirstDecrease(BadlandsCaves plugin) {
        this.plugin = plugin;
        descension = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        reflection = plugin.getServer().getWorld(plugin.getReflectionWorldName());
        backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
    }


    @EventHandler
    public void decrease_thirst (PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        final Player player = event.getPlayer();

        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if (player.isDead()) return;

        final World world = player.getWorld();
        if (world.equals(descension) || world.equals(reflection) || world.equals(backrooms)) return;
        final boolean moved_x = (Math.abs(event.getTo().getX() - event.getFrom().getX()) > 0);
        final boolean moved_y = (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0);
        final boolean moved_z = (Math.abs(event.getTo().getZ() - event.getFrom().getZ()) > 0);
        final boolean moved = moved_x || moved_y || moved_z;
        final boolean sprint = player.isSprinting();
        final boolean sneak = player.isSneaking();
        final boolean climb = moved_y && (player.getLocation().getBlock().getType().equals(Material.LADDER) || player.getLocation().getBlock().getType().equals(Material.VINE));
        final double current_thirst_sys = (double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player);

        if (moved) {
            //endurance cancel
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (has_powers) {
                int endurance_level = (int) PlayerScore.ENDURANCE_LEVEL.getScore(plugin, player);
                if (endurance_level > 0) {
                    int endurance_rand_cancel = random.nextInt(100);
                    if ((endurance_level == 1 && endurance_rand_cancel < 25) || (endurance_level == 2 && endurance_rand_cancel < 50)) {
                        return;
                    }
                }
            }

            double cost;
            if (climb) {
                cost = 3;
            }
            else if (sprint) {
                cost = 1.5;
            }
            else if (sneak) {
                cost = 0.5;
            }
            else {
                cost = 1;
            }

            if (world.getEnvironment().equals(World.Environment.NETHER)) {
                cost *= 2;
            }

            PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, current_thirst_sys + cost);
        }

        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final int threshold = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.threshold_thirst_sys" : "pre_hardmode_values.threshold_thirst_sys");
        if ((double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player) >= threshold) {
            PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, 0);

            double current_thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
            PlayerScore.THIRST.setScore(plugin, player, current_thirst - 0.1);
        }
    }

}
