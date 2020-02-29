package me.fullpotato.badlandscaves.badlandscaves.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomItemTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("customitem")) {
            if (sender.isOp()) {
                List<String> list = new ArrayList<>();

                if (args.length == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (args[0].isEmpty() || player.getName().toUpperCase().startsWith(args[0].toUpperCase())) {
                            list.add(player.getName());
                        }
                    }
                }

                else if (args.length == 2) {
                    final String[] items = {
                            "starter_sapling",
                            "starter_bone_meal",
                            "toxic_water",
                            "purified_water",
                            "antidote",
                            "mana_potion",
                            "purge_essence",
                            "hell_essence",
                            "magic_essence",
                            "displace",
                            "withdraw",
                            "enhanced_eyes",
                            "possess",
                            "tiny_blaze_powder",
                            "tainted_powder",
                            "zombie_soul",
                            "creeper_soul",
                            "skeleton_soul",
                            "spider_soul",
                            "pigzombie_soul",
                            "ghast_soul",
                            "silverfish_soul",
                            "witch_soul",
                            "phantom_soul",
                    };

                    if (args[1].isEmpty()) {
                        list.addAll(Arrays.asList(items));
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
