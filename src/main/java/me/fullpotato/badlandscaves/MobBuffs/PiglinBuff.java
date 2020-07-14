package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Using.CustomBows;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class PiglinBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Material[] armorTypes = {
            Material.GOLDEN_BOOTS,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_HELMET
    };

    public PiglinBuff(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void HMPiglin (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Piglin)) return;

        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!hardmode) return;

        final Piglin piglin = (Piglin) event.getEntity();
        final Random random = new Random();
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");

        final int[] enchantmentLevels = random.ints(4, 0, (chaos / 20) + 3).toArray();
        final ItemStack[] armor = new ItemStack[4];

        for (int i = 0; i < 4; i++) {
            ItemStack armorPiece = new ItemStack(armorTypes[i]);
            if (enchantmentLevels[i] > 0) {
                ItemMeta meta = armorPiece.getItemMeta();
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevels[i], true);
                armorPiece.setItemMeta(meta);
            }
            armor[i] = armorPiece;
        }

        piglin.getEquipment().setArmorContents(armor);

        // TODO: 7/4/2020 keep this until a piglin distracted event is made, and then cancel that instead of removing drops
        piglin.getEquipment().setHelmetDropChance(-999);
        piglin.getEquipment().setChestplateDropChance(-999);
        piglin.getEquipment().setLeggingsDropChance(-999);
        piglin.getEquipment().setBootsDropChance(-999);

        ItemStack currentWeapon = piglin.getEquipment().getItemInMainHand();
        if (currentWeapon.getType().equals(Material.GOLDEN_SWORD)) {
            int sharpness = random.nextInt((chaos / 20) + 3);
            if (sharpness > 0) {
                ItemMeta meta = currentWeapon.getItemMeta();
                meta.addEnchant(Enchantment.DAMAGE_ALL, sharpness, true);
                currentWeapon.setItemMeta(meta);
            }
        }
        else if (currentWeapon.getType().equals(Material.CROSSBOW)) {
            ItemMeta meta = currentWeapon.getItemMeta();
            int quickCharge = Math.min(random.nextInt((chaos / 20) + 1), 5);
            if (quickCharge > 0) meta.addEnchant(Enchantment.QUICK_CHARGE, quickCharge, true);
            if (random.nextBoolean()) {
                meta.addEnchant(Enchantment.MULTISHOT, 1, true);
            }
            else {
                meta.addEnchant(Enchantment.PIERCING, random.nextInt((chaos / 25) + 2) + 1, true);
            }
            currentWeapon.setItemMeta(meta);

            //shoot voltshock and corrosive arrows?
            final double chance = Math.pow(1.045, chaos) - 1;
            if (random.nextInt(100) < chance) {
                if (random.nextBoolean()) {
                    piglin.getPersistentDataContainer().set(new NamespacedKey(plugin, "shoots_voltshock"), PersistentDataType.BYTE, (byte) 1);
                }
                else {
                    piglin.getPersistentDataContainer().set(new NamespacedKey(plugin, "shoots_corrosive"), PersistentDataType.BYTE, (byte) 1);
                }
            }
        }

        piglin.getEquipment().setItemInMainHand(currentWeapon);

        // TODO: 7/4/2020 see above message
        piglin.getEquipment().setItemInMainHandDropChance(-999);
    }

    @EventHandler
    public void piglinShoot (EntityShootBowEvent event) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!hardmode) return;

        if (event.getEntity() instanceof Piglin) {
            final Piglin piglin = (Piglin) event.getEntity();
            if (event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                final PersistentDataContainer container = piglin.getPersistentDataContainer();
                final NamespacedKey voltshockKey = new NamespacedKey(plugin, "shoots_voltshock");
                final NamespacedKey corrosiveKey = new NamespacedKey(plugin, "shoots_corrosive");

                CustomBows customBows = new CustomBows(plugin);
                if (container.has(voltshockKey, PersistentDataType.BYTE) && container.get(voltshockKey, PersistentDataType.BYTE) == (byte) 1) {
                    customBows.makeVoltshock(arrow, true);
                    arrow.setCritical(true);
                }
                else if (container.has(corrosiveKey, PersistentDataType.BYTE) && container.get(corrosiveKey, PersistentDataType.BYTE) == (byte) 1) {
                    customBows.makeCorrosive(arrow, true);
                    arrow.setCritical(true);
                }
            }
        }
    }

    @EventHandler
    public void preventPiglinFriendlyFire (EntityDamageByEntityEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Piglin) {
                if (event.getDamager() instanceof Piglin) {
                    event.setCancelled(true);
                }
                else if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Piglin) {
                        event.setCancelled(true);
                        arrow.remove();
                    }
                }
            }
        }
    }
}
