package me.fullpotato.badlandscaves.badlandscaves.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DTT_TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("deaths") ||
                command.getName().equalsIgnoreCase("thirst") ||
                command.getName().equalsIgnoreCase("toxicity")) {
            List<String> list = new ArrayList<>();

            if (args.length == 1) {
                list.add("get");
                list.add("set");
            }

            else if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }

            return list;
        }
        return null;
    }
}
