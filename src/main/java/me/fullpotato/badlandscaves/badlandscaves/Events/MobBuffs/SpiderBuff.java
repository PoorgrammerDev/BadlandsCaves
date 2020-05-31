package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SpiderBuff implements Listener {
    private BadlandsCaves plugin;
    public SpiderBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMspider (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Spider)) return;
        boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");

        final Spider spider = (Spider) event.getEntity();
        final Random random = new Random();
        final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
        final double chance = Math.pow(1.045, chaos) - 1;

        if (event.getEntityType().equals(EntityType.SPIDER)) {
            if (!hardmode) {
                if (random.nextInt(100) < chance) {
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.CAVE_SPIDER);
                }
            }
            else {
                final int augment = (chaos / 5) + plugin.getConfig().getInt("game_values.hardmode_values.augmented_spawn_chance");
                final boolean augmented = random.nextInt(100) < augment;
                if (augmented) {
                    spider.setMetadata("augmented", new FixedMetadataValue(plugin, true));
                    spider.setCustomName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Peter the Stringweaver");
                    spider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(12.0);
                    spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                    spider.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);
                }

                spider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, augmented ? 3 : random.nextInt(2)));
                spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, augmented ? random.nextInt(2) + 1 : random.nextInt(1)));
                spider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, augmented ? random.nextInt(3) + 1 : random.nextInt(1)));

                final Location location = spider.getLocation();
                final World world = location.getWorld();
                int cave_spider_spawn_scale = augmented ? 6 : 3;

                if (random.nextInt(100) < chance) {
                    if (chaos / 10 > 0) {
                        cave_spider_spawn_scale += random.nextInt(chaos / 10);
                    }
                }

                for (int a = 0; a < (random.nextInt(cave_spider_spawn_scale) + cave_spider_spawn_scale); a++) {
                    assert world != null;
                    CaveSpider cavespider = (CaveSpider) (world.spawnEntity(location, EntityType.CAVE_SPIDER));
                    if (augmented) {
                        cavespider.setMetadata("augmented", new FixedMetadataValue(plugin, true));
                    }
                }
            }
        }
        else if (spider instanceof CaveSpider && event.getEntityType().equals(EntityType.CAVE_SPIDER)) {
            if (!hardmode) return;
            CaveSpider cavespider = (CaveSpider) spider;
            boolean augmented = cavespider.hasMetadata("augmented") && cavespider.getMetadata("augmented").get(0).asBoolean();

            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, augmented ? random.nextInt(3) + 2 : random.nextInt(2)));
            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, augmented ? random.nextInt(3) + 2 : random.nextInt(2)));
            if (augmented) {
                cavespider.setCustomName(ChatColor.DARK_RED + "Petr");
                cavespider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(6.0);
                cavespider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24.0);
            }
        }
    }

    //@EventHandler


}
