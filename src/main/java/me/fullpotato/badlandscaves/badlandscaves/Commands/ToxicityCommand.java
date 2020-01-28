package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class ToxicityCommand implements CommandExecutor {

    private BadlandsCaves plugin;
    public ToxicityCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("toxicity")) {
            if (sender.isOp()) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Too few arguments.");
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.sendMessage(ChatColor.GOLD + "Your Toxicity count is " + ChatColor.RED + player.getMetadata("Toxicity").get(0).asDouble() + ChatColor.GOLD + ".");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Please specify a player.");
                        }
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                sender.sendMessage(ChatColor.GOLD + "The Toxicity count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + targets.getMetadata("Deaths").get(0).asDouble() + ChatColor.GOLD + ".");
                                return true;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Please specify a player.");
                        return true;
                    } else if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Please specify a Toxicity value to set to.");
                        return true;
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                try {
                                    double change = Double.parseDouble(args[2]);
                                    targets.setMetadata("Toxicity", new FixedMetadataValue(plugin, change));
                                    sender.sendMessage(ChatColor.GOLD + "The Toxicity count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                    return true;
                                }
                                catch (NumberFormatException e) {
                                    sender.sendMessage(ChatColor.RED + "You must set the Toxicity count to a double!");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "You must be an operator to access this command.");
                return true;
            }
        }
        return false;
    }
}
