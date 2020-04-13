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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                randomlyEnchant(meta, Enchantment.WATER_WORKER, random, (int) (Enchantment.WATER_WORKER.getMaxLevel() * down), (int) (Enchantment.WATER_WORKER.getMaxLevel() * up));
                randomlyEnchant(meta, Enchantment.OXYGEN, random, (int) (Enchantment.OXYGEN.getMaxLevel() * down), (int) (Enchantment.OXYGEN.getMaxLevel() * up));
            }
            else if (material.equals(Material.DIAMOND_CHESTPLATE)) {
                randomlyEnchant(meta, Enchantment.THORNS, random, (int) (Enchantment.THORNS.getMaxLevel() * down), (int) (Enchantment.THORNS.getMaxLevel() * up));
            }
            else if (material.equals(Material.DIAMOND_BOOTS)) {
                randomlyEnchant(meta, Enchantment.DEPTH_STRIDER, random, (int) (Enchantment.DEPTH_STRIDER.getMaxLevel() * down), (int) (Enchantment.DEPTH_STRIDER.getMaxLevel() * up));
                randomlyEnchant(meta, Enchantment.PROTECTION_FALL, random, (int) (Enchantment.PROTECTION_FALL.getMaxLevel() * down), (int) (Enchantment.PROTECTION_FALL.getMaxLevel() * up));
            }
        }

        else if (material.equals(Material.DIAMOND_SWORD)) {
            randomlyEnchant(meta, Enchantment.DAMAGE_ALL, random, (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * down), (int) (Enchantment.DAMAGE_ALL.getMaxLevel() * up));
            randomlyEnchant(meta, Enchantment.FIRE_ASPECT, random, (int) (Enchantment.FIRE_ASPECT.getMaxLevel() * down), (int) (Enchantment.FIRE_ASPECT.getMaxLevel() * up));
            randomlyEnchant(meta, Enchantment.KNOCKBACK, random, (int) (Enchantment.KNOCKBACK.getMaxLevel() * down), (int) (Enchantment.KNOCKBACK.getMaxLevel() * up));
            randomlyEnchant(meta, Enchantment.LOOT_BONUS_MOBS, random, (int) (Enchantment.LOOT_BONUS_MOBS.getMaxLevel() * down), (int) (Enchantment.LOOT_BONUS_MOBS.getMaxLevel() * up));
            randomlyEnchant(meta, Enchantment.SWEEPING_EDGE, random, (int) (Enchantment.SWEEPING_EDGE.getMaxLevel() * down), (int) (Enchantment.SWEEPING_EDGE.getMaxLevel() * up));
        }
        else if (material.equals(Material.DIAMOND_SHOVEL) || material.equals(Material.DIAMOND_PICKAXE) || material.equals(Material.DIAMOND_AXE)) {
            randomlyEnchant(meta, Enchantment.DIG_SPEED, random, (int) (Enchantment.DIG_SPEED.getMaxLevel() * down), (int) (Enchantment.DIG_SPEED.getMaxLevel() * up));
            if (random.nextBoolean()) {
                randomlyEnchant(meta, Enchantment.LOOT_BONUS_BLOCKS, random, (int) (Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel() * down), (int) (Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel() * up));
            }
            else if (random.nextBoolean()) {
                meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
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
        final String[] names = {
                "Rex",
                "IHas",
                "Splendid",
                "Asaurus Rex",
                "Ruthless",
                "Uber Splendid Bear",
                "Disguised Bear",
                "SplendidOMG",
                "RuthlessLOL",
                "Cold-bloodedOMG",
                "SplendidLOL",
                "RuthlessOMG",
                "Cold-bloodedLMAO",
                "Iamsplendid",
                "Iamruthless",
                "Iamcold-blooded",
                "Iam",
                "BearMilk",
                "Cold-blooded Bear",
                "MindOf",
                "Gamerbear",
                "The Splendid Gamer",
                "The Ruthless Gamer",
                "The Cold-blooded Gamer",
                "DrSplendid",
                "Popper",
                "BigSplendidBear",
                "ItIsYeBear",
                "Bear Boy",
                "Bear Girl",
                "Bear Person",
                "Captain Splendid",
                "Total Bear",
                "The Splendid Dude",
                "The Gaming Bear",
                "Gaming With",
                "Mr Game Bear",
                "Ms Game Bear",
                "Balmy Tommy",
                "Abs-Brunette",
                "White Light",
                "AyDoubleYou",
                "Sapper Soprano",
                "Anielastic A W",
                "Tony Tony Too",
                "Rover Tommy",
                "Tapper Tony",
                "Tommy Ami",
                "Anielulous W",
                "Devito Veto",
                "Soprano Llano",
                "Tots-DeVito",
                "Cotton Tommy",
                "Rover DeVito",
                "Aniela-W",
                "Soprano Piano",
                "CooloWhite",
                "Light White",
                "White Bite",
                "Da Real Aniela",
                "Tommy Tsunami",
                "Devito Incognito",
                "Anielormous W",
                "Tots-Tommy",
                "Brunettedoc",
                "Cotton DeVito",
                "AyEnEyeEeElAy",
                "Aniela Aniela W.",
                "Brunetteface Aniela",
                "Incognito Devito",
                "Anieladonna",
                "Ol White",
                "Tots-Brunette",
                "DoubleYouAitchEyeTeeEe",
                "Soprano Soprano Soo",
                "Brunetteman",
                "Tony Ceremony",
                "Inspectah Brunette",
                "A.W. Tommy",
                "A.W. DeVito",
                "Tony Testimony",
                "Soprano Nano",
                "Big Rover",
                "SeaOhOhKay",
                "Cook Look",
                "Cook Book",
                "Pig Big",
                "Victor Richter",
                "Reggie Wedgie",
                "VeeEyeSeaTeeOhAre",
                "Papper Peppa",
                "Candy Reggie",
                "Abs-Intelligent",
                "Edgy Reggie",
                "VeeSea",
                "Victoradonna",
                "Tigger Kray",
                "Tigger Reggie",
                "Pig Dig",
                "Ol Cook",
                "Pig Pig Poo",
                "Peppa Peppa Poo",
                "Victorulous C",
                "Cook Outlook",
                "Victorastic V C",
                "V.C. Kray",
                "CooloCook",
                "Intelligentface Victor",
                "Intelligentdoc",
                "V.C. Reggie",
                "Victorormous C",
                "Victora Victora C.",
                "Inspectah Intelligent",
                "Big Tigger",
                "Tots-Kray",
                "Kray Day",
                "Victor Constrictor",
                "Big Pig",
                "Papper Pig",
                "Victor Predictor",
                "Candy Kray",
                "Da Real Victor",
                "Victor-C",
                "Tots-Intelligent",
                "Tots-Reggie",
                "Intelligentman",
                "Kevinadonna",
                "Seven Kevin",
                "CooloWashington",
                "DoubleYouAyEssAitchEyeEnJeeTeeOhEn",
                "Tots-Ben",
                "KayDoubleYou",
                "James Games",
                "James Names",
                "James James Joo",
                "Controllingface Kevin",
                "Kevina Kevina W.",
                "Kevinastic K W",
                "Charles Marls",
                "Chapper Charles",
                "Primrose Harris",
                "Ben Pen",
                "Harris Terrace",
                "K.W. Ben",
                "Kevin-W",
                "Big Buster",
                "Charles Charles Choo",
                "Kevinormous W",
                "Da Real Kevin",
                "Kevinulous W",
                "Tots-Harris",
                "Controllingman",
                "Abs-Controlling",
                "Charles Arles",
                "Controllingdoc",
                "Inspectah Controlling",
                "Wa$hington",
                "Kevin Seven",
                "Buster Harris",
                "Kevin Heaven",
                "Buster Ben",
                "Ol Washington",
                "Japper James",
                "Tots-Controlling",
                "Primrose Ben",
                "K.W. Harris",
                "KayEeVeeEyeEn",

        };

        return names[random.nextInt(names.length)];
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
