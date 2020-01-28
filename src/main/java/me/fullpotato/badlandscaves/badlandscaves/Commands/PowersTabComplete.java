package me.fullpotato.badlandscaves.badlandscaves.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PowersTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("powers")) {
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
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }

            else if (args.length == 3) {
                String[] strings = {
                        "supernatural",
                        "displace",
                        "enhanced_eyes",
                        "withdraw",
                        "possession",
                        "endurance",
                        "agility",
                };

                for (String str : strings) {
                    if (str.contains(args[2])) {
                        list.add(str);
                    }
                }
            }
            return list;
        }
        return null;
    }
}
