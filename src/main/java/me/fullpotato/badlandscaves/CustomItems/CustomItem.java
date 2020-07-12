package me.fullpotato.badlandscaves.CustomItems;

import org.bukkit.inventory.ItemStack;

public enum CustomItem {
    STARTER_SAPLING(false),
    STARTER_BONE_MEAL(false),
    TOXIC_WATER(false),
    PURIFIED_WATER(false),
    CANTEEN(false),
    CANTEEN_FILL_PLACEHOLDER(true),
    FISHING_CRATE(false),
    FISHING_CRATE_HARDMODE(false),
    FOREVER_FISH(false),
    ANTIDOTE(false),
    MANA_POTION(false),
    PURGE_ESSENCE(true),
    HELL_ESSENCE(true),
    MAGIC_ESSENCE(true),
    DISPLACE(false),
    WITHDRAW(false),
    ENHANCED_EYES(false),
    POSSESS(false),
    TINY_BLAZE_POWDER(true),
    TAINTED_POWDER(false),
    ZOMBIE_SOUL(true),
    CREEPER_SOUL(true),
    SKELETON_SOUL(true),
    SPIDER_SOUL(true),
    PIGZOMBIE_SOUL(true),
    GHAST_SOUL(true),
    SILVERFISH_SOUL(true),
    WITCH_SOUL(true),
    PHANTOM_SOUL(true),
    MERGED_SOULS(true),
    SOUL_LANTERN(false),
    SOUL_CRYSTAL_INCOMPLETE(false),
    SOUL_CRYSTAL(false),
    RUNE(false),
    CHARGED_RUNE(false),
    VOLTSHOCK_BATTERY(true),
    VOLTSHOCK_PLACEHOLDER(true),
    VOLTSHOCK_SWORD_CHARGE_PLACEHOLDER(true),
    VOLTSHOCK_SHOCKER(true),
    VOLTSHOCK_ARROW(false),
    CORROSIVE_SUBSTANCE(true),
    CORROSIVE_PLACEHOLDER(true),
    CORROSIVE_ARROW(false),
    CHAMBER_MAGMA_KEY(true),
    CHAMBER_GLOWSTONE_KEY(true),
    CHAMBER_SOULSAND_KEY(true),
    BLESSED_APPLE(false),
    ENCHANTED_BLESSED_APPLE(false),
    STONE_SHIELD(false),
    IRON_SHIELD(false),
    DIAMOND_SHIELD(false),
    NETHERITE_SHIELD(false),
    RECALL_POTION(false),
    TITANIUM_FRAGMENT(true),
    TITANIUM_INGOT(true),
    REINFORCED_TITANIUM(true),
    TITANIUM_ROD(true),
    BINDING(true),
    GOLDEN_CABLE(true),
    NETHER_STAR_FRAGMENT(true),
    STARLIGHT_CIRCUIT(true),
    STARLIGHT_BATTERY(true),
    STARLIGHT_MODULE(true),
    PHOTON_EMITTER(true),
    WAVELENGTH_DISRUPTOR(true),
    SILENCER(false),
    STARLIGHT_HELMET(false),
    STARLIGHT_CHESTPLATE(false),
    STARLIGHT_LEGGINGS(false),
    STARLIGHT_BOOTS(false),
    STARLIGHT_SABER(false),
    STARLIGHT_SHIELD(false),
    STARLIGHT_BLASTER(false),
    STARLIGHT_PAXEL(false),
    STARLIGHT_SENTRY(false),
    ENERGIUM(true),
    ENERGY_CORE(true),
    STARLIGHT_CHARGE_PLACEHOLDER(true),
    VOIDMATTER(true),
    VOIDMATTER_STICK(true),
    VOIDMATTER_STRING(true),
    VOIDMATTER_HELMET(false),
    VOIDMATTER_CHESTPLATE(false),
    VOIDMATTER_LEGGINGS(false),
    VOIDMATTER_BOOTS(false),
    VOIDMATTER_BLADE(false),
    VOIDMATTER_BOW(false),
    VOIDMATTER_PICKAXE(false),
    VOIDMATTER_SHOVEL(false),
    VOIDMATTER_AXE(false),
    DIMENSIONAL_ANCHOR(true),
    TOTEM_OF_PRESERVATION(true);

    private final boolean preventUse;

    CustomItem(boolean preventUse) {
        this.preventUse = preventUse;
    }

    public ItemStack getItem() {
        LoadCustomItems customItemManager = new LoadCustomItems();
        return customItemManager.getItem(this);
    }

    public boolean getPreventUse() {
        return this.preventUse;
    }
}
