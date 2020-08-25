package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SpiderBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    public SpiderBuff(BadlandsCaves bcav, Random random) {
        plugin = bcav;
        this.random = random;
    }

    @EventHandler
    public void HMspider (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Spider)) return;
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");

        final Spider spider = (Spider) event.getEntity();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final double chance = Math.pow(1.045, chaos) - 1;

        if (event.getEntityType().equals(EntityType.SPIDER)) {
            if (!hardmode) {
                if (random.nextInt(100) < chance) {
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.CAVE_SPIDER);
                }
            }
            else {
                final int ascend = (chaos / 5) + plugin.getOptionsConfig().getInt("hardmode_values.ascended_spawn_chance");
                final boolean ascended = random.nextInt(100) < ascend;
                if (ascended) {
                    spider.getPersistentDataContainer().set(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE, (byte) 1);
                    spider.setCustomName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Peter the Stringweaver");
                    spider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(12.0);
                    spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                    spider.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);
                }

                spider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, ascended ? 3 : random.nextInt(2)));
                spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, ascended ? random.nextInt(2) + 1 : random.nextInt(1)));
                spider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, ascended ? random.nextInt(3) + 1 : random.nextInt(1)));

                final Location location = spider.getLocation();
                final World world = location.getWorld();
                int cave_spider_spawn_scale = ascended ? 6 : 3;

                if (random.nextInt(100) < chance) {
                    if (chaos / 10 > 0) {
                        cave_spider_spawn_scale += random.nextInt(chaos / 10);
                    }
                }

                for (int a = 0; a < (random.nextInt(cave_spider_spawn_scale) + cave_spider_spawn_scale); a++) {
                    assert world != null;
                    CaveSpider cavespider = (CaveSpider) (world.spawnEntity(location, EntityType.CAVE_SPIDER));
                    if (ascended) {
                        cavespider.getPersistentDataContainer().set(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE, (byte) 1);
                    }
                }
            }
        }
        else if (spider instanceof CaveSpider && event.getEntityType().equals(EntityType.CAVE_SPIDER)) {
            if (!hardmode) return;
            CaveSpider cavespider = (CaveSpider) spider;
            boolean ascended = cavespider.getPersistentDataContainer().has(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) && cavespider.getPersistentDataContainer().get(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) == (byte) 1;

            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, ascended ? random.nextInt(3) + 2 : random.nextInt(2)));
            cavespider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, ascended ? random.nextInt(3) + 2 : random.nextInt(2)));
            if (ascended) {
                cavespider.setCustomName(ChatColor.DARK_RED + "Petr");
                cavespider.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(6.0);
                cavespider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24.0);
            }
        }
    }

    @EventHandler
    public void ascendedSpidersHit (EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Spider) {
            Spider spider = (Spider) event.getDamager();

            if (spider.getPersistentDataContainer().has(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) && spider.getPersistentDataContainer().get(new NamespacedKey(plugin, "ascended"), PersistentDataType.BYTE) == (byte) 1) {
                Player player = (Player) event.getEntity();
                player.setNoDamageTicks(0);
            }
        }
    }


}
