package me.fullpotato.badlandscaves.badlandscaves.NMS.CustomBlocks;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TestBlock implements Listener {
    private BadlandsCaves plugin;

    public TestBlock(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void a (PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        player.sendMessage("block");

        Location location = player.getLocation();
        location.subtract(0, 1, 0);

        location.getBlock().setType(Material.SPAWNER);
        spawnerData(location);
    }


    public void spawnerData (Location location) {
        final int X = location.getBlockX();
        final int Y = location.getBlockY();
        final int Z = location.getBlockZ();
        final World world = location.getWorld();
        if (world == null) return;

        BlockPosition block_pos = new BlockPosition(X, Y, Z);
        if (((CraftWorld) world).getHandle().getTileEntity(block_pos) instanceof TileEntityMobSpawner) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) ((CraftWorld) world).getHandle().getTileEntity(block_pos);
            NBTTagCompound spawnerTag = spawner.b();
            spawnerTag.setShort("RequiredPlayerRange", (short) 0);


            NBTTagList armorList = new NBTTagList();
            NBTTagCompound helmet = new NBTTagCompound();
            NBTTagCompound chestplate = new NBTTagCompound();
            NBTTagCompound leggings = new NBTTagCompound();
            NBTTagCompound boots = new NBTTagCompound();

            helmet.setString("id", "minecraft:cobblestone");
            helmet.setShort("Count", (short) 1);

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInt("CustomModelData", 137);
            helmet.set("tag", tag);

            armorList.add(boots);
            armorList.add(leggings);
            armorList.add(chestplate);
            armorList.add(helmet);

            NBTTagCompound spawnData = new NBTTagCompound();
            spawnData.setString("id", "armor_stand");
            spawnData.set("ArmorItems", armorList);
            spawnerTag.set("SpawnData", spawnData);
            spawner.load(spawnerTag);
        }
    }
}
