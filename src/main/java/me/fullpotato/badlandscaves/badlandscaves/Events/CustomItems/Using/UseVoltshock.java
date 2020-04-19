package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.Voltshock;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class UseVoltshock implements Listener {
    private BadlandsCaves plugin;

    public UseVoltshock(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void voltshockHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (!player.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    Voltshock voltshock = new Voltshock(plugin);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (voltshock.isVoltshock(item)) {
                        if (voltshock.getCharge(item) > 0) {
                            double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                            if (event.getDamage() >= player_dmg) {
                                Random random = new Random();
                                boolean critical = event.getDamage() > player_dmg;

                                if ((critical && random.nextInt(100) < 80) || (!critical && random.nextInt(100) < 40)) {
                                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                        voltshock.setCharge(item, voltshock.getCharge(item) - 1);
                                    }

                                    int metal = metallicArmor(entity);
                                    event.setDamage(event.getDamage() * ((metal / 8.0) + 1.5));
                                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 + (10 * (metal / 4)), 99, false, false));
                                    // TODO: 4/17/2020 electric sound
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int metallicArmor (LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        int output = 0;

        if (equipment != null) {
            if (equipment.getHelmet() != null) {
                if (equipment.getHelmet().getType().equals(Material.IRON_HELMET)) output++;
                else if (equipment.getHelmet().getType().equals(Material.GOLDEN_HELMET)) output++;
                else if (equipment.getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) output++;
            }

            if (equipment.getChestplate() != null) {
                if (equipment.getChestplate().getType().equals(Material.IRON_CHESTPLATE)) output++;
                else if (equipment.getChestplate().getType().equals(Material.GOLDEN_CHESTPLATE)) output++;
                else if (equipment.getChestplate().getType().equals(Material.CHAINMAIL_CHESTPLATE)) output++;
            }

            if (equipment.getLeggings() != null) {
                if (equipment.getLeggings().getType().equals(Material.IRON_LEGGINGS)) output++;
                else if (equipment.getLeggings().getType().equals(Material.GOLDEN_LEGGINGS)) output++;
                else if (equipment.getLeggings().getType().equals(Material.CHAINMAIL_LEGGINGS)) output++;
            }

            if (equipment.getBoots() != null) {
                if (equipment.getBoots().getType().equals(Material.IRON_BOOTS)) output++;
                else if (equipment.getBoots().getType().equals(Material.GOLDEN_BOOTS)) output++;
                else if (equipment.getBoots().getType().equals(Material.CHAINMAIL_BOOTS)) output++;
            }
        }

        return output;
    }
}
