package me.fullpotato.badlandscaves.Blocks;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.EnergyCore;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Info.CraftingGuide;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.UUID;

public class SilencerBlock implements Listener {
    private final BadlandsCaves plugin;
    private final String destroyMenuTitle = ChatColor.DARK_RED + "Destroy Silencer";

    public SilencerBlock(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void placeSilencer (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = event.getItem();
            if (item != null && item.isSimilar(CustomItem.SILENCER.getItem())) {
                event.setCancelled(true);

                Player player = event.getPlayer();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    final Block clickedBlock = event.getClickedBlock();
                    if (clickedBlock != null) {
                        final Block block = clickedBlock.getRelative(event.getBlockFace());
                        if (block.isPassable()) {
                            if (getNearbySilencer(block.getLocation(), 625) == null) {
                                if (!player.getGameMode().equals(GameMode.CREATIVE)) item.setAmount(item.getAmount() - 1);
                                addSilencer(block);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addSilencer (Block block) {
        block.setType(Material.STRUCTURE_BLOCK);
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);

        UUID uuid = UUID.randomUUID();

        if (block.getState() instanceof Structure) {
            Structure state = (Structure) block.getState();
            state.setUsageMode(UsageMode.LOAD);
            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE, (byte) 1);
            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_id"), PersistentDataType.STRING, uuid.toString());
            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_charge"), PersistentDataType.INTEGER, 0);
            state.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_max_charge"), PersistentDataType.INTEGER, 2000);
            state.update(true);
        }

        plugin.getSystemConfig().set("silencer_locations." + uuid.toString(), block.getLocation());
        plugin.saveSystemConfig();
    }

    @EventHandler
    public void silencerInteract (PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType().equals(Material.STRUCTURE_BLOCK)) {
            if (block.getState() instanceof Structure) {
                Structure state = (Structure) block.getState();
                if (state.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE)) {
                    Byte result = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE);
                    if (result != null && result == (byte) 1) {
                        ItemStack item = event.getItem();
                        event.setCancelled(true);
                        Player player = event.getPlayer();

                        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                            if (player.getGameMode().equals(GameMode.CREATIVE) || (item != null && (item.getType().equals(Material.DIAMOND_PICKAXE) || item.getType().equals(Material.NETHERITE_PICKAXE)))) {
                                openDestroySilencerMenu(player, block.getLocation());
                            }
                            else {
                                player.sendMessage(ChatColor.RED + "You need a stronger tool.");
                            }
                        }
                        else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                            if (event.getHand().equals(EquipmentSlot.HAND)) {
                                Integer chargeResult = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_charge"), PersistentDataType.INTEGER);
                                if (chargeResult != null) {
                                    final int silencerCharge = chargeResult;
                                    int finalCharge = chargeResult;

                                    Integer maxChargeResult = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_max_charge"), PersistentDataType.INTEGER);
                                    if (maxChargeResult != null) {
                                        final int maxCharge = maxChargeResult;
                                        if (item != null) {
                                            EnergyCore energyCore = new EnergyCore(plugin);
                                            if (energyCore.isEnergyCore(item)) {
                                                final int coreCharge = energyCore.getCharge(item);
                                                item.setAmount(item.getAmount() - 1);
                                                finalCharge = Math.min(coreCharge + silencerCharge, maxCharge);
                                                state.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_charge"), PersistentDataType.INTEGER, finalCharge);
                                                state.update(true);
                                            }
                                        }

                                        final Location location = block.getLocation().add(0.5, 1, 0.5);
                                        block.getWorld().getNearbyEntities(location, 0.1, 0.1, 0.1).forEach(entity -> {
                                            if (entity instanceof ArmorStand) {
                                                ArmorStand armorStand = (ArmorStand) entity;
                                                if (armorStand.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_silencer_icon"), PersistentDataType.BYTE)) {
                                                    Byte isSilencerIcon = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_silencer_icon"), PersistentDataType.BYTE);
                                                    if (isSilencerIcon != null && isSilencerIcon == (byte) 1) {
                                                        armorStand.remove();
                                                    }
                                                }
                                            }
                                        });

                                        ArmorStand armorStand = (ArmorStand) block.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_silencer_icon"), PersistentDataType.BYTE, (byte) 1);
                                        armorStand.setMarker(true);
                                        armorStand.setInvulnerable(true);
                                        armorStand.setVisible(false);
                                        armorStand.setCustomName(finalCharge + " / " + maxCharge + " Charge");
                                        armorStand.setCustomNameVisible(true);

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                armorStand.remove();
                                            }
                                        }.runTaskLaterAsynchronously(plugin, 100);
                                    }

                                    if (finalCharge > 0) {
                                        state.setUsageMode(UsageMode.SAVE);
                                    }
                                    else {
                                        state.setUsageMode(UsageMode.LOAD);
                                    }
                                    state.update(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void openDestroySilencerMenu (Player player, Location location) {
        final Inventory inventory = plugin.getServer().createInventory(player, InventoryType.HOPPER, destroyMenuTitle);

        final CraftingGuide craftingGuide = new CraftingGuide(plugin);
        final ItemStack empty = craftingGuide.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);

        final ItemStack icon = new ItemStack(Material.BARRIER);
        final ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Destroy Silencer");
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_world"), PersistentDataType.STRING, location.getWorld().getName());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_x"), PersistentDataType.INTEGER, location.getBlockX());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_y"), PersistentDataType.INTEGER, location.getBlockY());
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_z"), PersistentDataType.INTEGER, location.getBlockZ());

        icon.setItemMeta(meta);

        inventory.setItem(0, empty);
        inventory.setItem(1, empty);
        inventory.setItem(2, icon);
        inventory.setItem(3, empty);
        inventory.setItem(4, empty);

        player.openInventory(inventory);
    }

    @EventHandler
    public void destroyMenuInteract (InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(event.getClickedInventory()) && event.getView().getTitle().equals(destroyMenuTitle)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType().equals(Material.BARRIER)) {
                ItemMeta meta = item.getItemMeta();

                assert meta != null;
                String world = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_world"), PersistentDataType.STRING);
                int x = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_x"), PersistentDataType.INTEGER);
                int y = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_y"), PersistentDataType.INTEGER);
                int z = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_z"), PersistentDataType.INTEGER);

                assert world != null;
                final Location location = new Location(plugin.getServer().getWorld(world), x, y, z);
                final Block block = location.getBlock();

                if (block.getType().equals(Material.STRUCTURE_BLOCK)) {
                    if (block.getState() instanceof Structure) {
                        Structure state = (Structure) block.getState();
                        if (state.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE)) {
                            Byte isSilencer = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE);
                            if (isSilencer != null && isSilencer == (byte) 1) {
                                String uuid = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_id"), PersistentDataType.STRING);
                                if (uuid != null) {
                                    block.breakNaturally();
                                    block.getWorld().dropItemNaturally(block.getLocation(), CustomItem.SILENCER.getItem());

                                    event.getWhoClicked().closeInventory();
                                    plugin.getSystemConfig().set("silencer_locations." + uuid, null);
                                    plugin.saveSystemConfig();
                                }
                            }
                        }
                    }
                }



            }

        }
    }

    public @Nullable Location getNearbySilencer (Location location, int rangeSquared) {
        for (String string : plugin.getSystemConfig().getConfigurationSection("silencer_locations").getValues(false).keySet()) {
            Location silencerLocation = plugin.getSystemConfig().getLocation("silencer_locations." + string);
            if (silencerLocation != null && location.distanceSquared(silencerLocation) < rangeSquared) {
                return silencerLocation;
            }
        }
        return null;
    }

    public void useSilencer (Location location) {
        Block block = location.getBlock();
        // TODO: 7/8/2020 make this use charge from the silencer
    }
}
