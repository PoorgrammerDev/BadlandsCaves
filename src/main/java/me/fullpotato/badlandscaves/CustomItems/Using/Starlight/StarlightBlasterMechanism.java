package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;


import java.util.Collection;

public class StarlightBlasterMechanism extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightCharge chargeManager;
    private final StarlightTools toolManager;

    public StarlightBlasterMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.chargeManager = new StarlightCharge(this.plugin);
        this.toolManager = new StarlightTools(this.plugin);
    }

    @EventHandler
    public void shootBlaster(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = event.getItem();
            if (item != null && toolManager.isStarlightBlaster(item)) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta() instanceof CrossbowMeta) {
                        CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
                        if (meta != null) {
                            if (meta.getChargedProjectiles() == null || meta.getChargedProjectiles().isEmpty()) {
                                ItemStack arrow = new ItemStack(Material.ARROW);
                                ItemMeta arrow_meta = arrow.getItemMeta();
                                if (arrow_meta != null) {
                                    arrow_meta.setDisplayName("§r");
                                    arrow.setItemMeta(arrow_meta);

                                    meta.addChargedProjectile(arrow);
                                    item.setItemMeta(meta);
                                }
                            }
                        }
                    }
                }


                Player player = event.getPlayer();
                int charge = chargeManager.getCharge(item);
                short cooldown = getCooldown(item);
                event.setCancelled(true);
                boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                if (hardmode && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 && charge > 0 && cooldown <= 0) {
                    int travelled = 0;

                    Location location = player.getEyeLocation();
                    TargetEntity targetEntity = new TargetEntity();
                    LivingEntity target = targetEntity.findTargetLivingEntity(location, 50, 0.2, player);

                    ParticleShapes.particleLine(null, Particle.REDSTONE, player.getEyeLocation(), targetEntity.getTargetLocation(), 0, new Particle.DustOptions(Color.fromRGB(255, 200, 1), 1), 1);

                    final int damage = 20;
                    if (target != null) {
                        target.damage(damage, player);
                    }


                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        chargeManager.setCharge(item, charge - ((travelled / 2) + (target != null ? damage / 2 : 0)));
                    }

                    setCooldown(item, (short) 20);
                }
            }
        }
    }

    @Override
    public void run() {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (hardmode) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null) {
                            if (toolManager.isStarlightBlaster(item)) {
                                short cooldown = getCooldown(item);
                                if (cooldown > 0) {
                                    if (cooldown == 1) {
                                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, .5F, 1.2F);
                                    }
                                    cooldown--;
                                    setCooldown(item, cooldown);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setCooldown(ItemStack item, short time) {
        if (toolManager.isStarlightBlaster(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blaster_cooldown"), PersistentDataType.SHORT, time);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    public short getCooldown(ItemStack item) {
        if (toolManager.isStarlightBlaster(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Short cooldown = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "blaster_cooldown"), PersistentDataType.SHORT);
                    if (cooldown != null) return cooldown;
                }
            }
        }
        return -1;
    }

}
