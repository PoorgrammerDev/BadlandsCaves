package me.fullpotato.badlandscaves.badlandscaves.Commands.TabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChaosCommandTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("chaos")) {
            if (sender.isOp()) {
                List<String> list = new ArrayList<>();

                if (args.length == 1) {
                    if (args[0].startsWith("g")) list.add("get");
                    else if (args[0].startsWith("s")) list.add("set");
                    else {
                        list.add("get");
                        list.add("set");
                    }
                }

                return list;
            }
        }
        return null;
    }
}
