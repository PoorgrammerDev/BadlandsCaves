package me.fullpotato.badlandscaves.Commands.TabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class HM_TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("hardmode")) {
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

                else if (args.length == 2) {
                    if (args[1].startsWith("t")) list.add("true");
                    else if (args[1].startsWith("f")) list.add("false");
                    else {
                        list.add("true");
                        list.add("false");
                    }
                }

                return list;
            }
        }
        return null;
    }
}
