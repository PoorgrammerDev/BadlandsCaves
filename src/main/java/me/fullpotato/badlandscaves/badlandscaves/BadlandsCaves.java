package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.UseFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.UseIncompleteSoulCrystal;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.UseTaintPowder;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.StopCustomItemsInteract;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.GappleEat;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot.SoulDrop;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.GetFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot.ZombieDeathLoot;
import me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.CauldronMenu;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.Drinking;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.NaturalThirstDecrease;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.ToxicWaterBottling;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.IncreaseToxInRain;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.IncreaseToxInWater;
import me.fullpotato.badlandscaves.badlandscaves.Events.PlayerJoin;
import me.fullpotato.badlandscaves.badlandscaves.Events.PlayerLeave;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects.DeathEffectsRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects.PlayerEffectsRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects.ThirstEffectsRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects.ToxEffectsRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage.*;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.AgilitySpeedRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaRegen;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Toxicity.ToxSlowDecreaseRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.ActionbarRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.PlayerSaveToConfig;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.DescensionWorld;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.EmptyWorld;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.PreventNormalEnd;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.ReflectionWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BadlandsCaves extends JavaPlugin {
    private final String[] player_values = {
            "Deaths",
            "*Thirst",
            "#Toxicity",
            "#thirst_sys_var",
            "#tox_nat_decr_var",
            "#tox_slow_incr_var",
            "deaths_buff_speed_lvl",
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
            "opened_cauldron",
            "opened_cauldron_x",
            "opened_cauldron_y",
            "opened_cauldron_z",
            "has_supernatural_powers",
            "in_descension",
            "in_reflection",
            "descension_detect",
            "descension_detect_cooldown",
            "descension_timer",
            "descension_shrines_capped",
            "*Mana",
            "*max_mana",
            "mana_needed_timer",
            "mana_regen_delay_timer",
            "mana_bar_active_timer",
            "swap_slot",
            "swap_cooldown",
            "swap_name_timer",
            "displace_level",
            "displace_particle_id",
            "has_displace_marker",
            "displace_x",
            "displace_y",
            "displace_z",
            "withdraw_level",
            "withdraw_x",
            "withdraw_y",
            "withdraw_z",
            "withdraw_chunk_x",
            "withdraw_chunk_z",
            "withdraw_timer",
            "eyes_level",
            "using_eyes",
            "possess_level",
            "in_possession",
            "possessed_entity",
            "possess_orig_world",
            "possess_orig_x",
            "possess_orig_y",
            "possess_orig_z",
            "endurance_level",
            "agility_level",
            "agility_buff_speed_lvl",
            "agility_jump_id",
            "agility_jump_timer",
    };

    private final String[] custom_items = {
            "starter_sapling",
            "starter_bone_meal",
            "toxic_water",
            "purified_water",
            "antidote",
            "mana_potion",
            "purge_essence",
            "hell_essence",
            "magic_essence",
            "displace",
            "withdraw",
            "enhanced_eyes",
            "possess",
            "tiny_blaze_powder",
            "tainted_powder",
            "zombie_soul",
            "creeper_soul",
            "skeleton_soul",
            "spider_soul",
            "pigzombie_soul",
            "ghast_soul",
            "silverfish_soul",
            "witch_soul",
            "phantom_soul",
            "merged_souls",
            "soul_crystal_incomplete",
            "soul_crystal",
    };

    @Override
    public void onEnable() {
        //config
        loadConfig();

        //adding new items
        //LoadCustomItems.saveCustomItemsToConfig(this);

        //worlds
        {
            EmptyWorld empty_world = new EmptyWorld();
            empty_world.gen_void_world();

            DescensionWorld desc_world = new DescensionWorld(this);
            desc_world.gen_descension_world();

            ReflectionWorld refl_world = new ReflectionWorld();
            refl_world.gen_refl_world();
        }

        //event registering
        {
            this.getServer().getPluginManager().registerEvents(new PlayerJoin(this, player_values), this);
            this.getServer().getPluginManager().registerEvents(new NaturalThirstDecrease(this), this);
            this.getServer().getPluginManager().registerEvents(new IncreaseToxInWater(this), this);
            this.getServer().getPluginManager().registerEvents(new DeathHandler(this), this);
            this.getServer().getPluginManager().registerEvents(new GappleEat(this), this);
            this.getServer().getPluginManager().registerEvents(new CauldronMenu(this), this);
            this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this), this);
            this.getServer().getPluginManager().registerEvents(new Drinking(this), this);
            this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this),this);
            this.getServer().getPluginManager().registerEvents(new PlayerLeave(this, player_values), this);
            this.getServer().getPluginManager().registerEvents(new CombineTinyBlaze(this), this);
            this.getServer().getPluginManager().registerEvents(new PurgeEssence(this), this);
            this.getServer().getPluginManager().registerEvents(new StopCustomItemsInteract(this), this);
            this.getServer().getPluginManager().registerEvents(new UseTaintPowder(this), this);
            this.getServer().getPluginManager().registerEvents(new ZombieDeathLoot(this), this);
            this.getServer().getPluginManager().registerEvents(new GetFishingCrate(this), this);
            this.getServer().getPluginManager().registerEvents(new UseFishingCrate(this), this);
            this.getServer().getPluginManager().registerEvents(new SkeleBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new ZombieBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new CreeperBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new SpiderBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new PigZombieAngerBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new SilverfishBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new BlazeBuff(this), this);
            this.getServer().getPluginManager().registerEvents(new SwapPowers(this), this);
            this.getServer().getPluginManager().registerEvents(new Displace(this), this);
            this.getServer().getPluginManager().registerEvents(new StopPowersInvInteract(this), this);
            this.getServer().getPluginManager().registerEvents(new Withdraw(this), this);
            this.getServer().getPluginManager().registerEvents(new EnhancedEyes(this), this);
            this.getServer().getPluginManager().registerEvents(new Possession(this), this);
            this.getServer().getPluginManager().registerEvents(new IncreaseToxInRain(this), this);
            this.getServer().getPluginManager().registerEvents(new EnduranceCancelHunger(this), this);
            this.getServer().getPluginManager().registerEvents(new Agility(this), this);
            this.getServer().getPluginManager().registerEvents(new PreventNormalEnd(this), this);
            this.getServer().getPluginManager().registerEvents(new DescensionPlayerMove(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulDrop(this), this);
            this.getServer().getPluginManager().registerEvents(new HellEssence(this), this);
            this.getServer().getPluginManager().registerEvents(new MagicEssence(this), this);
            this.getServer().getPluginManager().registerEvents(new MergedSouls(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulCrystalIncomplete(this), this);
            this.getServer().getPluginManager().registerEvents(new UseIncompleteSoulCrystal(this), this);
        }

        //command reg
        {
            this.getCommand("thirst").setExecutor(new ThirstCommand(this));
            this.getCommand("thirst").setTabCompleter(new ValueCommandsTabComplete());

            this.getCommand("toxicity").setExecutor(new ToxicityCommand(this));
            this.getCommand("toxicity").setTabCompleter(new ValueCommandsTabComplete());

            this.getCommand("deaths").setExecutor(new DeathCommand(this));
            this.getCommand("deaths").setTabCompleter(new ValueCommandsTabComplete());

            this.getCommand("hardmode").setExecutor(new HardmodeCommand(this));
            this.getCommand("hardmode").setTabCompleter(new HM_TabComplete());

            this.getCommand("mana").setExecutor(new ManaCommand(this));
            this.getCommand("mana").setTabCompleter(new ValueCommandsTabComplete());

            this.getCommand("powers").setExecutor(new PowersCommand(this));
            this.getCommand("powers").setTabCompleter(new PowersTabComplete());

            this.getCommand("customitem").setExecutor(new CustomItemCommand(this));
            this.getCommand("customitem").setTabCompleter(new CustomItemTabComplete(custom_items));
        }

        //runnables
        {
            new ActionbarRunnable().runTaskTimer(this, 0 ,0);
            new ToxEffectsRunnable(this).runTaskTimer(this, 0, 0);
            new ThirstEffectsRunnable(this).runTaskTimer(this, 0, 0);
            new DeathEffectsRunnable(this).runTaskTimer(this, 0, 0);
            new PlayerEffectsRunnable().runTaskTimer(this,0,0);
            new ToxSlowDecreaseRunnable(this).runTaskTimer(this, 0, 600);
            new PlayerSaveToConfig(this, null, player_values, true).runTaskTimer(this, 5, 3600);
            new ManaBarRunnable(this).runTaskTimer(this, 0, 5);
            new ManaRegen(this).runTaskTimer(this, 0, 10);
            new AgilitySpeedRunnable(this).runTaskTimer(this, 0, 15);
            new StageEnter(this).runTaskTimer(this, 0, 20);
            new DescensionReset(this).runTaskTimer(this, 0, 60);
            new LostSoulParticle().runTaskTimer(this, 0, 3);
            new DetectedBar(this).runTaskTimer(this, 0, 3);
            new ShrineCapture(this).runTaskTimer(this, 0 ,0);
            new DescensionTimeLimit(this).runTaskTimer(this, 0, 20);
            new DetectionDecrease(this).runTaskTimer(this, 0, 20);
            new ExitPortal().runTaskTimer(this, 0, 3);
            new ReflectionZombieTrack(this).runTaskTimer(this, 0, 0);

        }

        //crafting recipes
        {
            TinyBlazePowderCrafting tiny_blz = new TinyBlazePowderCrafting(this);
            tiny_blz.tiny_blaze_powder_craft();
            tiny_blz.back_to_large();

            PurgeEssenceCrafting prg_ess = new PurgeEssenceCrafting(this);
            prg_ess.purge_essence_craft();

            NotchAppleCrafting e_gap = new NotchAppleCrafting(this);
            e_gap.crafting_notch_apple();

            ReedsCrafting reeds = new ReedsCrafting(this);
            reeds.craft_reeds();

            SandCrafting sand = new SandCrafting(this);
            sand.craft_sand();

            HellEssenceCrafting hell_essence = new HellEssenceCrafting(this);
            hell_essence.craft_hell_essence();

            MagicEssenceCrafting magic_essence = new MagicEssenceCrafting(this);
            magic_essence.magic_essence_craft();

            MergedSoulsCrafting merged_souls = new MergedSoulsCrafting(this);
            merged_souls.merge_souls();

            SoulCrystalIncompleteCrafting soul_crystal = new SoulCrystalIncompleteCrafting(this);
            soul_crystal.soul_crystal_incomplete();
        }
    }

    @Override
    public void onDisable() {
        //just a copy of the playersavetoconfig runnable (idk why, but if i just call the runnable, it spits out an error)
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String meta : player_values) {
                String filtered;
                String dot_meta;
                if (meta.contains("!")) {
                    continue;
                }
                else if (meta.contains("#") || meta.contains("*")) {
                    filtered = meta.substring(1);
                }
                else {
                    filtered = meta;
                }
                dot_meta = "." + filtered;
                this.getConfig().set("Scores.users." + player.getUniqueId() + dot_meta, player.getMetadata(filtered).get(0).asDouble());
                this.saveConfig();
            }
        }
        this.saveConfig();
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

}
