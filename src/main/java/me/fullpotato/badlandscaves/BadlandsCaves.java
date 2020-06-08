package me.fullpotato.badlandscaves;

import me.fullpotato.badlandscaves.Blocks.TitaniumOre;
import me.fullpotato.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.Commands.TabCompleters.*;
import me.fullpotato.badlandscaves.CustomItems.Crafting.*;
import me.fullpotato.badlandscaves.CustomItems.StopCustomItemsInteract;
import me.fullpotato.badlandscaves.CustomItems.Using.*;
import me.fullpotato.badlandscaves.Deaths.BlessedAppleEat;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.Effects.PlayerEffectsRunnable;
import me.fullpotato.badlandscaves.Info.CraftingGuide;
import me.fullpotato.badlandscaves.Loot.DestroySpawner;
import me.fullpotato.badlandscaves.Loot.GetFishingCrate;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.SoulDrop;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.ZombieDeathLoot;
import me.fullpotato.badlandscaves.Loot.TreasureGear;
import me.fullpotato.badlandscaves.Loot.UseFishingCrate;
import me.fullpotato.badlandscaves.MobBuffs.*;
import me.fullpotato.badlandscaves.Other.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionPlayerMove;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.AgilitySpeedRunnable;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaRegen;
import me.fullpotato.badlandscaves.Thirst.CauldronMenu;
import me.fullpotato.badlandscaves.Thirst.Drinking;
import me.fullpotato.badlandscaves.Thirst.NaturalThirstDecrease;
import me.fullpotato.badlandscaves.Thirst.ToxicWaterBottling;
import me.fullpotato.badlandscaves.Toxicity.IncreaseToxInRain;
import me.fullpotato.badlandscaves.Toxicity.IncreaseToxInWater;
import me.fullpotato.badlandscaves.Toxicity.ToxSlowDecreaseRunnable;
import me.fullpotato.badlandscaves.Util.ServerProperties;
import me.fullpotato.badlandscaves.WorldGeneration.*;
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

    @Override
    public void onEnable() {
        loadWorldNames();
        loadConfig();
        loadCustomWorlds();
        registerEvents();
        loadCommands();
        loadRunnables();
        loadCraftingRecipes();
    }

    @Override
    public void onDisable() {
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
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeave(this), this);
        this.getServer().getPluginManager().registerEvents(new NaturalThirstDecrease(this), this);
        this.getServer().getPluginManager().registerEvents(new IncreaseToxInWater(this), this);
        this.getServer().getPluginManager().registerEvents(new DeathHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new BlessedAppleEat(this), this);
        this.getServer().getPluginManager().registerEvents(new CauldronMenu(this), this);
        this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this), this);
        this.getServer().getPluginManager().registerEvents(new Drinking(this), this);
        this.getServer().getPluginManager().registerEvents(new ToxicWaterBottling(this),this);
        this.getServer().getPluginManager().registerEvents(new BlazePowder(this), this);
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
        this.getServer().getPluginManager().registerEvents(new Apples(this), this);
        this.getServer().getPluginManager().registerEvents(new NoMending(this), this);
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
        this.getCommand("customitem").setTabCompleter(new CustomItemTabComplete(this));

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
        BlazePowder blazePowder = new BlazePowder(this);
        blazePowder.tiny_blaze_powder_craft();
        blazePowder.back_to_large();

        PurgeEssence purgeEssence = new PurgeEssence(this);
        purgeEssence.purge_essence_craft();

        Apples apples = new Apples(this);
        apples.craftNotchApple();
        apples.craftBlessedApple();
        apples.craftEnchantedBlessedApple();

        Reeds reeds = new Reeds(this);
        reeds.craft_reeds();

        Sand sand = new Sand(this);
        sand.craft_sand();

        HellEssence hellEssence = new HellEssence(this);
        hellEssence.craft_hell_essence();

        MagicEssence magicEssence = new MagicEssence(this);
        magicEssence.magic_essence_craft();

        MergedSouls mergedSouls = new MergedSouls(this);
        mergedSouls.merge_souls();

        SoulCrystalIncomplete soulCrystalIncomplete = new SoulCrystalIncomplete(this);
        soulCrystalIncomplete.soul_crystal_incomplete();

        Voltshock voltshock = new Voltshock(this);
        voltshock.craft_battery();
        voltshock.craft_shocker();
        voltshock.modify_sword();
        voltshock.charge_sword();
        voltshock.craft_arrow();

        Corrosive corrosive = new Corrosive(this);
        corrosive.craftCorrosiveSubstance();
        corrosive.craftCorrosiveSword();
        corrosive.craftCorrosiveArrow();

        Quartz quartz = new Quartz(this);
        quartz.craft();

        Shield shield = new Shield(this);
        shield.craftRegularShield();
        shield.craftStoneShield();
        shield.craftIronShield();
        shield.craftDiamondShield();

        TitaniumBar titaniumBar = new TitaniumBar(this);
        titaniumBar.fragmentIntoBar();

        StarlightComponents starlightComponents = new StarlightComponents(this);
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
