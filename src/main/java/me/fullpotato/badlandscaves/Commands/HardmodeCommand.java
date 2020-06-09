package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HardmodeCommand extends Commands implements CommandExecutor {

    private final BadlandsCaves plugin;
    public HardmodeCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hardmode")) {
            if (sender.isOp()) {
                if (args.length == 0 || args[0].equalsIgnoreCase("get")) {
                    boolean isHM = plugin.getConfig().getBoolean("system.hardmode");
                    sender.sendMessage(ChatColor.GOLD + "Hardmode is currently set to " + ChatColor.RED + isHM + ChatColor.GOLD + ".");
                    return true;
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    boolean value;
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "You must specify a value to set it to. (TRUE/FALSE)");
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("true")) {
                        value = true;
                    }
                    else if (args[1].equalsIgnoreCase("false")) {
                        value = false;
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Possible Parameters: TRUE or FALSE.");
                        return true;
                    }

                    plugin.getConfig().set("system.hardmode", value);
                    plugin.saveConfig();
                    sender.sendMessage(ChatColor.GOLD + "Hardmode is now set to " + ChatColor.RED + value + ChatColor.GOLD + ".");

                    plugin.getServer().resetRecipes();
                    plugin.loadCraftingRecipes();

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
