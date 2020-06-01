package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.Info.CraftingGuide;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftingGuideCommand extends Commands implements CommandExecutor {
    private BadlandsCaves plugin;

    public CraftingGuideCommand(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equals("craftguide")) {
            if (commandSender instanceof Player) {
                CraftingGuide craftingGuide = new CraftingGuide(plugin);
                craftingGuide.openGUI((Player) commandSender);
                return true;
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                return true;
            }
        }
        return false;
    }
}
