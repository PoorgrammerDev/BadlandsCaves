package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomItemCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;
    public CustomItemCommand (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("customitem")) {
            if (sender.isOp()) {
                if (args.length < 1) {
                    tooFewArgs(sender);
                    return true;
                }
                else {
                    Player target = getPlayer(args[0]);
                    if (target != null) {
                        if (args.length > 1) {
                            try {
                                ItemStack item = CustomItem.valueOf(args[1].toUpperCase()).getItem();
                                int amount = 1;
                                if (args.length >= 3) {
                                    try {
                                        amount = Math.max(Math.min(Integer.parseInt(args[2]), 2304), 0);
                                    }
                                    catch (NumberFormatException e) {
                                        sender.sendMessage(ChatColor.RED + "The amount must be an integer!");
                                    }
                                }

                                item.setAmount(amount);
                                target.getInventory().addItem(item);
                                sender.sendMessage("§6Gave §c" + amount + "§6 [§r" + item.getItemMeta().getDisplayName() + "§6] to §c" + target.getDisplayName() + "§6.");
                                return true;
                            }
                            catch (IllegalArgumentException e) {
                                sender.sendMessage(ChatColor.RED + "You must specify a valid custom item.");
                                return true;
                            }
                        }
                        else {
                         tooFewArgs(sender);
                         return true;
                        }
                    }
                    else {
                        playerNotValid(sender);
                        return true;
                    }
                }
            }
            else {
                notOp(sender);
                return true;
            }
        }
        return false;
    }

    public Player getPlayer(String arg) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (arg.equalsIgnoreCase(player.getDisplayName()) || arg.equalsIgnoreCase(player.getName()) || arg.equalsIgnoreCase(player.getUniqueId().toString())) {
                return player;
            }
        }
        return null;
    }
}
