package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomBows implements Listener {
    private final BadlandsCaves plugin;
    final ItemStack corrosiveArrow;
    final ItemStack voltshockArrow;
    public CustomBows(BadlandsCaves plugin) {
        this.plugin = plugin;
        corrosiveArrow = plugin.getCustomItemManager().getItem(CustomItem.CORROSIVE_ARROW);
        voltshockArrow = plugin.getCustomItemManager().getItem(CustomItem.VOLTSHOCK_ARROW);
    }

    @EventHandler
    public void shoot (EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getProjectile();
            if (arrow instanceof SpectralArrow || !arrow.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE))
                return;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                PlayerInventory inventory = player.getInventory();
                ItemStack bow = event.getBow();

                int reg_slot = inventory.first(Material.ARROW);
                boolean inOffHand = inventory.getItemInOffHand().getType().equals(Material.ARROW);
                if (reg_slot != -1 || inOffHand) {
                    ItemStack arrowItem = inOffHand ? inventory.getItemInOffHand() : inventory.getItem(reg_slot);


                    if (arrowItem != null && (arrowItem.isSimilar(corrosiveArrow) || arrowItem.isSimilar(voltshockArrow))) {
                        boolean supernatural = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
                        if (!supernatural) {
                            if (arrowItem.isSimilar(corrosiveArrow)) {
                                makeCorrosive(arrow, event.getForce() >= 1);
                            }
                            else if (arrowItem.isSimilar(voltshockArrow)) {
                                makeVoltshock(arrow, event.getForce() >= 1);
                            }
                        }

                        if (bow != null && bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0 && !player.getGameMode().equals(GameMode.CREATIVE)) {
                            arrowItem.setAmount(arrowItem.getAmount() - 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void pickupItem (PlayerPickupArrowEvent event) {
        AbstractArrow arrow = event.getArrow();
        if (arrow.getPersistentDataContainer().has(new NamespacedKey(plugin, "corrosive_arrow"), PersistentDataType.BYTE) && arrow.getPersistentDataContainer().get(new NamespacedKey(plugin, "corrosive_arrow"), PersistentDataType.BYTE) == (byte) 1) {
            event.getItem().setItemStack(corrosiveArrow);
        }
        else if (arrow.getPersistentDataContainer().has(new NamespacedKey(plugin, "voltshock_arrow"), PersistentDataType.BYTE) && arrow.getPersistentDataContainer().get(new NamespacedKey(plugin, "voltshock_arrow"), PersistentDataType.BYTE) == (byte) 1) {
            event.getItem().setItemStack(voltshockArrow);
        }
    }

    public void particleTrail (Arrow arrow, Color color) {
        World world = arrow.getWorld();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isInBlock()) {
                    this.cancel();
                }
                else {
                    world.spawnParticle(Particle.REDSTONE, arrow.getLocation(), 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(color, 0.5F));
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    public void enforceAllowPickup (Arrow arrow) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isInBlock()) {
                    this.cancel();
                }
                else {
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 5);
    }

    public void makeCorrosive (Arrow arrow, boolean fullForce) {
        arrow.getPersistentDataContainer().set(new NamespacedKey(plugin, "corrosive_arrow"), PersistentDataType.BYTE, (byte) 1);
        enforceAllowPickup(arrow);
        if (fullForce) particleTrail(arrow, Color.fromRGB(0, 255, 0));
    }

    public void makeVoltshock (Arrow arrow, boolean fullForce) {
        arrow.getPersistentDataContainer().set(new NamespacedKey(plugin, "voltshock_arrow"), PersistentDataType.BYTE, (byte) 1);
        enforceAllowPickup(arrow);

        if (fullForce) {
            arrow.setVelocity(arrow.getVelocity().multiply(2));
            particleTrail(arrow, Color.AQUA);
        }
    }
}
