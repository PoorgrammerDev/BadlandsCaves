package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class BlazeBuff implements Listener {
    private final BadlandsCaves plugin;
    private final World chambers;
    public BlazeBuff(BadlandsCaves bcav) {
        plugin = bcav;
        chambers = plugin.getServer().getWorld(plugin.getChambersWorldName());
    }

    @EventHandler
    public void HMBlaze (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Blaze)) return;
        if (event.getEntity().getWorld().equals(chambers)) return;

        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!isHardmode) return;

        Blaze blaze = (Blaze) event.getEntity();
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, 2, true, true));
    }

    @EventHandler
    public void fireballExplode (ProjectileHitEvent event) {
        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!isHardmode) return;

        if (event.getEntity() instanceof SmallFireball) {
            SmallFireball fireball = (SmallFireball) event.getEntity();
            if (fireball.getShooter() instanceof Blaze) {
                Blaze blaze = (Blaze) fireball.getShooter();
                if (blaze.getTarget() instanceof Player) {
                    final Random random = new Random();
                    final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                    final double chance = Math.pow(1.045, chaos) - 1;
                    if (random.nextInt(100) >= chance) return;

                    Player player = (Player) blaze.getTarget();
                    Location fireball_loc = fireball.getLocation();
                    World world = fireball_loc.getWorld();

                    SmallFireball duplicate = (SmallFireball) world.spawnEntity(fireball_loc, EntityType.SMALL_FIREBALL);
                    world.playSound(fireball_loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 3, 1);
                    duplicate.setDirection(player.getEyeLocation().subtract(duplicate.getLocation()).toVector().multiply(0.15));
                }
            }
        }
    }
}
