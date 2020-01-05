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

public class ThirstCommand implements CommandExecutor {

    private Plugin plugin = BadlandsCaves.getPlugin(BadlandsCaves.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("thirst")) {
            if (player.isOp()) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Too few arguments.");
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.GOLD + "Your Thirst count is " + ChatColor.RED + player.getMetadata("Thirst").get(0).asDouble() + ChatColor.GOLD + ".");
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.GOLD + "The Thirst count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + targets.getMetadata("Thirst").get(0).asDouble() + ChatColor.GOLD + ".");
                                return true;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Please specify a player.");
                        return true;
                    } else if (args.length < 3) {
                        player.sendMessage(ChatColor.RED + "Please specify a Thirst value to set to.");
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                try {
                                    double change = Double.parseDouble(args[2]);
                                    targets.setMetadata("Thirst", new FixedMetadataValue(plugin, change));
                                    player.sendMessage(ChatColor.GOLD + "The Thirst count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                    return true;
                                }
                                catch (NumberFormatException e) {
                                    player.sendMessage(ChatColor.RED + "You must set the Thirst count to a double!");
                                    return true;
                                }
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
