package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.possessionNMS;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Random;

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
        RayTraceResult result = world.rayTraceEntities(player.getEyeLocation().add(0.5,0.5,0.5),player.getLocation().getDirection(),10);


        if (result != null && result.getHitEntity() != null && result.getHitEntity() instanceof LivingEntity && !(result.getHitEntity() instanceof Player) && !(result.getHitEntity() instanceof EnderDragon) && !(result.getHitEntity() instanceof Wither)) {
            LivingEntity entity = (LivingEntity) result.getHitEntity();
            boolean target_already_pos = entity.hasMetadata("possessed") && entity.getMetadata("possessed").get(0).asBoolean();
            if (target_already_pos) return;

            possessionNMS nms = new possessionNMS(player);
            nms.setIndicator(entity);

            Location location = entity.getLocation();
            makeParticleLine(location, location.getY() + entity.getHeight());

            location = entity.getLocation();
            makeParticleCircle(location, entity.getWidth() * 2);

            location.add(0, entity.getHeight(), 0);
            makeParticleCircle(location, entity.getWidth() * 2);
        }
    }


    public void makeParticleCircle (Location location, double radius) {
        for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI / 10) {
            double x = (radius / 2) * Math.cos(theta);
            double z = (radius / 2) * Math.sin(theta);

            location.add(x, 0, z);
            spawnParticle(location);
            location.subtract(x, 0, z);
        }
    }

    public void spawnParticle(Location location) {
        Random random = new Random();
        int rand_r = random.nextInt(76) + 6;
        int rand_g = random.nextInt(11) + 104;
        int rand_b = random.nextInt(115);

        player.spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.fromRGB(rand_r, rand_g, rand_b), 1));
    }

    public void makeParticleLine (Location location, double y_top) {
        if (location.getY() <= y_top) {
            for (double y = location.getY(); y < y_top; y += 0.1) {
                location.setY(y);
                spawnParticle(location);
            }
        }
    }
}
