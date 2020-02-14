package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;

public class PowersCommand implements CommandExecutor {
    private BadlandsCaves plugin;
    public PowersCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("powers")) {
            if (sender.isOp()) {

                HashMap<String,String> powers = new HashMap<>();
                powers.put("supernatural","has_supernatural_powers");
                powers.put("displace","displace_level");
                powers.put("enhanced_eyes","eyes_level");
                powers.put("withdraw","withdraw_level");
                powers.put("possession","possess_level");
                powers.put("endurance","endurance_level");
                powers.put("agility","agility_level");

                HashMap<String, Integer> powers_max_levels = new HashMap<>();
                powers_max_levels.put("supernatural", 1);
                powers_max_levels.put("displace", 2);
                powers_max_levels.put("enhanced_eyes", 2);
                powers_max_levels.put("withdraw", 2);
                powers_max_levels.put("possession", 2);
                powers_max_levels.put("endurance", 2);
                powers_max_levels.put("agility", 2);

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Too few arguments.");
                    return true;
                }
                else if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Too few arguments. Please specify a player.");
                        return true;
                    }
                    else {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (args[1].equalsIgnoreCase(target.getDisplayName()) || args[1].equalsIgnoreCase(target.getName()) || args[1].equalsIgnoreCase(target.getUniqueId().toString())) {
                                if (args.length < 3) {
                                    sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.RED + target.getDisplayName() + ChatColor.GOLD + " has the following values:");
                                    for (String str : powers.keySet()) {
                                        sender.sendMessage(ChatColor.GOLD + "The value of " + ChatColor.RED + str + ChatColor.GOLD + " is " + ChatColor.RED + target.getMetadata(powers.get(str)).get(0).asInt() + ChatColor.GOLD + ".");
                                    }
                                }
                                else if (powers.containsKey(args[2])) {
                                    sender.sendMessage(ChatColor.GOLD + "The value of " + ChatColor.RED + args[2] + ChatColor.GOLD + " for Player " + ChatColor.RED + target.getDisplayName() + ChatColor.GOLD + " is " + ChatColor.RED + target.getMetadata(powers.get(args[2])).get(0).asInt() + ChatColor.GOLD + ".");
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "You must provide a valid entry!");
                                }
                                return true;
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Too few arguments. Please specify a player.");
                    }
                    else {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (!args[1].equalsIgnoreCase(target.getDisplayName()) && !args[1].equalsIgnoreCase(target.getName()) && !args[1].equalsIgnoreCase(target.getUniqueId().toString())) continue;

                            if (args.length < 3) {
                                sender.sendMessage(ChatColor.RED + "Too few arguments. Please specify an entry.");
                                return true;
                            }
                            else if (powers.containsKey(args[2])) {
                                if (args.length < 4) {
                                    sender.sendMessage(ChatColor.RED + "Too few arguments. You need to specify a level to set. The max level of " + args[2] + " is " + powers_max_levels.get(args[2]) + ".");
                                    return true;
                                }
                                else {
                                    int input_value;
                                    try {
                                        input_value = Integer.parseInt(args[3]);
                                    }
                                    catch (NumberFormatException exception) {
                                        sender.sendMessage(ChatColor.RED + "Please enter a valid integer.");
                                        return true;
                                    }

                                    if (input_value >= 0 && input_value <= powers_max_levels.get(args[2])) {
                                        target.setMetadata(powers.get(args[2]), new FixedMetadataValue(plugin, input_value));
                                        sender.sendMessage(ChatColor.GOLD + "The value of " + ChatColor.RED + args[2] + ChatColor.GOLD + " for Player " + ChatColor.RED + target.getDisplayName() + ChatColor.GOLD + " has been set to " + ChatColor.RED + input_value + ChatColor.GOLD + ".");
                                        return true;
                                    }
                                    else {
                                        sender.sendMessage(ChatColor.RED + "Please make sure the level stays within bounds. The max level of " + args[2] + " is " + powers_max_levels.get(args[2]) + ".");
                                        return true;
                                    }
                                }
                            }
                            sender.sendMessage(ChatColor.RED + "You must specify a valid player!");
                            return true;
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
