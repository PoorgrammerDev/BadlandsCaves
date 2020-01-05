package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class player_save_to_config extends BukkitRunnable {

    private BadlandsCaves plugin;
    private Player player;

    public player_save_to_config (BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Deaths", player.getMetadata("Deaths").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Thirst", player.getMetadata("Thirst").get(0).asDouble());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Toxicity", player.getMetadata("Toxicity").get(0).asDouble());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_sys_var", player.getMetadata("thirst_sys_var").get(0).asDouble());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_nat_decr_var", player.getMetadata("tox_nat_decr_var").get(0).asDouble());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_slow_incr_var", player.getMetadata("tox_slow_incr_var").get(0).asDouble());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_slowmine_lvl", player.getMetadata("deaths_debuff_slowmine_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_slow_lvl", player.getMetadata("deaths_debuff_slow_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_hunger_lvl", player.getMetadata("deaths_debuff_hunger_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_poison_lvl", player.getMetadata("deaths_debuff_poison_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_slowmine_lvl", player.getMetadata("tox_debuff_slowmine_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_slow_lvl", player.getMetadata("tox_debuff_slow_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_hunger_lvl", player.getMetadata("tox_debuff_hunger_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_poison_lvl", player.getMetadata("tox_debuff_poison_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_slowmine_lvl", player.getMetadata("thirst_debuff_slowmine_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_slow_lvl", player.getMetadata("thirst_debuff_slow_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_hunger_lvl", player.getMetadata("thirst_debuff_hunger_lvl").get(0).asInt());
        plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_poison_lvl", player.getMetadata("thirst_debuff_poison_lvl").get(0).asInt());

        plugin.saveConfig();

        plugin.getServer().getConsoleSender().sendMessage("[BadlandsCaves] Saved to Config for Player " + player.getDisplayName() + " (" + player.getUniqueId() + ")");
    }
}
