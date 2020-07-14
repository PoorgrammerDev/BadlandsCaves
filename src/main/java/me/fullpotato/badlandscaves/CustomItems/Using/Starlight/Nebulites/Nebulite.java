package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;

public enum Nebulite {
    ENERGY_STORAGE(CustomItem.NEBULITE_ENERGY_STORAGE, CustomItem.STARLIGHT_HELMET, CustomItem.STARLIGHT_CHESTPLATE, CustomItem.STARLIGHT_LEGGINGS, CustomItem.STARLIGHT_BOOTS, CustomItem.STARLIGHT_SABER, CustomItem.STARLIGHT_BLASTER, CustomItem.STARLIGHT_SHIELD, CustomItem.STARLIGHT_PAXEL, CustomItem.STARLIGHT_SENTRY),
    CORRODING_LIGHTS(CustomItem.NEBULITE_CORRODING_LIGHTS, CustomItem.STARLIGHT_SABER),
    JAGGED_LIGHTS(CustomItem.NEBULITE_JAGGED_LIGHTS, CustomItem.STARLIGHT_SABER),
    FLURRYING_SWINGS(CustomItem.NEBULITE_FLURRYING_SWINGS, CustomItem.STARLIGHT_SABER),
    DECISIVE_SLICE(CustomItem.NEBULITE_DECISIVE_SLICE, CustomItem.STARLIGHT_SABER),
    WIDE_SWING(CustomItem.NEBULITE_WIDE_SWING, CustomItem.STARLIGHT_SABER);

    private final CustomItem nebuliteItem;
    private final CustomItem[] baseItems;
    Nebulite (CustomItem nebuliteItem, CustomItem... baseItems) {
        this.nebuliteItem = nebuliteItem;
        this.baseItems = baseItems;
    }

    public CustomItem getNebuliteItem() {
        return nebuliteItem;
    }

    public CustomItem[] getBaseItems() {
        return baseItems;
    }
}
