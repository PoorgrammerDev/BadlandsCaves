package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;

public abstract class ArtifactMechanisms {
    protected final BadlandsCaves plugin;
    protected final Voidmatter voidmatter;
    protected final ArtifactManager artifactManager;

    protected ArtifactMechanisms(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager) {
        this.plugin = plugin;
        this.voidmatter = voidmatter;
        this.artifactManager = artifactManager;
    }
}
