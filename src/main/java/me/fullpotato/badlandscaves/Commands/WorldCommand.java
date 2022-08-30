package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.UnloadedWorld;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;


public class WorldCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;

    public WorldCommand(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] args) {
        //Guard statements
        if (!command.getName().equalsIgnoreCase("world")) return false;
        if (!commandSender.isOp()) {
            super.notOp(commandSender);
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cYou must be a Player to use this command!");
            return true;
        }
        if (args.length < 1) {
            super.tooFewArgs(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;

        //Get a loaded world
        World world = plugin.getServer().getWorld(args[0]);
        if (world != null) {
            commandSender.sendMessage("§6Teleporting you to §c" + world.getName() + "§6.");
            player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            return true;
        }

        //Search for an unloaded world
        UnloadedWorld unloadedWorld = new UnloadedWorld(args[0]);
        if (unloadedWorld.exists()) {
            //FIXME: This does not load the world generator properly
            unloadedWorld.load(plugin);

            world = plugin.getServer().getWorld(args[0]);
            if (world != null) {
                commandSender.sendMessage("§6Teleporting you to §c" + world.getName() + "§6.");
                player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                return true;
            }
        }


        commandSender.sendMessage("§cInvalid world!");
        return true;
    }
}
