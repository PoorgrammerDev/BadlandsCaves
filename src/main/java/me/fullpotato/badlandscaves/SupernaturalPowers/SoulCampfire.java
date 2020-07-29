package me.fullpotato.badlandscaves.SupernaturalPowers;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactBaseItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.ActivePowers;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.SwapPowers;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoulCampfire implements Listener {
    private final BadlandsCaves plugin;
    private final SwapPowers swapPowers;
    private final ArtifactManager artifactManager;
    private final Map<Integer, ArtifactBaseItem> artifactSlots;
    private final ItemStack emptyWhite = EmptyItem.getEmptyItem(Material.WHITE_STAINED_GLASS_PANE);
    private final ItemStack emptyBlack = EmptyItem.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
    private final ItemStack selectedIndicator = getSelectedIndicator();
    private final ItemStack orderChangeExplanation = getOrderChangeIcon();
    private final ItemStack optionsExplanation = getOptionsIcon();
    private final ItemStack homeButton = getHomeButton();
    private final ItemStack doubleShiftIconOn = getDoubleShiftIcon(true);
    private final ItemStack doubleShiftIconOff = getDoubleShiftIcon(false);
    private final ItemStack swapCooldownIconOn = getSwapCooldownIcon(true);
    private final ItemStack swapCooldownIconOff = getSwapCooldownIcon(false);
    private final String title = ChatColor.of("#03969a") + "Soul Campfire";

    enum Menu {
        MAIN_MENU,
        SWAP_ORDER,
        ARTIFACTS,
        OTHER_OPTIONS,
    }

    public SoulCampfire(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.swapPowers = new SwapPowers(plugin);
        this.artifactManager = new ArtifactManager(plugin);

        this.artifactSlots = new HashMap<>();
        artifactSlots.put(11, ArtifactBaseItem.VOIDMATTER_ARMOR);
        artifactSlots.put(20, ArtifactBaseItem.VOIDMATTER_BLADE);
        artifactSlots.put(29, ArtifactBaseItem.VOIDMATTER_BOW);
        artifactSlots.put(38, ArtifactBaseItem.VOIDMATTER_TOOLS);
        artifactSlots.put(15, ArtifactBaseItem.DISPLACE);
        artifactSlots.put(24, ArtifactBaseItem.WITHDRAW);
        artifactSlots.put(33, ArtifactBaseItem.ENHANCED_EYES);
        artifactSlots.put(42, ArtifactBaseItem.POSSESSION);

    }

    @EventHandler
    public void campfireInteract (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();
            if (block != null && block.getType().equals(Material.SOUL_CAMPFIRE)) {
                final Player player = event.getPlayer();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                    if (PlayerScore.SWAP_WINDOW.hasScore(plugin, player) && (byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) != 0) {
                        event.setCancelled(true);
                        openInventory(player);
                    }
                }
            }
        }
    }

    public void openInventory(Player player) {
        final Inventory inventory = plugin.getServer().createInventory(player, 54, title);
        changeMenu(player, inventory, Menu.MAIN_MENU);

        player.openInventory(inventory);
    }

    public void changeMenu (Player player, Inventory inventory, Menu menu) {
        inventory.clear();
        //BOTTOM ROW
        inventory.setItem(45, getMenuMarker(menu));
        for (int i = 46; i < 53; i++) {
            inventory.setItem(i, emptyWhite);
        }
        inventory.setItem(53, homeButton);

        if (menu.equals(Menu.MAIN_MENU)) {
            inventory.setItem(10, orderChangeExplanation);
            inventory.setItem(12, optionsExplanation);
            inventory.setItem(14, getArtifactsIcon(plugin.getSystemConfig().getBoolean("hardmode")));
            //inventory.setItem(16, );
            //inventory.setItem(28, );
            //inventory.setItem(30, );
            //inventory.setItem(32, );
            //inventory.setItem(34, );
        }
        else if (menu.equals(Menu.SWAP_ORDER)) {
            for (int i = 0; i < 45; i++) {
                inventory.setItem(i, emptyBlack);
            }
            inventory.setItem(13, orderChangeExplanation);

            final ActivePowers[] order = swapPowers.getSwapOrder(player);
            for (int i = 0; i < order.length; i++) {
                inventory.setItem(28 + (2 * i), order[i].getItem().getItem());
            }
        }
        else if (menu.equals(Menu.OTHER_OPTIONS)) {
            for (int i = 0; i < 45; i++) {
                inventory.setItem(i, emptyBlack);
            }

            inventory.setItem(13, optionsExplanation);
            inventory.setItem(28, (byte) PlayerScore.SWAP_DOUBLESHIFT_OPTION.getScore(plugin, player) == 1 ? doubleShiftIconOn : doubleShiftIconOff);
            inventory.setItem(30, (byte) PlayerScore.SWAP_COOLDOWN_OPTION.getScore(plugin, player) == 1 ? swapCooldownIconOn : swapCooldownIconOff);
        }
        else if (menu.equals(Menu.ARTIFACTS)) {
            for (int i = 0; i < 9; i++) {
                inventory.setItem(i, emptyWhite);
            }
            for (int i = 9; i < 45; i++) {
                inventory.setItem(i, emptyBlack);
            }

            inventory.setItem(13, getArtifactsIcon(true));

            final Map<ArtifactBaseItem, Artifact> artifactMap = artifactManager.getArtifacts(player);
            artifactSlots.forEach((slot, baseItem) -> {
                if (artifactMap.containsKey(baseItem)) {
                    inventory.setItem(slot, artifactMap.get(baseItem).getArtifactItem().getItem());
                }
                else {
                    inventory.setItem(slot, getArtifactBaseItemIcons(baseItem));
                }
            });

        }
    }

    @EventHandler
    public void menuInteract (InventoryClickEvent event) {
        final String title = event.getView().getTitle();
        if (title.contains(this.title)) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory inventory = event.getClickedInventory();
            final ItemStack currentItem = event.getCurrentItem();
            final int slot = event.getSlot();
            if (inventory != null) {
                if (inventory.equals(event.getView().getTopInventory())) {
                    event.setCancelled(true);

                    //home button
                    if (currentItem != null && currentItem.isSimilar(homeButton)) {
                        saveSettings(player, inventory);
                        changeMenu(player, inventory, Menu.MAIN_MENU);
                    }

                    else if (getMenuType(inventory).equals(Menu.MAIN_MENU)) {
                        if (currentItem != null) {
                            if (currentItem.isSimilar(orderChangeExplanation)) {
                                changeMenu(player, inventory, Menu.SWAP_ORDER);
                            }
                            else if (currentItem.isSimilar(optionsExplanation)) {
                                changeMenu(player, inventory, Menu.OTHER_OPTIONS);
                            }
                            else if (currentItem.isSimilar(getArtifactsIcon(true))) {
                                changeMenu(player, inventory, Menu.ARTIFACTS);
                            }
                        }
                    }
                    else if (getMenuType(inventory).equals(Menu.SWAP_ORDER)) {
                        //Select Spells
                        if (currentItem != null) {
                            if (isSpellItem(currentItem)) {
                                for (int i = 19; i < 27; i++) {
                                    ItemStack iter = inventory.getItem(i);
                                    if (iter != null && iter.isSimilar(selectedIndicator)) {
                                        final ItemStack otherSpell = inventory.getItem(i + 9);

                                        //swap spells
                                        inventory.setItem(i + 9, currentItem);
                                        inventory.setItem(slot, otherSpell);

                                        //clear indicators
                                        inventory.setItem(i, emptyBlack);
                                        inventory.setItem(slot - 9, emptyBlack);
                                        return;
                                    }
                                }

                                inventory.setItem(slot - 9, selectedIndicator);
                            }
                        }
                    }
                    else if (getMenuType(inventory).equals(Menu.OTHER_OPTIONS)) {
                        if (currentItem != null) {
                            //Double Shift Option
                            if (currentItem.isSimilar(doubleShiftIconOn)) {
                                inventory.setItem(slot, doubleShiftIconOff);
                            }
                            else if (currentItem.isSimilar(doubleShiftIconOff)) {
                                inventory.setItem(slot, doubleShiftIconOn);
                            }

                            //Swap Cooldown Option
                            else if (currentItem.isSimilar(swapCooldownIconOn)) {
                                inventory.setItem(slot, swapCooldownIconOff);
                            }
                            else if (currentItem.isSimilar(swapCooldownIconOff)) {
                                inventory.setItem(slot, swapCooldownIconOn);
                            }
                        }
                    }
                    else if (getMenuType(inventory).equals(Menu.ARTIFACTS)) {
                        // TODO: 7/28/2020
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeInventory (InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(title)) {
            final Inventory inventory = event.getInventory();
            final Player player = (Player) event.getPlayer();

            saveSettings(player, inventory);
        }
    }

    public void saveSettings (Player player, Inventory inventory) {
        final Menu type = getMenuType(inventory);

        if (type.equals(Menu.SWAP_ORDER)) {
            //UPDATE SWAP POWER ORDER
            final ItemStack[] powerItemOrder = {
                    inventory.getItem(28),
                    inventory.getItem(30),
                    inventory.getItem(32),
                    inventory.getItem(34),
            };

            final ActivePowers[] values = ActivePowers.values();
            final ActivePowers[] powerOrder = new ActivePowers[4];
            for (int i = 0; i < powerItemOrder.length; i++) {
                for (ActivePowers value : values) {
                    if (powerItemOrder[i] != null && powerItemOrder[i].isSimilar(value.getItem().getItem())) {
                        powerOrder[i] = value;
                    }
                }
            }
            swapPowers.setSwapOrder(player, powerOrder);
        }
        else if (type.equals(Menu.OTHER_OPTIONS)) {
            //UPDATE DOUBLESHIFT OPTION
            final ItemStack doubleshift = inventory.getItem(28);
            if (doubleshift != null) {
                if (doubleshift.isSimilar(doubleShiftIconOn)) {
                    PlayerScore.SWAP_DOUBLESHIFT_OPTION.setScore(plugin, player, 1);
                }
                else if (doubleshift.isSimilar(doubleShiftIconOff)) {
                    PlayerScore.SWAP_DOUBLESHIFT_OPTION.setScore(plugin, player, 0);
                }
            }

            final ItemStack swapCooldown = inventory.getItem(30);
            if (swapCooldown != null) {
                if (swapCooldown.isSimilar(swapCooldownIconOn)) {
                    PlayerScore.SWAP_COOLDOWN_OPTION.setScore(plugin, player, 1);
                }
                else if (swapCooldown.isSimilar(swapCooldownIconOff)) {
                    PlayerScore.SWAP_COOLDOWN_OPTION.setScore(plugin, player, 0);
                }
            }
        }
        else if (type.equals(Menu.ARTIFACTS)) {
            final Map<ArtifactBaseItem, Artifact> artifactMap = new HashMap<>();

            artifactSlots.forEach((slot, baseItem) -> {
                final ItemStack item = inventory.getItem(slot);
                if (item != null && artifactManager.isArtifact(item)) {
                    artifactMap.put(baseItem, artifactManager.toArtifact(item));
                }
            });

            artifactManager.setArtifacts(player, artifactMap);
        }
    }

    public boolean isSpellItem (ItemStack item) {
        for (ActivePowers value : ActivePowers.values()) {
            if (item.isSimilar(value.getItem().getItem())) return true;
        }
        return false;
    }

    public ItemStack getOrderChangeIcon() {
        final ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Change Swap Order");
        meta.setCustomModelData(228);

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Change the order of swapping between powers here.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getOptionsIcon() {
        final ItemStack item = new ItemStack(Material.PAPER);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Extra Options");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Change extra options regarding Supernatural Powers here.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getArtifactsIcon(boolean hardmode) {
        final ItemStack item = hardmode ? new ItemStack(Material.KNOWLEDGE_BOOK) : new ItemStack(Material.BARRIER);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Artifacts");
        meta.setCustomModelData(223);

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(hardmode ? ChatColor.GRAY + "Manage Artifacts here." : ChatColor.RED + "Locked until Hardmode.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getArtifactBaseItemIcons (ArtifactBaseItem artifactBaseItem) {
        final String[] split = artifactBaseItem.name().split("_");
        final StringBuilder builder = new StringBuilder();
        for (String str : split) {
            builder.append(str, 0, 1).append(str.substring(1).toLowerCase()).append(" ");
        }

        final ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.BOLD + "Artifacts For " + builder.toString());
        meta.setCustomModelData(Math.min(artifactBaseItem.ordinal() + 223, 228));

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getSelectedIndicator () {
        final ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Selected");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This power has been selected.");
        lore.add(ChatColor.GRAY + "Select another power to swap places.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getDoubleShiftIcon(boolean activated) {
        final ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Doubleshift");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This is activated by default.");
        lore.add(ChatColor.GRAY + "Shift / sneak twice to access powers.");
        lore.add(ChatColor.GRAY + "When this is disabled, you only have to shift / sneak once.");

        if (activated) {
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVATED");
        }
        else {
            lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "DEACTIVATED");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getSwapCooldownIcon(boolean activated) {
        final ItemStack item = new ItemStack(Material.CLOCK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Swap Cooldown");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This is activated by default.");
        lore.add(ChatColor.GRAY + "A very short cooldown prevents you from");
        lore.add(ChatColor.GRAY + "switching between multiple powers quickly.");
        lore.add(ChatColor.GRAY + "This is meant to prevent accidental over-switching.");

        if (activated) {
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVATED");
        }
        else {
            lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "DEACTIVATED");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getHomeButton () {
        final ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Home");

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getMenuMarker (Menu menu) {
        final ItemStack item = emptyWhite.clone();
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "menu"), PersistentDataType.STRING, menu.name());

        item.setItemMeta(meta);
        return item;
    }

    public Menu getMenuType (Inventory inventory) {
        final ItemStack marker = inventory.getItem(45);
        if (marker != null) {
            final ItemMeta meta = marker.getItemMeta();
            try {
                return Menu.valueOf(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "menu"), PersistentDataType.STRING));
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
