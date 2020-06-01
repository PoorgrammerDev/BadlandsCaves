package me.fullpotato.badlandscaves.badlandscaves.Events.Info;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CraftingGuide implements Listener {
    private final BadlandsCaves plugin;
    private final String title = "§8Crafting Guide";
    private final String[] custom_items = {
            "purified_water",
            "antidote",
            "mana_potion",

            "blessed_apple",
            Material.ENCHANTED_GOLDEN_APPLE.toString(),
            "enchanted_blessed_apple",

            Material.RED_SAND.toString(),
            Material.SUGAR_CANE.toString(),
            Material.QUARTZ.toString(),

            "fishing_crate",
            "fishing_crate_hardmode",

            "purge_essence",
            "hell_essence",
            "magic_essence",
            "tainted_powder",

            "tiny_blaze_powder",

            "zombie_soul",
            "creeper_soul",
            "skeleton_soul",
            "spider_soul",
            "pigzombie_soul",
            "ghast_soul",
            "silverfish_soul",
            "witch_soul",
            "phantom_soul",
            "merged_souls",

            "soul_crystal_incomplete",
            "soul_crystal",

            "voltshock_battery",
            "voltshock_shocker",
            "voltshock_placeholder",
            "voltshock_sword_charge_placeholder",
            "voltshock_arrow",

            "corrosive_substance",
            "corrosive_placeholder",
            "corrosive_arrow",

            Material.SHIELD.toString(),
            "stone_shield",
            "iron_shield",
            "diamond_shield",

            "rune",
            "charged_rune",
            "recall_potion",
    };

    private final HashMap<ItemStack, ItemStack[]> craftingRecipes = new HashMap<>();
    private final HashMap<ItemStack, ItemStack[]> cauldronRecipes = new HashMap<>();
    private final HashMap<ItemStack, ItemStack> otherDescription = new HashMap<>();

    public CraftingGuide(BadlandsCaves plugin) {
        this.plugin = plugin;
        fillCraftingRecipes(craftingRecipes);
        fillCauldronRecipes(cauldronRecipes);
        fillOtherDescs(otherDescription);
    }

    public void openGUI (Player player) {
        Inventory inventory = plugin.getServer().createInventory(null, 54, title);
        reset(inventory);

        //-------------------------------------
        player.openInventory(inventory);
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(event.getClickedInventory()) && event.getView().getTitle().equals(title)) {
            event.setCancelled(true);
            Inventory inventory = event.getClickedInventory();
            int slot = event.getSlot();
            if (slot >= 0 && slot <= 53) {
                //CHANGE PAGE-------------------------
                if (slot == 48 || slot == 50) {
                    ItemStack pageIndicator = inventory.getItem(49);
                    assert pageIndicator != null;
                    Integer page = Objects.requireNonNull(pageIndicator.getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER);
                    if (page != null) {
                        if (event.getSlot() == 48 && inventory.getItem(48) != null && inventory.getItem(48).isSimilar(getArrow(true)) && page > 1) {
                            page--;
                            int start = (45 * (page - 1));
                            fillInventory(inventory, start);
                            inventory.setItem(49, getPageNumber(page));
                        }
                        else if (event.getSlot() == 50 && inventory.getItem(50) != null && inventory.getItem(50).isSimilar(getArrow(false))) {
                            page++;
                            int start = (45 * (page - 1));
                            if (custom_items.length > start) {
                                fillInventory(inventory, start);
                                inventory.setItem(49, getPageNumber(page));
                            }
                        }
                    }
                }
                //SELECT ITEM------------------
                else if (slot < 45) {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null) {
                        for (ItemStack key : craftingRecipes.keySet()) {
                            if (key.isSimilar(item)) {
                                craftingGUI(inventory, item, false);
                                return;
                            }
                        }

                        for (ItemStack key : cauldronRecipes.keySet()) {
                            if (key.isSimilar(item)) {
                                cauldronGUI(inventory, item, false);
                                return;
                            }
                        }

                        for (ItemStack key : otherDescription.keySet()) {
                            if (key.isSimilar(item)) {
                                otherGUI(inventory, item);
                                return;
                            }
                        }
                    }
                }
                //HARDMODE BUTTON---------------------
                else if (slot == 45 && inventory.getItem(45) != null) {
                    boolean craft = (inventory.getItem(49) != null && inventory.getItem(49).getType().equals(Material.CRAFTING_TABLE));
                    boolean cauldron = (inventory.getItem(49) != null && inventory.getItem(49).getType().equals(Material.CAULDRON));

                    if (inventory.getItem(45).isSimilar(getHardmodeButton(true))) {
                        if (craft) {
                            ItemStack item = inventory.getItem(24);
                            for (ItemStack key : craftingRecipes.keySet()) {
                                if (key.isSimilar(item)) {
                                    craftingGUI(inventory, item, false);
                                    return;
                                }
                            }
                        }

                        else if (cauldron) {
                            ItemStack item = inventory.getItem(31);
                            for (ItemStack key : cauldronRecipes.keySet()) {
                                if (key.isSimilar(item)) {
                                    cauldronGUI(inventory, item, false);
                                    return;
                                }
                            }
                        }

                    }
                    else if (inventory.getItem(45).isSimilar(getHardmodeButton(false))) {
                        if (craft) {
                            ItemStack item = inventory.getItem(24);
                            for (ItemStack key : craftingRecipes.keySet()) {
                                if (key.isSimilar(item)) {
                                    craftingGUI(inventory, item, true);
                                    return;
                                }
                            }
                        }

                        else if (cauldron) {
                            ItemStack item = inventory.getItem(31);
                            for (ItemStack key : cauldronRecipes.keySet()) {
                                if (key.isSimilar(item)) {
                                    cauldronGUI(inventory, item, true);
                                    return;
                                }
                            }
                        }
                    }
                }
                //RETURN BUTTON-------------
                else if (slot == 53 && inventory.getItem(53) != null && inventory.getItem(53).isSimilar(getReturnButton())) {
                    reset(inventory);
                }
            }

        }
    }

    public void reset(Inventory inventory) {
        inventory.clear();

        fillInventory(inventory, 0);
        ItemStack empty = getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);
        inventory.setItem(45, empty);
        inventory.setItem(46, empty);
        inventory.setItem(47, empty);
        inventory.setItem(48, getArrow(true));
        inventory.setItem(49, getPageNumber(1));
        inventory.setItem(50, getArrow(false));
        inventory.setItem(51, empty);
        inventory.setItem(52, empty);
        inventory.setItem(53, empty);
    }

    public void fillInventory (Inventory inventory, int start) {
        for (int i = 0; i + start < custom_items.length && i < 45; i++) {
            try {
                ItemStack item = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + custom_items[i + start]).getValues(true));
                item.setAmount(1);

                inventory.setItem(i, item);
            }
            catch (NullPointerException e) {
                ItemStack item = new ItemStack(Material.valueOf(custom_items[i + start]));
                inventory.setItem(i, item);
            }
        }
    }

    public void craftingGUI (Inventory inventory, ItemStack item, boolean hardmode) {
        final ItemStack black = getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        final ItemStack white = getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);

        ItemStack[] ingredients = null;
        for (ItemStack result : craftingRecipes.keySet()) {
            if (result.isSimilar(item)) {
                ingredients = craftingRecipes.get(result);
                break;
            }
        }
        if (ingredients == null) return;


        //layout
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, black);
        }

        //bottom bar
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, white);
        }

        //hardmode button
        if (ingredients.length > 9) {
            inventory.setItem(45, getHardmodeButton(hardmode));
        }

        ItemStack crafting = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = crafting.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7This recipe is made in the crafting table.");
        meta.setLore(lore);
        crafting.setItemMeta(meta);

        inventory.setItem(49, crafting);
        inventory.setItem(53, getReturnButton());
        inventory.setItem(24, item);

        int[] slots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        for (int i = 0; i < slots.length; i++) {
            if ((hardmode && 9 + i < ingredients.length) || (!hardmode && i < ingredients.length)) {
                inventory.setItem(slots[i], ingredients[hardmode ? 9 + i : i]);
            }
            else {
                inventory.setItem(slots[i], null);
            }
        }
    }

    public void cauldronGUI (Inventory inventory, ItemStack item, boolean hardmode) {
        final ItemStack black = getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        final ItemStack white = getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);

        ItemStack[] ingredients = null;
        for (ItemStack result : cauldronRecipes.keySet()) {
            if (result.isSimilar(item)) {
                ingredients = cauldronRecipes.get(result);
                break;
            }
        }
        if (ingredients == null) return;

        //layout
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, black);
        }

        //bottom bar
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, white);
        }

        //hardmode button
        if (ingredients.length > 2) {
            inventory.setItem(45, getHardmodeButton(hardmode));
        }

        ItemStack cauldron = new ItemStack(Material.CAULDRON);
        ItemMeta meta = cauldron.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7This recipe is made in the cauldron.");
        meta.setLore(lore);
        cauldron.setItemMeta(meta);

        inventory.setItem(49, cauldron);
        inventory.setItem(53, getReturnButton());

        final int[] green_slots = {9, 18, 27, 17, 26, 35};
        final int[] gray_slots = {11, 12, 13, 14, 15, 21, 23, 29, 30, 31, 32, 33};
        final ItemStack green = getEmptyItem(Material.GREEN_STAINED_GLASS_PANE);
        final ItemStack gray = getEmptyItem(Material.GRAY_STAINED_GLASS_PANE);

        for (int green_slot : green_slots) {
            inventory.setItem(green_slot, green);
        }

        for (int gray_slot : gray_slots) {
            inventory.setItem(gray_slot, gray);
        }

        inventory.setItem(22, getEmptyItem(Material.LIME_STAINED_GLASS_PANE));


        final int[] slots = {20, 24};
        for (int i = 0; i < slots.length; i++) {
            if ((hardmode && 2 + i < ingredients.length) || (!hardmode && i < ingredients.length)) {
                inventory.setItem(slots[i], ingredients[hardmode ? 2 + i : i]);
            }
            else {
                inventory.setItem(slots[i], null);
            }
        }

        inventory.setItem(31, item);
    }

    public void otherGUI (Inventory inventory, ItemStack item) {
        final ItemStack black = getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        final ItemStack white = getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);

        ItemStack descriptions = null;
        for (ItemStack result : otherDescription.keySet()) {
            if (result.isSimilar(item)) {
                descriptions = otherDescription.get(result);
                break;
            }
        }
        if (descriptions == null) return;

        //layout
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, black);
        }

        //bottom bar
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, white);
        }

        inventory.setItem(53, getReturnButton());

        inventory.setItem(20, descriptions);
        inventory.setItem(24, item);
    }

    public ItemStack getEmptyItem(Material material) {
        ItemStack empty = new ItemStack(material);
        ItemMeta meta = empty.getItemMeta();
        meta.setDisplayName("§r");
        empty.setItemMeta(meta);

        return empty;
    }

    public ItemStack getArrow (boolean reverse) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(reverse ? "§aPrevious Page" : "§aNext Page");

        ArrayList<String> desc = new ArrayList<>();
        desc.add("§7Click to go to the " + (reverse ? "previous" : "next") + " page.");
        meta.setLore(desc);

        arrow.setItemMeta(meta);

        return arrow;
    }

    public ItemStack getPageNumber (int page) {
        ItemStack pageNumber = new ItemStack(Material.PAPER);
        ItemMeta meta = pageNumber.getItemMeta();
        meta.setDisplayName("§aCurrent Page");

        ArrayList<String> desc = new ArrayList<>();
        desc.add("§7You are on Page " + page + ".");
        meta.setLore(desc);

        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page);
        pageNumber.setItemMeta(meta);
        return pageNumber;
    }

    public ItemStack getHardmodeButton (boolean hardmode) {
        ItemStack item = new ItemStack(hardmode ? Material.RED_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hardmode ? "§cHardmode Recipe" : "§3Prehardmode Recipe");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Click to view the " + (hardmode ? "Prehardmode" : "Hardmode") + " recipe.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getReturnButton () {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aReturn");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Brings you back to the home screen.");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public void fillCraftingRecipes(HashMap<ItemStack, ItemStack[]> recipes) {
        ItemStack any_soul = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true));
        ItemMeta zombie_soul_meta = any_soul.getItemMeta();
        zombie_soul_meta.setDisplayName("§rAny Soul");
        any_soul.setItemMeta(zombie_soul_meta);
        ItemStack[] blessed_apple_recipe = {
                any_soul,
                any_soul,
                any_soul,
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)),
                new ItemStack(Material.GOLDEN_APPLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)),
                any_soul,
                any_soul,
                any_soul,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.blessed_apple").getValues(true)), blessed_apple_recipe);


        ItemStack[] enchanted_blessed_apple_recipe = {
                any_soul,
                any_soul,
                any_soul,
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)),
                new ItemStack(Material.ENCHANTED_GOLDEN_APPLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)),
                any_soul,
                any_soul,
                any_soul,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enchanted_blessed_apple").getValues(true)), enchanted_blessed_apple_recipe);


        ItemStack tiny_blaze = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tiny_blaze_powder").getValues(true));
        tiny_blaze.setAmount(1);
        ItemStack[] purge_essence_recipe = {
                tiny_blaze,
                tiny_blaze,
                tiny_blaze,
                tiny_blaze,
                new ItemStack(Material.GOLD_INGOT),
                tiny_blaze,
                tiny_blaze,
                tiny_blaze,
                tiny_blaze,
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.BLAZE_POWDER),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)), purge_essence_recipe);


        ItemStack[] hell_essence_recipe = {
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.MAGMA_CREAM),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true)), hell_essence_recipe);


        ItemStack[] magic_essence_recipe = {
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.magic_essence").getValues(true)), magic_essence_recipe);

        ItemStack[] tiny_blaze_powder_recipe = {
                new ItemStack(Material.BLAZE_POWDER)
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tiny_blaze_powder").getValues(true)), tiny_blaze_powder_recipe);


        ItemStack[] merged_souls_recipe = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true)), merged_souls_recipe);


        ItemStack[] soul_crystal_incomplete_recipe = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.merged_souls").getValues(true)),
                new ItemStack(Material.DIAMOND)
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true)), soul_crystal_incomplete_recipe);


        ItemStack[] voltshock_battery_recipe = {
                null,
                new ItemStack(Material.COMPARATOR),
                null,
                new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.IRON_INGOT),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_battery").getValues(true)), voltshock_battery_recipe);


        ItemStack[] voltshock_shocker_recipe = {
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.IRON_BARS),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.IRON_BARS),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.IRON_BARS),
                new ItemStack(Material.REDSTONE),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_shocker").getValues(true)), voltshock_shocker_recipe);


        ItemStack shock_sword = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_placeholder").getValues(true));
        ItemMeta shock_sword_meta = shock_sword.getItemMeta();
        shock_sword_meta.setDisplayName("§rIron / Gold Sword");
        shock_sword.setItemMeta(shock_sword_meta);

        ItemStack[] voltshock_placeholder_recipe = {
                null,
                new ItemStack(Material.REDSTONE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_shocker").getValues(true)),
                null,
                new ItemStack(Material.REDSTONE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_shocker").getValues(true)),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_battery").getValues(true)),
                shock_sword,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_placeholder").getValues(true)), voltshock_placeholder_recipe);


        ItemStack[] voltshock_sword_charge_placeholder_recipe = {
                new ItemStack(Material.EXPERIENCE_BOTTLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_placeholder").getValues(true)),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_sword_charge_placeholder").getValues(true)), voltshock_sword_charge_placeholder_recipe);


        ItemStack[] voltshock_arrow_recipe = {
                null,
                null,
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_shocker").getValues(true)),
                null,
                new ItemStack(Material.REDSTONE),
                null,
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_battery").getValues(true)),
                new ItemStack(Material.ARROW),
                null,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.voltshock_arrow").getValues(true)), voltshock_arrow_recipe);

        ItemStack tainted_powder = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true));
        tainted_powder.setAmount(1);
        ItemStack poison_potion = new ItemStack(Material.POTION);
        PotionMeta poison_potion_meta = (PotionMeta) poison_potion.getItemMeta();
        poison_potion_meta.setBasePotionData(new PotionData(PotionType.POISON, false, true));
        poison_potion.setItemMeta(poison_potion_meta);

        ItemStack[] corrosive_substance_recipe = {
                new ItemStack(Material.SPIDER_EYE),
                tainted_powder,
                tainted_powder,
                tainted_powder,
                poison_potion,
                tainted_powder,
                tainted_powder,
                tainted_powder,
                new ItemStack(Material.SPIDER_EYE),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true)), corrosive_substance_recipe);

        ItemStack corrosive_sword = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_placeholder").getValues(true));
        ItemMeta corrosive_sword_meta = corrosive_sword.getItemMeta();
        corrosive_sword_meta.setDisplayName("§rAny Sword");
        corrosive_sword.setItemMeta(corrosive_sword_meta);

        ItemStack[] corrosive_placeholder_recipe = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true)),
                corrosive_sword,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_placeholder").getValues(true)), corrosive_placeholder_recipe);


        ItemStack[] corrosive_arrow_recipe = {
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_substance").getValues(true)),
                new ItemStack(Material.ARROW)
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.corrosive_arrow").getValues(true)), corrosive_arrow_recipe);


        ItemStack cobble = new ItemStack(Material.COBBLESTONE);
        ItemStack[] stone_shield_recipe = {
                cobble,
                null,
                cobble,
                cobble,
                cobble,
                cobble,
                null,
                cobble,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.stone_shield").getValues(true)), stone_shield_recipe);

        ItemStack iron = new ItemStack(Material.IRON_INGOT);
        ItemStack[] iron_shield_recipe = {
                iron,
                null,
                iron,
                iron,
                iron,
                iron,
                null,
                iron,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.iron_shield").getValues(true)), iron_shield_recipe);

        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemStack[] diamond_shield_recipe = {
                diamond,
                null,
                diamond,
                diamond,
                diamond,
                diamond,
                null,
                diamond,
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.diamond_shield").getValues(true)), diamond_shield_recipe);

        ItemStack planks = new ItemStack(Material.OAK_PLANKS);
        ItemMeta planks_meta = planks.getItemMeta();
        planks_meta.setDisplayName("§rAny Wooden Planks");
        planks.setItemMeta(planks_meta);

        ItemStack[] shield_recipe = {
                planks,
                null,
                planks,
                planks,
                planks,
                planks,
                null,
                planks,
        };
        recipes.put(new ItemStack(Material.SHIELD), shield_recipe);

        ItemStack[] notch_apple_recipe = {
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.APPLE),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
                new ItemStack(Material.GOLD_BLOCK),
        };
        recipes.put(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), notch_apple_recipe);

        ItemStack[] sand_recipe = {
                new ItemStack(Material.DIRT),
                new ItemStack(Material.TERRACOTTA),
        };
        recipes.put(new ItemStack(Material.RED_SAND), sand_recipe);

        ItemStack[] reeds_recipe = {
                new ItemStack(Material.LILY_PAD),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.OAK_SAPLING),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.DIAMOND_BLOCK),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.OAK_SAPLING),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.LILY_PAD),
        };
        recipes.put(new ItemStack(Material.SUGAR_CANE), reeds_recipe);

        ItemStack[] quartz_recipe = {
                new ItemStack(Material.QUARTZ_BLOCK),
        };
        recipes.put(new ItemStack(Material.QUARTZ), quartz_recipe);

    }

    public void fillCauldronRecipes(HashMap<ItemStack, ItemStack[]> recipes) {
        ItemStack[] purified_water_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.GLASS_BOTTLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.hell_essence").getValues(true)),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purified_water").getValues(true)), purified_water_recipe);


        ItemStack[] antidote_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purge_essence").getValues(true)),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.antidote").getValues(true)), antidote_recipe);


        ItemStack[] mana_potion_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.magic_essence").getValues(true)),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.mana_potion").getValues(true)), mana_potion_recipe);


        ItemStack[] tainted_powder_recipe = {
                new ItemStack(Material.BONE_MEAL),
                new ItemStack(Material.SUGAR),
        };
        recipes.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.tainted_powder").getValues(true)), tainted_powder_recipe);


    }

    public void fillOtherDescs (HashMap<ItemStack, ItemStack> descs) {
        ItemStack fishing_crate_desc = new ItemStack(Material.FISHING_ROD);
        ItemMeta fishing_crate_meta = fishing_crate_desc.getItemMeta();
        fishing_crate_meta.setDisplayName("§1Fishing");
        ArrayList<String> fishing_crate_lore = new ArrayList<>();
        fishing_crate_lore.add("§7This item can be obtained by fishing in Prehardmode.");
        fishing_crate_meta.setLore(fishing_crate_lore);
        fishing_crate_desc.setItemMeta(fishing_crate_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate").getValues(true)), fishing_crate_desc);


        ItemStack fishing_crate_hardmode_desc = new ItemStack(Material.FISHING_ROD);
        ItemMeta fishing_crate_hardmode_meta = fishing_crate_hardmode_desc.getItemMeta();
        fishing_crate_hardmode_meta.setDisplayName("§1Fishing");
        ArrayList<String> fishing_crate_hardmode_lore = new ArrayList<>();
        fishing_crate_hardmode_lore.add("§7This item can be obtained by fishing in Hardmode.");
        fishing_crate_hardmode_meta.setLore(fishing_crate_hardmode_lore);
        fishing_crate_hardmode_desc.setItemMeta(fishing_crate_hardmode_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate_hardmode").getValues(true)), fishing_crate_hardmode_desc);


        ItemStack zombie_soul_desc = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta zombie_soul_meta = zombie_soul_desc.getItemMeta();
        zombie_soul_meta.setDisplayName("§2Zombie Drops");
        ArrayList<String> zombie_soul_lore = new ArrayList<>();
        zombie_soul_lore.add("§7This item can be obtained by killing Zombies.");
        zombie_soul_meta.setLore(zombie_soul_lore);
        zombie_soul_desc.setItemMeta(zombie_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)), zombie_soul_desc);


        ItemStack creeper_soul_desc = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta creeper_soul_meta = creeper_soul_desc.getItemMeta();
        creeper_soul_meta.setDisplayName("§aCreeper Drops");
        ArrayList<String> creeper_soul_lore = new ArrayList<>();
        creeper_soul_lore.add("§7This item can be obtained by killing Creepers.");
        creeper_soul_meta.setLore(creeper_soul_lore);
        creeper_soul_desc.setItemMeta(creeper_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)), creeper_soul_desc);


        ItemStack skeleton_soul_desc = new ItemStack(Material.SKELETON_SPAWN_EGG);
        ItemMeta skeleton_soul_meta = skeleton_soul_desc.getItemMeta();
        skeleton_soul_meta.setDisplayName("§8Skeleton Drops");
        ArrayList<String> skeleton_soul_lore = new ArrayList<>();
        skeleton_soul_lore.add("§7This item can be obtained by killing Skeletons.");
        skeleton_soul_meta.setLore(skeleton_soul_lore);
        skeleton_soul_desc.setItemMeta(skeleton_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)), skeleton_soul_desc);


        ItemStack spider_soul_desc = new ItemStack(Material.SPIDER_SPAWN_EGG);
        ItemMeta spider_soul_meta = spider_soul_desc.getItemMeta();
        spider_soul_meta.setDisplayName("§4Spider Drops");
        ArrayList<String> spider_soul_lore = new ArrayList<>();
        spider_soul_lore.add("§7This item can be obtained by killing Spiders.");
        spider_soul_meta.setLore(spider_soul_lore);
        spider_soul_desc.setItemMeta(spider_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)), spider_soul_desc);


        ItemStack pigzombie_soul_desc = new ItemStack(Material.ZOMBIE_PIGMAN_SPAWN_EGG);
        ItemMeta pigzombie_soul_meta = pigzombie_soul_desc.getItemMeta();
        pigzombie_soul_meta.setDisplayName("§dZombie Pigman Drops");
        ArrayList<String> pigzombie_soul_lore = new ArrayList<>();
        pigzombie_soul_lore.add("§7This item can be obtained by killing Zombie Pigmen.");
        pigzombie_soul_meta.setLore(pigzombie_soul_lore);
        pigzombie_soul_desc.setItemMeta(pigzombie_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)), pigzombie_soul_desc);


        ItemStack ghast_soul_desc = new ItemStack(Material.GHAST_SPAWN_EGG);
        ItemMeta ghast_soul_meta = ghast_soul_desc.getItemMeta();
        ghast_soul_meta.setDisplayName("§fGhast Drops");
        ArrayList<String> ghast_soul_lore = new ArrayList<>();
        ghast_soul_lore.add("§7This item can be obtained by killing Ghasts.");
        ghast_soul_meta.setLore(ghast_soul_lore);
        ghast_soul_desc.setItemMeta(ghast_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)), ghast_soul_desc);


        ItemStack silverfish_soul_desc = new ItemStack(Material.SILVERFISH_SPAWN_EGG);
        ItemMeta silverfish_soul_meta = silverfish_soul_desc.getItemMeta();
        silverfish_soul_meta.setDisplayName("§7Silverfish Drops");
        ArrayList<String> silverfish_soul_lore = new ArrayList<>();
        silverfish_soul_lore.add("§7This item can be obtained by killing Silverfish.");
        silverfish_soul_meta.setLore(silverfish_soul_lore);
        silverfish_soul_desc.setItemMeta(silverfish_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)), silverfish_soul_desc);


        ItemStack witch_soul_desc = new ItemStack(Material.WITCH_SPAWN_EGG);
        ItemMeta witch_soul_meta = witch_soul_desc.getItemMeta();
        witch_soul_meta.setDisplayName("§5Witch Drops");
        ArrayList<String> witch_soul_lore = new ArrayList<>();
        witch_soul_lore.add("§7This item can be obtained by killing Witches.");
        witch_soul_meta.setLore(witch_soul_lore);
        witch_soul_desc.setItemMeta(witch_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)), witch_soul_desc);


        ItemStack phantom_soul_desc = new ItemStack(Material.PHANTOM_SPAWN_EGG);
        ItemMeta phantom_soul_meta = phantom_soul_desc.getItemMeta();
        phantom_soul_meta.setDisplayName("§9Phantom Drops");
        ArrayList<String> phantom_soul_lore = new ArrayList<>();
        phantom_soul_lore.add("§7This item can be obtained by killing Phantoms.");
        phantom_soul_meta.setLore(phantom_soul_lore);
        phantom_soul_desc.setItemMeta(phantom_soul_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)), phantom_soul_desc);


        ItemStack soul_crystal_desc = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal_incomplete").getValues(true));
        ItemMeta soul_crystal_meta = soul_crystal_desc.getItemMeta();
        soul_crystal_meta.setDisplayName("§7Complete Soul Crystal");
        ArrayList<String> soul_crystal_lore = new ArrayList<>();
        soul_crystal_lore.add("§7This item is obtained by defeating yourself in the World of Reflection.");
        soul_crystal_meta.setLore(soul_crystal_lore);
        soul_crystal_desc.setItemMeta(soul_crystal_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.soul_crystal").getValues(true)), soul_crystal_desc);


        ItemStack rune_desc = new ItemStack(Material.SPAWNER);
        ItemMeta rune_meta = rune_desc.getItemMeta();
        rune_meta.setDisplayName("§2Dungeon Loot");
        ArrayList<String> rune_lore = new ArrayList<>();
        rune_lore.add("§7This item is obtained by destroying spawners.");
        rune_meta.setLore(rune_lore);
        rune_desc.setItemMeta(rune_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.rune").getValues(true)), rune_desc);


        ItemStack charged_rune_desc = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.rune").getValues(true));
        ItemMeta charged_rune_meta = charged_rune_desc.getItemMeta();
        charged_rune_meta.setDisplayName("§dCharge Rune");
        ArrayList<String> charged_rune_lore = new ArrayList<>();
        charged_rune_lore.add("§7This item is obtained by charging the Rune with Merged Souls and Essences of Magic.");
        charged_rune_meta.setLore(charged_rune_lore);
        charged_rune_desc.setItemMeta(charged_rune_meta);
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.charged_rune").getValues(true)), charged_rune_desc);


        ItemStack recall_potion_desc = rune_desc.clone();
        descs.put(ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.recall_potion").getValues(true)), recall_potion_desc);


    }
}