package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HardmodeCommand implements CommandExecutor {

    private BadlandsCaves plugin;
    public HardmodeCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hardmode")) {
            if (sender.isOp()) {
                if (args.length == 0 || args[0].equalsIgnoreCase("get")) {
                    boolean isHM = plugin.getConfig().getBoolean("game_values.hardmode");
                    sender.sendMessage(ChatColor.GOLD + "Hardmode is currently set to " + ChatColor.RED + isHM + ChatColor.GOLD + ".");
                    return true;
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    int value = -1;
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "You must specify a value to set it to. (TRUE/FALSE)");
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("true")) {
                        value = 1;
                    }
                    else if (args[1].equalsIgnoreCase("false")) {
                        value = 0;
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Possible Parameters: TRUE or FALSE.");
                        return true;
                    }


                    boolean set_to = false;
                    if (value == 0) {
                        set_to = false;
                    }
                    else if (value == 1) {
                        set_to = true;
                    }

                    plugin.getConfig().set("game_values.hardmode", set_to);
                    plugin.saveConfig();
                    sender.sendMessage(ChatColor.GOLD + "Hardmode is now set to " + ChatColor.RED + set_to + ChatColor.GOLD + ".");

                    if (args.length < 3 || args[2] == null || args[2].equalsIgnoreCase("reload")) {
                        Bukkit.reload();
                    }
                    else if (args[2].equalsIgnoreCase("noreload")) {
                    }
                    else {
                        Bukkit.reload();
                    }
                    return true;
                }
            }
        }
        return true;
    }
}
