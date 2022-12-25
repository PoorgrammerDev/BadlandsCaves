package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;

public enum Artifact {
    TENACIOUS_TRICKERY(CustomItem.ARTIFACT_TENACIOUS_TRICKERY, ArtifactBaseItem.VOIDMATTER_ARMOR, ArtifactBaseItem.VOIDMATTER_BLADE, ArtifactBaseItem.VOIDMATTER_BOW, ArtifactBaseItem.VOIDMATTER_TOOLS),

    ECLIPSED_SHADOWS(CustomItem.ARTIFACT_ECLIPSED_SHADOWS, ArtifactBaseItem.VOIDMATTER_ARMOR),
    MANA_WARDING(CustomItem.ARTIFACT_MANA_WARDING, ArtifactBaseItem.VOIDMATTER_ARMOR),
    FLEETING_SPIRITS(CustomItem.ARTIFACT_FLEETING_SPIRITS, ArtifactBaseItem.VOIDMATTER_ARMOR),

    CONVERGING_SWINGS(CustomItem.ARTIFACT_CONVERGING_SWINGS, ArtifactBaseItem.VOIDMATTER_BLADE),
    TRAVELING_BLADES(CustomItem.ARTIFACT_TRAVELING_BLADES, ArtifactBaseItem.VOIDMATTER_BLADE),
    HASTE_WIND(CustomItem.ARTIFACT_HASTE_WIND, ArtifactBaseItem.VOIDMATTER_BLADE),
    BLOODSAPPING_BAYONET(CustomItem.ARTIFACT_BLOODSAPPING_BAYONET, ArtifactBaseItem.VOIDMATTER_BLADE),

    SIGHT_STEALING(CustomItem.ARTIFACT_SIGHT_STEALING, ArtifactBaseItem.VOIDMATTER_BOW),
    BLOODSAPPING_BOW(CustomItem.ARTIFACT_BLOODSAPPING_BOW, ArtifactBaseItem.VOIDMATTER_BOW),
    SUMMONERS_RIFT(CustomItem.ARTIFACT_SUMMONERS_RIFT, ArtifactBaseItem.VOIDMATTER_BOW),

    PRESCIENT_SIGHT(CustomItem.ARTIFACT_PRESCIENT_SIGHT, ArtifactBaseItem.VOIDMATTER_TOOLS),
    DIGGING_DOPPELGANGER(CustomItem.ARTIFACT_DIGGING_DOPPELGANGER, ArtifactBaseItem.VOIDMATTER_TOOLS),

    UNDISPLACE(CustomItem.ARTIFACT_UNDISPLACE, ArtifactBaseItem.DISPLACE),
    MOMENTOUS_MOMENTUM(CustomItem.ARTIFACT_MOMENTOUS_MOMENTUM, ArtifactBaseItem.DISPLACE),
    DISTRACTING_DOPPELGANGER(CustomItem.ARTIFACT_DISTRACTING_DOPPELGANGER, ArtifactBaseItem.DISPLACE),

    SOUL_HEIST(CustomItem.ARTIFACT_SOUL_HEIST, ArtifactBaseItem.WITHDRAW),
    TEMPORAL_SUSPENSION(CustomItem.ARTIFACT_TEMPORAL_SUSPENSION, ArtifactBaseItem.WITHDRAW),

    DIRECTIONAL_VISION(CustomItem.ARTIFACT_DIRECTIONAL_VISION, ArtifactBaseItem.ENHANCED_EYES),
    FORESIGHT(CustomItem.ARTIFACT_FORESIGHT, ArtifactBaseItem.ENHANCED_EYES),
    CHEAP_SIGHT(CustomItem.ARTIFACT_CHEAP_SIGHT, ArtifactBaseItem.ENHANCED_EYES),

    ADAPTIVE_EXPLOITATION(CustomItem.ARTIFACT_ADAPTIVE_EXPLOITATION, ArtifactBaseItem.POSSESSION),
    SAFEGUARD(CustomItem.ARTIFACT_SAFEGUARD, ArtifactBaseItem.POSSESSION),
    INSPIRE(CustomItem.ARTIFACT_INSPIRE, ArtifactBaseItem.POSSESSION),
    DOMINO(CustomItem.ARTIFACT_DOMINO, ArtifactBaseItem.POSSESSION),

    TRIPLE_JUMP(CustomItem.ARTIFACT_TRIPLE_JUMP, ArtifactBaseItem.AGILITY),
    RECOVERY_ROLL(CustomItem.ARTIFACT_RECOVERY_ROLL, ArtifactBaseItem.AGILITY),
    SHADOWSTEP(CustomItem.ARTIFACT_SHADOWSTEP, ArtifactBaseItem.AGILITY),

    METAPHYSICAL_NOURISHMENT(CustomItem.ARTIFACT_METAPHYSICAL_NOURISHMENT, ArtifactBaseItem.ENDURANCE);

    private final CustomItem artifactItem;
    private final ArtifactBaseItem[] artifactBaseItems;

    Artifact(CustomItem artifactItem, ArtifactBaseItem... artifactBaseItems) {
        this.artifactItem = artifactItem;
        this.artifactBaseItems = artifactBaseItems;
    }

    public CustomItem getArtifactItem() {
        return artifactItem;
    }

    public ArtifactBaseItem[] getArtifactBaseItems() {
        return artifactBaseItems;
    }

    public static Artifact[] GetLootItems() {
        return new Artifact[] {
            TENACIOUS_TRICKERY,
            ECLIPSED_SHADOWS,
            MANA_WARDING,
            FLEETING_SPIRITS,
            CONVERGING_SWINGS,
            //TRAVELING_BLADES,         // REMOVED: Orb of Ascension
            HASTE_WIND,
            BLOODSAPPING_BAYONET,
            SIGHT_STEALING,
            BLOODSAPPING_BOW,
            //SUMMONERS_RIFT,           // REMOVED: Orb of Ascension
            PRESCIENT_SIGHT,
            DIGGING_DOPPELGANGER,
            UNDISPLACE,
            MOMENTOUS_MOMENTUM,
            DISTRACTING_DOPPELGANGER,
            SOUL_HEIST,
            // TEMPORAL_SUSPENSION,     // not implemented
            DIRECTIONAL_VISION,
            // FORESIGHT,
            CHEAP_SIGHT,
            // ADAPTIVE_EXPLOITATION,   // not implemented
            SAFEGUARD,
            // INSPIRE,                 // not implemented
            DOMINO,
            TRIPLE_JUMP,
            RECOVERY_ROLL,
            // SHADOWSTEP,              // not implemented
            // METAPHYSICAL_NOURISHMENT // not implemented
        };
    }

}
