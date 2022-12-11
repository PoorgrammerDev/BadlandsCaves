package me.fullpotato.badlandscaves.Toxicity;

import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactFleetingSpirits;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class IncreaseToxInWater implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    private final StarlightArmor starlightArmor;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;
    private final Voidmatter voidmatter;
    private final ArtifactManager artifactManager;
    private final ArtifactFleetingSpirits artifactFleetingSpirits;
    private final EnvironmentalHazards environmentalHazards;
    private final PlayerEffects playerEffects;

    public IncreaseToxInWater(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightCharge starlightCharge, ArtifactManager artifactManager, Voidmatter voidmatter, ArtifactFleetingSpirits artifactFleetingSpirits, NebuliteManager nebuliteManager, EnvironmentalHazards environmentalHazards, PlayerEffects playerEffects) {
        this.plugin = plugin;
        this.random = random;
        this.starlightArmor = starlightArmor;
        this.starlightCharge = starlightCharge;
        this.voidmatter = voidmatter;
        this.artifactManager = artifactManager;
        this.artifactFleetingSpirits = artifactFleetingSpirits;
        this.nebuliteManager = nebuliteManager;
        this.environmentalHazards = environmentalHazards;
        this.playerEffects = playerEffects;
    }


    //INCREASE TOXICITY IN WATER WHEN MOVING
    @EventHandler
    public void incr_tox_in_water (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();
        if (event.getTo() != null && event.getTo().distanceSquared(event.getFrom()) <= 0) return;

        World world = player.getWorld();
        if (environmentalHazards.isDimension(world) && !environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.TOXIC_WATER)) return;

        final boolean in_reflection = (PlayerScore.IN_REFLECTION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_REFLECTION.getScore(plugin, player) == 1);
        if (in_reflection) return;

        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            if (block.getType() == Material.WATER) {
                double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
                int current_tox_slow = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));

                /* Order of increasing rates
                 *  Boat with wbreath
                 *  Boat w/o wbreath
                 *  Wbreath
                 *  Nothing
                 * */
                if (!player.isDead()) {
                    if (plugin.getSystemConfig().getBoolean("hardmode")) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                                for (ItemStack armor : equipment.getArmorContents()) {
                                    if (armor != null && starlightArmor.isStarlightArmor(armor) && starlightCharge.getCharge(armor) > 0) {
                                        final Nebulite[] nebulites = nebuliteManager.getNebulites(armor);
                                        for (Nebulite nebulite : nebulites) {
                                            if (nebulite != null && nebulite.equals(Nebulite.TOXIN_EXPELLER)) {
                                                if (random.nextInt(100) < 5)
                                                    starlightCharge.setCharge(armor, starlightCharge.getCharge(armor) - 1);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                if (!player.hasPotionEffect(PotionEffectType.WATER_BREATHING) && !player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
                                    //ARTIFACT Fleeting Spirits Override
                                    for (ItemStack armor : equipment.getArmorContents()) {
                                        if (armor != null && voidmatter.isVoidmatterArmor(armor)) {
                                            if (artifactManager.hasArtifact(player, Artifact.FLEETING_SPIRITS)) {
                                                if (artifactFleetingSpirits.attemptWarp(player, block.getLocation())) return;
                                            }
                                        }
                                    }

                                    //ARTIFACT Safeguard Override
                                    //In possession, has the safeguard artifact, is not immune (no water breathing) -> cancel pos.
                                    if (PlayerScore.IN_POSSESSION.hasScore(plugin, player) && (byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == (byte) 1) {
                                        if (this.artifactManager.hasArtifact(player, Artifact.SAFEGUARD)) {
                                            PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
                        if ((player.getVehicle() instanceof Boat)) {
                            //Boat with wbreath
                            int random_incr = (int) ((Math.random() * 5) + 5);
                            PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, current_tox_slow + random_incr);
                        }
                        else {
                            //Wbreath no boat
                            int random_incr = (int) ((Math.random() * 50) + 50);
                            PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, current_tox_slow + random_incr);
                        }
                    }
                    else if (player.getVehicle() instanceof Boat) {
                        //Boat w/o wbreath
                        int random_incr = (int) ((Math.random() * 10) + 10);
                        PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, current_tox_slow + random_incr);
                    }
                    else {
                        //Nothing
                        int random_incr = (int) (Math.random() * 25);
                        PlayerScore.TOXICITY.setScore(plugin, player, current_tox + random_incr);
                    }

                    current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
                    current_tox_slow = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));

                    if (current_tox_slow >= 100) {
                        PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, 0);
                        PlayerScore.TOXICITY.setScore(plugin, player, current_tox + 0.1);
                    }
                    playerEffects.applyEffects(player, true);
                }
            }
        }
    }
}
