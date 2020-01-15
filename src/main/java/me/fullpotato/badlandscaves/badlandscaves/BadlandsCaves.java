package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.combineTinyBlaze;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.purgeEssence;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.useFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.useTaintPowder;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.stopCustomItemsRClick;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.deathHandler;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.gappleEat;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.getFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.zombieDeathLoot;
import me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Displace;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Withdraw;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.noInteract;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.swapPowers;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.cauldronMenu;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.drinking;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.naturalThirstDecrease;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.toxicWaterBottling;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.increaseToxInWater;
import me.fullpotato.badlandscaves.badlandscaves.Events.playerJoin;
import me.fullpotato.badlandscaves.badlandscaves.Events.playerLeave;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class BadlandsCaves extends JavaPlugin {

    World world;
    World default_world;

    private final String[] player_values = {
            "Deaths",
            "*Thirst",
            "#Toxicity",
            "#thirst_sys_var",
            "#tox_nat_decr_var",
            "#tox_slow_incr_var",
            "deaths_debuff_slowmine_lvl",
            "deaths_debuff_slow_lvl",
            "deaths_debuff_hunger_lvl",
            "deaths_debuff_poison_lvl",
            "tox_debuff_slowmine_lvl",
            "tox_debuff_slow_lvl",
            "tox_debuff_hunger_lvl",
            "tox_debuff_poison_lvl",
            "thirst_debuff_slowmine_lvl",
            "thirst_debuff_slow_lvl",
            "thirst_debuff_hunger_lvl",
            "thirst_debuff_poison_lvl",
            "has_supernatural_powers",
            "is_cursed_soul",
            "swap_cooldown",
            "displace_level",
            "displace_particle_id",
            "has_displace_marker",
            "displace_x",
            "displace_y",
            "displace_z"
    };

    @Override
    public void onEnable() {
        default_world = Bukkit.getWorld("world");

        world_gen();

        loadConfig();

        //event registering
        {
            this.getServer().getPluginManager().registerEvents(new playerJoin(this, world, player_values), this);
            this.getServer().getPluginManager().registerEvents(new naturalThirstDecrease(this), this);
            this.getServer().getPluginManager().registerEvents(new increaseToxInWater(this), this);
            this.getServer().getPluginManager().registerEvents(new deathHandler(this), this);
            this.getServer().getPluginManager().registerEvents(new gappleEat(this), this);
            this.getServer().getPluginManager().registerEvents(new cauldronMenu(this), this);
            this.getServer().getPluginManager().registerEvents(new toxicWaterBottling(this), this);
            this.getServer().getPluginManager().registerEvents(new drinking(this), this);
            this.getServer().getPluginManager().registerEvents(new toxicWaterBottling(this),this);
            this.getServer().getPluginManager().registerEvents(new playerLeave(this, player_values), this);
            this.getServer().getPluginManager().registerEvents(new combineTinyBlaze(this), this);
            this.getServer().getPluginManager().registerEvents(new purgeEssence(this), this);
            this.getServer().getPluginManager().registerEvents(new stopCustomItemsRClick(this), this);
            this.getServer().getPluginManager().registerEvents(new useTaintPowder(this), this);
            this.getServer().getPluginManager().registerEvents(new zombieDeathLoot(this), this);
            this.getServer().getPluginManager().registerEvents(new getFishingCrate(this), this);
            this.getServer().getPluginManager().registerEvents(new useFishingCrate(this), this);
            this.getServer().getPluginManager().registerEvents(new skeleBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new zombieBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new creeperBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new spiderBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new pigZombieAngerBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new silverfishBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new blazeBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new swapPowers(this), this);
            this.getServer().getPluginManager().registerEvents(new Displace(this), this);
            this.getServer().getPluginManager().registerEvents(new noInteract(this), this);
            this.getServer().getPluginManager().registerEvents(new Withdraw(this), this);
        }


        //command reg
        this.getCommand("thirst").setExecutor(new ThirstCommand());
        this.getCommand("thirst").setTabCompleter(new DTT_TabComplete());

        this.getCommand("toxicity").setExecutor(new ToxicityCommand());
        this.getCommand("toxicity").setTabCompleter(new DTT_TabComplete());

        this.getCommand("deaths").setExecutor(new DeathCommand());
        this.getCommand("deaths").setTabCompleter(new DTT_TabComplete());

        this.getCommand("hardmode").setExecutor(new HardmodeCommand(this));
        this.getCommand("hardmode").setTabCompleter(new HM_TabComplete());

        //runnables
        BukkitTask act_bar = new actionbarRunnable().runTaskTimerAsynchronously(this, 0 ,0);

        BukkitTask tox_eff = new toxEffectsRunnable(this).runTaskTimer(this, 0, 0);
        BukkitTask thrst_eff = new thirstEffectsRunnable(this).runTaskTimer(this, 0, 0);
        BukkitTask dth_eff = new deathEffectsRunnable(this).runTaskTimer(this, 0 ,0);
        BukkitTask tot_eff = new playerEffectsRunnable().runTaskTimer(this,0,0);

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                BukkitTask decr_tox = new toxSlowDecreaseRunnable(this, player).runTaskTimerAsynchronously(this, 0, 600);
                BukkitTask save_config = new playerSaveToConfig(this, player, player_values, true).runTaskTimerAsynchronously(this, 5, 3600);
            }
        }
        catch (NoClassDefFoundError ignored) {
        }

        //crafting recipes
        tinyBlazePowder tiny_blz = new tinyBlazePowder(this);
        tiny_blz.tiny_blaze_powder_craft();
        tiny_blz.back_to_large();

        purgeEssenceRecipe prg_ess = new purgeEssenceRecipe(this);
        prg_ess.purge_essence_craft();

        notchAppleCrafting e_gap = new notchAppleCrafting(this);
        e_gap.crafting_notch_apple();

        reedsCrafting reeds = new reedsCrafting(this);
        reeds.craft_reeds();

        sandCrafting sand = new sandCrafting(this);
        sand.craft_sand();
    }

    @Override
    public void onDisable() {

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                new playerSaveToConfig(this, player, player_values, false).runTaskAsynchronously(this);
            }
        }
        catch (NoClassDefFoundError ignored) {
        }

        this.saveConfig();
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }


    public void world_gen () {
        //badlandscaves world
        /*
        WorldCreator worldCreator = new WorldCreator("world_badlandscaves");
        worldCreator.generator(new chunkGenerator());

        world = worldCreator.createWorld();
         */

        WorldCreator emptyworld = new WorldCreator("world_empty");
        emptyworld.environment(World.Environment.THE_END);
        emptyworld.type(WorldType.FLAT);
        //emptyworld.generatorSettings("minecraft:air;minecraft:the_void;");

        world = emptyworld.createWorld();
    }

}
