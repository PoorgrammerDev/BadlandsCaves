package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BackroomsCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;
    private final BackroomsManager backroomsManager;
    private final World backrooms;
    private final Random random = new Random();

    public BackroomsCommand(BadlandsCaves plugin, BackroomsManager backroomsManager) {
        this.plugin = plugin;
        this.backroomsManager = backroomsManager;
        backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] args) {
        if (command.getName().equalsIgnoreCase("backrooms")) {
            if (commandSender.isOp()) {
                if (args.length < 1) {
                    tooFewArgs(commandSender);
                    return true;
                }
                else {
                    if (args[0].equalsIgnoreCase("get")) {
                        Player player = null;
                        if (args.length < 2) {
                            if (commandSender instanceof Player) {
                                player = (Player) commandSender;
                            }
                        }
                        else {
                            player = getPlayer(args[1], plugin.getServer().getOnlinePlayers());
                        }

                        if (player == null) {
                            playerNotValid(commandSender);
                        }
                        else {
                            if (player.getWorld().equals(backrooms)) {
                                int time = (int) PlayerScore.BACKROOMS_TIMER.getScore(plugin, player);
                                boolean darkrooms = player.getLocation().getY() < 55;

                                commandSender.sendMessage("§c" + player.getDisplayName() + " §6has §c" + time + " §6seconds left in the §c" + (darkrooms ? "Darkrooms" : "Backrooms") + "§6.");
                            }
                            else {
                                commandSender.sendMessage("§c" + player.getDisplayName() + " §6is not currently in the Backrooms or Darkrooms.");
                            }
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("enter")) {
                        Player player = null;
                        if (args.length < 2) {
                            if (commandSender instanceof Player) {
                                player = (Player) commandSender;
                            }
                        }
                        else {
                            player = getPlayer(args[1], plugin.getServer().getOnlinePlayers());
                        }

                        if (player == null) {
                            playerNotValid(commandSender);
                            return true;
                        }
                        else {
                            if (args.length > 2) {
                                if (args[2].equalsIgnoreCase("backrooms")) {
                                    if (player.getWorld().equals(backrooms)) {
                                        commandSender.sendMessage("§6Could not send §c" + player.getDisplayName() + " §6to the §cBackrooms§6, as they are already there.");
                                    }
                                    else {
                                        commandSender.sendMessage("§c" + player.getDisplayName() + " §6has been sent to the §cBackrooms§6.");
                                        backroomsManager.enterBackRooms(player, random, BackroomsManager.BackroomsType.BACKROOMS);
                                    }
                                    return true;
                                }
                                else if (args[2].equalsIgnoreCase("darkrooms")) {
                                    if (player.getWorld().equals(backrooms)) {
                                        commandSender.sendMessage("§6Could not send §c" + player.getDisplayName() + " §6to the §cDarkrooms§6, as they are already there.");
                                    }
                                    else {
                                        commandSender.sendMessage("§c" + player.getDisplayName() + " §6has been sent to the §cDarkrooms§6.");
                                        backroomsManager.enterBackRooms(player, random, BackroomsManager.BackroomsType.DARKROOMS);
                                    }
                                    return true;
                                }
                                else {
                                    valueNotValid(commandSender);
                                    return true;
                                }
                            }
                            else {
                                if (player.getWorld().equals(backrooms)) {
                                    commandSender.sendMessage("§6Could not send §c" + player.getDisplayName() + " §6to the Backrooms, as they are already there.");
                                }
                                else {
                                    commandSender.sendMessage("§c" + player.getDisplayName() + " §6has been sent to the Backrooms.");
                                    backroomsManager.enterBackRooms(player, random);
                                }
                                return true;
                            }
                        }
                    }
                    else if (args[0].equalsIgnoreCase("leave")) {
                        Player player = null;
                        if (args.length < 2) {
                            if (commandSender instanceof Player) {
                                player = (Player) commandSender;
                            }
                        }
                        else {
                            player = getPlayer(args[1], plugin.getServer().getOnlinePlayers());
                        }

                        if (player == null) {
                            playerNotValid(commandSender);
                        }
                        else {
                            if (player.getWorld().equals(backrooms)) {
                                commandSender.sendMessage("§c" + player.getDisplayName() + " §6has been removed from the Backrooms.");
                                backroomsManager.leaveBackRooms(player);
                            }
                            else {
                                commandSender.sendMessage("§6Cannot remove §c" + player.getDisplayName() + "§6, as they are not in the Backrooms.");
                            }
                        }
                        return true;
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + "Possible Parameters: GET, ENTER, or LEAVE.");
                        return true;
                    }
                }
            }
            else {
                notOp(commandSender);
                return true;
            }
        }
        return false;
    }
}
