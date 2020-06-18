package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Random;

public class UseDimensionalAnchor implements Listener {
    private final BadlandsCaves plugin;

    public UseDimensionalAnchor(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void useOnSpawner (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (plugin.getConfig().getBoolean("system.hardmode")) {
                final Block block = event.getClickedBlock();
                if (block != null && block.getType().equals(Material.SPAWNER)) {
                    final ItemStack item = event.getItem();
                    if (item != null && isAnchor(item)) {
                        initiate(item, block);
                    }
                }
            }
        }
    }

    public void initiate (ItemStack item, Block bottom) {
        final String worldName = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING);

        Block middle = bottom.getRelative(BlockFace.UP);
        Block top = middle.getRelative(BlockFace.UP);

        top.setType(Material.SMOOTH_STONE_SLAB);
        bottom.setType(Material.SMOOTH_STONE_SLAB);
        Slab bottomSlabData = (Slab) Material.SMOOTH_STONE_SLAB.createBlockData();
        bottomSlabData.setType(Slab.Type.TOP);
        bottom.setBlockData(bottomSlabData);

        middle.setType(Material.END_GATEWAY);

        if (middle.getState() instanceof EndGateway) {
            EndGateway gatewayData = (EndGateway) middle.getState();

            gatewayData.setExitLocation(middle.getLocation());
            gatewayData.setExactTeleport(true);

            gatewayData.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_dim_portal"), PersistentDataType.BYTE, (byte) 1);
            gatewayData.getPersistentDataContainer().set(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, worldName);
            gatewayData.update(true);
        }
    }

    @EventHandler
    public void preventBreak (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.SMOOTH_STONE_SLAB)) {
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
                    final NamespacedKey key = new NamespacedKey(plugin, "is_dim_portal");
                    if (state.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                        Byte result = state.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                        if (result != null && result == (byte) 1) {
                            // FIXME: 6/18/2020 idk if this works
                            event.setCancelled(true);

                            Player player = event.getPlayer();
                            ZombieBossBehavior locationFinder = new ZombieBossBehavior(plugin);
                            Location nearby = locationFinder.getNearbyLocation(player.getLocation(), new Random(), 10);
                            if (nearby != null) {
                                player.teleport(nearby);
                                player.setVelocity(new Vector(0, 0, 0));
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
        final ItemStack anchor = CustomItem.DIMENSIONAL_ANCHOR.getItem();
        if (item.getType().equals(anchor.getType())) {
            if (item.hasItemMeta()) {
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
        }
        return false;
    }

}
