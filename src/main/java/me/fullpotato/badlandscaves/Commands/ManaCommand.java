package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ManaCommand extends Commands implements CommandExecutor {

    private final BadlandsCaves plugin;
    public ManaCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mana")) {
            if (sender.isOp()) {
                if (args.length == 0) {
                    tooFewArgs(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.sendMessage(ChatColor.GOLD + "Your Mana count is " + ChatColor.RED + PlayerScore.MANA.getScore(plugin, player) + ChatColor.GOLD + ".");
                        }
                        else {
                            playerNotValid(sender);
                        }
                    } else {
                        for (Player targets : plugin.getServer().getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                if (args.length > 2 && args[2].equalsIgnoreCase("max")) {
                                    sender.sendMessage(ChatColor.GOLD + "The Max Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + (PlayerScore.MAX_MANA.getScore(plugin, targets)) + ChatColor.GOLD + ".");
                                }
                                else {
                                    sender.sendMessage(ChatColor.GOLD + "The Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + (PlayerScore.MANA.getScore(plugin, targets)) + ChatColor.GOLD + ".");
                                }
                                return true;
                            }
                        }
                        playerNotValid(sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        tooFewArgs(sender);
                    }
                    else {
                        for (Player targets : plugin.getServer().getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                if (args.length > 2) {
                                    try {
                                        double change = Double.parseDouble(args[2]);
                                        if (args.length > 3 && args[3].equalsIgnoreCase("max")) {
                                            PlayerScore.MAX_MANA.setScore(plugin, targets, change);
                                            sender.sendMessage(ChatColor.GOLD + "The Max Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                        }
                                        else {
                                            PlayerScore.MANA.setScore(plugin, targets, change);
                                            sender.sendMessage(ChatColor.GOLD + "The Mana count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                        }
                                        return true;
                                    }
                                    catch (NumberFormatException e) {
                                        sender.sendMessage(ChatColor.RED + "You must set the Mana count to a number.");
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
                    }
                    return true;
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
