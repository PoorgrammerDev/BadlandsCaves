package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChaosCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;

    public ChaosCommand(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("chaos")) {
            if (sender.isOp()) {
                if (args.length == 0 || args[0].equalsIgnoreCase("get")) {
                    int chaos = plugin.getConfig().getInt("system.chaos_level");
                    sender.sendMessage(ChatColor.GOLD + "The Chaos level is " + ChatColor.RED + chaos + ChatColor.GOLD + ".");
                    return true;
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        mustSpecifyValue(sender);
                        return true;
                    }
                    else {
                        try {
                            int chaos = Integer.parseInt(args[1]);
                            if (chaos < 0 || chaos > 100) {
                                sender.sendMessage(ChatColor.RED + "Chaos value must stay between 0 and 100.");
                            }
                            else {
                                plugin.getConfig().set("system.chaos_level", chaos);
                                plugin.saveConfig();

                                sender.sendMessage(ChatColor.GOLD + "The Chaos level has been set to " + ChatColor.RED + chaos + ChatColor.GOLD + ".");
                            }
                            return true;
                        }
                        catch (NumberFormatException e) {
                            valueNotValid(sender);
                            return true;
                        }
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
