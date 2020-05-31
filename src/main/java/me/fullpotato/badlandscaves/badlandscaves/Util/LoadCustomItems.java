package me.fullpotato.badlandscaves.badlandscaves.Util;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
        ItemMeta charged_rune_meta = charged_rune.getItemMeta();
        ArrayList<String> charged_rune_lore = new ArrayList<>();

        charged_rune_meta.setDisplayName("§d§k%§r§8§lRune§r§9§k%");

        charged_rune_lore.add("§9§lRight Click§r§7 to upgrade Supernatural Abilities.");

        charged_rune_meta.setLore(charged_rune_lore);

        charged_rune_meta.setCustomModelData(122);
        charged_rune.setItemMeta(charged_rune_meta);
        plugin.getConfig().set("items.charged_rune", charged_rune.serialize());


        //---------------------------
        //123 USED BY ENDURANCE ICON
        //124 USED BY SERRATED SWORDS
        //---------------------------

        ItemStack voltshock_battery = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta voltshock_batter_meta = voltshock_battery.getItemMeta();
        voltshock_batter_meta.setDisplayName("§rBattery");
        voltshock_batter_meta.setCustomModelData(125);
        voltshock_battery.setItemMeta(voltshock_batter_meta);
        plugin.getConfig().set("items.voltshock_battery", voltshock_battery.serialize());

        //---------------------------

        ItemStack voltshock_shocker = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta voltshock_shocker_meta = voltshock_shocker.getItemMeta();
        voltshock_shocker_meta.setDisplayName("§rShocker");
        voltshock_shocker_meta.setCustomModelData(126);
        voltshock_shocker.setItemMeta(voltshock_shocker_meta);
        plugin.getConfig().set("items.voltshock_shocker", voltshock_shocker.serialize());

        //---------------------------

        ItemStack voltshock_placeholder = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta voltshock_placeholder_meta = voltshock_placeholder.getItemMeta();
        voltshock_placeholder_meta.setDisplayName("§rVoltshock Sword");
        voltshock_placeholder_meta.setCustomModelData(136);
        voltshock_placeholder.setItemMeta(voltshock_placeholder_meta);
        plugin.getConfig().set("items.voltshock_placeholder", voltshock_placeholder.serialize());

        //---------------------------

        ItemStack voltshock_sword_charge_placeholder = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta voltshock_sword_charge_placeholder_meta = voltshock_sword_charge_placeholder.getItemMeta();
        voltshock_sword_charge_placeholder_meta.setDisplayName("§rCharge Voltshock Sword");
        voltshock_sword_charge_placeholder_meta.setCustomModelData(137);
        voltshock_sword_charge_placeholder.setItemMeta(voltshock_sword_charge_placeholder_meta);
        plugin.getConfig().set("items.voltshock_sword_charge_placeholder", voltshock_sword_charge_placeholder.serialize());

        //---------------------------

        ItemStack voltshock_arrow = new ItemStack(Material.ARROW);
        ItemMeta voltshock_arrow_meta = voltshock_arrow.getItemMeta();
        voltshock_arrow_meta.setDisplayName("§3Voltshock Arrow");
        voltshock_arrow_meta.setCustomModelData(131);
        voltshock_arrow.setItemMeta(voltshock_arrow_meta);
        plugin.getConfig().set("items.voltshock_arrow", voltshock_arrow.serialize());

        //---------------------------
        //127 AND 128 USED BY VOLTSHOCK SWORDS
        //---------------------------

        ItemStack corrosive_substance = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta corrosive_substance_meta = corrosive_substance.getItemMeta();
        corrosive_substance_meta.setDisplayName("§2Corrosive Substance");
        corrosive_substance_meta.setCustomModelData(129);
        corrosive_substance.setItemMeta(corrosive_substance_meta);
        plugin.getConfig().set("items.corrosive_substance", corrosive_substance.serialize());

        //---------------------------

        ItemStack corrosive_placeholder = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta corrosive_placeholder_meta = corrosive_placeholder.getItemMeta();
        corrosive_placeholder_meta.setDisplayName("§rCorrosive Sword");
        corrosive_placeholder_meta.setCustomModelData(138);
        corrosive_placeholder.setItemMeta(corrosive_placeholder_meta);
        plugin.getConfig().set("items.corrosive_placeholder", corrosive_placeholder.serialize());

        //---------------------------

        ItemStack corrosive_arrow = new ItemStack(Material.ARROW);
        ItemMeta corrosive_arrow_meta = corrosive_arrow.getItemMeta();
        corrosive_arrow_meta.setDisplayName("§2Corrosive Arrow");
        corrosive_arrow_meta.setCustomModelData(132);
        corrosive_arrow.setItemMeta(corrosive_arrow_meta);
        plugin.getConfig().set("items.corrosive_arrow", corrosive_arrow.serialize());

        //---------------------------

        ItemStack chamber_magma_key = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta chamber_magma_key_meta = chamber_magma_key.getItemMeta();
        chamber_magma_key_meta.setDisplayName("§cMagma Key");
        chamber_magma_key_meta.setCustomModelData(133);
        chamber_magma_key.setItemMeta(chamber_magma_key_meta);
        plugin.getConfig().set("items.chamber_magma_key", chamber_magma_key.serialize());

        //---------------------------

        ItemStack chamber_glowstone_key = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta chamber_glowstone_key_meta = chamber_glowstone_key.getItemMeta();
        chamber_glowstone_key_meta.setDisplayName("§6Glowstone Key");
        chamber_glowstone_key_meta.setCustomModelData(134);
        chamber_glowstone_key.setItemMeta(chamber_glowstone_key_meta);
        plugin.getConfig().set("items.chamber_glowstone_key", chamber_glowstone_key.serialize());

        //---------------------------

        ItemStack chamber_soulsand_key = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta chamber_soulsand_key_meta = chamber_soulsand_key.getItemMeta();
        chamber_soulsand_key_meta.setDisplayName("§8Soul Sand Key");
        chamber_soulsand_key_meta.setCustomModelData(135);
        chamber_soulsand_key.setItemMeta(chamber_soulsand_key_meta);
        plugin.getConfig().set("items.chamber_soulsand_key", chamber_soulsand_key.serialize());

        //---------------------------

        ItemStack blessed_apple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta blessed_apple_meta = blessed_apple.getItemMeta();
        blessed_apple_meta.setDisplayName("§bBlessed Apple");
        blessed_apple_meta.setCustomModelData(139);
        blessed_apple.setItemMeta(blessed_apple_meta);
        plugin.getConfig().set("items.blessed_apple", blessed_apple.serialize());

        //---------------------------

        ItemStack enchanted_blessed_apple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta enchanted_blessed_apple_meta = enchanted_blessed_apple.getItemMeta();
        enchanted_blessed_apple_meta.setDisplayName("§dEnchanted Blessed Apple");
        enchanted_blessed_apple_meta.setCustomModelData(140);
        enchanted_blessed_apple.setItemMeta(enchanted_blessed_apple_meta);
        plugin.getConfig().set("items.enchanted_blessed_apple", enchanted_blessed_apple.serialize());

        //--------------------------

        ItemStack stone_shield = new ItemStack(Material.SHIELD);
        ItemMeta stone_shield_meta = stone_shield.getItemMeta();
        stone_shield_meta.setDisplayName("§rStone Shield");
        stone_shield_meta.setCustomModelData(141);
        stone_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.OFF_HAND));
        stone_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
        stone_shield_meta.addEnchant(Enchantment.DURABILITY, 4, true);
        stone_shield.setItemMeta(stone_shield_meta);
        plugin.getConfig().set("items.stone_shield", stone_shield.serialize());

        //--------------------------

        ItemStack iron_shield = new ItemStack(Material.SHIELD);
        ItemMeta iron_shield_meta = iron_shield.getItemMeta();
        iron_shield_meta.setDisplayName("§rIron Shield");
        iron_shield_meta.setCustomModelData(142);
        iron_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.OFF_HAND));
        iron_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
        iron_shield_meta.addEnchant(Enchantment.DURABILITY, 5, true);
        iron_shield.setItemMeta(iron_shield_meta);
        plugin.getConfig().set("items.iron_shield", iron_shield.serialize());

        //--------------------------

        ItemStack diamond_shield = new ItemStack(Material.SHIELD);
        ItemMeta diamond_shield_meta = diamond_shield.getItemMeta();
        diamond_shield_meta.setDisplayName("§rDiamond Shield");
        diamond_shield_meta.setCustomModelData(144);
        diamond_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.4, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.OFF_HAND));
        diamond_shield_meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Shield Speed Modifier", -0.4, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
        diamond_shield_meta.addEnchant(Enchantment.DURABILITY, 6, true);
        diamond_shield.setItemMeta(diamond_shield_meta);
        plugin.getConfig().set("items.diamond_shield", diamond_shield.serialize());

        //--------------------------

        ItemStack recall_potion = new ItemStack(Material.POTION);
        PotionMeta recall_potion_meta = (PotionMeta) recall_potion.getItemMeta();
        ArrayList<String> recall_potion_lore = new ArrayList<>();

        recall_potion_meta.setDisplayName("§eRecall Potion");

        recall_potion_lore.add("§7Brings you home.");
        recall_potion_lore.add("§cDo not move when using!");
        recall_potion_meta.setLore(recall_potion_lore);

        recall_potion_meta.addEnchant(Enchantment.DURABILITY, 50, true);
        recall_potion_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        recall_potion_meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));
        recall_potion_meta.setColor(Color.fromRGB(255, 255, 0));

        recall_potion.setItemMeta(recall_potion_meta);
        plugin.getConfig().set("items.recall_potion", recall_potion.serialize());

        //--------------------------

        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static ItemStack getGuideBook (BadlandsCaves plugin) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Guide");
        meta.setAuthor("FullPotato");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);

        String version = plugin.getDescription().getVersion();
        meta.addPage("\n" + "§0\n" + "§0\n" + "§6§lBadlandsCaves§0\n" + "§8§lby FullPotato§0\n" + "§0\n" + "§0\n" + "§0\n" + "§8ver. " + version);

        final String[] entries_page_1 = {
                "§0Introduction§0\n",
                "§0Getting Started§0\n",
                "§9Thirst§0\n",
                "§2Toxicity§0\n",
                "§6Deaths§0\n",
                "§3Purifying Water§0\n",
                "§1Fishing§0\n",
                "§0Classes§0\n",
                "§3Puresoul§0\n",
                "§5Heretic§0\n",
                "§2Dungeons §0\n",
                "§4Chaos§0\n",
                "§cHallowed Chambers§0",
        };
        final String[] pages_page_1 = {
                "4",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "16",
                "21",
                "22",
                "23",
        };
        meta.spigot().addPage(getTOCEntries(entries_page_1, pages_page_1, false));


        final String[] entries_page_2 = {"§4Hardmode§0\n", "§9Augmented Mobs"};
        final String[] pages_page_2 = {"26", "28"};
        meta.spigot().addPage(getTOCEntries(entries_page_2, pages_page_2, true));

        meta.addPage("§lIntroduction§0\n" + "§0You awaken in a mysterious underground cave that seems to never stop. There are no signs of life around you, and all you have are tools to make a tree.");
        meta.addPage("§lIntroduction (cont.)§0\n" + "§0All the water around you is highly poisonous, and your body would not last more than a few seconds in it. However, you need to drink water to survive. Dying only makes things worse.");
        meta.addPage("§lGetting Started§0\n" + "§0You can get wood by using the items you spawn with. With that, tools and weapons are now within reach. Sand for bottles can be made by combining dirt and terracotta, and fishing can be a good early-game food source.");
        meta.addPage("§9§lThirst§0\n" + "§0As you move around, you get thirsty. You must drink water, but the water around you is toxic. Drinking this will increase your Toxicity. To safely drink water, purify it. As your Thirst decreases, you get worse and worse effects until you die at 0%.");
        meta.addPage("§2§lToxicity§0\n" + "§0The reason you die in water is because it rapidly increases your Toxicity. Drinking toxic water also yields the same result, albeit slower. As your toxicity increases, you get worse and worse effects until you die at 100%.");
        meta.addPage("§6§lDeaths§0\n" + "§0You initially spawn with a few buffs. If you keep dying, these buffs are removed and replaced with worse and worse debuffs. To reverse deaths, craft and eat Blessed Apples.");
        meta.addPage("§3§lPurifying Water§0\n" + "§0To purify water, you need to light a fire underneath a cauldron, fill it with water, and open it by right-clicking. Then, add a glass bottle and blaze powder.");
        meta.addPage("§1§lFishing§0\n" + "§0Fishing is a great way to get food early on in the game. String can be harvested from cobwebs or spiders to make the fishing rod. You can fish up Fishing Crates that contain a decent amount of fish or even rare treasure.");
        meta.addPage("§lClasses§0\n" + "§0As you progress, you can either become a Heretic or stay a Puresoul. A Heretic is involved with black magic, whereas a Puresoul can invest into technology.");
        meta.addPage("§3§lPuresoul§0\n" + "§0As a puresoul, you can invest into tech, starting with first a modified sword all the way to power armor, laser sabers, and laser guns in Hardmode. The tech-enhanced player is more durable than a magic user, but less agile.");
        meta.addPage("§3§lPuresoul (cont.)§0\n" + "§0You can modify swords to be Serrated, Voltshock, or Corrosive. Arrows can also be modified to be Voltshock or Corrosive. Serrated inflicts slow-acting DoT, Voltshock increases damage and stuns, and Corrosive inflicts fast-acting true DoT.");
        meta.addPage("§3§lPuresoul (cont.)§0\n" + "§0You can also craft shields from different materials like Cobblestone, Iron, and Diamond. These shields are more protective and durable than a regular shield, but also slow you down more.");
        meta.addPage("§5§lHeretic§0\n" + "§0A heretic uses black magic to weave between enemies and obstacles. Their powers work together to make a menacing force. The Heretic is less durable, but more agile.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0To access black magic, you must collect souls from mobs. Combine the souls of a zombie, zombie pigman, creeper, witch, ghast, silverfish, skeleton, spider, and phantom to make a Merged Soul.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0Then, infuse that merged soul into a diamond. The result is a Soul Crystal, but it is incomplete. You must sacrifice one more soul - your own. Right-click to enter the World of Reflection and harvest your own soul.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0After that, the Soul Crystal is complete. Again, right-click to enter Descension. Here, you must sneak past Lost Souls to capture four towers in a large ring. With every tower you capture, you gain more powers to use.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0If you fail, all collected powers are lost. After you capture all four towers, you can walk along the beam to the center, and exit. You come back with only level 1 Displace. To unlock more powers, you need to find Runes.");
        meta.addPage("§2§lDungeons§0\n" + "§0Finding dungeons and breaking spawners gives players loot, but is vital to a Heretic because it is the only source of Runes. Everytime a spawner is broken, a new dungeon spawns elsewhere with the same spawner.");
        meta.addPage("§4§lChaos§0\n" + "§0Breaking spawners to get loot causes consequences. Everytime a spawner is broken, Chaos increases in the world. Higher chaos causes enemies to become stronger, but also causes more loot, too.");
        meta.addPage("§c§lHallowed Chambers§0\n" + "§0To progress to Hardmode, the latter half of the game, players must travel to The Hallowed Chambers to fight The Wither. To open a portal, simply build a Wither as normal. This will open a portal, and destroy everything around it.");
        meta.addPage("§c§lHallowed Chambers (cont.)§0\n" + "§0This is not an easy fight. You should be in fully enchanted diamond gear with plenty of enchanted golden apples before attempting this.");
        meta.addPage("§c§lHallowed Chambers (cont.)§0\n" + "§0After everyone has entered, right click the red barrier twice to start. After going down the hallway, kill mobs in each of the 3 dungeons. One mob in each dungeon holds a key. Put these keys in the center platform.");
        meta.addPage("§4§lHardmode§0\n" + "§0After defeating The Wither for the first time, the world is irreversibly put into Hardmode. All mobs are significantly buffed. Thirst drains quicker, water doesn't quench as much, more materials are needed to purify, among other things.");
        meta.addPage("§4§lHardmode (cont.)§0\n" + "§0However, you now get access to crazier items, like Power Suits, Sabers, and Modules for the Puresouls, and Voidmatter Armor, Tools, and Artifacts for the Heretics.");
        meta.addPage("§9§lAugmented Mobs§0\n" + "§0In Hardmode, there is a small chance of an Augmented Mob to spawn in the place of a regular mob. This chance is increased with Chaos. These mobs are VERY powerful and could be considered mini-bosses.");


        book.setItemMeta(meta);
        return book;
    }

    public static TextComponent[] getTOCEntries(String[] entries, String[] pages, boolean cont) {
        ArrayList<TextComponent> tableComponents = new ArrayList<>();
        tableComponents.add(new TextComponent(cont ? "§lTable of Contents (cont.)§0\n" : "§lTable of Contents§0\n"));

        for (int i = 0; i < entries.length; i++) {
            TextComponent component = new TextComponent(entries[i]);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, pages[i]));

            TextComponent[] hover = new TextComponent[1];
            hover[0] = new TextComponent("Click to skip to " + ChatColor.stripColor(entries[i]));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));

            tableComponents.add(component);
        }

        TextComponent[] a = new TextComponent[0];
        return tableComponents.toArray(a);
    }
}
