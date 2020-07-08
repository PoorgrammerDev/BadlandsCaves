package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Info.GuideBook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GuideCommand extends Commands implements CommandExecutor {
    private BadlandsCaves plugin;

    public GuideCommand(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equals("guide")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                int slot = player.getInventory().firstEmpty();
                if (slot != -1) {
                    player.getInventory().addItem(GuideBook.getGuideBook(plugin));
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
