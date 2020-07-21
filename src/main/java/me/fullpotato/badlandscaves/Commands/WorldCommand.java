package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
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
        if (command.getName().equalsIgnoreCase("world")) {
            if (commandSender.isOp()) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (args.length > 0) {
                        for (World world : plugin.getServer().getWorlds()) {
                            if (args[0].equalsIgnoreCase(world.getName())) {
                                commandSender.sendMessage("§6Teleporting you to §c" + world.getName() + "§6.");
                                player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                return true;
                            }
                        }
                        commandSender.sendMessage("§cInvalid world!");

                    }
                    else {
                        tooFewArgs(commandSender);
                    }
                }
                else {
                    commandSender.sendMessage("§cYou must be a Player to use this command!");
                }
            }
            else {
                notOp(commandSender);
            }
            return true;
        }
        return false;
    }
}
