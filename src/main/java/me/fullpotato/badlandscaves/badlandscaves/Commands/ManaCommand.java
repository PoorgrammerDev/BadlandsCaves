package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class ManaCommand extends Commands implements CommandExecutor {

    private BadlandsCaves plugin;
    public ManaCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mana")) {
            if (sender.isOp()) {
                if (args.length == 0) {
                    tooFewArgs(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.sendMessage(ChatColor.GOLD + "Your Mana count is " + ChatColor.RED + player.getMetadata("Mana").get(0).asDouble() + ChatColor.GOLD + ".");
                        }
                        else {
                            playerNotValid(sender);
                        }
                    } else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                sender.sendMessage(ChatColor.GOLD + "The Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + targets.getMetadata("Mana").get(0).asDouble() + ChatColor.GOLD + ".");
                                return true;
                            }
                        }
                        playerNotValid(sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        tooFewArgs(sender);
                        return true;
                    }
                    else {
                        for (Player targets : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                if (args.length > 2) {
                                    // TODO: 3/31/2020 also make the command be able to set MAX mana too
                                    try {
                                        double change = Double.parseDouble(args[2]);
                                        targets.setMetadata("Mana", new FixedMetadataValue(plugin, change));
                                        sender.sendMessage(ChatColor.GOLD + "The Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                        return true;
                                    }
                                    catch (NumberFormatException e) {
                                        sender.sendMessage(ChatColor.RED + "You must set the Mana count to a double.");
                                        return true;
                                    }
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "Please specify a Mana value to set to.");
                                    return true;
                                }
                            }
                        }
                        playerNotValid(sender);
                        return true;
                    }
                }
                else {
                    getOrSet(sender);
                    return true;
                }
            }
            else {
                notOp(sender);
                return true;
            }
        }
        return false;
    }
}
