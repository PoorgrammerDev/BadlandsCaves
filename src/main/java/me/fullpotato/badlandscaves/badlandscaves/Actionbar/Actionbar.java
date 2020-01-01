package me.fullpotato.badlandscaves.badlandscaves.Actionbar;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Actionbar extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatMessageType.ACTION_BAR + "ASDF");
        }
    }
}
