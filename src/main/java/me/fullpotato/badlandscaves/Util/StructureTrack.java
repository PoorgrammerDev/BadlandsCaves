package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Structure;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

public class StructureTrack {
    private final BadlandsCaves plugin;
    private final Location origin;
    private final int blockXOffset;
    private final int blockYOffset;
    private final int blockZOffset;
    private final int settingXOffset;
    private final int settingYOffset;
    private final int settingZOffset;
    private final double integrity;
    private final String structureName;
    private final BlockFace redstoneBlockRelative;

    //CONSTRUCTORS
    public StructureTrack(BadlandsCaves plugin, Location origin, int blockXOffset, int blockYOffset, int blockZOffset, int settingXOffset, int settingYOffset, int settingZOffset, String structureName, BlockFace redstoneBlockRelative) {
        this.plugin = plugin;
        this.origin = origin;
        this.blockXOffset = blockXOffset;
        this.blockYOffset = blockYOffset;
        this.blockZOffset = blockZOffset;
        this.settingXOffset = settingXOffset;
        this.settingYOffset = settingYOffset;
        this.settingZOffset = settingZOffset;
        this.integrity = 1;
        this.structureName = structureName;
        this.redstoneBlockRelative = redstoneBlockRelative;
    }

    public StructureTrack(BadlandsCaves plugin, Location origin, int blockXOffset, int blockYOffset, int blockZOffset, int settingXOffset, int settingYOffset, int settingZOffset, double integrity, String structureName, BlockFace redstoneBlockRelative) {
        this.plugin = plugin;
        this.origin = origin;
        this.blockXOffset = blockXOffset;
        this.blockYOffset = blockYOffset;
        this.blockZOffset = blockZOffset;
        this.settingXOffset = settingXOffset;
        this.settingYOffset = settingYOffset;
        this.settingZOffset = settingZOffset;
        this.integrity = integrity;
        this.structureName = structureName;
        this.redstoneBlockRelative = redstoneBlockRelative;
    }

    public StructureTrack(BadlandsCaves plugin, int blockXOffset, int blockYOffset, int blockZOffset, int settingXOffset, int settingYOffset, int settingZOffset, String structureName, BlockFace redstoneBlockRelative) {
        this.plugin = plugin;
        this.origin = null;
        this.blockXOffset = blockXOffset;
        this.blockYOffset = blockYOffset;
        this.blockZOffset = blockZOffset;
        this.settingXOffset = settingXOffset;
        this.settingYOffset = settingYOffset;
        this.settingZOffset = settingZOffset;
        this.integrity = 1;
        this.structureName = structureName;
        this.redstoneBlockRelative = redstoneBlockRelative;
    }

    public StructureTrack(BadlandsCaves plugin, int blockXOffset, int blockYOffset, int blockZOffset, int settingXOffset, int settingYOffset, int settingZOffset, double integrity, String structureName, BlockFace redstoneBlockRelative) {
        this.plugin = plugin;
        this.origin = null;
        this.blockXOffset = blockXOffset;
        this.blockYOffset = blockYOffset;
        this.blockZOffset = blockZOffset;
        this.settingXOffset = settingXOffset;
        this.settingYOffset = settingYOffset;
        this.settingZOffset = settingZOffset;
        this.integrity = integrity;
        this.structureName = structureName;
        this.redstoneBlockRelative = redstoneBlockRelative;
    }

    public boolean load () {
        if (origin != null) {
            load(this.origin);
            return true;
        }
        return false;
    }

    public void load(Location origin) {
        Location clone = origin.clone().add(blockXOffset, blockYOffset, blockZOffset);
        Block block = clone.getBlock();
        BlockData savedPreStrucData = block.getBlockData();

        block.setType(Material.STRUCTURE_BLOCK);

        Structure structureBlock = (Structure) block.getState();
        structureBlock.setUsageMode(UsageMode.LOAD);
        structureBlock.setRelativePosition(new BlockVector(settingXOffset, settingYOffset, settingZOffset));
        structureBlock.setStructureName(structureName);
        structureBlock.setIntegrity((float) integrity);
        structureBlock.update();

        Block relative = block.getRelative(redstoneBlockRelative);
        BlockData savedPreRSData = relative.getBlockData();
        relative.setType(Material.REDSTONE_BLOCK);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType().equals(Material.STRUCTURE_BLOCK)) block.setBlockData(savedPreStrucData);
                if (relative.getType().equals(Material.REDSTONE_BLOCK)) relative.setBlockData(savedPreRSData);
            }
        }.runTaskLater(plugin, 1);

    }
}
