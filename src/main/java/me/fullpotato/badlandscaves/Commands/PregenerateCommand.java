package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.AlternateDimensions.PregenerateDimensions;
import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PregenerateCommand extends Commands implements CommandExecutor {
    private final PregenerateDimensions pregenerateDimensions;

    public PregenerateCommand(BadlandsCaves plugin) {
        pregenerateDimensions = new PregenerateDimensions(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("pregenerate")) {
            if (commandSender.isOp()) {
                if (pregenerateDimensions.attemptPregenerate()) {
                    commandSender.sendMessage(ChatColor.GREEN + "An Alternate Dimension has pre-generated!");
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "Could not generate dimension.");
                }
            }
            else {
                notOp(commandSender);
            }
            return true;
        }
        return false;
    }
}
