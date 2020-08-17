package me.fullpotato.badlandscaves.AlternateDimensions;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.NMS.TPSGetter.TPSGetter;
import me.fullpotato.badlandscaves.Util.ItemBuilder;
import me.fullpotato.badlandscaves.WorldGeneration.DimensionsWorlds;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private final double TPS_THRESHOLD = 18.0;
    private final int WORLDS_LIMIT = 5;

    public PregenerateDimensions(BadlandsCaves plugin) {
        this.plugin = plugin;
        tpsGetter = plugin.getTpsGetterNMS();
        dimensionsWorlds = new DimensionsWorlds(plugin);
    }

    @Override
    public void run() {
        if (plugin.getServer().getOnlinePlayers().isEmpty()) {
            final double[] recentTPSArr = tpsGetter.getRecentTPS();
            for (double recentTPS : recentTPSArr) {
                if (recentTPS < TPS_THRESHOLD) {
                    return;
                }
            }

            if (plugin.getSystemConfig().getStringList("pregenerated_dimensions").size() <= WORLDS_LIMIT) {
                generateNewDimension();
            }
        }
    }

    public void generateNewDimension () {
        final String uuid = UUID.randomUUID().toString();
        dimensionsWorlds.generate(uuid);

        final List<String> list = plugin.getSystemConfig().getStringList("pregenerated_dimensions");
        list.add(uuid);
        plugin.getSystemConfig().set("pregenerated_dimensions", list);
        plugin.saveSystemConfig();
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
        String uuid = getPregeneratedDimension();
        if (uuid != null && !uuid.isEmpty()) {
            removePregneratedDimensionFromList(uuid);
        }
        else {
            uuid = UUID.randomUUID().toString();
        }

        return new ItemBuilder(Material.KNOWLEDGE_BOOK).setName("§9Dimensional Anchor").setLore("§7" + uuid).setCustomModelData(175).setPersistentData(new NamespacedKey(plugin, "world_name"), PersistentDataType.STRING, uuid).setPersistentData(new NamespacedKey(plugin, "is_dim_anchor"), PersistentDataType.BYTE, (byte) 1).build();
    }
}
