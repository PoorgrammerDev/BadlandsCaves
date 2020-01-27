package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

public class possessionIndicatorRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public possessionIndicatorRunnable (BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack possess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true));
        if (!player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
            return;
        }
        if (player.getMetadata("in_possession").get(0).asBoolean()) return;

        World world = player.getWorld();
        RayTraceResult result = world.rayTraceEntities(player.getEyeLocation().add(0.5,0.5,0.5),player.getLocation().getDirection(),5);


        if (result != null && result.getHitEntity() != null && result.getHitEntity() instanceof LivingEntity && !(result.getHitEntity() instanceof Player) && !(result.getHitEntity() instanceof EnderDragon) && !(result.getHitEntity() instanceof Wither)) {
            LivingEntity entity = (LivingEntity) result.getHitEntity();
            Location location = entity.getLocation();

            for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI / 4.0) {
                double x = Math.cos(theta);
                double z = Math.sin(theta);
                for (double y = 0; y < entity.getHeight(); y += 0.3) {
                    location.add(x, y, z);
                    player.spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.GREEN, 1));
                    location.subtract(x, y, z);
                }
            }
        }
    }
}
