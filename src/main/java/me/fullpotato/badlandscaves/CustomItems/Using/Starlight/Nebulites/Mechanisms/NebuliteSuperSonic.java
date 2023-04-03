package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NebuliteSuperSonic extends NebuliteMechanisms implements Listener {
    private final List<Material> ores = Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE, Material.ANCIENT_DEBRIS, Material.GILDED_BLACKSTONE);


    public NebuliteSuperSonic(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
    }

    @EventHandler
    public void preventSilkTouchOres (BlockBreakEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                final Block block = event.getBlock();
                if (ores.contains(block.getType())) {
                    final EntityEquipment equipment = player.getEquipment();
                    if (equipment != null) {
                        final ItemStack item = equipment.getItemInMainHand();
                        if (starlightTools.isStarlightPaxel(item) && starlightCharge.getCharge(item) > 0) {
                            final Nebulite[] nebulites = nebuliteManager.getNebulites(item);
                            for (Nebulite nebulite : nebulites) {
                                if (nebulite != null && nebulite.equals(Nebulite.SUPERSONIC_PROPULSORS)) {
                                    event.setCancelled(true);
                                    block.breakNaturally();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
