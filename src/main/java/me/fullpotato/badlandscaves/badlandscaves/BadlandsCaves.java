package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.CommandClass;
import me.fullpotato.badlandscaves.badlandscaves.events.Deaths.change_data;
import me.fullpotato.badlandscaves.badlandscaves.events.Deaths.gapple_eat;
import me.fullpotato.badlandscaves.badlandscaves.events.NewPlayer;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.decrease_thirst;
import me.fullpotato.badlandscaves.badlandscaves.events.Toxicity.incr_tox_in_water;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class BadlandsCaves extends JavaPlugin {

    @Override
    public void onEnable() {
        //config
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        //event registering
        this.getServer().getPluginManager().registerEvents(new NewPlayer(this), this);
        this.getServer().getPluginManager().registerEvents(new decrease_thirst(this), this);
        this.getServer().getPluginManager().registerEvents(new incr_tox_in_water(this), this);
        this.getServer().getPluginManager().registerEvents(new change_data(this), this);
        this.getServer().getPluginManager().registerEvents(new gapple_eat(this), this);

        //command reg
        this.getCommand("thirst").setExecutor(new CommandClass());

        //runnable
        actionbar_runnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void actionbar_runnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int death_count = player.getMetadata("Deaths").get(0).asInt();
                    int thirst_count = player.getMetadata("Thirst").get(0).asInt();
                    int tox_count = player.getMetadata("Toxicity").get(0).asInt();

                    String separator = ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + " | ";
                    String actionbarmsg = (
                            ChatColor.GOLD + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.RED + ChatColor.BOLD.toString() + death_count + separator
                            + ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count + ChatColor.BOLD.toString() + "%" + separator
                            + ChatColor.DARK_GREEN + ChatColor.BOLD.toString() +"Toxicity: " + ChatColor.GREEN + ChatColor.BOLD.toString() + tox_count + ChatColor.BOLD.toString() + "%");

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 5);
    }
}
