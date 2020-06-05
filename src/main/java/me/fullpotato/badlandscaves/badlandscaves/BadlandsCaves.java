package me.fullpotato.badlandscaves.badlandscaves;

import me.fullpotato.badlandscaves.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.badlandscaves.Commands.TabCompleters.*;
import me.fullpotato.badlandscaves.badlandscaves.CustomItemRecipes.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.Blocks.TitaniumOre;
import me.fullpotato.badlandscaves.badlandscaves.Events.Info.CraftingGuide;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.StopCustomItemsInteract;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.badlandscaves.Events.Deaths.BlessedAppleEat;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.DestroySpawner;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.GetFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot.SoulDrop;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot.ZombieDeathLoot;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.TreasureGear;
import me.fullpotato.badlandscaves.badlandscaves.Events.Loot.UseFishingCrate;
import me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.PlayerJoinLeave;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection.*;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.CauldronMenu;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.Drinking;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.NaturalThirstDecrease;
import me.fullpotato.badlandscaves.badlandscaves.Events.Thirst.ToxicWaterBottling;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.IncreaseToxInRain;
import me.fullpotato.badlandscaves.badlandscaves.Events.Toxicity.IncreaseToxInWater;
import me.fullpotato.badlandscaves.badlandscaves.Events.VillagerTrades;
import me.fullpotato.badlandscaves.badlandscaves.Events.WitherBossFight;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.ActionbarRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.AugmentedSpider;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.AugmentedZombie;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects.PlayerEffectsRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.AgilitySpeedRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage.*;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaRegen;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Surface;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.Toxicity.ToxSlowDecreaseRunnable;
import me.fullpotato.badlandscaves.badlandscaves.Util.LoadCustomItems;
import me.fullpotato.badlandscaves.badlandscaves.Util.PlayerConfigLoadSave;
import me.fullpotato.badlandscaves.badlandscaves.Util.ServerProperties;
import me.fullpotato.badlandscaves.badlandscaves.WorldGeneration.*;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class BadlandsCaves extends JavaPlugin {
    public String mainWorldName;
    public String descensionWorldName;
    public String withdrawWorldName;
    public String reflectionWorldName;
    public String backroomsWorldName;
    public String chambersWorldName;

    /**
     * PLAYER VALUE CONSTANTS
     * Prefixes:
     * $ - boolean type
     * % - default value 100
     */
    private final String[] player_values = {
            "Deaths",
            "%Thirst",
            "Toxicity",
            "thirst_sys_var",
            "tox_nat_decr_var",
            "tox_slow_incr_var",
            "$opened_cauldron",
            "$has_supernatural_powers",
            "in_descension",
            "$in_reflection",
            "reflection_zombie",
            "$refl_respawn_inv",
            "descension_detect",
            "descension_detect_cooldown",
            "descension_timer",
            "descension_shrines_capped",
            "%Mana",
            "%max_mana",
            "mana_regen_delay_timer",
            "mana_bar_active_timer",
            "mana_bar_message_timer",
            "swap_slot",
            "swap_doubleshift_window",
            "swap_window",
            "swap_cooldown",
            "swap_name_timer",
            "$spell_cooldown",
            "displace_level",
            "$has_displace_marker",
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
            "$using_eyes",
            "possess_level",
            "$in_possession",
            "possessed_entity",
            "possess_orig_world",
            "possess_orig_x",
            "possess_orig_y",
            "possess_orig_z",
            "endurance_level",
            "agility_level",
            "agility_buff_speed_lvl",
            "$has_seen_backrooms",
            "backrooms_timer",
            "$bleeding_debuff",

    };

    //all custom items' names
    private final String[] custom_items = {
            "starter_sapling",
            "starter_bone_meal",
            "toxic_water",
            "purified_water",
            "fishing_crate",
            "fishing_crate_hardmode",
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
            "rune",
            "charged_rune",
            "voltshock_battery",
            "voltshock_shocker",
            "voltshock_arrow",
            "corrosive_substance",
            "corrosive_arrow",
            "chamber_magma_key",
            "chamber_glowstone_key",
            "chamber_soulsand_key",
            "blessed_apple",
            "enchanted_blessed_apple",
            "stone_shield",
            "iron_shield",
            "diamond_shield",
            "recall_potion",
            "titanium_fragment",
            "titanium_ingot",
            "binding",
            "golden_cable",
            "nether_star_fragment",
            "starlight_circuit",
            "starlight_battery",
            "starlight_module",
    };

    @Override
    public void onEnable() {
        loadWorldNames();

        loadConfig();

        //load players metadata
        PlayerConfigLoadSave loader = new PlayerConfigLoadSave(this, player_values);
        loader.loadPlayers();

        //adding custom items
        LoadCustomItems.saveCustomItemsToConfig(this);

        loadCustomWorlds();
        registerEvents();
        loadCommands();
        loadRunnables();
        loadCraftingRecipes();
    }

    @Override
    public void onDisable() {
        PlayerConfigLoadSave save_player = new PlayerConfigLoadSave(this, player_values);
        save_player.saveToConfig(true);
        this.saveConfig();

        this.getServer().getScheduler().cancelTasks(this);
    }

    //CONFIG
    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    //WORLDS
    public void loadCustomWorlds() {
        EmptyWorld empty_world = new EmptyWorld(this);
        empty_world.gen_void_world();

        DescensionWorld desc_world = new DescensionWorld(this);
        desc_world.gen_descension_world();

        ReflectionWorld refl_world = new ReflectionWorld(this);
        refl_world.gen_refl_world();

        Backrooms backrooms = new Backrooms(this);
        backrooms.gen_backrooms();

        HallowedChambersWorld chambers = new HallowedChambersWorld(this);
        chambers.gen_world();

        this.getServer().getWorld(mainWorldName).setGameRule(GameRule.REDUCED_DEBUG_INFO, false);

        StartingDungeons dungeons = new StartingDungeons(this);
        dungeons.genSpawnDungeons();
    }

    //EVENTS
    public void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeave(this, player_values), this);
        this.getServer().getPluginManager().registerEvents(new NaturalThirstDecrease(this), this);
        this.getServer().getPluginManager().registerEvents(new IncreaseToxInWater(this), this);
        this.getServer().getPluginManager().registerEvents(new DeathHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new BlessedAppleEat(this), this);
        this.getServer().getPluginManager().registerEvents(new CauldronMenu(this), this);
        this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this), this);
        this.getServer().getPluginManager().registerEvents(new Drinking(this), this);
        this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this),this);
        this.getServer().getPluginManager().registerEvents(new CombineTinyBlaze(this), this);
        this.getServer().getPluginManager().registerEvents(new PurgeEssence(this), this);
        this.getServer().getPluginManager().registerEvents(new StopCustomItemsInteract(this, custom_items), this);
        this.getServer().getPluginManager().registerEvents(new UseTaintPowder(this), this);
        this.getServer().getPluginManager().registerEvents(new ZombieDeathLoot(this), this);
        this.getServer().getPluginManager().registerEvents(new GetFishingCrate(this), this);
        this.getServer().getPluginManager().registerEvents(new UseFishingCrate(this), this);
        this.getServer().getPluginManager().registerEvents(new SkeleBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new ZombieBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new CreeperBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new SpiderBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new PigZombieBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new SilverfishBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new BlazeBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new GhastBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new PhantomBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new WitchBuff(this), this);
        this.getServer().getPluginManager().registerEvents(new SwapPowers(this), this);
        this.getServer().getPluginManager().registerEvents(new Displace(this), this);
        this.getServer().getPluginManager().registerEvents(new StopPowersInvInteract(this), this);
        this.getServer().getPluginManager().registerEvents(new Withdraw(this), this);
        this.getServer().getPluginManager().registerEvents(new EnhancedEyes(this), this);
        this.getServer().getPluginManager().registerEvents(new Possession(this), this);
        this.getServer().getPluginManager().registerEvents(new IncreaseToxInRain(this), this);
        this.getServer().getPluginManager().registerEvents(new EnduranceCancelHunger(this), this);
        this.getServer().getPluginManager().registerEvents(new Agility(this), this);
        this.getServer().getPluginManager().registerEvents(new DescensionPlayerMove(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulDrop(this), this);
        this.getServer().getPluginManager().registerEvents(new HellEssence(this), this);
        this.getServer().getPluginManager().registerEvents(new MagicEssence(this), this);
        this.getServer().getPluginManager().registerEvents(new MergedSouls(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulCrystalIncomplete(this), this);
        this.getServer().getPluginManager().registerEvents(new UseIncompleteSoulCrystal(this), this);
        this.getServer().getPluginManager().registerEvents(new ReflectionBuild(this), this);
        this.getServer().getPluginManager().registerEvents(new ReflectionZombie(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerUnderSht(this), this);
        this.getServer().getPluginManager().registerEvents(new EndGame(this), this);
        this.getServer().getPluginManager().registerEvents(new LimitActions(this), this);
        this.getServer().getPluginManager().registerEvents(new UseCompleteSoulCrystal(this), this);
        this.getServer().getPluginManager().registerEvents(new MushroomStew(this), this);
        this.getServer().getPluginManager().registerEvents(new DestroySpawner(this), this);
        this.getServer().getPluginManager().registerEvents(new UseRune(this), this);
        this.getServer().getPluginManager().registerEvents(new UseChargedRune(this), this);
        this.getServer().getPluginManager().registerEvents(new BackroomsManager(this), this);
        this.getServer().getPluginManager().registerEvents(new TreasureGear(), this);
        this.getServer().getPluginManager().registerEvents(new EXPBottle(), this);
        this.getServer().getPluginManager().registerEvents(new SerratedSwords(this), this);
        this.getServer().getPluginManager().registerEvents(new UseSerrated(this), this);
        this.getServer().getPluginManager().registerEvents(new Voltshock(this), this);
        this.getServer().getPluginManager().registerEvents(new UseVoltshock(this), this);
        this.getServer().getPluginManager().registerEvents(new Corrosive(this), this);
        this.getServer().getPluginManager().registerEvents(new UseCorrosive(this), this);
        this.getServer().getPluginManager().registerEvents(new CustomBows(this), this);
        this.getServer().getPluginManager().registerEvents(new WitherBossFight(this), this);
        this.getServer().getPluginManager().registerEvents(new BlessedApple(this), this);
        this.getServer().getPluginManager().registerEvents(new VillagerTrades(this), this);
        this.getServer().getPluginManager().registerEvents(new ShieldBlocking(this), this);
        this.getServer().getPluginManager().registerEvents(new Shield(this), this);
        this.getServer().getPluginManager().registerEvents(new CraftingGuide(this), this);
        this.getServer().getPluginManager().registerEvents(new TitaniumOre(this), this);
        this.getServer().getPluginManager().registerEvents(new TitaniumBar(this), this);
        this.getServer().getPluginManager().registerEvents(new StarlightComponents(this), this);
    }

    //COMMANDS
    public void loadCommands() {
        this.getCommand("thirst").setExecutor(new ThirstCommand(this));
        this.getCommand("thirst").setTabCompleter(new ValueCommandsTabComplete(this));

        this.getCommand("toxicity").setExecutor(new ToxicityCommand(this));
        this.getCommand("toxicity").setTabCompleter(new ValueCommandsTabComplete(this));

        this.getCommand("deaths").setExecutor(new DeathCommand(this));
        this.getCommand("deaths").setTabCompleter(new ValueCommandsTabComplete(this));

        this.getCommand("hardmode").setExecutor(new HardmodeCommand(this));
        this.getCommand("hardmode").setTabCompleter(new HM_TabComplete());

        this.getCommand("mana").setExecutor(new ManaCommand(this));
        this.getCommand("mana").setTabCompleter(new ValueCommandsTabComplete(this));

        this.getCommand("powers").setExecutor(new PowersCommand(this));
        this.getCommand("powers").setTabCompleter(new PowersTabComplete(this));

        this.getCommand("customitem").setExecutor(new CustomItemCommand(this));
        this.getCommand("customitem").setTabCompleter(new CustomItemTabComplete(this, custom_items));

        this.getCommand("chaos").setExecutor(new ChaosCommand(this));
        this.getCommand("chaos").setTabCompleter(new ChaosCommandTabComplete());

        this.getCommand("guide").setExecutor(new GuideCommand(this));


        this.getCommand("craftguide").setExecutor(new CraftingGuideCommand(this));
    }

    //RUNNABLES
    public void loadRunnables() {
        new ActionbarRunnable(this).runTaskTimer(this, 0 ,0);
        new PlayerEffectsRunnable(this).runTaskTimer(this,0,0);
        new ToxSlowDecreaseRunnable(this).runTaskTimer(this, 0, 600);
        new PlayerConfigLoadSave(this, player_values).runTaskTimer(this, 5, 3600);
        new ManaBarManager(this).runTaskTimer(this, 0, 5);
        new ManaRegen(this).runTaskTimer(this, 0, 20);
        new AgilitySpeedRunnable(this).runTaskTimer(this, 0, 15);
        new DescensionReset(this).runTaskTimer(this, 0, 60);
        new LostSoulParticle(this).runTaskTimer(this, 0, 3);
        new DetectedBar(this).runTaskTimer(this, 0, 3);
        new ShrineCapture(this).runTaskTimer(this, 0 ,0);
        new DescensionTimeLimit(this).runTaskTimer(this, 0, 20);
        new DetectionDecrease(this).runTaskTimer(this, 0, 20);
        new ExitPortal(this).runTaskTimer(this, 0, 3);
        new ZombieBossBehavior(this).runTaskTimer(this, 0, 0);
        new LimitActions(this).runTaskTimer(this, 0, 20);
        new ForceFixDescensionValues(this).runTaskTimer(this, 0, 100);
        new AugmentedSpider(this).runTaskTimer(this, 0, 5);
        new AugmentedZombie(this).runTaskTimer(this, 0, 10);
        new Surface(this).runTaskTimer(this, 0, 100);

        WitherBossFight witherFight = new WitherBossFight(this);
        witherFight.checkIfEnded();
        witherFight.portalDestroyTimer();

    }

    //RECIPES
    public void loadCraftingRecipes() {
        TinyBlazePowderCrafting tiny_blz = new TinyBlazePowderCrafting(this);
        tiny_blz.tiny_blaze_powder_craft();
        tiny_blz.back_to_large();

        PurgeEssenceCrafting prg_ess = new PurgeEssenceCrafting(this);
        prg_ess.purge_essence_craft();

        AppleCrafting apple = new AppleCrafting(this);
        apple.craftNotchApple();
        apple.craftBlessedApple();
        apple.craftEnchantedBlessedApple();

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

        VoltshockCrafting voltshock = new VoltshockCrafting(this);
        voltshock.craft_battery();
        voltshock.craft_shocker();
        voltshock.modify_sword();
        voltshock.charge_sword();
        voltshock.craft_arrow();

        CorrosiveCrafting corrosive = new CorrosiveCrafting(this);
        corrosive.craftCorrosiveSubstance();
        corrosive.craftCorrosiveSword();
        corrosive.craftCorrosiveArrow();

        QuartzConvertCrafting quartzCraft = new QuartzConvertCrafting(this);
        quartzCraft.craft();

        ShieldCrafting shield = new ShieldCrafting(this);
        shield.craftRegularShield();
        shield.craftStoneShield();
        shield.craftIronShield();
        shield.craftDiamondShield();

        TitaniumBarCrafting titaniumBar = new TitaniumBarCrafting(this);
        titaniumBar.fragmentIntoBar();

        StarlightComponentsCrafting starlightComponents = new StarlightComponentsCrafting(this);
        starlightComponents.craftBinding();
        starlightComponents.craftGoldCable();
        starlightComponents.craftNetherStarFragment();
        starlightComponents.craftStarlightCircuit();
        starlightComponents.craftStarlightBattery();
        starlightComponents.craftStarlightModule();
    }

    public void loadWorldNames() {
        try {
            File properties = new File("server.properties");
            this.mainWorldName = ServerProperties.getSetting(properties, "level-name");
        }
        catch (IOException e) {
            this.getLogger().warning("[BadlandsCaves] No default world found, defaulting to \"world\"");
            this.mainWorldName = "world";
        }
        this.descensionWorldName = this.mainWorldName + "_descension";
        this.withdrawWorldName = this.mainWorldName + "_empty";
        this.reflectionWorldName = this.mainWorldName + "_reflection";
        this.backroomsWorldName = this.mainWorldName + "_backrooms";
        this.chambersWorldName = this.mainWorldName + "_chambers";
    }
}
