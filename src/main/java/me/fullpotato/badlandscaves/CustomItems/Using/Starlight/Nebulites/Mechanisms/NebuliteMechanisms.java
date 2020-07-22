package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;

import java.util.Random;

public abstract class NebuliteMechanisms {
    protected final BadlandsCaves plugin;
    protected final Random random = new Random();
    protected final StarlightArmor starlightArmor;
    protected final StarlightTools starlightTools;
    protected final StarlightCharge starlightCharge;
    protected final NebuliteManager nebuliteManager;

    public NebuliteMechanisms(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightTools = new StarlightTools(plugin);
        this.starlightCharge = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
    }
}
