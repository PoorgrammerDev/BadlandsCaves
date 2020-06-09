package me.fullpotato.badlandscaves.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class Commands {
    public void getOrSet (CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Possible Parameters: GET or SET.");
    }
    public void tooFewArgs (CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Too few arguments.");
    }
    public void notOp (CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You must be an operator to access this command.");
    }
    public void playerNotValid (CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Please specify a valid player.");
    }

    public void mustSpecifyValue(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You must specify a value to set it to.");
    }

    public void valueNotValid (CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You must provide a valid entry!");
    }

    public Player getPlayer(String str, Collection<? extends Player> players) {
        for (Player player : players) {
            if (str.equalsIgnoreCase(player.getDisplayName()) || str.equalsIgnoreCase(player.getName()) || str.equalsIgnoreCase(player.getUniqueId().toString())) {
                return player;
            }
        }
        return null;
    }
}
