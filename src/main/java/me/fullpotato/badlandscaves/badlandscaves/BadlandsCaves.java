package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.badlandscaves.CustomItems.essence_of_purging;
import me.fullpotato.badlandscaves.badlandscaves.CustomItems.tiny_blaze_powder;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.purge_ess;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.stop_custom_items_rclick;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.taint_powder_use;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.tiny_blaze_into_large;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.death_handler;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.gapple_eat;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.decrease_thirst;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.cauldron;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.toxic_water_bottling;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.water_drinking;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.incr_tox_in_water;
import me.fullpotato.badlandscaves.badlandscaves.Events.player_join;
import me.fullpotato.badlandscaves.badlandscaves.Events.player_leave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class BadlandsCaves extends JavaPlugin {

    @Override
    public void onEnable() {
        //config
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        //event registering
        this.getServer().getPluginManager().registerEvents(new player_join(this), this);
        this.getServer().getPluginManager().registerEvents(new decrease_thirst(this), this);
        this.getServer().getPluginManager().registerEvents(new incr_tox_in_water(this), this);
        this.getServer().getPluginManager().registerEvents(new death_handler(this), this);
        this.getServer().getPluginManager().registerEvents(new gapple_eat(this), this);
        this.getServer().getPluginManager().registerEvents(new cauldron(this), this);
        this.getServer().getPluginManager().registerEvents(new toxic_water_bottling(this), this);
        this.getServer().getPluginManager().registerEvents(new water_drinking(this), this);
        this.getServer().getPluginManager().registerEvents(new toxic_water_bottling(this),this);
        this.getServer().getPluginManager().registerEvents(new player_leave(this), this);
        this.getServer().getPluginManager().registerEvents(new tiny_blaze_into_large(this), this);
        this.getServer().getPluginManager().registerEvents(new purge_ess(this), this);
        this.getServer().getPluginManager().registerEvents(new stop_custom_items_rclick(this), this);
        this.getServer().getPluginManager().registerEvents(new taint_powder_use(this), this);

        //command reg
        this.getCommand("thirst").setExecutor(new ThirstCommand());
        this.getCommand("toxicity").setExecutor(new ToxicityCommand());
        this.getCommand("deaths").setExecutor(new DeathCommand());


        //runnables
        BukkitTask act_bar = new actionbar_runnable().runTaskTimerAsynchronously(this, 0 ,0);

        BukkitTask tox_eff = new tox_effects_runnable(this).runTaskTimer(this, 0, 0);
        BukkitTask thrst_eff = new thirst_effects_runnable(this).runTaskTimer(this, 0, 0);
        BukkitTask dth_eff = new death_effects_runnable(this).runTaskTimer(this, 0 ,0);
        BukkitTask tot_eff = new player_effects_runnable().runTaskTimer(this,0,0);

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                BukkitTask decr_tox = new tox_decr_slowly(this, player).runTaskTimerAsynchronously(this, 0, 600);
            }
        }
        catch (NoClassDefFoundError ignored) {
        }


        //config
        loadConfig();


        //crafting recipes
        tiny_blaze_powder tiny_blz = new tiny_blaze_powder(this);
        tiny_blz.tiny_blaze_powder_craft();
        tiny_blz.back_to_large();

        essence_of_purging prg_ess = new essence_of_purging(this);
        prg_ess.purge_essence_craft();

    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            new player_save_to_config(this, player).run();
        }

        this.saveConfig();
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}
