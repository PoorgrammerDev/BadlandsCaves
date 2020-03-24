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
        if (!event.getEntityType().equals(EntityType.SPIDER)) return;
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        final Spider spider = (Spider) event.getEntity();
        final Random random = new Random();

        final int augment = plugin.getConfig().getInt("game_values.hardmode_values.augmented_spawn_chance");
        final boolean augmented = random.nextInt(100) < augment;
        if (augmented) {
            spider.setMetadata("augmented", new FixedMetadataValue(plugin, true));
            spider.setCustomName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Peter the Stringweaver");
            spider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(12.0);
            spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(32.0);
        }

        spider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, augmented ? 3 : random.nextInt(2)));
        spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, augmented ? random.nextInt(2) + 1 : random.nextInt(1)));
        spider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, augmented ? random.nextInt(3) + 1 : random.nextInt(1)));

        final Location location = spider.getLocation();
        final World world = location.getWorld();
        final int cave_spider_spawn_scale = augmented ? 6 : 3;
        for (int a = 0; a < (random.nextInt(cave_spider_spawn_scale) + cave_spider_spawn_scale); a++) {
            assert world != null;
            CaveSpider cavespider = (CaveSpider) (world.spawnEntity(location, EntityType.CAVE_SPIDER));
            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, augmented ? random.nextInt(3) + 2 : random.nextInt(2)));
            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, augmented ? random.nextInt(3) + 2 : random.nextInt(2)));
            if (augmented) {
                cavespider.setCustomName(ChatColor.DARK_RED + "Petr");
                cavespider.setMetadata("augmented", new FixedMetadataValue(plugin, true));
                cavespider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(6.0);
                cavespider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24.0);
            }
        }
    }

    //@EventHandler


}
