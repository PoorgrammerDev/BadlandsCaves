package me.fullpotato.badlandscaves;

import com.google.common.base.Charsets;
import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.*;
import me.fullpotato.badlandscaves.AlternateDimensions.PregenerateDimensions;
import me.fullpotato.badlandscaves.AlternateDimensions.SpawnInhabitants;
import me.fullpotato.badlandscaves.AlternateDimensions.UnloadDimensions;
import me.fullpotato.badlandscaves.AlternateDimensions.UseDimensionalAnchor;
import me.fullpotato.badlandscaves.Blocks.SilencerBlock;
import me.fullpotato.badlandscaves.Blocks.TitaniumOre;
import me.fullpotato.badlandscaves.Commands.*;
import me.fullpotato.badlandscaves.Commands.TabCompleters.*;
import me.fullpotato.badlandscaves.CustomItems.Crafting.*;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.*;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.CustomItems.StopCustomItemsInteract;
import me.fullpotato.badlandscaves.CustomItems.Using.*;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms.*;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteInstaller;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteStatChanges;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.*;
import me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter.PreventTechUse;
import me.fullpotato.badlandscaves.Deaths.BlessedAppleEat;
import me.fullpotato.badlandscaves.Deaths.DeathHandler;
import me.fullpotato.badlandscaves.Effects.HungerLimit;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Info.CraftingGuide;
import me.fullpotato.badlandscaves.Info.GuideBook;
import me.fullpotato.badlandscaves.Loot.*;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.AugmentedDrops;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.SoulDrop;
import me.fullpotato.badlandscaves.Loot.MobDeathLoot.ZombieDeathLoot;
import me.fullpotato.badlandscaves.MobBuffs.*;
import me.fullpotato.badlandscaves.NMS.EclipsedShadows.EclipsedShadowsNMS;
import me.fullpotato.badlandscaves.NMS.EclipsedShadows.EclipsedShadows_1_16_R2;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyes_1_16_R2;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayerNMS;
import me.fullpotato.badlandscaves.NMS.FakePlayer.FakePlayer_1_16_R2;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSight_1_16_R2;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.NMS.Possession.Possession_1_16_R2;
import me.fullpotato.badlandscaves.NMS.TPSGetter.TPSGetter;
import me.fullpotato.badlandscaves.NMS.TPSGetter.TPSGetter_1_16_R2;
import me.fullpotato.badlandscaves.Other.*;
import me.fullpotato.badlandscaves.Research.UseResearchTable;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.DescensionPlayerMove;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.DescensionReset;
import me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage.ForceFixDescensionValues;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.SoulCampfire;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.*;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.*;
import me.fullpotato.badlandscaves.Thirst.CauldronMenu;
import me.fullpotato.badlandscaves.Thirst.Drinking;
import me.fullpotato.badlandscaves.Thirst.NaturalThirstDecrease;
import me.fullpotato.badlandscaves.Thirst.ToxicWaterBottling;
import me.fullpotato.badlandscaves.Toxicity.IncreaseToxInRain;
import me.fullpotato.badlandscaves.Toxicity.IncreaseToxInWater;
import me.fullpotato.badlandscaves.Toxicity.ToxSlowDecreaseRunnable;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
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
    private PlayerJoinLeave playerJoinLeave;
    private NaturalThirstDecrease naturalThirstDecrease;
    private IncreaseToxInWater increaseToxInWater;
    private DeathHandler deathHandler;
    private BlessedAppleEat blessedAppleEat;
    private CauldronMenu cauldronMenu;
    private ToxicWaterBottling toxicWaterBottling;
    private Drinking drinking;
    private BlazePowder blazePowder;
    private PurgeEssence purgeEssence;
    private StopCustomItemsInteract stopCustomItemsInteract;
    private UseTaintPowder useTaintPowder;
    private ZombieDeathLoot zombieDeathLoot;
    private GetFishingCrate getFishingCrate;
    private UseFishingCrate useFishingCrate;
    private SkeleBuff skeleBuff;
    private ZombieBuff zombieBuff;
    private CreeperBuff creeperBuff;
    private SpiderBuff spiderBuff;
    private PigZombieBuff pigZombieBuff;
    private SilverfishBuff silverfishBuff;
    private BlazeBuff blazeBuff;
    private GhastBuff ghastBuff;
    private PhantomBuff phantomBuff;
    private WitchBuff witchBuff;
    private SwapPowers swapPowers;
    private Displace displace;
    private StopPowersInvInteract stopPowersInvInteract;
    private Withdraw withdraw;
    private EnhancedEyes enhancedEyes;
    private Possession possession;
    private IncreaseToxInRain increaseToxInRain;
    private EnduranceCancelHunger enduranceCancelHunger;
    private Agility agility;
    private DescensionPlayerMove descensionPlayerMove;
    private SoulDrop soulDrop;
    private HellEssence hellEssence;
    private MagicEssence magicEssence;
    private MergedSouls mergedSouls;
    private SoulCrystalIncomplete soulCrystalIncomplete;
    private UseIncompleteSoulCrystal useIncompleteSoulCrystal;
    private ReflectionBuild reflectionBuild;
    private ReflectionZombie reflectionZombie;
    private PlayerUnderSht playerUnderSht;
    private EndGame endGame;
    private LimitActions limitActions;
    private UseCompleteSoulCrystal useCompleteSoulCrystal;
    private MushroomStew mushroomStew;
    private DestroySpawner destroySpawner;
    private UseRune useRune;
    private UseChargedRune useChargedRune;
    private BackroomsManager backroomsManager;
    private TreasureGear treasureGear;
    private EXPBottle eXPBottle;
    private SerratedSwords serratedSwords;
    private UseSerrated useSerrated;
    private Voltshock voltshock;
    private UseVoltshock useVoltshock;
    private Corrosive corrosive;
    private UseCorrosive useCorrosive;
    private CustomBows customBows;
    private WitherBossFight witherBossFight;
    private Apples apples;
    private NoMending noMending;
    private ShieldBlocking shieldBlocking;
    private Shield shield;
    private CraftingGuide craftingGuide;
    private TitaniumOre titaniumOre;
    private TitaniumBar titaniumBar;
    private StarlightComponents starlightComponents;
    private UseForeverFish useForeverFish;
    private StarlightArmor starlightArmor;
    private EnergyCore energyCore;
    private Voidmatter voidmatter;
    private StarlightTools starlightTools;
    private StarlightCharge starlightCharge;
    private StarlightBlasterMechanism starlightBlasterMechanism;
    private SpawnInhabitants spawnInhabitants;
    private NoOxygen noOxygen;
    private NoFood noFood;
    private Freezing freezing;
    private UseDimensionalAnchor useDimensionalAnchor;
    private PiglinBuff piglinBuff;
    private HoglinBuff hoglinBuff;
    private Silencer silencer;
    private SilencerBlock silencerBlock;
    private Canteen canteen;
    private UseCanteen useCanteen;
    private SoulLantern soulLantern;
    private UseSoulLantern useSoulLantern;
    private PreservationTotem preservationTotem;
    private StarlightSentryMechanism starlightSentryMechanism;
    private UseChambersBag useChambersBag;
    private AugmentedDrops augmentedDrops;
    private NebuliteInstaller nebuliteInstaller;
    private StarlightSaberMechanism starlightSaberMechanism;
    private NebuliteThruster nebuliteThruster;
    private NebuliteOxygenator nebuliteOxygenator;
    private NebuliteSmolderingFlames nebuliteSmolderingFlames;
    private NebuliteShockAbsorber nebuliteShockAbsorber;
    private NebuliteForcefield nebuliteForcefield;
    private NebuliteLightSpeed nebuliteLightSpeed;
    private NebuliteBigSmash nebuliteBigSmash;
    private NebuliteDecisiveDisintegration nebuliteDecisiveDisintegration;
    private NebulitePropulsionBash nebulitePropulsionBash;
    private NebuliteShieldThruster nebuliteShieldThruster;
    private NebuliteCounterattack nebuliteCounterattack;
    private SoulCampfire soulCampfire;
    private ArtifactTenaciousTrickery artifactTenaciousTrickery;
    private ArtifactEclipsedShadows artifactEclipsedShadows;
    private ArtifactManaWarding artifactManaWarding;
    private ArtifactFleetingSpirits artifactFleetingSpirits;
    private ArtifactConvergingSwings artifactConvergingSwings;
    private ArtifactTravellingBlades artifactTravellingBlades;
    private ArtifactHasteWind artifactHasteWind;
    private ArtifactBloodsappingBayonet artifactBloodsappingBayonet;
    private ArtifactSightStealing artifactSightStealing;
    private ArtifactBloodsappingBow artifactBloodsappingBow;
    private ArtifactSummonersRift artifactSummonersRift;
    private ArtifactEmancipatedEyes artifactEmancipatedEyes;
    private ArtifactDiggingDoppelganger artifactDiggingDoppelganger;
    private ArtifactMomentousMomentum artifactMomentousMomentum;
    private PreventWaterBucketPVP preventWaterBucketPVP;
    private StarterSapling starterSapling;
    private AnvilRepair anvilRepair;
    private FishWater fishWater;
    private ResearchTableItems researchTableItems;
    private UseResearchTable useResearchTable;
    private PregenerateDimensions pregenerateDimensions;
    private UseNebuliteCrate useNebuliteCrate;
    private DimensionStructureTable dimensionStructureTable;
    private UnloadDimensions unloadDimensions;
    private PlayerEffects playerEffects;
    private HungerLimit hungerLimit;
    private StarlightPaxelMechanism starlightPaxelMechanism;
    private SlowBreak slowBreak;
    private PreventTechUse preventTechUse;
    private ManaBarManager manaBarManager;
    private PreventMagicUse preventMagicUse;

    //CONFIG FILES
    private FileConfiguration optionsConfig;

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
    private EclipsedShadowsNMS eclipsedShadowsNMS;
    private TPSGetter tpsGetterNMS;

    //CUSTOM ITEMS
    private CustomItemManager customItemManager;

    @Override
    public void onEnable() {
        loadWorldNames();
        loadNMS();
        createOptionsConfig();
        createSystemConfig();
        initializeFields();
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

    public void initializeFields() {
        EnchantmentStorage enchantmentStorage = new EnchantmentStorage(this);
        pregenerateDimensions = new PregenerateDimensions(this);
        customItemManager = new CustomItemManager(this, enchantmentStorage, pregenerateDimensions);
        GuideBook guideBook = new GuideBook(this);
        playerEffects = new PlayerEffects(this);
        naturalThirstDecrease = new NaturalThirstDecrease(this, playerEffects);
        starlightArmor = new StarlightArmor(this);
        starlightTools = new StarlightTools(this);
        energyCore = new EnergyCore(this);
        starlightCharge = new StarlightCharge(this, starlightArmor, starlightTools, enchantmentStorage, energyCore);
        voidmatter = new Voidmatter(this);
        ArtifactManager artifactManager = new ArtifactManager(this);
        artifactFleetingSpirits = new ArtifactFleetingSpirits(this, voidmatter, artifactManager);
        NebuliteManager nebuliteManager = new NebuliteManager(this, starlightCharge, starlightArmor, starlightTools);
        EnvironmentalHazards environmentalHazards = new EnvironmentalHazards(this);
        increaseToxInWater = new IncreaseToxInWater(this, starlightArmor, starlightCharge, artifactManager, voidmatter, artifactFleetingSpirits, nebuliteManager, environmentalHazards);
        deathHandler = new DeathHandler(this, playerEffects);
        blessedAppleEat = new BlessedAppleEat(this, playerEffects);
        cauldronMenu = new CauldronMenu(this);
        toxicWaterBottling = new ToxicWaterBottling(this);
        blazePowder = new BlazePowder(this);
        purgeEssence = new PurgeEssence(this);
        stopCustomItemsInteract = new StopCustomItemsInteract(this, starlightCharge, voidmatter);
        useTaintPowder = new UseTaintPowder(this);
        zombieDeathLoot = new ZombieDeathLoot();
        getFishingCrate = new GetFishingCrate(this);
        useFishingCrate = new UseFishingCrate(this);
        skeleBuff = new SkeleBuff(this);
        zombieBuff = new ZombieBuff(this);
        creeperBuff = new CreeperBuff(this);
        spiderBuff = new SpiderBuff(this);
        pigZombieBuff = new PigZombieBuff(this);
        silverfishBuff = new SilverfishBuff(this);
        blazeBuff = new BlazeBuff(this);
        ghastBuff = new GhastBuff(this);
        phantomBuff = new PhantomBuff(this);
        witchBuff = new WitchBuff(this);
        swapPowers = new SwapPowers(this);
        manaBarManager = new ManaBarManager(this);
        ArtifactDistractingDoppelganger artifactDistractingDoppelganger = new ArtifactDistractingDoppelganger(this, voidmatter, artifactManager);
        displace = new Displace(this, artifactManager, manaBarManager, artifactDistractingDoppelganger);
        stopPowersInvInteract = new StopPowersInvInteract(this);
        enhancedEyes = new EnhancedEyes(this, artifactManager);
        possession = new Possession(this);
        withdraw = new Withdraw(this, artifactManager, possession, voidmatter);
        playerJoinLeave = new PlayerJoinLeave(this, guideBook, withdraw, playerEffects);
        increaseToxInRain = new IncreaseToxInRain(this, starlightArmor, starlightCharge, nebuliteManager, playerEffects, environmentalHazards);
        enduranceCancelHunger = new EnduranceCancelHunger(this);
        agility = new Agility(this);
        InventorySerialize inventorySerialize = new InventorySerialize(this);
        useIncompleteSoulCrystal = new UseIncompleteSoulCrystal(this, inventorySerialize);
        descensionPlayerMove = new DescensionPlayerMove(this, deathHandler, useIncompleteSoulCrystal, inventorySerialize);
        soulDrop = new SoulDrop(this);
        hellEssence = new HellEssence(this);
        magicEssence = new MagicEssence(this);
        mergedSouls = new MergedSouls(this);
        soulCrystalIncomplete = new SoulCrystalIncomplete(this);
        reflectionBuild = new ReflectionBuild(this);
        reflectionZombie = new ReflectionZombie(this);
        playerUnderSht = new PlayerUnderSht(this);
        endGame = new EndGame(this, deathHandler, useIncompleteSoulCrystal);
        limitActions = new LimitActions(this, useIncompleteSoulCrystal);
        useCompleteSoulCrystal = new UseCompleteSoulCrystal(this, inventorySerialize, deathHandler, descensionPlayerMove);
        mushroomStew = new MushroomStew();
        destroySpawner = new DestroySpawner(this);
        useRune = new UseRune(this);
        useChargedRune = new UseChargedRune(this);
        backroomsManager = new BackroomsManager(this, inventorySerialize);
        drinking = new Drinking(this, playerEffects, backroomsManager);
        treasureGear = new TreasureGear();
        eXPBottle = new EXPBottle();
        serratedSwords = new SerratedSwords(this, energyCore, starlightTools);
        useSerrated = new UseSerrated(this, serratedSwords);
        voltshock = new Voltshock(this, energyCore, starlightTools);
        useVoltshock = new UseVoltshock(this, voltshock);
        corrosive = new Corrosive(this, starlightTools, energyCore);
        useCorrosive = new UseCorrosive(this, starlightArmor, starlightCharge, nebuliteManager, corrosive);
        customBows = new CustomBows(this);
        witherBossFight = new WitherBossFight(this);
        apples = new Apples(this);
        noMending = new NoMending(this);
        shieldBlocking = new ShieldBlocking(this, starlightTools, starlightCharge, nebuliteManager);
        shield = new Shield(this);
        craftingGuide = new CraftingGuide(this);
        titaniumOre = new TitaniumOre(this);
        titaniumBar = new TitaniumBar(this);
        starlightComponents = new StarlightComponents(this);
        useForeverFish = new UseForeverFish(this);
        starlightBlasterMechanism = new StarlightBlasterMechanism(this, starlightCharge, starlightTools, nebuliteManager);
        spawnInhabitants = new SpawnInhabitants(this);
        noOxygen = new NoOxygen(this, environmentalHazards);
        noFood = new NoFood(environmentalHazards);
        freezing = new Freezing(this, environmentalHazards);
        useDimensionalAnchor = new UseDimensionalAnchor(this, environmentalHazards, destroySpawner, deathHandler);
        piglinBuff = new PiglinBuff(this);
        hoglinBuff = new HoglinBuff(this);
        silencer = new Silencer(this);
        silencerBlock = new SilencerBlock(this);
        canteen = new Canteen(this);
        useCanteen = new UseCanteen(this, drinking);
        soulLantern = new SoulLantern(this);
        useSoulLantern = new UseSoulLantern(this);
        preservationTotem = new PreservationTotem(this);
        starlightSentryMechanism = new StarlightSentryMechanism(this, starlightTools, starlightCharge, starlightBlasterMechanism);
        useChambersBag = new UseChambersBag(this);
        augmentedDrops = new AugmentedDrops(this);
        NebuliteStatChanges nebuliteStatChanges = new NebuliteStatChanges(this, nebuliteManager, starlightCharge, starlightArmor, starlightTools, enchantmentStorage);
        nebuliteInstaller = new NebuliteInstaller(this, starlightCharge, nebuliteManager, nebuliteStatChanges);
        starlightSaberMechanism = new StarlightSaberMechanism(this, starlightTools, starlightCharge, nebuliteManager, useVoltshock, useCorrosive, useSerrated);
        nebuliteThruster = new NebuliteThruster(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteOxygenator = new NebuliteOxygenator(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteSmolderingFlames = new NebuliteSmolderingFlames(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteShockAbsorber = new NebuliteShockAbsorber(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteForcefield = new NebuliteForcefield(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteLightSpeed = new NebuliteLightSpeed(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteBigSmash = new NebuliteBigSmash(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteDecisiveDisintegration = new NebuliteDecisiveDisintegration(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebulitePropulsionBash = new NebulitePropulsionBash(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteShieldThruster = new NebuliteShieldThruster(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        nebuliteCounterattack = new NebuliteCounterattack(this, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
        soulCampfire = new SoulCampfire(this, swapPowers, artifactManager);
        artifactTenaciousTrickery = new ArtifactTenaciousTrickery(this, voidmatter, artifactManager);
        artifactEclipsedShadows = new ArtifactEclipsedShadows(this, voidmatter, artifactManager, soulCampfire, manaBarManager, swapPowers);
        artifactManaWarding = new ArtifactManaWarding(this, voidmatter, artifactManager);
        artifactConvergingSwings = new ArtifactConvergingSwings(this, voidmatter, artifactManager);
        artifactTravellingBlades = new ArtifactTravellingBlades(this, voidmatter, artifactManager);
        artifactHasteWind = new ArtifactHasteWind(this, voidmatter, artifactManager);
        artifactBloodsappingBayonet = new ArtifactBloodsappingBayonet(this, voidmatter, artifactManager);
        artifactSightStealing = new ArtifactSightStealing(this, voidmatter, artifactManager);
        artifactBloodsappingBow = new ArtifactBloodsappingBow(this, voidmatter, artifactManager);
        artifactSummonersRift = new ArtifactSummonersRift(this, voidmatter, artifactManager, artifactFleetingSpirits);
        artifactEmancipatedEyes = new ArtifactEmancipatedEyes(this, voidmatter, artifactManager, enhancedEyes);
        starlightPaxelMechanism = new StarlightPaxelMechanism(this, starlightTools, starlightCharge);
        artifactDiggingDoppelganger = new ArtifactDiggingDoppelganger(this, voidmatter, artifactManager, starlightPaxelMechanism);
        artifactMomentousMomentum = new ArtifactMomentousMomentum(this);
        preventWaterBucketPVP = new PreventWaterBucketPVP();
        starterSapling = new StarterSapling(this);
        anvilRepair = new AnvilRepair(this);
        fishWater = new FishWater(this);
        researchTableItems = new ResearchTableItems(this);
        useResearchTable = new UseResearchTable(this);
        useNebuliteCrate = new UseNebuliteCrate(this);
        dimensionStructureTable = new DimensionStructureTable(this);
        unloadDimensions = new UnloadDimensions(this);
        hungerLimit = new HungerLimit();
        slowBreak = new SlowBreak(this);
        preventTechUse = new PreventTechUse(this, voidmatter, enchantmentStorage);
        preventMagicUse = new PreventMagicUse(this, starlightCharge, enchantmentStorage);
    }

    //CONFIG
    private void createOptionsConfig() {
        File optionsFile = new File(getDataFolder(), "options.yml");
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
        WithdrawWorld empty_world = new WithdrawWorld(this);
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
                playerJoinLeave,
                naturalThirstDecrease,
                increaseToxInWater,
                deathHandler,
                blessedAppleEat,
                cauldronMenu,
                toxicWaterBottling,
                drinking,
                blazePowder,
                purgeEssence,
                stopCustomItemsInteract,
                useTaintPowder,
                zombieDeathLoot,
                getFishingCrate,
                useFishingCrate,
                skeleBuff,
                zombieBuff,
                creeperBuff,
                spiderBuff,
                pigZombieBuff,
                silverfishBuff,
                blazeBuff,
                ghastBuff,
                phantomBuff,
                witchBuff,
                swapPowers,
                displace,
                stopPowersInvInteract,
                withdraw,
                enhancedEyes,
                possession,
                increaseToxInRain,
                enduranceCancelHunger,
                agility,
                descensionPlayerMove,
                soulDrop,
                hellEssence,
                magicEssence,
                mergedSouls,
                soulCrystalIncomplete,
                useIncompleteSoulCrystal,
                reflectionBuild,
                reflectionZombie,
                playerUnderSht,
                endGame,
                limitActions,
                useCompleteSoulCrystal,
                mushroomStew,
                destroySpawner,
                useRune,
                useChargedRune,
                backroomsManager,
                treasureGear,
                eXPBottle,
                serratedSwords,
                useSerrated,
                voltshock,
                useVoltshock,
                corrosive,
                useCorrosive,
                customBows,
                witherBossFight,
                apples,
                noMending,
                shieldBlocking,
                shield,
                craftingGuide,
                titaniumOre,
                titaniumBar,
                starlightComponents,
                useForeverFish,
                starlightArmor,
                energyCore,
                voidmatter,
                starlightTools,
                starlightCharge,
                starlightBlasterMechanism,
                spawnInhabitants,
                noOxygen,
                noFood,
                freezing,
                useDimensionalAnchor,
                piglinBuff,
                hoglinBuff,
                silencer,
                silencerBlock,
                canteen,
                useCanteen,
                soulLantern,
                useSoulLantern,
                preservationTotem,
                starlightSentryMechanism,
                useChambersBag,
                augmentedDrops,
                nebuliteInstaller,
                starlightSaberMechanism,
                nebuliteThruster,
                nebuliteOxygenator,
                nebuliteSmolderingFlames,
                nebuliteShockAbsorber,
                nebuliteForcefield,
                nebuliteLightSpeed,
                nebuliteBigSmash,
                nebuliteDecisiveDisintegration,
                nebulitePropulsionBash,
                nebuliteShieldThruster,
                nebuliteCounterattack,
                soulCampfire,
                artifactTenaciousTrickery,
                artifactEclipsedShadows,
                artifactManaWarding,
                artifactFleetingSpirits,
                artifactConvergingSwings,
                artifactTravellingBlades,
                artifactHasteWind,
                artifactBloodsappingBayonet,
                artifactSightStealing,
                artifactBloodsappingBow,
                artifactSummonersRift,
                artifactEmancipatedEyes,
                artifactDiggingDoppelganger,
                artifactMomentousMomentum,
                preventWaterBucketPVP,
                starterSapling,
                anvilRepair,
                fishWater,
                researchTableItems,
                useResearchTable,
                pregenerateDimensions,
                useNebuliteCrate,
                dimensionStructureTable,
                unloadDimensions,
                playerEffects,
                hungerLimit,
                starlightPaxelMechanism,
                slowBreak,
                preventTechUse,
                preventMagicUse,
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

        this.getCommand("backrooms").setExecutor(new BackroomsCommand(this, backroomsManager));
        this.getCommand("backrooms").setTabCompleter(new BackroomsCommandTabComplete(this));

        this.getCommand("world").setExecutor(new WorldCommand(this));

        this.getCommand("pregenerate").setExecutor(new PregenerateCommand(this));
    }

    //RUNNABLES
    public void loadRunnables() {
        new ActionbarRunnable(this).runTaskTimer(this, 0, 5);
        new ToxSlowDecreaseRunnable(this).runTaskTimer(this, 0, 600);
        new DisplaceParticleRunnable(this).runTaskTimerAsynchronously(this, 0, 2);
        new WithdrawIndicatorRunnable(this).runTaskTimerAsynchronously(this, 0, 5);
        new PossessionIndicatorRunnable(this).runTaskTimer(this, 0, 1);
        manaBarManager.runTaskTimer(this, 0, 5);
        new ManaRegen(this).runTaskTimer(this, 0, 5);
        new DescensionReset(this).runTaskTimer(this, 0, 120);
        new ForceFixDescensionValues(this).runTaskTimer(this, 0, 100);
        new AugmentedSpider(this).runTaskTimer(this, 0, 5);
        new AugmentedZombie(this).runTaskTimer(this, 0, 10);
        new Surface(this).runTaskTimer(this, 0, 100);
        starlightBlasterMechanism.runTaskTimer(this, 0, 20);
        new MeteorShowerRunnable(this).runTaskTimer(this, 0, 20);
        new BewildermentRunnable(this).runTaskTimer(this, 0, 100);
        noOxygen.runTaskTimer(this, 0, 10);
        new LavaFloorRunnable(this).runTaskTimer(this, 0, 20);
        new NoFloorRunnable(this).runTaskTimer(this, 0, 10);
        new ParanoiaRunnable(this).runTaskTimer(this, 0, 80);
        freezing.runTaskTimer(this, 0, 5);
        preventTechUse.runTaskTimer(this, 0, 200);
        preventMagicUse.runTaskTimer(this, 0, 200);
        new SilencerTimerRunnable(this).runTaskTimerAsynchronously(this, 0, 0);
        nebuliteCounterattack.runTaskTimer(this, 0, 0);
        artifactHasteWind.runTaskTimer(this, 0, 0);
        new ArtifactMomentousMomentum(this).runTaskTimer(this, 0, 0);
        new PregenerateDimensions(this).runTaskTimer(this, 600, 2000);

        witherBossFight.checkIfEnded();
        witherBossFight.portalDestroyTimer();
        nebulitePropulsionBash.cooldownMechanism();
    }

    //RECIPES
    public void loadCraftingRecipes() {
        blazePowder.tiny_blaze_powder_craft();
        blazePowder.back_to_large();

        purgeEssence.purge_essence_craft();

        apples.craftNotchApple();
        apples.craftBlessedApple();
        apples.craftEnchantedBlessedApple();

        Reeds reeds = new Reeds(this);
        reeds.craft_reeds();

        Sand sand = new Sand(this);
        sand.craft_sand();

        hellEssence.craft_hell_essence();

        magicEssence.magic_essence_craft();

        mergedSouls.merge_souls();

        soulCrystalIncomplete.soul_crystal_incomplete();

        voltshock.craft_battery();
        voltshock.craft_shocker();
        voltshock.modify_sword();
        voltshock.charge_sword();
        voltshock.craft_arrow();

        corrosive.craftCorrosiveSubstance();
        corrosive.craftCorrosiveSword();
        corrosive.craftCorrosiveArrow();

        Quartz quartz = new Quartz(this);
        quartz.craft();

        shield.craftRegularShield();
        shield.craftStoneShield();
        shield.craftIronShield();
        shield.craftDiamondShield();
        shield.craftNetheriteShield();


        titaniumBar.fragmentIntoBar();
        titaniumBar.craftTitaniumRod();
        titaniumBar.reinforceBarRecipe();

        starlightComponents.craftBinding();
        starlightComponents.craftGoldCable();
        starlightComponents.craftNetherStarFragment();
        starlightComponents.craftStarlightCircuit();
        starlightComponents.craftStarlightBattery();
        starlightComponents.craftStarlightModule();
        starlightComponents.craftEnergium();
        starlightComponents.craftPhotonEmitter();

        starlightCharge.chargeRecipe();

        starlightArmor.helmetRecipe();
        starlightArmor.chestplateRecipe();
        starlightArmor.leggingsRecipe();
        starlightArmor.bootsRecipe();

        starlightTools.saberRecipe();
        starlightTools.shieldRecipe();
        starlightTools.blasterRecipe();
        starlightTools.paxelRecipe();
        starlightTools.sentryRecipe();

        silencer.wavelengthDisruptorRecipe();
        silencer.silencerRecipe();

        energyCore.energyCoreRecipes();

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

        canteen.canteenRecipe();
        canteen.fillCanteen();

        soulLantern.soulLanternRecipe();

        researchTableItems.convexLensRecipe();
        researchTableItems.magnifyingGlassRecipe();
        researchTableItems.researchTableRecipe();
    }

    public void loadWorldNames() {
        try {
            File properties = new File("server.properties");
            this.mainWorldName = ServerProperties.getSetting(properties, "level-name");
        }
        catch (IOException e) {
            this.getLogger().warning("No default world found, defaulting to \"world\"");
            this.mainWorldName = "world";
        }
        this.descensionWorldName = this.getMainWorldName() + "_descension";
        this.withdrawWorldName = this.getMainWorldName() + "_empty";
        this.reflectionWorldName = this.getMainWorldName() + "_reflection";
        this.backroomsWorldName = this.getMainWorldName() + "_backrooms";
        this.chambersWorldName = this.getMainWorldName() + "_chambers";
        this.dimensionPrefixName = this.getMainWorldName() + "_dim_";
    }

    public void loadNMS() {
        String version = this.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 2);

        if ("1_16_R2".equals(version)) {
            enhancedEyesNMS = new EnhancedEyes_1_16_R2();
            fakePlayerNMS = new FakePlayer_1_16_R2(this);
            lineOfSightNMS = new LineOfSight_1_16_R2();
            possessionNMS = new Possession_1_16_R2();
            eclipsedShadowsNMS = new EclipsedShadows_1_16_R2(this);
            tpsGetterNMS = new TPSGetter_1_16_R2(this);

        } else {
            this.getServer().getLogger().severe("Invalid server version " + version + ". Disabling plugin.");
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

    public EclipsedShadowsNMS getEclipsedShadowsNMS() {
        return eclipsedShadowsNMS;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public TPSGetter getTpsGetterNMS() {
        return tpsGetterNMS;
    }
}
