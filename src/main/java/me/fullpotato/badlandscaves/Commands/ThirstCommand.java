package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThirstCommand extends Commands implements CommandExecutor {

    private final BadlandsCaves plugin;
    private final PlayerEffects playerEffects;
    public ThirstCommand (BadlandsCaves bcav) {
        plugin = bcav;
        playerEffects = new PlayerEffects(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("thirst")) {
            if (sender.isOp()) {
                if (args.length == 0) {
                    tooFewArgs(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.sendMessage(ChatColor.GOLD + "Your Thirst count is " + ChatColor.RED + PlayerScore.THIRST.getScore(plugin, player) + ChatColor.GOLD + ".");
                        }
                        else {
                            playerNotValid(sender);
                        }
                    } else {
                        for (Player targets : plugin.getServer().getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(targets.getDisplayName()) || args[1].equalsIgnoreCase(targets.getName()) || args[1].equalsIgnoreCase(targets.getUniqueId().toString())) {
                                sender.sendMessage(ChatColor.GOLD + "The Thirst count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + (PlayerScore.THIRST.getScore(plugin, targets)) + ChatColor.GOLD + ".");
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
                                        PlayerScore.THIRST.setScore(plugin, targets, change);
                                        sender.sendMessage(ChatColor.GOLD + "The Thirst count of " + ChatColor.RED + targets.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + change + ChatColor.GOLD + ".");
                                        playerEffects.applyEffects(targets, true);
                                        return true;
                                    }
                                    catch (NumberFormatException e) {
                                        sender.sendMessage(ChatColor.RED + "You must set the Thirst count to a double.");
                                        return true;
                                    }
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "Please specify a Thirst value to set to.");
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
