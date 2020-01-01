package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.events.Deaths.change_data;
import me.fullpotato.badlandscaves.badlandscaves.events.NewPlayer;
import me.fullpotato.badlandscaves.badlandscaves.events.Thirst.decrease_thirst;
import me.fullpotato.badlandscaves.badlandscaves.events.Toxicity.incr_tox_in_water;
import org.bukkit.plugin.java.JavaPlugin;

public final class BadlandsCaves extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new NewPlayer(this), this);
        this.getServer().getPluginManager().registerEvents(new decrease_thirst(this), this);
        this.getServer().getPluginManager().registerEvents(new incr_tox_in_water(this), this);
        this.getServer().getPluginManager().registerEvents(new change_data(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
