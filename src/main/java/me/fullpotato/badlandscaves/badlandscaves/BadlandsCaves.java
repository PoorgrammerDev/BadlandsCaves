package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.DeathCommand;
import me.fullpotato.badlandscaves.badlandscaves.Commands.ThirstCommand;
import me.fullpotato.badlandscaves.badlandscaves.Commands.ToxicityCommand;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.*;
import me.fullpotato.badlandscaves.badlandscaves.events.Deaths.death_handler;
import me.fullpotato.badlandscaves.badlandscaves.events.Deaths.gapple_eat;
import me.fullpotato.badlandscaves.badlandscaves.events.NewPlayer;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.decrease_thirst;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.purification;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.toxic_water_bottling;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.water_drinking;
import me.fullpotato.badlandscaves.badlandscaves.events.Toxicity.incr_tox_in_water;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
        this.getServer().getPluginManager().registerEvents(new death_handler(this), this);
        this.getServer().getPluginManager().registerEvents(new gapple_eat(this), this);
        this.getServer().getPluginManager().registerEvents(new purification(this), this);
        this.getServer().getPluginManager().registerEvents(new toxic_water_bottling(this), this);
        this.getServer().getPluginManager().registerEvents(new water_drinking(this), this);
        this.getServer().getPluginManager().registerEvents(new toxic_water_bottling(this),this);

        //command reg
        this.getCommand("thirst").setExecutor(new ThirstCommand());
        this.getCommand("toxicity").setExecutor(new ToxicityCommand());
        this.getCommand("deaths").setExecutor(new DeathCommand());


        //runnable
        BukkitTask act_bar = new actionbar_runnable().runTaskTimerAsynchronously(this, 0 ,0);

        BukkitTask tox_eff = new tox_effects_runnable(this).runTaskTimer(this, 0, 0);
        BukkitTask thrst_eff = new thirst_effects_runnable(this).runTaskTimer(this, 0, 0);
        BukkitTask dth_eff = new death_effects_runnable(this).runTaskTimer(this, 0 ,0);
        BukkitTask tot_eff = new player_effects_runnable().runTaskTimer(this,0,0);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
