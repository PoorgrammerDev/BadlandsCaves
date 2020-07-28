package me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter.Artifacts;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;

public enum Artifact {
    TENACIOUS_TRICKERY(CustomItem.ARTIFACT_TENACIOUS_TRICKERY, ArtifactBaseItem.ALL_VOIDMATTER),

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

    EMANCIPATED_EYES(CustomItem.ARTIFACT_EMANCIPATED_EYES, ArtifactBaseItem.VOIDMATTER_TOOLS),
    DIGGING_DOPPELGANGER(CustomItem.ARTIFACT_DIGGING_DOPPELGANGER, ArtifactBaseItem.VOIDMATTER_TOOLS),

    UNDISPLACE(CustomItem.ARTIFACT_UNDISPLACE, ArtifactBaseItem.DISPLACE),
    MOMENTOUS_MOMENTUM(CustomItem.ARTIFACT_MOMENTOUS_MOMENTUM, ArtifactBaseItem.DISPLACE),
    DISTRACTION_CLONE(CustomItem.ARTIFACT_DISTRACTION_CLONE, ArtifactBaseItem.DISPLACE),
    COMBAT_CLONE(CustomItem.ARTIFACT_COMBAT_CLONE, ArtifactBaseItem.DISPLACE),

    DIMENSIONAL_CAPTURING(CustomItem.ARTIFACT_DIMENSIONAL_CAPTURING, ArtifactBaseItem.WITHDRAW),
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
    private final ArtifactBaseItem artifactBaseItem;

    Artifact(CustomItem artifactItem, ArtifactBaseItem artifactBaseItem) {
        this.artifactItem = artifactItem;
        this.artifactBaseItem = artifactBaseItem;
    }

    public CustomItem getArtifactItem() {
        return artifactItem;
    }

    public ArtifactBaseItem getArtifactBaseItem() {
        return artifactBaseItem;
    }
}
