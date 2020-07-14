package me.fullpotato.badlandscaves.Commands.TabCompleters;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BackroomsCommandTabComplete implements TabCompleter {
    private final BadlandsCaves plugin;

    public BackroomsCommandTabComplete(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] args) {
        if (command.getName().equalsIgnoreCase("backrooms")) {
            if (commandSender.isOp()) {
                ArrayList<String> list = new ArrayList<>();

                if (args.length == 1) {
                    if (args[0].isEmpty() || "get".toUpperCase().startsWith(args[0].toUpperCase())) list.add("get");
                    if (args[0].isEmpty() || "enter".toUpperCase().startsWith(args[0].toUpperCase())) list.add("enter");
                    if (args[0].isEmpty() || "leave".toUpperCase().startsWith(args[0].toUpperCase())) list.add("leave");
                }

                else if (args.length == 2) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (args[1].isEmpty() || player.getName().toUpperCase().startsWith(args[1].toUpperCase())) {
                            list.add(player.getName());
                        }
                    }
                }

                else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("enter")) {
                        if (args[2].isEmpty() || "backrooms".toUpperCase().startsWith(args[2].toUpperCase())) list.add("backrooms");
                        if (args[2].isEmpty() || "darkrooms".toUpperCase().startsWith(args[2].toUpperCase())) list.add("darkrooms");
                    }
                }
                return list;
            }
        }
        return null;
    }
}
