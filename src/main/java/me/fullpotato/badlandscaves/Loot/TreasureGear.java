package me.fullpotato.badlandscaves.Loot;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureGear extends RandomlyEnchantedGear implements Listener {
    private final String prehardmode_label = "§2Prehardmode Treasure Gear";
    private final String hardmode_label = "§6Hardmode Treasure Gear";
    private final String[] adjectives;
    private final String[] nouns;
    private final Material[] armor = {
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE,
    };

    public TreasureGear() {
        adjectives = new String[]{"maniacal", "ruthless", "abounding", "hilarious", "sneaky", "rightful", "careless", "outrageous", "secret", "hypnotic", "intelligent", "pale", "polite", "free", "habitual", "luxuriant", "unhappy", "real", "old", "alleged", "ready", "tall", "administrative", "guiltless", "abject", "regular", "stable", "efficacious", "absent", "barbarous", "giddy", "wild", "unfair", "nasty", "descriptive", "slippery", "next", "naughty", "incompetent", "successful", "critical", "tacit", "expensive", "paltry", "wicked", "wonderful", "slimy", "optimal", "abiding", "shaky", "far", "coordinated", "obvious", "sleepy", "tough", "shrill", "abstracted", "longing", "lyrical", "decent", "spiteful", "financial", "nervous", "waiting", "informal", "didactic", "shallow", "soft", "understood", "selfish", "learned", "disillusioned", "aback", "muddled", "chief", "gigantic", "elastic", "pathetic", "bustling", "remarkable", "ultra", "immediate", "splendid"};
        nouns = new String[]{"operation", "personality", "comparison", "teaching", "bonus", "measurement", "committee", "goal", "basket", "industry", "promotion", "examination", "reflection", "internet", "accident", "knowledge", "scene", "perspective", "bedroom", "variety", "highway", "contract", "revolution", "height", "message", "heart", "assumption", "tea", "permission", "intention", "length", "energy", "significance", "town", "player", "bath", "computer", "article", "fishing", "ear", "trainer", "apartment", "analyst", "relationship", "appearance", "republic", "aspect", "quality", "reality", "argument", "entertainment", "river", "unit", "map", "combination", "emotion", "law", "introduction", "delivery", "passion", "assistant", "opinion", "drawer", "foundation", "lake", "satisfaction", "instance", "tradition", "percentage", "wood", "advertisement", "storage", "leader", "conclusion", "investment", "technology", "ambition", "mud", "user", "shirt", "hearing", "bird", "department", "payment", "ability", "equipment", "imagination", "penalty"};
    }

    public ItemStack getTreasureGear (boolean hardmode, Random random) {
        return getTreasureGear(hardmode, armor[random.nextInt(armor.length)], random);
    }


    public ItemStack getTreasureGear (boolean hardmode, Material material, Random random) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        final double up = hardmode ? 1.5 : 1.0;
        final double down = hardmode ? 1.0 : 0.5;

        meta.setDisplayName((hardmode ? "§6" : "§2") + "\"" + getRandomName(random) + "\"");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(hardmode ? hardmode_label : prehardmode_label);
        meta.setLore(lore);

        if (material.equals(Material.DIAMOND_HELMET) || material.equals(Material.DIAMOND_CHESTPLATE) || material.equals(Material.DIAMOND_LEGGINGS) || material.equals(Material.DIAMOND_BOOTS)) {
            randomlyEnchant(meta, Enchantment.PROTECTION_ENVIRONMENTAL, random, (int) (Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() * down), (int) (Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() * up));
            if (material.equals(Material.DIAMOND_HELMET)) {
                if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.WATER_WORKER, random, (int) (Enchantment.WATER_WORKER.getMaxLevel() * down), (int) (Enchantment.WATER_WORKER.getMaxLevel() * up));
                if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.OXYGEN, random, (int) (Enchantment.OXYGEN.getMaxLevel() * down), (int) (Enchantment.OXYGEN.getMaxLevel() * up));
            }
            else if (material.equals(Material.DIAMOND_CHESTPLATE)) {
                if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.THORNS, random, (int) (Enchantment.THORNS.getMaxLevel() * down), (int) (Enchantment.THORNS.getMaxLevel() * up));
            }
            else if (material.equals(Material.DIAMOND_BOOTS)) {
                if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.DEPTH_STRIDER, random, (int) (Enchantment.DEPTH_STRIDER.getMaxLevel() * down), (int) (Enchantment.DEPTH_STRIDER.getMaxLevel() * up));
                if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.PROTECTION_FALL, random, (int) (Enchantment.PROTECTION_FALL.getMaxLevel() * down), (int) (Enchantment.PROTECTION_FALL.getMaxLevel() * up));
            }
        }

        else if (material.equals(Material.DIAMOND_SWORD)) {
            randomlyEnchant(meta, Enchantment.DAMAGE_ALL, random, (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * down), (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * up));
            if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.FIRE_ASPECT, random, (int) (Enchantment.FIRE_ASPECT.getMaxLevel() * down), (int) (Enchantment.FIRE_ASPECT.getMaxLevel() * up));
            if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.KNOCKBACK, random, (int) (Enchantment.KNOCKBACK.getMaxLevel() * down), (int) (Enchantment.KNOCKBACK.getMaxLevel() * up));
            if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.LOOT_BONUS_MOBS, random, (int) (Enchantment.LOOT_BONUS_MOBS.getMaxLevel() * down), (int) (Enchantment.LOOT_BONUS_MOBS.getMaxLevel() * up));
            if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.SWEEPING_EDGE, random, (int) (Enchantment.SWEEPING_EDGE.getMaxLevel() * down), (int) (Enchantment.SWEEPING_EDGE.getMaxLevel() * up));
        }
        else if (material.equals(Material.DIAMOND_SHOVEL) || material.equals(Material.DIAMOND_PICKAXE) || material.equals(Material.DIAMOND_AXE)) {
            randomlyEnchant(meta, Enchantment.DIG_SPEED, random, (int) (Enchantment.DIG_SPEED.getMaxLevel() * down), (int) (Enchantment.DIG_SPEED.getMaxLevel() * up));
            if (random.nextBoolean()) randomlyEnchant(meta, Enchantment.LOOT_BONUS_BLOCKS, random, (int) (Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel() * down), (int) (Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel() * up));
            else if (random.nextBoolean()) meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);

            if (material.equals(Material.DIAMOND_AXE) && random.nextBoolean()) {
                randomlyEnchant(meta, Enchantment.DAMAGE_ALL, random, (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * down), (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * up));
            }
        }

        output.setItemMeta(meta);

        Repairable repairable_meta = (Repairable) output.getItemMeta();
        repairable_meta.setRepairCost(9999);
        output.setItemMeta((ItemMeta) repairable_meta);

        Damageable damageable_meta = (Damageable) output.getItemMeta();
        damageable_meta.setDamage(random.nextInt(output.getType().getMaxDurability() / 2));
        output.setItemMeta((ItemMeta) damageable_meta);

        return output;
    }

    private String getRandomName(Random random) {
        String adjective = adjectives[random.nextInt(adjectives.length)];
        adjective = adjective.substring(0, 1).toUpperCase() + adjective.substring(1).toLowerCase();

        String noun = nouns[random.nextInt(nouns.length)];
        noun = noun.substring(0, 1).toUpperCase() + noun.substring(1).toLowerCase();

        return adjective + " " + noun;
    }

    public boolean isTreasureGear (ItemStack item) {
        final Material type = item.getType();
        for (Material material : armor) {
            if (type.equals(material)) {
                if (item.hasItemMeta()) {
                    final ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.hasLore()) {
                        final List<String> lore = item.getItemMeta().getLore();
                        if (lore != null && lore.size() >= 1) {
                            return lore.get(0).equalsIgnoreCase(prehardmode_label) || lore.get(0).equalsIgnoreCase(hardmode_label);
                        }
                    }
                }
                break;
            }
        }
        return false;
    }
}
