package me.fullpotato.badlandscaves.MobBuffs;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class VoidCreeper implements Listener {
    private BadlandsCaves plugin;
    private NamespacedKey voidKey;

    public VoidCreeper(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.voidKey = new NamespacedKey(plugin, "voidMonster");
    }

    @EventHandler
    public void spreadVoid(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Creeper)) return;
        if (event.getLocation().getWorld().getEnvironment() != Environment.NORMAL) return;

        final Creeper creeper = (Creeper) event.getEntity();

        //Ensure that the creeper is a Void Creeper
        if (!creeper.getPersistentDataContainer().has(this.voidKey, PersistentDataType.BYTE) || 
            creeper.getPersistentDataContainer().get(this.voidKey, PersistentDataType.BYTE) != 1) return;
       
        final List<Block> blocks = event.blockList();

        //Spreads the void via explosions
        for (Block block : blocks) {
            if (block.getType().isSolid() && !(block.getState() instanceof Container)) {
                block.setType(Material.BLACKSTONE);
            }
            block.setBiome(Biome.GRAVELLY_MOUNTAINS);

            if (block.getRelative(BlockFace.UP).getType().isAir()) {
                block.getRelative(BlockFace.UP).setBiome(Biome.GRAVELLY_MOUNTAINS);
            }
        }

        event.blockList().clear();
    }
    


}
