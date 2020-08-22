package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.SerratedSwords;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UseSerrated implements Listener {
    private final BadlandsCaves plugin;
    private final SerratedSwords serratedSwords;
    private final Random random;
    public UseSerrated(BadlandsCaves plugin, SerratedSwords serratedSwords, Random random) {
        this.plugin = plugin;
        this.serratedSwords = serratedSwords;
        this.random = random;
    }

    @EventHandler
    public void serratedHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (serratedSwords.isSerrated(item)) {
                        double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                        if (event.getDamage() >= player_dmg) {
                            boolean critical = event.getDamage() > player_dmg;
                            if ((critical) || (random.nextBoolean())) {
                                if (applyBleeding(player, entity, event.getDamage(), random.nextInt(5) + 5, false)) {
                                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                        Damageable meta = (Damageable) item.getItemMeta();
                                        meta.setDamage(meta.getDamage() + 4);
                                        item.setItemMeta((ItemMeta) meta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean applyBleeding (Player player, LivingEntity entity, double originalHitDamage, int count, boolean force) {
        boolean alreadyBleeding = entity.getPersistentDataContainer().has(new NamespacedKey(plugin, "bleeding_debuff"), PersistentDataType.BYTE) && entity.getPersistentDataContainer().get(new NamespacedKey(plugin, "bleeding_debuff"), PersistentDataType.BYTE) == (byte) 1;

        if (force || !alreadyBleeding) {
            entity.getPersistentDataContainer().set(new NamespacedKey(plugin, "bleeding_debuff"), PersistentDataType.BYTE, (byte) 1);
            int[] times_ran = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead() || times_ran[0] > count || entity.getPersistentDataContainer().get(new NamespacedKey(plugin, "bleeding_debuff"), PersistentDataType.BYTE) == (byte) 0) {
                        entity.getPersistentDataContainer().set(new NamespacedKey(plugin, "bleeding_debuff"), PersistentDataType.BYTE, (byte) 0);
                        this.cancel();
                    }
                    else {
                        entity.getWorld().spawnParticle(Particle.BLOCK_DUST, entity.getEyeLocation(), 10, 0.25, 0.25, 0.25, 0, Material.REDSTONE_BLOCK.createBlockData());
                        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 2, 0.5F);

                        double bleed = Math.min(Math.max(originalHitDamage * 0.15, 1), 2);
                        entity.damage(bleed, player);

                        times_ran[0]++;
                    }
                }
            }.runTaskTimer(plugin, 0, 60);
            return true;
        }
        return false;
    }
}
