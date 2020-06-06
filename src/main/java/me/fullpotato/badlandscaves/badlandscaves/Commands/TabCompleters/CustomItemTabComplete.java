package me.fullpotato.badlandscaves.badlandscaves.Commands.TabCompleters;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomItemTabComplete implements TabCompleter {
    private final BadlandsCaves plugin;
    private final ArrayList<String> items = new ArrayList<>();
    public CustomItemTabComplete(BadlandsCaves plugin) {
        this.plugin = plugin;

        for (CustomItem customItem : CustomItem.values()) {
            items.add(customItem.toString().toLowerCase());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("customitem")) {
            if (sender.isOp()) {
                List<String> list = new ArrayList<>();

                if (args.length == 1) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (args[0].isEmpty() || player.getName().toUpperCase().startsWith(args[0].toUpperCase())) {
                            list.add(player.getName());
                        }
                    }
                }

                else if (args.length == 2) {
                    if (args[1].isEmpty()) {
                        list = items;
                    }
                    else {
                        for (String item : items) {
                            if (item.toUpperCase().startsWith(args[1].toUpperCase())) {
                                list.add(item);
                            }
                        }
                    }
                }

                return list;
            }
        }
        return null;
    }
}
