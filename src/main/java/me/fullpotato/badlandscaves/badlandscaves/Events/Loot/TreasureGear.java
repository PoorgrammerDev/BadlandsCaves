package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.*;

public class TreasureGear extends RandomlyEnchantedGear implements Listener {
    private String prehardmode_label = "§2Prehardmode Treasure Gear";
    private String hardmode_label = "§6Hardmode Treasure Gear";



    public ItemStack getTreasureGear (boolean hardmode, Material material, Random random) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        final double up = hardmode ? 2.0 : 1.5;
        final double down = hardmode ? 1.0 : 1.0 / up;

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
        HashSet<String> adjectives = new HashSet<>();
        {
            adjectives.add("minor");
            adjectives.add("maniacal");
            adjectives.add("ruthless");
            adjectives.add("abounding");
            adjectives.add("hilarious");
            adjectives.add("sneaky");
            adjectives.add("rightful");
            adjectives.add("careless");
            adjectives.add("outrageous");
            adjectives.add("secret");
            adjectives.add("hypnotic");
            adjectives.add("picayune");
            adjectives.add("intelligent");
            adjectives.add("pale");
            adjectives.add("polite");
            adjectives.add("free");
            adjectives.add("habitual");
            adjectives.add("sore");
            adjectives.add("luxuriant");
            adjectives.add("unhappy");
            adjectives.add("real");
            adjectives.add("old");
            adjectives.add("alleged");
            adjectives.add("ready");
            adjectives.add("tall");
            adjectives.add("administrative");
            adjectives.add("guiltless");
            adjectives.add("abject");
            adjectives.add("regular");
            adjectives.add("sable");
            adjectives.add("efficacious");
            adjectives.add("absent");
            adjectives.add("barbarous");
            adjectives.add("giddy");
            adjectives.add("zany");
            adjectives.add("wild");
            adjectives.add("unfair");
            adjectives.add("nasty");
            adjectives.add("descriptive");
            adjectives.add("slippery");
            adjectives.add("next");
            adjectives.add("naughty");
            adjectives.add("incompetent");
            adjectives.add("successful");
            adjectives.add("critical");
            adjectives.add("tacit");
            adjectives.add("expensive");
            adjectives.add("paltry");
            adjectives.add("wicked");
            adjectives.add("wonderful");
            adjectives.add("slimy");
            adjectives.add("husky");
            adjectives.add("optimal");
            adjectives.add("abiding");
            adjectives.add("shaky");
            adjectives.add("far");
            adjectives.add("coordinated");
            adjectives.add("obvious");
            adjectives.add("profuse");
            adjectives.add("sleepy");
            adjectives.add("tough");
            adjectives.add("shrill");
            adjectives.add("abstracted");
            adjectives.add("pink");
            adjectives.add("unable");
            adjectives.add("longing");
            adjectives.add("lyrical");
            adjectives.add("decent");
            adjectives.add("spiteful");
            adjectives.add("financial");
            adjectives.add("nervous");
            adjectives.add("waiting");
            adjectives.add("informal");
            adjectives.add("didactic");
            adjectives.add("shallow");
            adjectives.add("soft");
            adjectives.add("understood");
            adjectives.add("selfish");
            adjectives.add("learned");
            adjectives.add("political");
            adjectives.add("disillusioned");
            adjectives.add("unequal");
            adjectives.add("aback");
            adjectives.add("muddled");
            adjectives.add("chief");
            adjectives.add("gigantic");
            adjectives.add("elastic");
            adjectives.add("pathetic");
            adjectives.add("bustling");
            adjectives.add("remarkable");
            adjectives.add("ultra");
            adjectives.add("eight");
            adjectives.add("immediate");
            adjectives.add("poor");
            adjectives.add("splendid");
        }

        HashSet<String> nouns = new HashSet<>();
        {
            nouns.add("operation");
            nouns.add("police");
            nouns.add("personality");
            nouns.add("pollution");
            nouns.add("comparison");
            nouns.add("affair");
            nouns.add("teaching");
            nouns.add("bonus");
            nouns.add("measurement");
            nouns.add("committee");
            nouns.add("goal");
            nouns.add("basket");
            nouns.add("industry");
            nouns.add("promotion");
            nouns.add("examination");
            nouns.add("reflection");
            nouns.add("internet");
            nouns.add("accident");
            nouns.add("knowledge");
            nouns.add("scene");
            nouns.add("perspective");
            nouns.add("bedroom");
            nouns.add("singer");
            nouns.add("variety");
            nouns.add("highway");
            nouns.add("contract");
            nouns.add("revolution");
            nouns.add("height");
            nouns.add("message");
            nouns.add("heart");
            nouns.add("assumption");
            nouns.add("tea");
            nouns.add("permission");
            nouns.add("people");
            nouns.add("intention");
            nouns.add("length");
            nouns.add("energy");
            nouns.add("significance");
            nouns.add("town");
            nouns.add("player");
            nouns.add("bath");
            nouns.add("computer");
            nouns.add("article");
            nouns.add("fishing");
            nouns.add("ear");
            nouns.add("trainer");
            nouns.add("apartment");
            nouns.add("analyst");
            nouns.add("relationship");
            nouns.add("appearance");
            nouns.add("republic");
            nouns.add("aspect");
            nouns.add("quality");
            nouns.add("anxiety");
            nouns.add("reality");
            nouns.add("argument");
            nouns.add("entertainment");
            nouns.add("woman");
            nouns.add("river");
            nouns.add("unit");
            nouns.add("map");
            nouns.add("combination");
            nouns.add("dad");
            nouns.add("emotion");
            nouns.add("law");
            nouns.add("introduction");
            nouns.add("delivery");
            nouns.add("passion");
            nouns.add("assistant");
            nouns.add("opinion");
            nouns.add("drawer");
            nouns.add("foundation");
            nouns.add("lake");
            nouns.add("satisfaction");
            nouns.add("instance");
            nouns.add("tradition");
            nouns.add("percentage");
            nouns.add("wood");
            nouns.add("ad");
            nouns.add("storage");
            nouns.add("leader");
            nouns.add("conclusion");
            nouns.add("investment");
            nouns.add("technology");
            nouns.add("ambition");
            nouns.add("mud");
            nouns.add("user");
            nouns.add("shirt");
            nouns.add("hearing");
            nouns.add("bird");
            nouns.add("department");
            nouns.add("sir");
            nouns.add("payment");
            nouns.add("ability");
            nouns.add("equipment");
            nouns.add("imagination");
            nouns.add("penalty");
        }

        int adjective_index = random.nextInt(adjectives.size());
        int noun_index = random.nextInt(nouns.size());

        Iterator<String> adjective_iterator = adjectives.iterator();
        Iterator<String> noun_iterator = nouns.iterator();

        for (int i = 0; i < adjective_index; i++) {
            adjective_iterator.next();
        }

        for (int i = 0; i < noun_index; i++) {
            noun_iterator.next();
        }

        String adjective = adjective_iterator.next();
        String noun = noun_iterator.next();

        adjective = adjective.substring(0, 1).toUpperCase() + adjective.substring(1);
        noun = noun.substring(0, 1).toUpperCase() + noun.substring(1);

        return adjective + " " + noun;
    }

    public boolean isTreasureGear (ItemStack item) {
        ArrayList<Material> treasure_gear_materials = new ArrayList<>();
        treasure_gear_materials.add(Material.DIAMOND_HELMET);
        treasure_gear_materials.add(Material.DIAMOND_CHESTPLATE);
        treasure_gear_materials.add(Material.DIAMOND_LEGGINGS);
        treasure_gear_materials.add(Material.DIAMOND_BOOTS);
        treasure_gear_materials.add(Material.DIAMOND_SWORD);
        treasure_gear_materials.add(Material.DIAMOND_SHOVEL);
        treasure_gear_materials.add(Material.DIAMOND_PICKAXE);
        treasure_gear_materials.add(Material.DIAMOND_AXE);

        if (treasure_gear_materials.contains(item.getType())) {
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasLore()) {
                    List<String> lore = item.getItemMeta().getLore();
                    if (lore != null && lore.size() >= 1) {
                        if (lore.get(0).equalsIgnoreCase(prehardmode_label) || lore.get(0).equalsIgnoreCase(hardmode_label)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @EventHandler
    public void preventGrindstone (InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory != null && inventory.getLocation() != null && inventory.getLocation().getBlock().getType().equals(Material.GRINDSTONE) && event.getSlot() == 2) {
            final ItemStack item = event.getCurrentItem();
            if (item != null) {
                TreasureGear treasureGear = new TreasureGear();
                if (treasureGear.isTreasureGear(item)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
