package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class NoFood implements Listener {
    private final EnvironmentalHazards environmentalHazards;
    private final int FOOD_LEVEL = 6;

    public NoFood(EnvironmentalHazards environmentalHazards) {
        this.environmentalHazards = environmentalHazards;
    }

    @EventHandler
    public void enterDimension(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        //Ensure that this is relevant
        if (!environmentalHazards.isDimension(world) || !environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.NO_FOOD)) return;

        player.setFoodLevel(Math.min(player.getFoodLevel(), FOOD_LEVEL));
    }


    @EventHandler
    public void hungerChange(FoodLevelChangeEvent event) {
        final HumanEntity human = event.getEntity();
        final World world = human.getWorld();

        //Ensure that this is relevant
        if (!environmentalHazards.isDimension(world) || !environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.NO_FOOD)) return;

        event.setFoodLevel(Math.min(event.getFoodLevel(), FOOD_LEVEL));
    }
}
