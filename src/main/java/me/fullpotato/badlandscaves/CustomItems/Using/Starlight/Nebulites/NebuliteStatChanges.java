package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.*;

public class NebuliteStatChanges {
    private final BadlandsCaves plugin;
    private final NebuliteManager nebuliteManager;
    private final StarlightArmor starlightArmor;
    private final StarlightTools starlightTools;
    private final EnchantmentStorage enchantmentStorage;
    private final Map<Material, EquipmentSlot> slotMap;

    public NebuliteStatChanges(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.nebuliteManager = new NebuliteManager(plugin);
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightTools = new StarlightTools(plugin);
        this.enchantmentStorage = new EnchantmentStorage(plugin);

        this.slotMap = new HashMap<>();
        slotMap.put(Material.NETHERITE_HELMET, EquipmentSlot.HEAD);
        slotMap.put(Material.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST);
        slotMap.put(Material.NETHERITE_LEGGINGS, EquipmentSlot.LEGS);
        slotMap.put(Material.NETHERITE_BOOTS, EquipmentSlot.FEET);
    }

    public void updateStats(ItemStack item) {
        if (starlightArmor.isStarlightArmor(item)) {
            updateArmorStats(item);
        }
        else if (starlightTools.isStarlightSaber(item)) {
            updateSaberStats(item);
        }
        else if (starlightTools.isStarlightPaxel(item)) {
            updatePaxelStats(item);
        }
    }

    public void updateArmorStats (ItemStack item) {
        final Map<Enchantment, Integer> defaultEnchantments = CustomItem.STARLIGHT_CHESTPLATE.getItem().getEnchantments();
        final List<Nebulite> nebulites = Arrays.asList(nebuliteManager.getNebulites(item));

        int protection = defaultEnchantments.getOrDefault(Enchantment.PROTECTION_ENVIRONMENTAL, 7);
        int fire_protection = defaultEnchantments.getOrDefault(Enchantment.PROTECTION_FIRE, 0);
        int fall_protection = defaultEnchantments.getOrDefault(Enchantment.PROTECTION_FALL, 0);
        int respiration = defaultEnchantments.getOrDefault(Enchantment.OXYGEN, 0);
        int aqua_affinity = defaultEnchantments.getOrDefault(Enchantment.WATER_WORKER, 0);
        int knockback_resist = 0;
        int speed = 0;

        for (Nebulite nebulite : nebulites) {
            if (nebulite.equals(Nebulite.REINFORCED_PLATING)) {
                protection += 3;
                speed -= 10;
            }
            else if (nebulite.equals(Nebulite.STRONG_STANCE)) {
                knockback_resist += 25;
                speed -= 10;
            }
            else if (nebulite.equals(Nebulite.ROOTED_FEET)) {
                knockback_resist += 75;
                speed -= 25;
            }
            else if (nebulite.equals(Nebulite.THRUSTER)) {
                if (nebulites.contains(Nebulite.REINFORCED_PLATING)) speed += 10;
                if (nebulites.contains(Nebulite.STRONG_STANCE)) speed += 10;
                if (nebulites.contains(Nebulite.ROOTED_FEET)) speed += 25;
            }
            else if (nebulite.equals(Nebulite.OXYGENATOR)) {
                respiration += 3;
                aqua_affinity++;
            }
            else if (nebulite.equals(Nebulite.SMOLDERING_FLAMES)) {
                fire_protection += 5;
            }
            else if (nebulite.equals(Nebulite.SHOCK_ABSORBER)) {
                fall_protection += 5;
            }
        }

        Map<Enchantment, Integer> newEnchants = new HashMap<>();
        if (protection > 0) newEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
        if (fire_protection > 0) newEnchants.put(Enchantment.PROTECTION_FIRE, fire_protection);
        if (fall_protection > 0) newEnchants.put(Enchantment.PROTECTION_FALL, fall_protection);
        if (respiration > 0) newEnchants.put(Enchantment.OXYGEN, respiration);
        if (aqua_affinity > 0) newEnchants.put(Enchantment.WATER_WORKER, aqua_affinity);

        for (Enchantment enchantment : defaultEnchantments.keySet()) {
            if (!newEnchants.containsKey(enchantment)) {
                newEnchants.put(enchantment, defaultEnchantments.get(enchantment));
            }
        }
        enchantmentStorage.setEnchantments(item, newEnchants);

        final ItemMeta meta = item.getItemMeta();

        meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (knockback_resist > 0) {
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "Nebulite Knockback Resistance Modifier", (double) knockback_resist / 100, AttributeModifier.Operation.ADD_SCALAR, slotMap.get(item.getType())));
        }

        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != 0) {
            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Nebulite Speed Modifier", (double) speed / 100, AttributeModifier.Operation.ADD_SCALAR, slotMap.get(item.getType())));
        }

        item.setItemMeta(meta);
    }

    public void updateSaberStats (ItemStack item) {
        final Map<Enchantment, Integer> defaultEnchantments = CustomItem.STARLIGHT_SABER.getItem().getEnchantments();
        final Nebulite[] nebulites = nebuliteManager.getNebulites(item);

        int sharpness = defaultEnchantments.getOrDefault(Enchantment.DAMAGE_ALL, 10);
        int sweeping = defaultEnchantments.getOrDefault(Enchantment.SWEEPING_EDGE, 0);
        double speed = 0;

        for (Nebulite nebulite : nebulites) {
            if (nebulite.equals(Nebulite.FLURRYING_SWINGS)) {
                sharpness -= 3;
                speed += 0.5;
            }
            else if (nebulite.equals(Nebulite.DECISIVE_SLICE)) {
                sharpness += 3;
                speed -= 0.5;
            }
            else if (nebulite.equals(Nebulite.WIDE_SWING)) {
                sweeping += 3;
            }
        }

        final Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (sharpness > 0) enchantments.put(Enchantment.DAMAGE_ALL, sharpness);
        if (sweeping > 0) enchantments.put(Enchantment.SWEEPING_EDGE, sweeping);

        for (Enchantment enchantment : defaultEnchantments.keySet()) {
            if (!enchantments.containsKey(enchantment)) {
                enchantments.put(enchantment, defaultEnchantments.get(enchantment));
            }
        }
        enchantmentStorage.setEnchantments(item, enchantments);

        final ItemMeta meta = item.getItemMeta();

        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        if (speed != 0) {
            int dmg = 3;
            if (sharpness > 0) dmg += 0.5 + (0.5 * sharpness);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "Nebulite Attack Damage", dmg, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "Nebulite Attack Speed Modifier", -2.4 + speed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        }

        item.setItemMeta(meta);
    }

    public void updatePaxelStats (ItemStack item) {
        final Map<Enchantment, Integer> defaultEnchantments = CustomItem.STARLIGHT_PAXEL.getItem().getEnchantments();
        final Nebulite[] nebulites = nebuliteManager.getNebulites(item);

        int efficiency = defaultEnchantments.getOrDefault(Enchantment.DIG_SPEED, 7);
        int fortune = defaultEnchantments.getOrDefault(Enchantment.LOOT_BONUS_BLOCKS, 5);
        int silkTouch = defaultEnchantments.getOrDefault(Enchantment.SILK_TOUCH, 0);

        for (Nebulite nebulite : nebulites) {
            if (nebulite.equals(Nebulite.LIGHTSPEED_PROPULSORS)) {
                efficiency += 5;
                fortune -= 4;
            }
            else if (nebulite.equals(Nebulite.MOLECULAR_PRESERVATION)) {
                fortune -= 5;
                silkTouch++;
            }
        }

        final Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (efficiency > 0) enchantments.put(Enchantment.DIG_SPEED, efficiency);
        if (fortune > 0) enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, fortune);
        if (silkTouch > 0) enchantments.put(Enchantment.SILK_TOUCH, silkTouch);

        for (Enchantment enchantment : defaultEnchantments.keySet()) {
            if (!enchantments.containsKey(enchantment)) {
                enchantments.put(enchantment, defaultEnchantments.get(enchantment));
            }
        }
        enchantmentStorage.setEnchantments(item, enchantments);
    }

}
