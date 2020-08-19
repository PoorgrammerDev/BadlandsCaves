package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.TPSGetter.TPSGetter;
import me.fullpotato.badlandscaves.Util.ItemBuilder;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PregenerateDimensions extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final TPSGetter tpsGetter;
    private final DimensionsWorlds dimensionsWorlds;
    private final double TPSThreshold;
    private final int worldsLimit;

    public PregenerateDimensions(BadlandsCaves plugin) {
        this.plugin = plugin;
        worldsLimit = plugin.getOptionsConfig().getInt("hardmode_values.alternate_dimensions_pregenerate_limit");
        TPSThreshold = plugin.getOptionsConfig().getDouble("hardmode_values.alternate_dimensions_tps_threshold");
        tpsGetter = plugin.getTpsGetterNMS();
        dimensionsWorlds = new DimensionsWorlds(plugin);
    }

    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (plugin.getServer().getOnlinePlayers().isEmpty()) {
                plugin.getLogger().info("Attempting dimension pre-generation...");
                final double[] recentTPSArr = tpsGetter.getRecentTPS();
                for (double recentTPS : recentTPSArr) {
                    if (recentTPS < TPSThreshold) {
                        plugin.getLogger().info("Server TPS is too low to pre-generate.");
                        return;
                    }
                }

                if (plugin.getSystemConfig().getStringList("pregenerated_dimensions").size() <= worldsLimit) {
                    plugin.getLogger().info("Pre-generating new dimension.");
                    generateNewDimension();
                }
                else {
                    plugin.getLogger().info("Max amount of pre-generated dimensions reached.");
                }
            }
        }
    }

    public void generateNewDimension () {
        final String uuid = UUID.randomUUID().toString();
        final World world = dimensionsWorlds.generate(uuid, true);

        final List<String> list = plugin.getSystemConfig().getStringList("pregenerated_dimensions");
        list.add(uuid);

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSystemConfig().set("pregenerated_dimensions", list);
                plugin.saveSystemConfig();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getServer().unloadWorld(world, true);
                    }
                }.runTaskLater(plugin, 5);
            }
        }.runTaskLater(plugin, 5);
    }

    public @Nullable String getPregeneratedDimension() {
        final List<String> list = plugin.getSystemConfig().getStringList("pregenerated_dimensions");
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    public void removePregneratedDimensionFromList (String uuid) {
        final List<String> list = plugin.getSystemConfig().getStringList("pregenerated_dimensions");
        if (!list.isEmpty()) {
            list.remove(uuid);
            plugin.getSystemConfig().set("pregenerated_dimensions", list);
            plugin.saveSystemConfig();
        }
    }

    public ItemStack getDimensionalAnchor() {
        final ItemBuilder item = new ItemBuilder(plugin.getCustomItemManager().getItem(CustomItem.DIMENSIONAL_ANCHOR));
        String uuid = getPregeneratedDimension();

        final boolean pregenerated;
        if (uuid != null && !uuid.isEmpty()) {
            pregenerated = true;
            removePregneratedDimensionFromList(uuid);
        }
        else {
            pregenerated = false;
            uuid = UUID.randomUUID().toString();
        }

        return item.setLore(ChatColor.GRAY + uuid, pregenerated ? ChatColor.DARK_AQUA + "Pre-generated" : ChatColor.RED + "Not Pre-generated").setPersistentData(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, uuid).build();
    }
}
