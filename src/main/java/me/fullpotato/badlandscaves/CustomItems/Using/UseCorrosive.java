package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.CustomItems.Crafting.Corrosive;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UseCorrosive implements Listener {
    private BadlandsCaves plugin;

    public UseCorrosive(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void corrosiveHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();

            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    Corrosive corrosive = new Corrosive(plugin);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (corrosive.isCorrosive(item)) {
                        if (corrosive.getHitsLeft(item) > 0) {
                            double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                            if (event.getDamage() >= player_dmg) {
                                Random random = new Random();
                                boolean critical = event.getDamage() > player_dmg;

                                boolean already_corroding = entity.getPersistentDataContainer().has(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE) && entity.getPersistentDataContainer().get(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE) == (byte) 1;

                                if (!already_corroding && ((critical && random.nextInt(100) < 80) || (!critical && random.nextInt(100) < 40))) {
                                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                        corrosive.setHitsLeft(item, corrosive.getHitsLeft(item) - 1);

                                        if (item.getType().equals(Material.WOODEN_SWORD)) {
                                            Damageable meta = (Damageable) item.getItemMeta();
                                            meta.setDamage(meta.getDamage() + random.nextInt(3) + 3);
                                            item.setItemMeta((ItemMeta) meta);
                                        }
                                    }
                                    applyCorrosion(entity, random, random.nextInt(5) + 10);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void arrowShot (ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getPersistentDataContainer().has(new NamespacedKey(plugin, "corrosive_arrow"), PersistentDataType.BYTE) && arrow.getPersistentDataContainer().get(new NamespacedKey(plugin, "corrosive_arrow"), PersistentDataType.BYTE) == (byte) 1) {
                if (arrow.isCritical()) {
                    if (event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event.getHitEntity();
                        Random random = new Random();
                        boolean already_corroding = entity.getPersistentDataContainer().has(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE) && entity.getPersistentDataContainer().get(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE) == (byte) 1;
                        if (!already_corroding) applyCorrosion(entity, random, random.nextInt(3) + 2);
                    }
                    else {
                        arrow.getWorld().spawnParticle(Particle.REDSTONE, arrow.getLocation(), 5, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.GREEN, 1.5F));
                        arrow.remove();
                    }
                }
            }
        }
    }

    public void applyCorrosion (LivingEntity entity, Random random, int max_times) {
        entity.getPersistentDataContainer().set(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE, (byte) 1);
        int[] times_ran = {0};

        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        new BukkitRunnable(){
            @Override
            public void run() {
                if (entity.isDead() || times_ran[0] > max_times || dataContainer.get(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE) == (byte) 0) {
                    dataContainer.set(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE, (byte) 0);
                    this.cancel();
                }
                else {
                    entity.setHealth(Math.max(Math.min(entity.getHealth() - random.nextDouble() / 2.0, 20.0), 0.0));
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.PLAYERS, 2, 2);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.PLAYERS, 2, 2);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_CORAL_BLOCK_BREAK, SoundCategory.PLAYERS, 0.5F, 1);
                    entity.getWorld().spawnParticle(Particle.REDSTONE, entity.getEyeLocation(), 5, 0.3, 0.3, 0.3, 0, new Particle.DustOptions(Color.GREEN, 2));

                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        PlayerScore.TOXICITY.setScore(plugin, target, ((double) PlayerScore.TOXICITY.getScore(plugin, target)) + (random.nextDouble() / 2));
                    }

                    times_ran[0]++;
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }
}
