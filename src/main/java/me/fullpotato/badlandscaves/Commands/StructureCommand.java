package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.AlternateDimensions.DimensionStructures;
import me.fullpotato.badlandscaves.AlternateDimensions.DimensionStructures.Structure;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class StructureCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;
    private final DimensionStructures structureManager;

    public StructureCommand(BadlandsCaves plugin, DimensionStructures structureManager) {
        this.plugin = plugin;
        this.structureManager = structureManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] args) {
        //Right command
        if (!command.getName().equalsIgnoreCase("structure")) return false;

        //Is OP
        if (!commandSender.isOp()) {
            super.notOp(commandSender);
            return true;
        }

        //Is player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a Player to use this command!");
            return true;
        }

        if (args.length == 0) {
            super.tooFewArgs(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;
        final String subCommand = args[0].toUpperCase();

        switch (subCommand) {
            case "SPAWN":
                spawnSubcommand(player, args);
                break;
            case "LIST":
                //TODO: Implement functionality
                break;
            default:
                commandSender.sendMessage(ChatColor.RED + "Invalid subcommand!");
                break;
        }

        return true;
    }

    // /structure spawn <structureName> <x> <y> <z> [leaveStructureBlocks true|false]
    private void spawnSubcommand(final Player player, final String[] args) {
        if (args.length < 2) {
            tooFewArgs(player);
            return;
        }

        //Argument list:
        //[0]: spawn
        //[1]: <structureName>
        //[2]: <x>
        //[3]: <y>
        //[4]: <z>

        //Determine structure type to spawn
        Structure structure = null;
        try {
            structure = Structure.valueOf(args[1].toUpperCase());
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid structure name!");
            return;
        }

        //Determine location to spawn at =====
        Location location = player.getLocation();

        if (args.length > 2) {
            try {
                location.setX(parseCoordinate(args[2], location.getX()));
                location.setY(parseCoordinate(args[3], location.getY()));
                location.setZ(parseCoordinate(args[4], location.getZ()));
            }
            catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates!");
                return;
            }
        }

        // ======================
        boolean leaveStructureBlocks = false;
        if (args.length > 5) {
            try {
                leaveStructureBlocks = Boolean.parseBoolean(args[5]);
            }
            catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid argument!");
                return;
            }
        }


        player.sendMessage(ChatColor.GOLD + "Generating structure " + ChatColor.RED + structure.name()
            + ChatColor.GOLD + " at location ("
            + ChatColor.RED + location.getBlockX() + ChatColor.GOLD + ", "
            + ChatColor.RED + location.getBlockY() + ChatColor.GOLD + ", "
            + ChatColor.RED + location.getBlockZ() + ChatColor.GOLD + ")."
        );

        this.structureManager.generateStructure(location, structure, leaveStructureBlocks, true);
    }

    private double parseCoordinate(String string, double base) throws IllegalArgumentException {
        if (string.charAt(0) == '~') {
            if (string.length() == 1) return base;

            return base + Integer.parseInt(string.substring(1));
        }

        return Integer.parseInt(string);
    }
}
