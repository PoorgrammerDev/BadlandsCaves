package me.fullpotato.badlandscaves.Commands.TabCompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.fullpotato.badlandscaves.AlternateDimensions.Structure;

public class StructureCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        //Guard statements
        if (!command.getName().equalsIgnoreCase("structure")) return null;
        if (!(sender instanceof Player) || !sender.isOp()) return null;

        final Player player = (Player) sender;
        final List<String> output = new ArrayList<>();

        //Check for subcommands
        if (args.length == 1) {
            output.add("spawn");
            output.add("list");
        }
        else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("spawn")) {
                spawnSubcommand(player, args, output);
            }
            else if (args[0].equalsIgnoreCase("list")) {

            }
        }
        else return null;

        return output;



        // /structure spawn <structureName> <x> <y> <z> [leaveStructureBlocks true|false]
    }

    private void spawnSubcommand(Player player, String[] args, List<String> output) {
        //length 2: index[1] = structureName
        //length 3: index[2] = x
        //length 4: index[3] = y
        //length 5: index[4] = z
        //length 6: index[5] = leaveStructureBlocks
        
        switch (args.length) {
            //length 2: index[1] = structureName
            case 2:
                for (Structure structure : Structure.values()) {
                    final String name = structure.name();
                    if (name.startsWith(args[1].toUpperCase())) {
                        output.add(name.toLowerCase());
                    }
                }
            break;
            
            //xyz
            case 3:
            case 4:
            case 5:
                if (args[args.length - 1].length() == 0) {
                    output.add("~");
                }    
            break;
            
            //leaveStructureBlocks; true | false
            case 6:
                if ("true".toUpperCase().startsWith(args[5].toUpperCase())) {
                    output.add("true");
                }
                if ("false".toUpperCase().startsWith(args[5].toUpperCase())) {
                    output.add("false");
                }
            break; 
        }
    }
    
}
