package me.fullpotato.badlandscaves.Blocks;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.EnergyCore;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
    private final ParticleShapes particleShapes;

    public SilencerBlock(BadlandsCaves plugin, ParticleShapes particleShapes) {
        this.plugin = plugin;
        this.particleShapes = particleShapes;
    }

    @EventHandler
    public void placeSilencer (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = event.getItem();
            if (item != null && item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.SILENCER))) {
                event.setCancelled(true);

                Player player = event.getPlayer();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    final Block clickedBlock = event.getClickedBlock();
                    if (clickedBlock != null) {
                        final Block block = clickedBlock.isPassable() ? clickedBlock : clickedBlock.getRelative(event.getBlockFace());
                        if (block.isPassable()) {
                            Location nearbySilencer = getNearbySilencer(block.getLocation());
                            if (nearbySilencer == null) {
                                if (!player.getGameMode().equals(GameMode.CREATIVE)) item.setAmount(item.getAmount() - 1);
                                addSilencer(block);
                            }
                            else {
                                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1, 0.5F);
                                particleShapes.line(null, Particle.REDSTONE, block.getLocation().add(0.5, 0.5, 0.5), nearbySilencer.clone().add(0.5, 0.5, 0.5), 0, new Particle.DustOptions(Color.PURPLE, 0.7F), 1);
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
        if (block != null && isSilencer(block) && block.getState() instanceof Structure) {
            final World world = block.getWorld();
            final Location location = block.getLocation();
            final Structure state = (Structure) block.getState();
            final ItemStack item = event.getItem();
            final Player player = event.getPlayer();
            event.setCancelled(true);

            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (player.getGameMode().equals(GameMode.CREATIVE) || (item != null && (item.getType().equals(Material.DIAMOND_PICKAXE) || item.getType().equals(Material.NETHERITE_PICKAXE)))) {
                    boolean ready = false;
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                        if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                            ready = true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "You can't do that right now.");
                        }
                    }
                    else {
                        ready = true;
                    }
                    if (ready) openDestroySilencerMenu(player, location);
                }
                else {
                    player.sendMessage(ChatColor.RED + "You need a stronger tool.");
                }
            }
            else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                        final int silencerCharge = getCharge(block);

                        Integer maxChargeResult = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_max_charge"), PersistentDataType.INTEGER);
                        if (maxChargeResult != null) {
                            final int maxCharge = maxChargeResult;
                            if (item != null) {
                                EnergyCore energyCore = new EnergyCore(plugin);
                                if (energyCore.isEnergyCore(item)) {
                                    final int coreCharge = energyCore.getCharge(item);
                                    item.setAmount(item.getAmount() - 1);

                                    setCharge(block, Math.min(silencerCharge + coreCharge, maxCharge));

                                    if (silencerCharge <= 0 && getCharge(block) > 0) {
                                        final int range = plugin.getOptionsConfig().getInt("hardmode_values.silencer_range");

                                        world.playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1, 1);
                                        particleShapes.lineDelayed(player, Particle.REDSTONE, location.clone().add(0.5, 0, 0.5), location.clone().add(0.5, range, 0.5), 0, new Particle.DustOptions(Color.PURPLE, 1), 1, 1);

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                particleShapes.sphereDelayed(player, Particle.REDSTONE, location, 25, 0, new Particle.DustOptions(Color.PURPLE, 1), 1, false);

                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 10, 1);
                                                        particleShapes.sphere(player, Particle.REDSTONE, location, 25, 0, new Particle.DustOptions(Color.PURPLE, 1));
                                                    }
                                                }.runTaskLaterAsynchronously(plugin, range);

                                            }
                                        }.runTaskLaterAsynchronously(plugin, range);
                                    }
                                    else {
                                        world.playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1, 1);
                                    }
                                }
                            }

                            final Location centered = location.add(0.5, 1.0, 0.5);
                            destroySilencerIcon(centered);

                            Location clone = centered.clone();
                            clone.setY(0);

                            ArmorStand armorStand = (ArmorStand) world.spawnEntity(clone, EntityType.ARMOR_STAND);
                            armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_silencer_icon"), PersistentDataType.BYTE, (byte) 1);
                            armorStand.setMarker(true);
                            armorStand.setInvulnerable(true);
                            armorStand.setVisible(false);
                            armorStand.setCustomName(ChatColor.of("#9d00ff").toString() + getCharge(block) + " / " + maxCharge + " Charge");
                            armorStand.setCustomNameVisible(true);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    armorStand.teleport(centered, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                }
                            }.runTaskLater(plugin, 2);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    armorStand.remove();
                                }
                            }.runTaskLaterAsynchronously(plugin, 100);
                        }
                    }
                }
            }
        }
    }

    public void destroySilencerIcon(Location location) {
        final World world = location.getWorld();

        assert world != null;
        world.getNearbyEntities(location, 1, 1, 1).forEach(entity -> {
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
    }

    public void openDestroySilencerMenu (Player player, Location location) {
        final Inventory inventory = plugin.getServer().createInventory(player, InventoryType.HOPPER, destroyMenuTitle);

        final ItemStack empty = EmptyItem.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);

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
                String worldName = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_world"), PersistentDataType.STRING);

                assert worldName != null;
                World world = plugin.getServer().getWorld(worldName);

                int x = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_x"), PersistentDataType.INTEGER);
                int y = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_y"), PersistentDataType.INTEGER);
                int z = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_z"), PersistentDataType.INTEGER);

                assert world != null;
                final Location location = new Location(world, x, y, z);
                final Block block = location.getBlock();


                if (isSilencer(block) && block.getState() instanceof Structure) {
                    Structure state = (Structure) block.getState();
                    String uuid = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_id"), PersistentDataType.STRING);
                    if (uuid != null) {
                        destroySilencerIcon(block.getLocation().add(0.5, 1, 0.5));
                        block.breakNaturally();
                        block.getWorld().dropItemNaturally(block.getLocation(), plugin.getCustomItemManager().getItem(CustomItem.SILENCER));

                        event.getWhoClicked().closeInventory();
                        plugin.getSystemConfig().set("silencer_locations." + uuid, null);
                        plugin.saveSystemConfig();

                        world.playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1, 1);
                    }
                }
            }
        }
    }

    public @Nullable Location getNearbySilencer(Location location) {
        final int range = plugin.getOptionsConfig().getInt("hardmode_values.silencer_range");
        final int rangeSquared = range * range;

        ConfigurationSection section = plugin.getSystemConfig().getConfigurationSection("silencer_locations");
        if (section != null) {
            for (String string : section.getValues(false).keySet()) {
                Location silencerLocation = plugin.getSystemConfig().getLocation("silencer_locations." + string);
                if (silencerLocation != null && location.distanceSquared(silencerLocation) < rangeSquared) {
                    Block block = silencerLocation.getBlock();
                    if (isSilencer(block)) {
                        return silencerLocation;
                    }
                }
            }
        }
        return null;
    }

    public @Nullable Location getNearbyActiveSilencer(Location location) {
        final int range = plugin.getOptionsConfig().getInt("hardmode_values.silencer_range");
        final int rangeSquared = range * range;

        ConfigurationSection section = plugin.getSystemConfig().getConfigurationSection("silencer_locations");
        if (section != null) {
            for (String string : section.getValues(false).keySet()) {
                Location silencerLocation = plugin.getSystemConfig().getLocation("silencer_locations." + string);
                if (silencerLocation != null && location.getWorld() != null && location.getWorld().equals(silencerLocation.getWorld()) && location.distanceSquared(silencerLocation) < rangeSquared) {
                    Block block = silencerLocation.getBlock();
                    if (isSilencer(block)) {
                        if (getCharge(block) > 0) {
                            return silencerLocation;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void useSilencer(Location location) {
        Block block = location.getBlock();
        if (isSilencer(block)) {
            setCharge(block, getCharge(block) - plugin.getOptionsConfig().getInt("hardmode_values.silencer_cost"));
        }
    }

    public boolean isSilencer (Block block) {
        if (block.getType().equals(Material.STRUCTURE_BLOCK)) {
            if (block.getState() instanceof Structure) {
                Structure state = (Structure) block.getState();
                if (state.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE)) {
                    Byte isSilencer = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_silencer"), PersistentDataType.BYTE);
                    if (isSilencer != null) {
                        return isSilencer == (byte) 1;
                    }
                }
            }
        }
        return false;
    }

    public int getCharge (Block block) {
        if (isSilencer(block)) {
            if (block.getState() instanceof Structure) {
                Structure state = (Structure) block.getState();
                Integer result = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_charge"), PersistentDataType.INTEGER);
                if (result != null) {
                    return result;
                }
            }
        }
        return -1;
    }

    public void setCharge (Block block, int charge) {
        if (isSilencer(block)) {
            if (block.getState() instanceof Structure) {
                Structure state = (Structure) block.getState();
                Integer result = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "silencer_max_charge"), PersistentDataType.INTEGER);
                if (result != null) {
                    final int maxCharge = result;
                    state.getPersistentDataContainer().set(new NamespacedKey(plugin, "silencer_charge"), PersistentDataType.INTEGER, Math.max(Math.min(charge, maxCharge), 0));
                    state.update(true);
                    if (getCharge(block) > 0) {
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
