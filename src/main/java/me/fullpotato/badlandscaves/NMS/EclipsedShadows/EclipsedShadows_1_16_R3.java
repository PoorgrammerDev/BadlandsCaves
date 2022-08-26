package me.fullpotato.badlandscaves.NMS.EclipsedShadows;

import com.mojang.datafixers.util.Pair;
import me.fullpotato.badlandscaves.BadlandsCaves;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EclipsedShadows_1_16_R3 implements EclipsedShadowsNMS {
    private final BadlandsCaves plugin;
    private final EnumItemSlot[] enumItemSlots = {
            EnumItemSlot.HEAD,
            EnumItemSlot.CHEST,
            EnumItemSlot.LEGS,
            EnumItemSlot.FEET,
    };

    public EclipsedShadows_1_16_R3(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setArmorVisible(Player ply, boolean visible) {
        final EntityPlayer player = ((CraftPlayer) ply).getHandle();
        final List<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();

        for (EnumItemSlot enumItemSlot : enumItemSlots) {
            list.add(new Pair<>(enumItemSlot, visible ? player.getEquipment(enumItemSlot) : CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
        }

        final PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getId(), list);
        plugin.getServer().getOnlinePlayers().forEach(online -> ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet));
    }
}
