package me.fullpotato.badlandscaves.NMS.TPSGetter;

import me.fullpotato.badlandscaves.BadlandsCaves;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;

public class TPSGetter_1_16_R2 implements TPSGetter {
    private final BadlandsCaves plugin;

    public TPSGetter_1_16_R2(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public double[] getRecentTPS() {
        MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
        return server.recentTps.clone();
    }
}
