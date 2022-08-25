package me.fullpotato.badlandscaves.Util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.*;

import me.fullpotato.badlandscaves.BadlandsCaves;

public class NameTagHide {

    //Singleton reference
    private static NameTagHide instance;
    public static NameTagHide getInstance() {
        return instance;
    }

    private BadlandsCaves plugin;
    private Team team;
    private String teamName;

    public NameTagHide(BadlandsCaves plugin) {
        this.plugin = plugin;

        final Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        teamName = plugin.getOptionsConfig().getString("nametag_hide_team_name");
        
        //Get team if exists, create if it doesn't
        this.team = scoreboard.getTeam(teamName);
        if (this.team == null) {
            this.team = scoreboard.registerNewTeam(teamName);
        }

        //Set nametag rule and singleton instance
        this.team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        instance = this;
    }

    public void Hide(LivingEntity entity) {
        //TODO: For some reason this isn't working; therefore the workaround is being used instead
        // this.team.addEntry(entity.getUniqueId().toString());

        // String command = "team join " + this.teamName + " " + entity.getUniqueId().toString();
        // this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);

    }
    
}
