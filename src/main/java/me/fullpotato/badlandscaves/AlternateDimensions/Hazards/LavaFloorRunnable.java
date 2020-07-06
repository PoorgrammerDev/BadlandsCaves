package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class LavaFloorRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards dims;

    public LavaFloorRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.dims = new EnvironmentalHazards(plugin);
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (dims.isDimension(world) && dims.hasHazard(world, EnvironmentalHazards.Hazard.LAVA_FLOOR)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (!player.isSneaking() && ((LivingEntity) player).isOnGround() && !player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                        player.setFireTicks(20);
                    }
                }
            }
        }
    }
}
