package me.fullpotato.badlandscaves.Commands.TabCompleters;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class WorldCommandTabCompleter implements TabCompleter {
    private final BadlandsCaves plugin;

    public WorldCommandTabCompleter(BadlandsCaves plugin) {
        this.plugin = plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("world")) return null;
        if (!(sender instanceof Player) || !sender.isOp()) return null;

        final Player player = (Player) sender;
        final List<String> list = new ArrayList<>();

        switch (args.length) {
            //World names
            case 1:
                File[] worlds = plugin.getServer().getWorldContainer().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() && Arrays.asList(file.list()).contains("level.dat");
                    }
                });

                for (File world : worlds) {
                    list.add(world.getName());
                }
                return list;

            default:
                return null;
        }


    }

}