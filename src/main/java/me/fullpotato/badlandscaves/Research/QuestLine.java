package me.fullpotato.badlandscaves.Research;

public enum QuestLine {
    STONE(Quest.STONE_0, Quest.STONE_1, Quest.STONE_2, Quest.STONE_3, Quest.STONE_4),
    SAPLING(Quest.SAPLING_0, Quest.SAPLING_1, Quest.SAPLING_2),
    WHEAT(Quest.WHEAT_0, Quest.WHEAT_1, Quest.WHEAT_2, Quest.WHEAT_3),
    POTATO(Quest.POTATO_0, Quest.POTATO_1, Quest.POTATO_2, Quest.POTATO_3),
    POISONOUS_POTATO(Quest.POISONOUS_POTATO_0, Quest.POISONOUS_POTATO_1, Quest.POISONOUS_POTATO_2),
    CARROT(Quest.CARROT_0, Quest.CARROT_1, Quest.CARROT_2, Quest.CARROT_3),
    COAL(Quest.COAL_0, Quest.COAL_1, Quest.COAL_2),
    IRON(Quest.IRON_0, Quest.IRON_1, Quest.IRON_2),
    GOLD(Quest.GOLD_0, Quest.GOLD_1, Quest.GOLD_2),
    ROTTEN_FLESH(Quest.ROTTEN_FLESH_0, Quest.ROTTEN_FLESH_1, Quest.ROTTEN_FLESH_2),
    BONE(Quest.BONE_0, Quest.BONE_1, Quest.BONE_2, Quest.BONE_3),
    GUNPOWDER(Quest.GUNPOWDER_0, Quest.GUNPOWDER_1, Quest.GUNPOWDER_2, Quest.GUNPOWDER_3),
    MAGMA_CREAM(Quest.MAGMA_CREAM_0, Quest.MAGMA_CREAM_1, Quest.MAGMA_CREAM_2),
    BLAZE_POWDER(Quest.BLAZE_POWDER_0, Quest.BLAZE_POWDER_1, Quest.BLAZE_POWDER_2),
    GHAST_TEAR(Quest.GHAST_TEAR_0, Quest.GHAST_TEAR_1, Quest.GHAST_TEAR_2),
    PHANTOM_MEMBRANE(Quest.PHANTOM_MEMBRANE_0, Quest.PHANTOM_MEMBRANE_1),
    ENDER_PEARL(Quest.ENDER_PEARL_0, Quest.ENDER_PEARL_1),
    VOLTSHOCK(Quest.VOLTSHOCK_0, Quest.VOLTSHOCK_1, Quest.VOLTSHOCK_2, Quest.VOLTSHOCK_3, Quest.VOLTSHOCK_4, Quest.VOLTSHOCK_5),
    TAINTED_POWDER(Quest.TAINTED_POWDER_0, Quest.TAINTED_POWDER_1, Quest.TAINTED_POWDER_2, Quest.TAINTED_POWDER_3),
    CORROSIVE(Quest.CORROSIVE_0, Quest.CORROSIVE_1, Quest.CORROSIVE_2, Quest.CORROSIVE_3, Quest.CORROSIVE_4, Quest.CORROSIVE_5),
    RAIN_RESISTANCE(Quest.RAIN_RESISTANCE_0, Quest.RAIN_RESISTANCE_1),
    DUNGEON_COMPASS(Quest.DUNGEON_COMPASS_0, Quest.DUNGEON_COMPASS_1, Quest.DUNGEON_COMPASS_2),
    STIM_PACK(Quest.STIM_PACK_0, Quest.STIM_PACK_1, Quest.STIM_PACK_2),
    ;

    private final Quest[] quests;
    QuestLine(Quest... quests) {
        this.quests = quests;
    }

    public Quest[] getQuests() {
        return quests;
    }
}
