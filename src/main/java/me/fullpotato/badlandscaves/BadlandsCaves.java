package me.fullpotato.badlandscaves;

import com.google.common.base.Charsets;
import me.fullpotato.badlandscaves.AlternateDimensions.GravityFallDamage;
import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.*;
import me.fullpotato.badlandscaves.AlternateDimensions.SpawnInhabitants;
import me.fullpotato.badlandscaves.Blocks.SilencerBlock;
import me.fullpotato.badlandscaves.Blocks.TitaniumOre;
import me.fullpotato.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.Commands.TabCompleters.*;
import me.fullpotato.badlandscaves.CustomItems.Crafting.*;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.*;
import me.fullpotato.badlandscaves.CustomItems.StopCustomItemsInteract;
import me.fullpotato.badlandscaves.CustomItems.Using.*;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.*;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms.NebuliteOxygenator;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms.NebuliteThruster;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.StarlightSaberMechanism;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteInstaller;
import me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter.PreventNonPoweredUsage;
import me.fullpotato.badlandscaves.Deaths.BlessedAppleEat;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.Effects.PlayerEffectsRunnable;
import me.fullpotato.badlandscaves.Info.CraftingGuide;
import me.fullpotato.badlandscaves.Loot.*;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.AugmentedDrops;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.SoulDrop;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.ZombieDeathLoot;
import me.fullpotato.badlandscaves.MobBuffs.*;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyes_1_16_R1;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayer_1_16_R1;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSight_1_16_R1;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.NMS.Possession.Possession_1_16_R1;
import me.fullpotato.badlandscaves.Other.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class BadlandsCaves extends JavaPlugin {

    //CONFIG FILES
    private FileConfiguration optionsConfig;
    private File optionsFile;

    private FileConfiguration systemConfig;
    private File systemFile;

    //WORLD NAMES
    private String mainWorldName;
    private String descensionWorldName;
    private String withdrawWorldName;
    private String reflectionWorldName;
    private String backroomsWorldName;
    private String chambersWorldName;
    private String dimensionPrefixName;

    //NMS
    private EnhancedEyesNMS enhancedEyesNMS;
    private FakePlayerNMS fakePlayerNMS;
    private LineOfSightNMS lineOfSightNMS;
    private PossessionNMS possessionNMS;

    @Override
    public void onEnable() {
        getServerVersion();
        createOptionsConfig();
        createSystemConfig();
        loadWorldNames();
        loadCustomWorlds();
        registerEvents();
        loadCommands();
        loadRunnables();
        loadCraftingRecipes();
    }

    @Override
    public void onDisable() {
        this.saveSystemConfig();
        this.getServer().getScheduler().cancelTasks(this);
    }

    //CONFIG
    private void createOptionsConfig() {
        optionsFile = new File(getDataFolder(), "options.yml");
        if (!optionsFile.exists()) {
            optionsFile.getParentFile().mkdirs();
            saveResource("options.yml", false);
        }

        optionsConfig = new YamlConfiguration();
        try {
            optionsConfig.load(optionsFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createSystemConfig() {
        systemFile = new File(getDataFolder(), "system.yml");
        if (!systemFile.exists()) {
            systemFile.getParentFile().mkdirs();
            saveResource("system.yml", false);
        }

        systemConfig = new YamlConfiguration();
        try {
            systemConfig.load(systemFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveSystemConfig() {
        try {
            this.getSystemConfig().save(this.systemFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadSystemConfig () {
        this.systemConfig = YamlConfiguration.loadConfiguration(this.systemFile);
        InputStream defConfigStream = this.getResource("system.yml");
        if (defConfigStream != null) {
            this.systemConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
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

        this.getServer().getWorld(getMainWorldName()).setGameRule(GameRule.REDUCED_DEBUG_INFO, false);

        StartingDungeons dungeons = new StartingDungeons(this);
        dungeons.genSpawnDungeons();
    }

    //EVENTS
    public void registerEvents() {
        final Listener[] events = {
                new PlayerJoinLeave(this),
                new NaturalThirstDecrease(this),
                new IncreaseToxInWater(this),
                new DeathHandler(this),
                new BlessedAppleEat(this),
                new CauldronMenu(this),
                new ToxicWaterBottling(this),
                new Drinking(this),
                new ToxicWaterBottling(this),
                new BlazePowder(this),
                new PurgeEssence(this),
                new StopCustomItemsInteract(),
                new UseTaintPowder(this),
                new ZombieDeathLoot(),
                new GetFishingCrate(this),
                new UseFishingCrate(this),
                new SkeleBuff(this),
                new ZombieBuff(this),
                new CreeperBuff(this),
                new SpiderBuff(this),
                new PigZombieBuff(this),
                new SilverfishBuff(this),
                new BlazeBuff(this),
                new GhastBuff(this),
                new PhantomBuff(this),
                new WitchBuff(this),
                new SwapPowers(this),
                new Displace(this),
                new StopPowersInvInteract(this),
                new Withdraw(this),
                new EnhancedEyes(this),
                new Possession(this),
                new IncreaseToxInRain(this),
                new EnduranceCancelHunger(this),
                new Agility(this),
                new DescensionPlayerMove(this),
                new SoulDrop(this),
                new HellEssence(this),
                new MagicEssence(this),
                new MergedSouls(this),
                new SoulCrystalIncomplete(this),
                new UseIncompleteSoulCrystal(this),
                new ReflectionBuild(this),
                new ReflectionZombie(this),
                new PlayerUnderSht(this),
                new EndGame(this),
                new LimitActions(this),
                new UseCompleteSoulCrystal(this),
                new MushroomStew(),
                new DestroySpawner(this),
                new UseRune(this),
                new UseChargedRune(this),
                new BackroomsManager(this),
                new TreasureGear(),
                new EXPBottle(),
                new SerratedSwords(this),
                new UseSerrated(this),
                new Voltshock(this),
                new UseVoltshock(this),
                new Corrosive(this),
                new UseCorrosive(this),
                new CustomBows(this),
                new WitherBossFight(this),
                new Apples(this),
                new NoMending(),
                new ShieldBlocking(this),
                new Shield(this),
                new CraftingGuide(this),
                new TitaniumOre(this),
                new TitaniumBar(this),
                new StarlightComponents(this),
                new UseForeverFish(),
                new StarlightArmor(this),
                new EnergyCore(this),
                new Voidmatter(this),
                new StarlightTools(this),
                new StarlightCharge(this),
                new StarlightBlasterMechanism(this),
                new GravityFallDamage(this),
                new SpawnInhabitants(this),
                new NoOxygen(this),
                new NoFood(this),
                new Freezing(this),
                new UseDimensionalAnchor(this),
                new PiglinBuff(this),
                new HoglinBuff(this),
                new Silencer(this),
                new SilencerBlock(this),
                new Canteen(this),
                new UseCanteen(this),
                new SoulLantern(this),
                new UseSoulLantern(this),
                new PreservationTotem(this),
                new StarlightSentryMechanism(this),
                new UseChambersBag(this),
                new AugmentedDrops(this),
                //new RestlessNight(this),
                new NebuliteInstaller(this),
                new StarlightSaberMechanism(this),
                new NebuliteThruster(this),
                new NebuliteOxygenator(this),
        };

        for (Listener event : events) {
            this.getServer().getPluginManager().registerEvents(event, this);
        }

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

        this.getCommand("backrooms").setExecutor(new BackroomsCommand(this));
        this.getCommand("backrooms").setTabCompleter(new BackroomsCommandTabComplete(this));

        this.getCommand("world").setExecutor(new WorldCommand(this));
    }

    //RUNNABLES
    public void loadRunnables() {
        new ActionbarRunnable(this).runTaskTimer(this, 0 ,0);
        new PlayerEffectsRunnable(this).runTaskTimer(this,0,0);
        new ToxSlowDecreaseRunnable(this).runTaskTimer(this, 0, 600);
        new ManaBarManager(this).runTaskTimer(this, 0, 5);
        new ManaRegen(this).runTaskTimer(this, 0, 0);
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
        new ChargeRunnable(this).runTaskTimer(this, 0, 0);
        new StarlightPaxelMechanism(this).runTaskTimerAsynchronously(this, 0, 0);
        new StarlightBlasterMechanism(this).runTaskTimer(this, 0, 0);
        new SlowBreakRunnable(this).runTaskTimer(this, 0, 20);
        new MeteorShowerRunnable(this).runTaskTimer(this, 0, 20);
        new BewildermentRunnable(this).runTaskTimer(this, 0, 100);
        new NoOxygen(this).runTaskTimer(this, 0, 0);
        new LavaFloorRunnable(this).runTaskTimer(this, 0, 20);
        new NoFloorRunnable(this).runTaskTimer(this, 0, 10);
        new ParanoiaRunnable(this).runTaskTimer(this, 0, 20);
        new Freezing(this).runTaskTimer(this, 0, 5);
        new PreventNonPoweredUsage(this).runTaskTimerAsynchronously(this, 0, 0);
        new SilencerTimerRunnable(this).runTaskTimerAsynchronously(this, 0, 0);


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
        shield.craftNetheriteShield();

        TitaniumBar titaniumBar = new TitaniumBar(this);
        titaniumBar.fragmentIntoBar();
        titaniumBar.craftTitaniumRod();
        titaniumBar.reinforceBarRecipe();

        StarlightComponents starlightComponents = new StarlightComponents(this);
        starlightComponents.craftBinding();
        starlightComponents.craftGoldCable();
        starlightComponents.craftNetherStarFragment();
        starlightComponents.craftStarlightCircuit();
        starlightComponents.craftStarlightBattery();
        starlightComponents.craftStarlightModule();
        starlightComponents.craftEnergium();
        starlightComponents.craftPhotonEmitter();

        StarlightCharge starlightCharge = new StarlightCharge(this);
        starlightCharge.chargeRecipe();

        StarlightArmor starlightArmor = new StarlightArmor(this);
        starlightArmor.helmetRecipe();
        starlightArmor.chestplateRecipe();
        starlightArmor.leggingsRecipe();
        starlightArmor.bootsRecipe();

        StarlightTools starlightTools = new StarlightTools(this);
        starlightTools.saberRecipe();
        starlightTools.shieldRecipe();
        starlightTools.blasterRecipe();
        starlightTools.paxelRecipe();
        starlightTools.sentryRecipe();

        Silencer silencer = new Silencer(this);
        silencer.wavelengthDisruptorRecipe();
        silencer.silencerRecipe();

        EnergyCore energyCore = new EnergyCore(this);
        energyCore.energyCoreRecipe();

        Voidmatter voidmatter = new Voidmatter(this);
        voidmatter.stickRecipe();
        voidmatter.stringRecipe();
        voidmatter.helmetRecipe();
        voidmatter.chestplateRecipe();
        voidmatter.leggingsRecipe();
        voidmatter.bootsRecipe();
        voidmatter.bladeRecipe();
        voidmatter.bowRecipe();
        voidmatter.pickaxeRecipe();
        voidmatter.shovelRecipe();
        voidmatter.axeRecipe();

        Canteen canteen = new Canteen(this);
        canteen.canteenRecipe();
        canteen.fillCanteen();

        SoulLantern soulLantern = new SoulLantern(this);
        soulLantern.soulLanternRecipe();
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
        this.descensionWorldName = this.getMainWorldName() + "_descension";
        this.withdrawWorldName = this.getMainWorldName() + "_empty";
        this.reflectionWorldName = this.getMainWorldName() + "_reflection";
        this.backroomsWorldName = this.getMainWorldName() + "_backrooms";
        this.chambersWorldName = this.getMainWorldName() + "_chambers";
        this.dimensionPrefixName = this.getMainWorldName() + "_dim_";
    }

    public void getServerVersion() {
        String version = this.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 2);

        if ("1_16_R1".equals(version)) {
            enhancedEyesNMS = new EnhancedEyes_1_16_R1();
            fakePlayerNMS = new FakePlayer_1_16_R1(this);
            lineOfSightNMS = new LineOfSight_1_16_R1();
            possessionNMS = new Possession_1_16_R1();
        } else {
            this.getServer().getLogger().severe("[BadlandsCaves] Invalid server version " + version + ". Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    //GETTERS----------------------------------------------------------------
    public String getMainWorldName() {
        return mainWorldName;
    }

    public String getDescensionWorldName() {
        return descensionWorldName;
    }

    public String getWithdrawWorldName() {
        return withdrawWorldName;
    }

    public String getReflectionWorldName() {
        return reflectionWorldName;
    }

    public String getBackroomsWorldName() {
        return backroomsWorldName;
    }

    public String getChambersWorldName() {
        return chambersWorldName;
    }

    public String getDimensionPrefixName() {
        return dimensionPrefixName;
    }

    public EnhancedEyesNMS getEnhancedEyesNMS() {
        return enhancedEyesNMS;
    }

    public FakePlayerNMS getFakePlayerNMS() {
        return fakePlayerNMS;
    }

    public LineOfSightNMS getLineOfSightNMS() {
        return lineOfSightNMS;
    }

    public PossessionNMS getPossessionNMS() {
        return possessionNMS;
    }

    public FileConfiguration getOptionsConfig() {
        return optionsConfig;
    }

    public FileConfiguration getSystemConfig() {
        return systemConfig;
    }
}
