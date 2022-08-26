package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.Loot.DestroySpawner;
import me.fullpotato.badlandscaves.NMS.TPSGetter.TPSGetter;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.Util.EmptyItem;
import me.fullpotato.badlandscaves.Util.ItemBuilder;
import me.fullpotato.badlandscaves.Util.UnloadedWorld;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UseDimensionalAnchor implements Listener {
    private final BadlandsCaves plugin;
    private final HashMap<String, String> nameFromCode = new HashMap<>();
    private final DimensionsWorlds dimensions;
    private final EnvironmentalHazards environmentalHazards;
    private final DestroySpawner destroySpawner;
    private final DeathHandler deathHandler;
    private final Random random;
    private final String title = "§9Dimensional Doorway";
    private final NamespacedKey dimPortalKey;
    private final NamespacedKey usableKey;
    private final TPSGetter tpsGetter;
    private final DimensionStructures structures;
    private final double tpsThreshold;

    public UseDimensionalAnchor(BadlandsCaves plugin, EnvironmentalHazards environmentalHazards, DestroySpawner destroySpawner, DeathHandler deathHandler, Random random) {
        this.plugin = plugin;
        dimensions = new DimensionsWorlds(plugin, random);
        tpsGetter = plugin.getTpsGetterNMS();
        tpsThreshold = plugin.getOptionsConfig().getDouble("hardmode_values.alternate_dimensions_tps_threshold");
        structures = new DimensionStructures(plugin, random);
        this.environmentalHazards = environmentalHazards;
        this.destroySpawner = destroySpawner;
        this.deathHandler = deathHandler;
        this.random = random;

        nameFromCode.put(EnvironmentalHazards.Hazard.TOXIC_WATER.name(), "Toxic Water & Acid Rain");
        nameFromCode.put(EnvironmentalHazards.Hazard.METEOR_SHOWERS.name(), "Meteor Showers");
        nameFromCode.put(EnvironmentalHazards.Hazard.NO_FOOD.name(), "Famine");
        nameFromCode.put(EnvironmentalHazards.Hazard.PARANOIA.name(), "Paranoia");
        nameFromCode.put(EnvironmentalHazards.Hazard.FREEZING.name(), "Cryogenic");
        dimPortalKey = new NamespacedKey(this.plugin, "is_dim_portal");
        usableKey = new NamespacedKey(this.plugin, "usable");
    }

    @EventHandler
    public void useOnSpawner (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                final Block block = event.getClickedBlock();
                if (block != null && block.getType().equals(Material.SPAWNER)) {
                    final ItemStack item = event.getItem();
                    if (item != null && isAnchor(item)) {
                        event.setUseItemInHand(Event.Result.DENY);
                        final Player player = event.getPlayer();
                        for (double recentTPS : tpsGetter.getRecentTPS()) {
                            if (recentTPS < tpsThreshold) {
                                player.sendMessage(ChatColor.RED + "You can't do that right now.");
                                return;
                            }
                        }

                        // TODO: 8/17/2020 fix the crashing and remove this
                        final String worldName = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);
                        final UnloadedWorld unloadedWorld = new UnloadedWorld(plugin.getDimensionPrefixName() + worldName);

                        if (unloadedWorld.exists()) {
                            initiate(item, block);
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Unfortunately this isn't supported right now. You can only load pre-generated worlds at this time.");
                        }
                    }
                }
            }
        }
    }

    public void initiate (ItemStack item, Block bottom) {
        final String worldName = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);

        Block middle = bottom.getRelative(BlockFace.UP);
        Block top = middle.getRelative(BlockFace.UP);

        top.setType(Material.BLACKSTONE_SLAB);
        bottom.setType(Material.BLACKSTONE_SLAB);
        Slab bottomSlabData = (Slab) Material.BLACKSTONE_SLAB.createBlockData();
        bottomSlabData.setType(Slab.Type.TOP);
        bottom.setBlockData(bottomSlabData);

        EntityType entityType = null;
        if (middle.getType().equals(Material.SPAWNER)) {
            if (middle.getState() instanceof CreatureSpawner) {
                CreatureSpawner state = (CreatureSpawner) middle.getState();
                entityType = state.getSpawnedType();
            }
        }

        middle.setType(Material.END_GATEWAY);

        if (middle.getState() instanceof EndGateway) {
            EndGateway gatewayData = (EndGateway) middle.getState();

            gatewayData.setExitLocation(middle.getLocation());
            gatewayData.setExactTeleport(true);
            gatewayData.getPersistentDataContainer().set(dimPortalKey, PersistentDataType.BYTE, (byte) 1);
            gatewayData.getPersistentDataContainer().set(usableKey, PersistentDataType.BYTE, (byte) 0);
            gatewayData.getPersistentDataContainer().set(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, worldName);
            gatewayData.update(true);

            item.setAmount(item.getAmount() - 1);
            final EntityType finalEntityType = entityType;
            new BukkitRunnable() {
                @Override
                public void run() {
                    final World world = dimensions.generate(worldName, false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            final String fullName = plugin.getDimensionPrefixName() + worldName;
                            if (!plugin.getSystemConfig().getBoolean("alternate_dimensions." + fullName + ".accessed") && !environmentalHazards.hasHazards(world)) {
                                dimensions.addHazards(world);

                                plugin.getSystemConfig().set("alternate_dimensions." + fullName + ".accessed", true);
                                plugin.saveSystemConfig();
                            }

                            destroySpawner.incrementChaos(true);
                            destroySpawner.getNewLocation(middle.getLocation(), random, 500);
                            destroySpawner.makeDungeon(finalEntityType, random, true, false);

                            plugin.getServer().broadcastMessage("§9A Dimensional Doorway has opened!");
                            gatewayData.getPersistentDataContainer().set(usableKey, PersistentDataType.BYTE, (byte) 1);
                            gatewayData.update(true);
                        }
                    }.runTaskLater(plugin, 5);
                }
            }.runTaskLater(plugin, 5);
        }
    }

    @EventHandler
    public void clickPortal(PlayerInteractEvent event) {
        if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
            Player player = event.getPlayer();
            if (player.isSneaking()) return;

            Block block = event.getClickedBlock();
            if (block != null) {
                if (block.getType().equals(Material.END_GATEWAY)) {
                    if (block.getState() instanceof EndGateway) {
                        EndGateway state = (EndGateway) block.getState();
                        if (state.getPersistentDataContainer().has(dimPortalKey, PersistentDataType.BYTE)) {
                            final Byte portalResult = state.getPersistentDataContainer().get(dimPortalKey, PersistentDataType.BYTE);
                            if (portalResult != null && portalResult == (byte) 1) {
                                if (state.getPersistentDataContainer().has(usableKey, PersistentDataType.BYTE)) {
                                    final Byte usableResult = state.getPersistentDataContainer().get(usableKey, PersistentDataType.BYTE);
                                    if (usableResult != null && usableResult == (byte) 1) {
                                        String worldName = state.getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);
                                        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                                            event.setCancelled(true);
                                            openWarpMenu(player, block, worldName, MenuType.WARP);
                                        }
                                        else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                                            event.setCancelled(true);
                                            openWarpMenu(player, block, worldName, MenuType.BREAK);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (block.getType().equals(Material.CAMPFIRE)) {
                    if (block.getState() instanceof Campfire) {
                        Campfire state = (Campfire) block.getState();
                        NamespacedKey key = new NamespacedKey(plugin, "is_return_portal");
                        if (state.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                            Byte result = state.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                            if (result != null && result == (byte) 1) {
                                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                                    event.setCancelled(true);
                                    openWarpMenu(player, block, null, MenuType.RETURN);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    enum MenuType {
        WARP,
        BREAK,
        RETURN
    }

    public void openWarpMenu (Player player, Block doorway, String worldName, MenuType type) {
        Inventory inventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, this.title);

        final ItemStack empty = EmptyItem.getEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        inventory.setItem(0, empty);
        inventory.setItem(1, empty);
        inventory.setItem(3, empty);
        inventory.setItem(4, empty);

        ItemStack icon = null;
        if (type.equals(MenuType.BREAK)) {
            icon = new ItemStack(Material.BARRIER);
            ItemMeta icon_meta = icon.getItemMeta();
            icon_meta.setDisplayName("§cDestroy Doorway");

            Location location = doorway.getLocation();
            icon_meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gateway_x"), PersistentDataType.INTEGER, location.getBlockX());
            icon_meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gateway_y"), PersistentDataType.INTEGER, location.getBlockY());
            icon_meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gateway_z"), PersistentDataType.INTEGER, location.getBlockZ());
            icon_meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, worldName);

            icon.setItemMeta(icon_meta);
        }
        else if (type.equals(MenuType.WARP)) {
            World world = plugin.getServer().getWorld(plugin.getDimensionPrefixName() + worldName);
            if (world == null) {
                DimensionsWorlds dimensions = new DimensionsWorlds(plugin, random);
                world = dimensions.generate(worldName, false);
            }

            icon = new ItemBuilder(Material.KNOWLEDGE_BOOK).setCustomModelData(175).build();
            ItemMeta icon_meta = icon.getItemMeta();
            icon_meta.setDisplayName("§9Warp to Alternate Dimension");

            ArrayList<String> icon_lore = new ArrayList<>();
            icon_lore.add("§7Dimension: " + worldName);
            icon_lore.add("§r");
            icon_lore.add("§7Hazards:");

            List<String> hazards = plugin.getSystemConfig().getStringList("alternate_dimensions." + world.getName() + ".hazards");
            for (String code : hazards) {
                icon_lore.add("§7 - " + nameFromCode.get(code));
            }
            icon_meta.setLore(icon_lore);
            icon_meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, worldName);

            icon.setItemMeta(icon_meta);
        }
        else if (type.equals(MenuType.RETURN)) {
            icon = new ItemStack(Material.ORANGE_TERRACOTTA);
            ItemMeta icon_meta = icon.getItemMeta();
            icon_meta.setDisplayName("§6Return Home");
            icon.setItemMeta(icon_meta);
        }

        if (icon == null) return;
        inventory.setItem(2, icon);
        player.openInventory(inventory);
        player.updateInventory();
    }

    @EventHandler
    public void warpMenuInteract (InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(event.getClickedInventory()) && event.getView().getTitle().equals(title)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item != null) {
                Player player = (Player) event.getWhoClicked();
                if (item.getType().equals(Material.KNOWLEDGE_BOOK)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING)) {
                        String worldName = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);
                        if (worldName != null) {
                            World world = plugin.getServer().getWorld(plugin.getDimensionPrefixName() + worldName);
                            if (world != null) {
                                player.closeInventory();
                                player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                player.playSound(player.getLocation(), "custom.supernatural.displace.warp", SoundCategory.BLOCKS, 0.5F, 1);

                                attemptGenerateStructures(world);
                            }
                        }
                    }
                }
                else if (item.getType().equals(Material.BARRIER)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null &&
                            meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "gateway_x"), PersistentDataType.INTEGER) &&
                            meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "gateway_y"), PersistentDataType.INTEGER) &&
                            meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "gateway_z"), PersistentDataType.INTEGER)) {
                                Integer x = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gateway_x"), PersistentDataType.INTEGER);
                                Integer y = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gateway_y"), PersistentDataType.INTEGER);
                                Integer z = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gateway_z"), PersistentDataType.INTEGER);

                                if (x != null && y != null && z != null) {
                                    Location location = new Location(player.getWorld(), x, y, z);

                                    Block gateway = location.getBlock();
                                    Block top = gateway.getRelative(BlockFace.UP);
                                    Block bottom = gateway.getRelative(BlockFace.DOWN);

                                    ItemStack empty = new ItemStack(Material.AIR);
                                    gateway.breakNaturally(empty);
                                    top.breakNaturally(empty);
                                    bottom.breakNaturally(empty);

                                    String worldName = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);
                                    player.closeInventory();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.getWorld().dropItemNaturally(location, getSpecificAnchor(worldName));
                                        }
                                    }.runTaskLater(plugin, 5);
                                }
                    }
                }
                else if (item.getType().equals(Material.ORANGE_TERRACOTTA)) {
                    deathHandler.resetPlayer(player, false, true, false);
                }
            }

        }
    }

    @EventHandler
    public void preventBreak (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.BLACKSTONE_SLAB)) {
            final Player player = event.getPlayer();

            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                if (isAnchorSlab(block)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void preventExplode (EntityExplodeEvent event) {
        event.blockList().removeIf(this::isAnchorSlab);
    }

    @EventHandler
    public void preventPistonPush (BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isAnchorSlab(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventPistonPull (BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isAnchorSlab(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventEntrance (PlayerMoveEvent event) {
        Location location = event.getTo();
        if (location != null) {
            Block block = location.getBlock();
            if (block.getType().equals(Material.END_GATEWAY)) {
                if (block.getState() instanceof EndGateway) {
                    EndGateway state = (EndGateway) block.getState();
                    if (state.getPersistentDataContainer().has(dimPortalKey, PersistentDataType.BYTE)) {
                        Byte result = state.getPersistentDataContainer().get(dimPortalKey, PersistentDataType.BYTE);
                        if (result != null && result == (byte) 1) {
                            Player player = event.getPlayer();
                            ZombieBossBehavior locationFinder = new ZombieBossBehavior(plugin, random);
                            Location nearby = locationFinder.getNearbyLocation(player.getLocation(), random, 5);
                            if (nearby != null) {
                                event.setTo(nearby);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isAnchorSlab (Block block) {
        final Block top = block.getRelative(BlockFace.UP);
        final Block bottom = block.getRelative(BlockFace.DOWN);
        final NamespacedKey key = new NamespacedKey(plugin, "is_dim_portal");

        if (top.getType().equals(Material.END_GATEWAY)) {
            if (top.getState() instanceof EndGateway) {
                final EndGateway state = (EndGateway) top.getState();
                if (state.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    Byte result = state.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                    if (result != null && result == (byte) 1) {
                        return true;
                    }
                }
            }
        }

        if (bottom.getType().equals(Material.END_GATEWAY)) {
            if (bottom.getState() instanceof EndGateway) {
                final EndGateway state = (EndGateway) bottom.getState();
                if (state.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    Byte result = state.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                    return result != null && result == (byte) 1;
                }
            }
        }
        return false;
    }

    public boolean isAnchor (ItemStack item) {
        if (item.getType().equals(Material.KNOWLEDGE_BOOK) && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(plugin, "is_dim_anchor");
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte result = container.get(key, PersistentDataType.BYTE);
                    if (result != null) {
                        return (result == (byte) 1);
                    }
                }
            }
        }
        return false;
    }

    public ItemStack getSpecificAnchor(String str) {
        ItemStack item = plugin.getCustomItemManager().getItem(CustomItem.DIMENSIONAL_ANCHOR);
        ItemMeta meta = item.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7" + str);
        lore.add(ChatColor.BLUE + "Visited");
        meta.setLore(lore);

        NamespacedKey key = new NamespacedKey(plugin, "world_name");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);

        key = new NamespacedKey(plugin, "is_dim_anchor");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public void attemptGenerateStructures(World world) {
        final boolean generated = plugin.getSystemConfig().getBoolean("alternate_dimensions." + world.getName() + ".structures_generated");
        if (!generated) {
            final int chaos = plugin.getSystemConfig().getInt("chaos_level");
            structures.generateStructures(world, null, (int) (world.getWorldBorder().getSize() / 2), random.nextInt((chaos / 5) + 20) + 5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getSystemConfig().set("alternate_dimensions." + world.getName() + ".structures_generated", true);
                    plugin.saveSystemConfig();
                }
            }.runTaskLater(plugin, 1);
        }
    }

}
