package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;

public enum ActivePowers {
    DISPLACE(CustomItem.DISPLACE, PlayerScore.DISPLACE_LEVEL, ChatColor.BOLD.toString() + ChatColor.LIGHT_PURPLE + "Displace"),
    ENHANCED_EYES(CustomItem.ENHANCED_EYES, PlayerScore.EYES_LEVEL, ChatColor.BOLD.toString() + ChatColor.BLUE + "Enhanced Eyes"),
    WITHDRAW(CustomItem.WITHDRAW, PlayerScore.WITHDRAW_LEVEL, ChatColor.BOLD.toString() + ChatColor.GRAY + "Withdraw"),
    POSSESSION(CustomItem.POSSESS, PlayerScore.POSSESS_LEVEL, ChatColor.BOLD.toString() + ChatColor.DARK_GREEN + "Possession");

    private final CustomItem item;
    private final PlayerScore levelScore;
    private final String displayName;

    ActivePowers(CustomItem item, PlayerScore levelScore, String displayName) {
        this.item = item;
        this.levelScore = levelScore;
        this.displayName = displayName;
    }

    public CustomItem getItem() {
        return item;
    }

    public PlayerScore getLevelScore() {
        return levelScore;
    }

    public String getDisplayName() {
        return displayName;
    }
}
