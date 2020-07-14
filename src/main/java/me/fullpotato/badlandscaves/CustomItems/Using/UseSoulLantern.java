package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.SoulLantern;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class UseSoulLantern implements Listener {
    private final BadlandsCaves plugin;
    private final String title = ChatColor.of("#03969a") + "Soul Lantern";
    private final NamespacedKey key;
    private final CustomItem[] souls = {
            CustomItem.ZOMBIE_SOUL,
            CustomItem.CREEPER_SOUL,
            CustomItem.SKELETON_SOUL,
            CustomItem.SPIDER_SOUL,
            CustomItem.PIGZOMBIE_SOUL,
            CustomItem.GHAST_SOUL,
            CustomItem.SILVERFISH_SOUL,
            CustomItem.WITCH_SOUL,
            CustomItem.PHANTOM_SOUL,
            CustomItem.MERGED_SOULS,
    };


    public UseSoulLantern(BadlandsCaves plugin) {
        this.plugin = plugin;
        key = new NamespacedKey(plugin, "soul_lantern_items");
    }

    @EventHandler
    public void openMenu (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                ItemStack item = event.getItem();
                if (item != null) {
                    SoulLantern lantern = new SoulLantern(plugin);
                    if (lantern.isSoulLantern(item)) {
                        final Player player = event.getPlayer();
                        event.setCancelled(true);
                        openInventory(player, item);
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventInputNonSoulItem (InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title)) {
            SoulLantern soulLanternManager = new SoulLantern(plugin);
            ItemStack lantern = event.getCurrentItem();
            if (soulLanternManager.isSoulLantern(lantern)) {
                event.setCancelled(true);
                return;
            }

            final Inventory clickedInventory = event.getClickedInventory();
            final Inventory top = event.getInventory();
            if (clickedInventory != null) {
                InventoryAction action = event.getAction();
                if (clickedInventory.equals(top)) {
                    if (action.equals(InventoryAction.PLACE_ALL) ||
                            action.equals(InventoryAction.PLACE_ONE) ||
                            action.equals(InventoryAction.PLACE_SOME) ||
                            action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                        ItemStack item = event.getCursor();

                        if (getCustomItemFromSoulItemStack(item) == null) {
                            event.setCancelled(true);
                        }
                    }
                }
                else {
                    if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        ItemStack item = event.getCurrentItem();

                        if (getCustomItemFromSoulItemStack(item) == null) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventInputNonSoulItemDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(title)) {
            final CustomItem item = getCustomItemFromSoulItemStack(event.getOldCursor());
            if (item == null) {
                boolean affectsSoulLantern = false;
                for (int slot : event.getRawSlots()) {
                    if (slot >= 0 && slot <= 26) {
                        affectsSoulLantern = true;
                        break;
                    }
                }
                if (affectsSoulLantern) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void closeMenu (InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(title)) {
            final Player player = (Player) event.getPlayer();
            final Inventory inventory = event.getInventory();
            final SoulLantern soulLantern = new SoulLantern(plugin);
            ItemStack lantern = player.getInventory().getItemInMainHand();
            if (!soulLantern.isSoulLantern(lantern)) {
                lantern = player.getInventory().getItemInOffHand();
            }

            if (soulLantern.isSoulLantern(lantern)) {
                String string = convertSoulsToString(inventory.getContents());
                ItemMeta meta = lantern.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, string);

                lantern.setItemMeta(meta);
            }
        }
    }

    public void openInventory (Player player, ItemStack item) {
        Inventory inventory = plugin.getServer().createInventory(player, 27, title);
        String string = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (string != null) {
            inventory.setContents(convertStringToSouls(string));
        }

        player.openInventory(inventory);
    }

    public String convertSoulsToString (ItemStack[] items) {
        //format SLOT:ITEMNAME#AMOUNT;
        //ex) 0:ZOMBIE_SOUL#5;1:CREEPER_SOUL#2;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                CustomItem item = getCustomItemFromSoulItemStack(items[i]);
                if (item != null) {
                    builder.append(i).append(":").append(item.name()).append("#").append(items[i].getAmount()).append(";");
                }
            }
        }

        return builder.toString();
    }

    public ItemStack[] convertStringToSouls(String string) {
        final ItemStack[] output = new ItemStack[27];
        if (string.isEmpty()) return output;

        //0:ZOMBIE_SOUL#5;1:CREEPER_SOUL#2;
        final String[] eachEntry = string.split(";");

        //0:ZOMBIE_SOUL#5
        for (String entry : eachEntry) {
            final String[] separateSlotFromItem = entry.split(":");
            final String slotStr = separateSlotFromItem[0];

            final String[] separateNameFromAmount = separateSlotFromItem[1].split("#");
            final String name = separateNameFromAmount[0];
            final String amountStr = separateNameFromAmount[1];

            try {
                final int slot = Integer.parseInt(slotStr);
                final int amount = Integer.parseInt(amountStr);
                output[slot] = CustomItem.valueOf(name).getItem();
                output[slot].setAmount(amount);
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }

        return output;
    }

    public CustomItem getCustomItemFromSoulItemStack(ItemStack item) {
        for (CustomItem soul : souls) {
            if (item.isSimilar(soul.getItem())) {
                return soul;
            }
        }
        return null;
    }
}
