package me.fullpotato.badlandscaves.badlandscaves.Util;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class LoadCustomItems {

    public static void saveCustomItemsToConfig(BadlandsCaves plugin) {
        ItemStack starter_sapling = new ItemStack(Material.OAK_SAPLING);
        ItemMeta starter_sapling_meta = starter_sapling.getItemMeta();
        starter_sapling_meta.setDisplayName("§8[§aStarter Sapling§8]");
        starter_sapling_meta.addEnchant(Enchantment.DURABILITY, 1, false);
        starter_sapling_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        starter_sapling.setItemMeta(starter_sapling_meta);
        plugin.getConfig().set("items.starter_sapling", starter_sapling.serialize());

        //---------------------------

        ItemStack starter_bone_meal = new ItemStack(Material.BONE_MEAL, 3);
        ItemMeta starter_bone_meal_meta = starter_bone_meal.getItemMeta();
        starter_bone_meal_meta.setDisplayName("§8[§fStarter Bone Meal§8]");
        starter_bone_meal_meta.addEnchant(Enchantment.DURABILITY, 1, false);
        starter_bone_meal_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        starter_bone_meal.setItemMeta(starter_bone_meal_meta);
        plugin.getConfig().set("items.starter_bone_meal", starter_bone_meal.serialize());

        //---------------------------

        ItemStack toxic_water = new ItemStack(Material.POTION);
        PotionMeta toxic_water_meta = (PotionMeta) toxic_water.getItemMeta();
        ArrayList<String> toxic_water_lore = new ArrayList<>();

        toxic_water_meta.setDisplayName("§2Toxic Water Bottle");

        toxic_water_lore.add("§8Leaves a disgusting taste in your mouth");
        toxic_water_meta.setLore(toxic_water_lore);

        toxic_water_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        toxic_water_meta.setColor(Color.fromRGB(55, 117, 89));
        toxic_water_meta.setBasePotionData(new PotionData(PotionType.WATER));
        toxic_water.setItemMeta(toxic_water_meta);
        plugin.getConfig().set("items.toxic_water", toxic_water.serialize());

        //---------------------------

        ItemStack purified_water = new ItemStack(Material.POTION);
        PotionMeta purified_water_meta = (PotionMeta) purified_water.getItemMeta();
        ArrayList<String> purified_water_lore = new ArrayList<>();

        purified_water_meta.setDisplayName("§3Purified Water Bottle");

        purified_water_lore.add("§7Light and refreshing.");
        purified_water_meta.setLore(purified_water_lore);

        purified_water_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        purified_water_meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));
        purified_water_meta.setColor(Color.fromRGB(76, 162, 255));
        purified_water.setItemMeta(purified_water_meta);
        plugin.getConfig().set("items.purified_water", purified_water.serialize());

        //---------------------------

        ItemStack antidote = new ItemStack(Material.POTION);
        PotionMeta antidote_meta = (PotionMeta) antidote.getItemMeta();
        ArrayList<String> antidote_lore = new ArrayList<>();

        antidote_meta.setDisplayName("§dAntidote Bottle");

        antidote_lore.add("§7Purges the toxins from your body.");
        antidote_meta.setLore(antidote_lore);

        antidote_meta.addEnchant(Enchantment.DURABILITY, 50, true);
        antidote_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        antidote_meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));
        antidote_meta.setColor(Color.fromRGB(224, 74, 188));

        antidote.setItemMeta(antidote_meta);
        plugin.getConfig().set("items.antidote", antidote.serialize());

        //---------------------------

        ItemStack mana_potion = new ItemStack(Material.POTION);
        PotionMeta mana_potion_meta = (PotionMeta) mana_potion.getItemMeta();
        ArrayList<String> mana_potion_lore = new ArrayList<>();

        mana_potion_meta.setDisplayName("§9Mana Potion");

        mana_potion_lore.add("§7Recovers §9100 §7Mana points.");
        mana_potion_meta.setLore(mana_potion_lore);

        mana_potion_meta.addEnchant(Enchantment.DURABILITY, 100, true);
        mana_potion_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
        mana_potion_meta.setColor(Color.fromRGB(54, 137, 255));
        mana_potion_meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));

        mana_potion.setItemMeta(mana_potion_meta);
        plugin.getConfig().set("items.mana_potion", mana_potion.serialize());

        //---------------------------

        ItemStack tiny_blaze_powder = new ItemStack(Material.STRUCTURE_BLOCK, 9);
        ItemMeta tiny_blaze_powder_meta = tiny_blaze_powder.getItemMeta();
        tiny_blaze_powder_meta.setDisplayName("§rTiny Pile of Blaze Powder");
        tiny_blaze_powder_meta.setCustomModelData(100);
        tiny_blaze_powder.setItemMeta(tiny_blaze_powder_meta);
        plugin.getConfig().set("items.tiny_blaze_powder", tiny_blaze_powder.serialize());

        //---------------------------

        ItemStack purge_essence = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta purge_essence_meta = purge_essence.getItemMeta();
        purge_essence_meta.setDisplayName("§dEssence of Purging");
        purge_essence_meta.setCustomModelData(101);
        purge_essence.setItemMeta(purge_essence_meta);
        plugin.getConfig().set("items.purge_essence", purge_essence.serialize());

        //---------------------------

        ItemStack tainted_powder = new ItemStack(Material.COMMAND_BLOCK, 2);
        ItemMeta tainted_powder_meta = tainted_powder.getItemMeta();
        ArrayList<String> tainted_powder_lore = new ArrayList<>();
        tainted_powder_meta.setDisplayName("§2Tainted Powder");

        tainted_powder_lore.add("§7Right click to throw.");
        tainted_powder_meta.setLore(tainted_powder_lore);

        tainted_powder_meta.setCustomModelData(102);
        tainted_powder.setItemMeta(tainted_powder_meta);
        plugin.getConfig().set("items.tainted_powder", tainted_powder.serialize());

        //---------------------------

        ItemStack fishing_crate = new ItemStack(Material.BARREL);
        ItemMeta fishing_crate_meta = fishing_crate.getItemMeta();
        ArrayList<String> fishing_crate_lore = new ArrayList<>();
        fishing_crate_meta.setDisplayName("§2Fishing Crate");

        fishing_crate_lore.add("§7Right click to open.");
        fishing_crate_lore.add("§2Contains Prehardmode Loot.");
        fishing_crate_meta.setLore(fishing_crate_lore);

        fishing_crate_meta.addEnchant(Enchantment.DURABILITY, 1, false);
        fishing_crate_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        fishing_crate.setItemMeta(fishing_crate_meta);
        plugin.getConfig().set("items.fishing_crate", fishing_crate.serialize());

        //---------------------------

        ItemStack fishing_crate_hardmode = new ItemStack(Material.BARREL);
        ItemMeta fishing_crate_hardmode_meta = fishing_crate.getItemMeta();
        ArrayList<String> fishing_crate_hardmode_lore = new ArrayList<>();
        fishing_crate_hardmode_meta.setDisplayName("§6Fishing Crate");

        fishing_crate_hardmode_lore.add("§7Right click to open.");
        fishing_crate_hardmode_lore.add("§6Contains Hardmode Loot.");
        fishing_crate_hardmode_meta.setLore(fishing_crate_hardmode_lore);

        fishing_crate_hardmode_meta.addEnchant(Enchantment.DURABILITY, 1, false);
        fishing_crate_hardmode_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        fishing_crate_hardmode.setItemMeta(fishing_crate_hardmode_meta);
        plugin.getConfig().set("items.fishing_crate_hardmode", fishing_crate_hardmode.serialize());

        //---------------------------

        ItemStack displace = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta displace_meta = displace.getItemMeta();
        displace_meta.setDisplayName("§dDisplace");
        displace_meta.setCustomModelData(103);
        displace.setItemMeta(displace_meta);
        plugin.getConfig().set("items.displace", displace.serialize());

        //---------------------------

        ItemStack withdraw = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta withdraw_meta = withdraw.getItemMeta();
        withdraw_meta.setDisplayName("§7Withdraw");
        withdraw_meta.setCustomModelData(104);
        withdraw.setItemMeta(withdraw_meta);
        plugin.getConfig().set("items.withdraw", withdraw.serialize());

        //---------------------------

        ItemStack hell_essence = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta hell_essence_meta = hell_essence.getItemMeta();
        hell_essence_meta.setDisplayName("§cEssence of Hell");
        hell_essence_meta.setCustomModelData(105);
        hell_essence.setItemMeta(hell_essence_meta);
        plugin.getConfig().set("items.hell_essence", hell_essence.serialize());

        //---------------------------

        ItemStack enhanced_eyes = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta enhanced_eyes_meta = enhanced_eyes.getItemMeta();
        enhanced_eyes_meta.setDisplayName("§9Enhanced Eyes");
        enhanced_eyes_meta.setCustomModelData(106);
        enhanced_eyes.setItemMeta(enhanced_eyes_meta);
        plugin.getConfig().set("items.enhanced_eyes", enhanced_eyes.serialize());

        //---------------------------

        ItemStack possess = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta possess_meta = possess.getItemMeta();
        possess_meta.setDisplayName("§2Possession");
        possess_meta.setCustomModelData(107);
        possess.setItemMeta(possess_meta);
        plugin.getConfig().set("items.possess", possess.serialize());

        //---------------------------

        ItemStack magic_essence = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta magic_essence_meta = magic_essence.getItemMeta();
        magic_essence_meta.setDisplayName("§9Essence of Magic");
        magic_essence_meta.setCustomModelData(108);
        magic_essence.setItemMeta(magic_essence_meta);
        plugin.getConfig().set("items.magic_essence", magic_essence.serialize());

        //---------------------------

        ItemStack zombie_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta zombie_soul_meta = zombie_soul.getItemMeta();
        zombie_soul_meta.setDisplayName("§rSoul of Decay");
        zombie_soul_meta.setCustomModelData(109);
        zombie_soul.setItemMeta(zombie_soul_meta);
        plugin.getConfig().set("items.zombie_soul", zombie_soul.serialize());

        //---------------------------

        ItemStack creeper_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta creeper_soul_meta = creeper_soul.getItemMeta();
        creeper_soul_meta.setDisplayName("§rSoul of Destruction");
        creeper_soul_meta.setCustomModelData(110);
        creeper_soul.setItemMeta(creeper_soul_meta);
        plugin.getConfig().set("items.creeper_soul", creeper_soul.serialize());

        //---------------------------

        ItemStack skeleton_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta skeleton_soul_meta = skeleton_soul.getItemMeta();
        skeleton_soul_meta.setDisplayName("§rSoul of War");
        skeleton_soul_meta.setCustomModelData(111);
        skeleton_soul.setItemMeta(skeleton_soul_meta);
        plugin.getConfig().set("items.skeleton_soul", skeleton_soul.serialize());

        //---------------------------

        ItemStack spider_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta spider_soul_meta = spider_soul.getItemMeta();
        spider_soul_meta.setDisplayName("§rSoul of Arachnid");
        spider_soul_meta.setCustomModelData(112);
        spider_soul.setItemMeta(spider_soul_meta);
        plugin.getConfig().set("items.spider_soul", spider_soul.serialize());

        //---------------------------

        ItemStack pigzombie_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta pigzombie_soul_meta = pigzombie_soul.getItemMeta();
        pigzombie_soul_meta.setDisplayName("§rSoul of Hellish Decay");
        pigzombie_soul_meta.setCustomModelData(113);
        pigzombie_soul.setItemMeta(pigzombie_soul_meta);
        plugin.getConfig().set("items.pigzombie_soul", pigzombie_soul.serialize());

        //---------------------------

        ItemStack ghast_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta ghast_soul_meta = ghast_soul.getItemMeta();
        ghast_soul_meta.setDisplayName("§rSoul of Suffering");
        ghast_soul_meta.setCustomModelData(114);
        ghast_soul.setItemMeta(ghast_soul_meta);
        plugin.getConfig().set("items.ghast_soul", ghast_soul.serialize());

        //---------------------------

        ItemStack silverfish_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta silverfish_soul_meta = silverfish_soul.getItemMeta();
        silverfish_soul_meta.setDisplayName("§rSoul of Parasite");
        silverfish_soul_meta.setCustomModelData(115);
        silverfish_soul.setItemMeta(silverfish_soul_meta);
        plugin.getConfig().set("items.silverfish_soul", silverfish_soul.serialize());

        //---------------------------

        ItemStack witch_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta witch_soul_meta = witch_soul.getItemMeta();
        witch_soul_meta.setDisplayName("§rSoul of Supernatural");
        witch_soul_meta.setCustomModelData(116);
        witch_soul.setItemMeta(witch_soul_meta);
        plugin.getConfig().set("items.witch_soul", witch_soul.serialize());

        //---------------------------

        ItemStack phantom_soul = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta phantom_soul_meta = phantom_soul.getItemMeta();
        phantom_soul_meta.setDisplayName("§rSoul of Insomnia");
        phantom_soul_meta.setCustomModelData(117);
        phantom_soul.setItemMeta(phantom_soul_meta);
        plugin.getConfig().set("items.phantom_soul", phantom_soul.serialize());

        //---------------------------

        ItemStack merged_souls = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta merged_souls_meta = merged_souls.getItemMeta();
        ArrayList<String> merged_souls_lore = new ArrayList<>();

        merged_souls_meta.setDisplayName("§9§k%§r§d§lMerged Souls§r§9§k%");
        merged_souls_lore.add(ChatColor.GRAY + "If you listen closely, you can hear the screams.");
        merged_souls_meta.setLore(merged_souls_lore);
        merged_souls_meta.setCustomModelData(118);
        merged_souls.setItemMeta(merged_souls_meta);
        plugin.getConfig().set("items.merged_souls", merged_souls.serialize());

        //---------------------------

        ItemStack soul_crystal_incomplete = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta soul_crystal_incomplete_meta = soul_crystal_incomplete.getItemMeta();
        ArrayList<String> soul_crystal_incomplete_lore = new ArrayList<>();

        soul_crystal_incomplete_meta.setDisplayName("§3§k%§r§5§lIncomplete Soul Crystal§r§3§k%");

        soul_crystal_incomplete_lore.add("§9§lRight Click§r§7 to collect your own soul. Be prepared to fight.");
        soul_crystal_incomplete_lore.add("§7Requires a human soul to complete.");
        soul_crystal_incomplete_lore.add("§7Uses Left: 9");
        soul_crystal_incomplete_meta.setLore(soul_crystal_incomplete_lore);

        soul_crystal_incomplete_meta.setCustomModelData(119);
        soul_crystal_incomplete.setItemMeta(soul_crystal_incomplete_meta);
        plugin.getConfig().set("items.soul_crystal_incomplete", soul_crystal_incomplete.serialize());
        //---------------------------

        ItemStack soul_crystal = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta soul_crystal_meta = soul_crystal.getItemMeta();
        ArrayList<String> soul_crystal_lore = new ArrayList<>();

        soul_crystal_meta.setDisplayName("§b§k%§r§d§lSoul Crystal§r§b§k%");

        soul_crystal_lore.add("§9§lRight Click§r§7 to use.");
        soul_crystal_lore.add("§7Can be used as a sacrifice to §kenter Descension§r§7.");
        soul_crystal_lore.add("§7Uses Left: 10");
        soul_crystal_meta.setLore(soul_crystal_lore);

        soul_crystal_meta.setCustomModelData(120);
        soul_crystal.setItemMeta(soul_crystal_meta);
        plugin.getConfig().set("items.soul_crystal", soul_crystal.serialize());

        //---------------------------
        ItemStack rune = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta rune_meta = rune.getItemMeta();
        ArrayList<String> rune_lore = new ArrayList<>();

        rune_meta.setDisplayName("§8§lRune");

        rune_lore.add("§9§lRight Click§r§7 to use.");
        rune_lore.add("§7Used to upgrade Supernatural Abilities.");
        rune_lore.add("§70 / 8 §dMerged Souls");
        rune_lore.add("§70 / 8 §9Essences of Magic");

        rune_meta.setLore(rune_lore);

        rune_meta.setCustomModelData(121);
        rune.setItemMeta(rune_meta);
        plugin.getConfig().set("items.rune", rune.serialize());


        //---------------------------

        ItemStack charged_rune = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta charged_rune_meta = rune.getItemMeta();
        ArrayList<String> charged_rune_lore = new ArrayList<>();

        charged_rune_meta.setDisplayName("§d§k%§r§8§lRune§r§9§k%");

        charged_rune_lore.add("§9§lRight Click§r§7 to upgrade Supernatural Abilities.");

        charged_rune_meta.setLore(charged_rune_lore);

        charged_rune_meta.setCustomModelData(122);
        charged_rune.setItemMeta(charged_rune_meta);
        plugin.getConfig().set("items.charged_rune", charged_rune.serialize());


        //---------------------------
        //CUSTOM MODEL DATA 123 USED BY ENDURANCE ICON
        //---------------------------


        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
