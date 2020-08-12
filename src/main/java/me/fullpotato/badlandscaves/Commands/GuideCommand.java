package me.fullpotato.badlandscaves.Commands;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Info.GuideBook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;


public class GuideCommand extends Commands implements CommandExecutor {
    private final BadlandsCaves plugin;
    private final GuideBook guideBook;

    public GuideCommand(BadlandsCaves plugin) {
        this.plugin = plugin;
        guideBook = new GuideBook(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] strings) {
        if (command.getName().equals("guide")) {
            if (commandSender instanceof Player) {
                final Player player = (Player) commandSender;
                final PlayerInventory inventory = player.getInventory();

                //remove any existing guide books
                inventory.forEach(item -> {
                    if (item != null && guideBook.isGuideBook(item)) {
                        inventory.remove(item);
                    }
                });

                int slot = inventory.firstEmpty();
                if (slot != -1) {
                    inventory.addItem(guideBook.getGuideBook());
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "You must have an open space in your inventory!");
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            }
            return true;
        }
        return false;
    }
}
