package me.fullpotato.badlandscaves.CustomItems;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.Loot.TreasureGear;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopCustomItemsInteract implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final TreasureGear treasureGear = new TreasureGear();
    private final StarlightCharge starlightCharge;
    private final Voidmatter voidmatter;
    private final List<Material> repairTypes = Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_AXE, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS, Material.NETHERITE_SWORD, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_AXE, Material.BOW, Material.CROSSBOW, Material.FISHING_ROD);

    public StopCustomItemsInteract(BadlandsCaves plugin, StarlightCharge starlightCharge, Voidmatter voidmatter) {
        this.plugin = plugin;
        this.customItemManager = plugin.getCustomItemManager();
        this.starlightCharge = starlightCharge;
        this.voidmatter = voidmatter;
    }

    @EventHandler
    public void preventRightClick (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = event.getItem();
            if (item != null) {
                if (item.getType().equals(Material.KNOWLEDGE_BOOK)) {
                    event.setUseItemInHand(Event.Result.DENY);
                    return;
                }

                for (CustomItem customItem : CustomItem.values()) {
                    if (item.isSimilar(customItemManager.getItem(customItem)) && customItem.getPreventUse()) {
                        event.setUseItemInHand(Event.Result.DENY);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventRightClickEntity (PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final ItemStack[] items = {
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand(),
        };

        for (ItemStack item : items) {
            if (item != null) {
                if (item.getType().equals(Material.KNOWLEDGE_BOOK)) {
                    event.setCancelled(true);
                    return;
                }

                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    for (CustomItem customItem : CustomItem.values()) {
                        if (item.isSimilar(customItemManager.getItem(customItem)) && customItem.getPreventUse()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventPlace (BlockPlaceEvent event) {
        final ItemStack item = event.getItemInHand();
        for (CustomItem customItem : CustomItem.values()) {
            if (item.isSimilar(customItemManager.getItem(customItem)) && customItem.getPreventUse()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventAnvil (PrepareAnvilEvent event) {
        final AnvilInventory inventory = event.getInventory();

        for (ItemStack item : inventory) {
            if (isCustomItem(item)) {
                event.setResult(null);
                event.getViewers().forEach(humanEntity -> {
                    if (humanEntity instanceof Player) {
                        final Player player = (Player) humanEntity;
                        player.updateInventory();
                    }
                });

                return;
            }
        }
    }

    @EventHandler
    public void preventGrindstone (InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory instanceof GrindstoneInventory) {
            final GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inventory;
            final Location location = grindstoneInventory.getLocation();
            if (location != null && location.getBlock().getType().equals(Material.GRINDSTONE) && event.getSlot() == 2) {
                final ItemStack[] items = {
                        grindstoneInventory.getItem(0),
                        grindstoneInventory.getItem(1),
                };

                for (ItemStack item : items) {
                    if (item != null) {
                        final ItemMeta meta = item.getItemMeta();
                        if (meta != null && meta.hasEnchants() && isCustomItem(item)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventRepairing (PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getInventory().getResult() != null) {
            final ItemStack result = event.getInventory().getResult();
            final Material type = result.getType();
            if (repairTypes.contains(type)) {
                final ItemStack[] matrix = event.getInventory().getMatrix();
                final List<ItemStack> armors = new ArrayList<>();
                for (ItemStack item : matrix) {
                    if (item != null && item.getType().equals(type)) {
                        armors.add(item);
                    }
                }

                if (armors.size() >= 2) {
                    for (ItemStack armor : armors) {
                        if (treasureGear.isTreasureGear(armor) || starlightCharge.isStarlight(armor) || voidmatter.isVoidmatter(armor)) {
                            event.getInventory().setResult(null);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isCustomItem(ItemStack item) {
        if (item != null && item.getItemMeta() != null) {
            if (treasureGear.isTreasureGear(item) || starlightCharge.isStarlight(item) || voidmatter.isVoidmatter(item))
                return true;

            for (CustomItem value : CustomItem.values()) {
                if (item.isSimilar(customItemManager.getItem(value))) return true;
            }
        }

        return false;
    }
}
