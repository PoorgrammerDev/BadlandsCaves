package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.LoadCustomItems;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Guide extends Commands implements CommandExecutor {
    private BadlandsCaves plugin;

    public Guide(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equals("guide")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                int slot = player.getInventory().firstEmpty();
                if (slot != -1) {
                    player.getInventory().addItem(LoadCustomItems.getGuideBook(plugin));
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "You must have an open space in your inventory!");
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            }
            return true;
        }
        return false;
    }
}
