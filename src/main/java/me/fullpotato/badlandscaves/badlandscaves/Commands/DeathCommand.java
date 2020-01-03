package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class DeathCommand implements CommandExecutor {

    private Plugin plugin = BadlandsCaves.getPlugin(BadlandsCaves.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("deaths")) {
            if (player.isOp()) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Too few arguments.");
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.GOLD + "Your Deaths count is " + ChatColor.RED + player.getMetadata("Deaths").get(0).asDouble() + ChatColor.GOLD + ".");
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.GOLD + "The Deaths count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + targets.getMetadata("Deaths").get(0).asDouble() + ChatColor.GOLD + ".");
                                return true;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Please specify a player.");
                        return true;
                    } else if (args.length < 3) {
                        player.sendMessage(ChatColor.RED + "Please specify a Deaths value to set to.");
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                targets.setMetadata("Deaths", new FixedMetadataValue(plugin, args[2]));
                                player.sendMessage(ChatColor.GOLD + "The Deaths count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + args[2] + ChatColor.GOLD + ".");
                                return true;
                            }
                        }
                    }
                }
                }
            else {
                player.sendMessage(ChatColor.RED + "You must be an operator to access this command.");
                return true;
            }
        }
        return false;
    }
}
