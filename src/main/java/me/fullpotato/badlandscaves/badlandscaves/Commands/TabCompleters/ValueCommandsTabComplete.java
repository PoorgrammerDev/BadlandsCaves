package me.fullpotato.badlandscaves.badlandscaves.Commands.TabCompleters;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ValueCommandsTabComplete implements TabCompleter {
    private BadlandsCaves plugin;

    public ValueCommandsTabComplete(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("deaths") ||
                command.getName().equalsIgnoreCase("thirst") ||
                command.getName().equalsIgnoreCase("toxicity") ||
                command.getName().equalsIgnoreCase("mana")) {
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
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (args[1].isEmpty() || player.getName().toUpperCase().startsWith(args[1].toUpperCase())) {
                            list.add(player.getName());
                        }
                    }
                }

                if (command.getName().equalsIgnoreCase("mana") && args.length == 4) {
                    list.add("max");
                }

                return list;
            }
        }
        return null;
    }
}
