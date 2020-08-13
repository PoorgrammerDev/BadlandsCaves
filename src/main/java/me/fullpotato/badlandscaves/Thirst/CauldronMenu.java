package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.LocationSave;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Set;

public class CauldronMenu implements Listener {
    private final BadlandsCaves plugin;
    private final String title = ChatColor.DARK_GRAY + "Cauldron";
    private final ItemStack gray = EmptyItem.getEmptyItem(Material.GRAY_STAINED_GLASS_PANE);
    private final ItemStack black = EmptyItem.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
    private final ItemStack notReadyButton = getNotReadyButton();
    private final int[] blackBarSlots = {7, 10, 16, 19, 25};
    private final int[][] levelIndicatorSlots = {{18, 26}, {9, 17}, {0, 8}};
    private final int MAX_LEVEL = ((Levelled) (Material.CAULDRON.createBlockData())).getMaximumLevel();
    private final NamespacedKey waterLevelKey;
    private final NamespacedKey recipeKey;
    private final NamespacedKey locationKey;
    private final LocationSave locationSave;

    public CauldronMenu(BadlandsCaves plugin) {
        this.plugin = plugin;
        waterLevelKey = new NamespacedKey(plugin, "level");
        recipeKey = new NamespacedKey(plugin, "recipe");
        locationKey = new NamespacedKey(plugin, "location");
        locationSave = new LocationSave(plugin);

    }

    @EventHandler
    public void openCauldron(PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            final EquipmentSlot hand = event.getHand();
            if (hand != null && hand.equals(EquipmentSlot.HAND)) {
                final Block block = event.getClickedBlock();
                if (block != null && block.getType().equals(Material.CAULDRON)) {
                    final Player player = event.getPlayer();
                    final Block under = block.getRelative(BlockFace.DOWN);
                    if (under.getType().equals(Material.FIRE)) {
                        if (block.getBlockData() instanceof Levelled) {
                            final Levelled levelled = (Levelled) block.getBlockData();
                            final Material handMat = event.getMaterial();
                            final int level = levelled.getLevel();
                            final boolean cancel = (level > 0 && (handMat.equals(Material.BUCKET) || handMat.equals(Material.GLASS_BOTTLE)))
                                    || (level < MAX_LEVEL && (handMat.equals(Material.WATER_BUCKET) || handMat.equals(Material.POTION)));

                            if (!cancel) {
                                final Inventory inventory = openInventory(player, block.getLocation(), level);
                                new CauldronRunnable(plugin, player, block.getLocation(), inventory, this).runTaskTimer(plugin, 0, 10);
                            }
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Light a fire underneath the Cauldron to use it.");
                    }
                }
            }
        }
    }

    public Inventory openInventory (Player player, Location cauldronLocation, int level) {
        final Inventory inventory = plugin.getServer().createInventory(player, 27, title);

        //background
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, gray);
        }

        //black bars
        for (int blackBarSlot : blackBarSlots) {
            inventory.setItem(blackBarSlot, black);
        }

        updateWaterLevel(inventory, level);

        //slots
        inventory.setItem(11, null);
        inventory.setItem(15, null);

        //craft button
        inventory.setItem(13, notReadyButton);

        //location save item
        final ItemStack locationSave = black.clone();
        saveLocationInsideItem(locationSave, cauldronLocation);
        inventory.setItem(1, locationSave);

        player.openInventory(inventory);
        return inventory;
    }

    public void updateWaterLevel(Inventory inventory, int level) {
        //water level indicator
        final ItemStack waterIndicator = getWaterIndicator(level, false);
        final ItemStack waterIndicatorClear = getWaterIndicator(level, true);
        for (int i = 0; i < levelIndicatorSlots.length; i++) {
            for (int slot : levelIndicatorSlots[i]) {
                inventory.setItem(slot, i < level ? waterIndicator : waterIndicatorClear);
            }
        }
    }

    public ItemStack getWaterIndicator(int level, boolean empty) {
        final ItemStack item = new ItemStack(empty ? Material.GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();

        final ChatColor gray = ChatColor.of("#3b3b3b");
        final ChatColor color = (level > 0 ? ChatColor.of("#077809") : ChatColor.DARK_GRAY);
        meta.setDisplayName(color.toString() + ChatColor.BOLD + "Water Level");

        final ArrayList<String> lore = new ArrayList<>();
        final StringBuilder builder = new StringBuilder(gray + "[");

        if (level > 0) {
            builder.append(color);
            for (int i = 0; i < level; i++) {
                builder.append("▓▓▓");
            }
        }

        if (level < MAX_LEVEL) {
            builder.append(gray);
            for (int i = 0; i < MAX_LEVEL - level; i++) {
                builder.append("░░░");
            }
        }

        lore.add(builder.append(gray).append("]").toString());
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(waterLevelKey, PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void cauldronInventoryClick (InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title)) {
            if (event.getWhoClicked() instanceof Player) {
                final Player player = (Player) event.getWhoClicked();
                final Inventory clickedInventory = event.getClickedInventory();
                final Inventory topInventory = event.getView().getTopInventory();

                if (clickedInventory != null) {
                    if (clickedInventory.equals(topInventory)) {
                        final int slot = event.getSlot();
                        if (slot != 11 && slot != 15) {
                            event.setCancelled(true);
                        }
                        if (slot == 13) {
                            attemptCraft(topInventory, player);
                        }
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateCauldronStatus(topInventory);
                        }
                    }.runTaskLater(plugin, 1);
                }
            }
        }
    }

    public void updateCauldronStatus (Inventory inventory) {
        final ArrayList<ItemStack> slots = new ArrayList<>();
        slots.add(inventory.getItem(11));
        slots.add(inventory.getItem(15));

        if (slots.get(0) != null && slots.get(1) != null) {
            final int waterLevel = getWaterLevel(inventory);
            if (waterLevel >= MAX_LEVEL) {
                final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                final ArrayList<ItemStack> slotsClone = cloneItemList(slots);
                for (CauldronRecipe recipe : CauldronRecipe.values()) {
                    final RecipeAvailability availability = recipe.getRecipeAvailability();
                    if (availability.equals(RecipeAvailability.ALWAYS) ||
                            (availability.equals(RecipeAvailability.PREHARDMODE_ONLY) && !hardmode) ||
                            (availability.equals(RecipeAvailability.HARDMODE_ONLY) && hardmode)) {
                        final Set<ItemStack> ingredients = recipe.getIngredients();
                        if (ingredients.containsAll(slotsClone)) {
                            inventory.setItem(13, getReadyButton(recipe));
                            return;
                        }
                    }
                }
            }
        }
        inventory.setItem(13, notReadyButton);
    }

    public int getWaterLevel (Inventory inventory) {
        final ItemStack item = inventory.getItem(0);
        if (item != null) {
            return getWaterLevel(item);
        }
        throw new IllegalArgumentException("Invalid inventory");
    }

    public int getWaterLevel (ItemStack item) {
        final Material type = item.getType();
        if (type.equals(Material.GREEN_STAINED_GLASS_PANE) || type.equals(Material.GLASS_PANE)) {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta.getPersistentDataContainer().has(waterLevelKey, PersistentDataType.INTEGER)) {
                    final Integer result = meta.getPersistentDataContainer().get(waterLevelKey, PersistentDataType.INTEGER);
                    if (result != null) return result;
                }
            }
        }
        throw new IllegalArgumentException("Invalid water indicator item");
    }

    public ItemStack getReadyButton (CauldronRecipe recipe) {
        final ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Ready");

        final ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to craft " + recipe.getDisplayName() + ".");
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(recipeKey, PersistentDataType.STRING, recipe.name());
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getNotReadyButton () {
        final ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Not Ready");

        item.setItemMeta(meta);
        return item;
    }

    public ArrayList<ItemStack> cloneItemList (ArrayList<ItemStack> slots) {
        final ArrayList<ItemStack> slotsClone = new ArrayList<>();
        final ItemStack slotOneClone = slots.get(0).clone();
        final ItemStack slotTwoClone = slots.get(1).clone();
        slotOneClone.setAmount(1);
        slotTwoClone.setAmount(1);
        slotsClone.add(slotOneClone);
        slotsClone.add(slotTwoClone);

        return slotsClone;
    }

    public void attemptCraft (Inventory inventory, Player player) {
        final ItemStack craftButton = inventory.getItem(13);
        if (craftButton != null && craftButton.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            final CauldronRecipe recipe = getRecipeFromCraftButton(craftButton);
            if (recipe != null) {

                //check water level of cauldron block
                final Location cauldronLocation = getCauldronLocation(inventory);
                if (cauldronLocation != null) {
                    final Block block = cauldronLocation.getBlock();
                    if (block.getType().equals(Material.CAULDRON) && block.getBlockData() instanceof Levelled) {
                        final Levelled data = (Levelled) block.getBlockData();
                        if (data.getLevel() >= data.getMaximumLevel()) {
                            //CRAFT--------------------------------------

                            //set water level to 0
                            data.setLevel(0);
                            block.setBlockData(data);
                            updateWaterLevel(inventory, 0);

                            //deplete ingredients
                            final ItemStack[] slots = {
                                    inventory.getItem(11),
                                    inventory.getItem(15),
                            };

                            for (ItemStack slot : slots) {
                                if (slot != null) {
                                    slot.setAmount(slot.getAmount() - 1);
                                }
                            }

                            //give item
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItemNaturally(player.getLocation(), plugin.getCustomItemManager().getItem(recipe.getResult()));
                            }
                            else {
                                player.getInventory().addItem(plugin.getCustomItemManager().getItem(recipe.getResult()));
                            }

                            //sfx
                            World world = cauldronLocation.getWorld();
                            if (world == null) world = player.getWorld();
                            world.playSound(cauldronLocation, Sound.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1, 1);
                        }
                    }
                }
            }
        }
    }

    public CauldronRecipe getRecipeFromCraftButton (ItemStack item) {
        if (item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(recipeKey, PersistentDataType.STRING)) {
                String result = container.get(recipeKey, PersistentDataType.STRING);
                if (result != null && !result.isEmpty()) {
                    for (CauldronRecipe recipe : CauldronRecipe.values()) {
                        if (result.equals(recipe.name())) {
                            return recipe;
                        }
                    }
                }
            }
        }
        return null;
    }

    @EventHandler
    public void closeInventory (InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(title)) {
            if (event.getPlayer() instanceof Player) {
                final Player player = (Player) event.getPlayer();
                final Inventory inventory = event.getInventory();
                final ItemStack[] slots = {
                        inventory.getItem(11),
                        inventory.getItem(15),
                };

                for (ItemStack slot : slots) {
                    if (slot != null) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItemNaturally(player.getLocation(), slot);
                        }
                        else {
                            player.getInventory().addItem(slot);
                        }
                    }
                }
            }
        }
    }

    public void saveLocationInsideItem (ItemStack item, Location location) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(locationKey, PersistentDataType.STRING, locationSave.getStringFromLocation(location));
            item.setItemMeta(meta);
        }
    }

    public Location getCauldronLocation (Inventory inventory) {
        return getCauldronLocation(inventory.getItem(1));
    }

    public Location getCauldronLocation (ItemStack tagItem) {
        if (tagItem != null) {
            final ItemMeta meta = tagItem.getItemMeta();
            if (meta != null) {
                if (meta.getPersistentDataContainer().has(locationKey, PersistentDataType.STRING)) {
                    final String saved = meta.getPersistentDataContainer().get(locationKey, PersistentDataType.STRING);
                    if (saved != null && !saved.isEmpty()) {
                        return locationSave.getLocationFromString(saved);
                    }
                }
            }
        }
        return null;
    }
}
