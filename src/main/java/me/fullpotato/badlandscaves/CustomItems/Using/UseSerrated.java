package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.CustomItems.Crafting.SerratedSwords;
import me.fullpotato.badlandscaves.BadlandsCaves;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UseSerrated implements Listener {
    private BadlandsCaves plugin;

    public UseSerrated(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void serratedHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    SerratedSwords serratedSwords = new SerratedSwords(plugin);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (serratedSwords.isSerrated(item)) {
                        double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                        if (event.getDamage() >= player_dmg) {
                            Random random = new Random();
                            boolean critical = event.getDamage() > player_dmg;

                            boolean already_bleeding = entity.hasMetadata("bleeding_debuff") && entity.getMetadata("bleeding_debuff").get(0).asBoolean();
                            if (!already_bleeding && ((critical && random.nextInt(100) < 80)) || (!critical && random.nextInt(100) < 40)) {
                                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                    Damageable meta = (Damageable) item.getItemMeta();
                                    meta.setDamage(meta.getDamage() + 4);
                                    item.setItemMeta((ItemMeta) meta);
                                }


                                entity.setMetadata("bleeding_debuff", new FixedMetadataValue(plugin, true));
                                int max_times = random.nextInt(5) + 5;
                                int[] times_ran = {0};

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (entity.isDead() || times_ran[0] > max_times || !entity.getMetadata("bleeding_debuff").get(0).asBoolean()) {
                                            entity.setMetadata("bleeding_debuff", new FixedMetadataValue(plugin, false));
                                            this.cancel();
                                        }
                                        else {
                                            entity.getWorld().spawnParticle(Particle.BLOCK_DUST, entity.getEyeLocation(), 10, 0.25, 0.25, 0.25, 0, Material.REDSTONE_BLOCK.createBlockData());
                                            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 2, 0.5F);

                                            double bleed = Math.min(Math.max(event.getDamage() * 0.15, 1), 2);
                                            entity.damage(bleed, player);

                                            times_ran[0]++;
                                        }
                                    }
                                }.runTaskTimer(plugin, 0, 60);
                            }
                        }
                    }
                }
            }
        }
    }
}
