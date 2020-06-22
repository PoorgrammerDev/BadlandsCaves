package me.fullpotato.badlandscaves.NMS.FakePlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface FakePlayerNMS {
    Player summonFakePlayer(Location location, Player player, Player sendTo, String name);
    Player summonFakePlayer(Location location, Player player, Player sendTo, String name, boolean copyArmor);
    void giveHandItem (Player player, Player sendTo, ItemStack item);
    void move (Location location, Player player, Player sendTo, boolean rotation);
    void damage (Player player, Player sendTo, boolean damaged);
    void remove (Player player);
    void sendToAll (Object... packets);
}
