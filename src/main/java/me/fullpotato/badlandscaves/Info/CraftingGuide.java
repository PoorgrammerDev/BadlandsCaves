package me.fullpotato.badlandscaves.Info;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.ChatColor;
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
            CustomItem.PURIFIED_WATER.toString(),
            CustomItem.ANTIDOTE.toString(),
            CustomItem.MANA_POTION.toString(),

            CustomItem.BLESSED_APPLE.toString(),
            Material.ENCHANTED_GOLDEN_APPLE.toString(),
            CustomItem.ENCHANTED_BLESSED_APPLE.toString(),

            Material.RED_SAND.toString(),
            Material.SUGAR_CANE.toString(),
            Material.QUARTZ.toString(),

            CustomItem.FISHING_CRATE.toString(),
            CustomItem.FISHING_CRATE_HARDMODE.toString(),
            CustomItem.FOREVER_FISH.toString(),

            CustomItem.PURGE_ESSENCE.toString(),
            CustomItem.HELL_ESSENCE.toString(),
            CustomItem.MAGIC_ESSENCE.toString(),
            CustomItem.TAINTED_POWDER.toString(),

            CustomItem.TINY_BLAZE_POWDER.toString(),

            CustomItem.ZOMBIE_SOUL.toString(),
            CustomItem.CREEPER_SOUL.toString(),
            CustomItem.SKELETON_SOUL.toString(),
            CustomItem.SPIDER_SOUL.toString(),
            CustomItem.PIGZOMBIE_SOUL.toString(),
            CustomItem.GHAST_SOUL.toString(),
            CustomItem.SILVERFISH_SOUL.toString(),
            CustomItem.WITCH_SOUL.toString(),
            CustomItem.PHANTOM_SOUL.toString(),
            CustomItem.MERGED_SOULS.toString(),

            CustomItem.SOUL_CRYSTAL_INCOMPLETE.toString(),
            CustomItem.SOUL_CRYSTAL.toString(),

            CustomItem.VOLTSHOCK_BATTERY.toString(),
            CustomItem.VOLTSHOCK_SHOCKER.toString(),
            CustomItem.VOLTSHOCK_PLACEHOLDER.toString(),
            CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER.toString(),
            CustomItem.VOLTSHOCK_ARROW.toString(),

            CustomItem.CORROSIVE_SUBSTANCE.toString(),
            CustomItem.CORROSIVE_PLACEHOLDER.toString(),
            CustomItem.CORROSIVE_ARROW.toString(),

            Material.SHIELD.toString(),
            CustomItem.STONE_SHIELD.toString(),
            CustomItem.IRON_SHIELD.toString(),
            CustomItem.DIAMOND_SHIELD.toString(),

            CustomItem.RUNE.toString(),
            CustomItem.CHARGED_RUNE.toString(),
            CustomItem.RECALL_POTION.toString(),

            CustomItem.TITANIUM_FRAGMENT.toString(),
            CustomItem.TITANIUM_INGOT.toString(),
            CustomItem.REINFORCED_TITANIUM.toString(),
            CustomItem.TITANIUM_ROD.toString(),

            CustomItem.BINDING.toString(),
            CustomItem.GOLDEN_CABLE.toString(),
            CustomItem.NETHER_STAR_FRAGMENT.toString(),
            CustomItem.STARLIGHT_CIRCUIT.toString(),
            CustomItem.STARLIGHT_BATTERY.toString(),
            CustomItem.STARLIGHT_MODULE.toString(),
            CustomItem.PHOTON_EMITTER.toString(),

            CustomItem.STARLIGHT_HELMET.toString(),
            CustomItem.STARLIGHT_CHESTPLATE.toString(),
            CustomItem.STARLIGHT_LEGGINGS.toString(),
            CustomItem.STARLIGHT_BOOTS.toString(),
            CustomItem.STARLIGHT_SABER.toString(),
            CustomItem.STARLIGHT_SHIELD.toString(),
            CustomItem.STARLIGHT_BLASTER.toString(),
            CustomItem.STARLIGHT_PAXEL.toString(),

            CustomItem.ENERGIUM.toString(),
            CustomItem.ENERGY_CORE.toString(),
            CustomItem.STARLIGHT_CHARGE_PLACEHOLDER.toString(),

            CustomItem.VOIDMATTER.toString(),
            CustomItem.VOIDMATTER_STICK.toString(),
            CustomItem.VOIDMATTER_STRING.toString(),
            CustomItem.VOIDMATTER_HELMET.toString(),
            CustomItem.VOIDMATTER_CHESTPLATE.toString(),
            CustomItem.VOIDMATTER_LEGGINGS.toString(),
            CustomItem.VOIDMATTER_BOOTS.toString(),
            CustomItem.VOIDMATTER_BLADE.toString(),
            CustomItem.VOIDMATTER_BOW.toString(),
            CustomItem.VOIDMATTER_PICKAXE.toString(),
            CustomItem.VOIDMATTER_SHOVEL.toString(),
            CustomItem.VOIDMATTER_AXE.toString(),
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
                            if (item.isSimilar(key)) {
                                craftingGUI(inventory, item, false);
                                return;
                            }
                        }

                        for (ItemStack key : cauldronRecipes.keySet()) {
                            if (item.isSimilar(key)) {
                                cauldronGUI(inventory, item, false);
                                return;
                            }
                        }

                        for (ItemStack key : otherDescription.keySet()) {
                            if (item.isSimilar(key)) {
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
        clearItems(inventory);

        for (int i = 0; i + start < custom_items.length && i < 45; i++) {
            try {
                ItemStack item = CustomItem.valueOf(custom_items[i + start]).getItem();
                item.setAmount(1);

                inventory.setItem(i, item);
            }
            catch (IllegalArgumentException e) {
                try {
                    ItemStack item = new ItemStack(Material.valueOf(custom_items[i + start]));
                    inventory.setItem(i, item);
                }
                catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void clearItems(Inventory inventory) {
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, null);
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
        ItemStack any_soul = CustomItem.ZOMBIE_SOUL.getItem();
        ItemMeta zombie_soul_meta = any_soul.getItemMeta();
        zombie_soul_meta.setDisplayName(ChatColor.WHITE + "Any Soul");
        any_soul.setItemMeta(zombie_soul_meta);
        ItemStack[] blessed_apple_recipe = {
                any_soul,
                any_soul,
                any_soul,
                CustomItem.PURGE_ESSENCE.getItem(),
                new ItemStack(Material.GOLDEN_APPLE),
                CustomItem.PURGE_ESSENCE.getItem(),
                any_soul,
                any_soul,
                any_soul,
        };
        recipes.put(CustomItem.BLESSED_APPLE.getItem(), blessed_apple_recipe);


        ItemStack[] enchanted_blessed_apple_recipe = {
                any_soul,
                any_soul,
                any_soul,
                CustomItem.PURGE_ESSENCE.getItem(),
                new ItemStack(Material.ENCHANTED_GOLDEN_APPLE),
                CustomItem.PURGE_ESSENCE.getItem(),
                any_soul,
                any_soul,
                any_soul,
        };
        recipes.put(CustomItem.ENCHANTED_BLESSED_APPLE.getItem(), enchanted_blessed_apple_recipe);


        ItemStack tiny_blaze = CustomItem.TINY_BLAZE_POWDER.getItem();
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
        recipes.put(CustomItem.PURGE_ESSENCE.getItem(), purge_essence_recipe);


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
        recipes.put(CustomItem.HELL_ESSENCE.getItem(), hell_essence_recipe);


        ItemStack[] magic_essence_recipe = {
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                CustomItem.WITCH_SOUL.getItem(),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.LAPIS_LAZULI),
        };
        recipes.put(CustomItem.MAGIC_ESSENCE.getItem(), magic_essence_recipe);

        ItemStack[] tiny_blaze_powder_recipe = {
                new ItemStack(Material.BLAZE_POWDER)
        };
        recipes.put(CustomItem.TINY_BLAZE_POWDER.getItem(), tiny_blaze_powder_recipe);


        ItemStack[] merged_souls_recipe = {
                CustomItem.ZOMBIE_SOUL.getItem(),
                CustomItem.CREEPER_SOUL.getItem(),
                CustomItem.SPIDER_SOUL.getItem(),
                CustomItem.SKELETON_SOUL.getItem(),
                CustomItem.GHAST_SOUL.getItem(),
                CustomItem.SILVERFISH_SOUL.getItem(),
                CustomItem.PHANTOM_SOUL.getItem(),
                CustomItem.PIGZOMBIE_SOUL.getItem(),
                CustomItem.WITCH_SOUL.getItem(),
        };
        recipes.put(CustomItem.MERGED_SOULS.getItem(), merged_souls_recipe);


        ItemStack[] soul_crystal_incomplete_recipe = {
                CustomItem.MERGED_SOULS.getItem(),
                new ItemStack(Material.DIAMOND)
        };
        recipes.put(CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem(), soul_crystal_incomplete_recipe);


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
        recipes.put(CustomItem.VOLTSHOCK_BATTERY.getItem(), voltshock_battery_recipe);


        ItemStack[] voltshock_shocker_recipe = {
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.CHAIN),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.CHAIN),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.CHAIN),
                new ItemStack(Material.REDSTONE),
        };
        recipes.put(CustomItem.VOLTSHOCK_SHOCKER.getItem(), voltshock_shocker_recipe);


        ItemStack shock_sword = CustomItem.VOLTSHOCK_PLACEHOLDER.getItem();
        ItemMeta shock_sword_meta = shock_sword.getItemMeta();
        shock_sword_meta.setDisplayName(ChatColor.WHITE + "Iron / Gold Sword");
        shock_sword.setItemMeta(shock_sword_meta);

        ItemStack[] voltshock_placeholder_recipe = {
                null,
                new ItemStack(Material.REDSTONE),
                CustomItem.VOLTSHOCK_SHOCKER.getItem(),
                null,
                new ItemStack(Material.REDSTONE),
                CustomItem.VOLTSHOCK_SHOCKER.getItem(),
                CustomItem.VOLTSHOCK_BATTERY.getItem(),
                shock_sword,
        };
        recipes.put(CustomItem.VOLTSHOCK_PLACEHOLDER.getItem(), voltshock_placeholder_recipe);


        ItemStack[] voltshock_sword_charge_placeholder_recipe = {
                new ItemStack(Material.EXPERIENCE_BOTTLE),
                CustomItem.VOLTSHOCK_PLACEHOLDER.getItem(),
        };
        recipes.put(CustomItem.VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER.getItem(), voltshock_sword_charge_placeholder_recipe);


        ItemStack[] voltshock_arrow_recipe = {
                null,
                null,
                CustomItem.VOLTSHOCK_SHOCKER.getItem(),
                null,
                new ItemStack(Material.REDSTONE),
                null,
                CustomItem.VOLTSHOCK_BATTERY.getItem(),
                new ItemStack(Material.ARROW),
                null,
        };
        recipes.put(CustomItem.VOLTSHOCK_ARROW.getItem(), voltshock_arrow_recipe);

        ItemStack tainted_powder = CustomItem.TAINTED_POWDER.getItem();
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
        recipes.put(CustomItem.CORROSIVE_SUBSTANCE.getItem(), corrosive_substance_recipe);

        ItemStack corrosive_sword = CustomItem.CORROSIVE_PLACEHOLDER.getItem();
        ItemMeta corrosive_sword_meta = corrosive_sword.getItemMeta();
        corrosive_sword_meta.setDisplayName(ChatColor.WHITE + "Any Sword");
        corrosive_sword.setItemMeta(corrosive_sword_meta);

        ItemStack[] corrosive_placeholder_recipe = {
                CustomItem.CORROSIVE_SUBSTANCE.getItem(),
                corrosive_sword,
        };
        recipes.put(CustomItem.CORROSIVE_PLACEHOLDER.getItem(), corrosive_placeholder_recipe);


        ItemStack[] corrosive_arrow_recipe = {
                CustomItem.CORROSIVE_SUBSTANCE.getItem(),
                new ItemStack(Material.ARROW)
        };
        recipes.put(CustomItem.CORROSIVE_ARROW.getItem(), corrosive_arrow_recipe);


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
        recipes.put(CustomItem.STONE_SHIELD.getItem(), stone_shield_recipe);

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
        recipes.put(CustomItem.IRON_SHIELD.getItem(), iron_shield_recipe);

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
        recipes.put(CustomItem.DIAMOND_SHIELD.getItem(), diamond_shield_recipe);

        ItemStack planks = new ItemStack(Material.OAK_PLANKS);
        ItemMeta planks_meta = planks.getItemMeta();
        planks_meta.setDisplayName(ChatColor.WHITE + "Any Wooden Planks");
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

        ItemStack[] titanium_ingot_recipe = {
                CustomItem.TITANIUM_FRAGMENT.getItem(),
                CustomItem.TITANIUM_FRAGMENT.getItem(),
                null,
                CustomItem.TITANIUM_FRAGMENT.getItem(),
                CustomItem.TITANIUM_FRAGMENT.getItem(),
        };
        recipes.put(CustomItem.TITANIUM_INGOT.getItem(), titanium_ingot_recipe);

        final ItemStack goldNugget = new ItemStack(Material.GOLD_NUGGET);
        final ItemStack netheriteScrap = new ItemStack(Material.NETHERITE_SCRAP);
        ItemStack[] reinforced_titanium_recipe = {
                goldNugget,
                goldNugget,
                goldNugget,
                netheriteScrap,
                CustomItem.TITANIUM_INGOT.getItem(),
                netheriteScrap,
                goldNugget,
                goldNugget,
                goldNugget,
        };
        recipes.put(CustomItem.REINFORCED_TITANIUM.getItem(), reinforced_titanium_recipe);

        ItemStack[] binding_recipe = {
                new ItemStack(Material.RED_SAND),
                new ItemStack(Material.GRAVEL),
                null,
                new ItemStack(Material.GRAVEL),
                new ItemStack(Material.RED_SAND),
        };
        recipes.put(CustomItem.BINDING.getItem(), binding_recipe);

        ItemStack[] golden_cable_recipe = {
            CustomItem.BINDING.getItem(),
                CustomItem.BINDING.getItem(),
                CustomItem.BINDING.getItem(),
                new ItemStack(Material.GOLD_INGOT),
                new ItemStack(Material.GOLD_INGOT),
                new ItemStack(Material.GOLD_INGOT),
                CustomItem.BINDING.getItem(),
                CustomItem.BINDING.getItem(),
                CustomItem.BINDING.getItem(),
        };
        recipes.put(CustomItem.GOLDEN_CABLE.getItem(), golden_cable_recipe);

        ItemStack[] nether_star_fragment_recipe = {
            new ItemStack(Material.NETHER_STAR)
        };
        recipes.put(CustomItem.NETHER_STAR_FRAGMENT.getItem(), nether_star_fragment_recipe);

        ItemStack[] starlight_circuit_recipe = {
                CustomItem.GOLDEN_CABLE.getItem(),
                CustomItem.GOLDEN_CABLE.getItem(),
                CustomItem.GOLDEN_CABLE.getItem(),
                new ItemStack(Material.REDSTONE),
                CustomItem.NETHER_STAR_FRAGMENT.getItem(),
                new ItemStack(Material.REDSTONE),
                CustomItem.GOLDEN_CABLE.getItem(),
                CustomItem.GOLDEN_CABLE.getItem(),
                CustomItem.GOLDEN_CABLE.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_CIRCUIT.getItem(), starlight_circuit_recipe);

        ItemStack[] starlight_battery_recipe = {
                null,
                new ItemStack(Material.COMPARATOR),
                null,
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.NETHER_STAR_FRAGMENT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.NETHER_STAR_FRAGMENT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_BATTERY.getItem(), starlight_battery_recipe);

        ItemStack[] starlight_module_recipe = {
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.STARLIGHT_CIRCUIT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.STARLIGHT_BATTERY.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_MODULE.getItem(), starlight_module_recipe);

        ItemStack[] titanium_rod_recipe = {
                CustomItem.TITANIUM_ROD.getItem(),
                null,
                null,
                CustomItem.TITANIUM_ROD.getItem(),
        };
        recipes.put(CustomItem.TITANIUM_ROD.getItem(), titanium_rod_recipe);


        ItemStack[] photon_emitter_recipe = {
                CustomItem.TITANIUM_INGOT.getItem(),
                new ItemStack(Material.ENDER_PEARL),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.NETHER_STAR_FRAGMENT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.TITANIUM_INGOT.getItem(),
        };
        recipes.put(CustomItem.PHOTON_EMITTER.getItem(), photon_emitter_recipe);


        ItemStack[] energium_recipe = {
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.DIAMOND),
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.DIAMOND),
        };
        recipes.put(CustomItem.ENERGIUM.getItem(), energium_recipe);

        ItemStack[] energy_core_recipe = {
               CustomItem.ENERGIUM.getItem(),
                new ItemStack(Material.EXPERIENCE_BOTTLE),
        };
        recipes.put(CustomItem.ENERGY_CORE.getItem(), energy_core_recipe);


        ItemStack[] starlight_helmet_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_HELMET.getItem(), starlight_helmet_recipe);


        ItemStack[] starlight_chestplate_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_CHESTPLATE.getItem(), starlight_chestplate_recipe);


        ItemStack[] starlight_leggings_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_LEGGINGS.getItem(), starlight_leggings_recipe);


        ItemStack[] starlight_boots_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_BOOTS.getItem(), starlight_boots_recipe);


        ItemStack[] starlight_saber_recipe = {
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.PHOTON_EMITTER.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                null,
                CustomItem.TITANIUM_ROD.getItem(),
                null,
        };
        recipes.put(CustomItem.STARLIGHT_SABER.getItem(), starlight_saber_recipe);

        ItemStack[] starlight_shield_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.STARLIGHT_MODULE.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_SHIELD.getItem(), starlight_shield_recipe);

        ItemStack[] starlight_blaster_recipe = {
                new ItemStack(Material.REDSTONE),
                new ItemStack(Material.REDSTONE),
                CustomItem.PHOTON_EMITTER.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                new ItemStack(Material.COMPARATOR),
                null,
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_BLASTER.getItem(), starlight_blaster_recipe);


        ItemStack[] starlight_paxel_recipe = {
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.PHOTON_EMITTER.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.REINFORCED_TITANIUM.getItem(),
                CustomItem.TITANIUM_ROD.getItem(),
                null,
                null,
                CustomItem.TITANIUM_ROD.getItem(),
        };
        recipes.put(CustomItem.STARLIGHT_PAXEL.getItem(), starlight_paxel_recipe);

        ItemStack starlight_icon = CustomItem.STARLIGHT_CHARGE_PLACEHOLDER.getItem();
        ItemMeta starlight_icon_meta = starlight_icon.getItemMeta();
        starlight_icon_meta.setDisplayName("§eAny Starlight Armor / Tool");
        starlight_icon.setItemMeta(starlight_icon_meta);

        ItemStack[] starlight_charge_placeholder_recipe = {
                CustomItem.ENERGY_CORE.getItem(),
                starlight_icon,
        };
        recipes.put(CustomItem.STARLIGHT_CHARGE_PLACEHOLDER.getItem(), starlight_charge_placeholder_recipe);

        ItemStack[] voidmatter_stick_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_STICK.getItem(), voidmatter_stick_recipe);

        ItemStack[] voidmatter_string_recipe = {
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_STRING.getItem(), voidmatter_string_recipe);

        ItemStack[] voidmatter_helmet_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_HELMET.getItem(), voidmatter_helmet_recipe);

        ItemStack[] voidmatter_chestplate_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_CHESTPLATE.getItem(), voidmatter_chestplate_recipe);

        ItemStack[] voidmatter_leggings_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_LEGGINGS.getItem(), voidmatter_leggings_recipe);

        ItemStack[] voidmatter_boots_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_BOOTS.getItem(), voidmatter_boots_recipe);

        ItemStack[] voidmatter_blade_recipe = {
                null,
                CustomItem.VOIDMATTER.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_BLADE.getItem(), voidmatter_blade_recipe);

        ItemStack[] voidmatter_bow_recipe = {
                CustomItem.VOIDMATTER_STRING.getItem(),
                CustomItem.VOIDMATTER_STICK.getItem(),
                null,
                CustomItem.VOIDMATTER_STRING.getItem(),
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
                null,
                CustomItem.VOIDMATTER_STRING.getItem(),
                CustomItem.VOIDMATTER_STICK.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_BOW.getItem(), voidmatter_bow_recipe);

        ItemStack[] voidmatter_pickaxe_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_PICKAXE.getItem(), voidmatter_pickaxe_recipe);

        ItemStack[] voidmatter_shovel_recipe = {
                null,
                CustomItem.VOIDMATTER.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_SHOVEL.getItem(), voidmatter_shovel_recipe);

        ItemStack[] voidmatter_axe_recipe = {
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER.getItem(),
                null,
                CustomItem.VOIDMATTER.getItem(),
                CustomItem.VOIDMATTER_STICK.getItem(),
                null,
                null,
                CustomItem.VOIDMATTER_STICK.getItem(),
        };
        recipes.put(CustomItem.VOIDMATTER_AXE.getItem(), voidmatter_axe_recipe);
    }

    public void fillCauldronRecipes(HashMap<ItemStack, ItemStack[]> recipes) {
        ItemStack[] purified_water_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.GLASS_BOTTLE),
                CustomItem.HELL_ESSENCE.getItem(),
        };
        recipes.put(CustomItem.PURIFIED_WATER.getItem(), purified_water_recipe);


        ItemStack[] antidote_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                CustomItem.PURGE_ESSENCE.getItem(),
        };
        recipes.put(CustomItem.ANTIDOTE.getItem(), antidote_recipe);


        ItemStack[] mana_potion_recipe = {
                new ItemStack(Material.GLASS_BOTTLE),
                CustomItem.MAGIC_ESSENCE.getItem(),
        };
        recipes.put(CustomItem.MANA_POTION.getItem(), mana_potion_recipe);


        ItemStack[] tainted_powder_recipe = {
                new ItemStack(Material.BONE_MEAL),
                new ItemStack(Material.SUGAR),
        };
        recipes.put(CustomItem.TAINTED_POWDER.getItem(), tainted_powder_recipe);


    }

    public void fillOtherDescs (HashMap<ItemStack, ItemStack> descs) {
        ItemStack fishing_crate_desc = new ItemStack(Material.FISHING_ROD);
        ItemMeta fishing_crate_meta = fishing_crate_desc.getItemMeta();
        fishing_crate_meta.setDisplayName("§1Fishing");
        ArrayList<String> fishing_crate_lore = new ArrayList<>();
        fishing_crate_lore.add("§7This item can be obtained by fishing in Prehardmode.");
        fishing_crate_meta.setLore(fishing_crate_lore);
        fishing_crate_desc.setItemMeta(fishing_crate_meta);
        descs.put(CustomItem.FISHING_CRATE.getItem(), fishing_crate_desc);


        ItemStack fishing_crate_hardmode_desc = new ItemStack(Material.FISHING_ROD);
        ItemMeta fishing_crate_hardmode_meta = fishing_crate_hardmode_desc.getItemMeta();
        fishing_crate_hardmode_meta.setDisplayName("§1Fishing");
        ArrayList<String> fishing_crate_hardmode_lore = new ArrayList<>();
        fishing_crate_hardmode_lore.add("§7This item can be obtained by fishing in Hardmode.");
        fishing_crate_hardmode_meta.setLore(fishing_crate_hardmode_lore);
        fishing_crate_hardmode_desc.setItemMeta(fishing_crate_hardmode_meta);
        descs.put(CustomItem.FISHING_CRATE_HARDMODE.getItem(), fishing_crate_hardmode_desc);


        ItemStack zombie_soul_desc = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta zombie_soul_meta = zombie_soul_desc.getItemMeta();
        zombie_soul_meta.setDisplayName("§2Zombie Drops");
        ArrayList<String> zombie_soul_lore = new ArrayList<>();
        zombie_soul_lore.add("§7This item can be obtained by killing Zombies.");
        zombie_soul_meta.setLore(zombie_soul_lore);
        zombie_soul_desc.setItemMeta(zombie_soul_meta);
        descs.put(CustomItem.ZOMBIE_SOUL.getItem(), zombie_soul_desc);


        ItemStack creeper_soul_desc = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta creeper_soul_meta = creeper_soul_desc.getItemMeta();
        creeper_soul_meta.setDisplayName("§aCreeper Drops");
        ArrayList<String> creeper_soul_lore = new ArrayList<>();
        creeper_soul_lore.add("§7This item can be obtained by killing Creepers.");
        creeper_soul_meta.setLore(creeper_soul_lore);
        creeper_soul_desc.setItemMeta(creeper_soul_meta);
        descs.put(CustomItem.CREEPER_SOUL.getItem(), creeper_soul_desc);


        ItemStack skeleton_soul_desc = new ItemStack(Material.SKELETON_SPAWN_EGG);
        ItemMeta skeleton_soul_meta = skeleton_soul_desc.getItemMeta();
        skeleton_soul_meta.setDisplayName("§8Skeleton Drops");
        ArrayList<String> skeleton_soul_lore = new ArrayList<>();
        skeleton_soul_lore.add("§7This item can be obtained by killing Skeletons.");
        skeleton_soul_meta.setLore(skeleton_soul_lore);
        skeleton_soul_desc.setItemMeta(skeleton_soul_meta);
        descs.put(CustomItem.SKELETON_SOUL.getItem(), skeleton_soul_desc);


        ItemStack spider_soul_desc = new ItemStack(Material.SPIDER_SPAWN_EGG);
        ItemMeta spider_soul_meta = spider_soul_desc.getItemMeta();
        spider_soul_meta.setDisplayName("§4Spider Drops");
        ArrayList<String> spider_soul_lore = new ArrayList<>();
        spider_soul_lore.add("§7This item can be obtained by killing Spiders.");
        spider_soul_meta.setLore(spider_soul_lore);
        spider_soul_desc.setItemMeta(spider_soul_meta);
        descs.put(CustomItem.SPIDER_SOUL.getItem(), spider_soul_desc);


        ItemStack pigzombie_soul_desc = new ItemStack(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        ItemMeta pigzombie_soul_meta = pigzombie_soul_desc.getItemMeta();
        pigzombie_soul_meta.setDisplayName("§dZombified Piglin Drops");
        ArrayList<String> pigzombie_soul_lore = new ArrayList<>();
        pigzombie_soul_lore.add("§7This item can be obtained by killing Zombified Piglins.");
        pigzombie_soul_meta.setLore(pigzombie_soul_lore);
        pigzombie_soul_desc.setItemMeta(pigzombie_soul_meta);
        descs.put(CustomItem.PIGZOMBIE_SOUL.getItem(), pigzombie_soul_desc);


        ItemStack ghast_soul_desc = new ItemStack(Material.GHAST_SPAWN_EGG);
        ItemMeta ghast_soul_meta = ghast_soul_desc.getItemMeta();
        ghast_soul_meta.setDisplayName("§fGhast Drops");
        ArrayList<String> ghast_soul_lore = new ArrayList<>();
        ghast_soul_lore.add("§7This item can be obtained by killing Ghasts.");
        ghast_soul_meta.setLore(ghast_soul_lore);
        ghast_soul_desc.setItemMeta(ghast_soul_meta);
        descs.put(CustomItem.GHAST_SOUL.getItem(), ghast_soul_desc);


        ItemStack silverfish_soul_desc = new ItemStack(Material.SILVERFISH_SPAWN_EGG);
        ItemMeta silverfish_soul_meta = silverfish_soul_desc.getItemMeta();
        silverfish_soul_meta.setDisplayName("§7Silverfish Drops");
        ArrayList<String> silverfish_soul_lore = new ArrayList<>();
        silverfish_soul_lore.add("§7This item can be obtained by killing Silverfish.");
        silverfish_soul_meta.setLore(silverfish_soul_lore);
        silverfish_soul_desc.setItemMeta(silverfish_soul_meta);
        descs.put(CustomItem.SILVERFISH_SOUL.getItem(), silverfish_soul_desc);


        ItemStack witch_soul_desc = new ItemStack(Material.WITCH_SPAWN_EGG);
        ItemMeta witch_soul_meta = witch_soul_desc.getItemMeta();
        witch_soul_meta.setDisplayName("§5Witch Drops");
        ArrayList<String> witch_soul_lore = new ArrayList<>();
        witch_soul_lore.add("§7This item can be obtained by killing Witches.");
        witch_soul_meta.setLore(witch_soul_lore);
        witch_soul_desc.setItemMeta(witch_soul_meta);
        descs.put(CustomItem.WITCH_SOUL.getItem(), witch_soul_desc);


        ItemStack phantom_soul_desc = new ItemStack(Material.PHANTOM_SPAWN_EGG);
        ItemMeta phantom_soul_meta = phantom_soul_desc.getItemMeta();
        phantom_soul_meta.setDisplayName("§9Phantom Drops");
        ArrayList<String> phantom_soul_lore = new ArrayList<>();
        phantom_soul_lore.add("§7This item can be obtained by killing Phantoms.");
        phantom_soul_meta.setLore(phantom_soul_lore);
        phantom_soul_desc.setItemMeta(phantom_soul_meta);
        descs.put(CustomItem.PHANTOM_SOUL.getItem(), phantom_soul_desc);


        ItemStack soul_crystal_desc = CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem();
        ItemMeta soul_crystal_meta = soul_crystal_desc.getItemMeta();
        soul_crystal_meta.setDisplayName("§7Complete Soul Crystal");
        ArrayList<String> soul_crystal_lore = new ArrayList<>();
        soul_crystal_lore.add("§7This item is obtained by defeating yourself in the World of Reflection.");
        soul_crystal_meta.setLore(soul_crystal_lore);
        soul_crystal_desc.setItemMeta(soul_crystal_meta);
        descs.put(CustomItem.SOUL_CRYSTAL.getItem(), soul_crystal_desc);


        ItemStack rune_desc = new ItemStack(Material.SPAWNER);
        ItemMeta rune_meta = rune_desc.getItemMeta();
        rune_meta.setDisplayName("§2Dungeon Loot");
        ArrayList<String> rune_lore = new ArrayList<>();
        rune_lore.add("§7This item is obtained by destroying spawners.");
        rune_meta.setLore(rune_lore);
        rune_desc.setItemMeta(rune_meta);
        descs.put(CustomItem.RUNE.getItem(), rune_desc);


        ItemStack charged_rune_desc = CustomItem.RUNE.getItem();
        ItemMeta charged_rune_meta = charged_rune_desc.getItemMeta();
        charged_rune_meta.setDisplayName("§dCharge Rune");
        ArrayList<String> charged_rune_lore = new ArrayList<>();
        charged_rune_lore.add("§7This item is obtained by charging the Rune with Merged Souls and Essences of Magic.");
        charged_rune_meta.setLore(charged_rune_lore);
        charged_rune_desc.setItemMeta(charged_rune_meta);
        descs.put(CustomItem.CHARGED_RUNE.getItem(), charged_rune_desc);


        ItemStack recall_potion_desc = rune_desc.clone();
        descs.put(CustomItem.RECALL_POTION.getItem(), recall_potion_desc);


        ItemStack titanium_fragment_desc = new ItemStack(Material.DEAD_TUBE_CORAL_BLOCK);
        ItemMeta titanium_fragment_meta = titanium_fragment_desc.getItemMeta();
        titanium_fragment_meta.setDisplayName("§bMine Titanium Ore");
        ArrayList<String> titanium_fragment_lore = new ArrayList<>();
        titanium_fragment_lore.add("§7This item is obtained by mining Titanium Ore,");
        titanium_fragment_lore.add("§7which generates right above The Void.");
        titanium_fragment_meta.setLore(titanium_fragment_lore);
        titanium_fragment_desc.setItemMeta(titanium_fragment_meta);
        descs.put(CustomItem.TITANIUM_FRAGMENT.getItem(), titanium_fragment_desc);


        ItemStack forever_fish_desc = CustomItem.FISHING_CRATE_HARDMODE.getItem();
        ItemMeta forever_fish_meta = forever_fish_desc.getItemMeta();
        ArrayList<String> forever_fish_lore = new ArrayList<>();
        forever_fish_lore.add("§7This item is Tier 4 Hardmode Fishing Crate loot.");
        forever_fish_meta.setLore(forever_fish_lore);
        forever_fish_desc.setItemMeta(forever_fish_meta);
        descs.put(CustomItem.FOREVER_FISH.getItem(), forever_fish_desc);

        ItemStack voidmatter_desc = new ItemStack(Material.ENDER_PEARL);
        ItemMeta voidmatter_meta = voidmatter_desc.getItemMeta();
        voidmatter_meta.setDisplayName("§eExtradimensional Loot");
        ArrayList<String> voidmatter_lore = new ArrayList<>();
        voidmatter_lore.add("§7This item is obtained by raiding other dimensions.");
        voidmatter_meta.setLore(voidmatter_lore);
        voidmatter_desc.setItemMeta(voidmatter_meta);
        descs.put(CustomItem.VOIDMATTER.getItem(), voidmatter_desc);

    }
}