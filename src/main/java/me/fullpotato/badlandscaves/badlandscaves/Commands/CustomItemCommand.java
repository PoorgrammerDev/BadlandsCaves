package me.fullpotato.badlandscaves.badlandscaves.Commands;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemCommand implements CommandExecutor {
    private BadlandsCaves plugin;
    public CustomItemCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("customitem")) {
            if (sender.isOp()) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Too few arguments.");
                    return true;
                }
                else {
                    Player target = getPlayer(args[0]);
                    if (target != null) {
                        try {
                            ItemStack item = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items." + args[1]).getValues(true));
                            if (args.length >= 3) {
                                try {
                                    int amount = Math.max(Math.min(Integer.parseInt(args[2]), 2304), 0);
                                    item.setAmount(amount);
                                }
                                catch (NumberFormatException e) {
                                    sender.sendMessage(ChatColor.RED + "The amount must be an integer!");
                                }
                            }

                            target.getInventory().addItem(item);
                            sender.sendMessage("§6Gave §c" + item.getAmount() + "§6 [§r" + item.getItemMeta().getDisplayName() + "§6] to §c" + target.getDisplayName() + "§6.");
                            return true;
                        }
                        catch (NullPointerException e) {
                            sender.sendMessage(ChatColor.RED + "You must specify a valid custom item!");
                            return true;
                        }

                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "You must specify a valid player!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Player getPlayer(String arg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (arg.equalsIgnoreCase(player.getDisplayName()) || arg.equalsIgnoreCase(player.getName()) || arg.equalsIgnoreCase(player.getUniqueId().toString())) {
                return player;
            }
        }
        return null;
    }
}
