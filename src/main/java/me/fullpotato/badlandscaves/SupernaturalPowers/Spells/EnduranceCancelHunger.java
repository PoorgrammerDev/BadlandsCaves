package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EnduranceCancelHunger implements Listener {
    private final BadlandsCaves plugin;
    public EnduranceCancelHunger(BadlandsCaves bcav) {
        this.plugin = bcav;
    }

    @EventHandler
    public void hungerDecrease (FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        ItemStack item = event.getItem();
        if (item != null) return;

        int endurance_level = (int) PlayerScore.ENDURANCE_LEVEL.getScore(plugin, player);
        if (endurance_level < 1) return;

        Random random = new Random();
        int rand = random.nextInt(100);
        if ((endurance_level == 1 && rand < 25) || (endurance_level == 2 && rand < 50)) {
            event.setCancelled(true);
            player.setSaturation(player.getSaturation() + 1);
        }

    }
}
